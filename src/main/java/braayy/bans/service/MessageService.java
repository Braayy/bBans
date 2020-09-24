package braayy.bans.service;

import braayy.bans.Bans;
import org.bukkit.command.CommandSender;

public abstract class MessageService extends Service {

    public MessageService(Bans plugin) {
        super(plugin);
    }

    public abstract String[] get(String key, Object... args);

    public abstract String getAsString(String key, Object... args);

    public abstract void sendMessage(CommandSender sender, String key, Object... args);
}
