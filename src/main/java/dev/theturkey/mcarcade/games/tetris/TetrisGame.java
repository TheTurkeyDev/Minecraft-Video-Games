package dev.theturkey.mcarcade.games.tetris;

import dev.theturkey.mcarcade.MCACore;
import dev.theturkey.mcarcade.games.GameManager;
import dev.theturkey.mcarcade.games.VideoGameBase;
import dev.theturkey.mcarcade.games.VideoGamesEnum;
import dev.theturkey.mcarcade.leaderboard.LeaderBoardManager;
import dev.theturkey.mcarcade.util.Hologram;
import dev.theturkey.mcarcade.util.TextToBannerHelper;
import dev.theturkey.mcarcade.util.Vector2I;
import dev.theturkey.mcarcade.util.Vector3I;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.awt.event.KeyEvent;

public class TetrisGame extends VideoGameBase
{
	private static final DyeColor TEXT_COLOR = DyeColor.WHITE;
	private static final DyeColor BG_COLOR = DyeColor.BLUE;
	public static int WIDTH = 12;
	public static int HEIGHT = 24;
	private static final int Y_BASE = 75;
	public static final int DIST_FROM_PLAYER = 20;

	private int[] scoreAwards = new int[]{0, 40, 100, 300, 1200};
	private int gameTick = -1;
	private int score = 0;
	private int level = 0;
	private int levelLineClears = 0;
	private int fallDelay = 20;
	private int fallDelayTick = fallDelay;

	private boolean wasSwapped = false;

	private Hologram scoreHologram;
	private Hologram levelHologram;

	private TetrisPiece nextPiece;
	private TetrisPiece fallingPiece;
	private TetrisPiece heldPiece = null;

	public TetrisGame(Vector2I gameLoc)
	{
		super(gameLoc, new Vector3I(gameLoc.getX(), Y_BASE - 8, gameLoc.getY()));
	}

	@Override
	public void constructGame(Player player)
	{
		level = 0;
		fallDelay = 20;
		fallDelayTick = fallDelay;
		Vector3I worldLoc = getGameLocScaled();
		fallingPiece = TetrisPiece.getRandomPiece();
		fallingPiece.place(worldLoc);
		nextPiece = TetrisPiece.getRandomPiece();
		nextPiece.place(new Vector3I(worldLoc).add(-12, -4, 0));

		Location playerLoc = getPlayerLoc();
		scoreHologram = new Hologram(playerLoc.clone().add(-3, -1.5, 5), ChatColor.RED + "Score: " + score);
		levelHologram = new Hologram(playerLoc.clone().add(-3, -1.75, 5), ChatColor.RED + "Level: " + level);

		for(int x = 0; x < getWidth(); x++)
		{
			for(int yy = 0; yy < getHeight(); yy++)
			{
				MCACore.gameWorld.getBlockAt(worldLoc.getX() + x, worldLoc.getY() + yy, worldLoc.getZ() + DIST_FROM_PLAYER + 1).setType(Material.BLACK_CONCRETE);
				if(x == 0 || x == getWidth() - 1 || yy == 0 || yy == getHeight() - 1)
					MCACore.gameWorld.getBlockAt(worldLoc.getX() + x, worldLoc.getY() + yy, worldLoc.getZ() + DIST_FROM_PLAYER).setType(Material.WHITE_CONCRETE);
			}
		}

		for(int x = 0; x < 6; x++)
		{
			for(int yy = 0; yy < 6; yy++)
			{
				MCACore.gameWorld.getBlockAt(worldLoc.getX() + x - 10, worldLoc.getY() + (getHeight() - 4 - yy), worldLoc.getZ() + DIST_FROM_PLAYER + 1).setType(Material.BLACK_CONCRETE);
				MCACore.gameWorld.getBlockAt(worldLoc.getX() + x + 15, worldLoc.getY() + (getHeight() - 4 - yy), worldLoc.getZ() + DIST_FROM_PLAYER + 1).setType(Material.BLACK_CONCRETE);
			}
		}

		Location loc = new Location(MCACore.gameWorld, worldLoc.getX() - 6, worldLoc.getY() + (getHeight() - 3), worldLoc.getZ() + DIST_FROM_PLAYER + 1);
		TextToBannerHelper.placeString("NEXT", loc, BlockFace.WEST, TEXT_COLOR, BG_COLOR);
		loc = new Location(MCACore.gameWorld, worldLoc.getX() + 18, worldLoc.getY() + (getHeight() - 3), worldLoc.getZ() + DIST_FROM_PLAYER + 1);
		TextToBannerHelper.placeString("HELD", loc, BlockFace.WEST, TEXT_COLOR, BG_COLOR);
	}

