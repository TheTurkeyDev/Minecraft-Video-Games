package dev.theturkey.videogames.games.minesweeper;

import dev.theturkey.videogames.games.VideoGamesEnum;

public class MinesweeperDifficulty
{
	public static final MinesweeperDifficulty EASY = new MinesweeperDifficulty("Easy", 9, 9, 10);
	public static final MinesweeperDifficulty MEDIUM = new MinesweeperDifficulty("Medium", 16, 16, 40);
	public static final MinesweeperDifficulty HARD = new MinesweeperDifficulty("Hard", 30, 16, 99);

	private String name;
	private int width;
	private int height;
	private int bombs;

	public MinesweeperDifficulty(String name, int width, int height, int bombs)
	{
		this.name = name;
		this.width = width;
		this.height = height;
		this.bombs = bombs;
	}

	public String getName()
	{
		return name;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public int getBombs()
	{
		return bombs;
	}

	public String getLeaderBoardKey()
	{
		return "mcvg_" + VideoGamesEnum.MINESWEEPER.name().toLowerCase() + "_" + getName().toLowerCase();
	}
}
