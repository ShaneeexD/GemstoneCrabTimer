package com.gimserenity;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

import java.awt.*;

@ConfigGroup("gemstonecrab")
public interface GemstoneCrabTimerConfig extends Config
{
	@ConfigItem(
		keyName = "enableNotifications",
		name = "Enable Notifications",
		description = "Enable desktop notifications when Gemstone Crab reaches HP threshold"
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
		description = "Send notification when Gemstone Crab HP reaches this percentage"
	)
	default int hpThreshold()
	{
		return 2;
	}
	
	@ConfigItem(
		keyName = "notificationMessage",
		name = "Notification Message",
		description = "Message to show in the notification"
	)
	default String notificationMessage()
	{
		return "Gemstone Crab HP threshold reached!";
	}
	
	@ConfigItem(
		keyName = "highlightTunnel",
		name = "Highlight Tunnel",
		description = "Highlight the nearest tunnel after boss death"
	)
	default boolean highlightTunnel()
	{
		return true;
	}
	
	@ConfigItem(
		keyName = "tunnelHighlightColor",
		name = "Tunnel Highlight Color",
		description = "Color to highlight the tunnel with"
	)
	default Color tunnelHighlightColor()
	{
		return Color.GREEN;
	}
	
	@ConfigItem(
		keyName = "showDpsTracker",
		name = "Show DPS Tracker",
		description = "Display damage and DPS information during boss fights"
	)
	default boolean showDpsTracker()
	{
		return true;
	}

	@ConfigItem(
		keyName = "pulseScreen",
		name = "Pulse Screen",
		description = "Pulse the screen overlay when the tunnel is highlighted"
	)
	default boolean pulseScreen()
	{
		return true;
	}

	@ConfigItem(
		keyName = "pulseColor",
		name = "Pulse Color",
		description = "Color of the screen pulse"
	)
	default Color pulseColor()
	{
		return new Color(255, 0, 0, 128); // Semi-transparent red
	}
}
