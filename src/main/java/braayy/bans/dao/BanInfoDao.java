package braayy.bans.dao;

import braayy.bans.Bans;
import braayy.bans.model.BanInfo;

import java.util.UUID;

public abstract class BanInfoDao extends Dao<UUID, BanInfo> {
    public BanInfoDao(Bans bans) {
        super(bans);
    }
}