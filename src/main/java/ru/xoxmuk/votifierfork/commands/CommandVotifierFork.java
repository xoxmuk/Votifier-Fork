package ru.xoxmuk.votifierfork.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.bencodez.simpleapi.command.CommandHandler;
import ru.xoxmuk.votifierfork.VotifierFork;

public class CommandVotifierFork implements CommandExecutor {

	private VotifierFork plugin;

	public CommandVotifierFork(VotifierFork plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		for (CommandHandler commandHandler : plugin.getCommands()) {
			if (commandHandler.runCommand(sender, args)) {
				return true;
			}
		}

		sender.sendMessage(ChatColor.RED + "No valid arguments, see /VotifierFork help!");
		return true;
	}

}
