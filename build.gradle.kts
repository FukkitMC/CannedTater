import net.fabricmc.loom.LoomGradlePlugin
import net.fabricmc.loom.processors.JarProcessor
import org.objectweb.asm.*
import org.objectweb.asm.tree.ClassNode
import org.zeroturnaround.zip.ByteSource
import org.zeroturnaround.zip.ZipUtil
import uk.jamierocks.propatcher.task.ApplyPatchesTask
import uk.jamierocks.propatcher.task.MakePatchesTask
import uk.jamierocks.propatcher.task.ResetSourcesTask
import java.nio.file.Files
import java.util.Arrays

buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        classpath("org.zeroturnaround:zt-zip:1.13")
    }
}

plugins {
    id("fabric-loom") version "0.5.34"
    id("com.github.fudge.forgedflowerloom") version "2.0.0"
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

    addJarProcessor(SideStripperJarProcessor.SERVER)
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

// <editor-fold desc="Side stripping">
internal class ClassStripper(api: Int, classVisitor: ClassVisitor?, private val stripInterfaces: Collection<String>, private val stripFields: Collection<String>, private val stripMethods: Collection<String>)
    : ClassVisitor(api, classVisitor) {
    override fun visit(version: Int, access: Int, name: String, signature: String?, superName: String, interfaces: Array<String>?) {
        super.visit(version, access, name, signature, superName, if (stripInterfaces.isEmpty()) interfaces else {
            val interfacesList = interfaces?.toMutableList() ?: mutableListOf()
            interfacesList.removeAll(stripInterfaces)
            interfacesList.toTypedArray()
        })
    }

    override fun visitField(access: Int, name: String, descriptor: String, signature: String?, value: Any?): FieldVisitor? {
        return if (stripFields.contains(name + descriptor)) null else super.visitField(access, name, descriptor, signature, value)
    }

    override fun visitMethod(access: Int, name: String, descriptor: String, signature: String?, exceptions: Array<String>?): MethodVisitor? {
        return if (stripMethods.contains(name + descriptor)) null else super.visitMethod(access, name, descriptor, signature, exceptions)
    }
}

internal class EnvironmentStrippingData(api: Int, private val envType: String) : ClassVisitor(api) {
    private var stripEntireClass = false
    private val stripInterfaces = mutableSetOf<String>()
    private val stripFields = mutableSetOf<String>()
    private val stripMethods = mutableSetOf<String>()

    private inner class EnvironmentAnnotationVisitor constructor(api: Int, private val onEnvMismatch: Runnable) : AnnotationVisitor(api) {
        override fun visitEnum(name: String, descriptor: String, value: String) {
            if ("value" == name && envType != value) {
                onEnvMismatch.run()
            }
        }
    }

    private inner class EnvironmentInterfaceAnnotationVisitor constructor(api: Int) : AnnotationVisitor(api) {
        private var envMismatch = false
        private var itf: Type? = null

        override fun visitEnum(name: String, descriptor: String, value: String) {
            if ("value" == name && envType != value) {
                envMismatch = true
            }
        }

        override fun visit(name: String, value: Any) {
            if ("itf" == name) {
                itf = value as Type
            }
        }

        override fun visitEnd() {
            val itf = itf

            if (envMismatch && itf != null) {
                stripInterfaces.add(itf.internalName)
            }
        }
    }

    private fun visitMemberAnnotation(descriptor: String, onEnvMismatch: Runnable): AnnotationVisitor? {
        return if (ENVIRONMENT_DESCRIPTOR == descriptor) {
            EnvironmentAnnotationVisitor(api, onEnvMismatch)
        } else null
    }

    override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor? {
        when (descriptor) {
            ENVIRONMENT_DESCRIPTOR -> {
                return EnvironmentAnnotationVisitor(api) { stripEntireClass = true }
            }
            ENVIRONMENT_INTERFACE_DESCRIPTOR -> {
                return EnvironmentInterfaceAnnotationVisitor(api)
            }
            ENVIRONMENT_INTERFACES_DESCRIPTOR -> {
                return object : AnnotationVisitor(api) {
                    override fun visitArray(name: String): AnnotationVisitor? {
                        return if ("value" == name) {
                            object : AnnotationVisitor(api) {
                                override fun visitAnnotation(name: String?, descriptor: String): AnnotationVisitor {
                                    return EnvironmentInterfaceAnnotationVisitor(api)
                                }
                            }
                        } else null
                    }
                }
            }
            else -> return null
        }
    }

    override fun visitField(access: Int, name: String, descriptor: String, signature: String?, value: Any?): FieldVisitor {
        return object : FieldVisitor(api) {
            override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor? {
                return visitMemberAnnotation(descriptor) { stripFields.add(name + descriptor) }
            }
        }
    }

    override fun visitMethod(access: Int, name: String, descriptor: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
        val methodId = name + descriptor
        return object : MethodVisitor(api) {
            override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor? {
                return visitMemberAnnotation(descriptor) { stripMethods.add(methodId) }
            }
        }
    }

    fun stripEntireClass(): Boolean {
        return stripEntireClass
    }

    fun getStripInterfaces(): Collection<String> {
        return stripInterfaces
    }

    fun getStripFields(): Collection<String> {
        return stripFields
    }

    fun getStripMethods(): Collection<String> {
        return stripMethods
    }

    val isEmpty: Boolean
        get() = stripInterfaces.isEmpty() && stripFields.isEmpty() && stripMethods.isEmpty()

    companion object {
        private const val ENVIRONMENT_DESCRIPTOR = "Lnet/fabricmc/api/Environment;"
        private const val ENVIRONMENT_INTERFACE_DESCRIPTOR = "Lnet/fabricmc/api/EnvironmentInterface;"
        private const val ENVIRONMENT_INTERFACES_DESCRIPTOR = "Lnet/fabricmc/api/EnvironmentInterfaces;"
    }
}

enum class SideStripperJarProcessor : JarProcessor {
    CLIENT, SERVER;

    override fun setup() {}
    override fun process(file: File) {
        val toRemove = mutableSetOf<String>()
        val toTransform = mutableSetOf<ByteSource>()

        ZipUtil.iterate(file) { `in`, zipEntry ->
            val name: String = zipEntry.name
            if (!zipEntry.isDirectory && name.endsWith(".class")) {
                val original = ClassNode()
                ClassReader(`in`).accept(original, 0)
                val stripData = EnvironmentStrippingData(Opcodes.ASM8, name)
                original.accept(stripData)
                if (stripData.stripEntireClass()) {
                    toRemove.add(name)
                } else if (!stripData.isEmpty) {
                    val classWriter = ClassWriter(0)
                    original.accept(ClassStripper(Opcodes.ASM8, classWriter, stripData.getStripInterfaces(), stripData.getStripFields(), stripData.getStripMethods()))
                    toTransform.add(ByteSource(name, classWriter.toByteArray()))
                }
            }
        }

        ZipUtil.replaceEntries(file, toTransform.toTypedArray())
        ZipUtil.removeEntries(file, toRemove.toTypedArray())
        ZipUtil.addEntry(file, "side.txt", name.toByteArray())
    }

    override fun isInvalid(file: File): Boolean {
        return !Arrays.equals(ZipUtil.unpackEntry(file, "side.txt"), name.toByteArray())
    }
}
// </editor-fold

// <editor-fold desc="Patches">
tasks.named<ResetSourcesTask>("resetSources") {
    doFirst {
        if (!root.exists()) {
            val sourceJar = LoomGradlePlugin.getMappedByproduct(project, "-sources.jar")

            if (!sourceJar.exists()) {
                throw GradleException("You must run genSourcesWithForgedFlower")
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
