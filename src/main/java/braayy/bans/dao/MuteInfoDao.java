package braayy.bans.dao;

import braayy.bans.Bans;
import braayy.bans.model.MuteInfo;

import java.util.UUID;

public abstract class MuteInfoDao extends Dao<UUID, MuteInfo> {
    public MuteInfoDao(Bans bans) {
        super(bans);
    }
}