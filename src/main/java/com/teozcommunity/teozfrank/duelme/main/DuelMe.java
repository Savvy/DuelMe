package com.teozcommunity.teozfrank.duelme.main;

import com.teozcommunity.teozfrank.duelme.commands.DuelAdminCommand;
import com.teozcommunity.teozfrank.duelme.commands.DuelCommand;
import com.teozcommunity.teozfrank.duelme.events.*;
import com.teozcommunity.teozfrank.duelme.util.Locations;
import com.teozcommunity.teozfrank.duelme.util.SendConsoleMessage;
import com.teozcommunity.teozfrank.duelme.util.UpdateChecker;
import com.teozcommunity.teozfrank.duelme.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Frank
 * Date: 19/07/13
 * Time: 21:53
 * To change this template use File | Settings | File Templates.
 */
public class DuelMe extends JavaPlugin {

    //String to hold our plugin prefix
    public String pluginPrefix;

    //boolean to keep track of if a duel is in progress
    public boolean inProgress;

    //String to hold the plugin version
    public String version;

    //String to to keep track of the dueling status
    public String duelStatus;

    //hashmap to keep track of the dueling requests
    public HashMap<String,String> duelRequests;

    //arraylist to hold the dueling players
    public ArrayList<Player> duelingPlayers;

    //arraylist to hold the spectating players
    public ArrayList<Player> spectatingPlayers;

    //arraylist to hold the frozen players (before a duel starts)
    public ArrayList<Player> frozenPlayers;

    //our util class
    public Util util;

    //our locations class
    public Locations locations;

    //our update checker class
    public UpdateChecker updateChecker;

    //our colored console message class
    public SendConsoleMessage sendConsoleMessage;


    @Override
    public void onEnable(){
        this.sendConsoleMessage = new SendConsoleMessage(this);
        this.version = this.getDescription().getVersion();// called early so that any classes or methods that use this is available
        this.sendConsoleMessage.info("Enabling");
        this.util = new Util(this);
        this.locations = new Locations(this);
        this.updateChecker = new UpdateChecker(this,"http://dev.bukkit.org/bukkit-plugins/rank-list/files.rss");
        if(this.updateChecker.updateAvailable()&&this.getConfig().getBoolean("duelme.checkforupdates")){
            this.sendConsoleMessage.info("A new version of this plugin is available: " + this.updateChecker.getVersion());
            this.sendConsoleMessage.info("Download it here " + this.updateChecker.getLink());
        }

        if(!(new File(getDataFolder(), "config.yml")).exists())
        {
            saveDefaultConfig();
        }
        this.pluginPrefix = ChatColor.GOLD+"[DuelMe] ";
        this.inProgress = false;
        this.duelStatus = "WAITING";
        this.duelRequests = new HashMap<String, String>();
        this.duelingPlayers = new ArrayList<Player>();
        this.spectatingPlayers = new ArrayList<Player>();
        this.frozenPlayers = new ArrayList<Player>();
        this.registerCommands();
        this.registerEvents();
        this.sendConsoleMessage.info("Enabled!");
    }

    @Override
    public void onDisable(){

        Bukkit.getScheduler().cancelTasks(this);
    }

   /*
    * Register our plugin commands
    */
    public void registerCommands(){
       getCommand("duel").setExecutor(new DuelCommand(this));
       getCommand("dueladmin").setExecutor(new DuelAdminCommand(this));
    }

    public void registerEvents(){
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerMove(this), this);
        pm.registerEvents(new PlayerDeath(this),this);
        pm.registerEvents(new PlayerBreakBlock(this),this);
        pm.registerEvents(new PlayerQuit(this),this);
        pm.registerEvents(new PlayerTeleport(this),this);
        pm.registerEvents(new PlayerRespawn(this),this);
        pm.registerEvents(new PlayerJoin(this),this);
    }

}
