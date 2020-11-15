package org.bukkit.craftbukkit.inventory;

import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;

public class CraftInventoryMerchant extends CraftInventory implements MerchantInventory {

    private final net.minecraft.village.Merchant merchant;

    public CraftInventoryMerchant(net.minecraft.village.Merchant merchant, net.minecraft.village.MerchantInventory inventory) {
        super(inventory);
        this.merchant = merchant;
    }

    @Override
    public int getSelectedRecipeIndex() {
        return getInventory().recipeIndex;
    }

    @Override
    public MerchantRecipe getSelectedRecipe() {
        net.minecraft.village.TradeOffer nmsRecipe = getInventory().getTradeOffer();
        return (nmsRecipe == null) ? null : nmsRecipe.asBukkit();
    }

    @Override
    public net.minecraft.village.MerchantInventory getInventory() {
        return (net.minecraft.village.MerchantInventory) inventory;
    }

    @Override
    public Merchant getMerchant() {
        return merchant.getCraftMerchant();
    }
}
