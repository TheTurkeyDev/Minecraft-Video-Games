package dev.theturkey.videogames.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GamesCommand implements IVGCommand
{
	public boolean execute(Player player, String[] args)
	{
		player.sendRawMessage(ChatColor.DARK_GREEN + "Playable Games (To play run `/play <game name>`):");
		player.sendRawMessage(ChatColor.GREEN + "- brickbreaker");

		return true;
	}
}
