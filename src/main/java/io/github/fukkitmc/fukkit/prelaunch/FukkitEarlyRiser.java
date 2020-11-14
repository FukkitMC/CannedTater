package io.github.fukkitmc.fukkit.prelaunch;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;

public class FukkitEarlyRiser {

    private FukkitEarlyRiser() {
    }

    public static void run() throws IOException {
        Path path = FabricLoader.getInstance().getModContainer("canned-tater").get().getRootPath();

        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path f, BasicFileAttributes attrs) throws IOException {
                Path file = path.relativize(f);
                String name = file.toString();

                if (name.endsWith(".class") && name.startsWith("net")) {
                    ClassNode node = new ClassNode();
                    byte[] bytes = Files.readAllBytes(f);
                    new ClassReader(bytes).accept(node, 0);

                    String actualName = name.substring(0, name.length() - 6).replace(File.separatorChar, '.');

                    ClassTinkerers.define(actualName, bytes);
                    ClassTinkerers.addReplacement(actualName, n -> {
                        try {
                            reset(n);
                        } catch (IllegalAccessException exception) {
                            throw new RuntimeException(exception);
                        }

                        node.accept(n);
                    });
                }

                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static void reset(ClassNode node) throws IllegalAccessException {
        for (Field field : ClassNode.class.getFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            if (field.getType().isPrimitive()) {
                field.set(node, 0);
            } else if (Collection.class.isAssignableFrom(field.getType())) {
                Collection<?> collection = (Collection<?>) field.get(node);

                if (collection != null) {
                    collection.clear();
                }
            } else {
                field.set(node, null);
            }
        }
    }
}
