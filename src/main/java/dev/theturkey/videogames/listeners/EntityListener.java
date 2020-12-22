package dev.theturkey.videogames.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;

public class EntityListener implements Listener
{

	@EventHandler
	public void onEnterVehicle(VehicleEnterEvent e)
	{
		e.setCancelled(true);
	}

}
