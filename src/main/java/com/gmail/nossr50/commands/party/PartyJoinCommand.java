package com.gmail.nossr50.commands.party;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;

public class PartyJoinCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 2:
            case 3:
                String targetName = Misc.getMatchedPlayerName(args[1]);
                McMMOPlayer mcMMOTarget = UserManager.getPlayer(targetName);

                if (!CommandUtils.checkPlayerExistence(sender, targetName, mcMMOTarget)) {
                    return true;
                }

                Player target = mcMMOTarget.getPlayer();

                if (!mcMMOTarget.inParty()) {
                    sender.sendMessage(LocaleLoader.getString("Party.PlayerNotInParty", targetName));
                    return true;
                }

                Player player = (Player) sender;
                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
                Party targetParty = mcMMOTarget.getParty();

                if (player.equals(target) || (mcMMOPlayer.inParty() && mcMMOPlayer.getParty().equals(targetParty))) {
                    sender.sendMessage(LocaleLoader.getString("Party.Join.Self"));
                    return true;
                }

                String password = getPassword(args);

                // Make sure party passwords match
                if (!PartyManager.checkPartyPassword(player, targetParty, password)) {
                    return true;
                }

                String partyName = targetParty.getName();

                // Changing parties
                if (!PartyManager.changeOrJoinParty(mcMMOPlayer, partyName)) {
                    return true;
                }

                player.sendMessage(LocaleLoader.getString("Commands.Party.Join", partyName));
                PartyManager.addToParty(mcMMOPlayer, targetParty);
                return true;

            default:
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.3", "party", "join", "<" + LocaleLoader.getString("Commands.Usage.Player") + ">", "[" + LocaleLoader.getString("Commands.Usage.Password") + "]"));
                return true;
        }
    }

    private String getPassword(String[] args) {
        if (args.length == 3) {
            return args[2];
        }

        return null;
    }
}
