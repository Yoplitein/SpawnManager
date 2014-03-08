package net.yoplitein.spawnmanager;

import java.io.File;

import net.minecraftforge.common.Configuration;

public class SpawnManagerConfiguration
{
	public Configuration cfg;
	
	public SpawnManagerConfiguration(File file)
	{
		cfg = new Configuration(file);
		
		cfg.load();
	}
	
	public int get(String key, int defaultValue)
	{
		return cfg.get("main", key, defaultValue).getInt();
	}
	
	public String[] getWhitelist()
	{
		return cfg.get("main", "whitelist", new String[0]).getStringList();
	}
}
