package dev.theturkey.mcarcade.commands;

import org.bukkit.entity.Player;

public interface IVGCommand
{
	boolean execute(Player player, String[] args);
}
