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
	static final String CMD_MAPPINGS = "mappings";
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
		return name + " whitelist <add|remove|list> [username]\n" +
			   name + " mappings <add|remove|list> [mapping] (e.g. add 0:1 to map the Overworld to the End)\n" +
			   name + " toggle [on|off]\n" +
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
					String subcmd = args[1];
					Property whitelist = config.cfg.get("main", "whitelist", new String[0]);
					List<String> list = new ArrayList<String>(Arrays.asList(whitelist.getStringList()));
					
					if     (subcmd.equals("add"))
					{
						list.add(args[2]);
						sendMessage(sender, "Added " + args[2]);
					}
					else if(subcmd.equals("remove"))
					{
						list.remove(args[2]);
						sendMessage(sender, "Removed " + args[2]);
					}
					else if(subcmd.equals("list"))
					{
						String result = "";
						
						for(String username: list)
							result += ", " + username;
						
						if(result.equals(""))
							result = "  No whitelisted players";
						
						sendMessage(sender, result.substring(2));
						
						return;
					}
					else
					{
						sendUsage(sender);
						
						return;
					}
					
					whitelist.set((String[])list.toArray(new String[list.size()]));
				}
				else if(cmd.equals(CMD_MAPPINGS))
				{
					String subcmd = args[1];
					Property mappings = config.cfg.get("main", "mappings", new String[0]);
					List<String> list = new ArrayList<String>(Arrays.asList(mappings.getStringList()));
					
					if     (subcmd.equals("add"))
					{
						if(isValidMapping(args[2]))
						{
							list.add(args[2]);
							sendMessage(sender, "Added " + args[2]);
						}
						else
							sendMessage(sender, args[2] + " is not a valid mapping (see /help " + name + ")");
					}
					else if(subcmd.equals("remove"))
					{
						list.remove(args[2]);
						sendMessage(sender, "Removed " + args[2]);
					}
					else if(subcmd.equals("list"))
					{
						String result = "";
						
						for(String mapping: list)
							result += ", " + mapping;
						
						if(result.equals(""))
							result = "  No mappings";
						
						sendMessage(sender, result.substring(2));
						
						return;
					}
					else
					{
						sendUsage(sender);
						
						return;
					}
					
					mappings.set((String[])list.toArray(new String[list.size()]));
				}
				else if(cmd.equals(CMD_TOGGLE))
				{
					if(args.length > 1)
					{
						String option = args[1];
						
						if     (option.equals("on"))
						{
							config.cfg.get("main", "enabled", true).set(true);
							sendMessage(sender, "Toggled on");
						}
						else if(option.equals("off"))
						{
							config.cfg.get("main", "enabled", true).set(false);
							sendMessage(sender, "Toggled off");
						}
						else
							sendUsage(sender);
					}
					else
					{
						if(config.cfg.get("main", "enabled", true).getBoolean(true))
							sendMessage(sender, "Currently on");
						else
							sendMessage(sender, "Currently off");
					}
				}
				else if(cmd.equals(CMD_SAVE))
					config.cfg.save();
				else
					sendUsage(sender);
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
					return Lists.newArrayList("add", "remove", "list");
				else if(cmd.equals(CMD_MAPPINGS))
					return Lists.newArrayList("add", "remove", "list");
				else if(cmd.equals(CMD_TOGGLE))
					return Lists.newArrayList("on", "off");
			}
			else if(args.length == 3 && cmd.equals(CMD_WHITELIST))
			{
				String[] usernames = MinecraftServer.getServer().getConfigurationManager().getAvailablePlayerDat();
				
				return Arrays.asList(usernames);
			}
			else if(args.length == 1)
				return Lists.newArrayList(CMD_WHITELIST, CMD_MAPPINGS, CMD_TOGGLE, CMD_SAVE);
		}
		
		return Lists.newArrayList();
		
	}
	
	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return false;
	}
	
	void sendMessage(ICommandSender sender, String message)
	{
		sender.sendChatToPlayer(ChatMessageComponent.createFromText(message));
	}
	
	void sendUsage(ICommandSender sender)
	{
		sendMessage(sender, getCommandUsage(sender));
	}
	
	boolean isValidMapping(String mapping)
	{
		try
		{
			String[] split = mapping.split(":");
			
			Integer.parseInt(split[0]);
			Integer.parseInt(split[1]);
			
			return true;
		}
		catch(Exception err)
		{
			return false;
		}
	}
}
