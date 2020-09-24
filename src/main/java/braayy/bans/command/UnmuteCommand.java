package braayy.bans.command;

import braayy.bans.Bans;
import braayy.bans.dao.MuteInfoDao;
import braayy.bans.model.MuteInfo;
import braayy.bans.service.CacheService;
import braayy.bans.service.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@SuppressWarnings("deprecation")
public class UnmuteCommand implements CommandExecutor {

    private final Bans plugin;

    private final MessageService messageService;
    private final CacheService cacheService;

    private final MuteInfoDao muteInfoDao;

    public UnmuteCommand(Bans plugin) {
        this.plugin = plugin;

        this.messageService = plugin.getMessageService();
        this.cacheService = plugin.getCacheService();

        this.muteInfoDao = plugin.getMuteInfoDao();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1) {
            this.messageService.sendMessage(sender, "command.unmute.usage");

            return true;
        }

        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (!target.hasPlayedBefore()) {
            this.messageService.sendMessage(sender, "command.player-not-found", "player", args[0]);

            return true;
        }

        this.plugin.async(() -> {
            MuteInfo info = this.muteInfoDao.load(target.getUniqueId());

            if (info == null) {
                this.messageService.sendMessage(sender, "command.unmute.player-already-unmuted", "player", target.getName());

                return;
            }

            this.muteInfoDao.delete(info);
            this.cacheService.uncacheMute(target.getUniqueId());

            this.messageService.sendMessage(sender, "command.unmute.unmuted", "player", target.getName());
        });

        return true;
    }
}