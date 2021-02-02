package dev.theturkey.videogames.games.tetris;

import dev.theturkey.videogames.VGCore;
import dev.theturkey.videogames.util.Vector2I;
import dev.theturkey.videogames.util.Vector3I;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TetrisPiece
{
	private static final List<TetrisPiece> PIECES = Arrays.asList(
			//T
			new TetrisPiece(true, Material.PURPLE_WOOL,
					new Vector2I(0, 0),
					new Vector2I(-1, 0),
					new Vector2I(1, 0),
					new Vector2I(0, 1)
			),
			//Z
			new TetrisPiece(true, Material.LIME_WOOL,
					new Vector2I(0, 0),
					new Vector2I(1, 0),
					new Vector2I(0, -1),
					new Vector2I(-1, -1)
			),
			//Z-Reverse
			new TetrisPiece(true, Material.RED_WOOL,
					new Vector2I(0, 0),
					new Vector2I(-1, 0),
					new Vector2I(0, -1),
					new Vector2I(1, -1)
			),
			//L
			new TetrisPiece(true, Material.BLUE_WOOL,
					new Vector2I(1, 0),
					new Vector2I(0, 0),
					new Vector2I(-1, 0),
					new Vector2I(1, -1)
			),
			//L-Reverse
			new TetrisPiece(true, Material.ORANGE_WOOL,
					new Vector2I(1, 0),
					new Vector2I(0, 0),
					new Vector2I(-1, 0),
					new Vector2I(-1, -1)
			),
			//Line
			new TetrisPiece(false, Material.DIAMOND_BLOCK,
					new Vector2I(1, 0),
					new Vector2I(0, 0),
					new Vector2I(-1, 0),
					new Vector2I(-2, 0)
			),
			//Block
			new TetrisPiece(false, Material.YELLOW_WOOL,
					new Vector2I(0, 0),
					new Vector2I(-1, 0),
					new Vector2I(0, -1),
					new Vector2I(-1, -1)
			)
	);

	public static TetrisPiece getRandomPiece()
	{
		return PIECES.get(VGCore.RAND.nextInt(PIECES.size())).clone();
	}

	private Vector2I gameLoc;
	private List<Vector2I> blockLocs = new ArrayList<>();
	private boolean blockRot;
	private Material material;

	private TetrisPiece(boolean blockRot, Material material, Vector2I... locs)
	{
		this(blockRot, material, Arrays.asList(locs));
	}

	private TetrisPiece(boolean blockRot, Material material, List<Vector2I> locs)
	{
		this.blockRot = blockRot;
		this.material = material;
		blockLocs.addAll(locs);
		resetGameLoc();
	}

	public TetrisPiece clone()
	{
		return new TetrisPiece(blockRot, material, blockLocs.stream().map(Vector2I::new).collect(Collectors.toList()));
	}

	public void rotate(Vector3I gameLocScaled)
	{
		for(Vector2I loc : blockLocs)
			loc.set(loc.getY(), -loc.getX());
		if(!blockRot)
		{
			for(Vector2I loc : blockLocs)
				loc.set(loc.getX(), loc.getY() - 1);
		}


		Vector2I tempLoc = new Vector2I(gameLoc);
		int amount = 0;
		while(!isValidX(tempLoc.getX() + amount))
		{
			if(amount > 0)
				amount = -amount;
			else
				amount = -amount + 1;

			tempLoc.set(gameLoc.getX() + amount, gameLoc.getY());
		}
		gameLoc.add(amount, 0);
		if(!isValid(gameLocScaled))
		{
			gameLoc.add(-amount, 0);

			if(!blockRot)
			{
				for(Vector2I loc : blockLocs)
					loc.set(loc.getX(), loc.getY() + 1);
			}
			for(Vector2I loc : blockLocs)
				loc.set(-loc.getY(), loc.getX());

		}
	}

	public boolean isValidX(int gameLocX)
	{
		for(Vector2I loc : blockLocs)
			if(gameLocX + loc.getX() <= 0 || gameLocX + loc.getX() >= 11)
				return false;
		return true;
	}

	public boolean isValid(Vector3I gameLocScaled)
	{
		Location loc = new Location(VGCore.gameWorld, gameLocScaled.getX() + gameLoc.getX(), gameLocScaled.getY() + gameLoc.getY(), gameLocScaled.getZ() + TetrisGame.DIST_FROM_PLAYER);
		for(Vector2I vector2I : blockLocs)
			if(loc.clone().add(vector2I.getX(), vector2I.getY(), 0).getBlock().getType() != Material.AIR)
				return false;
		return true;
	}

	public void move(Vector3I gameLocScaled, int xAmount)
	{
		if(isValidX(gameLoc.getX() + xAmount))
		{
			gameLoc.add(xAmount, 0);
			if(!isValid(gameLocScaled))
				gameLoc.add(-xAmount, 0);
		}
	}

	public boolean fall(Vector3I gameLocScaled)
	{
		gameLoc.add(0, -1);
		if(!isValid(gameLocScaled))
		{
			gameLoc.add(0, 1);
			return false;
		}
		return true;
	}

	public void clear(Vector3I gameLocScaled)
	{
		Location loc = new Location(VGCore.gameWorld, gameLocScaled.getX() + gameLoc.getX(), gameLocScaled.getY() + gameLoc.getY(), gameLocScaled.getZ() + TetrisGame.DIST_FROM_PLAYER);
		for(Vector2I vector2I : blockLocs)
			loc.clone().add(vector2I.getX(), vector2I.getY(), 0).getBlock().setType(Material.AIR);
	}

	public boolean spawn(Vector3I gameLocScaled)
	{
		int shift = 0;
		for(Vector2I vector2I : blockLocs)
			if(vector2I.getY() > shift)
				shift = vector2I.getY();

		gameLoc.add(0, -shift);

		return !isValid(gameLocScaled);
	}

	public void place(Vector3I gameLocScaled)
	{
		Location loc = new Location(VGCore.gameWorld, gameLocScaled.getX() + gameLoc.getX(), gameLocScaled.getY() + gameLoc.getY(), gameLocScaled.getZ() + TetrisGame.DIST_FROM_PLAYER);

		for(Vector2I vector2I : blockLocs)
			loc.clone().add(vector2I.getX(), vector2I.getY(), 0).getBlock().setType(material);
	}

	public void resetGameLoc()
	{
		gameLoc = new Vector2I(5, 22);
	}
}
