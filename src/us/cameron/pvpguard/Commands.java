package us.cameron.pvpguard;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Commands implements CommandExecutor {
    Main plugin;

    public Commands(Main instance) {
        plugin = instance;
    }

    public boolean onCommand(CommandSender cs, Command cms, String lbl, String[] args) {
        if(lbl.equalsIgnoreCase("region")) {
            if(args.length == 0) {
                cs.sendMessage("§7------ §9§lRegions Help §r§7------");
                cs.sendMessage("§7Regions can be used to protect your items in your chest(s). You need to be near the chest(s) when you add a region to protect your stuff because it is chunk based.");
                cs.sendMessage("§7/region add §c<name> §7- §9Adds a region.");
                cs.sendMessage("§7/region list [<all>] §7- §91. Lists all regions you are apart of; 2. Lists all regions (OP).");
                cs.sendMessage("§7/region whitelist §6<player> §c<region> §7- §9Adds a player to the region's whitelisted players.");
                cs.sendMessage("§7/region remove §c<region> §7- §9Removes the region.");
            } else {
                if(args[0].equalsIgnoreCase("add")) {
                    if (cs instanceof Player) {
                        if (args[1] == null) {
                            cs.sendMessage("§1[§9Regions§1] §8>> §cError! The name parameter is required. /region add <name>");
                            return true;
                        } else {
                            Player theplayer = (Player) cs;
                            ConfigurationSection inventorySection = plugin.getConfig().getConfigurationSection("Regions");
                            for(String key : inventorySection.getKeys(false)) {
                                ConfigurationSection item = inventorySection.getConfigurationSection(key);
                                if (theplayer.getLocation().getChunk().getX() == Integer.parseInt(item.getString("ChunkX")) && theplayer.getLocation().getChunk().getZ() == Integer.parseInt(item.getString("ChunkZ" ))) {
                                   cs.sendMessage("§1[§9Regions§1] §8>> §cError! You cannot make a region within an existing region.");
                                   return true;
                                }
                            }
                            if (plugin.getConfig().isSet("Regions." + args[1])) {
                                cs.sendMessage("§1[§9Regions§1] §8>> §cError! That region already exists.");
                                return true;
                            } else {
                                Player player = (Player) cs;
                                String[] list = {"openChest"};
                                String[] players = {""};
                                plugin.getConfig().set("Regions." + args[1] + ".Owner", player.getUniqueId().toString());
                                plugin.getConfig().set("Regions." + args[1] + ".ChunkX", player.getLocation().getChunk().getX());
                                plugin.getConfig().set("Regions." + args[1] + ".ChunkZ", player.getLocation().getChunk().getZ());
                                plugin.getConfig().set("Regions." + args[1] + ".OutsideRules", Arrays.asList(list));
                                plugin.getConfig().set("Regions." + args[1] + ".Whitelist", Arrays.asList(players));
                                plugin.saveConfig();
                                cs.sendMessage("§1[§9Regions§1] §8>> §7The region §a" + args[1] + " §7has successfully been added with the X chunk at " + player.getLocation().getChunk().getX() + " and the Z chunk at " + player.getLocation().getChunk().getZ() + ". §cOptional: Remember to add some players to your region's whitelist so they can interact with chests too!");
                            }
                        }
                    }
                } else if(args[0].equalsIgnoreCase("whitelist")) {
                    if(cs instanceof Player) {
                        if (args.length == 1) {
                            cs.sendMessage("§1[§9Regions§1] §8>> §cError! The player parameter is required. /region whitelist <player> <region>");
                            return true;
                        } else if (args.length == 2) {
                            cs.sendMessage("§1[§9Regions§1] §8>> §cError! The region parameter is required. /region whitelist <player> <region>");
                            return true;
                        } else {
                            Player theplayer = (Player) cs;
                            Player player = plugin.getServer().getPlayer(args[1]);
                            if (player == null) {
                                cs.sendMessage("§1[§9Regions§1] §8>> §cError! That player does not exist.");
                                return true;
                            } else if (!plugin.getConfig().isSet("Regions." + args[2])) {
                                cs.sendMessage("§1[§9Regions§1] §8>> §cError! That region does not exit.");
                                return true;
                            } else if(player.getUniqueId().equals(UUID.fromString(plugin.getConfig().getString("Regions." + args[2] + ".Owner")))) {
                                cs.sendMessage("§1[§9Regions§1] §8>> §cError! You can't add yourself to the whitelist.");
                            } else if(plugin.getConfig().getStringList("Regions." + args[2] + ".Whitelist").contains(player.getUniqueId().toString())) {
                                cs.sendMessage("The player " + args[1] + " has already been added to the whitelist.");
                            } else {
                                if (!(plugin.getConfig().getString("Regions." + args[2] + ".Owner").equals(theplayer.getUniqueId().toString()))) {
                                    cs.sendMessage("§1[§9Regions§1] §8>> §cError! You don't have permission to do this.");
                                    return true;
                                } else {
                                    List<String> whitelistedPlayers = plugin.getConfig().getStringList("Regions." + args[2] + ".Whitelist");
                                    whitelistedPlayers.add(player.getUniqueId().toString());
                                    plugin.getConfig().set("Regions." + args[2] + ".Whitelist", whitelistedPlayers);
                                    cs.sendMessage("§1[§9Regions§1] §8>> §7The region §a" + args[2] + " §7has been successfully modified.");
                                    plugin.saveConfig();
                                }
                            }
                        }
                    } else {
                        cs.sendMessage("You must be a player to execute this command!");
                    }
                } else if (args[0].equalsIgnoreCase("list")) {
                    // region list [<all>]
                    if(cs instanceof Player) {
                        Player theplayer = (Player) cs;
                        int index = 0;
                        int apartofanyregions = 0;
                        if (plugin.getConfig().isSet("Regions")) {
                            if (args.length == 1) {
                                ConfigurationSection inventorySection = plugin.getConfig().getConfigurationSection("Regions");
                                cs.sendMessage("§7------ §9§lRegions §r§7------");
                                for (String key : inventorySection.getKeys(false)) {
                                    ConfigurationSection item = inventorySection.getConfigurationSection(key);
                                    if (item.getString("Owner").equals(theplayer.getUniqueId().toString()) || item.getStringList("Whitelist").contains(theplayer.getUniqueId().toString())) {
                                        cs.sendMessage("§7" + (index++ + 1)  + ". §c§l" + item.getName() + " §r§6§lX: " + item.getString("ChunkX") + " §r§5§lZ:" + item.getString("ChunkZ") + " §r§2§lOwner: " + Bukkit.getOfflinePlayer(UUID.fromString(item.getString("Owner"))).getName());
                                        apartofanyregions++;
                                    }
                                }
                                if (apartofanyregions == 0) {
                                    cs.sendMessage("§7You are not currently in any regions.");
                                }
                            } else {
                                ConfigurationSection inventorySection = plugin.getConfig().getConfigurationSection("Regions");
                                cs.sendMessage("§7------ §9§lRegions §r§7------");
                                for (String key : inventorySection.getKeys(false)) {
                                    ConfigurationSection item = inventorySection.getConfigurationSection(key);
                                    cs.sendMessage("§7" + (index++ + 1) + ". §c§l" + item.getName() + " §r§6§lX: " + item.getString("ChunkX") + " §r§5§lZ:" + item.getString("ChunkZ") + " §r§2§lOwner: " + Bukkit.getOfflinePlayer(UUID.fromString(item.getString("Owner"))).getName());
                                }
                                if (index == 0) {
                                    cs.sendMessage("§7There are currently no regions that exist.");
                                }
                            }
                        } else {
                            cs.sendMessage("§7There are currently no regions that exist.");
                        }
                    }
                } else if(args[0].equalsIgnoreCase("remove")) {
                   if(cs instanceof Player) {
                       // region remove <region
                       Player theplayer = (Player) cs;
                       if(args[1] != null) {
                           if (plugin.getConfig().isSet("Regions." + args[1])) {
                               if((plugin.getConfig().getString("Regions." + args[1] + ".Owner").equals(theplayer.getUniqueId().toString()) || theplayer.isOp())) {
                                   plugin.getConfig().set("Regions." + args[1], null);
                                   plugin.saveConfig();
                                   cs.sendMessage("§1[§9Regions§1] §8>> §7The region §a" + args[1] + " §7has been successfully §cdeleted§7.");
                               } else {
                                   cs.sendMessage("§1[§9Regions§1] §8>> §7You don't have permission to do this.");
                                   return true;
                               }
                           } else {
                               cs.sendMessage("§1[§9Regions§1] §8>> §cError! That region does not exit.");
                               return true;
                           }
                       } else {
                           cs.sendMessage("the args length is not 1");
                       }
                   } else {
                       cs.sendMessage("not a player");
                   }
                } else if(args[0].equalsIgnoreCase("whitelistremove")) {
                    if (cs instanceof Player) {
                        if (args.length == 1) {
                            cs.sendMessage("§1[§9Regions§1] §8>> §cError! The player parameter is required. /region whitelistremove <player> <region>");
                            return true;
                        } else if (args.length == 2) {
                            cs.sendMessage("§1[§9Regions§1] §8>> §cError! The region parameter is required. /region whitelistremove <player> <region>");
                            return true;
                        } else {
                            Player theplayer = (Player) cs;
                            Player player = plugin.getServer().getPlayer(args[1]);
                            if (player == null) {
                                cs.sendMessage("§1[§9Regions§1] §8>> §cError! That player does not exist.");
                                return true;
                            } else if (!plugin.getConfig().isSet("Regions." + args[2])) {
                                cs.sendMessage("§1[§9Regions§1] §8>> §cError! That region does not exit.");
                                return true;
                            } else if(((Player) cs).getUniqueId().equals(UUID.fromString(plugin.getConfig().getString("Regions." + args[2] + ".Owner")))) {
                                cs.sendMessage("§1[§9Regions§1] §8>> §cError! You can't add yourself to the whitelist.");
                            } else if(plugin.getConfig().getStringList("Regions." + args[2] + ".Whitelist").contains(player.getUniqueId().toString())) {
                                cs.sendMessage("The player " + args[1] + " has already been added to the whitelist.");
                            } else {
                                if (!(plugin.getConfig().getString("Regions." + args[2] + ".Owner").equals(theplayer.getUniqueId().toString()))) {
                                    cs.sendMessage("§1[§9Regions§1] §8>> §cError! You don't have permission to do this.");
                                    return true;
                                } else {
                                    List<String> whitelistedPlayers = plugin.getConfig().getStringList("Regions." + args[2] + ".Whitelist");
                                    whitelistedPlayers.add(player.getUniqueId().toString());
                                    plugin.getConfig().set("Regions." + args[2] + ".Whitelist", whitelistedPlayers);
                                    cs.sendMessage("§1[§9Regions§1] §8>> §7The region §a" + args[2] + " §7has been successfully modified.");
                                    plugin.saveConfig();
                                }
                            }
                        }
                    }
                } else {
                    cs.sendMessage("That sub-command does not exist.");
                    return true;
                }
            }
        }
        return true;
    }
}