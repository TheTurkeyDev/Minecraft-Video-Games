package dev.theturkey.videogames.games;

import dev.theturkey.videogames.games.brickbreaker.BrickBreakerGame;
import dev.theturkey.videogames.util.Vector2I;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManager
{
	public static final Location SPAWN = new Location(null, 0.5, 255, 0.5, 0, 0);

	private static final List<Vector2I> ACTIVE_GAME_LOCS = new ArrayList<>();
	private static final Map<Player, VideoGameBase> ACTIVE_GAMES = new HashMap<>();

	public static void playGame(Player player, VideoGamesEnum game)
	{
		if(ACTIVE_GAMES.containsKey(player))
		{
			player.sendRawMessage("You are already currently in a game! Use '/leave' first!");
			return;
		}

		Vector2I gameLoc = null;

		int xx = 0, zz = 0, dx = 0, dz = -1;
		int t = 32;
		int maxI = t * t;
		for(int i = 0; i < maxI; i++)
		{
			if((xx == zz) || ((xx < 0) && (xx == -zz)) || ((xx > 0) && (xx == 1 - zz)))
			{
				t = dx;
				dx = -dz;
				dz = t;
			}
			xx += dx;
			zz += dz;
			gameLoc = new Vector2I(xx, zz);
			if(!ACTIVE_GAME_LOCS.contains(gameLoc))
			{
				ACTIVE_GAME_LOCS.add(gameLoc);
				break;
			}
		}

		VideoGameBase vGame;
		switch(game)
		{
			case BRICK_BREAKER:
				BrickBreakerGame bbgame = new BrickBreakerGame(gameLoc);
				vGame = bbgame;
				break;
			default:
				player.sendRawMessage("Sorry that is not a valid game!");
				return;
		}

		ACTIVE_GAMES.put(player, vGame);
		vGame.constructGame(player.getWorld(), player);

		vGame.startGame(player.getWorld(), player);
	}

	public static void leaveGame(Player player)
	{
		if(!ACTIVE_GAMES.containsKey(player))
		{
			player.sendRawMessage("You are not currently in a game!");
			return;
		}

		SPAWN.setWorld(player.getWorld());
		player.teleport(SPAWN, PlayerTeleportEvent.TeleportCause.COMMAND);
		VideoGameBase game = ACTIVE_GAMES.remove(player);
		game.endGame(player.getWorld(), player);
		ACTIVE_GAME_LOCS.remove(game.getGameLoc());
		game.deconstructGame(player.getWorld(), player);
	}

	public static void reset()
	{
		for(Player player : ACTIVE_GAMES.keySet())
			leaveGame(player);
	}
}
