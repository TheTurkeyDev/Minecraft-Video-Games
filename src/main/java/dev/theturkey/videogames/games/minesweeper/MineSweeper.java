package dev.theturkey.videogames.games.minesweeper;

import dev.theturkey.videogames.VGCore;
import dev.theturkey.videogames.games.GameManager;
import dev.theturkey.videogames.games.VideoGameBase;
import dev.theturkey.videogames.games.VideoGamesEnum;
import dev.theturkey.videogames.leaderboard.LeaderBoardManager;
import dev.theturkey.videogames.util.FakeArmorStandUtil;
import dev.theturkey.videogames.util.FakeHologram;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MineSweeper extends VideoGameBase
{
	private static final int Y_BASE = 150;
	private static final int ZDIST = 15;
	private static final float BLOCK_WIDTH = 0.6f;
	private static final float BLOCK_HEIGHT = 0.6f;

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

	private MinesweeperDifficulty difficulty;
	private int[] gameBoard;
	private boolean[] bombsLoc;
	private int BLOCK_START_ID;

	private Vector2I lookingAt = new Vector2I(0, 0);

	//If time stays in the way move it to the action bar?
	private Hologram timeHologram;
	private Hologram flagsHologram;
	private Hologram difficultyHologram;
	private Map<Integer, FakeHologram> fakeEnts = new HashMap<>();

	private int gameTick = -1;
	private boolean gameOver = false;
	private long startTime = 0;

	public MineSweeper(Vector2I gameLoc, MinesweeperDifficulty difficulty)
	{
		super(gameLoc, new Vector3I(gameLoc.getX(), (int) (Y_BASE - ((difficulty.getHeight() / 2) * BLOCK_HEIGHT)), gameLoc.getY()));
		this.difficulty = difficulty;
		this.gameBoard = new int[difficulty.getWidth() * difficulty.getHeight()];
		this.bombsLoc = new boolean[difficulty.getWidth() * difficulty.getHeight()];
		this.BLOCK_START_ID = Integer.MAX_VALUE - (difficulty.getWidth() * difficulty.getHeight());
	}

	@Override
	public void constructGame(Player player)
	{
		Vector3I worldLoc = getGameLocScaled();

		Location playerLoc = getPlayerLoc();
		timeHologram = new Hologram(playerLoc.clone().add(5, -2, 5), ChatColor.RED + "Time: 0 seconds");
		difficultyHologram = new Hologram(playerLoc.clone().add(5, -1.5, 5), ChatColor.RED + "Difficulty: " + difficulty.getName());
		flagsHologram = new Hologram(playerLoc.clone().add(-3, -2, 5), ChatColor.RED + "" + difficulty.getBombs() + " flags left");

		for(int y = 0; y < difficulty.getHeight(); y++)
		{
			for(int x = 0; x < difficulty.getWidth(); x++)
			{
				int entID = BLOCK_START_ID + ((y * difficulty.getWidth()) + x);
				Location entLoc = new Location(VGCore.gameWorld, worldLoc.getX() + ((difficulty.getWidth() - x) * BLOCK_WIDTH), worldLoc.getY() + ((difficulty.getHeight() - y) * BLOCK_HEIGHT), worldLoc.getZ() + ZDIST);

				fakeEnts.put(entID, new FakeHologram(entID, entLoc));
			}
		}

		FakeArmorStandUtil.send(player, new ArrayList<>(fakeEnts.values()));
	}

	@Override
	public void deconstructGame(Player player)
	{
		int[] ents = new int[difficulty.getWidth() * difficulty.getHeight()];
		for(int y = 0; y < difficulty.getHeight(); y++)
		{
			for(int x = 0; x < difficulty.getWidth(); x++)
			{
				int oneDIndex = (y * difficulty.getWidth()) + x;
				ents[oneDIndex] = BLOCK_START_ID + oneDIndex;
			}
		}
		// Remove entities
		FakeArmorStandUtil.removeArmorStands(player, ents);
		timeHologram.remove();
		flagsHologram.remove();
		difficultyHologram.remove();
	}

	@Override
	public void startGame(Player player)
	{
		super.startGame(player);
		startTime = System.currentTimeMillis();
		player.getInventory().setItem(0, new ItemStack(Material.STICK));

		player.sendRawMessage(ChatColor.GREEN + "Right click to flag, Left click to reveal tile");
		player.sendRawMessage(ChatColor.GREEN + "You must have the stick in hand to flag tiles!");

		List<FakeHologram> headUpdate = new ArrayList<>();
		for(int y = 0; y < difficulty.getHeight(); y++)
		{
			for(int x = 0; x < difficulty.getWidth(); x++)
			{
				int index = (y * difficulty.getWidth()) + x;
				gameBoard[index] = UNMARKED_TILE_ID;
				FakeHologram hologram = fakeEnts.get(BLOCK_START_ID + index);
				hologram.setHeadItem(new ItemStack(Material.WHITE_CONCRETE));
				headUpdate.add(hologram);
			}
		}
		FakeArmorStandUtil.updateArmor(player, headUpdate);

		for(int i = 0; i < difficulty.getBombs(); i++)
		{
			int x = VGCore.RAND.nextInt(difficulty.getWidth());
			int y = VGCore.RAND.nextInt(difficulty.getHeight());

			int index = y * difficulty.getWidth() + x;

			if(!bombsLoc[index])
				bombsLoc[index] = true;
			else
				i--;
		}

		gameTick = Bukkit.getScheduler().scheduleSyncRepeatingTask(VGCore.getPlugin(), () ->
		{
			if(gameOver)
				return;
			timeHologram.setText(ChatColor.RED + "Time: " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds");
			Location playerLoc = player.getLocation();
			float yaw = playerLoc.getYaw();
			float pitch = playerLoc.getPitch();
			double boardX = ZDIST * Math.tan(Math.toRadians(yaw));
			double boardY = ZDIST * Math.tan(Math.toRadians(pitch));

			int x = (int) (((boardX / BLOCK_WIDTH) + (difficulty.getWidth() / 2)) - 0.3);
			int y = (int) ((boardY / BLOCK_HEIGHT) + (difficulty.getHeight() / 2)) - 1;

			if(x >= 0 && x < difficulty.getWidth() && y >= 0 && y < difficulty.getHeight())
			{
				if(lookingAt.getX() != x || lookingAt.getY() != y)
				{
					List<FakeHologram> toUpdate = new ArrayList<>();
					if(lookingAt.getX() != -1 && lookingAt.getY() != -1)
					{
						FakeHologram hologram = fakeEnts.get(BLOCK_START_ID + ((lookingAt.getY() * difficulty.getWidth()) + lookingAt.getX()));
						hologram.setGlowing(false);
						toUpdate.add(hologram);
					}
					FakeHologram hologram = fakeEnts.get(BLOCK_START_ID + ((y * difficulty.getWidth()) + x));
					hologram.setGlowing(true);
					toUpdate.add(hologram);
					FakeArmorStandUtil.updateMeta(player, toUpdate);
					lookingAt = new Vector2I(x, y);
				}
			}
			else
			{
				if(lookingAt.getX() != -1 && lookingAt.getY() != -1)
				{
					FakeHologram hologram = fakeEnts.get(BLOCK_START_ID + ((lookingAt.getY() * difficulty.getWidth()) + lookingAt.getX()));
					hologram.setGlowing(false);
					FakeArmorStandUtil.updateMeta(player, hologram);
				}
				lookingAt = new Vector2I(-1, -1);
			}
		}, 0, 1);
	}

	public void playerLeftClick(Player player)
	{
		if(gameOver || lookingAt.getX() == -1 || lookingAt.getY() == -1)
			return;

		FakeArmorStandUtil.updateArmor(player, uncover(player, lookingAt.getX(), lookingAt.getY()));
		if(hasWon())
		{
			gameOver = true;
			long time = System.currentTimeMillis() - startTime;
			long minutes = (time / 1000) / 60;
			long seconds = (time / 1000) % 60;
			player.sendRawMessage(ChatColor.GREEN + "You won in " + minutes + " minutes and " + seconds + " seconds!");
			Thread t = new Thread(() -> LeaderBoardManager.addScore(player, time, getLeaderBoardKey()));
			t.start();
			Bukkit.getScheduler().scheduleSyncDelayedTask(VGCore.getPlugin(), () -> GameManager.leaveGame(player), 100);
		}
	}

	public void playerRightClick(Player player)
	{
		if(gameOver || lookingAt.getX() == -1 || lookingAt.getY() == -1)
			return;
		int index = lookingAt.getY() * difficulty.getWidth() + lookingAt.getX();
		if(gameBoard[index] == FLAG_TILE_ID)
		{
			gameBoard[index] = UNMARKED_TILE_ID;
			FakeHologram fakeHologram = fakeEnts.get(BLOCK_START_ID + index);
			fakeHologram.setHeadItem(new ItemStack(Material.WHITE_CONCRETE));
			fakeHologram.sendArmorStandHeadPacket(player);
		}
		else if(gameBoard[index] == UNMARKED_TILE_ID)
		{
			gameBoard[index] = FLAG_TILE_ID;
			FakeHologram fakeHologram = fakeEnts.get(BLOCK_START_ID + index);
			fakeHologram.setHeadItem(HEADS_MAPPINGS.get(-2).clone());
			fakeHologram.sendArmorStandHeadPacket(player);
		}

		int flags = 0;
		for(int y = 0; y < difficulty.getHeight(); y++)
			for(int x = 0; x < difficulty.getWidth(); x++)
				if(gameBoard[y * difficulty.getWidth() + x] == FLAG_TILE_ID)
					flags++;
		flagsHologram.setText(ChatColor.RED + "" + (difficulty.getBombs() - flags) + " flags left");
	}

	public List<FakeHologram> uncover(Player player, int x, int y)
	{
		List<FakeHologram> toUpdate = new ArrayList<>();
		int index = y * difficulty.getWidth() + x;
		int entID = BLOCK_START_ID + index;
		if(gameBoard[index] == FLAG_TILE_ID)
			return new ArrayList<>();
		if(bombsLoc[index])
		{
			gameOver = true;
			player.sendRawMessage(ChatColor.RED + "You Lost!");
			World world = player.getWorld();
			Vector3I worldLoc = getGameLocScaled();
			Location entLoc = new Location(world, worldLoc.getX() + ((difficulty.getWidth() - x) * BLOCK_WIDTH), worldLoc.getY() + ((difficulty.getHeight() - y) * BLOCK_HEIGHT) + 1, worldLoc.getZ() + ZDIST - 0.5);
			TNTPrimed tntPrimed = (TNTPrimed) world.spawnEntity(entLoc, EntityType.PRIMED_TNT);
			tntPrimed.setFuseTicks(20);
			tntPrimed.setGravity(false);
			tntPrimed.setIsIncendiary(false);
			tntPrimed.setVelocity(new Vector(0, 0, 0));
			for(int yy = 0; yy < difficulty.getHeight(); yy++)
			{
				for(int xx = 0; xx < difficulty.getWidth(); xx++)
				{
					int index2 = yy * difficulty.getWidth() + xx;
					if(bombsLoc[index2])
					{
						FakeHologram fakeHologram = fakeEnts.get(BLOCK_START_ID + index2);
						fakeHologram.setHeadItem(new ItemStack(Material.TNT));
						toUpdate.add(fakeHologram);
					}
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
					if(x + xx < 0 || x + xx >= difficulty.getWidth() || y + yy < 0 || y + yy >= difficulty.getHeight())
						continue;
					if(xx == 0 && yy == 0)
						continue;
					int i2 = (y + yy) * difficulty.getWidth() + (x + xx);
					if(bombsLoc[i2])
						bombsArround++;
				}
			}

			gameBoard[index] = bombsArround;
			if(bombsArround == 0)
			{
				//TODO: SET ArmorStand
				FakeHologram fakeHologram = fakeEnts.get(entID);
				fakeHologram.setHeadItem(new ItemStack(Material.LIGHT_GRAY_CONCRETE));
				toUpdate.add(fakeHologram);
				for(int yy = -1; yy < 2; yy++)
				{
					for(int xx = -1; xx < 2; xx++)
					{
						if(x + xx < 0 || x + xx >= difficulty.getWidth() || y + yy < 0 || y + yy >= difficulty.getHeight())
							continue;
						if(xx == 0 && yy == 0)
							continue;
						int i2 = (y + yy) * difficulty.getWidth() + (x + xx);
						if(gameBoard[i2] == UNMARKED_TILE_ID)
							toUpdate.addAll(this.uncover(player, x + xx, y + yy));
					}
				}
			}
			else
			{
				FakeHologram fakeHologram = fakeEnts.get(entID);
				fakeHologram.setHeadItem(HEADS_MAPPINGS.get(bombsArround).clone());
				toUpdate.add(fakeHologram);
			}
		}
		return toUpdate;
	}

	public boolean hasWon()
	{
		for(int y = 0; y < difficulty.getHeight(); y++)
		{
			for(int x = 0; x < difficulty.getWidth(); x++)
			{
				int index = y * difficulty.getWidth() + x;
				if(!bombsLoc[index] && (gameBoard[index] == UNMARKED_TILE_ID || gameBoard[index] == FLAG_TILE_ID))
					return false;
			}
		}
		return true;
	}

	@Override
	public void endGame(Player player)
	{
		super.endGame(player);
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
	public void onKeyPress(Player player, int key)
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
		return (int) (difficulty.getWidth() * BLOCK_WIDTH);
	}

	@Override
	public int getHeight()
	{
		return (int) (difficulty.getHeight() * BLOCK_HEIGHT);
	}

	@Override
	public VideoGamesEnum getGameType()
	{
		return VideoGamesEnum.MINESWEEPER;
	}

	@Override
	public String getLeaderBoardKey()
	{
		return difficulty.getLeaderBoardKey();
	}
}
