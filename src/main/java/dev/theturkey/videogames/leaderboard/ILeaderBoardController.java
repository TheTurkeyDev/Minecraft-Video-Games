package dev.theturkey.videogames.leaderboard;

import com.google.gson.JsonObject;
import org.bukkit.entity.Player;

public interface ILeaderBoardController
{

	JsonObject getRankings(String leaderBoardId, int top, boolean asc);

	void addScore(Player player, long score, String leaderBoardId);
}
