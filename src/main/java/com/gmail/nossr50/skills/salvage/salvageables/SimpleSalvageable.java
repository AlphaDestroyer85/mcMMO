package com.gmail.nossr50.skills.salvage.salvageables;

import org.bukkit.Material;

import com.gmail.nossr50.datatypes.RepairAndSalvageItemType;
import com.gmail.nossr50.datatypes.RepairAndSalvageMaterialType;


public class SimpleSalvageable implements Salvageable {
    private final Material itemMaterial, salvageMaterial;
    private final int minimumQuantity, minimumLevel;
    private final short maximumDurability, baseSalvageDurability;
    private final byte salvageMetadata;
    private final RepairAndSalvageItemType salvageItemType;
    private final RepairAndSalvageMaterialType salvageMaterialType;
    private final double xpMultiplier;

    protected SimpleSalvageable(Material type, Material salvageMaterial, byte salvageMetadata, int minimumLevel, int minimumQuantity, short maximumDurability, RepairAndSalvageItemType salvageItemType, RepairAndSalvageMaterialType salvageMaterialType, double xpMultiplier) {
        this.itemMaterial = type;
        this.salvageMaterial = salvageMaterial;
        this.salvageMetadata = salvageMetadata;
        this.salvageItemType = salvageItemType;
        this.salvageMaterialType = salvageMaterialType;
        this.minimumLevel = minimumLevel;
        this.minimumQuantity = minimumQuantity;
        this.maximumDurability = maximumDurability;
        this.baseSalvageDurability = (short) (maximumDurability / minimumQuantity);
        this.xpMultiplier = xpMultiplier;
    }

    @Override
    public Material getItemMaterial() {
        return itemMaterial;
    }

    @Override
    public Material getSalvageMaterial() {
        return salvageMaterial;
    }

    @Override
    public byte getSalvageMaterialMetadata() {
        return salvageMetadata;
    }

    @Override
    public RepairAndSalvageItemType getSalvageItemType() {
        return salvageItemType;
    }

    @Override
    public RepairAndSalvageMaterialType getSalvageMaterialType() {
        return salvageMaterialType;
    }

    @Override
    public int getMinimumQuantity() {
        return minimumQuantity;
    }

    @Override
    public short getMaximumDurability() {
        return maximumDurability;
    }

    @Override
    public short getBaseSalvageDurability() {
        return baseSalvageDurability;
    }

    @Override
    public int getMinimumLevel() {
        return minimumLevel;
    }

    @Override
    public double getXpMultiplier() {
        return xpMultiplier;
    }
}
