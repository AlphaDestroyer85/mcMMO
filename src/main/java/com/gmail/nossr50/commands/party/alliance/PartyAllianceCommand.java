package com.gmail.nossr50.commands.party.alliance;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;

import com.google.common.collect.ImmutableList;

public class PartyAllianceCommand implements TabExecutor {
    private Player player;
    private Party playerParty;
    private Party targetParty;

    public static final List<String> ALLIANCE_SUBCOMMANDS = ImmutableList.of("invite", "accept");

    private CommandExecutor partyAllianceAcceptCommand = new PartyAllianceAcceptCommand();
    private CommandExecutor partyAllianceInviteCommand = new PartyAllianceInviteCommand();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        System.out.println("args.length " + args.length);
        switch (args.length) {
            case 1:
            case 2:
            case 3:
                if (args[1].equalsIgnoreCase("invite")) {
                    return partyAllianceInviteCommand.onCommand(sender, command, label, args);
                }

                if (args[1].equalsIgnoreCase("accept")) {
                    return partyAllianceAcceptCommand.onCommand(sender, command, label, args);
                }

                player = (Player) sender;
                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

                playerParty = mcMMOPlayer.getParty();

                if (playerParty.getAlly() == null) {
                    printUsage();
                    return true;
                }

                targetParty = playerParty.getAlly();

                displayPartyHeader();
                displayMemberInfo();
                return true;

            default:
                return false;
        }
    }

    private boolean printUsage() {
        player.sendMessage(LocaleLoader.getString("Commands.Party.Alliance.Help.0"));
        player.sendMessage(LocaleLoader.getString("Commands.Party.Alliance.Help.1"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1:
                List<String> matches = StringUtil.copyPartialMatches(args[0], ALLIANCE_SUBCOMMANDS, new ArrayList<String>(ALLIANCE_SUBCOMMANDS.size()));

                if (matches.size() == 0) {
                    Set<String> playerNames = UserManager.getPlayerNames();
                    return StringUtil.copyPartialMatches(args[0], playerNames, new ArrayList<String>(playerNames.size()));
                }

                return matches;
            default:
                return ImmutableList.of();
        }
    }

    private void displayPartyHeader() {
        player.sendMessage(LocaleLoader.getString("Commands.Party.Alliance.Header"));
        player.sendMessage(LocaleLoader.getString("Commands.Party.Alliance.Ally", playerParty.getName(), targetParty.getName()));
    }

    private void displayMemberInfo() {
        player.sendMessage(LocaleLoader.getString("Commands.Party.Alliance.Members.Header"));
        player.sendMessage(createMembersList(playerParty));
        player.sendMessage(ChatColor.DARK_GRAY + "----------------------------");
        player.sendMessage(createMembersList(targetParty));
    }

    private String createMembersList(Party party) {
        StringBuilder memberList = new StringBuilder();

        for (String memberName : party.getMembers()) {
            Player member = mcMMO.p.getServer().getPlayerExact(memberName);

            if (party.getLeader().equalsIgnoreCase(memberName)) {
                memberList.append(ChatColor.GOLD);
            }
            else if (member != null) {
                memberList.append(ChatColor.WHITE);
            }
            else {
                memberList.append(ChatColor.GRAY);
            }

            memberList.append(memberName).append(" ");
        }

        return memberList.toString();
    }
}
