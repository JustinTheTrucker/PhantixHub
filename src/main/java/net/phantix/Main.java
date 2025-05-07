package net.phantix;

import net.phantix.commands.OpMe;
import net.phantix.listeners.*;
import net.phantix.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new AntiBreak(), this);
        getServer().getPluginManager().registerEvents(new Weather(), this);
        getServer().getPluginManager().registerEvents(new JoinItems(), this);
        getServer().getPluginManager().registerEvents(new PlayerHider(), this);
        getServer().getPluginManager().registerEvents(new ItemLock(), this);
        getServer().getPluginManager().registerEvents(new VoidTeleport(), this);
        getServer().getPluginManager().registerEvents(new SilentJoinQuit(), this);
        getServer().getPluginManager().registerEvents(new DisableHunger(), this);
        getServer().getPluginManager().registerEvents(new DisableDamage(), this);
        getServer().getPluginManager().registerEvents(new ServerSelector(this), this);
        getServer().getPluginManager().registerEvents(new TabListManager(), this);

        this.getCommand("opme").setExecutor(new OpMe());

        getLogger().info("PhantixHub has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("PhantixHub has been disabled!");
    }
}
