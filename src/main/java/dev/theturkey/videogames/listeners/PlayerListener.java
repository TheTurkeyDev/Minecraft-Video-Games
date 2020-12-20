package dev.theturkey.videogames.listeners;

import dev.theturkey.videogames.VGCore;
import dev.theturkey.videogames.games.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerListener implements Listener
{
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e)
	{
		World world = e.getPlayer().getWorld();
		GameManager.SPAWN.setWorld(world);

		for(int x = -1; x < 2; x++)
			for(int z = -1; z < 2; z++)
				world.getBlockAt(new Location(world, x, 254, z)).setType(Material.BEDROCK);

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(VGCore.getPlugin(), () ->
		{
			e.getPlayer().teleport(GameManager.SPAWN, PlayerTeleportEvent.TeleportCause.COMMAND);
			e.getPlayer().setWalkSpeed(0);
		}, 1);
	}

	@EventHandler
	public void onPlayerLogout(PlayerQuitEvent e)
	{
		GameManager.leaveGame(e.getPlayer());
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e)
	{
		if(e.getTo() != null && (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getY() != e.getTo().getY() || e.getFrom().getZ() != e.getTo().getZ()))
		{
			e.getPlayer().setWalkSpeed(0);
			e.setCancelled(true);
		}
	}
}
