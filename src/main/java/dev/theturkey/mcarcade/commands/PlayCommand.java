package dev.theturkey.mcarcade.commands;

import dev.theturkey.mcarcade.MCACore;
import dev.theturkey.mcarcade.games.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayCommand implements IVGCommand
{
	public boolean execute(Player player, String[] args)
	{
		if(args.length == 0)
		{
			MCACore.sendMessage(player, ChatColor.RED + "You must include the game you want to play!");
			return false;
		}

		String game = args[0].toLowerCase();
		if(GameManager.GAMES.containsKey(game))
			GameManager.playGame(player, GameManager.GAMES.get(game), args);
		else
			MCACore.sendMessage(player, ChatColor.RED + "Sorry that is not a valid game!");

		return true;
	}
}
