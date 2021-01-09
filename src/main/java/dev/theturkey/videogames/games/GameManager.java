package dev.theturkey.videogames.games;

import dev.theturkey.videogames.VGCore;
import dev.theturkey.videogames.games.brickbreaker.BrickBreakerGame;
import dev.theturkey.videogames.games.minesweeper.MineSweeper;
import dev.theturkey.videogames.util.Vector2I;
import dev.theturkey.videogames.util.Vector3I;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManager
{
	private static final Location SPAWN = new Location(null, 0.5, 255, 0.5, 0, 0);
	public static final Map<String, VideoGamesEnum> GAMES = new HashMap<>();

	static
	{
		GAMES.put("brickbreaker", VideoGamesEnum.BRICK_BREAKER);
		GAMES.put("minesweeper", VideoGamesEnum.MINESWEEPER);
	}

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

		int xx = 0, zz = 0, dx = 128, dz = -1;
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
				vGame = new BrickBreakerGame(gameLoc);
				break;
			case MINESWEEPER:
				vGame = new MineSweeper(gameLoc);
				break;
			default:
				player.sendRawMessage("Sorry that is not a valid game!");
				return;
		}

		ACTIVE_GAMES.put(player, vGame);

		double halfWidth = vGame.getWidth() / 2d;
		World world = player.getWorld();
		Vector3I gameLocSale = vGame.getGameLocScaled();
		world.getBlockAt(new Location(world, gameLocSale.getX() + halfWidth, vGame.getYBase(), gameLocSale.getZ())).setType(Material.BEDROCK);

		Location playerLoc = new Location(world, gameLocSale.getX() + halfWidth + 0.5, vGame.getYBase() + 1, gameLocSale.getZ() + 0.5, 0, 0);
		player.teleport(playerLoc, PlayerTeleportEvent.TeleportCause.COMMAND);

		Bukkit.getScheduler().scheduleSyncDelayedTask(VGCore.getPlugin(), () ->
		{
			vGame.constructGame(player.getWorld(), player);
			vGame.startGame(player.getWorld(), player);
		}, 10);

	}

	public static void leaveGame(Player player)
	{
		if(!ACTIVE_GAMES.containsKey(player))
		{
			player.sendRawMessage("You are not currently in a game!");
			return;
		}

		World world = player.getWorld();

		sendPlayerToSpawn(player);
		VideoGameBase game = ACTIVE_GAMES.remove(player);
		game.endGame(player.getWorld(), player);
		ACTIVE_GAME_LOCS.remove(game.getGameLoc());


		Vector3I gameLocSale = game.getGameLocScaled();
		world.getBlockAt(new Location(world, gameLocSale.getX() + (game.getWidth() / 2d), game.getYBase(), gameLocSale.getZ())).setType(Material.AIR);
		game.deconstructGame(player.getWorld(), player);
	}

	public static void sendPlayerToSpawn(Player player)
	{
		SPAWN.setWorld(player.getWorld());
		player.teleport(SPAWN, PlayerTeleportEvent.TeleportCause.COMMAND);
		player.setInvisible(true);
	}

	public static void reset()
	{
		for(Player player : ACTIVE_GAMES.keySet())
			leaveGame(player);
	}

	public static VideoGameBase getGameForPlayer(Player player)
	{
		return ACTIVE_GAMES.get(player);
	}

	public static VideoGameBase getGameForEntity(Entity entity)
	{
		for(VideoGameBase vgb : ACTIVE_GAMES.values())
			if(vgb.isEntInGame(entity))
				return vgb;
		return null;
	}

}
