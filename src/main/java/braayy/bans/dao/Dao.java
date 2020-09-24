package braayy.bans.dao;

import braayy.bans.Bans;
import braayy.bans.service.DatabaseService;

public abstract class Dao<K, V> {

    protected final DatabaseService databaseService;

    public Dao(Bans plugin) {
        this.databaseService = plugin.getDatabaseService();
    }

    public abstract void createTable();

    public abstract void create(V value);

    public abstract void update(V value);

    public abstract void delete(V value);

    public abstract V load(K key);

}