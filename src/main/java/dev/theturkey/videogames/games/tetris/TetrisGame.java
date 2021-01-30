package dev.theturkey.videogames.games.tetris;

import dev.theturkey.videogames.VGCore;
import dev.theturkey.videogames.games.GameManager;
import dev.theturkey.videogames.games.VideoGameBase;
import dev.theturkey.videogames.games.VideoGamesEnum;
import dev.theturkey.videogames.util.Hologram;
import dev.theturkey.videogames.util.Vector2I;
import dev.theturkey.videogames.util.Vector3I;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.awt.event.KeyEvent;

public class TetrisGame extends VideoGameBase
{
	public static int WIDTH = 12;
	public static int HEIGHT = 24;
	private static final int Y_BASE = 75;
	public static final int DIST_FROM_PLAYER = 20;

	private int[] scoreAwards = new int[]{0, 40, 100, 300, 1200};
	private int gameTick = -1;
	private int score = 0;
	private int fallDelay = 20;
	private int fallDelayTick = fallDelay;
	private Hologram scoreHologram;

	private TetrisPiece fallingPiece;

	public TetrisGame(Vector2I gameLoc)
	{
		super(gameLoc, new Vector3I(gameLoc.getX(), Y_BASE - 8, gameLoc.getY()));
	}

	@Override
	public void constructGame(Player player)
	{
		Vector3I worldLoc = getGameLocScaled();
		fallingPiece = TetrisPiece.getRandomPiece();
		fallingPiece.place(worldLoc);

		Location playerLoc = getPlayerLoc();
		scoreHologram = new Hologram(playerLoc.clone().add(-3, -1.5, 5), ChatColor.RED + "Score: " + score);

		for(int x = 0; x < getWidth(); x++)
		{
			for(int yy = 0; yy < getHeight(); yy++)
			{
				VGCore.gameWorld.getBlockAt(worldLoc.getX() + x, worldLoc.getY() + yy, worldLoc.getZ() + DIST_FROM_PLAYER + 1).setType(Material.BLACK_CONCRETE);
				if(x == 0 || x == getWidth() - 1 || yy == 0 || yy == getHeight() - 1)
					VGCore.gameWorld.getBlockAt(worldLoc.getX() + x, worldLoc.getY() + yy, worldLoc.getZ() + DIST_FROM_PLAYER).setType(Material.WHITE_CONCRETE);
			}
		}

		for(int x = 0; x < 6; x++)
		{
			for(int yy = 0; yy < 6; yy++)
			{
				VGCore.gameWorld.getBlockAt(worldLoc.getX() + x - 10, worldLoc.getY() + (getHeight() - 4 - yy), worldLoc.getZ() + DIST_FROM_PLAYER + 1).setType(Material.BLACK_CONCRETE);
			}
		}
	}

	@Override
	public void startGame(Player player)
	{
		super.startGame(player);
		score = 0;

		gameTick = Bukkit.getScheduler().scheduleSyncRepeatingTask(VGCore.getPlugin(), () ->
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

					fallingPiece = TetrisPiece.getRandomPiece();
					if(fallingPiece.spawn(gameScaled))
					{
						player.sendRawMessage(ChatColor.RED + "GAME OVER!");
						player.sendRawMessage(ChatColor.GREEN + "Your score: " + score);
						GameManager.leaveGame(player);
					}
					else
					{
						fallingPiece.place(gameScaled);
					}
				}

				fallDelayTick = fallDelay;
			}
		}, 0, 1);
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
				Block b = VGCore.gameWorld.getBlockAt(gameScaled.getX() + 1 + i, gameScaled.getY() + yIndex, z);
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

		score += scoreAwards[lineClears];
		scoreHologram.setText(ChatColor.RED + "Score: " + score);
	}

	public void shiftBoardDown(int lineStart)
	{
		Vector3I gameScaled = getGameLocScaled();
		int z = gameScaled.getZ() + TetrisGame.DIST_FROM_PLAYER;
		for(int j = lineStart; j < getHeight() - 1; j++)
		{
			for(int i = 0; i < getWidth() - 2; i++)
			{
				Block b = VGCore.gameWorld.getBlockAt(gameScaled.getX() + 1 + i, gameScaled.getY() + j, z);
				if(j == getHeight() - 2)
					b.setType(Material.AIR);
				else
					b.setType(VGCore.gameWorld.getBlockAt(gameScaled.getX() + 1 + i, gameScaled.getY() + 1 + j, z).getType());
			}
		}
	}

	@Override
	public void endGame(Player player)
	{
		super.endGame(player);
		Bukkit.getScheduler().cancelTask(gameTick);
	}

	@Override
	public void deconstructGame(Player player)
	{
		Vector3I gameLoc = getGameLocScaled();

		fallingPiece.clear(gameLoc);
		scoreHologram.remove();

		for(int x = 0; x < getWidth(); x++)
		{
			for(int yy = 0; yy < getHeight(); yy++)
			{
				VGCore.gameWorld.getBlockAt(gameLoc.getX() + x, gameLoc.getY() + yy, gameLoc.getZ() + DIST_FROM_PLAYER + 1).setType(Material.AIR);
				VGCore.gameWorld.getBlockAt(gameLoc.getX() + x, gameLoc.getY() + yy, gameLoc.getZ() + DIST_FROM_PLAYER).setType(Material.AIR);
			}
		}

		for(int x = 0; x < 6; x++)
		{
			for(int yy = 0; yy < 6; yy++)
			{
				VGCore.gameWorld.getBlockAt(gameLoc.getX() + x - 10, gameLoc.getY() + (getHeight() - 4 - yy), gameLoc.getZ() + DIST_FROM_PLAYER + 1).setType(Material.AIR);
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
				player.sendRawMessage("Space PRESSED!");
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
