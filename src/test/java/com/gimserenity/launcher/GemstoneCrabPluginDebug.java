package com.gimserenity.launcher;

import com.gimserenity.GemstoneCrabTimerPlugin;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class GemstoneCrabPluginDebug
{

	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(GemstoneCrabTimerPlugin.class);
		RuneLite.main(args);
	}
}
