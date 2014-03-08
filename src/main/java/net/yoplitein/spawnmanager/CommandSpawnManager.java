package net.yoplitein.spawnmanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraftforge.common.Property;

import com.google.common.collect.Lists;

public class CommandSpawnManager implements ICommand
{
	static final String CMD_WHITELIST = "whitelist";
	static final String CMD_CFG = "configure";
	static final String CMD_TOGGLE = "toggle";
	static final String CMD_SAVE = "save";
	static final String name = "spawnmanager";
	
	SpawnManagerConfiguration config;
	
	public CommandSpawnManager(SpawnManagerConfiguration config)
	{
		this.config = config;
	}
	
	@Override
	public int compareTo(Object other)
	{
		return name.compareTo(((ICommand)other).getCommandName());
	}
	
	@Override
	public String getCommandName()
	{
		return name;
	}
	
	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return name + " whitelist <add|remove> <username>\n" +
			   name + " configure <fromID|toID> <dimension ID>\n" +
			   name + " toggle <on|off>\n" +
			   name + " save";
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public List getCommandAliases()
	{
		return null;
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if(args.length > 0)
		{
			String cmd = args[0];
			try
			{
				if     (cmd.equals(CMD_WHITELIST))
				{
					String addOrRemove = args[1];
					String username = args[2];
					Property whitelist = config.cfg.get("main", "whitelist", new String[0]);
					List<String> list = new ArrayList<String>(Arrays.asList(whitelist.getStringList()));
					
					if     (addOrRemove.equals("add"))
					{
						list.add(username);
					}
					else if(addOrRemove.equals("remove"))
					{
						list.remove(username);
					}
					else
					{
						sendUsage(sender);
						
						return;
					}
					
					whitelist.set((String[])list.toArray(new String[list.size()]));
				}
				else if(cmd.equals(CMD_CFG))
				{
					String option = args[1];
					String value = args[2];
					
					try
					{
						if     (option.equals("fromID"))
						{
							config.cfg.get("main", "fromDimension", 0).set(Integer.parseInt(value));
						}
						else if(option.equals("toID"))
						{
							config.cfg.get("main", "toDimension", 0).set(Integer.parseInt(value));
						}
						else
							sendUsage(sender);
					}
					catch(NumberFormatException err)
					{
						sendMsg(sender, value + " is not a number");
					}
				}
				else if(cmd.equals(CMD_TOGGLE))
				{
					String option = args[1];
					
					if     (option.equals("on"))
					{
						config.cfg.get("main", "enabled", true).set(true);
					}
					else if(option.equals("off"))
					{
						config.cfg.get("main", "enabled", true).set(false);
					}
					else
						sendUsage(sender);
				}
				else if(cmd.equals(CMD_SAVE))
					config.cfg.save();
			}
			catch(IndexOutOfBoundsException err)
			{
				sendUsage(sender);
			}
		}
		else
			sendUsage(sender);
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		return sender.canCommandSenderUseCommand(4, name);
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if(args.length > 0)
		{
			String cmd = args[0];
			
			if     (args.length == 2)
			{
				if     (cmd.equals(CMD_WHITELIST))
					return Lists.newArrayList("add", "remove");
				else if(cmd.equals(CMD_CFG))
					return Lists.newArrayList("fromID", "toID");
				else if(cmd.equals(CMD_TOGGLE))
					return Lists.newArrayList("on", "off");
			}
			else if(args.length == 3 && cmd.equals(CMD_WHITELIST))
			{
				String[] usernames = MinecraftServer.getServer().getConfigurationManager().getAvailablePlayerDat();
				
				return Arrays.asList(usernames);
			}
			else if(args.length == 1)
				return Lists.newArrayList(CMD_WHITELIST, CMD_CFG, CMD_TOGGLE, CMD_SAVE);
		}
		
		return Lists.newArrayList();
		
	}
	
	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		/*for(String item: args)
			System.out.print(item + " ");
		
		System.out.println(" " + index);
		
		if(args.length == 3 && args[0] == CMD_WHITELIST)
		{
			System.out.println("is whitelist player arg");
			return true;
		}
		
		System.out.println("is not");*/
		return false;
	}
	
	void sendMsg(ICommandSender sender, String message)
	{
		sender.sendChatToPlayer(ChatMessageComponent.createFromText(message));
	}
	
	void sendUsage(ICommandSender sender)
	{
		sendMsg(sender, getCommandUsage(sender));
	}
}
