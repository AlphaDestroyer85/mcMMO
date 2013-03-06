package com.gmail.nossr50.skills.repair;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;

public class Repair {
    public static int    repairMasteryMaxBonusLevel = AdvancedConfig.getInstance().getRepairMasteryMaxLevel();
    public static double repairMasteryMaxBonus      = AdvancedConfig.getInstance().getRepairMasteryMaxBonus();

    public static Material anvilMaterial  = Config.getInstance().getRepairAnvilMaterial();
    public static boolean  anvilMessagesEnabled = Config.getInstance().getRepairAnvilMessagesEnabled();
}
