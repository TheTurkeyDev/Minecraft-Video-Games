package dev.theturkey.videogames.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class Hologram
{
	private ArmorStand armorStand;

	public Hologram(World world, Location location, String text)
	{
		armorStand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
		armorStand.setVisible(false);
		armorStand.setCustomName(text);
		armorStand.setCustomNameVisible(true);
		armorStand.setGravity(false);
	}

	public void setText(String text)
	{
		armorStand.setCustomName(text);
	}

	public void remove()
	{
		armorStand.remove();
	}

}
