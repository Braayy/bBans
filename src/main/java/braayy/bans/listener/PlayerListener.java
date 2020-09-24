package braayy.bans.listener;

import braayy.bans.Bans;
import braayy.bans.Util;
import braayy.bans.model.BanInfo;
import braayy.bans.model.MuteInfo;
import braayy.bans.service.CacheService;
import braayy.bans.service.MessageService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class PlayerListener implements Listener {

    private final MessageService messageService;
    private final CacheService cacheService;

    public PlayerListener(Bans plugin) {
        this.messageService = plugin.getMessageService();
        this.cacheService = plugin.getCacheService();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        BanInfo info = this.cacheService.isBanned(event.getUniqueId());

        if (info != null) {
            if (info.getEnd() == -1) {
                String[] kickMessage = this.messageService.get("ban-messages.permaban", "reason", info.getReason());

                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, String.join("\n", kickMessage));
                return;
            }

            String formattedEndDate = Util.getFormattedEndDate(info.getEnd());

            String[] kickMessage = this.messageService.get("ban-messages.tempban", "reason", info.getReason(), "end", formattedEndDate);

            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, String.join("\n", kickMessage));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        MuteInfo info = this.cacheService.isMuted(event.getPlayer().getUniqueId());

        if (info != null) {
            event.setCancelled(true);

            if (info.getEnd() == -1) {
                this.messageService.sendMessage(event.getPlayer(), "mute-messages.permamute", "reason", info.getReason());
            } else {
                String formattedEndDate = Util.getFormattedEndDate(info.getEnd());

                this.messageService.sendMessage(event.getPlayer(), "mute-messages.tempmute", "reason", info.getReason(), "end", formattedEndDate);
            }
        }
    }

}