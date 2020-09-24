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
import org.bukkit.entity.Player;

import java.util.Arrays;

@SuppressWarnings("deprecation")
public class BanCommand implements CommandExecutor {

    private final Bans plugin;

    private final MessageService messageService;
    private final CacheService cacheService;

    private final BanInfoDao banInfoDao;

    public BanCommand(Bans plugin) {
        this.plugin = plugin;

        this.messageService = plugin.getMessageService();
        this.cacheService = plugin.getCacheService();

        this.banInfoDao = plugin.getBanInfoDao();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            this.messageService.sendMessage(sender, "command.ban.usage");

            return true;
        }

        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (!target.hasPlayedBefore()) {
            this.messageService.sendMessage(sender, "command.player-not-found", "player", args[0]);

            return true;
        }

        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        this.plugin.async(() -> {
            BanInfo info = this.banInfoDao.load(target.getUniqueId());

            if (info != null) {
                if (info.getEnd() == -1) {
                    this.messageService.sendMessage(sender, "command.ban.player-already-banned", "player", target.getName());

                    return;
                }

                info.setReason(reason);
                info.setEnd(-1);

                this.banInfoDao.update(info);
            } else {
                info = new BanInfo(target.getUniqueId(), reason, -1);

                this.banInfoDao.create(info);
            }

            this.cacheService.cacheBan(target.getUniqueId(), info);

            this.messageService.sendMessage(sender, "command.ban.banned", "player", target.getName(), "reason", reason);

            if (target.isOnline()) {
                this.plugin.sync(() -> {
                    String[] kickMessage = this.messageService.get("ban-messages.permaban", "reason", reason);

                    ((Player) target).kickPlayer(String.join("\n", kickMessage));
                });
            }
        });

        return true;
    }

}
