package dev.theturkey.videogames.games.brickbreaker;

import dev.theturkey.videogames.VGCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;

import java.util.ArrayList;
import java.util.List;

public class Paddle
{
	private static final float WIDTH = 1.1f;
	private List<Minecart> paddleEnts;

	public Paddle()
	{
	}

	public void setWidth(int ents, World world, double x, double y, double z)
	{
		paddleEnts = new ArrayList<>();
		for(int i = 0; i < ents; i++)
		{
			Location paddleLoc = new Location(world, (x - ((WIDTH * paddleEnts.size()) / 2)) + (WIDTH * i) + 0.5, y, z, 0, 0);
			Minecart paddle = (Minecart) world.spawnEntity(paddleLoc.clone().add(-10, -paddleLoc.getBlockY(), 0), EntityType.MINECART);
			Bukkit.getScheduler().scheduleSyncDelayedTask(VGCore.getPlugin(), () -> paddle.teleport(paddleLoc), 5);
			paddle.setGravity(false);

			//paddle.setDisplayBlockData(Material.SLIME_BLOCK.createBlockData());

			paddleEnts.add(paddle);
		}
		update(x);
	}

	public void update(double centerX)
	{
		for(int i = 0; i < paddleEnts.size(); i++)
		{
			Minecart paddle = paddleEnts.get(i);
			Location newLoc = paddle.getLocation().clone();
			newLoc.setX((centerX - ((WIDTH * paddleEnts.size()) / 2)) + (WIDTH * i) + 0.5);
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
}
