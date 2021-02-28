package dev.theturkey.mcarcade.games.brickbreaker;

import dev.theturkey.mcarcade.util.Vector2D;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class PowerUp
{
	public ArmorStand powerUpEnt;
	public PowerUpEnum powerUpType;

	private Vector2D powerUpWorldOffset;
	private Vector2D powerUpLoc;

	public PowerUp(PowerUpEnum powerUpType, World world, double x, double y, double z, double gameX, double gameY)
	{
		this.powerUpType = powerUpType;
		powerUpWorldOffset = new Vector2D(x, y);
		powerUpLoc = new Vector2D((BrickBreakerGame.WIDTH - gameX) + 0.5, gameY);
		Location startLoc = new Location(world, getWorldX(), getWorldY() - 0.5f, z, 0, 0);
		powerUpEnt = (ArmorStand) world.spawnEntity(startLoc, EntityType.ARMOR_STAND);
		powerUpEnt.setAI(false);
		powerUpEnt.getEquipment().setHelmet(new ItemStack(powerUpType.getPowerUpMat()));
		powerUpEnt.setInvulnerable(true);
		powerUpEnt.setInvisible(true);
		powerUpEnt.setGravity(false);
	}

	public boolean update(double paddleX, float paddleWidth)
	{
		powerUpLoc.add(0, -.1);
		updateLoc();

		if(getGameY() < BrickBreakerGame.PADDLE_Y && getGameY() > BrickBreakerGame.PADDLE_Y - 1.5)
		{
			double xOff = (getGameX() + 0.5) - (paddleX - (paddleWidth / 2));
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
