package com.gimserenity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.client.config.ConfigManager;

@ExtendWith(MockitoExtension.class)
public class GemstoneCrabPluginTest {

    @Mock
	Client mockClient;

	@Mock
	ConfigManager configManager;

	@InjectMocks
	GemstoneCrabTimerPlugin plugin;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPlayerInTop3PlayerNamesSuccess() {
        var mockPlayer = mock(Player.class);
        when(mockPlayer.getName()).thenReturn("Pi no");
        when(mockClient.getLocalPlayer()).thenReturn(mockPlayer);

        boolean result = plugin.isTop3Player("The top three crab crushers were Pi no, Mod Ash, & GIM Serenity!");

        assertTrue(result);

        result = plugin.isTop3Player("The top three crab crushers were GIM Serenity, Pi no, & Mod Ash!");

        assertTrue(result);

        result = plugin.isTop3Player("The top three crab crushers were Mod Ash, GIM Serenity, & Pi no!");
        
        assertTrue(result);
    }

    @Test
    public void testPlayerInTop3PlayerNamesFailsWhenNameIsSimilar() {
        var mockPlayer = mock(Player.class);
        when(mockPlayer.getName()).thenReturn("Pino");
        when(mockClient.getLocalPlayer()).thenReturn(mockPlayer);

        boolean result = plugin.isTop3Player("The top three crab crushers were PinoIron, GIM Serenity, & Mod Ash!");

        assertFalse(result);
    }

    @Test
    public void testPlayerInTop3PlayerNamesFailsWhenNameIsNotIncluded() {
        var mockPlayer = mock(Player.class);
        when(mockPlayer.getName()).thenReturn("GIM Serenity");
        when(mockClient.getLocalPlayer()).thenReturn(mockPlayer);

        boolean result = plugin.isTop3Player("The top three crab crushers were Pi no, PinoIron, & Serenity!");

        assertFalse(result);
    }
}
