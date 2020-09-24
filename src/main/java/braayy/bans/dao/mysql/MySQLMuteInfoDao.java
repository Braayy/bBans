package braayy.bans.dao.mysql;

import braayy.bans.Bans;
import braayy.bans.dao.MuteInfoDao;
import braayy.bans.model.MuteInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.logging.Level;

public class MySQLMuteInfoDao extends MuteInfoDao {

    public MySQLMuteInfoDao(Bans bans) {
        super(bans);
    }

    @Override
    public void createTable() {
        try (Connection connection = this.databaseService.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "CREATE TABLE IF NOT EXISTS bbans_mutes(uuid BINARY(16) not null, reason VARCHAR(255) not null, end BIGINT not null, PRIMARY KEY(uuid))"
             )) {

            stmt.executeUpdate();
        } catch (Exception exception) {
            this.databaseService.getLogger().log(Level.SEVERE, "Something went while creating table bbans_mutes", exception);
        }
    }

    @Override
    public void create(MuteInfo info) {
        try (Connection connection = this.databaseService.getConnection();
             PreparedStatement stmt = connection.prepareStatement("INSERT INTO bbans_mutes VALUES(UNHEX(?),?,?)")) {

            stmt.setString(1, info.getSqlUUID());
            stmt.setString(2, info.getReason());
            stmt.setLong(3, info.getEnd());

            stmt.executeUpdate();
        } catch (Exception exception) {
            this.databaseService.getLogger().log(Level.SEVERE, "Something went while creating " + info.getUuid() + "' ban info to bbans_mutes", exception);
        }
    }

    @Override
    public void update(MuteInfo info) {
        try (Connection connection = this.databaseService.getConnection();
             PreparedStatement stmt = connection.prepareStatement("UPDATE bbans_mutes SET reason = ?, end = ? WHERE uuid = UNHEX(?)")) {

            stmt.setString(1, info.getReason());
            stmt.setLong(2, info.getEnd());
            stmt.setString(3, info.getSqlUUID());

            stmt.executeUpdate();
        } catch (Exception exception) {
            this.databaseService.getLogger().log(Level.SEVERE, "Something went while updating " + info.getUuid() + "' ban info to bbans_mutes", exception);
        }
    }

    @Override
    public void delete(MuteInfo info) {
        try (Connection connection = this.databaseService.getConnection();
             PreparedStatement stmt = connection.prepareStatement("DELETE FROM bbans_mutes WHERE uuid = UNHEX(?)")) {

            stmt.setString(1, info.getSqlUUID());

            stmt.executeUpdate();
        } catch (Exception exception) {
            this.databaseService.getLogger().log(Level.SEVERE, "Something went while deleting " + info.getUuid() + "' ban info to bbans_mutes", exception);
        }
    }

    @Override
    public MuteInfo load(UUID uuid) {
        try (Connection connection = this.databaseService.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT reason, end FROM bbans_mutes WHERE uuid = UNHEX(?)")) {

            stmt.setString(1, uuid.toString().replace("-", ""));

            try (ResultSet set = stmt.executeQuery()) {
                if (set.next()) {
                    String reason = set.getString("reason");
                    long end = set.getLong("end");

                    return new MuteInfo(uuid, reason, end);
                }
            }
        } catch (Exception exception) {
            this.databaseService.getLogger().log(Level.SEVERE, "Something went while deleting " + uuid + "' ban info to bbans_mutes", exception);
        }

        return null;
    }
}