	@Override
	public void startGame(Player player)
	{
		super.startGame(player);
		score = 0;

		gameTick = Bukkit.getScheduler().scheduleSyncRepeatingTask(MCACore.getPlugin(), () ->
		{
			fallDelayTick--;
			if(fallDelayTick <= 0)
			{
				Vector3I gameScaled = getGameLocScaled();
				fallingPiece.clear(gameScaled);
				boolean moved = fallingPiece.fall(gameScaled);
				fallingPiece.place(gameScaled);
				if(!moved)
				{
					clearLines();
					NextPiece(gameScaled, player);
				}

				fallDelayTick = fallDelay;
			}
		}, 0, 1);
	}

	public void NextPiece(Vector3I gameScaled, Player player)
	{
		wasSwapped = false;
		nextPiece.clear(new Vector3I(gameScaled).add(-12, -4, 0));
		fallingPiece = nextPiece;
		nextPiece = TetrisPiece.getRandomPiece();
		nextPiece.place(new Vector3I(gameScaled).add(-12, -4, 0));
		if(fallingPiece.spawn(gameScaled))
		{
			Bukkit.getScheduler().cancelTask(gameTick);
			MCACore.sendMessage(player,ChatColor.RED + "GAME OVER!");
			MCACore.sendMessage(player,ChatColor.GREEN + "Your score: " + score);
			Thread t = new Thread(() -> LeaderBoardManager.addScore(player, score, getLeaderBoardKey()));
			t.start();
			Bukkit.getScheduler().scheduleSyncDelayedTask(MCACore.getPlugin(), () -> GameManager.leaveGame(player), 40);
		}
		else
		{
			fallingPiece.place(gameScaled);
		}
	}

	public void clearLines()
	{
		Vector3I gameScaled = getGameLocScaled();
		int z = gameScaled.getZ() + TetrisGame.DIST_FROM_PLAYER;
		int yIndex = 1;
		int lineClears = 0;
		while(yIndex < getHeight() - 2)
		{
			boolean lineClear = true;
			for(int i = 0; i < getWidth() - 2; i++)
			{
				Block b = MCACore.gameWorld.getBlockAt(gameScaled.getX() + 1 + i, gameScaled.getY() + yIndex, z);
				if(b.getType() == Material.AIR)
				{
					lineClear = false;
					break;
				}
			}

			yIndex++;
			if(lineClear)
			{
				lineClears++;
				yIndex--;
				shiftBoardDown(yIndex);
			}
		}

		score += (level + 1) * scoreAwards[lineClears];
		scoreHologram.setText(ChatColor.RED + "Score: " + score);

		this.levelLineClears += lineClears;
		if(levelLineClears > 10)
		{
			level++;
			fallDelay -= 2;
			this.levelLineClears = 0;
		}
		levelHologram.setText(ChatColor.RED + "Level: " + level);
	}

	public void shiftBoardDown(int lineStart)
	{
		Vector3I gameScaled = getGameLocScaled();
		int z = gameScaled.getZ() + TetrisGame.DIST_FROM_PLAYER;
		for(int j = lineStart; j < getHeight() - 1; j++)
		{
			for(int i = 0; i < getWidth() - 2; i++)
			{
				Block b = MCACore.gameWorld.getBlockAt(gameScaled.getX() + 1 + i, gameScaled.getY() + j, z);
				if(j == getHeight() - 2)
					b.setType(Material.AIR);
				else
					b.setType(MCACore.gameWorld.getBlockAt(gameScaled.getX() + 1 + i, gameScaled.getY() + 1 + j, z).getType());
			}
		}
	}

