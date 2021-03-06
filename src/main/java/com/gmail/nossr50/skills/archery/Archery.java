package com.gmail.nossr50.skills.archery;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.util.Misc;

public class Archery {
    private static List<TrackedEntity> trackedEntities = new ArrayList<TrackedEntity>();

    public static int    skillShotIncreaseLevel      = AdvancedConfig.getInstance().getSkillShotIncreaseLevel();
    public static double skillShotIncreasePercentage = AdvancedConfig.getInstance().getSkillShotIncreasePercentage();
    public static double skillShotMaxBonusPercentage = AdvancedConfig.getInstance().getSkillShotBonusMax();
    public static double skillShotMaxBonusDamage     = AdvancedConfig.getInstance().getSkillShotDamageMax();

    public static double dazeModifier      = AdvancedConfig.getInstance().getDazeModifier();

    public static final double DISTANCE_XP_MULTIPLIER = 0.025;

    protected static void incrementTrackerValue(LivingEntity livingEntity) {
        for (TrackedEntity trackedEntity : trackedEntities) {
            if (trackedEntity.getLivingEntity().getEntityId() == livingEntity.getEntityId()) {
                trackedEntity.incrementArrowCount();
                return;
            }
        }

        addToTracker(livingEntity); // If the entity isn't tracked yet
    }

    protected static void addToTracker(LivingEntity livingEntity) {
        TrackedEntity trackedEntity = new TrackedEntity(livingEntity);

        trackedEntity.incrementArrowCount();
        trackedEntities.add(trackedEntity);
    }

    protected static void removeFromTracker(TrackedEntity trackedEntity) {
        trackedEntities.remove(trackedEntity);
    }

    /**
     * Check for arrow retrieval.
     *
     * @param livingEntity The entity hit by the arrows
     */
    public static void arrowRetrievalCheck(LivingEntity livingEntity) {
        for (Iterator<TrackedEntity> entityIterator = trackedEntities.iterator(); entityIterator.hasNext();) {
            TrackedEntity trackedEntity = entityIterator.next();

            if (trackedEntity.getID() == livingEntity.getUniqueId()) {
                Misc.dropItems(livingEntity.getLocation(), new ItemStack(Material.ARROW), trackedEntity.getArrowCount());
                entityIterator.remove();
                return;
            }
        }
    }

    public static Location stringToLocation(String location) {
        String[] values = location.split(",");

        return new Location(mcMMO.p.getServer().getWorld(values[0]), Double.parseDouble(values[1]), Double.parseDouble(values[2]), Double.parseDouble(values[3]), Float.parseFloat(values[4]), Float.parseFloat(values[5]));
    }

    public static String locationToString(Location location) {
        return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
    }
}
