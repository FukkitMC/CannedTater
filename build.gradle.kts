plugins {
    id("fabric-loom") version "0.5.35"
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

    // Nullable fixes
    implementation("com.google.code.findbugs", "jsr305", "3.0.2")

    // CB Remapping
    implementation("net.fabricmc", "tiny-remapper", "0.3.1.72")
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

tasks.withType<JavaCompile> {
    if (JavaVersion.current().isJava9Compatible) {
        options.release.set(8)
    }

    options.encoding = "UTF-8"
}
