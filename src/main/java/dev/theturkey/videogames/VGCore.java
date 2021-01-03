package dev.theturkey.videogames;


import com.google.gson.JsonParser;
import dev.theturkey.videogames.commands.GamesCommand;
import dev.theturkey.videogames.commands.IVGCommand;
import dev.theturkey.videogames.commands.LeaveCommand;
import dev.theturkey.videogames.commands.PlayCommand;
import dev.theturkey.videogames.games.GameManager;
import dev.theturkey.videogames.listeners.EntityListener;
import dev.theturkey.videogames.listeners.PlayerListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class VGCore extends JavaPlugin
{
	public static final JsonParser JSON_PARSER = new JsonParser();
	public static final Logger log = Logger.getLogger("VG");

	private static Map<String, IVGCommand> commands = new HashMap<>();

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id)
	{
		return new VoidWorldGenerator();
	}

	@Override
	public void onEnable()
	{
		PluginManager m = getServer().getPluginManager();
		m.registerEvents(new PlayerListener(), this);
		m.registerEvents(new EntityListener(), this);

		commands.put("games", new GamesCommand());
		commands.put("play", new PlayCommand());
		commands.put("leave", new LeaveCommand());
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
