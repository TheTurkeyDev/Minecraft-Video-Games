package dev.theturkey.videogames.games;

import dev.theturkey.videogames.VGCore;
import dev.theturkey.videogames.games.brickbreaker.BrickBreakerGame;
import dev.theturkey.videogames.games.minesweeper.MineSweeper;
import dev.theturkey.videogames.games.minesweeper.MinesweeperDifficulty;
import dev.theturkey.videogames.games.tetris.TetrisGame;
import dev.theturkey.videogames.leaderboard.LeaderBoardManager;
import dev.theturkey.videogames.util.Vector2I;
import dev.theturkey.videogames.util.Vector3I;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
	public static final Map<String, VideoGamesEnum> GAMES = new HashMap<>();

	static
	{
		GAMES.put("brickbreaker", VideoGamesEnum.BRICK_BREAKER);
		GAMES.put("minesweeper", VideoGamesEnum.MINESWEEPER);
		GAMES.put("tetris", VideoGamesEnum.TETRIS);
	}

	private static final List<Vector2I> ACTIVE_GAME_LOCS = new ArrayList<>();
	private static final Map<Player, VideoGameBase> ACTIVE_GAMES = new HashMap<>();

	public static void playGame(Player player, VideoGamesEnum game, String[] args)
	{
		if(ACTIVE_GAMES.containsKey(player))
		{
			player.sendMessage("You are already currently in a game! Use '/leave' first!");
			return;
		}

		LeaderBoardManager.removeLeaderBoards(player);

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
				MinesweeperDifficulty difficulty;
				if(args.length > 1)
				{
					switch(args[1].toLowerCase())
					{
						case "easy":
							difficulty = MinesweeperDifficulty.EASY;
							break;
						case "medium":
							difficulty = MinesweeperDifficulty.MEDIUM;
							break;
						case "hard":
							difficulty = MinesweeperDifficulty.HARD;
							break;
						case "custom":
							if(args.length <= 4)
							{
								player.sendMessage(ChatColor.RED + "You must specify the width, height, and number of bombs for custom minesweeper games!");
								player.sendMessage(ChatColor.RED + "Syntax: '/play minesweeper custom <width> <height> <# bombs>'");
								return;
							}

							String widthStr = args[2].trim();
							int width = -1;
							if(widthStr.matches("[0-9]{1,2}"))
							{
								width = Integer.parseInt(widthStr);
								if(width > 40 || width < 3)
									width = -1;
							}

							if(width == -1)
							{
								player.sendMessage(ChatColor.RED + "The width must be a number greater than 2 and less than or equal to 40");
								return;
							}

							String heightStr = args[3].trim();
							int height = -1;
							if(heightStr.matches("[0-9]{1,2}"))
							{
								height = Integer.parseInt(heightStr);
								if(height > 30 || height < 3)
									height = -1;
							}
							if(height == -1)
							{
								player.sendMessage(ChatColor.RED + "The height must be a number greater than 2 and less than or equal to 30");
								return;
							}


							String bombsStr = args[4].trim();
							int bombs = -1;
							if(bombsStr.matches("[0-9]{1,3}"))
							{
								bombs = Integer.parseInt(bombsStr);
								if(bombs > (width * height) - 1 || bombs == 0)
									bombs = -1;
							}

							if(bombs == -1)
							{
								player.sendMessage(ChatColor.RED + "The number of bombs must be a number greater than 0 and less than or equal to one less than the number of tiles");
								return;
							}

							difficulty = new MinesweeperDifficulty("Custom", width, height, bombs);
							break;
						default:
							player.sendMessage(ChatColor.RED + "Sorry " + args[1] + " is not a valid difficulty!");
							player.sendMessage(ChatColor.RED + "Valid difficulties are: easy, medium, hard, and custom!");
							return;
					}
				}
				else
				{
					difficulty = MinesweeperDifficulty.MEDIUM;
				}

				vGame = new MineSweeper(gameLoc, difficulty);
				break;
			case TETRIS:
				vGame = new TetrisGame(gameLoc);
				break;
			default:
				player.sendMessage("Sorry that is not a valid game!");
				return;
		}

		ACTIVE_GAMES.put(player, vGame);

		double halfWidth = vGame.getWidth() / 2d;
		Vector3I gameLocSale = vGame.getGameLocScaled();
		VGCore.gameWorld.getBlockAt(new Location(VGCore.gameWorld, gameLocSale.getX() + halfWidth, vGame.getYBase(), gameLocSale.getZ())).setType(Material.BEDROCK);

		Location playerLoc = new Location(VGCore.gameWorld, gameLocSale.getX() + halfWidth + 0.5, vGame.getYBase() + 1, gameLocSale.getZ() + 0.5, 0, 0);
		player.teleport(playerLoc, PlayerTeleportEvent.TeleportCause.COMMAND);

		Bukkit.getScheduler().scheduleSyncDelayedTask(VGCore.getPlugin(), () ->
		{
			vGame.constructGame(player);
			vGame.startGame(player);
		}, 10);

	}

	public static void leaveGame(Player player)
	{
		if(!ACTIVE_GAMES.containsKey(player))
		{
			player.sendMessage("You are not currently in a game!");
			return;
		}

		World world = player.getWorld();

		sendPlayerToSpawn(player);
		VideoGameBase game = ACTIVE_GAMES.remove(player);
		game.endGame(player);
		ACTIVE_GAME_LOCS.remove(game.getGameLoc());

		Vector3I gameLocSale = game.getGameLocScaled();
		world.getBlockAt(new Location(world, gameLocSale.getX() + (game.getWidth() / 2d), game.getYBase(), gameLocSale.getZ())).setType(Material.AIR);
		game.deconstructGame(player);

		LeaderBoardManager.showLeaderBoards(player);
	}

	public static void sendPlayerToSpawn(Player player)
	{
		player.teleport(VGCore.SPAWN, PlayerTeleportEvent.TeleportCause.COMMAND);
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
