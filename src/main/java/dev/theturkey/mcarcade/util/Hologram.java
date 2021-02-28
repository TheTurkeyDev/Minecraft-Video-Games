package dev.theturkey.mcarcade.util;

import dev.theturkey.mcarcade.MCACore;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;

public class Hologram
{
	private ArmorStand armorStand;

	public Hologram(Location location, String text)
	{
		armorStand = (ArmorStand) MCACore.gameWorld.spawnEntity(location, EntityType.ARMOR_STAND);
		armorStand.setVisible(false);
		armorStand.setCustomName(text);
		armorStand.setCustomNameVisible(!text.isEmpty());
		armorStand.setGravity(false);
	}

	public Hologram(ArmorStand armorStand)
	{
		this.armorStand = armorStand;
	}

	public void setKey(String key)
	{
		armorStand.setMetadata("hologram-key", new FixedMetadataValue(MCACore.getPlugin(), key));
	}

	public void setText(String text)
	{
		armorStand.setCustomName(text);
		armorStand.setCustomNameVisible(!text.isEmpty());
	}

	public void remove()
	{
		armorStand.remove();
	}

}
