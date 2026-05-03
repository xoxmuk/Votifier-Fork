package ru.xoxmuk.votifierfork.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.bencodez.simpleapi.command.TabCompleteHandler;
import com.bencodez.simpleapi.messages.MessageAPI;
import ru.xoxmuk.votifierfork.VotifierFork;

public class VotifierForkTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {

		ArrayList<String> tab = new ArrayList<String>();

		Set<String> cmds = new HashSet<String>();

		cmds.addAll(TabCompleteHandler.getInstance().getTabCompleteOptions(VotifierFork.getInstance().getCommands(),
				sender, args, args.length - 1));

		for (String str : cmds) {
			if (MessageAPI.startsWithIgnoreCase(str, args[args.length - 1])) {
				tab.add(str);
			}
		}

		Collections.sort(tab, String.CASE_INSENSITIVE_ORDER);

		return tab;
	}

}
