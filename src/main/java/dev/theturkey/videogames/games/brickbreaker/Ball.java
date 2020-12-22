package dev.theturkey.videogames.games.brickbreaker;

import dev.theturkey.videogames.util.Vector2D;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;

public class Ball
{
	private Vector2D ballWorldOffset;
	private Vector2D ballLoc;
	private Vector2D ballVel;
	private Slime ballEnt;
	private double paddleY;
	private boolean paddleCheck = false;

	public Ball(World world, double x, double y, double z)
	{
		this.paddleY = y;
		ballWorldOffset = new Vector2D(x, y);
		ballLoc = new Vector2D(0, 0);
		ballVel = new Vector2D(0, 0.5);
		ballEnt = (Slime) world.spawnEntity(new Location(world, ballWorldOffset.getX(), ballWorldOffset.getY(), z), EntityType.SLIME);
		ballEnt.setSize(1);
		ballEnt.setAI(false);
		ballEnt.setGravity(false);
	}

	public void update(double paddleX, float paddleWidth)
	{
		ballLoc.add(ballVel);
		if(ballLoc.getX() > 9)
		{
			ballLoc.setX(ballLoc.getX() - (ballLoc.getX() - 9));
			ballVel.setX(-ballVel.getX());
		}
		else if(ballLoc.getX() < -8)
		{
			ballLoc.setX(ballLoc.getX() - (ballLoc.getX() + 9));
			ballVel.setX(-ballVel.getX());
		}

		if(ballLoc.getY() > 28)
		{
			ballLoc.setY(ballLoc.getY() - (ballLoc.getY() - 28));
			bounceY();
		}
		else if(ballLoc.getY() < -3)
		{
			ballLoc.setY(ballLoc.getY() - (ballLoc.getY() + 3));
			bounceY();
		}
		else if(!paddleCheck && getY() < (paddleY + 0.75f))
		{
			paddleCheck = true;
			double xOff = (ballLoc.getX() - 0.5) - (paddleX - (paddleWidth / 2));
			System.out.println(xOff);
			if(xOff > 0 && xOff < paddleWidth)
			{
				bounceY();
				double xChange = 8 * Math.pow((xOff / paddleWidth) - 0.5, 3);
				ballVel.setX(ballVel.getX() + xChange);
			}
		}

		Location loc = ballEnt.getLocation().clone();
		loc.setX(getX());
		loc.setY(getY());
		ballEnt.teleport(loc);
	}

	public void bounceY()
	{
		ballVel.setY(-ballVel.getY());
		if(ballVel.getY() < 0)
			paddleCheck = false;
	}

	public double getX()
	{
		return ballLoc.getX() + ballWorldOffset.getX();
	}

	public double getY()
	{
		return ballLoc.getY() + ballWorldOffset.getY();
	}

	public Slime getEntity()
	{
		return ballEnt;
	}

	public void remove()
	{
		ballEnt.remove();
	}
}
