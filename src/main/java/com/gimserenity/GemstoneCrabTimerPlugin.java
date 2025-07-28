package com.gimserenity;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@PluginDescriptor(
	name = "Gemstone Crab Timer",
	description = "Tracks Gemstone Crab boss HP and sends notifications at threshold for a better afk experience.",
	tags = {"boss", "hp", "notification", "gemstone", "crab"}
)
public class GemstoneCrabTimerPlugin extends Plugin
{
	// Gemstone Crab boss NPC ID
	private static final int GEMSTONE_CRAB_ID = 14779;

	// Tunnel game object ID
	private static final int TUNNEL_OBJECT_ID = 57631;

	// HP widget ID constants
	private static final int BOSS_HP_BAR_WIDGET_ID = 19857428;

	// Maximum distance to highlight tunnel (in tiles)
	private static final int MAX_TUNNEL_DISTANCE = 20;

	@Inject
	private Client client;

	@Inject
	private GemstoneCrabTimerConfig config;

	@Inject
	private Notifier notifier;

	@Inject
	private OverlayManager overlayManager;

	// Track if we've already sent a notification for this boss fight
	private boolean notificationSent = false;

	// Track if the boss is present
	private boolean bossPresent = false;

	// Track if we should highlight the tunnel
	private boolean shouldHighlightTunnel = false;

	// Track the nearest tunnel to highlight
	private GameObject nearestTunnel = null;

	// Track all tunnels in the scene
	private final Map<WorldPoint, GameObject> tunnels = new HashMap<>();

	// DPS tracking variables
	private int totalDamage = 0;
	private long fightStartTime = 0;
	private long fightDuration = 0;
	private double currentDps = 0;
	private boolean fightInProgress = false;

	// XP tracking for DPS calculation
	private int lastAttackXp = 0;
	private int lastStrengthXp = 0;
	private int lastDefenceXp = 0;
	private int lastRangedXp = 0;
	private int lastMagicXp = 0;
	private long lastXpGainTime = 0;
	private static final long XP_GAIN_TIMEOUT = 1000; // 1 second timeout for XP gains

	// Overlay for highlighting tunnels
	@Inject
	private GemstoneCrabTimerOverlay overlay;

	@Inject
	private GemstoneCrabTimerDpsOverlay dpsOverlay;

	private Instant flashStartTime = null;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Gemstone Crab Timer started!");
		notificationSent = false;
		shouldHighlightTunnel = false;
		nearestTunnel = null;
		tunnels.clear();
		resetDpsTracking();
		overlayManager.add(overlay);
		overlayManager.add(dpsOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Gemstone Crab Timer stopped!");
		notificationSent = false;
		bossPresent = false;
		shouldHighlightTunnel = false;
		nearestTunnel = null;
		tunnels.clear();
		resetDpsTracking();
		overlayManager.remove(overlay);
		overlayManager.remove(dpsOverlay);
	}

	// Getter methods for the overlay
	public GameObject getNearestTunnel()
	{
		return nearestTunnel;
	}

	public boolean shouldHighlightTunnel()
	{
		return shouldHighlightTunnel;
	}

	// DPS tracking getter methods
	public int getTotalDamage()
	{
		return totalDamage;
	}

	public double getCurrentDps()
	{
		return currentDps;
	}

	public long getFightDuration()
	{
		if (fightInProgress)
		{
			return (System.currentTimeMillis() - fightStartTime);
		}
		return fightDuration;
	}

	public long getEstimatedTimeRemainingMillis()
	{
		if (!bossPresent || !fightInProgress)
		{
			return 0;
		}

		Widget bossHpBar = client.getWidget(BOSS_HP_BAR_WIDGET_ID);
		if (bossHpBar == null || bossHpBar.isHidden() || bossHpBar.getText() == null)
		{
			return 0;
		}

		try
		{
			int hpPercent = Integer.parseInt(bossHpBar.getText().replace("%", "").trim());
			hpPercent = Math.max(1, Math.min(hpPercent, 100));
			double timeLeftSeconds = (hpPercent / 100.0) * 600;
			return (long) (timeLeftSeconds * 1000);
		}
		catch (NumberFormatException e)
		{
			log.debug("Failed to parse HP percentage for countdown");
			return 0;
		}
	}

	public boolean isFightInProgress()
	{
		return fightInProgress;
	}

