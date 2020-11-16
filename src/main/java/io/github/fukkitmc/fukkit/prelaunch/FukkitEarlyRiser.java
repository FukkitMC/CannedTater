package io.github.fukkitmc.fukkit.prelaunch;

import com.chocohead.mm.api.ClassTinkerers;
import io.github.fukkitmc.fukkit.jar.Remapper;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class FukkitEarlyRiser {

    private FukkitEarlyRiser() {
    }

    public static void run() throws IOException {
        Remapper.run(); //TODO: yeef

        Path path = FabricLoader.getInstance().getModContainer("canned-tater").get().getRootPath();

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
                    ClassTinkerers.addReplacement(actualName, original -> {
                        FukkitPatcher.merge(original, patch);
                    });
                }

                return FileVisitResult.CONTINUE;
            }
        });
    }
}
