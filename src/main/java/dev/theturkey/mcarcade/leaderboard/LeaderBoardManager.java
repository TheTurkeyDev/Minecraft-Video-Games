package dev.theturkey.mcarcade.leaderboard;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.theturkey.mcarcade.MCACore;
import dev.theturkey.mcarcade.util.FakeArmorStandUtil;
import dev.theturkey.mcarcade.util.FakeHologram;
import dev.theturkey.mcarcade.util.Vector2D;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderBoardManager
{
	private static final Map<String, LeaderBoardDataWrapper> LEADER_BOARDS = new HashMap<>();

	private static ILeaderBoardController leaderBoardController;


	public static void addScore(Player player, long score, String leaderBoardId)
	{
		leaderBoardController.addScore(player, score, leaderBoardId);
	}

	public static void setLeaderBoardController(ILeaderBoardController controller)
	{
		leaderBoardController = controller;
	}

	public static void registerLeaderBoard(String id, String displayName, LeaderBoardScoreType scoreType, boolean asc)
	{
		LEADER_BOARDS.put(id, new LeaderBoardDataWrapper(displayName, scoreType, asc));
	}

	public static void showLeaderBoards(Player player)
	{
		List<FakeHologram> hologramList = new ArrayList<>();
		int currentEntID = Integer.MAX_VALUE - 1000000;

		for(String leaderboardId : LEADER_BOARDS.keySet())
		{
			LeaderBoardDataWrapper leaderBoardDataWrapper = LEADER_BOARDS.get(leaderboardId);
			JsonObject data;
			try
			{
				data = leaderBoardController.getRankings(leaderboardId, 10, leaderBoardDataWrapper.asc);
			} catch(Exception e)
			{
				e.printStackTrace();
				continue;
			}
			if(!data.has("rankings"))
				return;

			JsonArray rankings = data.getAsJsonArray("rankings");
			for(int i = 0; i < 11; i++)
			{
				FakeHologram hologram = new FakeHologram(currentEntID, new Location(MCACore.gameWorld, leaderBoardDataWrapper.xzLocation.getX(), 256 - (i * 0.25), leaderBoardDataWrapper.xzLocation.getY()));

				hologramList.add(hologram);
				currentEntID--;
				if(i == 0)
				{
					hologram.setText(leaderBoardDataWrapper.displayName);
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
						hologram.setText(rankObj.get("display_name").getAsString() + " - " + score);
					}
				}
			}
		}

		FakeArmorStandUtil.send(player, hologramList);
	}

	public static void removeLeaderBoards(Player player)
	{
		int currentEntID = Integer.MAX_VALUE - 1000000;
		int[] entIds = new int[LEADER_BOARDS.size() * 11];
		int index = 0;
		for(int i = 0; i < LEADER_BOARDS.keySet().size(); i++)
		{
			for(int j = 0; j < 11; j++)
			{
				entIds[index] = currentEntID;
				currentEntID--;
				index++;
			}
		}

		FakeArmorStandUtil.removeArmorStands(player, entIds);
	}

	private static class LeaderBoardDataWrapper
	{
		private String displayName;
		private Vector2D xzLocation;
		private LeaderBoardScoreType scoreType;
		private boolean asc;

		public LeaderBoardDataWrapper(String displayName, LeaderBoardScoreType scoreType, boolean asc)
		{
			this.displayName = displayName;
			this.scoreType = scoreType;
			this.asc = asc;

			double angle = LEADER_BOARDS.size() * (Math.PI / 8);

			xzLocation = new Vector2D(Math.cos(angle) * 15, Math.sin(angle) * 15);
		}
	}
}
