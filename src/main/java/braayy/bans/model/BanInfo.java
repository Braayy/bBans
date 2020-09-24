package braayy.bans.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class BanInfo {

    private final UUID uuid;
    private String reason;
    private long end;

    public String getSqlUUID() {
        return this.uuid.toString().replace("-", "");
    }

}