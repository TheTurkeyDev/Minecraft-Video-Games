package dev.theturkey.videogames.commands;

import dev.theturkey.videogames.games.GameManager;
import dev.theturkey.videogames.games.VideoGamesEnum;
import org.bukkit.entity.Player;

public class PlayCommand implements IVGCommand
{
	public boolean execute(Player player, String[] args)
	{
		switch(args[0])
		{
			case "breakout":
			case "brickbreaker":
				GameManager.playGame(player, VideoGamesEnum.BRICK_BREAKER);
				break;
			//case "test":
				//GameManager.playGame(player, VideoGamesEnum.TEST);
				//break;
			default:
				player.sendRawMessage("Sorry that is not a valid game!");
				break;
		}

		return true;
	}
}
