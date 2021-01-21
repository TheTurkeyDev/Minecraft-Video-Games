package dev.theturkey.videogames.util;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import dev.theturkey.videogames.packetwrappers.WrapperPlayServerEntityEquipment;
import dev.theturkey.videogames.packetwrappers.WrapperPlayServerEntityMetadata;
import dev.theturkey.videogames.packetwrappers.WrapperPlayServerSpawnEntity;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;

public class FakeHologram
{
	private int entId;
	private Location location;
	private String text = "";
	private boolean glowing;
	private ItemStack headItem;

	public FakeHologram(int entId, Location location)
	{
		this.entId = entId;
		this.location = location;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public void setGlowing(boolean glowing)
	{
		this.glowing = glowing;
	}

	public void setHeadItem(ItemStack headItem)
	{
		this.headItem = headItem;
	}

	public void spawn(Player player)
	{
		WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity();

		packet.setX(location.getX());
		packet.setY(location.getY());
		packet.setZ(location.getZ());
		packet.setUniqueId(UUID.randomUUID());
		packet.setType(EntityType.ARMOR_STAND);
		packet.setEntityID(entId);
		packet.setPitch(0);
		packet.setYaw(180);
		packet.sendPacket(player);

		sendMetaDataPacket(player);
	}

	public void sendMetaDataPacket(Player player)
	{
		WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
		dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) (0x20 + (glowing ? 0x40 : 0))); // Invisible and possibly Glowing
		if(!text.isEmpty())
		{
			dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)), Optional.of(WrappedChatComponent.fromChatMessage(text)[0].getHandle()));
			dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)), true); // Custom Name Visible
		}
		dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(5, WrappedDataWatcher.Registry.get(Boolean.class)), true); // No Gravity
		dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(14, WrappedDataWatcher.Registry.get(Byte.class)), (byte) (0x08 + 0x10)); // No Base plate & marker

		WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata();
		metadataPacket.setMetadata(dataWatcher.getWatchableObjects());
		metadataPacket.setEntityID(entId);
		metadataPacket.sendPacket(player);
	}

	public void sendArmorStandHeadPacket(Player player)
	{
		WrapperPlayServerEntityEquipment equipmentPacket = new WrapperPlayServerEntityEquipment();
		equipmentPacket.setEntityID(entId);
		equipmentPacket.setSlotStackPair(EnumWrappers.ItemSlot.HEAD, headItem);
		equipmentPacket.sendPacket(player);
	}

}
