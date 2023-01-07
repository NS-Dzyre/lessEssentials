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
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener, CommandExecutor {
  public static Map < String, String > tpa = new HashMap < String, String > ();
  public static Map < String, String > homes = new HashMap < String, String > ();
  public static Map < String, String > warps = new HashMap < String, String > ();
  final static String FilePath = Bukkit.getServer().getWorlds().get(0).getName() +"/homes.txt";
  final static String warpsFilePath = Bukkit.getServer().getWorlds().get(0).getName()  + "/warps.txt";
  public File configFile = new File(Bukkit.getServer().getWorlds().get(0).getName()  + "/config.txt");
  
  @Override
  public void onEnable() {
    File file = new File("plugins/lessEssentials");
    if (!(file.exists())) {
      file.mkdirs();
    }

    if (!(configFile.exists())) {
      try {
        configFile.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        writeConfig();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    this.getCommand("sethome").setExecutor(this);
    this.getCommand("home").setExecutor(this);
    this.getCommand("delhome").setExecutor(this);
    this.getCommand("tpa").setExecutor(this);
    this.getCommand("tpaccept").setExecutor(this);

    this.getServer().getPluginManager().registerEvents(this, this);
    getMap();
    getWarpsMap();
  }
  
  // link warps and homes to worlds 
  
  @Override
  public void onDisable() {
    WriteToFile(FilePath);
    WriteWarpsToFile(warpsFilePath);
    try {
      if (checkConfig("backupLocations")) {
        WriteToFile(Bukkit.getServer().getWorlds().get(0).getName() + "/backupHomes.txt");
        WriteWarpsToFile(Bukkit.getServer().getWorlds().get(0).getName()  + "/backupWarps.txt");
      }
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    try {
      changeConfig("backupCurrencies", "True");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void writeConfig() throws IOException {

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
      writer.write("backupLocations : False \n" +
        "backupCurrencies : False \n" + "onePersonSleep : False \n");
    }
  }

  public void changeConfig(String changeItem, String value) throws IOException {
    Map < String, String > config = new HashMap < String, String > ();
    if (configFile != null) {
      try {
        BufferedReader reader = new BufferedReader(new FileReader(configFile));
        String inputLine = "input";
        while (inputLine != "") {
          inputLine = reader.readLine();
          if (inputLine == null) {
            break;
          }
          String[] myList = inputLine.split(":");
          config.put(myList[0].trim(), myList[1].trim());;
          if (myList[0].trim().contains(changeItem)) {
            config.replace(changeItem, value);
          }
        }
        reader.close();
      } finally {}
      BufferedWriter bf = null;;

      try {
        bf = new BufferedWriter(new FileWriter(configFile));
        for (Map.Entry < String, String > entry: config.entrySet()) {
          bf.write(entry.getKey() + " : " + entry.getValue());
          bf.newLine();
        }

        bf.flush();
        bf.close();
      } finally {

      }
    }
    return;
  }

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (label.equalsIgnoreCase("config")) {
      if (args.length < 2) {
        if (configFile != null) {

          int i = 1;
          try {
            BufferedReader reader = new BufferedReader(new FileReader(configFile));
            String inputLine = "input";
            while (inputLine != "") {
              inputLine = reader.readLine();
              if (inputLine == null) {
            	  reader.close();
                return true;
              }
              Player player = (Player) sender;
              player.sendMessage(i + ". " + inputLine);
              i ++;
            }
            reader.close();

          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          } finally {}
        }

      }
      try {
        changeConfig(changeConfigName(Integer.parseInt(args[0])), args[1]);
        Player player = (Player) sender;
        player.sendMessage(changeConfigName(Integer.parseInt(args[0])) + " config option has been changed to " +  args[1]);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return true;
    }
    if (label.equalsIgnoreCase("tpa")) {
      if (!(sender instanceof Player)) {
        sender.sendMessage("Only players can TPA");
        return true;
      }
      if (args.length < 1) {
        sender.sendMessage(ChatColor.RED + "Correct Usage: /tpa <player>");
        return true;
      }
      String playerName = args[0];
      if (!(Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(playerName)))) {
        sender.sendMessage("Invalid Player Name, Try Again");
        return true;
      } else {
        Bukkit.getPlayer(playerName).sendMessage(ChatColor.RED + sender.getName() + " Has requested to TP to you, do you accept?");
        tpa.put(playerName, sender.getName());
        sender.sendMessage(ChatColor.GREEN + "Request Sent!");
        return true;
      }
    }
    if (label.equalsIgnoreCase("tpaccept")) {
      if (tpa.get(sender.getName()) == null) {
        sender.sendMessage("You have no pending requests");
        return true;
      } else {
        Bukkit.getPlayer(tpa.get(sender.getName())).teleport(Bukkit.getPlayer(sender.getName()));
        tpa.remove(sender.getName());
        return true;
      }
    }
    if (label.equalsIgnoreCase("tpdeny")) {
      if (tpa.get(sender.getName()) == null) {
        sender.sendMessage("You have no pending requests");
        return true;
      } else {
        tpa.remove(sender.getName());
        return true;
      }
    }

    if (label.equalsIgnoreCase("warp")) {
      if (!(sender instanceof Player)) {
        sender.sendMessage("Only players can warp");
        return true;
      }
      if (args.length < 1) {
        sender.sendMessage("Warps: ");
        for(int i = 0; i < warps.keySet().size(); i++){
        	sender.sendMessage(warps.keySet().toArray()[i] + "\n");
        }
        return true;
      } else {
        Player player = (Player) sender;

        if (warps.containsKey(args[0])) {
          String location = warps.get(args[0]);
          String[] loc = location.split(":");
          int x = Integer.parseInt(loc[0]);
          int y = Integer.parseInt(loc[1]);
          int z = Integer.parseInt(loc[2]);
          String world;
          if(loc.length <= 3) {
        	  world = player.getWorld().getName();
        	  warps.put(args[0], warps.get(args[0]) + ":" + player.getWorld().getName());
          } else
          {
        	  world = loc[3];
          }
          World w = Bukkit.getWorld(world);
          Location homeLoc = new Location(w, x, y, z);
          player.sendMessage(ChatColor.GREEN + "Teleporting to warp " + args[0]);
          player.teleport(homeLoc);
          return true;
        }
        else {
        	player.sendMessage("Unknown Warp " + args[0]);
        	return true;
        }
      }
    }
    if (label.equalsIgnoreCase("spawn")) {
      if (!(sender instanceof Player)) {
        sender.sendMessage("Only players can go to spawn");
        return true;
      }
      Player player = (Player) sender;
      player.teleport(player.getWorld().getSpawnLocation());
      return true;
    }

    if (label.equalsIgnoreCase("home")) {
      if (!(sender instanceof Player)) {
        sender.sendMessage("Only players can go home");
        return true;
      }
      if (args.length < 1) {
        sender.sendMessage(ChatColor.RED + "Please type the name of the home you wish to go to");
        return true;
      }
      Player player = (Player) sender;
      goHome(player, args[0]);
      return true;
    }
    if (label.equalsIgnoreCase("homes")) {
      String homesList = ChatColor.DARK_GREEN + "Homes: \n";
      for (String home: homes.keySet()) {
        if (Bukkit.getOfflinePlayer(UUID.fromString(home.split(":")[0])).getName() == ((Player) sender).getName()) {
          homesList = String.join(" ", homesList, ChatColor.GREEN + home.split(":")[1], "\n");
        }
      }
      sender.sendMessage(homesList);
      return true;
    }
    if (label.equalsIgnoreCase("sethome")) {
      if (!(sender instanceof Player)) {
        sender.sendMessage("Only players can set homes");
        return true;
      }
      if (args.length < 1) {
        sender.sendMessage(ChatColor.RED + "Please type the name of the home you wish to set");
        return true;
      }
      Player player = (Player) sender;
      sender.sendMessage(setHome(player, args[0]));
      return true;
    }
    if (label.equalsIgnoreCase("delhome")) {
      if (!(sender instanceof Player)) {
        sender.sendMessage("Only players can delete homes ");
        return true;
      }
      if (args.length < 1) {
        sender.sendMessage(ChatColor.RED + "Please type the name of the home you wish to delete");
        return true;
      }
      Player player = (Player) sender;
      sender.sendMessage(delHome(player, args[0]));
      return true;
    }
    if (label.equalsIgnoreCase("setwarp")) {
      if (!(sender instanceof Player)) {
        sender.sendMessage("Only players can set warps");
        return true;
      }
      if (args.length < 1) {
        sender.sendMessage(ChatColor.RED + "Please type the name of the warp you wish to set");
        return true;
      }
      Player player = (Player) sender;
      sender.sendMessage(setWarp(player, args[0]));
      return true;
    }
    if (label.equalsIgnoreCase("delwarp")) {
      if (!(sender instanceof Player)) {
        sender.sendMessage("Only players can delete warps");
        return true;
      }
      if (args.length < 1) {
        sender.sendMessage(ChatColor.RED + "Please type the name of the warp you wish to delete");
        return true;
      }
      Player player = (Player) sender;
      sender.sendMessage(delWarp(player, args[0]));
      return true;
    }

    return false;
  }

  public String setHome(Player player, String home) {
    String uuid = player.getUniqueId() + ":" + home;
    int x = player.getLocation().getBlockX();
    int y = player.getLocation().getBlockY();
    int z = player.getLocation().getBlockZ();
    String location = x + ":" + y + ":" + z + ":" + player.getWorld().getName();
    if (homes.containsKey(uuid)) {
      return "Home already exists!";
    }
    homes.put(uuid, location);
    return ChatColor.GREEN + "Home " + ChatColor.DARK_RED + home + ChatColor.GREEN + " created successfully";
  }

  public String delHome(Player player, String home) {
    String uuid = player.getUniqueId() + ":" + home;
    if (!(homes.containsKey(uuid))) {
      return "Home doesn't exist!";
    }
    homes.remove(uuid);
    return ChatColor.RED + "Home " + home + " deleted successfully";
  }

  public void goHome(Player player, String home) {
    String uuid = player.getUniqueId() + ":" + home;
    if (homes.containsKey(uuid)) {
      String location = homes.get(uuid);
      String[] loc = location.split(":");
      int x = Integer.parseInt(loc[0]);
      int y = Integer.parseInt(loc[1]);
      int z = Integer.parseInt(loc[2]);
      String world;
      if(loc.length <= 3) {
    	  world = player.getWorld().getName();
    	  homes.put(uuid, homes.get(uuid) + ":" + player.getWorld().getName());
      } else
      {
    	  world = loc[3];
      }
    
    	  
      
      World w = Bukkit.getWorld(world);
      Location homeLoc = new Location(w, x, y, z);
      player.sendMessage(ChatColor.GREEN + "Teleporting to home: " + home);
      player.teleport(homeLoc);
      return;
    } else {
      player.sendMessage(home + " does not exist, check capitalization or spelling");
    }

  }

  public String changeConfigName(int number) {
    switch (number) {
    case 1:
      return "backupCurrencies";
    case 2:
      return "backupLocations";
    case 3:
      return "onePersonSleep";
    default:
      return "null";
    }
  }

  public String setWarp(Player player, String home) {
    String uuid = home;
    int x = player.getLocation().getBlockX();
    int y = player.getLocation().getBlockY();
    int z = player.getLocation().getBlockZ();
    String location = x + ":" + y + ":" + z;
    if (warps.containsKey(uuid)) {
      return "Warp already exists!";
    }
    warps.put(uuid, location);
    return ChatColor.GREEN + "Warp " + ChatColor.DARK_RED + home + ChatColor.GREEN + " created successfully";
  }

  public String delWarp(Player player, String home) {
    String uuid = home;
    if (!(warps.containsKey(uuid))) {
      return "Warp doesn't exist!";
    }
    warps.remove(uuid);
    return ChatColor.RED + "Warp " + home + " deleted successfully";
  }

  public static void getMap() {

    //read text file to HashMap
    homes = getHashMapFromTextFile();

    //iterate over HashMap entries
    for (Entry < String, String > entry: homes.entrySet()) {
      System.out.println(entry.getKey() + " => " + entry.getValue());
    }
  }

  public static Map < String, String > getHashMapFromTextFile() {

    Map < String, String > mapFileContents = new HashMap < String, String > ();
    BufferedReader br = null;

    try {

      //create file object
      File file = new File(FilePath);

      //create BufferedReader object from the File
      br = new BufferedReader(new FileReader(file));

      String line = null;
      while ((line = br.readLine()) != null) {
        String[] parts = line.split(";");
        String name = parts[0].trim();
        String location = parts[1].trim();
        if (!name.equals("") && !location.equals(""))
          mapFileContents.put(name, location);
      }

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (Exception e) {};
      }
    }

    return mapFileContents;

  }

  public void WriteToFile(String filepath) {
    /*** Change the path ***/

    //new file object
    File file = new File(filepath);

    BufferedWriter bf = null;;

    try {

      //create new BufferedWriter for the output file
      bf = new BufferedWriter(new FileWriter(file));

      //iterate map entries
      for (Map.Entry < String, String > entry: homes.entrySet()) {

        //put key and value separated by a colon
        bf.write(entry.getKey() + ";" + entry.getValue());

        //new line
        bf.newLine();
      }

      bf.flush();

    } catch (IOException e) {
      e.printStackTrace();
    } finally {

      try {
        //always close the writer
        bf.close();
      } catch (Exception e) {}
    }
  }

  public static void getWarpsMap() {

    //read text file to HashMap
    warps = getWarpsHashMapFromTextFile();

    //iterate over HashMap entries
    for (Entry < String, String > entry: warps.entrySet()) {
      System.out.println(entry.getKey() + " => " + entry.getValue());
    }
  }

  public static Map < String, String > getWarpsHashMapFromTextFile() {

    Map < String, String > mapFileContents = new HashMap < String, String > ();
    BufferedReader br = null;

    try {

      //create file object
      File file = new File(warpsFilePath);

      //create BufferedReader object from the File
      br = new BufferedReader(new FileReader(file));

      String line = null;

      //read file line by line
      while ((line = br.readLine()) != null) {

        //split the line by :
        String[] parts = line.split(";");

        //first part is name, second is age
        String name = parts[0].trim();
        String location = parts[1].trim();

        //put name, age in HashMap if they are not empty
        if (!name.equals("") && !location.equals(""))
          mapFileContents.put(name, location);
      }

    } catch (Exception e) {
      e.printStackTrace();
    } finally {

      //Always close the BufferedReader
      if (br != null) {
        try {
          br.close();
        } catch (Exception e) {};
      }
    }

    return mapFileContents;

  }

  public void WriteWarpsToFile(String filepath) {
    /*** Change the path ***/

    //new file object
    File file = new File(filepath);

    BufferedWriter bf = null;;

    try {

      //create new BufferedWriter for the output file
      bf = new BufferedWriter(new FileWriter(file));

      //iterate map entries
      for (Map.Entry < String, String > entry: warps.entrySet()) {

        //put key and value separated by a semicolon
        bf.write(entry.getKey() + ";" + entry.getValue());

        //new line
        bf.newLine();
      }

      bf.flush();

    } catch (IOException e) {
      e.printStackTrace();
    } finally {

      try {
        //always close the writer
        bf.close();
      } catch (Exception e) {}
    }
  }

  public boolean checkConfig(String configOption) throws IOException {
    BufferedReader br = null;
    File file = new File("plugins/lessEssentials/config.txt");
    if (!(file.exists())) {
      file.createNewFile();
      writeConfig();
    }
    try {
      br = new BufferedReader(new FileReader(file));
      String line = null;
      while ((line = br.readLine()) != null) {
        String[] parts = line.split(":");
        String name = parts[0].trim();
        String trueOrFalse = parts[1].trim();
        if (name == configOption && trueOrFalse.toLowerCase() == "true") {
          return true;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {

      //Always close the BufferedReader
      if (br != null) {
        try {
          br.close();
        } catch (Exception e) {};
      }
    }

    return false;
  }
}