	@Override
	public void deconstructGame(Player player)
	{
		Vector3I gameLoc = getGameLocScaled();

		fallingPiece.clear(gameLoc);
		nextPiece.clear(new Vector3I(gameLoc).add(-12, -4, 0));
		if(heldPiece != null)
			heldPiece.clear(new Vector3I(gameLoc).add(12, -4, 0));
		scoreHologram.remove();
		levelHologram.remove();

		for(int x = 0; x < getWidth(); x++)
		{
			for(int yy = 0; yy < getHeight(); yy++)
			{
				MCACore.gameWorld.getBlockAt(gameLoc.getX() + x, gameLoc.getY() + yy, gameLoc.getZ() + DIST_FROM_PLAYER + 1).setType(Material.AIR);
				MCACore.gameWorld.getBlockAt(gameLoc.getX() + x, gameLoc.getY() + yy, gameLoc.getZ() + DIST_FROM_PLAYER).setType(Material.AIR);
			}
		}

		MCACore.gameWorld.getBlockAt(gameLoc.getX() - 6, gameLoc.getY() + (getHeight() - 3), gameLoc.getZ() + DIST_FROM_PLAYER + 1).setType(Material.AIR);
		MCACore.gameWorld.getBlockAt(gameLoc.getX() - 7, gameLoc.getY() + (getHeight() - 3), gameLoc.getZ() + DIST_FROM_PLAYER + 1).setType(Material.AIR);
		MCACore.gameWorld.getBlockAt(gameLoc.getX() - 8, gameLoc.getY() + (getHeight() - 3), gameLoc.getZ() + DIST_FROM_PLAYER + 1).setType(Material.AIR);
		MCACore.gameWorld.getBlockAt(gameLoc.getX() - 9, gameLoc.getY() + (getHeight() - 3), gameLoc.getZ() + DIST_FROM_PLAYER + 1).setType(Material.AIR);
		MCACore.gameWorld.getBlockAt(gameLoc.getX() + 18, gameLoc.getY() + (getHeight() - 3), gameLoc.getZ() + DIST_FROM_PLAYER + 1).setType(Material.AIR);
		MCACore.gameWorld.getBlockAt(gameLoc.getX() + 17, gameLoc.getY() + (getHeight() - 3), gameLoc.getZ() + DIST_FROM_PLAYER + 1).setType(Material.AIR);
		MCACore.gameWorld.getBlockAt(gameLoc.getX() + 16, gameLoc.getY() + (getHeight() - 3), gameLoc.getZ() + DIST_FROM_PLAYER + 1).setType(Material.AIR);
		MCACore.gameWorld.getBlockAt(gameLoc.getX() + 15, gameLoc.getY() + (getHeight() - 3), gameLoc.getZ() + DIST_FROM_PLAYER + 1).setType(Material.AIR);

		for(int x = 0; x < 6; x++)
		{
			for(int yy = 0; yy < 6; yy++)
			{
				MCACore.gameWorld.getBlockAt(gameLoc.getX() + x - 10, gameLoc.getY() + (getHeight() - 4 - yy), gameLoc.getZ() + DIST_FROM_PLAYER + 1).setType(Material.AIR);
				MCACore.gameWorld.getBlockAt(gameLoc.getX() + x + 15, gameLoc.getY() + (getHeight() - 4 - yy), gameLoc.getZ() + DIST_FROM_PLAYER + 1).setType(Material.BLACK_CONCRETE);
			}
		}
	}

	@Override
	public boolean isEntInGame(Entity entity)
	{
		return false;
	}

	@Override
	public void onKeyPress(Player player, int key)
	{
		Vector3I gameScaled = getGameLocScaled();
		fallingPiece.clear(gameScaled);
		switch(key)
		{
			case KeyEvent.VK_W:
				fallingPiece.rotate(gameScaled);
				break;
			case KeyEvent.VK_A:
				fallingPiece.move(gameScaled, 1);
				break;
			case KeyEvent.VK_S:
				fallDelayTick = 0;
				break;
			case KeyEvent.VK_D:
				fallingPiece.move(gameScaled, -1);
				break;
			case KeyEvent.VK_SPACE:
				fallingPiece.clear(gameScaled);
				while(fallingPiece.fall(gameScaled)) ;
				fallingPiece.place(gameScaled);
				clearLines();
				NextPiece(gameScaled, player);

				break;
			case KeyEvent.VK_SHIFT:
				if(wasSwapped)
					break;
				TetrisPiece temp = heldPiece;
				fallingPiece.clear(gameScaled);
				heldPiece = fallingPiece;
				heldPiece.resetGameLoc();

				if(temp == null)
				{
					NextPiece(gameScaled, player);
				}
				else
				{
					temp.clear(new Vector3I(gameScaled).add(12, -4, 0));
					fallingPiece = temp;
					fallingPiece.resetGameLoc();
					fallingPiece.spawn(gameScaled);
				}
				heldPiece.place(new Vector3I(gameScaled).add(12, -4, 0));
				wasSwapped = true;
				break;
		}
		fallingPiece.place(gameScaled);
	}

	@Override
	public void playerLeftClick(Player player)
	{

	}

	@Override
	public void playerRightClick(Player player)
	{

	}

	@Override
	public int getYBase()
	{
		return Y_BASE;
	}

	public int getWidth()
	{
		return WIDTH;
	}

	public int getHeight()
	{
		return HEIGHT;
	}

	@Override
	public void onEntityCollide(Entity entity)
	{

	}


	@Override
	public VideoGamesEnum getGameType()
	{
		return VideoGamesEnum.TETRIS;
	}

	public static final String LEADER_BOARD_ID = "mcvg_" + VideoGamesEnum.TETRIS.name().toLowerCase();

	@Override
	public String getLeaderBoardKey()
	{
		return LEADER_BOARD_ID;
	}
}
