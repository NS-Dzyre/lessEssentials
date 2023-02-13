public class warps {
    
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
  if(sender.hasPermission("warp")){
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
}

if(sender.hasPermission("controlWarps")){
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

}
