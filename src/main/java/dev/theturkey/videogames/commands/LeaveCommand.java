package dev.theturkey.videogames.commands;

import dev.theturkey.videogames.games.GameManager;
import org.bukkit.entity.Player;


public class LeaveCommand implements IVGCommand
{
	public boolean execute(Player player, String[] args)
	{
		GameManager.leaveGame(player);
		return true;
	}
}
