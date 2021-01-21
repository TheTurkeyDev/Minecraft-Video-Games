package dev.theturkey.videogames.leaderboard;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;

public class DefaultLeaderBoardController implements ILeaderBoardController
{
	private String host, port, database, username, password;
	private static Connection connection;

	public DefaultLeaderBoardController()
	{
		host = "localhost";
		port = "3306";
		database = "video_games_db";
		username = "minecraft";
		password = "password123";
		try
		{
			openConnection();
		} catch(ClassNotFoundException | SQLException e)
		{
			e.printStackTrace();
		}
	}

	public void openConnection() throws SQLException, ClassNotFoundException
	{
		if(connection != null && !connection.isClosed())
		{
			return;
		}
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
	}

	@Override
	public JsonObject getRankings(String leaderBoardId, int top, boolean asc)
	{
		JsonObject json = new JsonObject();
		JsonArray rankings = new JsonArray();
		json.add("rankings", rankings);
		try
		{
			PreparedStatement stmt = connection.prepareStatement("SELECT * FROM leaderboard WHERE `leaderboard_id`=? ORDER BY `score` " + (asc ? "ASC" : "DESC") + " LIMIT ?;");
			stmt.setString(1, leaderBoardId);
			stmt.setInt(2, top);
			ResultSet resultSet = stmt.executeQuery();
			while(resultSet.next())
			{
				JsonObject rank = new JsonObject();
				rank.addProperty("score", resultSet.getLong("score"));
				rank.addProperty("display_name", resultSet.getString("display_name"));
				rankings.add(rank);
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		return json;
	}

	@Override
	public void addScore(Player player, long score, String leaderBoardId)
	{
		try
		{
			PreparedStatement stmt = connection.prepareStatement("INSERT INTO `leaderboard` VALUES (?,?,?,?,?,?);");
			stmt.setString(1, randomUID(8));
			stmt.setString(2, leaderBoardId);
			stmt.setString(3, player.getDisplayName());
			stmt.setString(4, player.getUniqueId().toString());
			stmt.setLong(5, score);
			stmt.setTimestamp(6, new Timestamp((new Date()).getTime()));
			stmt.execute();
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private static final Random rand = new Random();
	private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

	public static String randomUID(int length)
	{
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < length; i++)
			builder.append(CHARACTERS[rand.nextInt(CHARACTERS.length)]);
		return builder.toString();
	}
}
