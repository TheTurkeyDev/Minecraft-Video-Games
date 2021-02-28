package dev.theturkey.mcarcade.games.brickbreaker;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;

import java.util.ArrayList;
import java.util.List;

public class Paddle
{
	private static final float WIDTH = 1.1f;
	private List<Minecart> paddleEnts = new ArrayList<>();

	private double worldX;
	private double worldZ;

	public Paddle(World world, double x, double y, double z)
	{
		this.worldX = x;
		this.worldZ = z;
		setWidth(world, y + BrickBreakerGame.PADDLE_Y, 4, Material.AIR.createBlockData());
	}

	private void setWidth(World world, double y, int width, BlockData containedBlock)
	{
		for(Minecart cart : paddleEnts)
			cart.remove();
		paddleEnts = new ArrayList<>();
		for(int i = 0; i < width; i++)
		{
			Location paddleLoc = new Location(world, (worldX - (BrickBreakerGame.WIDTH / 2d) - ((WIDTH * i) - (width / 2d))), y, this.worldZ, 0, 0);
			Minecart paddle = (Minecart) world.spawnEntity(paddleLoc, EntityType.MINECART);
			paddle.setGravity(false);
			paddle.setDisplayBlockData(containedBlock);

			paddleEnts.add(paddle);
		}
		update(this.worldX - (BrickBreakerGame.WIDTH / 2d));
	}

	public void shrink()
	{

		if(paddleEnts.size() > 2)
		{
			Minecart minecart = paddleEnts.get(0);
			setWidth(minecart.getWorld(), minecart.getLocation().getY(), paddleEnts.size() - 2, minecart.getDisplayBlockData());
		}
	}

	public void grow()
	{
		if(paddleEnts.size() < 6)
		{
			Minecart minecart = paddleEnts.get(0);
			setWidth(minecart.getWorld(), minecart.getLocation().getY(), paddleEnts.size() + 2, minecart.getDisplayBlockData());
		}
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
			newLoc.setYaw(0);
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
