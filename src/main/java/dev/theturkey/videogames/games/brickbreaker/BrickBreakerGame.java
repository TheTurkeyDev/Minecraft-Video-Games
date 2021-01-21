package dev.theturkey.videogames.games.brickbreaker;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.theturkey.videogames.VGCore;
import dev.theturkey.videogames.games.GameManager;
import dev.theturkey.videogames.games.VideoGameBase;
import dev.theturkey.videogames.games.VideoGamesEnum;
import dev.theturkey.videogames.leaderboard.LeaderBoardManager;
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

	private Paddle paddle;
	private List<Ball> balls = new ArrayList<>();
	private List<PowerUp> fallingPowerUps = new ArrayList<>();
	private Map<PowerUpEnum, Integer> powerUpTimers = new HashMap<>();
	private Hologram healthHologram;
	private Hologram levelHologram;
	private Hologram scoreHologram;
	private int gameTick = -1;
	private int lives = 3;
	private int level = 0;
	private int score = 0;

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
	public void constructGame(Player player)
	{
		Vector3I worldLoc = getGameLocScaled();

		paddle = new Paddle(VGCore.gameWorld, worldLoc.getX() + getWidth(), worldLoc.getY(), worldLoc.getZ() + 0.5 + DIST_FROM_PLAYER);
		balls.add(new Ball(VGCore.gameWorld, worldLoc.getX() + getWidth(), worldLoc.getY(), worldLoc.getZ() + 0.5 + DIST_FROM_PLAYER));

		Location playerLoc = getPlayerLoc();
		healthHologram = new Hologram(playerLoc.clone().add(3, -2, 5), ChatColor.RED + "LIVES: \u2665\u2665\u2665");
		levelHologram = new Hologram(playerLoc.clone().add(-3, -2, 5), ChatColor.RED + "Level: " + level);
		scoreHologram = new Hologram(playerLoc.clone().add(-3, -1.5, 5), ChatColor.RED + "Score: " + score);

		for(int x = 0; x < getWidth(); x++)
		{
			for(int yy = 0; yy < getHeight(); yy++)
			{
				VGCore.gameWorld.getBlockAt(worldLoc.getX() + x, worldLoc.getY() + yy, worldLoc.getZ() + DIST_FROM_PLAYER + 1).setType(Material.BLACK_CONCRETE);
				if(x == 0 || x == getWidth() - 1 || yy == 0 || yy == getHeight() - 1)
					VGCore.gameWorld.getBlockAt(worldLoc.getX() + x, worldLoc.getY() + yy, worldLoc.getZ() + DIST_FROM_PLAYER).setType(Material.WHITE_CONCRETE);
			}
		}
	}

	@Override
	public void startGame(Player player)
	{
		super.startGame(player);
		lives = 3;
		level = 0;
		score = 0;
		powerUpTimers.put(PowerUpEnum.STICKY_PADDLE, 0);
		powerUpTimers.put(PowerUpEnum.MULTI_BALL, 0);
		powerUpTimers.put(PowerUpEnum.PADDLE_GROW, 0);
		powerUpTimers.put(PowerUpEnum.PADDLE_SHRINK, 0);
		nextLevel(VGCore.gameWorld);

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
			for(int i = balls.size() - 1; i >= 0; i--)
			{
				Ball ball = balls.get(i);
				if(ball.isBallGrabbed())
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
						ball.setBallGrabbed(true);
					}
					else if(ball.getGameY() < 2)
					{
						if(balls.size() > 1)
						{
							ball.remove();
							balls.remove(i);
						}
						else
						{
							ball.setVelocity(0, 0);
							ball.setGameY(PADDLE_Y + 0.75);
							lives--;
							if(lives == 0)
							{
								player.sendRawMessage(ChatColor.RED + "You Lost!");
								player.sendRawMessage(ChatColor.RED + "You made it to level " + level + "!");
								player.sendRawMessage(ChatColor.RED + "Score: " + score);
								Thread t = new Thread(() -> LeaderBoardManager.addScore(player, score, getLeaderBoardKey()));
								t.start();

								GameManager.leaveGame(player);
							}
							else
							{
								StringBuilder builder = new StringBuilder(ChatColor.RED.toString()).append("LIVES: ");
								for(int j = 0; j < lives; j++)
									builder.append("\u2665");
								healthHologram.setText(builder.toString());
								ball.setBallGrabbed(true);
							}
						}
					}
				}
			}
		}, 0, 1);
	}

	@Override
	public void endGame(Player player)
	{
		super.endGame(player);
		Bukkit.getScheduler().cancelTask(gameTick);
	}

	@Override
	public void deconstructGame(Player player)
	{
		Vector3I gameLoc = getGameLocScaled();
		paddle.remove();
		for(Ball ball : balls)
			ball.remove();
		balls.clear();
		healthHologram.remove();
		levelHologram.remove();
		scoreHologram.remove();
		for(PowerUp powerUp : fallingPowerUps)
			powerUp.remove();
		fallingPowerUps.clear();

		for(int x = 0; x < getWidth(); x++)
		{
			for(int yy = 0; yy < getHeight(); yy++)
			{
				VGCore.gameWorld.getBlockAt(gameLoc.getX() + x, gameLoc.getY() + yy, gameLoc.getZ() + DIST_FROM_PLAYER + 1).setType(Material.AIR);
				VGCore.gameWorld.getBlockAt(gameLoc.getX() + x, gameLoc.getY() + yy, gameLoc.getZ() + DIST_FROM_PLAYER).setType(Material.AIR);
			}
		}
	}

	@Override
	public boolean isEntInGame(Entity entity)
	{
		for(Ball ball : balls)
			if(entity.equals(ball.getEntity()))
				return true;
		return false;
	}

	@Override
	public void onPlayerJump()
	{
		for(Ball ball : balls)
		{
			if(ball.isBallGrabbed())
			{
				ball.setVelocity(0, 0.5);
				ball.setBallGrabbed(false);
			}
		}
	}

	@Override
	public void playerLeftClick(Player player)
	{

	}

	@Override
	public void playerRightClick(Player player)
	{

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
		int row = (entLoc.getBlockY() - (getHeight() - 14)) / 2;
		if(row >= 0 && row < blocks.length && col >= 0 && col < blocks[row].length && blocks[row][col] != 0)
		{
			blocks[row][col]--;
			givePoints(5);
			if(blocks[row][col] == 0)
			{
				givePoints(10);
				if(VGCore.RAND.nextInt(10) == 4)
					spawnPowerUp(PowerUpEnum.values()[VGCore.RAND.nextInt(PowerUpEnum.values().length)], entity.getWorld(), entLoc.getX(), entLoc.getY());
			}

			if(isLevelComplete())
			{
				givePoints(50);
				nextLevel(entity.getWorld());
			}
			else
			{
				Ball ball = null;

				for(Ball b : balls)
				{
					if(b.getEntity().equals(entity))
					{
						ball = b;
						break;
					}
				}

				if(ball != null)
					ball.bounceY();
			}
			updateBlocks(entity.getWorld());
		}
	}

	public void givePoints(int points)
	{
		this.score += points;
		scoreHologram.setText(ChatColor.RED + "Score: " + score);
	}

	public void spawnPowerUp(PowerUpEnum powerUpType, World world, double blockX, double blockY)
	{
		Vector3I gameLoc = getGameLocScaled();
		PowerUp powerUp = new PowerUp(powerUpType, world, gameLoc.getX() + getWidth() + 0.5, gameLoc.getY(), gameLoc.getZ() + DIST_FROM_PLAYER, blockX, blockY);
		fallingPowerUps.add(powerUp);
	}

	public void givePowerUp(PowerUpEnum powerUpType)
	{
		givePoints(15);
		powerUpTimers.put(powerUpType, powerUpTimers.get(powerUpType) + 300);

		switch(powerUpType)
		{
			case STICKY_PADDLE:
				paddle.setContainedBlock(Material.SLIME_BLOCK);
				break;
			case MULTI_BALL:
				balls.add(new Ball(balls.get(0), getGameLocScaled().getZ() + 0.5 + DIST_FROM_PLAYER));
				balls.add(new Ball(balls.get(0), getGameLocScaled().getZ() + 0.5 + DIST_FROM_PLAYER));
				break;
			case PADDLE_SHRINK:
				paddle.shrink();
				break;
			case PADDLE_GROW:
				paddle.grow();
				break;
		}
	}


	public void removePowerUp(PowerUpEnum powerUpType)
	{
		switch(powerUpType)
		{

			case STICKY_PADDLE:
				paddle.setContainedBlock(Material.AIR);
				break;
			case MULTI_BALL:
				break;
			case PADDLE_SHRINK:
				paddle.grow();
				break;
			case PADDLE_GROW:
				paddle.shrink();
				break;
		}
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
		while(balls.size() > 1)
			balls.remove(balls.size() - 1).remove();
		Ball ball = balls.get(0);
		ball.setVelocity(0, 0);
		ball.setGameY(PADDLE_Y + 0.75);
		ball.setBallGrabbed(true);
		levelHologram.setText(ChatColor.RED + "Level: " + level);
	}

	public void updateBlocks(World world)
	{
		int blockBottom = getHeight() - 14;
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
		for(int[] block : blocks)
			for(int i : block)
				if(i != 0)
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

	@Override
	public VideoGamesEnum getGameType()
	{
		return VideoGamesEnum.BRICK_BREAKER;
	}

	public static final String LEADER_BOARD_ID = "mcvg_" + VideoGamesEnum.BRICK_BREAKER.name().toLowerCase();

	@Override
	public String getLeaderBoardKey()
	{
		return LEADER_BOARD_ID;
	}
}
