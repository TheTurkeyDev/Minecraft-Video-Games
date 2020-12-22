package dev.theturkey.videogames;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.Collections;
import java.util.List;
import java.util.Random;


/**
 * VoidWorldGenerator class.
 * This is the main class that will be loaded by Bukkit.
 *
 * @author P.J.S. Kools via https://github.com/Pieter12345/VoidWorldGenerator/blob/master/src/main/java/io/github/pieter12345/voidworldgenerator/VoidWorldGenerator.java
 */
public class VoidWorldGenerator extends ChunkGenerator
{
	@Override
	public List<BlockPopulator> getDefaultPopulators(World world)
	{
		return Collections.emptyList();
	}

	@Override
	public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome)
	{
		ChunkData chunkData = super.createChunkData(world);

		// Set biome.
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				for(int y = 0; y < world.getMaxHeight(); y += 4)
				{
					biome.setBiome(x, y, z, Biome.PLAINS);
				}
			}
		}

		// Return the new chunk data.
		return chunkData;
	}

	@Override
	public boolean canSpawn(World world, int x, int z)
	{
		return true;
	}

	@Override
	public Location getFixedSpawnLocation(World world, Random random)
	{
		return new Location(world, 0, 255, 0);
	}
}
