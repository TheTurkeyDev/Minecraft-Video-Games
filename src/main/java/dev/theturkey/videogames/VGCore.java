package dev.theturkey.videogames;


import com.google.gson.JsonParser;
import dev.theturkey.videogames.commands.GamesCommand;
import dev.theturkey.videogames.commands.IVGCommand;
import dev.theturkey.videogames.commands.LeaveCommand;
import dev.theturkey.videogames.commands.PlayCommand;
import dev.theturkey.videogames.games.GameManager;
import dev.theturkey.videogames.games.brickbreaker.BrickBreakerGame;
import dev.theturkey.videogames.games.minesweeper.MinesweeperDifficulty;
import dev.theturkey.videogames.leaderboard.DefaultLeaderBoardController;
import dev.theturkey.videogames.leaderboard.LeaderBoardManager;
import dev.theturkey.videogames.leaderboard.LeaderBoardScoreType;
import dev.theturkey.videogames.listeners.EntityListener;
import dev.theturkey.videogames.listeners.PlayerListener;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

public class VGCore extends JavaPlugin
{
	public static final Random RAND = new Random();
	public static final JsonParser JSON_PARSER = new JsonParser();
	public static final Logger log = Logger.getLogger("VG");

	public static World gameWorld;
	public static final Location SPAWN = new Location(null, 0.5, 255, 0.5, 0, 0);

	private static Map<String, IVGCommand> commands = new HashMap<>();

	@Override
	public void onEnable()
	{
		PluginManager m = getServer().getPluginManager();
		m.registerEvents(new PlayerListener(), this);
		m.registerEvents(new EntityListener(), this);

		commands.put("games", new GamesCommand());
		commands.put("play", new PlayCommand());
		commands.put("leave", new LeaveCommand());
		getCommand("play").setTabCompleter((commandSender, command, s, args) ->
		{
			if(args.length == 1)
				return new ArrayList<>(GameManager.GAMES.keySet());
			return new ArrayList<>();
		});

		WorldCreator wc = new WorldCreator("video_games_world");
		wc.generator(new VoidWorldGenerator()).generateStructures(false).type(WorldType.FLAT).environment(World.Environment.NORMAL).generatorSettings("");
		gameWorld = getServer().createWorld(wc);
		SPAWN.setWorld(gameWorld);
		LeaderBoardManager.registerLeaderBoard(BrickBreakerGame.LEADER_BOARD_ID, "Brick Breaker High Scores", LeaderBoardScoreType.NUMBER, false);
		LeaderBoardManager.registerLeaderBoard(MinesweeperDifficulty.EASY.getLeaderBoardKey(), "Minesweeper Easy Mode High Scores", LeaderBoardScoreType.TIME_MS, true);
		LeaderBoardManager.registerLeaderBoard(MinesweeperDifficulty.MEDIUM.getLeaderBoardKey(), "Minesweeper Medium Mode Scores", LeaderBoardScoreType.TIME_MS, true);
		LeaderBoardManager.registerLeaderBoard(MinesweeperDifficulty.HARD.getLeaderBoardKey(), "Minesweeper Hard Mode Scores", LeaderBoardScoreType.TIME_MS, true);

		LeaderBoardManager.setLeaderBoardController(new DefaultLeaderBoardController());
	}

	@Override
	public void onDisable()
	{
		GameManager.reset();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		Player player;
		if(sender instanceof Player)
		{
			player = (Player) sender;
		}
		else
		{
			sender.sendMessage("You must be a player to run these commands!");
			return true;
		}

		commands.computeIfPresent(label, (lbl, command) ->
		{
			command.execute(player, args);
			return command;
		});

		return false;
	}

	public static VGCore getPlugin()
	{
		return JavaPlugin.getPlugin(VGCore.class);
	}
}
