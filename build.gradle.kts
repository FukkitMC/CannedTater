import net.fabricmc.loom.LoomGradlePlugin
import uk.jamierocks.propatcher.task.ApplyPatchesTask
import uk.jamierocks.propatcher.task.MakePatchesTask
import uk.jamierocks.propatcher.task.ResetSourcesTask
import java.nio.file.Files

plugins {
    id("fabric-loom") version "0.5.34"
    id("uk.jamierocks.propatcher") version "1.3.2"
}

group = "io.github.fukkitmc"
version = "1.0.0-SNAPSHOT"

repositories {
    // For Bukkit
    mavenLocal()

    // For Fabric-ASM
    maven {
        name = "JitPack"
        url = uri("https://jitpack.io/")
    }
}

dependencies {
    minecraft("net.minecraft", "minecraft", "1.16.4")
    mappings("net.fabricmc", "yarn", "1.16.4+build.6", classifier = "v2")
    modImplementation("net.fabricmc", "fabric-loader", "0.10.6+build.214")

    // Bukkit 1.16.4 libraries
    implementation("org.bukkit", "bukkit", "1.16.4-R0.1-SNAPSHOT")
    implementation("jline", "jline", "2.12.1")
    implementation("org.apache.logging.log4j", "log4j-iostreams", "2.8.1")
    implementation("com.googlecode.json-simple", "json-simple", "1.1.1")
    include("org.bukkit", "bukkit", "1.16.4-R0.1-SNAPSHOT")
    include("jline", "jline", "2.12.1")
    include("org.apache.logging.log4j", "log4j-iostreams", "2.8.1")
    include("com.googlecode.json-simple", "json-simple", "1.1.1")

    // Class replacement
    modImplementation("com.github.Chocohead", "Fabric-ASM", "v2.1")
    include("com.github.Chocohead", "Fabric-ASM", "v2.1")
}

sourceSets {
    main {
        java.srcDirs(file("src/main/minecraft"))
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

minecraft {
    accessWidener = file("src/main/resources/canned-tater.aw")
}

patches {
    root = file(".gradle/extracted-1.16.4+build.6")
    target = file("src/main/minecraft")
    patches = file("patches")
}

tasks.withType<JavaCompile> {
    options.release.set(8)
    options.encoding = "UTF-8"
}

// <editor-fold desc="Patches">
tasks.named<ResetSourcesTask>("resetSources") {
    doFirst {
        if (!root.exists()) {
            val sourceJar = LoomGradlePlugin.getMappedByproduct(project, "-sources.jar")

            if (!sourceJar.exists()) {
                throw GradleException("You must run genSources")
            }

            copy {
                from(zipTree(sourceJar))
                into(root)

                includeEmptyDirs = false
                include("**/*.java")
            }
        }
    }
}

tasks.named<ApplyPatchesTask>("applyPatches") {
    doLast {
        Files.walk(target.toPath()).forEach {
            val original = project.patches.root.toPath().resolve(target.toPath().relativize(it))

            if (!Files.isSameFile(it, original)
                    && Files.isRegularFile(it)
                    && Files.readAllBytes(it).contentEquals(Files.readAllBytes(original))) {
                Files.delete(it)
            }
        }
    }
}

tasks.named<MakePatchesTask>("makePatches") {
    doLast {
        Files.walk(patches.toPath())
                .filter { Files.isRegularFile(it) }
                .forEach {
                    val iterator = Files.readAllLines(it).iterator()

                    if (iterator.peek("--- a/")
                            && iterator.peek("+++ /dev/null")
                            && iterator.peek("@@ ")) {
                        while (iterator.hasNext()) {
                            if (!iterator.next().startsWith("-")) {
                                return@forEach
                            }
                        }

                        Files.delete(it)
                    }
                }
    }
}

fun Iterator<String>.peek(s: String): Boolean {
    if (!hasNext()) return false

    return next().let {
        it.isNotEmpty() && it.startsWith(s)
    }
}
// </editor-fold>
