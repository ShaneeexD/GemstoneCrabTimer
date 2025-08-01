package com.gimserenity;

import java.awt.Color;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Notification;
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
		keyName = "hpThresholdNotification",
		name = "HP Threshold Notification",
		description = "Show a notification when the Gemstone Crab reaches the HP threshold",
		section = notificationList,
		position = 0
	)
	default Notification hpThresholdNotification()
	{
		return Notification.ON;
	}

	@Range(
		min = 1,
		max = 99
	)
	@ConfigItem(
		keyName = "hpThreshold",
		name = "HP Threshold %",
		description = "Send notification when Gemstone Crab HP reaches this percentage",
		section = notificationList,
		position = 1
	)
	default int hpThreshold()
	{
		return 2;
	}
	
	@ConfigItem(
		keyName = "notificationMessage",
		name = "Notification Message",
		description = "Message to show in the notification",
		section = notificationList,
		position = 2
	)
	default String notificationMessage()
	{
		return "Gemstone Crab HP threshold reached!";
	}

	@ConfigSection(
		name = "Pulse Screen",
		description = "Pulse Screen when boss is dead",
		position = 1
	)
	String pulseScreenList = "pulseScreenList";


	@ConfigItem(
		keyName = "pulseScreen",
		name = "Pulse Screen",
		description = "Pulse the screen when boss is dead",
		section = pulseScreenList,
		position = 0
	)
	default boolean pulseScreen()
	{
		return false;
	}

	@ConfigItem(
		keyName = "pulseColor",
		name = "Pulse Color",
		description = "Color of the screen pulse",
		section = pulseScreenList,
		position = 1
	)
	default Color pulseColor()
	{
		return new Color(255, 0, 0, 128); // Semi-transparent red
	}

		
	@ConfigSection(
		name = "Highlights",
		description = "Highlight settings",
		position = 2
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

	@ConfigItem(
		keyName = "tunnelTimeLeft",
		name = "Display Time Left",
		description = "Display time left until fight end over the tunnel",
		section = highlights,
		position = 3
	)
	default boolean tunnelTimeLeft()
	{
		return true;
	}

	@ConfigSection(
		name = "Current Fight Tracking",
		description = "Current Fight Information",
		position = 3
	)
	String currentFightTracking = "currentFightTracking";
	
	@ConfigItem(
		keyName = Constants.SHOW_MAIN_STATS,
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
		keyName = Constants.DISPLAY_TOTAL_DAMAGE,
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
		keyName = Constants.DISPLAY_DPS,
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
		keyName = Constants.DISPLAY_XP_GAINED,
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
		keyName = Constants.DISPLAY_DURATION,
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
		keyName = Constants.DISPLAY_TIME_LEFT,
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
		position = 4
	)
	String statTracking = "statTracking";

    @ConfigItem(
        keyName = Constants.SHOW_STAT_TRACKING,
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
        keyName = Constants.DISPLAY_KILL_COUNT,
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
        keyName = "killMessage",
        name = "Display kill message",
        description = "Enable to recieve a kill count message in chat when boss is killed",
		section = statTracking,
		position = 2
    )
    default boolean displayKillMessage()
    {
        return true;
    }

	@ConfigItem(
        keyName = Constants.DISPLAY_MINING_ATTEMPTS,
        name = "Display total mining attempts",
        description = "Display total Gemstone Crabs attempts at mining in overlay",
		section = statTracking,
		position = 3
    )
    default boolean displayMiningAttempts()
    {
        return false;
    }

	@ConfigItem(
        keyName = Constants.DISPLAY_MINED_COUNT,
        name = "Display total successful",
        description = "Display total successful mining attempts at Gemstone Crabs in overlay",
		section = statTracking,
		position = 4
    )
    default boolean displayMinedCount()
    {
        return false;
    }

	@ConfigItem(
        keyName = Constants.DISPLAY_FAILED_MINING_COUNT,
        name = "Display total failed",
        description = "Display total failed mining attempts at Gemstone Crabs in the overlay",
		section = statTracking,
		position = 5
    )
    default boolean displayFailedMiningCount()
    {
        return false;
    }

	@ConfigItem(
        keyName = Constants.DISPLAY_GEM_COUNT,
        name = "Display total gems mined",
        description = "Display total gems mined at Gemstone Crabs in the overlay",
		section = statTracking,
		position = 6
    )
    default boolean displayGemCount()
    {
        return false;
    }
	
	@ConfigSection(
		name = "Gem Tracking",
		description = "Gem Tracking",
		position = 5
	)
	String gemTracking = "gemTracking";

	@ConfigItem(
		keyName = Constants.SHOW_GEM_TRACKING,
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
		keyName = Constants.DISPLAY_OPALS,
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
		keyName = Constants.DISPLAY_JADES,
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
		keyName = Constants.DISPLAY_RED_TOPAZ,
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
		keyName = Constants.DISPLAY_SAPPHIRES,
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
		keyName = Constants.DISPLAY_EMERALDS,
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
		keyName = Constants.DISPLAY_RUBIES,
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
		keyName = Constants.DISPLAY_DIAMONDS,
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
		keyName = Constants.DISPLAY_DRAGONSTONES,
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
