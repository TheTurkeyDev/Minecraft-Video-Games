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
import org.bukkit.event.player.PlayerToggleSprintEvent;

import java.awt.event.KeyEvent;

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


			e.getPlayer().setWalkSpeed(.05f);
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
		boolean moved = e.getPlayer().getGameMode().equals(GameMode.SURVIVAL) && e.getTo() != null && (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getY() != e.getTo().getY() || e.getFrom().getZ() != e.getTo().getZ());
		VideoGameBase game = GameManager.getGameForPlayer(e.getPlayer());
		if(game != null)
		{
			if(moved)
			{
				double yAmount = e.getFrom().getY() - e.getTo().getY();

				double xAmount = e.getFrom().getX() - e.getTo().getX();
				double zAmount = e.getFrom().getZ() - e.getTo().getZ();

				double moveTheta = Math.atan2(zAmount, xAmount) + (Math.PI / 2f);
				if(moveTheta > Math.PI)
					moveTheta = (-2 * Math.PI) + moveTheta;
				else if(moveTheta < -Math.PI)
					moveTheta = (2 * Math.PI) + moveTheta;

				double playerYaw = e.getPlayer().getLocation().getYaw();
				if(playerYaw > 180)
					playerYaw = -360 + playerYaw;
				if(playerYaw < -180)
					playerYaw = 360 + playerYaw;

				double playerTheta = Math.toRadians(playerYaw);
				if(moveTheta < 0 && playerTheta > 0)
				{
					moveTheta += 2 * Math.PI;
					if(moveTheta > Math.PI)
						moveTheta = (-2 * Math.PI) + moveTheta;
				}
				else if(playerTheta < 0 && moveTheta > 0)
				{
					playerTheta += 2 * Math.PI;
					if(playerTheta > Math.PI)
						playerTheta = (-2 * Math.PI) + playerTheta;
				}

				double theta = moveTheta - playerTheta;
				//This if is to eliminate modulus on PI and -PI
				if(theta > Math.PI + 0.25 || theta < -Math.PI - 0.25)
					theta %= Math.PI;

				if(yAmount < 0)
					game.onKeyPress(e.getPlayer(), KeyEvent.VK_SPACE);
				else if(theta >= -Math.PI / 4f && theta < Math.PI / 4f)
					game.onKeyPress(e.getPlayer(), KeyEvent.VK_W);
				else if(theta >= -Math.PI * (3f / 4f) && theta < -Math.PI / 4f)
					game.onKeyPress(e.getPlayer(), KeyEvent.VK_A);
				else if(theta >= Math.PI / 4f && theta < Math.PI * (3f / 4f))
					game.onKeyPress(e.getPlayer(), KeyEvent.VK_D);
				else
					game.onKeyPress(e.getPlayer(), KeyEvent.VK_S);
			}
		}

		if(moved)
		{
			e.getPlayer().setWalkSpeed(.05f);
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerSprint(PlayerToggleSprintEvent e)
	{
		if(e.isSprinting())
			e.setCancelled(true);
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
