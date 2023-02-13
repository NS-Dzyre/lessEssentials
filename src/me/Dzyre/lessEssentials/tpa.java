package me.Dzyre.lessEssentials

public class tpa extends CommandExecutor {
  public static Map < UUID, UUID > tpa = new HashMap < UUID , UUID > ();

public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
  if(sender.hasPermission("tpa")){
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
        tpa.put(Bukkit.getPlayer(playerName).getUniqueId(), sender.getUniqueId());
        sender.sendMessage(ChatColor.GREEN + "Request Sent!");
        return true;
      }
    }
    if (label.equalsIgnoreCase("tpaccept")) {
      if (tpa.get(sender.getUniqueId()) == null) {
        sender.sendMessage("You have no pending requests");
        return true;
      } else {
        Bukkit.getPlayer(tpa.get(sender.getUniqueId())).teleport(Bukkit.getPlayer(sender.getName()));
        tpa.remove(sender.getUniqueId());
        return true;
      }
    }
    if (label.equalsIgnoreCase("tpdeny")) {
      if (tpa.get(sender.getUniqueId()) == null) {
        sender.sendMessage("You have no pending requests");
        return true;
      } else {
        tpa.remove(sender.getUniqueId());
        return true;
      }
    }
  }
}
