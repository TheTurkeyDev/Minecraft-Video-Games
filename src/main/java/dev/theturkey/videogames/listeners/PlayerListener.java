package dev.theturkey.videogames.listeners;

import dev.theturkey.videogames.VGCore;
import dev.theturkey.videogames.games.GameManager;
import dev.theturkey.videogames.games.VideoGameBase;
import dev.theturkey.videogames.leaderboard.LeaderBoardManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener
{
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e)
	{
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(VGCore.getPlugin(), () ->
		{
			if(!e.getPlayer().getWorld().getName().equalsIgnoreCase(VGCore.gameWorld.getName()))
				return;

			for(int x = -1; x < 2; x++)
				for(int z = -1; z < 2; z++)
					VGCore.gameWorld.getBlockAt(new Location(VGCore.gameWorld, x, 254, z)).setType(Material.BEDROCK);

			LeaderBoardManager.showLeaderBoards(e.getPlayer());


			e.getPlayer().setWalkSpeed(0);
			GameManager.sendPlayerToSpawn(e.getPlayer());
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
		VideoGameBase game = GameManager.getGameForPlayer(e.getPlayer());
		if(game != null && e.getTo() != null && e.getFrom().getY() < e.getTo().getY())
		{
			game.onPlayerJump();
		}

		if(e.getPlayer().getGameMode().equals(GameMode.SURVIVAL) && e.getTo() != null && (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getY() != e.getTo().getY() || e.getFrom().getZ() != e.getTo().getZ()))
		{
			e.getPlayer().setWalkSpeed(0);
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e)
	{
		if(e.getEntity() instanceof Player)
		{
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDamage(PlayerInteractEvent e)
	{
		VideoGameBase game = GameManager.getGameForPlayer(e.getPlayer());
		if(game == null)
			return;
		if(e.getAction().equals(Action.LEFT_CLICK_AIR))
		{
			game.playerLeftClick(e.getPlayer());
		}
		else if(e.getAction().equals(Action.RIGHT_CLICK_AIR))
		{
			game.playerRightClick(e.getPlayer());
		}
	}
}
