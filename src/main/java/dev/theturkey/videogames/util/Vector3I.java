package dev.theturkey.videogames.util;

import java.util.Objects;

public class Vector3I
{
	private int x;
	private int y;
	private int z;

	public Vector3I(Vector3I vec)
	{
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
	}

	public Vector3I(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public int getZ()
	{
		return z;
	}

	public Vector3I add(int x, int y, int z)
	{
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;

		Vector3I vector2I = (Vector3I) o;
		return x == vector2I.x && y == vector2I.y && z == vector2I.z;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(x, y, z);
	}
}
