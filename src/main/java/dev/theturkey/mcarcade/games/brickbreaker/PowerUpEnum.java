package dev.theturkey.mcarcade.games.brickbreaker;

import org.bukkit.Material;

public enum PowerUpEnum
{
	STICKY_PADDLE(Material.SLIME_BALL),
	MULTI_BALL(Material.BEETROOT_SEEDS),
	PADDLE_SHRINK(Material.SHULKER_SHELL),
	PADDLE_GROW(Material.SHULKER_BOX);

	private Material powerUpMat;

	PowerUpEnum(Material material)
	{
		this.powerUpMat = material;
	}

	public Material getPowerUpMat()
	{
		return powerUpMat;
	}
}
