package dev.theturkey.mcarcade.util;

import java.util.Objects;

public class Vector2D
{
	private double x;
	private double y;

	public Vector2D(Vector2D vec)
	{
		this.x = vec.getX();
		this.y = vec.getY();
	}

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

	public void set(double x, double y)
	{
		setX(x);
		setY(y);
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

	public Vector2D add(double x, double y)
	{
		this.x += x;
		this.y += y;
		return this;
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

	@Override
	public String toString()
	{
		return "Vector2D{" +
				"x=" + x +
				", y=" + y +
				'}';
	}
}
