package com.gimserenity;

import java.awt.Color;

import javax.inject.Inject;

import com.google.inject.Singleton;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.config.ConfigManager;

@Slf4j
@Singleton
public class GemstoneCrabUtil {
    @Inject
    private ConfigManager configManager;

    @Inject
    private Client client;

    public int loadConfigValue(String configGroup, String key){
        Integer savedValue = configManager.getRSProfileConfiguration(configGroup, key, Integer.class);
        int count = 0;
        if (savedValue != null)
        {
            try
            {
                count = savedValue;
            }
            catch (NumberFormatException e)
            {
                log.warn("Failed to parse saved value with key {}", key);
            }
        }
        return count;
    }

    /*
	 * Send a chat message with the specified color and message
	 * Only sends if the feature is enabled in the config
	 */
	public void sendChatMessage(Color color, String message, boolean isEnabled) {
		if (!isEnabled) {
			return;
		}
		String formattedMessage = new ChatMessageBuilder()
			.append(color, String.format( "[Gemstone Crab] %s", message))
			.build();
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", formattedMessage, "");
	}
}
