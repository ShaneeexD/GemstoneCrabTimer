package com.gimserenity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.awt.Color;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GemstoneCrabUtilTest
{
	@Mock
	Client mockClient;

	@Mock
	ConfigManager configManager;

	@InjectMocks
	GemstoneCrabUtil util;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

	@Test
	public void testSendChatMessge()
	{
		var testMessage = "Test message";
		var expectedMessage = "[Gemstone Crab] Test message";

		util.sendChatMessage(Color.GREEN, testMessage, true);

		verify(mockClient).addChatMessage(eq(ChatMessageType.GAMEMESSAGE), anyString(), contains(expectedMessage), anyString());
	}

	@Test
	public void testDoesNotSendChatMessage()
	{
		util.sendChatMessage(null, "", false);

		verify(mockClient, times(0)).addChatMessage(ChatMessageType.GAMEMESSAGE, "", "", "");
	}
}
