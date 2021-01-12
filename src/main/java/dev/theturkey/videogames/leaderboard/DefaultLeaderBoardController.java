package dev.theturkey.videogames.leaderboard;

import com.google.gson.JsonObject;
import org.bukkit.entity.Player;

public class DefaultLeaderBoardController implements ILeaderBoardController
{
	@Override
	public JsonObject getRankings(String leaderBoardId, int top, boolean asc)
	{
		return new JsonObject();
	}

	@Override
	public void addScore(Player player, long score, String leaderBoardId)
	{

	}
}
