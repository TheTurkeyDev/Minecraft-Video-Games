package dev.theturkey.videogames.games.brickbreaker;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Paddle
{
	private static final float WIDTH = 0.9f;
	private List<ArmorStand> paddleEnts;

	public Paddle()
	{
	}

	public void setWidth(int ents, World world, double x, double y, double z)
	{
		paddleEnts = new ArrayList<>();
		for(int i = 0; i < ents; i++)
		{
			Location paddleLoc = new Location(world, x - ((WIDTH * paddleEnts.size()) / 2) + 0.5 + (WIDTH * i), y, z, 0, 0);
			ArmorStand paddle = (ArmorStand) world.spawnEntity(paddleLoc, EntityType.ARMOR_STAND);
			paddle.getEquipment().setHelmet(new ItemStack(Material.STONE_SLAB));
			paddle.setGravity(false);
			paddle.setVisible(false);
			paddle.setMarker(true);

			paddleEnts.add(paddle);
		}
		update(x);
	}

	public void update(double centerX)
	{
		for(int i = 0; i < paddleEnts.size(); i++)
		{
			ArmorStand paddle = paddleEnts.get(i);
			Location newLoc = paddle.getLocation().clone();
			newLoc.setX(centerX - ((WIDTH * paddleEnts.size()) / 2) + 0.5 + (WIDTH * i));
			paddle.teleport(newLoc);
		}
	}

	public void remove()
	{
		for(ArmorStand paddle : paddleEnts)
			paddle.remove();
	}
}
