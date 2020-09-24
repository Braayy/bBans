package braayy.bans.command;

import braayy.bans.Bans;
import braayy.bans.dao.MuteInfoDao;
import braayy.bans.model.MuteInfo;
import braayy.bans.service.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

@SuppressWarnings("deprecation")
public class MuteCommand implements CommandExecutor {

    private final Bans plugin;
    private final MessageService messageService;
    private final MuteInfoDao muteInfoDao;

    public MuteCommand(Bans plugin) {
        this.plugin = plugin;

        this.muteInfoDao = plugin.getMuteInfoDao();
        this.messageService = plugin.getMessageService();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 2) {
            this.messageService.sendMessage(sender, "command.mute.usage");

            return true;
        }

        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (!target.hasPlayedBefore()) {
            this.messageService.sendMessage(sender, "command.player-not-found", "player", args[0]);

            return true;
        }

        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        this.plugin.async(() -> {
            MuteInfo info = this.muteInfoDao.load(target.getUniqueId());

            if (info != null) {
                if (info.getEnd() == -1) {
                    this.messageService.sendMessage(sender, "command.mute.player-already-muted", "player", target.getName());

                    return;
                }

                info.setReason(reason);
                info.setEnd(-1);

                this.muteInfoDao.update(info);
            } else {
                info = new MuteInfo(target.getUniqueId(), reason, -1);

                this.muteInfoDao.create(info);
            }

            this.messageService.sendMessage(sender, "command.mute.muted", "player", target.getName(), "reason", reason);
        });

        return true;
    }
}
