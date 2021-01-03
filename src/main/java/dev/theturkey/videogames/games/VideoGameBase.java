package dev.theturkey.videogames.games;

import dev.theturkey.videogames.util.Vector2I;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public abstract class VideoGameBase
{
	public static final int DIST_SCALE = 20;
	private Vector2I gameLoc;

	public VideoGameBase(Vector2I gameLoc)
	{
		this.gameLoc = gameLoc;
	}

	public Vector2I getGameLoc()
	{
		return gameLoc;
	}

	public Vector2I getGameLocScaled()
	{
		return new Vector2I(gameLoc.getX() * DIST_SCALE, gameLoc.getY() * DIST_SCALE);
	}

	public Location getPlayerLoc(World world)
	{
		Vector2I scaled = getGameLocScaled();
		return new Location(world, scaled.getX() + 0.5, getYBase() + 1, scaled.getY() + 0.5, 0, 0);
	}

	public abstract void constructGame(World world, Player player);

	public abstract void deconstructGame(World world, Player player);

	public abstract void startGame(World world, Player player);

	public abstract void endGame(World world, Player player);

	public abstract boolean isEntInGame(Entity entity);

	public abstract void onEntityCollide(Entity entity);

	public abstract void onPlayerJump();

	public abstract int getYBase();
}
