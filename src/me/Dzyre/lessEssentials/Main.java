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

public class Main extends JavaPlugin implements Listener, CommandExecutor {
  
  public static boolean isLoaded = false;
  public static Map < String, String > warps = new HashMap < String, String > ();
  final static String warpsFilePath = Bukkit.getServer().getWorlds().get(0).getName()  + "/warps.txt";
  public File configFile = new File(Bukkit.getServer().getWorlds().get(0).getName()  + "/config.txt");
  
  @Override
  public void onEnable() {
    File file = new File("plugins/lessEssentials");
    if (!(file.exists())) {
      file.mkdirs();
    }
    if(Bukkit.getOnlinePlayers().size() != 0){
      getMap();
      getWarpsMap();
      isLoaded = true;
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
    this.getCommand("sethome").setExecutor(new homes());
    this.getCommand("home").setExecutor(new homes());
    this.getCommand("homes").setExecutor(new homes());
    this.getCommand("delhome").setExecutor(new homes());
    this.getCommand("tpa").setExecutor(new tpa());
    this.getCommand("tpaccept").setExecutor(new tpa());
    this.getCommand("tpdeny").setExecutor(new tpa());
    this.getCommand("setwarp").setExecutor(new warps());
    this.getCommand("delwarp").setExecutor(new warps());
    this.getCommand("warp").setExecutor(new warps());
    this.getCommand("config").setExecutor(this);
    
    this.getServer().getPluginManager().registerEvents(this, this);
   
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


  @EventHandler
  public void loadMapsFromFirstPerson(PlayerJoinEvent event){
    if(!isLoaded){
      homes.getMap();
      warps.getWarpsMap();
      isLoaded = true;
    }
  }

  public void writeConfig() throws IOException {

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
      writer.write("backupLocations : True \n" +
        "backupCurrencies : False \n");
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
      // change 2 here if adding more config options
      if(args.length == 2 && args[0].toLowerCase() == 'change'){
        if(Integer.parseInt(args[0]) > 2; || Integer.parseInt(args[0]) < 0){
          sender.sendMessage("Please use the number associated with the config option you wish to chose, type /config to get the options.\n")
        }
      try {
        changeConfig(changeConfigName(Integer.parseInt(args[1])), args[2]);
        Player player = (Player) sender;
        player.sendMessage(changeConfigName(Integer.parseInt(args[1])) + " config option has been changed to " +  args[2]);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return true;
    }
    }
    if (label.equalsIgnoreCase("spawn")) {
      if (!(sender instanceof Player)) {
        sender.sendMessage("Only players can go to spawn");
        return true;
      }
      Player player = (Player) sender;
      player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
      return true;
    }
}

  public String changeConfigName(int number) {
    switch (number) {
    case 1:
      return "backupCurrencies";
    case 2:
      return "backupLocations";
    default:
      return "null";
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
      if (br != null) {
        try {
          br.close();
        } catch (Exception e) {};
      }
    }

    return false;
  }
}