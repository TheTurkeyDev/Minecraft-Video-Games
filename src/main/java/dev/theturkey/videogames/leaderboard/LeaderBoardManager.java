package dev.theturkey.videogames.leaderboard;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.theturkey.videogames.VGCore;
import dev.theturkey.videogames.games.VideoGamesEnum;
import dev.theturkey.videogames.util.Hologram;
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
	public static final Map<String, Hologram[]> leaderBoardHolograms = new HashMap<>();

	private static ILeaderBoardController leaderBoardController = new DefaultLeaderBoardController();

	public static void updateLeaderBoard(World world, VideoGamesEnum gameEnum)
	{
		String leaderBoardId = "mcvg_" + gameEnum.name().toLowerCase();
		if(!leaderBoardHolograms.containsKey(leaderBoardId))
		{
			List<Entity> ents = world.getEntities();
			Hologram[] holograms = new Hologram[11];

			for(Entity ent : ents)
			{
				if(ent instanceof ArmorStand && ent.hasMetadata("hologram-key"))
				{
					String key = ent.getMetadata("hologram-key").get(0).asString();
					if(key.startsWith(leaderBoardId + "_"))
					{
						String part = key.replace(leaderBoardId + "_", "");
						Hologram hologram = new Hologram((ArmorStand) ent);
						int index = 0;
						if(!part.equalsIgnoreCase("TITLE"))
							index = 1 + Integer.parseInt(part);
						holograms[index] = hologram;
					}
				}
			}
			leaderBoardHolograms.put(leaderBoardId, holograms);
		}


		JsonObject data = leaderBoardController.getRankings(leaderBoardId, 10);
		if(!data.has("rankings"))
			return;

		JsonArray rankings = data.getAsJsonArray("rankings");

		Bukkit.getScheduler().scheduleSyncDelayedTask(VGCore.getPlugin(), () ->
		{
			Hologram[] holograms = leaderBoardHolograms.get(leaderBoardId);
			for(int i = 0; i < holograms.length; i++)
			{
				if(holograms[i] == null)
				{
					Hologram hologram = new Hologram(world, new Location(world, 10, 256 - (i * 0.25), 0), "");
					hologram.setKey(leaderBoardId + "_" + (i == 0 ? "TITLE" : (i - 1)));
					holograms[i] = hologram;
				}

				if(i == 0)
				{
					holograms[i].setText(gameEnum.name().replace("_", " ") + " High Scores");
				}
				else
				{
					if(rankings.size() > i - 1)
					{
						JsonObject rankObj = rankings.get(i - 1).getAsJsonObject();
						holograms[i].setText(rankObj.get("display_name").getAsString() + " - " + rankObj.get("score").getAsLong());
					}
				}
			}
		}, 1);
	}

	public static void addScore(Player player, long score, VideoGamesEnum gameEnum)
	{
		leaderBoardController.addScore(player, score, gameEnum);
	}

	public static void setLeaderBoardController(ILeaderBoardController controller)
	{
		leaderBoardController = controller;
	}
}
