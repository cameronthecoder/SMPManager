package us.cameron.pvpguard;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class Main extends JavaPlugin {
    public void loadConfiguration() {
        //See "Creating you're defaults"

        this.getConfig().options().copyDefaults(true); // NOTE: You do not have to use "plugin." if the class extends the java plugin
        //Save the config whenever you manipulate it
        this.saveConfig();


    }
    @Override
    public void onEnable() {
        loadConfiguration();
        getServer().getPluginManager().registerEvents(new Events(this), this);
        this.getCommand("region").setExecutor(new Commands(this));
    }


}
