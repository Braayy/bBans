package braayy.bans.command;

import braayy.bans.Bans;
import braayy.bans.dao.BanInfoDao;
import braayy.bans.model.BanInfo;
import braayy.bans.service.CacheService;
import braayy.bans.service.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@SuppressWarnings("deprecation")
public class UnbanCommand implements CommandExecutor {

    private final Bans plugin;

    private final MessageService messageService;
    private final CacheService cacheService;

    private final BanInfoDao banInfoDao;

    public UnbanCommand(Bans plugin) {
        this.plugin = plugin;

        this.messageService = plugin.getMessageService();
        this.cacheService = plugin.getCacheService();

        this.banInfoDao = plugin.getBanInfoDao();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1) {
            this.messageService.sendMessage(sender, "command.unban.usage");

            return true;
        }

        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (!target.hasPlayedBefore()) {
            this.messageService.sendMessage(sender, "command.player-not-found", "player", args[0]);

            return true;
        }

        this.plugin.async(() -> {
            BanInfo info = this.banInfoDao.load(target.getUniqueId());

            if (info == null) {
                this.messageService.sendMessage(sender, "command.unban.player-already-unbanned", "player", target.getName());

                return;
            }

            this.banInfoDao.delete(info);
            this.cacheService.uncacheBan(target.getUniqueId());

            this.messageService.sendMessage(sender, "command.unban.unbanned", "player", target.getName());
        });

        return true;
    }
}