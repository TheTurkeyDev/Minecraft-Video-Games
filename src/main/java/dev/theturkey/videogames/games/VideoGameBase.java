package dev.theturkey.videogames.games;

import dev.theturkey.videogames.util.Vector2I;
import dev.theturkey.videogames.util.Vector3I;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public abstract class VideoGameBase
{
	public static final int DIST_SCALE = 20;
	private Vector3I gameLoc;
	private Vector2I gameXZLoc;

	public VideoGameBase(Vector2I gameXZLoc, Vector3I gameLoc)
	{
		this.gameXZLoc = gameXZLoc;
		this.gameLoc = gameLoc;
	}

	public Vector2I getGameLoc()
	{
		return gameXZLoc;
	}

	public Vector3I getGameLocScaled()
	{
		return new Vector3I(gameLoc.getX() * DIST_SCALE, gameLoc.getY(), gameLoc.getZ() * DIST_SCALE);
	}

	public Location getPlayerLoc(World world)
	{
		Vector3I scaled = getGameLocScaled();
		return new Location(world, scaled.getX() + (getWidth() / 2d), getYBase() + 1, scaled.getZ() + 0.5, 0, 0);
	}

	public abstract void constructGame(World world, Player player);

	public abstract void deconstructGame(World world, Player player);

	public void startGame(World world, Player player)
	{
		player.setInvisible(false);
	}

	public void endGame(World world, Player player)
	{
		GameManager.sendPlayerToSpawn(player);
	}

	public abstract boolean isEntInGame(Entity entity);

	public abstract void onEntityCollide(Entity entity);

	public abstract void onPlayerJump();

	public abstract void playerLeftClick(Player player);

	public abstract void playerRightClick(Player player);

	public abstract int getYBase();

	public abstract int getWidth();

	public abstract int getHeight();
}
