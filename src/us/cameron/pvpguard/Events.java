package us.cameron.pvpguard;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;


import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

public class Events implements Listener {
    Main plugin;
    public HashMap<String, Long> cooldowns = new HashMap<String, Long>();
    public Events(Main instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(event.getEntity().getKiller() instanceof Player || event.getEntity().getKiller() != null) {
            cooldowns.put(event.getEntity().getName(),      System.currentTimeMillis());
        }
    };

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("§7Regions have now been added! You may use them to protect your precious items from getting stolen. If you would like to learn more about regions and how to use them, type /region.");
    }

    @EventHandler
    public void onChestOpen(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.CHEST && event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            int index = 1;
            ConfigurationSection inventorySection = plugin.getConfig().getConfigurationSection("Regions");
            for(String key : inventorySection.getKeys(false)) {
                ConfigurationSection item = inventorySection.getConfigurationSection(key);
                if(event.getPlayer().getLocation().getChunk().getX() == Integer.parseInt(item.getString("ChunkX")) && event.getPlayer().getLocation().getChunk().getZ() == Integer.parseInt(item.getString("ChunkZ" ))) {
                    if (!(item.getString("Owner").equals(event.getPlayer().getUniqueId().toString()) || item.getStringList("Whitelist").contains(event.getPlayer().getUniqueId().toString()))) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage("§1[§9Regions§1] §8>> §7You don't have permission to do that.");
                    }
                }
                }
        }
    }

    @EventHandler
    public void onPlayerHitEvent(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                if (cooldowns.containsKey(player.getName())) {
                    // 1200
                    long secondsLeft = ((cooldowns.get(player.getName()) / 1000) + 1200) - (System.currentTimeMillis() / 1000);
                    if (secondsLeft > 0) {
                        event.setCancelled(true);
                        DecimalFormat df = new DecimalFormat("#.#");
                        DecimalFormat tf = new DecimalFormat("#.#");
                        Player damager = (Player) event.getDamager();
                        if ((secondsLeft / 60) > 0) {
                            damager.sendMessage("§c§lPvP §r§8>> §r§9You can't hit this player for another " + df.format((secondsLeft / 60)) + " minutes.");
                        } else {
                            Bukkit.broadcastMessage(Double.toString(secondsLeft / 60));
                            damager.sendMessage("§c§lPvP §r§8>> §r§9You can't hit this player for another " + Double.toString(secondsLeft / 60) + " of a minute.");
                        }
                    } else {
                        cooldowns.remove(player.getName());
                    }
                }
            }
    }


    }}
