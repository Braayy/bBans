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

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("deprecation")
public class TempmuteCommand implements CommandExecutor {

    private final Bans plugin;

    private final MessageService messageService;
    private final CacheService cacheService;

    private final MuteInfoDao muteInfoDao;

    public TempmuteCommand(Bans plugin) {
        this.plugin = plugin;

        this.messageService = plugin.getMessageService();
        this.cacheService = plugin.getCacheService();

        this.muteInfoDao = plugin.getMuteInfoDao();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 3) {
            this.messageService.sendMessage(sender, "command.tempmute.usage");

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
            MuteInfo info = this.muteInfoDao.load(target.getUniqueId());

            if (info != null) {
                info.setReason(reason);
                info.setEnd(end);

                this.muteInfoDao.update(info);
            } else {
                info = new MuteInfo(target.getUniqueId(), reason, end);

                this.muteInfoDao.create(info);
            }

            this.cacheService.cacheMute(target.getUniqueId(), info);

            this.messageService.sendMessage(sender, "command.tempmute.tempmuted", "player", target.getName(), "reason", info.getReason(), "time", Integer.parseInt(args[1]));
        });

        return false;
    }

}