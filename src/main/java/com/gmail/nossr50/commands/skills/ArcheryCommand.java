package com.gmail.nossr50.commands.skills;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.SkillAbility;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.archery.Archery;
import com.gmail.nossr50.util.Permissions;

public class ArcheryCommand extends SkillCommand {
    private String skillShotBonus;
    private String dazeChance;
    private String dazeChanceLucky;
    private String retrieveChance;
    private String retrieveChanceLucky;

    private boolean canSkillShot;
    private boolean canDaze;
    private boolean canRetrieve;

    public ArcheryCommand() {
        super(SkillType.ARCHERY);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue, boolean isLucky) {
        // SKILL SHOT
        if (canSkillShot) {
            double bonus = (skillValue / Archery.skillShotIncreaseLevel) * Archery.skillShotIncreasePercentage;
            skillShotBonus = percent.format(Math.min(bonus, Archery.skillShotMaxBonusPercentage));
        }

        // DAZE
        if (canDaze) {
            String[] dazeStrings = calculateAbilityDisplayValues(skillValue, SkillAbility.DAZE, isLucky);
            dazeChance = dazeStrings[0];
            dazeChanceLucky = dazeStrings[1];
        }

        // RETRIEVE
        if (canRetrieve) {
            String[] retrieveStrings = calculateAbilityDisplayValues(skillValue, SkillAbility.RETRIEVE, isLucky);
            retrieveChance = retrieveStrings[0];
            retrieveChanceLucky = retrieveStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canSkillShot = Permissions.bonusDamage(player, skill);
        canDaze = Permissions.skillAbilityEnabled(player, SkillAbility.DAZE);
        canRetrieve = Permissions.skillAbilityEnabled(player, SkillAbility.RETRIEVE);
    }

    @Override
    protected List<String> effectsDisplay() {
        List<String> messages = new ArrayList<String>();

        if (canSkillShot) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Archery.Effect.0"), LocaleLoader.getString("Archery.Effect.1")));
        }

        if (canDaze) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Archery.Effect.2"), LocaleLoader.getString("Archery.Effect.3", Archery.dazeModifier)));
        }

        if (canRetrieve) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Archery.Effect.4"), LocaleLoader.getString("Archery.Effect.5")));
        }

        return messages;
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<String>();

        if (canSkillShot) {
            messages.add(LocaleLoader.getString("Archery.Combat.SkillshotBonus", skillShotBonus));
        }

        if (canDaze) {
            messages.add(LocaleLoader.getString("Archery.Combat.DazeChance", dazeChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", dazeChanceLucky) : ""));
        }

        if (canRetrieve) {
            messages.add(LocaleLoader.getString("Archery.Combat.RetrieveChance", retrieveChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", retrieveChanceLucky) : ""));
        }

        return messages;
    }
}
