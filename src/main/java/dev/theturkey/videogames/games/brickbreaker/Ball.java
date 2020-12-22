package dev.theturkey.videogames.games.brickbreaker;

import dev.theturkey.videogames.util.Vector2D;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;

public class Ball
{
	private Vector2D ballLoc;
	private Vector2D ballVel;
	private Slime ballEnt;

	public Ball(World world, double x, double y, double z)
	{
		ballLoc = new Vector2D(x, y);
		ballVel = new Vector2D(0, 0);
		ballEnt = (Slime) world.spawnEntity(new Location(world, ballLoc.getX(), ballLoc.getY(), z), EntityType.SLIME);
		ballEnt.setSize(1);
		ballEnt.setAI(false);
		ballEnt.setGravity(false);
	}

	public void remove()
	{
		ballEnt.remove();
	}
}
