package dev.theturkey.videogames.leaderboard;

import com.google.gson.JsonObject;
import dev.theturkey.videogames.games.VideoGamesEnum;
import org.bukkit.entity.Player;

public interface ILeaderBoardController
{

	JsonObject getRankings(String leaderBoardId, int top);

	void addScore(Player player, long score, VideoGamesEnum gameEnum);
}
