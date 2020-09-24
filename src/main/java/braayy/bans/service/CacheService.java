package braayy.bans.service;

import braayy.bans.Bans;
import braayy.bans.model.BanInfo;
import braayy.bans.model.MuteInfo;

import java.util.UUID;

public abstract class CacheService extends Service {

    public CacheService(Bans plugin) {
        super(plugin);
    }

    public abstract MuteInfo isMuted(UUID uuid);

    public abstract BanInfo isBanned(UUID uuid);

    public abstract void cacheBan(UUID uuid, BanInfo info);

    public abstract void uncacheBan(UUID uuid);

    public abstract void cacheMute(UUID uuid, MuteInfo info);

    public abstract void uncacheMute(UUID uuid);
}
