package dev.theturkey.mcarcade;


import com.google.gson.JsonParser;
import dev.theturkey.mcarcade.commands.GamesCommand;
import dev.theturkey.mcarcade.commands.IVGCommand;
import dev.theturkey.mcarcade.commands.LeaveCommand;
import dev.theturkey.mcarcade.commands.PlayCommand;
import dev.theturkey.mcarcade.games.GameManager;
import dev.theturkey.mcarcade.games.brickbreaker.BrickBreakerGame;
import dev.theturkey.mcarcade.games.minesweeper.MinesweeperDifficulty;
import dev.theturkey.mcarcade.games.tetris.TetrisGame;
import dev.theturkey.mcarcade.leaderboard.DefaultLeaderBoardController;
import dev.theturkey.mcarcade.leaderboard.LeaderBoardManager;
import dev.theturkey.mcarcade.leaderboard.LeaderBoardScoreType;
import dev.theturkey.mcarcade.listeners.EntityListener;
import dev.theturkey.mcarcade.listeners.PlayerListener;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

public class MCACore extends JavaPlugin
{
	public static final Random RAND = new Random();
	public static final JsonParser JSON_PARSER = new JsonParser();
	public static final Logger log = Logger.getLogger("MC_Arcade");

	public static World gameWorld;
	public static final Location SPAWN = new Location(null, 0.5, 255, 0.5, 0, 0);

	private static Map<String, IVGCommand> commands = new HashMap<>();

	@Override
	public void onEnable()
	{
		Config.loadConfig(this.getConfig());

		PluginManager m = getServer().getPluginManager();
		m.registerEvents(new PlayerListener(), this);
		m.registerEvents(new EntityListener(), this);

		commands.put("games", new GamesCommand());
		commands.put("play", new PlayCommand());
		commands.put("leave", new LeaveCommand());
		getCommand("mcarcade").setTabCompleter((commandSender, command, s, args) ->
		{
			if(args.length == 1)
				return new ArrayList<>(commands.keySet());
			if(args.length == 2)
				return new ArrayList<>(GameManager.GAMES.keySet());
			return new ArrayList<>();
		});

		WorldCreator wc = new WorldCreator("mc_arcade_world");
		wc.generator(new VoidWorldGenerator()).generateStructures(false).type(WorldType.FLAT).environment(World.Environment.NORMAL).generatorSettings("");
		gameWorld = getServer().createWorld(wc);
		gameWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		gameWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
		gameWorld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
		gameWorld.setTime(1200);
		SPAWN.setWorld(gameWorld);
		LeaderBoardManager.registerLeaderBoard(BrickBreakerGame.LEADER_BOARD_ID, "Brick Breaker High Scores", LeaderBoardScoreType.NUMBER, false);
		LeaderBoardManager.registerLeaderBoard(MinesweeperDifficulty.EASY.getLeaderBoardKey(), "Minesweeper Easy Mode High Scores", LeaderBoardScoreType.TIME_MS, true);
		LeaderBoardManager.registerLeaderBoard(MinesweeperDifficulty.MEDIUM.getLeaderBoardKey(), "Minesweeper Medium Mode High Scores", LeaderBoardScoreType.TIME_MS, true);
		LeaderBoardManager.registerLeaderBoard(MinesweeperDifficulty.HARD.getLeaderBoardKey(), "Minesweeper Hard Mode High Scores", LeaderBoardScoreType.TIME_MS, true);
		LeaderBoardManager.registerLeaderBoard(TetrisGame.LEADER_BOARD_ID, "Tetris High Scores", LeaderBoardScoreType.NUMBER, false);

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

		if(args.length == 0)
		{
			StringBuilder commandsStr = new StringBuilder();
			for(String cmdStr : commands.keySet())
				commandsStr.append(cmdStr).append(" | ");
			commandsStr.delete(commandsStr.length() - 3, commandsStr.length());
			sendMessage(player, "Try /mcarcade <" + commandsStr.toString() + ">");
			return true;
		}

		commands.computeIfPresent(args[0], (lbl, command) ->
		{
			command.execute(player, Arrays.copyOfRange(args, 1, args.length));
			return command;
		});

		return false;
	}

	public static MCACore getPlugin()
	{
		return JavaPlugin.getPlugin(MCACore.class);
	}

	private static final String MESSAGE_PREFIX = ChatColor.BLUE + "[" + ChatColor.AQUA + "MC Arcade" + ChatColor.BLUE + "] " + ChatColor.WHITE;

	public static void sendMessage(Player player, String message)
	{
		player.sendMessage(MESSAGE_PREFIX + message);
	}
}
