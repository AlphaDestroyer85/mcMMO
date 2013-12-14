package com.gmail.nossr50.skills.salvage;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.material.MaterialData;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.salvage.Salvage.Tier;
import com.gmail.nossr50.skills.salvage.salvageables.Salvageable;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

public class SalvageManager extends SkillManager {
    private boolean placedAnvil;
    private int     lastClick;

    public SalvageManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.SALVAGE);
    }

    /**
     * Handles notifications for placing an anvil.
     *
     * @param anvilType The {@link Material} of the anvil block
     */
    public void placedAnvilCheck(Material anvilType) {
        Player player = getPlayer();

        if (getPlacedAnvil(anvilType)) {
            return;
        }

        player.sendMessage(LocaleLoader.getString("Salvage.Listener.Anvil"));

        player.playSound(player.getLocation(), Sound.ANVIL_LAND, Misc.ANVIL_USE_VOLUME, Misc.ANVIL_USE_PITCH);
        togglePlacedAnvil(anvilType);
    }

    public void handleSalvage(Location location, ItemStack item) {
        Player player = getPlayer();

        if (player.getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        Salvageable salvageable = mcMMO.getSalvageableManager().getSalvageable(item.getType());

        // Permissions checks on material and item types
        if (!Permissions.salvageItemType(player, salvageable.getSalvageItemType())) {
            player.sendMessage(LocaleLoader.getString("mcMMO.NoPermission"));
            return;
        }

        if (!Permissions.salvageMaterialType(player, salvageable.getSalvageMaterialType())) {
            player.sendMessage(LocaleLoader.getString("mcMMO.NoPermission"));
            return;
        }

        int skillLevel = getSkillLevel();
        int minimumSalvageableLevel = salvageable.getMinimumLevel();

        // Level check
        if (skillLevel < minimumSalvageableLevel) {
            player.sendMessage(LocaleLoader.getString("Salvage.Skills.Adept", minimumSalvageableLevel, StringUtils.getPrettyItemString(item.getType())));
            return;
        }

        if (item.getDurability() != 0 && (getSkillLevel() < Salvage.advancedSalvageUnlockLevel || !Permissions.advancedSalvage(player))) {
            player.sendMessage(LocaleLoader.getString("Salvage.Skills.AdeptDamaged"));
            return;
        }

        Material salvageMaterial = salvageable.getSalvageMaterial();
        byte salvageMaterialMetadata = salvageable.getSalvageMaterialMetadata();
        int baseSalvageAmount = SkillUtils.getRepairAndSalvageQuantities(item, salvageable.getSalvageMaterial(), (byte) -1);

        int salvageableAmount = Salvage.calculateSalvageableAmount(item.getDurability(), salvageMaterial.getMaxDurability(), baseSalvageAmount);

        if (salvageableAmount == 0) {
            player.sendMessage(LocaleLoader.getString("Salvage.Skills.TooDamaged"));
            return;
        }

        double salvagePercentage = Math.min((((Salvage.salvageMaxPercentage / Salvage.salvageMaxPercentageLevel) * getSkillLevel()) / 100.0D), Salvage.salvageMaxPercentage / 100.0D);
        salvageableAmount = Math.max((int) (salvageableAmount * salvagePercentage), 1); // Always get at least something back, if you're capable of repairing it.

        player.setItemInHand(new ItemStack(Material.AIR));
        location.add(0, 1, 0);

        Map<Enchantment, Integer> enchants = item.getEnchantments();

        if (!enchants.isEmpty()) {
            ItemStack enchantBook = arcaneSalvageCheck(enchants);

            if (enchantBook != null) {
                Misc.dropItem(location, enchantBook);
            }
        }

        Misc.dropItems(location, new MaterialData(salvageMaterial, salvageMaterialMetadata).toItemStack(1), salvageableAmount);

        player.playSound(player.getLocation(), Sound.ANVIL_USE, Misc.ANVIL_USE_VOLUME, Misc.ANVIL_USE_PITCH);
        player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
        player.sendMessage(LocaleLoader.getString("Salvage.Skills.Success"));
    }

    /**
     * Gets the Arcane Salvage rank
     *
     * @return the current Arcane Salvage rank
     */
    public int getArcaneSalvageRank() {
        int skillLevel = getSkillLevel();

        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.toNumerical();
            }
        }

        return 0;
    }

    public double getExtractFullEnchantChance() {
        int skillLevel = getSkillLevel();

        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getExtractFullEnchantChance();
            }
        }

        return 0;
    }

    public double getExtractPartialEnchantChance() {
        int skillLevel = getSkillLevel();

        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getExtractPartialEnchantChance();
            }
        }

        return 0;
    }

    private ItemStack arcaneSalvageCheck(Map<Enchantment, Integer> enchants) {
        Player player = getPlayer();

        if (getArcaneSalvageRank() == 0 || !Permissions.arcaneSalvage(player)) {
            player.sendMessage(LocaleLoader.getString("Salvage.Skills.ArcaneFailed"));
            return null;
        }

        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta enchantMeta = (EnchantmentStorageMeta) book.getItemMeta();

        boolean downgraded = false;

        for (Entry<Enchantment, Integer> enchant : enchants.entrySet()) {
            int successChance = Misc.getRandom().nextInt(activationChance);

            if (!Salvage.arcaneSalvageEnchantLoss || getExtractFullEnchantChance() > successChance) {
                enchantMeta.addStoredEnchant(enchant.getKey(), enchant.getValue(), true);
            }
            else if (enchant.getValue() > 1 && Salvage.arcaneSalvageDowngrades && getExtractPartialEnchantChance() > successChance) {
                enchantMeta.addStoredEnchant(enchant.getKey(), enchant.getValue() - 1, true);
                downgraded = true;
            }
            else {
                downgraded = true;
            }
        }

        Map<Enchantment, Integer> newEnchants = enchantMeta.getStoredEnchants();

        if (newEnchants.isEmpty()) {
            player.sendMessage(LocaleLoader.getString("Salvage.Skills.ArcaneFailed"));
            return null;
        }

        if (downgraded || newEnchants.size() < enchants.size()) {
            player.sendMessage(LocaleLoader.getString("Salvage.Skills.ArcanePartial"));
        }
        else {
            player.sendMessage(LocaleLoader.getString("Salvage.Skills.ArcaneSuccess"));
        }

        book.setItemMeta(enchantMeta);
        return book;
    }

    /*
     * Salvage Anvil Placement
     */

    public boolean getPlacedAnvil(Material anvilType) {
        if (anvilType == Salvage.anvilMaterial) {
            return placedAnvil;
        }

        return true;
    }

    public void togglePlacedAnvil(Material anvilType) {
        if (anvilType == Salvage.anvilMaterial) {
            placedAnvil = !placedAnvil;
        }
    }

    /*
     * Salvage Anvil Usage
     */

    public int getLastAnvilUse(Material anvilType) {
        if (anvilType == Salvage.anvilMaterial) {
            return lastClick;
        }

        return 0;
    }

    public void setLastAnvilUse(Material anvilType, int value) {
        if (anvilType == Salvage.anvilMaterial) {
            lastClick = value;
        }
    }

    public void actualizeLastAnvilUse(Material anvilType) {
        if (anvilType == Salvage.anvilMaterial) {
            lastClick = (int) (System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR);
        }
    }
}
