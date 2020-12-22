package dev.theturkey.videogames.util;

import java.util.Objects;

public class Vector2D
{
	private double x;
	private double y;

	public Vector2D(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public void setY(double y)
	{
		this.y = y;
	}

	public void add(Vector2D vec)
	{
		this.x += vec.x;
		this.y += vec.y;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;

		Vector2D vector2I = (Vector2D) o;
		return x == vector2I.x && y == vector2I.y;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(x, y);
	}
}