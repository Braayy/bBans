package braayy.bans.service.impl;

import braayy.bans.Bans;
import braayy.bans.dao.BanInfoDao;
import braayy.bans.dao.MuteInfoDao;
import braayy.bans.model.BanInfo;
import braayy.bans.model.MuteInfo;
import braayy.bans.service.CacheService;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CacheServiceImpl extends CacheService {

    private MuteInfoDao muteInfoDao;
    private BanInfoDao banInfoDao;

    private final Map<UUID, MuteInfo> muteInfoMap;
    private final Map<UUID, BanInfo> banInfoMap;

    private BukkitTask timer;

    public CacheServiceImpl(Bans plugin) {
        super(plugin);

        this.muteInfoMap = new HashMap<>();
        this.banInfoMap = new HashMap<>();
    }

    @Override
    public void enable() {
        this.muteInfoDao = this.plugin.getMuteInfoDao();
        this.banInfoDao = this.plugin.getBanInfoDao();

        this.timer = this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, () -> {
            this.muteInfoMap.clear();
            this.banInfoMap.clear();
        }, 20 * 60 * 5, 0);
    }

    @Override
    public void disable() {
        if (this.timer != null) {
            this.timer.cancel();
        }
    }

    @Override
    public MuteInfo isMuted(UUID uuid) {
        MuteInfo info = this.muteInfoMap.get(uuid);

        if (info != null) {
            if (info.getEnd() == -1) return info;

            long diff = info.getEnd() - System.currentTimeMillis();

            if (diff <= 0) {
                this.muteInfoMap.remove(uuid);

                this.muteInfoDao.delete(info);

                return null;
            }

            return info;
        }

        info = this.muteInfoDao.load(uuid);

        if (info != null) {
            this.muteInfoMap.put(uuid, info);

            if (info.getEnd() == -1) return info;

            long diff = info.getEnd() - System.currentTimeMillis();

            if (diff <= 0) {
                this.muteInfoMap.remove(uuid);

                this.muteInfoDao.delete(info);

                return null;
            }

            return info;
        }

        return null;
    }

    @Override
    public BanInfo isBanned(UUID uuid) {
        BanInfo info = this.banInfoMap.get(uuid);

        if (info != null) {
            if (info.getEnd() == -1) return info;

            long diff = info.getEnd() - System.currentTimeMillis();

            if (diff <= 0) {
                this.banInfoMap.remove(uuid);

                this.banInfoDao.delete(info);

                return null;
            }

            return info;
        }

        info = this.banInfoDao.load(uuid);

        if (info != null) {
            this.banInfoMap.put(uuid, info);

            if (info.getEnd() == -1) return info;

            long diff = info.getEnd() - System.currentTimeMillis();

            if (diff <= 0) {
                this.banInfoMap.remove(uuid);

                this.banInfoDao.delete(info);

                return null;
            }

            return info;
        }

        return null;
    }

    @Override
    public void cacheBan(UUID uuid, BanInfo info) {
        this.banInfoMap.put(uuid, info);
    }

    @Override
    public void uncacheBan(UUID uuid) {
        this.banInfoMap.remove(uuid);
    }

    @Override
    public void cacheMute(UUID uuid, MuteInfo info) {
        this.muteInfoMap.put(uuid, info);
    }

    @Override
    public void uncacheMute(UUID uuid) {
        this.muteInfoMap.remove(uuid);
    }

}