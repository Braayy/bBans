package braayy.bans.service.impl;

import braayy.bans.Bans;
import braayy.bans.service.MessageService;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class MessageServiceImpl extends MessageService {

    private final Map<String, String[]> messageMap;

    public MessageServiceImpl(Bans plugin) {
        super(plugin);

        this.messageMap = new HashMap<>();
    }

    @Override
    public void enable() {
        try {
            File messagesFile = new File(this.plugin.getDataFolder(), "messages.yml");

            if (!messagesFile.exists()) {
                this.plugin.saveResource("messages.yml", false);
            }

            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(messagesFile);

            this.searchMessages(yaml);
        } catch (Exception ex) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not load messages.yml", ex);
        }
    }

    @Override
    public void disable() {
        this.messageMap.clear();
    }

    @Override
    public String[] get(String key, Object... args) {
        if (args.length % 2 > 0) {
            throw new IllegalArgumentException("args length must be even");
        }

        String[] messageArray = this.messageMap.get(key);

        Objects.requireNonNull(messageArray, key + " message was not found");

        String[] appliedArray = new String[messageArray.length];

        for (int i = 0 ; i < messageArray.length; i++) {
            appliedArray[i] = this.applyParameters(messageArray[i], args);
        }

        return appliedArray;
    }

    @Override
    public String getAsString(String key, Object... args) {
        String[] messageArray = this.get(key, args);

        if (messageArray.length > 1) {
            throw new IllegalArgumentException(key + " can only be a one line message");
        }

        return messageArray[0];
    }

    @Override
    public void sendMessage(CommandSender sender, String key, Object... args) {
        String[] messageArray = this.get(key, args);

        sender.sendMessage(messageArray);
    }

    private void searchMessages(ConfigurationSection section) {
        for (String key : section.getKeys(false)) {
            String path = section.getCurrentPath() + '.' + key;
            if (path.startsWith(".")) path = path.substring(1);

            if (section.isConfigurationSection(key)) {
                this.searchMessages(section.getConfigurationSection(key));
            } else if (section.isString(key)) {
                String message = ChatColor.translateAlternateColorCodes('&', section.getString(key));

                this.messageMap.put(path, new String[] { message });
            } else if (section.isList(key)) {
                List<String> messageList = section.getStringList(key);

                messageList.replaceAll(string -> ChatColor.translateAlternateColorCodes('&', string));

                this.messageMap.put(path, messageList.toArray(new String[0]));
            }
        }
    }

    private String applyParameters(String string, Object... args) {
        for (int i = 0; i < args.length; i += 2) {
            String argKey = (String) args[i];
            String argValue = args[i + 1].toString();

            string = string.replace("{" + argKey + "}", argValue);
        }

        return string;
    }

}