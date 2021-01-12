package dev.theturkey.videogames.commands;

import dev.theturkey.videogames.games.GameManager;
import org.bukkit.entity.Player;

public class PlayCommand implements IVGCommand
{
	public boolean execute(Player player, String[] args)
	{
		if(args.length == 0)
		{
			player.sendRawMessage("You must include the game you want to play!");
			return false;
		}

		String game = args[0].toLowerCase();
		if(GameManager.GAMES.containsKey(game))
			GameManager.playGame(player, GameManager.GAMES.get(game), args);
		else
			player.sendRawMessage("Sorry that is not a valid game!");

		return true;
	}
}
