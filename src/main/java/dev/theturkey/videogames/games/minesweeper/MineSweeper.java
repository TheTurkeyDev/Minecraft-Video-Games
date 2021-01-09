package dev.theturkey.videogames.games.minesweeper;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import dev.theturkey.videogames.VGCore;
import dev.theturkey.videogames.games.GameManager;
import dev.theturkey.videogames.games.VideoGameBase;
import dev.theturkey.videogames.packetwrappers.WrapperPlayServerEntityDestroy;
import dev.theturkey.videogames.packetwrappers.WrapperPlayServerEntityEquipment;
import dev.theturkey.videogames.packetwrappers.WrapperPlayServerEntityMetadata;
import dev.theturkey.videogames.packetwrappers.WrapperPlayServerSpawnEntity;
import dev.theturkey.videogames.util.Hologram;
import dev.theturkey.videogames.util.PlayerHeadRetriever;
import dev.theturkey.videogames.util.Vector2I;
import dev.theturkey.videogames.util.Vector3I;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MineSweeper extends VideoGameBase
{
	private static final int Y_BASE = 150;
	private static final int ZDIST = 15;
	private static final int WIDTH = 20;
	private static final int HEIGHT = 20;
	private static final float BLOCK_WIDTH = 0.6f;
	private static final float BLOCK_HEIGHT = 0.6f;
	private static final int BLOCK_START_ID = Integer.MAX_VALUE - (WIDTH * HEIGHT);


	private static final int BOMBS = 30;
	private static final int FLAG_TILE_ID = -2;
	private static final int UNMARKED_TILE_ID = -1;

	private static final Map<Integer, ItemStack> HEADS_MAPPINGS = new HashMap<>();

	static
	{
		HEADS_MAPPINGS.put(-2, PlayerHeadRetriever.makeSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDhmZDcxMjZjZDY3MGM3OTcxYTI4NTczNGVkZmRkODAyNTcyYTcyYTNmMDVlYTQxY2NkYTQ5NDNiYTM3MzQ3MSJ9fX0="));
		HEADS_MAPPINGS.put(1, PlayerHeadRetriever.makeSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQyMWIwYmFmYjg5NzIxY2FjNDk0ZmYyZWY1MmE1NGExODMzOTg1OGU0ZGNhOTlhNDEzYzQyZDlmODhlMGY2In19fQ=="));
		HEADS_MAPPINGS.put(2, PlayerHeadRetriever.makeSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGQ1NGQxZjhmYmY5MWIxZTdmNTVmMWJkYjI1ZTJlMzNiYWY2ZjQ2YWQ4YWZiZTA4ZmZlNzU3ZDMwNzVlMyJ9fX0="));
		HEADS_MAPPINGS.put(3, PlayerHeadRetriever.makeSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMDMxZjY2YmUwOTUwNTg4NTk4ZmVlZWE3ZTZjNjc3OTM1NWU1N2NjNmRlOGI5MWE0NDM5MWIyZTlmZDcyIn19fQ=="));
		HEADS_MAPPINGS.put(4, PlayerHeadRetriever.makeSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmUzMTgzN2Q4OWMyNjRjM2I1NGY4MjE0YWUyNGY4OWEzNjhhOTJiYzQ2ZGY5MjI1MzMzYWQ3Y2Q0NDlmODU2In19fQ=="));
		HEADS_MAPPINGS.put(5, PlayerHeadRetriever.makeSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTg3ODdmZGM5M2VhMmE3MjUxYjEzZTI5ODIzMjI3ZWU0ZTI5MTVhOGJhNmQzOTllYThkZDE5ZTVkYTg3YzVlIn19fQ=="));
		HEADS_MAPPINGS.put(6, PlayerHeadRetriever.makeSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDEyYWFiNDAxZDY5YTVmMGM1Y2FkY2IzYzFkMTM3YjEwNzk0YzQzYjA1OGY5MzYzMGI4MTQ1YjgzNDk3YTQwIn19fQ=="));
		HEADS_MAPPINGS.put(7, PlayerHeadRetriever.makeSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWUxOThmZDgzMWNiNjFmMzkyN2YyMWNmOGE3NDYzYWY1ZWEzYzdlNDNiZDNlOGVjN2QyOTQ4NjMxY2NlODc5In19fQ=="));
		HEADS_MAPPINGS.put(8, PlayerHeadRetriever.makeSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjFjOWMwOWQ1MmRlYmM0NjVjMzI1NDJjNjhiZTQyYmRhNmY2NzUzZmUxZGViYTI1NzMyN2FjNWEwYzNhZCJ9fX0="));
	}

	private int[] gameBoard = new int[WIDTH * HEIGHT];
	private boolean[] bombsLoc = new boolean[WIDTH * HEIGHT];

	private Vector2I lookingAt = new Vector2I(0, 0);

	//If time stays in the way move it to the action bar?
	private Hologram timeHologram;
	private Hologram flagsHologram;

	private int gameTick = -1;
	private boolean gameOver = false;
	private long startTime = 0;

	public MineSweeper(Vector2I gameLoc)
	{
		super(gameLoc, new Vector3I(gameLoc.getX(), (int) (Y_BASE - ((HEIGHT / 2) * BLOCK_HEIGHT)), gameLoc.getY()));
	}

	@Override
	public void constructGame(World world, Player player)
	{
		Vector3I worldLoc = getGameLocScaled();

		Location playerLoc = getPlayerLoc(world);
		timeHologram = new Hologram(world, playerLoc.clone().add(5, -2, 5), ChatColor.RED + "Time: 0 seconds");
		flagsHologram = new Hologram(world, playerLoc.clone().add(-3, -2, 5), ChatColor.RED + "" + BOMBS + " flags left");

		for(int y = 0; y < HEIGHT; y++)
		{
			for(int x = 0; x < WIDTH; x++)
			{
				int entID = BLOCK_START_ID + ((y * WIDTH) + x);
				Location entLoc = new Location(world, worldLoc.getX() + ((WIDTH - x) * BLOCK_WIDTH), worldLoc.getY() + ((HEIGHT - y) * BLOCK_HEIGHT), worldLoc.getZ() + ZDIST);

				WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity();

				packet.setX(entLoc.getX());
				packet.setY(entLoc.getY());
				packet.setZ(entLoc.getZ());
				packet.setUniqueId(UUID.randomUUID());
				packet.setType(EntityType.ARMOR_STAND);
				packet.setEntityID(entID);
				packet.setPitch(0);
				packet.setYaw(180);
				packet.sendPacket(player);

				sendMetaDataPacket(player, entID, (byte) 0x20);
			}
		}
	}

	@Override
	public void deconstructGame(World world, Player player)
	{
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

		timeHologram.remove();
		flagsHologram.remove();
	}

	@Override
	public void startGame(World world, Player player)
	{
		super.startGame(world, player);
		startTime = System.currentTimeMillis();
		player.getInventory().setItem(0, new ItemStack(Material.STICK));

		player.sendRawMessage(ChatColor.GREEN + "Right click to flag, Left click to reveal tile");
		player.sendRawMessage(ChatColor.GREEN + "You must have the stick in hand to flag tiles!");

		for(int y = 0; y < HEIGHT; y++)
		{
			for(int x = 0; x < WIDTH; x++)
			{
				int index = (y * WIDTH) + x;
				gameBoard[index] = UNMARKED_TILE_ID;
				setArmorStandHead(player, BLOCK_START_ID + index, new ItemStack(Material.LIGHT_GRAY_CONCRETE));
			}
		}

		for(int i = 0; i < BOMBS; i++)
		{
			int x = VGCore.RAND.nextInt(WIDTH);
			int y = VGCore.RAND.nextInt(HEIGHT);

			int index = y * WIDTH + x;

			if(!bombsLoc[index])
				bombsLoc[index] = true;
			else
				i--;
		}

		gameTick = Bukkit.getScheduler().scheduleSyncRepeatingTask(VGCore.getPlugin(), () ->
		{
			timeHologram.setText(ChatColor.RED + "Time: " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds");
			Location playerLoc = player.getLocation();
			float yaw = playerLoc.getYaw();
			float pitch = playerLoc.getPitch();
			double boardX = ZDIST * Math.tan(Math.toRadians(yaw));
			double boardY = ZDIST * Math.tan(Math.toRadians(pitch));

			int x = (int) (((boardX / BLOCK_WIDTH) + (WIDTH / 2)) - 0.3);
			int y = (int) ((boardY / BLOCK_HEIGHT) + (HEIGHT / 2)) - 1;

			if(x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT)
			{
				if(lookingAt.getX() != x || lookingAt.getY() != y)
				{
					if(lookingAt.getX() != -1 && lookingAt.getY() != -1)
						setNotGlowing(player, BLOCK_START_ID + ((lookingAt.getY() * WIDTH) + lookingAt.getX()));
					setGlowing(player, BLOCK_START_ID + ((y * WIDTH) + x));
					lookingAt = new Vector2I(x, y);
				}
			}
			else
			{
				if(lookingAt.getX() != -1 && lookingAt.getY() != -1)
					setNotGlowing(player, BLOCK_START_ID + ((lookingAt.getY() * WIDTH) + lookingAt.getX()));
				lookingAt = new Vector2I(-1, -1);
			}
		}, 0, 1);
	}

	public void playerLeftClick(Player player)
	{
		if(gameOver || lookingAt.getX() == -1 || lookingAt.getY() == -1)
			return;

		uncover(player, lookingAt.getX(), lookingAt.getY());
		if(hasWon())
		{
			gameOver = true;
			long time = System.currentTimeMillis() - startTime;
			long minutes = (time / 1000) / 60;
			long seconds = (time / 1000) % 60;
			player.sendRawMessage(ChatColor.GREEN + "You won in " + minutes + " minutes and " + seconds + " seconds!");
			Bukkit.getScheduler().scheduleSyncDelayedTask(VGCore.getPlugin(), () -> GameManager.leaveGame(player), 100);
		}
	}

	public void playerRightClick(Player player)
	{
		if(gameOver || lookingAt.getX() == -1 || lookingAt.getY() == -1)
			return;
		int index = lookingAt.getY() * WIDTH + lookingAt.getX();
		if(gameBoard[index] == FLAG_TILE_ID)
		{
			gameBoard[index] = UNMARKED_TILE_ID;
			setArmorStandHead(player, BLOCK_START_ID + index, new ItemStack(Material.LIGHT_GRAY_CONCRETE));
		}
		else if(gameBoard[index] == UNMARKED_TILE_ID)
		{
			gameBoard[index] = FLAG_TILE_ID;
			setArmorStandHead(player, BLOCK_START_ID + index, HEADS_MAPPINGS.get(-2).clone());
		}

		int flags = 0;
		for(int y = 0; y < HEIGHT; y++)
			for(int x = 0; x < WIDTH; x++)
				if(gameBoard[y * WIDTH + x] == FLAG_TILE_ID)
					flags++;
		flagsHologram.setText(ChatColor.RED + "" + (BOMBS - flags) + " flags left");
	}

	public void setGlowing(Player player, int entID)
	{
		sendMetaDataPacket(player, entID, (byte) (0x20 + 0x40));
	}

	public void setNotGlowing(Player player, int entID)
	{
		sendMetaDataPacket(player, entID, (byte) 0x20);
	}

	public void sendMetaDataPacket(Player player, int entID, byte infoByte)
	{
		WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
		dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), infoByte); // Invisible and Glowing
		dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(5, WrappedDataWatcher.Registry.get(Boolean.class)), true); // No Gravity
		dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(14, WrappedDataWatcher.Registry.get(Byte.class)), (byte) (0x08 + 0x10)); // No Base plate & marker

		WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata();
		metadataPacket.setMetadata(dataWatcher.getWatchableObjects());
		metadataPacket.setEntityID(entID);
		metadataPacket.sendPacket(player);
	}

	public void setArmorStandHead(Player player, int entID, ItemStack stack)
	{
		WrapperPlayServerEntityEquipment equipmentPacket = new WrapperPlayServerEntityEquipment();
		equipmentPacket.setEntityID(entID);
		equipmentPacket.setSlotStackPair(EnumWrappers.ItemSlot.HEAD, stack);
		equipmentPacket.sendPacket(player);
	}

	public void uncover(Player player, int x, int y)
	{
		int index = y * WIDTH + x;
		int entID = BLOCK_START_ID + index;
		if(gameBoard[index] == FLAG_TILE_ID)
			return;
		if(bombsLoc[index])
		{
			gameOver = true;
			player.sendRawMessage(ChatColor.RED + "You Lost!");
			World world = player.getWorld();
			Vector3I worldLoc = getGameLocScaled();
			Location entLoc = new Location(world, worldLoc.getX() + ((WIDTH - x) * BLOCK_WIDTH), worldLoc.getY() + ((HEIGHT - y) * BLOCK_HEIGHT) + 1, worldLoc.getZ() + ZDIST - 0.5);
			TNTPrimed tntPrimed = (TNTPrimed) world.spawnEntity(entLoc, EntityType.PRIMED_TNT);
			tntPrimed.setFuseTicks(20);
			tntPrimed.setGravity(false);
			tntPrimed.setIsIncendiary(false);
			tntPrimed.setVelocity(new Vector(0, 0, 0));
			for(int yy = 0; yy < HEIGHT; yy++)
			{
				for(int xx = 0; xx < WIDTH; xx++)
				{
					int index2 = yy * WIDTH + xx;
					if(bombsLoc[index2])
						setArmorStandHead(player, BLOCK_START_ID + index2, new ItemStack(Material.TNT));
				}
			}
			Bukkit.getScheduler().scheduleSyncDelayedTask(VGCore.getPlugin(), () -> GameManager.leaveGame(player), 100);
		}
		else if(gameBoard[index] == -1)
		{
			int bombsArround = 0;
			for(int yy = -1; yy < 2; yy++)
			{
				for(int xx = -1; xx < 2; xx++)
				{
					if(x + xx < 0 || x + xx >= WIDTH || y + yy < 0 || y + yy >= HEIGHT)
						continue;
					if(xx == 0 && yy == 0)
						continue;
					int i2 = (y + yy) * WIDTH + (x + xx);
					if(bombsLoc[i2])
						bombsArround++;
				}
			}

			gameBoard[index] = bombsArround;
			if(bombsArround == 0)
			{
				//TODO: SET ArmorStand
				setArmorStandHead(player, entID, new ItemStack(Material.WHITE_CONCRETE));
				for(int yy = -1; yy < 2; yy++)
				{
					for(int xx = -1; xx < 2; xx++)
					{
						if(x + xx < 0 || x + xx >= WIDTH || y + yy < 0 || y + yy >= HEIGHT)
							continue;
						if(xx == 0 && yy == 0)
							continue;
						int i2 = (y + yy) * WIDTH + (x + xx);
						if(gameBoard[i2] == UNMARKED_TILE_ID)
							this.uncover(player, x + xx, y + yy);
					}
				}
			}
			else
			{
				setArmorStandHead(player, entID, HEADS_MAPPINGS.get(bombsArround));
			}
		}
	}

	public boolean hasWon()
	{
		for(int y = 0; y < HEIGHT; y++)
		{
			for(int x = 0; x < WIDTH; x++)
			{
				int index = y * WIDTH + x;
				if(!bombsLoc[index] && (gameBoard[index] == UNMARKED_TILE_ID || gameBoard[index] == FLAG_TILE_ID))
					return false;
			}
		}
		return true;
	}

	@Override
	public void endGame(World world, Player player)
	{
		super.endGame(world, player);
		player.getInventory().clear();
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
		return (int) (WIDTH * BLOCK_WIDTH);
	}

	@Override
	public int getHeight()
	{
		return (int) (HEIGHT * BLOCK_HEIGHT);
	}
}
