package com.gimserenity;

import java.awt.Color;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Notification;
import net.runelite.client.config.Range;
import net.runelite.client.config.Alpha;

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
		keyName = "nextSpawnNotification",
		name = "Next Spawn Notification",
		description = "Show a notification when the next Gemstone Crab spawns",
		section = notificationList,
		position = 0
	)
	default Notification nextSpawnNotification()
	{
		return Notification.OFF;
	}

	@ConfigItem(
		keyName = "hpThresholdNotification",
		name = "HP Threshold Notification",
		description = "Show a notification when the Gemstone Crab reaches the HP threshold",
		section = notificationList,
		position = 1
	)
	default Notification hpThresholdNotification()
	{
		return Notification.ON;
	}

	@Range(
		min = 0,
		max = 100
	)
	@ConfigItem(
		keyName = "hpThreshold",
		name = "HP Threshold %",
		description = "Send notification when Gemstone Crab HP reaches this percentage",
		section = notificationList,
		position = 2
	)
	default int hpThreshold()
	{
		return 2;
	}
	
	@ConfigItem(
		keyName = "notificationMessage",
		name = "HP Threshold Notification Message",
		description = "Message to show in the notification",
		section = notificationList,
		position = 3
	)
	default String notificationMessage()
	{
		return "Gemstone Crab HP threshold reached!";
	}
		
	@ConfigSection(
		name = "Overlay Appearance",
		description = "Overlay appearance settings",
		position = 1
	)
	String overlayAppearance = "overlayAppearance";

	@Alpha
	@ConfigItem(
		keyName = "overlayBackgroundColor",
		name = "Overlay background color",
		description = "Background color for the Gemstone Crab overlay panel",
		section = overlayAppearance,
		position = 0
	)
	default Color overlayBackgroundColor()
	{
		return new Color(18, 18, 18, 180);
	}

	@Alpha
	@ConfigItem(
		keyName = "overlayHeaderTextColor",
		name = "Header text color",
		description = "Text color used for section headers",
		section = overlayAppearance,
		position = 1
	)
	default Color overlayHeaderTextColor()
	{
		return Color.GREEN;
	}

	@Alpha
	@ConfigItem(
		keyName = "overlayItemTextColor",
		name = "Item text color",
		description = "Text color used for item rows",
		section = overlayAppearance,
		position = 2
	)
	default Color overlayItemTextColor()
	{
		return Color.WHITE;
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
		keyName = "highlightShell",
		name = "Highlight Shell",
		description = "Highlight the shell after boss death",
		section = highlights
	)
	default boolean highlightShell()
	{
		return true;
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
		keyName = Constants.DISPLAY_PLAYER_COUNT,
		name = "Display player count",
		description = "Display player count in the current fight",
		section = currentFightTracking,
		position = 1
	)
	default boolean displayPlayerCount()
	{
		return false;
	}
	
	@ConfigItem(
		keyName = Constants.DISPLAY_TOTAL_DAMAGE,
		name = "Display total damage",
		description = "Display total damage dealt in the current fight",
		section = currentFightTracking,
		position = 2
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
		position = 3
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
		position = 4
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
		position = 5
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
		position = 6
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
	
	@ConfigItem(
        keyName = Constants.DISPLAY_TOP3_COUNT,
        name = "Display top 3 count",
        description = "Display number of times you've been in the top 3 crab crushers",
		section = statTracking,
		position = 7
    )
    default boolean displayTop3Count()
    {
        return false;
    }

	@ConfigItem(
        keyName = Constants.DISPLAY_CUMULATIVE_XP,
        name = "Display cumulative XP",
        description = "Display total XP gained across all crab kills",
		section = statTracking,
		position = 8
    )
    default boolean displayCumulativeXp()
    {
        return true;
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
