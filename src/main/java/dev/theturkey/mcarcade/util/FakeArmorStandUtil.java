package dev.theturkey.mcarcade.util;

import dev.theturkey.mcarcade.packetwrappers.WrapperPlayServerEntityDestroy;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class FakeArmorStandUtil
{

	public static void send(Player player, FakeHologram... holograms)
	{
		send(player, Arrays.asList(holograms));
	}

	public static void send(Player player, List<FakeHologram> holograms)
	{
		//Thread t = new Thread(() ->
		//{
		for(FakeHologram hologram : holograms)
			hologram.spawn(player);
		//});
		//t.start();
	}


	public static void updateMeta(Player player, FakeHologram... holograms)
	{
		updateMeta(player, Arrays.asList(holograms));
	}

	public static void updateMeta(Player player, List<FakeHologram> holograms)
	{
		//Thread t = new Thread(() ->
		//{
		for(FakeHologram hologram : holograms)
			hologram.sendMetaDataPacket(player);
		//});
		//t.start();
	}

	public static void updateArmor(Player player, FakeHologram... holograms)
	{
		updateArmor(player, Arrays.asList(holograms));
	}

	public static void updateArmor(Player player, List<FakeHologram> holograms)
	{
		//Thread t = new Thread(() ->
		//{
		for(FakeHologram hologram : holograms)
			hologram.sendArmorStandHeadPacket(player);
		//});
		//t.start();
	}

	public static void removeArmorStands(Player player, int... ents)
	{
		WrapperPlayServerEntityDestroy destroyPacket = new WrapperPlayServerEntityDestroy();
		destroyPacket.setEntityIds(ents);
		destroyPacket.sendPacket(player);
	}
}
