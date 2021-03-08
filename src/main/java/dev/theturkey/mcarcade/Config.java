package dev.theturkey.mcarcade;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class Config
{
	public static String dbHost;
	public static int dbPort;
	public static String dbDatabase;
	public static String dbUsername;
	public static String dbPassword;
	public static void loadConfig(FileConfiguration config)
	{
		ConfigurationSection dbSec = config.getConfigurationSection("DB");
		dbHost= dbSec.getString("host", "localhost");
		dbPort = dbSec.getInt("port", 3306);
		dbDatabase = dbSec.getString("database", "video_games_db");
		dbUsername = dbSec.getString("username", "user");
		dbPassword = dbSec.getString("password", "password");
	}
}
