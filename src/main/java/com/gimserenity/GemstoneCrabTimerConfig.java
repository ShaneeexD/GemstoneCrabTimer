package com.gimserenity;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup("gemstonecrab")
public interface GemstoneCrabTimerConfig extends Config
{
	@ConfigSection(
		name = "Notifications",
		description = "Notifications",
		position = 0
	)
	String notificationList = "notificationList";
	
	@ConfigItem(
		keyName = "enableNotifications",
		name = "Enable Notifications",
		description = "Enable desktop notifications when Gemstone Crab reaches HP threshold",
		section = notificationList
	)
	default boolean enableNotifications()
	{
		return true;
	}
	
	@Range(
		min = 1,
		max = 99
	)
	@ConfigItem(
		keyName = "hpThreshold",
		name = "HP Threshold %",
		description = "Send notification when Gemstone Crab HP reaches this percentage",
		section = notificationList
	)
	default int hpThreshold()
	{
		return 2;
	}
	
	@ConfigItem(
		keyName = "notificationMessage",
		name = "Notification Message",
		description = "Message to show in the notification",
		section = notificationList
	)
	default String notificationMessage()
	{
		return "Gemstone Crab HP threshold reached!";
	}

	@ConfigItem(
		keyName = "pulseScreen",
		name = "Pulse Screen",
		description = "Pulse the screen overlay when the tunnel is highlighted",
		section = notificationList
	)
	default boolean pulseScreen()
	{
		return true;
	}

	@ConfigItem(
		keyName = "pulseColor",
		name = "Pulse Color",
		description = "Color of the screen pulse",
		section = notificationList
	)
	default Color pulseColor()
	{
		return new Color(255, 0, 0, 128); // Semi-transparent red
	}

		
	@ConfigSection(
		name = "Highlights",
		description = "Highlight settings",
		position = 1
	)
	String highlights = "highlights";
	
	@ConfigItem(
		keyName = "highlightTunnel",
		name = "Highlight Tunnel",
		description = "Highlight the nearest tunnel after boss death",
		section = highlights
	)
	default boolean highlightTunnel()
	{
		return true;
	}
	
	@ConfigItem(
		keyName = "tunnelHighlightColor",
		name = "Tunnel Highlight Color",
		description = "Color to highlight the tunnel with",
		section = highlights
	)
	default Color tunnelHighlightColor()
	{
		return Color.GREEN;
	}

	@ConfigSection(
		name = "Current Fight Tracking",
		description = "Current Fight Information",
		position = 2
	)
	String currentFightTracking = "currentFightTracking";
	
	@ConfigItem(
		keyName = "showMainStats",
		name = "Show overlay",
		description = "Display damage, DPS, XP gained, duration and time left in the stats overlay",
		section = currentFightTracking,
		position = 0
	)
	default boolean showMainStats()
	{
		return true;
	}
	
	@ConfigItem(
		keyName = "displayTotalDamage",
		name = "Display total damage",
		description = "Display total damage dealt in the current fight",
		section = currentFightTracking,
		position = 1
	)
	default boolean displayTotalDamage()
	{
		return true;
	}
	
	@ConfigItem(
		keyName = "displayDps",
		name = "Display DPS",
		description = "Display damage per second in the current fight",
		section = currentFightTracking,
		position = 2
	)
	default boolean displayDps()
	{
		return true;
	}
	
	@ConfigItem(
		keyName = "displayXpGained",
		name = "Display XP gained",
		description = "Display XP gained in the current fight",
		section = currentFightTracking,
		position = 3
	)
	default boolean displayXpGained()
	{
		return true;
	}
	
	@ConfigItem(
		keyName = "displayDuration",
		name = "Display duration",
		description = "Display duration of the current fight",
		section = currentFightTracking,
		position = 4
	)
	default boolean displayDuration()
	{
		return true;
	}
	
	@ConfigItem(
		keyName = "displayTimeLeft",
		name = "Display time left",
		description = "Display estimated time left in the current fight",
		section = currentFightTracking,
		position = 5
	)
	default boolean displayTimeLeft()
	{
		return true;
	}

