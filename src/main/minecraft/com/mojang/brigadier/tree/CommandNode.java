package com.mojang.brigadier.tree;

import com.mojang.brigadier.AmbiguityConsumer;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.fukkitmc.fukkit.asm.Final;
import io.github.fukkitmc.fukkit.asm.Shadow;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public abstract class CommandNode<S> implements Comparable<CommandNode<S>> {

    public void removeCommand(String name) {
        children.remove(name);
        literals.remove(name);
        arguments.remove(name);
    }

    public boolean canUse(final S source) {
        if (source instanceof ServerCommandSource) {
            try {
                ((ServerCommandSource) source).currentCommand = this;
                return requirement.test(source);
            } finally {
                ((ServerCommandSource) source).currentCommand = null;
            }
        }

        return requirement.test(source);
    }

    @Shadow
    private final Map<String, CommandNode<S>> children;

    @Shadow
    private final Map<String, LiteralCommandNode<S>> literals;

    @Shadow
    private final Map<String, ArgumentCommandNode<S, ?>> arguments;

    @Final
    @Shadow
    private final Predicate<S> requirement;

    @Final
    @Shadow
    private final CommandNode<S> redirect;

    @Final
    @Shadow
    private final RedirectModifier<S> modifier;

    @Final
    @Shadow
    private final boolean forks;

    @Shadow
    private final Command<S> command;

    @Shadow
    protected CommandNode(Command<S> command, Predicate<S> requirement, CommandNode<S> redirect, RedirectModifier<S> modifier, boolean forks) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Command<S> getCommand() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Collection<CommandNode<S>> getChildren() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public CommandNode<S> getChild(String name) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public CommandNode<S> getRedirect() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public RedirectModifier<S> getRedirectModifier() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void addChild(CommandNode<S> node) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public void findAmbiguities(AmbiguityConsumer<S> consumer) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    protected abstract boolean isValidInput(String var1);

    @Shadow
    public boolean equals(Object o) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public Predicate<S> getRequirement() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public abstract String getName();

    @Shadow
    public abstract String getUsageText();

    @Shadow
    public abstract void parse(StringReader var1, CommandContextBuilder<S> var2) throws CommandSyntaxException;

    @Shadow
    public abstract CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) throws CommandSyntaxException;

    @Shadow
    public abstract ArgumentBuilder<S, ?> createBuilder();

    @Shadow
    protected abstract String getSortedKey();

    @Shadow
    public Collection<? extends CommandNode<S>> getRelevantNodes(StringReader input) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public int compareTo(CommandNode<S> o) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean isFork() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public abstract Collection<String> getExamples();
}
