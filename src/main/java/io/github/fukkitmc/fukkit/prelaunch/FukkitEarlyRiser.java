package io.github.fukkitmc.fukkit.prelaunch;

import com.chocohead.mm.api.ClassTinkerers;
import io.github.fukkitmc.fukkit.jar.Remapper;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.launch.common.FabricLauncherBase;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipFile;

public class FukkitEarlyRiser {

    private FukkitEarlyRiser() {
    }

    public static void run() throws IOException {
        Remapper.run(); //TODO: yeef

        Path path = FabricLoader.getInstance().getModContainer("canned-tater").get().getRootPath();

        try (FileSystem fs = FileSystems.newFileSystem(Remapper.REMAPPED_CRAFTBUKKIT, FabricLauncherBase.getLauncher().getTargetClassLoader())) {
            Files.walkFileTree(fs.getPath("/"), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path f, BasicFileAttributes attrs) throws IOException {
                    String name = f.toString().substring(1);

                    if (name.endsWith(".class") && (name.startsWith("net") || name.startsWith("org/bukkit"))) {
                        ClassNode patch = new ClassNode();
                        byte[] patchBytes = Files.readAllBytes(f);
                        new ClassReader(patchBytes).accept(patch, 0);
                        String actualName = name.substring(0, name.length() - 6).replace(File.separatorChar, '.');
                        if(actualName.equals("net/minecraft/server/Main")){
                            final MethodVisitor main = patch.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
                            main.visitCode();
                            main.visitVarInsn(Opcodes.ALOAD, 0);
                            main.visitMethodInsn(Opcodes.INVOKESTATIC, "org/bukkit/craftbukkit/Main", "main", "([Ljava/lang/String;)V", false);
                            main.visitInsn(Opcodes.RETURN);
                            main.visitEnd();
                        }
                        ClassTinkerers.define(actualName, patchBytes);
                        ClassTinkerers.addReplacement(actualName, original -> FukkitPatcher.overwrite(original, patch));
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }

        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path f, BasicFileAttributes attrs) throws IOException {
                Path file = path.relativize(f);
                String name = file.toString();

                if (name.endsWith(".class") && name.startsWith("net")) {
                    ClassNode patch = new ClassNode();
                    byte[] patchBytes = Files.readAllBytes(f);
                    new ClassReader(patchBytes).accept(patch, 0);

                    String actualName = name.substring(0, name.length() - 6).replace(File.separatorChar, '.');

                    ClassTinkerers.define(actualName, patchBytes);
                    ClassTinkerers.addReplacement(actualName, original -> FukkitPatcher.merge(original, patch));
                }

                return FileVisitResult.CONTINUE;
            }
        });
    }
}