	//Reset all DPS tracking variables
	private void resetDpsTracking()
	{
		totalDamage = 0;
		fightStartTime = 0;
		fightDuration = 0;
		currentDps = 0;
		fightInProgress = false;

		// Initialize XP tracking with current XP values
		if (client != null)
		{
			lastAttackXp = client.getSkillExperience(Skill.ATTACK);
			lastStrengthXp = client.getSkillExperience(Skill.STRENGTH);
			lastDefenceXp = client.getSkillExperience(Skill.DEFENCE);
			lastRangedXp = client.getSkillExperience(Skill.RANGED);
			lastMagicXp = client.getSkillExperience(Skill.MAGIC);
		}
		else
		{
			// Fallback if client is not available
			lastAttackXp = 0;
			lastStrengthXp = 0;
			lastDefenceXp = 0;
			lastRangedXp = 0;
			lastMagicXp = 0;
		}
		lastXpGainTime = 0;
	}



	@Subscribe
	public void onStatChanged(StatChanged statChanged)
	{
		// Only process if boss is present and we're near it
		if (!bossPresent)
		{
			return;
		}

		// Get the skill that changed
		Skill skill = statChanged.getSkill();
		int xp = statChanged.getXp();
		int estimatedDamage = 0;
		boolean isRelevantXp = false;

		// Check if it's a combat skill and calculate estimated damage
		switch (skill)
		{
			case ATTACK:
				if (xp > lastAttackXp)
				{
					estimatedDamage = estimateDamageFromXp(xp - lastAttackXp);
					lastAttackXp = xp;
					isRelevantXp = true;
				}
				break;
			case STRENGTH:
				if (xp > lastStrengthXp)
				{
					estimatedDamage = estimateDamageFromXp(xp - lastStrengthXp);
					lastStrengthXp = xp;
					isRelevantXp = true;
				}
				break;
			case DEFENCE:
				if (xp > lastDefenceXp)
				{
					estimatedDamage = estimateDamageFromXp(xp - lastDefenceXp);
					lastDefenceXp = xp;
					isRelevantXp = true;
				}
				break;
			case RANGED:
				if (xp > lastRangedXp)
				{
					estimatedDamage = estimateDamageFromXp(xp - lastRangedXp);
					lastRangedXp = xp;
					isRelevantXp = true;
				}
				break;
			case MAGIC:
				if (xp > lastMagicXp)
				{
					estimatedDamage = estimateDamageFromXp(xp - lastMagicXp);
					lastMagicXp = xp;
					isRelevantXp = true;
				}
				break;
			default:
				break;
		}

		// If we got relevant XP and it's been more than the timeout since last XP gain
		if (isRelevantXp && estimatedDamage > 0)
		{
			long currentTime = System.currentTimeMillis();

			// Only process if it's been more than the timeout since last XP gain
			// This helps prevent double-counting damage that was already tracked via hitsplats
			if (currentTime - lastXpGainTime > XP_GAIN_TIMEOUT)
			{
				// If we're not tracking a fight yet, start now
				if (!fightInProgress)
				{
					fightInProgress = true;
					fightStartTime = currentTime;
				}

				// Add the estimated damage to our total
				totalDamage += estimatedDamage;

				// Update current DPS
				long currentDuration = currentTime - fightStartTime;
				if (currentDuration > 0)
				{
					currentDps = (double) totalDamage / (currentDuration / 1000.0);
				}

				log.debug("XP-based damage on Gemstone Crab: {}. Total damage: {}, DPS: {}",
					estimatedDamage, totalDamage, currentDps);
			}

			// Update last XP gain time
			lastXpGainTime = currentTime;
		}
	}

