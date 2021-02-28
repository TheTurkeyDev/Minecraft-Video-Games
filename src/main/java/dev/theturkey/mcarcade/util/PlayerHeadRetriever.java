package dev.theturkey.mcarcade.util;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

public class PlayerHeadRetriever
{

	// retrieve the ItemStack from a base64-encoded String-value
	// for example from minecraft-heads.com -> Value
	public static ItemStack makeSkull(String base64Encoded)
	{
		final ItemStack stack = new ItemStack(Material.PLAYER_HEAD);
		stack.setItemMeta(createSkullMeta(base64Encoded));
		return stack;
	}

	private static ItemMeta createSkullMeta(String value)
	{
		try
		{
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			final DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(out)));
			write(dos, value);
			dos.close();
			final String internal = Base64.getEncoder().encodeToString(out.toByteArray());
			final Map<String, Object> map = new HashMap<>();
			map.put("internal", internal);
			map.put("meta-type", "SKULL");
			map.put("==", "ItemMeta");
			return (ItemMeta) ConfigurationSerialization.deserializeObject(map);
		} catch(IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private static final int TYPE_COMPOUND = 10;
	private static final int TYPE_LIST = 9;
	private static final int TYPE_STRING = 8;
	private static final int END_MARK = 0;

	private static void write(DataOutput output, String value) throws IOException
	{
		output.writeByte(TYPE_COMPOUND);
		output.writeUTF("");
		output.writeByte(TYPE_COMPOUND);
		output.writeUTF("SkullProfile");
		output.writeByte(TYPE_STRING);
		output.writeUTF("Id");
		output.writeUTF(UUID.randomUUID().toString());
		output.writeByte(TYPE_COMPOUND);
		output.writeUTF("Properties");
		output.writeByte(TYPE_LIST);
		output.writeUTF("textures");
		output.writeByte(TYPE_COMPOUND);
		output.writeInt(1);  // Length of the list
		output.writeByte(TYPE_STRING);
		output.writeUTF("Value");
		output.writeUTF(value);
		output.writeByte(END_MARK);
		output.writeByte(END_MARK);
		output.writeByte(END_MARK);
		output.writeByte(END_MARK);
	}
}
