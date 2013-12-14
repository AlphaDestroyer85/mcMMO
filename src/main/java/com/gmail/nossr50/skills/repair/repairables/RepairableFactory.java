package com.gmail.nossr50.skills.repair.repairables;

import org.bukkit.Material;

import com.gmail.nossr50.datatypes.RepairAndSalvageItemType;
import com.gmail.nossr50.datatypes.RepairAndSalvageMaterialType;


public class RepairableFactory {
    public static Repairable getRepairable(Material itemMaterial, Material repairMaterial, byte repairMetadata, int minimumQuantity, short maximumDurability) {
        return getRepairable(itemMaterial, repairMaterial, repairMetadata, 0, minimumQuantity, maximumDurability, RepairAndSalvageItemType.OTHER, RepairAndSalvageMaterialType.OTHER, 1);
    }

    public static Repairable getRepairable(Material itemMaterial, Material repairMaterial, byte repairMetadata, int minimumLevel, int minimumQuantity, short maximumDurability, RepairAndSalvageItemType repairItemType, RepairAndSalvageMaterialType repairMaterialType, double xpMultiplier) {
        // TODO: Add in loading from config what type of repairable we want.
        return new SimpleRepairable(itemMaterial, repairMaterial, repairMetadata, minimumLevel, minimumQuantity, maximumDurability, repairItemType, repairMaterialType, xpMultiplier);
    }
}
