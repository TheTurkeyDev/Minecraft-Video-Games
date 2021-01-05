package dev.theturkey.videogames.games.brickbreaker;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;

import java.util.ArrayList;
import java.util.List;

public class Paddle
{
	private static final float WIDTH = 1.1f;
	private List<Minecart> paddleEnts;

	private double worldX;
	private double worldZ;

	public Paddle()
	{
	}

	public void setWidth(int ents, World world, double x, double y, double z)
	{
		this.worldX = x;
		this.worldZ = z;
		paddleEnts = new ArrayList<>();
		for(int i = 0; i < ents; i++)
		{
			Location paddleLoc = new Location(world, (worldX - (BrickBreakerGame.WIDTH / 2d) - ((WIDTH * i) - (ents / 2d))), y + BrickBreakerGame.PADDLE_Y, z, 0, 0);
			Minecart paddle = (Minecart) world.spawnEntity(paddleLoc, EntityType.MINECART);
			paddle.setGravity(false);

			paddleEnts.add(paddle);
		}
		update(this.worldX - (BrickBreakerGame.WIDTH / 2d));
	}

	public void update(double centerX)
	{
		centerX = worldX - centerX;
		for(int i = 0; i < paddleEnts.size(); i++)
		{
			Minecart paddle = paddleEnts.get(i);
			Location newLoc = paddle.getLocation().clone();
			newLoc.setX(centerX - ((WIDTH * i) - (getWidth() / 2)));
			newLoc.setZ(worldZ);
			paddle.teleport(newLoc);
		}
	}

	public void remove()
	{
		for(Minecart paddle : paddleEnts)
			paddle.remove();
	}

	public float getWidth()
	{
		return paddleEnts.size() * WIDTH;
	}

	public void setContainedBlock(Material mat)
	{
		for(Minecart paddle : paddleEnts)
			paddle.setDisplayBlockData(mat.createBlockData());
	}
}
