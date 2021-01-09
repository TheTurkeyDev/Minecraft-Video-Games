package dev.theturkey.videogames.games.brickbreaker;

import dev.theturkey.videogames.VGCore;
import dev.theturkey.videogames.util.Vector2D;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;

public class Ball
{
	private static final double RIGHT_WALL_BOUNCE = BrickBreakerGame.WIDTH - 1.25;
	private Vector2D ballWorldOffset;
	private Vector2D ballLoc;
	private Vector2D ballVel;
	private Slime ballEnt;
	private boolean paddleCheck = false;
	private boolean ballGrabbed;

	public Ball(World world, double x, double y, double z)
	{
		ballGrabbed = true;
		ballWorldOffset = new Vector2D(x, y);
		ballLoc = new Vector2D((BrickBreakerGame.WIDTH / 2d) - 0.5, BrickBreakerGame.PADDLE_Y + 0.75);
		ballVel = new Vector2D(0, 0);
		Location startLoc = new Location(world, getWorldX(), getWorldY(), z);
		ballEnt = (Slime) world.spawnEntity(startLoc, EntityType.SLIME);
		ballEnt.setSize(1);
		ballEnt.setAI(false);
		ballEnt.setGravity(false);
	}

	public Ball(Ball ball, double z)
	{
		ballGrabbed = ball.isBallGrabbed();
		ballWorldOffset = new Vector2D(ball.ballWorldOffset);
		ballLoc = new Vector2D(ball.ballLoc);
		ballVel = new Vector2D(ball.ballVel).add(VGCore.RAND.nextDouble() - 0.5, 0);
		Location startLoc = new Location(ball.getEntity().getWorld(), getWorldX(), getWorldY(), z);
		ballEnt = (Slime) ball.getEntity().getWorld().spawnEntity(startLoc, EntityType.SLIME);
		ballEnt.setSize(1);
		ballEnt.setAI(false);
		ballEnt.setGravity(false);
	}

	public boolean update(double paddleX, float paddleWidth)
	{
		ballLoc.add(ballVel);
		if(getGameX() > RIGHT_WALL_BOUNCE)
		{
			ballLoc.setX(RIGHT_WALL_BOUNCE - (getGameX() - RIGHT_WALL_BOUNCE));
			ballVel.setX(-Math.abs(ballVel.getX()));
		}
		else if(getGameX() < 1.25)
		{
			ballLoc.setX(Math.abs(getGameX()));
			ballVel.setX(Math.abs(ballVel.getX()));
		}

		if(getGameY() > BrickBreakerGame.HEIGHT - 2)
		{
			ballLoc.setY(getGameY() - (getGameY() - (BrickBreakerGame.HEIGHT - 2)));
			bounceY();
		}
		else if(!paddleCheck && getGameY() < BrickBreakerGame.PADDLE_Y + 0.75f)
		{
			paddleCheck = true;
			double xOff = (getGameX() + 0.5) - (paddleX - (paddleWidth / 2));
			if(xOff > 0 && xOff < paddleWidth)
			{
				bounceY();
				double xChange = 8 * Math.pow((xOff / paddleWidth) - 0.5, 3);
				ballVel.setX(ballVel.getX() + xChange);
				updateLoc();
				return true;
			}
		}

		updateLoc();
		return false;
	}

	public void updateLoc()
	{
		Location loc = ballEnt.getLocation().clone();
		loc.setX(getWorldX());
		loc.setY(getWorldY());
		ballEnt.teleport(loc);
	}

	public void bounceY()
	{
		ballVel.setY(-ballVel.getY());
		if(ballVel.getY() < 0)
			paddleCheck = false;
	}

	public void setGameX(double x)
	{
		this.ballLoc.setX(x);
	}

	public void setGameY(double y)
	{
		this.ballLoc.setY(y);
	}

	public void setVelocity(double x, double y)
	{
		this.ballVel.set(x, y);
	}

	public double getWorldX()
	{
		return ballWorldOffset.getX() - getGameX();
	}

	public double getWorldY()
	{
		return ballWorldOffset.getY() + getGameY();
	}

	public double getGameY()
	{
		return ballLoc.getY();
	}

	public double getGameX()
	{
		return ballLoc.getX();
	}

	public Slime getEntity()
	{
		return ballEnt;
	}

	public void remove()
	{
		ballEnt.remove();
	}

	public boolean isBallGrabbed()
	{
		return ballGrabbed;
	}

	public void setBallGrabbed(boolean grabbed)
	{
		this.ballGrabbed = grabbed;
	}
}