	@ConfigSection(
		name = "Stat Tracking",
		description = "Stat Tracking",
		position = 3
	)
	String statTracking = "statTracking";
	
	@ConfigSection(
		name = "Gem Tracking",
		description = "Gem Tracking",
		position = 4
	)
	String gemTracking = "gemTracking";

    @ConfigItem(
        keyName = "showOverlay",
        name = "Show overlay",
        description = "Show the gemstone crab count overlay",
		section = statTracking,
		position = 0
    )
    default boolean showOverlay()
    {
        return true;
    }

    @ConfigItem(
        keyName = "displayKillCount",
        name = "Display kill count",
        description = "Display total Gemstone Crab kill count in overlay",
		section = statTracking,
		position = 1
    )
    default boolean displayKillCount()
    {
        return true;
    }

	@ConfigItem(
        keyName = "displayMiningAttempts",
        name = "Display total mining attempts",
        description = "Display total Gemstone Crabs attempts at mining in overlay",
		section = statTracking,
		position = 2
    )
    default boolean displayMiningAttempts()
    {
        return false;
    }

	@ConfigItem(
        keyName = "displayMinedCount",
        name = "Display total successful",
        description = "Display total successful mining attempts at Gemstone Crabs in overlay",
		section = statTracking,
		position = 3
    )
    default boolean displayMinedCount()
    {
        return false;
    }

	@ConfigItem(
        keyName = "displayFailedMiningCount",
        name = "Display total failed",
        description = "Display total failed mining attempts at Gemstone Crabs in the overlay",
		section = statTracking,
		position = 4
    )
    default boolean displayFailedMiningCount()
    {
        return false;
    }

	@ConfigItem(
        keyName = "displayGemCount",
        name = "Display total gems mined",
        description = "Display total gems mined at Gemstone Crabs in the overlay",
		section = statTracking,
		position = 5
    )
    default boolean displayGemCount()
    {
        return false;
    }
	
	@ConfigItem(
		keyName = "showGemTracking",
		name = "Show overlay",
		description = "Display gem tracking section in the overlay",
		section = gemTracking,
		position = 0
	)
	default boolean showGemTracking()
	{
		return true;
	}
	
	@ConfigItem(
		keyName = "displayOpals",
		name = "Display opals",
		description = "Display opal count and percentage in the overlay",
		section = gemTracking,
		position = 1
	)
	default boolean displayOpals()
	{
		return true;
	}
	
	@ConfigItem(
		keyName = "displayJades",
		name = "Display jades",
		description = "Display jade count and percentage in the overlay",
		section = gemTracking,
		position = 2
	)
	default boolean displayJades()
	{
		return true;
	}
	
	@ConfigItem(
		keyName = "displayRedTopaz",
		name = "Display red topaz",
		description = "Display red topaz count and percentage in the overlay",
		section = gemTracking,
		position = 3
	)
	default boolean displayRedTopaz()
	{
		return true;
	}
	
	@ConfigItem(
		keyName = "displaySapphires",
		name = "Display sapphires",
		description = "Display sapphire count and percentage in the overlay",
		section = gemTracking,
		position = 4
	)
	default boolean displaySapphires()
	{
		return true;
	}
	
	@ConfigItem(
		keyName = "displayEmeralds",
		name = "Display emeralds",
		description = "Display emerald count and percentage in the overlay",
		section = gemTracking,
		position = 5
	)
	default boolean displayEmeralds()
	{
		return true;
	}
	
	@ConfigItem(
		keyName = "displayRubies",
		name = "Display rubies",
		description = "Display ruby count and percentage in the overlay",
		section = gemTracking,
		position = 6
	)
	default boolean displayRubies()
	{
		return true;
	}
	
	@ConfigItem(
		keyName = "displayDiamonds",
		name = "Display diamonds",
		description = "Display diamond count and percentage in the overlay",
		section = gemTracking,
		position = 7
	)
	default boolean displayDiamonds()
	{
		return true;
	}
	
	@ConfigItem(
		keyName = "displayDragonstones",
		name = "Display dragonstones",
		description = "Display dragonstone count and percentage in the overlay",
		section = gemTracking,
		position = 8
	)
	default boolean displayDragonstones()
	{
		return true;
	}
}
