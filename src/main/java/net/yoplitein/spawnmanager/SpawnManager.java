package net.yoplitein.spawnmanager;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

@Mod(modid=ModInfo.ID, name=ModInfo.NAME, version=ModInfo.VERSION)
public class SpawnManager
{
	@Instance(ModInfo.ID)
	public static SpawnManager instance;
	
	private SpawnManagerConfiguration config;
	
	@EventHandler
	public void preinit(FMLPreInitializationEvent event)
	{
		config = new SpawnManagerConfiguration(event.getSuggestedConfigurationFile());
	}
	
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	MinecraftForge.EVENT_BUS.register(new SpawnManagerEvents(config));
    }
    
    @EventHandler
    public void start(FMLServerStartingEvent event)
    {
    	event.registerServerCommand(new CommandSpawnManager(config));
    }
    
    @EventHandler
    public void stop(FMLServerStoppingEvent event)
    {
    	config.cfg.save();
    }
}
