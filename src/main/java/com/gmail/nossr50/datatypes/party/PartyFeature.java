package com.gmail.nossr50.datatypes.party;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.StringUtils;

public enum PartyFeature {
    CHAT,
    TELEPORT,
    ALLIANCE,
    ITEM_SHARE,
    XP_SHARE;

    public String getLocaleString() {
        return LocaleLoader.getString("Party.ItemShare.Category." + StringUtils.getCapitalized(this.toString()));
    }

    public String getFeatureLockedLocaleString() {
        return LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Party.Feature.Locked." + StringUtils.getPrettyPartyFeatureString(this).replace(" ", ""), Config.getInstance().getPartyFeatureUnlockLevel(this)));
    }
}
