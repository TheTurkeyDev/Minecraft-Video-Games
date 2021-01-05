package dev.theturkey.videogames.games.brickbreaker;

import dev.theturkey.videogames.util.Vector2D;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Silverfish;

public class PowerUp
{
	public Silverfish powerUpEnt;
	public PowerUpEnum powerUpType;

	private Vector2D powerUpWorldOffset;
	private Vector2D powerUpLoc;

	public PowerUp(PowerUpEnum powerUpType, World world, double x, double y, double z, double gameX, double gameY)
	{
		this.powerUpType = powerUpType;
		powerUpWorldOffset = new Vector2D(x, y);
		powerUpLoc = new Vector2D((BrickBreakerGame.WIDTH - gameX) + 0.5, gameY);
		Location startLoc = new Location(world, getWorldX(), getWorldY(), z, 90, 0);
		powerUpEnt = (Silverfish) world.spawnEntity(startLoc, EntityType.SILVERFISH);
		powerUpEnt.setAI(false);
		powerUpEnt.setInvulnerable(true);
		powerUpEnt.setGravity(false);
	}

	public boolean update(double paddleX, float paddleWidth)
	{
		powerUpLoc.add(0, -.1);
		updateLoc();

		System.out.println(getGameX() + "  " + (paddleX - (paddleWidth / 2)));
		if(getGameY() < BrickBreakerGame.PADDLE_Y && getGameY() > BrickBreakerGame.PADDLE_Y - 1.5)
		{
			double xOff = (getGameX() - 0.5) - (paddleX - (paddleWidth / 2));
			return xOff > 0 && xOff < paddleWidth;
		}
		return false;
	}

	public void updateLoc()
	{
		Location loc = powerUpEnt.getLocation().clone();
		loc.setX(getWorldX());
		loc.setY(getWorldY());
		powerUpEnt.teleport(loc);
	}

	public double getWorldX()
	{
		return powerUpWorldOffset.getX() - getGameX();
	}

	public double getWorldY()
	{
		return powerUpWorldOffset.getY() + getGameY();
	}

	public double getGameY()
	{
		return powerUpLoc.getY();
	}

	public double getGameX()
	{
		return powerUpLoc.getX();
	}

	public void remove()
	{
		powerUpEnt.remove();
	}
}
