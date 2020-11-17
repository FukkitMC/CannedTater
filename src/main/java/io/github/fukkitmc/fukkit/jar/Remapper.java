package io.github.fukkitmc.fukkit.jar;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.launch.common.FabricLauncherBase;
import net.fabricmc.mapping.tree.ClassDef;
import net.fabricmc.mapping.tree.FieldDef;
import net.fabricmc.mapping.tree.MethodDef;
import net.fabricmc.mapping.tree.TinyTree;
import net.fabricmc.tinyremapper.IMappingProvider;
import net.fabricmc.tinyremapper.OutputConsumerPath;
import net.fabricmc.tinyremapper.TinyRemapper;
import net.fabricmc.tinyremapper.TinyUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Remapper {
    public static final File remappingDir = new File(FabricLoader.getInstance().getGameDir().toFile(), ".fukkit");
    public static final Path REMAPPED_CRAFTBUKKIT = new File(remappingDir, "cb-remapped.jar").toPath();

    public static void mapCraftBukkit(Path in, Path out, IMappingProvider mappings) {
        TinyRemapper remapper = TinyRemapper.newRemapper().withMappings(mappings).renameInvalidLocals(true).rebuildSourceFilenames(true).build();

        try (OutputConsumerPath outputConsumer = new OutputConsumerPath.Builder(out).build()) {
            outputConsumer.addNonClassFiles(in);
            remapper.readInputs(in);

            remapper.apply(outputConsumer);
        } catch (Exception e) {
            throw new RuntimeException("Failed to remap jar", e);
        } finally {
            remapper.finish();
        }
    }

    public static void run() throws IOException {
        if (!remappingDir.exists()) {
            remappingDir.mkdirs();
        }

        Path craftBukkit = new File(FabricLoader.getInstance().getGameDir().toFile(), "craftbukkit-1.16.4.jar").toPath(); //hardcoded for now because im lazy Tiny Potat
        Path mappings = new File(FabricLoader.getInstance().getGameDir().toFile(), "craftbukkit.tiny").toPath(); //hardcoded for now because im lazy Tiny Potat
        Path yarn = new File(FabricLoader.getInstance().getGameDir().toFile(), "yarn-1.16.4build.6-v2.tiny").toPath(); //hardcoded for now because im lazy Tiny Potat
        Path finalM = new File(FabricLoader.getInstance().getGameDir().toFile(), "final.tiny").toPath(); //hardcoded for now because im lazy Tiny Potat
        Path officialCB = new File(remappingDir, "cb-official.jar").toPath();
        Path intermediaryCB = new File(remappingDir, "cb-intermediary.jar").toPath();
        Path yarnCB = new File(remappingDir, "cb-yarn.jar").toPath();

        remap(TinyUtils.createTinyMappingProvider(mappings, "spigot", "official"), craftBukkit, officialCB);
        TinyTree tinyTree = FabricLauncherBase.getLauncher().getMappingConfiguration().getMappings();
        remap(getProviderFromTree(tinyTree, "official", "intermediary"), officialCB, intermediaryCB);
        remap(TinyUtils.createTinyMappingProvider(yarn, "intermediary", "named"), intermediaryCB, yarnCB);
        remap(TinyUtils.createTinyMappingProvider(finalM, "a", "b"), yarnCB, REMAPPED_CRAFTBUKKIT);
        //TODO: extend tiny remapper and make it get methods by name and not by name and descriptor. fixes global warming TM
    }

    private static IMappingProvider getProviderFromTree(TinyTree tinyTree, String from, String to) {
        return out -> {
            for (ClassDef classDef : tinyTree.getClasses()) {
                String owner = classDef.getName(from);
                out.acceptClass(owner, classDef.getName(to));
                for (FieldDef fieldDef : classDef.getFields()) {
                    out.acceptField(new IMappingProvider.Member(owner, fieldDef.getName(from), fieldDef.getDescriptor(from)), fieldDef.getName(to));
                }
                for (MethodDef methodDef : classDef.getMethods()) {
                    out.acceptMethod(new IMappingProvider.Member(owner, methodDef.getName(from), methodDef.getDescriptor(from)), methodDef.getName(to));
                }
            }
        };
    }

    public static void remap(IMappingProvider mappings, Path from, Path to) throws IOException {
        TinyRemapper remapper = TinyRemapper.newRemapper()
                .withMappings(mappings)
                .ignoreConflicts(true)
                .fixPackageAccess(true)
                .build();

        try (OutputConsumerPath out = new OutputConsumerPath.Builder(to).build()) {
            out.addNonClassFiles(from);
            remapper.readInputs(from);
            remapper.apply(out);
        }
        remapper.finish();
    }
}
