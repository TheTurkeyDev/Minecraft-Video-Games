package dev.theturkey.videogames.games.displaytest;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import dev.theturkey.videogames.VGCore;
import dev.theturkey.videogames.games.VideoGameBase;
import dev.theturkey.videogames.packetwrappers.WrapperPlayServerEntityDestroy;
import dev.theturkey.videogames.packetwrappers.WrapperPlayServerEntityEquipment;
import dev.theturkey.videogames.packetwrappers.WrapperPlayServerEntityMetadata;
import dev.theturkey.videogames.packetwrappers.WrapperPlayServerSpawnEntity;
import dev.theturkey.videogames.util.Vector2I;
import dev.theturkey.videogames.util.Vector3I;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class DisplayTest extends VideoGameBase
{
	private static final int Y_BASE = 150;
	private static final int WIDTH = 70;
	private static final int HEIGHT = 50;
	private static final float BLOCK_WIDTH = 0.6f;
	private static final float BLOCK_HEIGHT = 0.6f;
	private static final int BLOCK_START_ID = Integer.MAX_VALUE - (WIDTH * HEIGHT);

	private int gameTick = -1;


	public DisplayTest(Vector2I gameLoc)
	{
		super(gameLoc, new Vector3I(gameLoc.getX(), Y_BASE, gameLoc.getY()));
	}

	@Override
	public void constructGame(World world, Player player)
	{
		Location playerLoc = getPlayerLoc(world);

		for(int y = 0; y < HEIGHT; y++)
		{
			for(int x = 0; x < WIDTH; x++)
			{
				int entID = BLOCK_START_ID + ((y * WIDTH) + x);
				Location entLoc = playerLoc.clone().add((x - (WIDTH / 2f)) * BLOCK_WIDTH, (y - (HEIGHT / 2f)) * BLOCK_HEIGHT, 30);


				WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity();

				packet.setX(entLoc.getX());
				packet.setY(entLoc.getY());
				packet.setZ(entLoc.getZ());
				packet.setUniqueId(UUID.randomUUID());
				packet.setType(EntityType.ARMOR_STAND);
				packet.setEntityID(entID);
				packet.setPitch(0);
				packet.setYaw(0);
				packet.sendPacket(player);


				WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
				dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 0x20); // Invisible
				dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(5, WrappedDataWatcher.Registry.get(Boolean.class)), true); // No Gravity
				byte statusMask = 0x08; // no base plate
				dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(14, WrappedDataWatcher.Registry.get(Byte.class)), statusMask); // Armor Stand status

				WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata();
				metadataPacket.setMetadata(dataWatcher.getWatchableObjects());
				metadataPacket.setEntityID(entID);
				metadataPacket.sendPacket(player);
			}
		}
	}

	@Override
	public void deconstructGame(World world, Player player)
	{
		Vector3I gameLoc = getGameLocScaled();
		world.getBlockAt(new Location(world, gameLoc.getX(), 49, gameLoc.getY())).setType(Material.AIR);

		int[] ents = new int[WIDTH * HEIGHT];
		for(int y = 0; y < HEIGHT; y++)
		{
			for(int x = 0; x < WIDTH; x++)
			{
				int oneDIndex = (y * WIDTH) + x;
				ents[oneDIndex] = BLOCK_START_ID + oneDIndex;
			}
		}
		// Remove entities
		WrapperPlayServerEntityDestroy destroyPacket = new WrapperPlayServerEntityDestroy();
		destroyPacket.setEntityIds(ents);
		destroyPacket.sendPacket(player);
	}

	private List<Material> mats = Arrays.asList(Material.WHITE_WOOL, Material.BLACK_WOOL, Material.BLUE_WOOL,
			Material.BROWN_WOOL, Material.CYAN_WOOL, Material.GREEN_WOOL, Material.ORANGE_WOOL);
	private Random random = new Random();

	@Override
	public void startGame(World world, Player player)
	{
		super.startGame(world, player);
		gameTick = Bukkit.getScheduler().scheduleSyncRepeatingTask(VGCore.getPlugin(), () ->
		{

			for(int y = 0; y < HEIGHT; y++)
			{
				for(int x = 0; x < WIDTH; x++)
				{
					int oneDIndex = (y * WIDTH) + x;

					WrapperPlayServerEntityEquipment equipmentPacket = new WrapperPlayServerEntityEquipment();
					equipmentPacket.setEntityID(BLOCK_START_ID + oneDIndex);
					equipmentPacket.setSlotStackPair(EnumWrappers.ItemSlot.HEAD, new ItemStack(mats.get(random.nextInt(mats.size()))));
					equipmentPacket.sendPacket(player);
				}
			}
			// Add entities

		}, 0, 1);
	}

	@Override
	public void endGame(World world, Player player)
	{
		super.endGame(world, player);
		Bukkit.getScheduler().cancelTask(gameTick);
	}

	@Override
	public boolean isEntInGame(Entity entity)
	{
		return false;
	}

	@Override
	public void onEntityCollide(Entity entity)
	{

	}

	@Override
	public void onPlayerJump()
	{

	}

	@Override
	public int getYBase()
	{
		return Y_BASE;
	}

	@Override
	public int getWidth()
	{
		return 75;
	}

	@Override
	public int getHeight()
	{
		return 50;
	}
}