	/**
	 * Estimate damage based on XP gained
	 * For Gemstone Crabs, XP is about 3.5x the damage dealt (87.5% of normal)
	 * according to the OSRS Wiki
	 */
	private int estimateDamageFromXp(int xpGained)
	{
		// XP is roughly 3.5x the damage dealt for Gemstone Crabs
		return Math.max(1, Math.round(xpGained / 3.5f));
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned)
	{
		NPC npc = npcSpawned.getNpc();

		if (npc.getId() == GEMSTONE_CRAB_ID)
		{
			log.debug("Gemstone Crab boss spawned");
			bossPresent = true;
			notificationSent = false;

			// Start a new DPS tracking session
			resetDpsTracking();
			fightInProgress = true;
			fightStartTime = System.currentTimeMillis();
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		NPC npc = npcDespawned.getNpc();

		if (npc.getId() == GEMSTONE_CRAB_ID)
		{
			log.debug("Gemstone Crab boss despawned");
			bossPresent = false;
			notificationSent = false;

			// Finalize DPS tracking
			if (fightInProgress)
			{
				fightDuration = System.currentTimeMillis() - fightStartTime;
				fightInProgress = false;

				// Calculate final DPS
				if (fightDuration > 0)
				{
					currentDps = (double) totalDamage / (fightDuration / 1000.0);
				}
				log.debug("Fight ended. Total damage: {}, Duration: {}s, DPS: {}",
					totalDamage, fightDuration / 1000.0, currentDps);
			}

			// When the boss dies, find and highlight the nearest tunnel
			if (config.highlightTunnel())
			{
				findNearestTunnel();
				shouldHighlightTunnel = true;
				flashStartTime = Instant.now();
				log.debug("Boss died, highlighting nearest tunnel");
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		// Check boss HP for notification
		if (bossPresent && config.enableNotifications() && !notificationSent)
		{
			checkBossHpAndNotify();
		}

		// If boss respawns, reset tunnel highlighting
		if (bossPresent && shouldHighlightTunnel)
		{
			shouldHighlightTunnel = false;
			nearestTunnel = null;
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			// Reset notification state when logging in
			notificationSent = false;
		}
		else if (gameStateChanged.getGameState() == GameState.LOADING)
		{
			// Reset tunnel highlighting when changing areas
			shouldHighlightTunnel = false;
			nearestTunnel = null;
			tunnels.clear();

			// Reset DPS tracking when changing areas
			if (fightInProgress)
			{
				log.debug("Area change detected, resetting DPS tracking");
				resetDpsTracking();
			}
		}
	}

	private void checkBossHpAndNotify()
	{
		// Get the boss HP widget
		Widget bossHpBar = client.getWidget(BOSS_HP_BAR_WIDGET_ID);

		if (bossHpBar == null || bossHpBar.isHidden())
		{
			return;
		}

		// Get HP percentage directly from the widget's text value
		String text = bossHpBar.getText();
		if (text == null || text.isEmpty())
		{
			return;
		}

		// Parse the percentage value (e.g., "50%" -> 50)
		int hpPercent;
		try
		{
			// Remove the % sign and parse the number
			hpPercent = Integer.parseInt(text.replace("%", "").trim());
		}
		catch (NumberFormatException e)
		{
			log.debug("Failed to parse HP percentage from text: {}", text);
			return;
		}

		// Check if HP is at or below the threshold
		if (hpPercent <= config.hpThreshold() && !notificationSent)
		{
			notifier.notify(config.notificationMessage() + " (" + hpPercent + "% HP)");
			notificationSent = true;
			log.debug("Sent notification for Gemstone Crab at {}% HP", hpPercent);
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		final GameObject gameObject = event.getGameObject();

		// Track tunnels in the scene
		if (gameObject.getId() == TUNNEL_OBJECT_ID)
		{
			WorldPoint location = gameObject.getWorldLocation();
			tunnels.put(location, gameObject);
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		final GameObject gameObject = event.getGameObject();

		// Remove tunnels from tracking when they despawn
		if (gameObject.getId() == TUNNEL_OBJECT_ID)
		{
			WorldPoint location = gameObject.getWorldLocation();
			tunnels.remove(location);

			// If this was our highlighted tunnel, clear it
			if (nearestTunnel != null && nearestTunnel.equals(gameObject))
			{
				nearestTunnel = null;
				shouldHighlightTunnel = false;
			}
		}
	}


	// Find the nearest tunnel to the player
	private void findNearestTunnel()
	{
		if (client.getLocalPlayer() == null || tunnels.isEmpty())
		{
			return;
		}

		WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
		GameObject closest = null;
		int closestDistance = Integer.MAX_VALUE;

		for (GameObject tunnel : tunnels.values())
		{
			int distance = tunnel.getWorldLocation().distanceTo(playerLocation);

			// Only consider tunnels within the maximum distance
			if (distance <= MAX_TUNNEL_DISTANCE && distance < closestDistance)
			{
				closest = tunnel;
				closestDistance = distance;
			}
		}

		nearestTunnel = closest;
		log.debug("Found nearest tunnel at distance: {}", closestDistance);
	}

	@Provides
	GemstoneCrabTimerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(GemstoneCrabTimerConfig.class);
	}

	public Instant getFlashStartTime()
	{
	    return flashStartTime;
	}

}
