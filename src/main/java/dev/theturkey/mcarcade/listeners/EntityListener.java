package dev.theturkey.mcarcade.listeners;

import dev.theturkey.mcarcade.games.GameManager;
import dev.theturkey.mcarcade.games.VideoGameBase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

public class EntityListener implements Listener
{

	@EventHandler
	public void onEnterVehicle(VehicleEnterEvent e)
	{
		e.setCancelled(true);
	}

	@EventHandler
	public void onEntityCollideWithBlock(EntityDamageEvent e)
	{
		if(!e.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION))
			return;

		VideoGameBase vgb = GameManager.getGameForEntity(e.getEntity());
		if(vgb == null)
			return;
		vgb.onEntityCollide(e.getEntity());
		e.setCancelled(true);
	}

}
