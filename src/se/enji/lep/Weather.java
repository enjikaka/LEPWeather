package se.enji.lep;

import java.util.Arrays;
import java.util.List;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Weather extends JavaPlugin {
	FileConfiguration config = null;
	static String notAllowed = "§cYou are not allowed to use this command.";
	static String wrongWay = "§cYou executed the command in a wrong way.";
	static List<?> supportedArgs = Arrays.asList(new String[]{"clear","sun","rain","thunder"});

	@Override
	public void onEnable() {
		config=getConfig();
		config.options().copyDefaults(true);
		saveConfig();
	}
	
	private void WeatherSet(World w, String s, CommandSender cs) {
		if (s.equals("clear")) s = "sun";
		String name = cs.getName();
		String world = w.getName();
		world = world.replaceAll("world_", "").replaceAll("_", " ");
		world = world.substring(0,1).toUpperCase() + world.substring(1,world.length());
		name = name.equals("CONSOLE") ? config.getString("alias.console") : name;
		switch (s) {
			case "sun":
				if (!allow(cs,s)) {
					((Player)cs).sendMessage(notAllowed);
					return;
				}
				w.setStorm(false);
				w.setThundering(false);
				break;
			case "rain":
				if (!allow(cs,s)) {
					((Player)cs).sendMessage(notAllowed);
					return;
				}
				w.setStorm(true);
				w.setThundering(false);
				break;
			case "thunder":
				w.setStorm(true);
				w.setThundering(true);
				break;
		}
		getServer().broadcastMessage(config.getString("messages."+s+".text").replaceAll("%p", name).replaceFirst("%w", world));
	}
	
	private boolean allow(CommandSender s, String a) {
		if (s instanceof Player) {
			if (((Player)s).hasPermission("lep.weather.*") || ((Player)s).hasPermission("lep.weather." + a)) return true;
			else return false;
		}
		return true;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("weather")) {
			if ((args.length <= 0  || args.length > 2) || (!supportedArgs.contains(args[0]))) {
				sender.sendMessage(wrongWay);
				return false;
			}
			int targetWorld = (args.length == 2 && args[1] != null) ? Integer.parseInt(args[1]) : 0;
			World w = (sender instanceof Player) ? ((Player)sender).getWorld() : getServer().getWorlds().get(targetWorld);
			WeatherSet(w,args[0],sender);
			return true;
		}
		
		return false; 
	}
}