package io.github.fukkitmc.fukkit.prelaunch;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FukkitPatcher {

    private static final String ADD_IF_NOT_PRESENT = "Lio/github/fukkitmc/fukkit/asm/AddIfNotPresent;";
    private static final String CLASS_INITIALIZE = "Lio/github/fukkitmc/fukkit/asm/ClassInitialize;";
    private static final String FINAL = "Lio/github/fukkitmc/fukkit/asm/Final;";
    private static final String REMOVE = "Lio/github/fukkitmc/fukkit/asm/Remove;";
    private static final String REMOVE_IMPLEMENTATION = "Lio/github/fukkitmc/fukkit/asm/RemoveImplementation;";
    private static final String RENAME = "Lio/github/fukkitmc/fukkit/asm/Rename;";
    private static final String SHADOW = "Lio/github/fukkitmc/fukkit/asm/Shadow;";

    public static void merge(ClassNode original, ClassNode patch) {
        // Step 1 - Copy attributes
        {
            if (!"java/lang/Object".equals(patch.superName)) {
                original.superName = patch.superName;
            }

            if (original.interfaces == null) {
                original.interfaces = new ArrayList<>();
            }

            if (patch.interfaces != null) {
                original.interfaces.addAll(patch.interfaces);
            }

            // TODO: RemoveImplementation
        }


        // Step 2 - Ignore everything which is shadowed
        patch.fields.removeIf(field -> has(SHADOW, field.visibleAnnotations));
        patch.methods.removeIf(field -> has(SHADOW, field.visibleAnnotations));

        // Step 3 - Remove methods or fields
        // TODO: Remove

        // Step 4 - Fix final fields
        for (FieldNode field : patch.fields) {
            if (has(FINAL, field.visibleAnnotations)) {
                field.access |= Opcodes.ACC_FINAL;
            } else {
                field.access &= ~Opcodes.ACC_FINAL;
            }
        }

        // Step 5 - Fix final methods
        for (MethodNode method : patch.methods) {
            if (has(FINAL, method.visibleAnnotations)) {
                method.access |= Opcodes.ACC_FINAL;
            } else {
                method.access &= ~Opcodes.ACC_FINAL;
            }
        }

        // Step 6 - Merge fields
        for (FieldNode field : patch.fields) {
            boolean allow = true;

            if (has(ADD_IF_NOT_PRESENT, field.visibleAnnotations)) {
                for (FieldNode originalField : original.fields) {
                    if (originalField.name.equals(field.name) && originalField.desc.equals(field.desc)) {
                        allow = false;
                        break;
                    }
                }
            }

            if (allow) {
                // TODO: Rename
                original.fields.removeIf(fieldNode -> fieldNode.name.equals(field.name) && fieldNode.desc.equals(field.desc));
                original.fields.add(field);
            }
        }

        // Step 7 - Merge methods
        for (MethodNode method : patch.methods) {
            if (method.name.equals("<clinit>")) {
                // Ignore
            } else if (has(CLASS_INITIALIZE, method.visibleAnnotations)) {
                // TODO: ClassInitialize
            } else {
                // TODO: Rename
                original.methods.removeIf(methodNode -> methodNode.name.equals(method.name) && methodNode.desc.equals(method.desc));
                original.methods.add(method);
            }
        }
    }

    private static boolean has(String type, List<AnnotationNode> annotations) {
        for (AnnotationNode annotation : safe(annotations)) {
            if (type.equals(annotation.desc)) {
                return true;
            }
        }

        return false;
    }

    // Makes sure the list isn't null
    private static List<AnnotationNode> safe(List<AnnotationNode> annotations) {
        if (annotations == null) {
            return Collections.emptyList();
        } else {
            return annotations;
        }
    }
}
