package dev.theturkey.videogames.leaderboard;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.theturkey.videogames.VGCore;
import dev.theturkey.videogames.util.Hologram;
import dev.theturkey.videogames.util.Vector2D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderBoardManager
{
	private static final Map<String, LeaderBoardDataWrapper> LEADER_BOARDS = new HashMap<>();

	private static ILeaderBoardController leaderBoardController = new DefaultLeaderBoardController();

	public static void updateLeaderBoard(World world, String leaderBoardId)
	{
		if(!LEADER_BOARDS.containsKey(leaderBoardId))
			return;

		LeaderBoardDataWrapper leaderBoardDataWrapper = LEADER_BOARDS.get(leaderBoardId);
		JsonObject data = leaderBoardController.getRankings(leaderBoardId, 10, leaderBoardDataWrapper.asc);
		if(!data.has("rankings"))
			return;

		JsonArray rankings = data.getAsJsonArray("rankings");
		Bukkit.getScheduler().scheduleSyncDelayedTask(VGCore.getPlugin(), () ->
		{
			Hologram[] holograms = leaderBoardDataWrapper.holograms;
			for(int i = 0; i < holograms.length; i++)
			{
				if(holograms[i] == null)
				{
					Hologram hologram = new Hologram(world, new Location(world, leaderBoardDataWrapper.xzLocation.getX(), 256 - (i * 0.25), leaderBoardDataWrapper.xzLocation.getY()), "");
					hologram.setKey(leaderBoardId + "_" + (i == 0 ? "TITLE" : (i - 1)));
					holograms[i] = hologram;
				}

				if(i == 0)
				{
					holograms[i].setText(leaderBoardDataWrapper.displayName);
				}
				else
				{
					if(rankings.size() > i - 1)
					{
						JsonObject rankObj = rankings.get(i - 1).getAsJsonObject();
						String score = "";
						if(leaderBoardDataWrapper.scoreType == LeaderBoardScoreType.NUMBER)
						{
							score = rankObj.get("score").getAsString();
						}
						else if(leaderBoardDataWrapper.scoreType == LeaderBoardScoreType.TIME_MS)
						{
							long ms = rankObj.get("score").getAsLong();
							long minutes = (ms / 1000) / 60;
							ms = ms - (minutes * 60 * 1000);
							long seconds = (ms / 1000);
							ms = ms - (seconds * 1000);
							score = String.format("%d:%02d.%03d", minutes, seconds, ms);
						}
						holograms[i].setText(rankObj.get("display_name").getAsString() + " - " + score);
					}
				}
			}
		}, 1);
	}

	public static void updateAllLeaderBoards(World world)
	{
		for(String leaderBoardId : LEADER_BOARDS.keySet())
			updateLeaderBoard(world, leaderBoardId);
	}

	public static void addScore(Player player, long score, String leaderBoardId)
	{
		leaderBoardController.addScore(player, score, leaderBoardId);
	}

	public static void setLeaderBoardController(ILeaderBoardController controller)
	{
		leaderBoardController = controller;
	}

	public static void registerLeaderBoard(World world, String id, String displayName, LeaderBoardScoreType scoreType, boolean asc)
	{
		LEADER_BOARDS.put(id, new LeaderBoardDataWrapper(world, id, displayName, scoreType, asc));
	}


	private static class LeaderBoardDataWrapper
	{
		private String leaderBoardId;
		private String displayName;
		private Vector2D xzLocation;
		private Hologram[] holograms;
		private LeaderBoardScoreType scoreType;
		private boolean asc;

		public LeaderBoardDataWrapper(World world, String leaderBoardId, String displayName, LeaderBoardScoreType scoreType, boolean asc)
		{
			this.leaderBoardId = leaderBoardId;
			this.displayName = displayName;
			this.holograms = new Hologram[11];
			this.scoreType = scoreType;
			this.asc = asc;

			double angle = LEADER_BOARDS.size() * (Math.PI / 8);

			xzLocation = new Vector2D(Math.cos(angle) * 15, Math.sin(angle) * 15);

			List<Entity> ents = world.getEntities();

			for(Entity ent : ents)
			{
				if(ent instanceof ArmorStand && ent.hasMetadata("hologram-key"))
				{
					String key = ent.getMetadata("hologram-key").get(0).asString();
					if(key.startsWith(leaderBoardId + "_"))
					{
						String[] parts = key.split("_");
						String part = parts[parts.length - 1];
						Hologram hologram = new Hologram((ArmorStand) ent);
						int index = 0;
						if(!part.equalsIgnoreCase("TITLE"))
							index = 1 + Integer.parseInt(part);
						holograms[index] = hologram;
					}
				}
			}
		}
	}
}
