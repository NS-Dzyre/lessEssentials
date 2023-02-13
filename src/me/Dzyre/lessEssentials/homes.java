package me.Dzyre.lessEssentials;

public class homes {
  public static Map < String, String > homes = new HashMap < String, String > ();
  final static String homesPath = Bukkit.getServer().getWorlds().get(0).getName() +"/homes.txt";


public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
  if(sender.hasPermission("home")){
    if (label.equalsIgnoreCase("home")) {
      if(sender.hasPermission("adminHomes")){
      if(args[0].contains(":")){
        Player player = (Player) sender;
        if(!Bukkit.getOfflinePlayer(args[0].split(":")[0]) == null){
          goHome(player, args[0].split[":"][1], Bukkit.getOfflinePlayer(args[0].split(":")[0]).getUniqueId())
        }
      }
    }
      if (!(sender instanceof Player)) {
        sender.sendMessage("Only players can go home");
        return true;
      }
      if (args.length < 1) {
        sender.sendMessage(ChatColor.RED + "Please type the name of the home you wish to go to");
        return true;
      }
      Player player = (Player) sender;
      goHome(player, args[0], player.getUniqueId());
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
  }

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
  String home_string = player.getUniqueId() + ":" + home;
  if (!(homes.containsKey(home_string))) {
    return "Home doesn't exist!";
  }
  homes.remove(home_string);
  return ChatColor.RED + "Home " + home + " deleted successfully";
}

public void goHome(Player player, String home, String uuid) {
   String home_string = uuid + ":" + home;
  if (homes.containsKey()) {
    String location = homes.get(home_string);
    String[] loc = location.split(":");
    int x = Integer.parseInt(loc[0]);
    int y = Integer.parseInt(loc[1]);
    int z = Integer.parseInt(loc[2]);
    World w = Bukkit.getWorld(loc[3]);
    Location homeLoc = new Location(w, x, y, z);
    player.sendMessage(ChatColor.GREEN + "Teleporting to home: " + home);
    player.teleport(homeLoc);
    return;
  } else {
    player.sendMessage(home + " does not exist, check capitalization or spelling");
  }

}


public static void getMap() {
  homes = getHomesHashMapFromTextFile();
  for (Entry < String, String > entry: homes.entrySet()) {
    System.out.println(entry.getKey() + " => " + entry.getValue());
  }
}

public static Map < String, String > getHomesHashMapFromTextFile() {

  Map < String, String > mapFileContents = new HashMap < String, String > ();
  BufferedReader br = null;

  try {
    File file = new File(FilePath);
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

public void writeHomesToFile(String filepath) {
  File file = new File(filepath);
  BufferedWriter bf = null;;
  try {
    bf = new BufferedWriter(new FileWriter(file));
    for (Map.Entry < String, String > entry: homes.entrySet()) {
      bf.write(entry.getKey() + ";" + entry.getValue());
      bf.newLine();
    }
    bf.flush();
  } catch (IOException e) {
    e.printStackTrace();
  } finally {
    try {
      bf.close();
    } catch (Exception e) {}
  }
}



}