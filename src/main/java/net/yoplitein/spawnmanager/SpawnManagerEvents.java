package net.yoplitein.spawnmanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class SpawnManagerEvents
{
	SpawnManagerConfiguration config;
	TravelDelayHandler delayer;
	
	public SpawnManagerEvents(SpawnManagerConfiguration config)
	{
		this.config = config;
		delayer = new TravelDelayHandler();
		
		TickRegistry.registerTickHandler(delayer, Side.SERVER);
	}
	
	@ForgeSubscribe
	public void entJoin(EntityJoinWorldEvent event)
	{
		try
		{
			EntityPlayer ply = (EntityPlayer)event.entity;
			
			if(Arrays.asList(config.getWhitelist()).contains(ply.getDisplayName()))
				return;
			
			if(event.world.provider.dimensionId == config.get("fromDimension", 0))
				delayer.addDelay(ply, config.get("toDimension", -1));
		}
		catch(ClassCastException err)
		{
		}
	}
	
	private class DelayContainer
	{
		EntityPlayer ply;
		int dimensionID;
		
		public DelayContainer(EntityPlayer ply, int dimensionID)
		{
			this.ply = ply;
			this.dimensionID = dimensionID;
		}
	}
	
	private class TravelDelayHandler implements ITickHandler
	{
		ArrayList<DelayContainer> delays;
		
		public TravelDelayHandler()
		{
			delays = new ArrayList<DelayContainer>();
		}
		
		public void addDelay(EntityPlayer ply, int dimensionID)
		{
			delays.add(new DelayContainer(ply, dimensionID));
		}
		
		@Override
		public void tickStart(EnumSet<TickType> type, Object... tickData)
		{
		}
		
		@Override
		public void tickEnd(EnumSet<TickType> type, Object... tickData)
		{
			if(type.equals(EnumSet.of(TickType.SERVER)))
			{
				for(DelayContainer item: delays)
					item.ply.travelToDimension(item.dimensionID);
				
				delays.clear();
			}
		}
		
		@Override
		@SuppressWarnings({"rawtypes", "unchecked"})
		public EnumSet ticks()
		{
			return EnumSet.of(TickType.SERVER);
		}
		
		@Override
		public String getLabel()
		{
			return null;
		}
	}
}







