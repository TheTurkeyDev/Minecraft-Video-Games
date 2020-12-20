package dev.theturkey.videogames.commands;

import dev.theturkey.videogames.games.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class LeaveCommand implements IVGCommand
{
	public boolean execute(Player player, String[] args)
	{
		GameManager.leaveGame(player);
		return true;
	}
}
