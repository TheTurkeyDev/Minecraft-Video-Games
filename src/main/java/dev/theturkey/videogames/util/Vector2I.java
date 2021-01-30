package dev.theturkey.videogames.util;

import java.util.Objects;

public class Vector2I
{
	private int x;
	private int y;

	public Vector2I(Vector2I vec)
	{
		this.x = vec.getX();
		this.y = vec.getY();
	}

	public Vector2I(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public Vector2I set(int x, int y)
	{
		this.x = x;
		this.y = y;
		return this;
	}

	public Vector2I add(int x, int y)
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

		Vector2I vector2I = (Vector2I) o;
		return x == vector2I.x && y == vector2I.y;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(x, y);
	}
}
