package net.yoplitein.spawnmanager;

import java.io.File;

import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.FMLLog;

public class SpawnManagerConfiguration
{
	public Configuration cfg;
	
	public SpawnManagerConfiguration(File file)
	{
		cfg = new Configuration(file);
		
		cfg.load();
		cfg.save();
	}
	
	public String[] getWhitelist()
	{
		return cfg.get("main", "whitelist", new String[0]).getStringList();
	}
	
	public String[] getMappings()
	{
		return cfg.get("main", "mappings", new String[0]).getStringList();
	}
	
	public int getMapping(Integer source)
	{
		String[] mappings = getMappings();
		
		for(String mapping: mappings)
		{
			try
			{
				String[] split = mapping.split(":");
				
				if(split[0].equals(source.toString()))
					return Integer.parseInt(split[1]);
			}
			catch(IndexOutOfBoundsException err)
			{
				FMLLog.getLogger().warning("Bad formatting on mapping " + mapping);
			}
		}
		
		return source;
	}
}
