package dev.theturkey.videogames.games.brickbreaker;

import dev.theturkey.videogames.VGCore;
import dev.theturkey.videogames.games.VideoGameBase;
import dev.theturkey.videogames.util.Vector2I;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BrickBreakerGame extends VideoGameBase
{
	private static final int DIST_FROM_PLAYER = 30;
	private static final int Y = 75;
	private Paddle paddle;
	private Ball ball;
	private int gameTick = -1;

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
		world.getBlockAt(new Location(world, gameLoc.getX(), Y, gameLoc.getY())).setType(Material.BEDROCK);


		player.teleport(new Location(world, gameLoc.getX() + 0.5, Y + 1, gameLoc.getY() + 0.5, 0, 0), PlayerTeleportEvent.TeleportCause.COMMAND);

		paddle = new Paddle();
		paddle.setWidth(4, world, gameLoc.getX(), Y - 4, gameLoc.getY() + 0.5 + DIST_FROM_PLAYER);

		ball = new Ball(world, gameLoc.getX() + 0.5, Y - 4, gameLoc.getY() + 0.5 + DIST_FROM_PLAYER);

		for(int x = -10; x < 10; x++)
		{
			for(int yy = -15; yy < 25; yy++)
			{
				world.getBlockAt(gameLoc.getX() + x, Y + yy, gameLoc.getY() + DIST_FROM_PLAYER + 1).setType(Material.BLACK_CONCRETE);
				if(x == -10 || x == 9 || yy == -15 || yy == 24)
					world.getBlockAt(gameLoc.getX() + x, Y + yy, gameLoc.getY() + DIST_FROM_PLAYER).setType(Material.WHITE_CONCRETE);
			}
		}
		updateBlocks(world);
	}

	public void startGame(World world, Player player)
	{
		gameTick = Bukkit.getScheduler().scheduleSyncRepeatingTask(VGCore.getPlugin(), () ->
		{
			float yaw = player.getLocation().getYaw();
			double newX = Math.max(-9, Math.min(9, (DIST_FROM_PLAYER * Math.tan(Math.toRadians(yaw))) - 0.5));
			paddle.update(getGameLocScaled().getX() - newX);
			ball.update(newX, paddle.getWidth());
		}, 0, 1);
	}

	public void endGame(World world, Player player)
	{
		Bukkit.getScheduler().cancelTask(gameTick);
	}

	@Override
	public void deconstructGame(World world, Player player)
	{
		Vector2I gameLoc = getGameLocScaled();
		world.getBlockAt(new Location(world, gameLoc.getX(), 49, gameLoc.getY())).setType(Material.AIR);
		paddle.remove();
		ball.remove();

		for(int x = -10; x < 10; x++)
		{
			for(int yy = -15; yy < 25; yy++)
			{
				world.getBlockAt(gameLoc.getX() + x, Y + yy, gameLoc.getY() + DIST_FROM_PLAYER + 1).setType(Material.AIR);
				world.getBlockAt(gameLoc.getX() + x, Y + yy, gameLoc.getY() + DIST_FROM_PLAYER).setType(Material.AIR);
			}
		}
	}

	@Override
	public boolean isEntInGame(Entity entity)
	{
		return entity.equals(ball.getEntity());
	}

	@Override
	public void onEntityCollide(Entity entity)
	{
		Vector2I gameLoc = getGameLocScaled();
		Location entLoc = entity.getLocation().clone().subtract(new Location(entity.getWorld(), gameLoc.getX(), Y, gameLoc.getY()));
		int col = (entLoc.getBlockX() + 9) / 2;
		int row = (entLoc.getBlockY() - 12) / 2;
		if(row < blocks.length && col < blocks[row].length)
		{
			blocks[row][col]--;
			updateBlocks(entity.getWorld());
			ball.bounceY();
		}
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
					world.getBlockAt(gameLoc.getX() + x, Y + yy, gameLoc.getY() + DIST_FROM_PLAYER).setType(getMatForHP(hitPoints));
				}
			}
		}
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
