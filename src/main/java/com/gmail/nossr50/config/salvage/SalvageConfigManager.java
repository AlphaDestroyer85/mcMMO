package com.gmail.nossr50.config.salvage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.salvage.salvageables.Salvageable;

public class SalvageConfigManager {
    private List<Salvageable> salvageables;

    public SalvageConfigManager(mcMMO plugin) {
        salvageables = new ArrayList<Salvageable>();

        Pattern pattern = Pattern.compile("salvage\\.(?:.+)\\.yml");
        File dataFolder = plugin.getDataFolder();
        File vanilla = new File(dataFolder, "salvage.vanilla.yml");

        if (!vanilla.exists()) {
            plugin.saveResource("salvage.vanilla.yml", false);
        }

        for (String fileName : dataFolder.list()) {
            if (!pattern.matcher(fileName).matches()) {
                continue;
            }

            File file = new File(dataFolder, fileName);

            if (file.isDirectory()) {
                continue;
            }

            SalvageConfig salvageConfig = new SalvageConfig(fileName);
            List<Salvageable> sConfigSalvageables = salvageConfig.getLoadedSalvageables();

            if (sConfigSalvageables != null) {
                salvageables.addAll(sConfigSalvageables);
            }
        }
    }

    public List<Salvageable> getLoadedSalvageables() {
        if (salvageables == null) {
            return new ArrayList<Salvageable>();
        }

        return salvageables;
    }
}
