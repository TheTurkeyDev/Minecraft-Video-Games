package dev.theturkey.videogames.commands;

import dev.theturkey.videogames.games.GameManager;
import dev.theturkey.videogames.games.VideoGamesEnum;
import org.bukkit.entity.Player;

public class PlayCommand implements IVGCommand
{
	public boolean execute(Player player, String[] args)
	{
		GameManager.playGame(player, VideoGamesEnum.BRICK_BREAKER);
		return true;
	}
}
