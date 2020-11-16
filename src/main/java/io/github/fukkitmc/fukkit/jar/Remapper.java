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
        File remappingDir = new File(FabricLoader.getInstance().getGameDir().toFile(), ".fukkit");
        if (!remappingDir.exists()) {
            remappingDir.mkdirs();
        }

        Path craftBukkit = new File(FabricLoader.getInstance().getGameDir().toFile(), "craftbukkit-1.16.4.jar").toPath(); //hardcoded for now because im lazy Tiny Potat
        Path mappings = new File(FabricLoader.getInstance().getGameDir().toFile(), "craftbukkit.tiny").toPath(); //hardcoded for now because im lazy Tiny Potat
        Path officialCB = new File(remappingDir, "cb-official.jar").toPath();
        Path intermediaryCB = new File(remappingDir, "cb-intermediary.jar").toPath();

        remap(TinyUtils.createTinyMappingProvider(mappings, "spigot", "official"), craftBukkit, officialCB);
        TinyTree tinyTree = FabricLauncherBase.getLauncher().getMappingConfiguration().getMappings();
        remap(out -> {
            for (ClassDef classDef : tinyTree.getClasses()) {
                String owner = classDef.getName("official");
                out.acceptClass(owner, classDef.getName("intermediary"));
                for (FieldDef fieldDef : classDef.getFields()) {
                    out.acceptField(new IMappingProvider.Member(owner, fieldDef.getName("official"), fieldDef.getDescriptor("official")), fieldDef.getName("intermediary"));
                }
                for (MethodDef methodDef : classDef.getMethods()) {
                    out.acceptMethod(new IMappingProvider.Member(owner, methodDef.getName("official"), methodDef.getDescriptor("official")), methodDef.getName("intermediary"));
                }
            }
        }, officialCB, intermediaryCB);
        //TODO: extend tiny remapper and make it get methods by name and not by name and descriptor. fixes global warming TM
    }

    public static void remap(IMappingProvider mappings, Path from, Path to) throws IOException {
        TinyRemapper remapper = TinyRemapper.newRemapper()
                .withMappings(mappings)
                .ignoreConflicts(true)
                .build();

        try (OutputConsumerPath out = new OutputConsumerPath.Builder(to).build()) {
            out.addNonClassFiles(from);
            remapper.readInputs(from);
            remapper.apply(out);
        }
        remapper.finish();
    }
}
