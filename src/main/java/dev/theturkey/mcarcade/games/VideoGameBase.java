package dev.theturkey.mcarcade.games;

import dev.theturkey.mcarcade.MCACore;
import dev.theturkey.mcarcade.util.Vector2I;
import dev.theturkey.mcarcade.util.Vector3I;
import org.bukkit.Location;
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

	public Location getPlayerLoc()
	{
		Vector3I scaled = getGameLocScaled();
		return new Location(MCACore.gameWorld, scaled.getX() + (getWidth() / 2d), getYBase() + 1, scaled.getZ() + 0.5, 0, 0);
	}

	public abstract void constructGame(Player player);

	public abstract void deconstructGame(Player player);

	public void startGame(Player player)
	{
		player.setInvisible(false);
	}

	public void endGame(Player player)
	{
		GameManager.sendPlayerToSpawn(player);
	}

	public abstract boolean isEntInGame(Entity entity);

	public abstract void onEntityCollide(Entity entity);

	public abstract void onKeyPress(Player player, int key);

	public abstract void playerLeftClick(Player player);

	public abstract void playerRightClick(Player player);

	public abstract int getYBase();

	public abstract int getWidth();

	public abstract int getHeight();

	public abstract VideoGamesEnum getGameType();

	public abstract String getLeaderBoardKey();
}
