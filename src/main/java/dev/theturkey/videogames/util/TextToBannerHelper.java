package dev.theturkey.videogames.util;

import dev.theturkey.videogames.VGCore;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rotatable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextToBannerHelper
{
	private static final Map<Character, List<PatternDef>> CHARS_TO_PATTERN = new HashMap<>();

	static
	{
		CHARS_TO_PATTERN.put('d', Arrays.asList(new PatternDef(false, PatternType.BASE), new PatternDef(true, PatternType.STRIPE_RIGHT), new PatternDef(true, PatternType.STRIPE_BOTTOM), new PatternDef(true, PatternType.STRIPE_TOP), new PatternDef(false, PatternType.CURLY_BORDER), new PatternDef(true, PatternType.STRIPE_LEFT), new PatternDef(false, PatternType.BORDER)));
		CHARS_TO_PATTERN.put('e', Arrays.asList(new PatternDef(false, PatternType.BASE), new PatternDef(true, PatternType.STRIPE_LEFT), new PatternDef(true, PatternType.STRIPE_TOP), new PatternDef(true, PatternType.STRIPE_MIDDLE), new PatternDef(true, PatternType.STRIPE_BOTTOM), new PatternDef(false, PatternType.BORDER)));
		CHARS_TO_PATTERN.put('h', Arrays.asList(new PatternDef(true, PatternType.BASE), new PatternDef(false, PatternType.STRIPE_TOP), new PatternDef(false, PatternType.STRIPE_BOTTOM), new PatternDef(true, PatternType.STRIPE_LEFT), new PatternDef(true, PatternType.STRIPE_RIGHT), new PatternDef(false, PatternType.BORDER)));
		CHARS_TO_PATTERN.put('l', Arrays.asList(new PatternDef(false, PatternType.BASE), new PatternDef(true, PatternType.STRIPE_BOTTOM), new PatternDef(true, PatternType.STRIPE_LEFT), new PatternDef(false, PatternType.BORDER)));
		CHARS_TO_PATTERN.put('n', Arrays.asList(new PatternDef(false, PatternType.BASE), new PatternDef(true, PatternType.STRIPE_LEFT), new PatternDef(false, PatternType.TRIANGLE_TOP), new PatternDef(true, PatternType.STRIPE_DOWNRIGHT), new PatternDef(true, PatternType.STRIPE_RIGHT), new PatternDef(false, PatternType.BORDER)));
		CHARS_TO_PATTERN.put('o', Arrays.asList(new PatternDef(false, PatternType.BASE), new PatternDef(true, PatternType.STRIPE_LEFT), new PatternDef(true, PatternType.STRIPE_RIGHT), new PatternDef(true, PatternType.STRIPE_BOTTOM), new PatternDef(true, PatternType.STRIPE_TOP), new PatternDef(false, PatternType.BORDER)));
		CHARS_TO_PATTERN.put('t', Arrays.asList(new PatternDef(false, PatternType.BASE), new PatternDef(true, PatternType.STRIPE_TOP), new PatternDef(true, PatternType.STRIPE_CENTER), new PatternDef(false, PatternType.BORDER)));
		CHARS_TO_PATTERN.put('x', Arrays.asList(new PatternDef(false, PatternType.BASE), new PatternDef(true, PatternType.CROSS), new PatternDef(false, PatternType.BORDER)));
	}


	public static void placeString(String message, Location startLoc, BlockFace dir, DyeColor textColor, DyeColor bgColor)
	{
		Location copy = startLoc.clone();
		for(char c : message.toLowerCase().toCharArray())
		{
			Block block = VGCore.gameWorld.getBlockAt(copy);
			block.setType(Material.BLUE_BANNER);
			BlockData blockData = block.getBlockData();
			if(blockData instanceof Rotatable)
			{
				((Rotatable) blockData).setRotation(BlockFace.NORTH);
				block.setBlockData(blockData);
			}
			Banner banner = ((Banner) block.getState());
			for(PatternDef p : CHARS_TO_PATTERN.getOrDefault(c, new ArrayList<>()))
				banner.addPattern(p.getPattern(textColor, bgColor));
			banner.update();
			copy.add(dir.getDirection());
		}
	}


	private static class PatternDef
	{
		private boolean isCharPart;
		private PatternType type;

		public PatternDef(boolean isCharPart, PatternType type)
		{
			this.isCharPart = isCharPart;
			this.type = type;
		}

		public Pattern getPattern(DyeColor textColor, DyeColor bgColor)
		{
			return new Pattern(isCharPart ? textColor : bgColor, type);
		}
	}
}
