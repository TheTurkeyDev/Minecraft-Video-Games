package dev.theturkey.videogames.games.brickbreaker;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.theturkey.videogames.VGCore;
import dev.theturkey.videogames.games.GameManager;
import dev.theturkey.videogames.games.VideoGameBase;
import dev.theturkey.videogames.util.Hologram;
import dev.theturkey.videogames.util.Vector2I;
import dev.theturkey.videogames.util.Vector3I;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrickBreakerGame extends VideoGameBase
{
	public static int PADDLE_Y = 8;
	public static int WIDTH = 20;
	public static int HEIGHT = 40;
	private static final int Y_BASE = 75;
	private static final int DIST_FROM_PLAYER = 30;
	private boolean ballGrabbed;
	private Paddle paddle;
	private Ball ball;
	private List<PowerUp> fallingPowerUps = new ArrayList<>();
	private Map<PowerUpEnum, Integer> powerUpTimers = new HashMap<>();
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
		super(gameLoc, new Vector3I(gameLoc.getX(), Y_BASE - 15, gameLoc.getY()));
	}

	@Override
	public void constructGame(World world, Player player)
	{
		Vector3I worldLoc = getGameLocScaled();

		paddle = new Paddle();
		paddle.setWidth(4, world, worldLoc.getX() + getWidth(), worldLoc.getY(), worldLoc.getZ() + 0.5 + DIST_FROM_PLAYER);

		ballGrabbed = true;
		ball = new Ball(world, worldLoc.getX() + getWidth(), worldLoc.getY(), worldLoc.getZ() + 0.5 + DIST_FROM_PLAYER);

		Location playerLoc = getPlayerLoc(world);
		healthHologram = new Hologram(world, playerLoc.clone().add(3, -2, 5), ChatColor.RED + "LIVES: \u2665\u2665\u2665");
		levelHologram = new Hologram(world, playerLoc.clone().add(-3, -2, 5), ChatColor.RED + "Level: " + level);

		for(int x = 0; x < getWidth(); x++)
		{
			for(int yy = 0; yy < getHeight(); yy++)
			{
				world.getBlockAt(worldLoc.getX() + x, worldLoc.getY() + yy, worldLoc.getZ() + DIST_FROM_PLAYER + 1).setType(Material.BLACK_CONCRETE);
				if(x == 0 || x == getWidth() - 1 || yy == 0 || yy == getHeight() - 1)
					world.getBlockAt(worldLoc.getX() + x, worldLoc.getY() + yy, worldLoc.getZ() + DIST_FROM_PLAYER).setType(Material.WHITE_CONCRETE);
			}
		}
	}

	@Override
	public void startGame(World world, Player player)
	{
		super.startGame(world, player);
		lives = 3;
		level = 0;
		powerUpTimers.put(PowerUpEnum.STICKY_PADDLE, 0);
		nextLevel(world);

		player.sendRawMessage(ChatColor.GREEN + "Jump to start!");

		gameTick = Bukkit.getScheduler().scheduleSyncRepeatingTask(VGCore.getPlugin(), () ->
		{
			for(Map.Entry<PowerUpEnum, Integer> powerUpEntry : powerUpTimers.entrySet())
			{
				if(powerUpEntry.getValue() > 0)
				{
					if(powerUpEntry.getValue() == 1)
						removePowerUp(powerUpEntry.getKey());
					powerUpTimers.replace(powerUpEntry.getKey(), powerUpEntry.getValue() - 1);
				}
			}

			float yaw = player.getLocation().getYaw();
			double newX = Math.max(1, Math.min(getWidth() - 2, (DIST_FROM_PLAYER * Math.tan(Math.toRadians(yaw))) + (getWidth() / 2d)));
			paddle.update(newX);
			for(int i = fallingPowerUps.size() - 1; i >= 0; i--)
			{
				PowerUp powerUp = fallingPowerUps.get(i);
				if(powerUp.update(newX, paddle.getWidth()))
				{
					givePowerUp(powerUp.powerUpType);
					powerUp.remove();
					fallingPowerUps.remove(i);
					continue;
				}
				if(powerUp.getGameY() < 2)
				{
					powerUp.remove();
					fallingPowerUps.remove(i);
				}
			}
			if(ballGrabbed)
			{
				ball.setGameX(newX - 0.5);
				ball.updateLoc();
			}
			else
			{
				boolean bounced = ball.update(newX, paddle.getWidth());
				if(bounced && powerUpTimers.get(PowerUpEnum.STICKY_PADDLE) > 0)
				{
					ball.setVelocity(0, 0);
					ball.setGameY(PADDLE_Y + 0.75);
					ballGrabbed = true;
				}
				else if(ball.getGameY() < 2)
				{
					ball.setVelocity(0, 0);
					ball.setGameY(PADDLE_Y + 0.75);
					lives--;
					if(lives == 0)
					{
						player.sendRawMessage(ChatColor.RED + "You Lost!");
						player.sendRawMessage(ChatColor.RED + "You made it to level " + level + "!");
						GameManager.leaveGame(player);
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
		Vector3I gameLoc = getGameLocScaled();
		paddle.remove();
		ball.remove();
		healthHologram.remove();
		levelHologram.remove();
		for(PowerUp powerUp : fallingPowerUps)
			powerUp.remove();
		fallingPowerUps.clear();

		for(int x = 0; x < getWidth(); x++)
		{
			for(int yy = 0; yy < getHeight(); yy++)
			{
				world.getBlockAt(gameLoc.getX() + x, gameLoc.getY() + yy, gameLoc.getZ() + DIST_FROM_PLAYER + 1).setType(Material.AIR);
				world.getBlockAt(gameLoc.getX() + x, gameLoc.getY() + yy, gameLoc.getZ() + DIST_FROM_PLAYER).setType(Material.AIR);
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
		return Y_BASE;
	}

	public int getWidth()
	{
		return WIDTH;
	}

	public int getHeight()
	{
		return HEIGHT;
	}

	@Override
	public void onEntityCollide(Entity entity)
	{
		Vector3I gameLoc = getGameLocScaled();
		Location entLoc = entity.getLocation().clone().subtract(new Location(entity.getWorld(), gameLoc.getX(), gameLoc.getY(), gameLoc.getZ()));
		int col = ((entLoc.getBlockX() - 1) / 2);
		int row = (entLoc.getBlockY() - (getHeight() - 12)) / 2;
		if(row >= 0 && row < blocks.length && col >= 0 && col < blocks[row].length && blocks[row][col] != 0)
		{
			blocks[row][col]--;
			if(blocks[row][col] == 0)
			{
				spawnPowerUp(PowerUpEnum.STICKY_PADDLE, entity.getWorld(), entLoc.getX(), entLoc.getY());
			}
			if(isLevelComplete())
				nextLevel(entity.getWorld());
			else
				ball.bounceY();
			updateBlocks(entity.getWorld());
		}
	}

	public void spawnPowerUp(PowerUpEnum powerUpType, World world, double blockX, double blockY)
	{
		Vector3I gameLoc = getGameLocScaled();
		PowerUp powerUp = new PowerUp(powerUpType, world, gameLoc.getX() + getWidth() + 0.5, gameLoc.getY(), gameLoc.getZ() + DIST_FROM_PLAYER, blockX, blockY);
		fallingPowerUps.add(powerUp);
	}

	public void givePowerUp(PowerUpEnum powerUpType)
	{
		powerUpTimers.put(powerUpType, powerUpTimers.get(powerUpType) + 300);
	}


	public void removePowerUp(PowerUpEnum powerUpType)
	{

	}

	public void nextLevel(World world)
	{
		Reader levelsStream = new InputStreamReader(VGCore.getPlugin().getResource("brick_breaker_levels.json"), StandardCharsets.UTF_8);
		BufferedReader reader = new BufferedReader(levelsStream);
		JsonObject leveljson = VGCore.JSON_PARSER.parse(reader).getAsJsonArray().get(level).getAsJsonObject();
		blocks = new int[6][9];
		for(int row = 0; row < blocks.length; row++)
		{
			JsonArray rowArray = leveljson.getAsJsonArray("level_data").get(row).getAsJsonArray();
			for(int col = 0; col < blocks[row].length; col++)
				blocks[5 - row][8 - col] = rowArray.get(col).getAsInt();
		}

		updateBlocks(world);
		level++;
		ball.setVelocity(0, 0);
		ball.setGameY(PADDLE_Y + 0.75);
		ballGrabbed = true;
		levelHologram.setText(ChatColor.RED + "Level: " + level);
	}

	public void updateBlocks(World world)
	{
		int blockBottom = getHeight() - 12;
		Vector3I gameLoc = getGameLocScaled();
		for(int x = 1; x < getWidth(); x++)
		{
			for(int yy = blockBottom; yy < getHeight() - 2; yy++)
			{
				int colIndex = (x - 1) / 2;
				int rowIndex = (yy - blockBottom) / 2;
				if(yy % 2 == 0 && colIndex < blocks[rowIndex].length)
				{
					int hitPoints = blocks[rowIndex][colIndex];
					world.getBlockAt(gameLoc.getX() + x, gameLoc.getY() + yy, gameLoc.getZ() + DIST_FROM_PLAYER).setType(getMatForHP(hitPoints));
				}
			}
		}
	}

	public boolean isLevelComplete()
	{
		for(int row = 0; row < blocks.length; row++)
			for(int col = 0; col < blocks[row].length; col++)
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
