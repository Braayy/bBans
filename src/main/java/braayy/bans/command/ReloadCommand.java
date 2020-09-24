package braayy.bans.command;

import braayy.bans.Bans;
import braayy.bans.service.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    private final MessageService messageService;

    public ReloadCommand(Bans plugin) {
        this.messageService = plugin.getMessageService();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        this.messageService.disable();
        this.messageService.enable();

        this.messageService.sendMessage(sender, "command.reload.success");

        return true;
    }

}
