package me.Dzyre.lessEssentials;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;




public class Main extends JavaPlugin implements Listener, CommandExecutor{
	public static Map<String, String> tpa = new HashMap<String, String>();
	public static Map<String, String> homes = new HashMap<String, String>();
    final static String FilePath = "lessEssentials/homes.txt";
	
	@Override
	public void onEnable() {
		File file = new File("lessEssentials");
		if (!(file.exists())) {
			file.mkdirs();
		}
		this.getCommand("sethome").setExecutor(this);
		this.getCommand("home").setExecutor(this);
		this.getCommand("delhome").setExecutor(this);
		this.getCommand("tpa").setExecutor(this);
		this.getCommand("tpaccept").setExecutor(this);
		
		this.getServer().getPluginManager().registerEvents(this, this);
		getMap();
	}

	
	@Override
	public void onDisable() {
		WriteToFile();
	}
    	
	

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("tpa")) {
			if(!(sender instanceof Player)) {
				sender.sendMessage("Only players can TPA");
				return true;
			}
			if(args.length < 1) {
				sender.sendMessage(ChatColor.RED + "Correct Usage: /tpa <player>");
				return true;
			}
			String playerName = args[0];
			if(!(Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(playerName)))) {
				sender.sendMessage("Invalid Player Name, Try Again");
				return true;
			}
			else {
				Bukkit.getPlayer(playerName).sendMessage(ChatColor.RED + sender.getName() + " Has requested to TP to you, do you accept?");
				tpa.put(playerName, sender.getName());
				sender.sendMessage(ChatColor.GREEN + "Request Sent!");
				return true;
			}
		}
		if(label.equalsIgnoreCase("tpaccept")) {
			if(tpa.get(sender.getName()) == null) {
				sender.sendMessage("You have no pending requests");
				return true;
			}
			else {
				Bukkit.getPlayer(tpa.get(sender.getName())).teleport(Bukkit.getPlayer(sender.getName()));
				tpa.remove(sender.getName());
				return true;
			}
		}
		if(label.equalsIgnoreCase("tpdeny")) {
			if(tpa.get(sender.getName()) == null) {
				sender.sendMessage("You have no pending requests");
				return true;
			}
			else {
				tpa.remove(sender.getName());
				return true;
			}
		}
		if(label.equalsIgnoreCase("home")) {
			if(!(sender instanceof Player)) {
				sender.sendMessage("Only players can go home");
				return true;
			}
			if(args.length < 1) {
				sender.sendMessage(ChatColor.RED + "Please type the name of the home you wish to go to");
				return true;
			}
			Player player = (Player) sender;
			goHome(player, args[0]);
			return true;
		}
		if(label.equalsIgnoreCase("homes")){
			String homesList = ChatColor.DARK_GREEN + "Homes: \n";
			for(String home : homes.keySet()) {
				if(Bukkit.getPlayer(UUID.fromString(home.split(":")[0])).getName() == sender.getName()) {
					homesList = String.join(" ",homesList, ChatColor.GREEN + home.split(":")[1], "\n");
				}
			}
			sender.sendMessage(homesList);
			return true;
		}
		if(label.equalsIgnoreCase("sethome")) {
			if(!(sender instanceof Player)) {
				sender.sendMessage("Only players can set homes");
				return true;
			}
			if(args.length < 1) {
				sender.sendMessage(ChatColor.RED + "Please type the name of the home you wish to set");
				return true;
			}
			Player player = (Player) sender;
			sender.sendMessage(setHome(player, args[0]));
			return true;
		}
		if(label.equalsIgnoreCase("delhome")) {
			if(!(sender instanceof Player)) {
				sender.sendMessage("Only players can delete homes");
				return true;
			}
			if(args.length < 1) {
				sender.sendMessage(ChatColor.RED + "Please type the name of the home you wish to delete");
				return true;
			}
			Player player = (Player) sender;
			sender.sendMessage(delHome(player, args[0]));
			return true;
		}
		
		return false;
		}
	
	
	
	
	public String setHome(Player player, String home) {
		String uuid = player.getUniqueId() + ":" + home;
		int x = player.getLocation().getBlockX();
		int y = player.getLocation().getBlockY();
		int z = player.getLocation().getBlockZ();
		String location = x + ":" + y + ":" + z;
		if(homes.containsKey(uuid)) {
			return "Home already exists!";
		}
		homes.put(uuid, location);
		return ChatColor.GREEN + "Home " + ChatColor.DARK_RED +  home + ChatColor.GREEN + " created successfully";
	}
	
	public String delHome(Player player, String home) {
		String uuid = player.getUniqueId() + ":" + home;
		if(!(homes.containsKey(uuid))) {
			return "Home doesn't exist!";
		}
		homes.remove(uuid);
		return ChatColor.RED + "Home " + home + " deleted successfully";
	}
	
	public void goHome(Player player, String home) {
		String uuid = player.getUniqueId() + ":" + home;
			if(homes.containsKey(uuid)) {
				String location = homes.get(uuid);
				String[] loc = location.split(":");
				int x = Integer.parseInt(loc[0]);
				int y = Integer.parseInt(loc[1]);
				int z = Integer.parseInt(loc[2]);
				World w = player.getWorld();
				Location homeLoc = new Location(w,x,y,z);
				player.sendMessage(ChatColor.GREEN + "Teleporting to home: " + home);
				player.teleport(homeLoc);
				return;
		}
			else {
				player.sendMessage(home + " does not exist, check capitalization or spelling");
			}
		
	}
	
	
	
    public static void getMap () {
        
        //read text file to HashMap
    	homes = getHashMapFromTextFile();
        
        //iterate over HashMap entries
        for(Entry<String, String> entry : homes.entrySet()){
            System.out.println( entry.getKey() + " => " + entry.getValue() );
        }
    }
    
    public static Map<String, String> getHashMapFromTextFile(){
        
        Map<String, String> mapFileContents = new HashMap<String, String>();
        BufferedReader br = null;
        
        try{
            
            //create file object
            File file = new File(FilePath);
            
            //create BufferedReader object from the File
            br = new BufferedReader( new FileReader(file) );
            
            String line = null;
            
            //read file line by line
            while ( (line = br.readLine()) != null ){
                
                //split the line by :
                String[] parts = line.split(";");
                
                //first part is name, second is age
                String name = parts[0].trim();
                String location = parts[1].trim();
                
                //put name, age in HashMap if they are not empty
                if(!name.equals("") && !location.equals("") )
                    mapFileContents.put(name, location);
            }
                        
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            
            //Always close the BufferedReader
            if(br != null){
                try { 
                    br.close(); 
                }catch(Exception e){};
            }
        }        
        
        return mapFileContents;
        
    }

public void WriteToFile () {
	    /*** Change the path ***/
	
	        
	        //new file object
	        File file = new File(FilePath);
	        
	        BufferedWriter bf = null;;
	        
	        try{
	            
	            //create new BufferedWriter for the output file
	            bf = new BufferedWriter( new FileWriter(file) );
	 
	            //iterate map entries
	            for(Map.Entry<String, String> entry : homes.entrySet()){
	                
	                //put key and value separated by a colon
	                bf.write( entry.getKey() + ";" + entry.getValue() );
	                
	                //new line
	                bf.newLine();
	            }
	            
	            bf.flush();
	 
	        }catch(IOException e){
	            e.printStackTrace();
	        }finally{
	            
	            try{
	                //always close the writer
	                bf.close();
	            }catch(Exception e){}
	        }
	    }
	
}
