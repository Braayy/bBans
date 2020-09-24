package braayy.bans;

import braayy.bans.command.*;
import braayy.bans.dao.BanInfoDao;
import braayy.bans.dao.MuteInfoDao;
import braayy.bans.dao.mysql.MySQLBanInfoDao;
import braayy.bans.dao.mysql.MySQLMuteInfoDao;
import braayy.bans.dao.sqlite.SQLiteBanInfoDao;
import braayy.bans.dao.sqlite.SQLiteMuteInfoDao;
import braayy.bans.listener.PlayerListener;
import braayy.bans.service.CacheService;
import braayy.bans.service.DatabaseService;
import braayy.bans.service.MessageService;
import braayy.bans.service.impl.CacheServiceImpl;
import braayy.bans.service.impl.DatabaseServiceImpl;
import braayy.bans.service.impl.MessageServiceImpl;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class Bans extends JavaPlugin {

    private DatabaseService databaseService;
    private MessageService messageService;
    private CacheService cacheService;

    private BanInfoDao banInfoDao;
    private MuteInfoDao muteInfoDao;

    @Override
    public void onEnable() {
        this.initServices();

        this.initDaos();

        this.enableServices();

        this.createTables();

        this.registerListeners();

        this.registerCommands();
    }

    @Override
    public void onDisable() {
        this.disableServices();
    }

    private void initServices() {
        this.databaseService = new DatabaseServiceImpl(this);
        this.messageService = new MessageServiceImpl(this);
        this.cacheService = new CacheServiceImpl(this);
    }

    private void enableServices() {
        try {
            this.databaseService.enable();
            this.messageService.enable();
            this.cacheService.enable();
        } catch (Exception exception) {
            this.getLogger().log(Level.SEVERE, "Something went wrong while enabling services", exception);
        }
    }

    private void disableServices() {
        try {
            this.databaseService.disable();
            this.messageService.disable();
            this.cacheService.disable();
        } catch (Exception exception) {
            this.getLogger().log(Level.SEVERE, "Something went wrong while disabling services", exception);
        }
    }

    private void initDaos() {
        String type = this.getConfig().getString("Storage.Type");

        if (type.equalsIgnoreCase("mysql")) {
            this.banInfoDao = new MySQLBanInfoDao(this);
            this.muteInfoDao = new MySQLMuteInfoDao(this);
        } else {
            this.banInfoDao = new SQLiteBanInfoDao(this);
            this.muteInfoDao = new SQLiteMuteInfoDao(this);
        }
    }

    private void createTables() {
        this.banInfoDao.createTable();
        this.muteInfoDao.createTable();
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    private void registerCommands() {
        this.getCommand("bbansreload").setExecutor(new ReloadCommand(this));

        this.getCommand("ban").setExecutor(new BanCommand(this));
        this.getCommand("tempban").setExecutor(new TempbanCommand(this));
        this.getCommand("unban").setExecutor(new UnbanCommand(this));

        this.getCommand("mute").setExecutor(new MuteCommand(this));
        this.getCommand("tempmute").setExecutor(new TempmuteCommand(this));
        this.getCommand("unmute").setExecutor(new UnmuteCommand(this));
    }

    public void async(Runnable run) {
        this.getServer().getScheduler().runTaskAsynchronously(this, run);
    }

    public void sync(Runnable run) {
        this.getServer().getScheduler().runTask(this, run);
    }

    public BanInfoDao getBanInfoDao() {
        return banInfoDao;
    }

    public MuteInfoDao getMuteInfoDao() {
        return muteInfoDao;
    }

    public DatabaseService getDatabaseService() {
        return databaseService;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public CacheService getCacheService() {
        return cacheService;
    }
}