package braayy.bans.service;

import braayy.bans.Bans;

import java.sql.Connection;
import java.util.logging.Logger;

public abstract class DatabaseService extends Service {

    public DatabaseService(Bans plugin) {
        super(plugin);
    }

    public abstract Connection getConnection();

    public Logger getLogger() {
        return this.plugin.getLogger();
    }

}