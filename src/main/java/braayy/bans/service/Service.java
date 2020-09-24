package braayy.bans.service;

import braayy.bans.Bans;

public abstract class Service {

    protected final Bans plugin;

    public Service(Bans plugin) {
        this.plugin = plugin;
    }

    public abstract void enable();

    public abstract void disable();

}