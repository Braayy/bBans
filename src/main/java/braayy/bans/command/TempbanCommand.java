package braayy.bans.command;

import braayy.bans.Bans;
import braayy.bans.Util;
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
import java.util.concurrent.TimeUnit;

@SuppressWarnings("deprecation")
public class TempbanCommand implements CommandExecutor {

    private final Bans plugin;

    private final MessageService messageService;
    private final CacheService cacheService;

    private final BanInfoDao banInfoDao;

    public TempbanCommand(Bans plugin) {
        this.plugin = plugin;

        this.messageService = plugin.getMessageService();
        this.cacheService = plugin.getCacheService();

        this.banInfoDao = plugin.getBanInfoDao();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 3) {
            this.messageService.sendMessage(sender, "command.tempban.usage");

            return true;
        }

        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (!target.hasPlayedBefore()) {
            this.messageService.sendMessage(sender, "command.player-not-found", "player", args[0]);

            return true;
        }

        final long end;

        try {
            end = TimeUnit.MINUTES.toMillis(Integer.parseInt(args[1])) + System.currentTimeMillis();
        } catch (Exception ignore) {
            this.messageService.sendMessage(sender, "commmand.invalid-time");

            return true;
        }

        String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        this.plugin.async(() -> {
            BanInfo info = this.banInfoDao.load(target.getUniqueId());

            if (info != null) {
                info.setReason(reason);
                info.setEnd(end);

                this.banInfoDao.update(info);
            } else {
                info = new BanInfo(target.getUniqueId(), reason, end);

                this.banInfoDao.create(info);
            }

            this.cacheService.cacheBan(target.getUniqueId(), info);

            this.messageService.sendMessage(sender, "command.tempban.tempbanned", "player", target.getName(), "reason", info.getReason(), "time", Integer.parseInt(args[1]));

            if (target.isOnline()) {
                this.plugin.sync(() -> {
                    String[] kickMessage = this.messageService.get("ban-messages.tempban", "reason", reason, "end", Util.getFormattedEndDate(end));

                    ((Player) target).kickPlayer(String.join("\n", kickMessage));
                });
            }
        });

        return false;
    }

}