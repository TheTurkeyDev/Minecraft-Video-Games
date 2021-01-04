package dev.theturkey.videogames.games.brickbreaker;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.theturkey.videogames.VGCore;
import dev.theturkey.videogames.games.VideoGameBase;
import dev.theturkey.videogames.util.Hologram;
import dev.theturkey.videogames.util.Vector2I;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class BrickBreakerGame extends VideoGameBase
{
	private static final int DIST_FROM_PLAYER = 30;
	private boolean ballGrabbed;
	private Paddle paddle;
	private Ball ball;
	private Hologram healthHologram;
	private Hologram levelHologram;
	private int gameTick = -1;
	private int lives = 3;
	private int level = 0;

	//In world this is mirrored
	private int[][] blocks = new int[][]{
			{1, 2, 3, 4, 5, 4, 3, 2, 1},
			{1, 2, 3, 4, 5, 4, 3, 2, 1},
			{1, 2, 3, 4, 5, 4, 3, 2, 1},
			{1, 2, 3, 4, 5, 4, 3, 2, 1},
			{1, 2, 3, 4, 5, 4, 3, 2, 1},
			{1, 2, 3, 4, 5, 4, 3, 2, 1}
	};

	public BrickBreakerGame(Vector2I gameLoc)
	{
		super(gameLoc);
	}

	@Override
	public void constructGame(World world, Player player)
	{
		Vector2I gameLoc = getGameLocScaled();

		paddle = new Paddle();
		paddle.setWidth(4, world, getGameLocScaled().getX(), getYBase() - 4, getGameLocScaled().getY() + 0.5 + DIST_FROM_PLAYER);

		ballGrabbed = true;
		ball = new Ball(world, gameLoc.getX() + 0.5, getYBase() - 4, gameLoc.getY() + 0.5 + DIST_FROM_PLAYER);

		Location playerLoc = getPlayerLoc(world);
		healthHologram = new Hologram(world, playerLoc.clone().add(3, -2, 5), ChatColor.RED + "LIVES: \u2665\u2665\u2665");
		levelHologram = new Hologram(world, playerLoc.clone().add(-3, -2, 5), ChatColor.RED + "Level: " + level);

		for(int x = -10; x < 10; x++)
		{
			for(int yy = -15; yy < 25; yy++)
			{
				world.getBlockAt(gameLoc.getX() + x, getYBase() + yy, gameLoc.getY() + DIST_FROM_PLAYER + 1).setType(Material.BLACK_CONCRETE);
				if(x == -10 || x == 9 || yy == -15 || yy == 24)
					world.getBlockAt(gameLoc.getX() + x, getYBase() + yy, gameLoc.getY() + DIST_FROM_PLAYER).setType(Material.WHITE_CONCRETE);
			}
		}
	}

	@Override
	public void startGame(World world, Player player)
	{
		super.startGame(world, player);
		lives = 3;
		level = 0;
		nextLevel(world);

		player.sendRawMessage(ChatColor.GREEN + "Jump to start!");

		gameTick = Bukkit.getScheduler().scheduleSyncRepeatingTask(VGCore.getPlugin(), () ->
		{
			float yaw = player.getLocation().getYaw();
			double newX = Math.max(-9, Math.min(9, (DIST_FROM_PLAYER * Math.tan(Math.toRadians(yaw))) - 0.5));
			paddle.update(getGameLocScaled().getX() - newX);
			if(ballGrabbed)
			{
				ball.setGameX(newX + 0.5);
				ball.updateLoc();
			}
			else
			{
				ball.update(newX, paddle.getWidth());
				if(ball.getGameY() < -3)
				{
					ball.setVelocity(0, 0);
					ball.setGameY(0.75);
					lives--;
					if(lives == 0)
					{
						endGame(world, player);
						deconstructGame(world, player);
						player.sendRawMessage(ChatColor.RED + "You Lost!");
						player.sendRawMessage(ChatColor.RED + "You made it to level " + level + "!");
					}
					else
					{
						StringBuilder builder = new StringBuilder(ChatColor.RED.toString()).append("LIVES: ");
						for(int i = 0; i < lives; i++)
							builder.append("\u2665");
						healthHologram.setText(builder.toString());
						ballGrabbed = true;
					}
				}
			}
		}, 0, 1);
	}

	@Override
	public void endGame(World world, Player player)
	{
		super.endGame(world, player);
		Bukkit.getScheduler().cancelTask(gameTick);
	}

	@Override
	public void deconstructGame(World world, Player player)
	{
		Vector2I gameLoc = getGameLocScaled();
		world.getBlockAt(new Location(world, gameLoc.getX(), 49, gameLoc.getY())).setType(Material.AIR);
		paddle.remove();
		ball.remove();
		healthHologram.remove();
		levelHologram.remove();

		for(int x = -10; x < 10; x++)
		{
			for(int yy = -15; yy < 25; yy++)
			{
				world.getBlockAt(gameLoc.getX() + x, getYBase() + yy, gameLoc.getY() + DIST_FROM_PLAYER + 1).setType(Material.AIR);
				world.getBlockAt(gameLoc.getX() + x, getYBase() + yy, gameLoc.getY() + DIST_FROM_PLAYER).setType(Material.AIR);
			}
		}
	}

	@Override
	public boolean isEntInGame(Entity entity)
	{
		return entity.equals(ball.getEntity());
	}

	@Override
	public void onPlayerJump()
	{
		if(ballGrabbed)
		{
			ball.setVelocity(0, 0.5);
			ballGrabbed = false;
		}
	}

	@Override
	public int getYBase()
	{
		return 75;
	}

	@Override
	public void onEntityCollide(Entity entity)
	{
		Vector2I gameLoc = getGameLocScaled();
		Location entLoc = entity.getLocation().clone().subtract(new Location(entity.getWorld(), gameLoc.getX(), getYBase(), gameLoc.getY()));
		int col = (entLoc.getBlockX() + 9) / 2;
		int row = (entLoc.getBlockY() - 12) / 2;
		if(row >= 0 && row < blocks.length && col >= 0 && col < blocks[row].length && blocks[row][col] != 0)
		{
			blocks[row][col]--;
			if(isLevelComplete())
				nextLevel(entity.getWorld());
			else
				ball.bounceY();
			updateBlocks(entity.getWorld());
		}
	}

	public void nextLevel(World world)
	{
		Reader levelsStream = new InputStreamReader(VGCore.getPlugin().getResource("brick_breaker_levels.json"), StandardCharsets.UTF_8);
		BufferedReader reader = new BufferedReader(levelsStream);
		JsonObject leveljson = VGCore.JSON_PARSER.parse(reader).getAsJsonArray().get(level).getAsJsonObject();
		blocks = new int[6][9];
		for(int row = 0; row < 6; row++)
		{
			JsonArray rowArray = leveljson.getAsJsonArray("level_data").get(row).getAsJsonArray();
			for(int col = 0; col < 9; col++)
				blocks[5 - row][8 - col] = rowArray.get(col).getAsInt();
		}

		updateBlocks(world);
		level++;
		ball.setVelocity(0, 0);
		ball.setGameY(0.75);
		ballGrabbed = true;
		levelHologram.setText(ChatColor.RED + "Level: " + level);
	}

	public void updateBlocks(World world)
	{
		Vector2I gameLoc = getGameLocScaled();
		for(int x = -9; x < 10; x++)
		{
			for(int yy = 12; yy < 23; yy++)
			{
				int colIndex = (x + 9) / 2;
				int rowIndex = (yy - 12) / 2;
				if(yy % 2 == 0 && colIndex < blocks[rowIndex].length)
				{
					int hitPoints = blocks[rowIndex][colIndex];
					world.getBlockAt(gameLoc.getX() + x, getYBase() + yy, gameLoc.getY() + DIST_FROM_PLAYER).setType(getMatForHP(hitPoints));
				}
			}
		}
	}

	public boolean isLevelComplete()
	{
		for(int row = 0; row < 6; row++)
			for(int col = 0; col < 9; col++)
				if(blocks[row][col] != 0)
					return false;
		return true;
	}

	public Material getMatForHP(int hitPoints)
	{
		switch(hitPoints)
		{
			case 1:
				return Material.RED_CONCRETE;
			case 2:
				return Material.ORANGE_CONCRETE;
			case 3:
				return Material.YELLOW_CONCRETE;
			case 4:
				return Material.BLUE_CONCRETE;
			case 5:
				return Material.GREEN_CONCRETE;
			default:
				return Material.AIR;
		}
	}
}
