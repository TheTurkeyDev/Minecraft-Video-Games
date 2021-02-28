package dev.theturkey.mcarcade.commands;

import dev.theturkey.mcarcade.MCACore;
import dev.theturkey.mcarcade.games.GameManager;
import dev.theturkey.mcarcade.leaderboard.LeaderBoardManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GamesCommand implements IVGCommand
{
	public boolean execute(Player player, String[] args)
	{
		MCACore.sendMessage(player, ChatColor.DARK_GREEN + "Playable Games (To play run `/mcarcade play <game name>`):");
		for(String game : GameManager.GAMES.keySet())
			player.sendMessage(ChatColor.GREEN + "- " + game);

		LeaderBoardManager.showLeaderBoards(player);

		return true;
	}
}
