package com.gimserenity;

import java.awt.Color;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.Skill;
import net.runelite.api.WorldView;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.StatChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@PluginDescriptor(
    name = "Gemstone Crab",
    description = "All-in-one Gemstone Crab plugin for a better afk and informational experience.",
    tags = {"boss", "hp", "notification", "gemstone", "crab", "afk", "info", "tracker", "dps"}
)
public class GemstoneCrabTimerPlugin extends Plugin
{
	// Gemstone Crab boss NPC ID
	private static final int GEMSTONE_CRAB_ID = 14779;

	// Gemstone Crab shell NPC ID
	private static final int GEMSTONE_CRAB_SHELL_ID = 14780;
	
	// Tunnel game object ID
	private static final int TUNNEL_OBJECT_ID = 57631;
	
	// HP widget ID constants
	private static final int BOSS_HP_BAR_WIDGET_ID = 19857428;
	
	// Maximum distance to highlight tunnel (in tiles)
	private static final int MAX_TUNNEL_DISTANCE = 20;

	// Distance from the crab to considered in the area
	private static final int DISTANCE_THRESHOLD = 13; 

	// Minutes at the boss required to count as a kill
	// Also, used as a cooldown for mining so its not counted multiple times
	private static final long KILL_THRESHOLD_MILLISECONDS = 5*60*1000; // 5 minutes

	// Location of each crab from its center
	private static final WorldPoint EAST_CRAB = new WorldPoint(1353, 3112, 0);
	private static final WorldPoint SOUTH_CRAB = new WorldPoint(1239,3043, 0);
	private static final WorldPoint NORTH_CRAB = new WorldPoint(1273,3173, 0);

	// Crab Chat messages
	private static final String GEMSTONE_CRAB_DEATH_MESSAGE = "The gemstone crab burrows away, leaving a piece of its shell behind.";
	private static final String GEMSTONE_CRAB_MINE_SUCCESS_MESSAGE = "You swing your pick at the crab shell.";
	private static final String GEMSTONE_CRAB_MINE_FAIL_MESSAGE = "Your understanding of the gemstone crab is not great enough to mine its shell.";
	private static final String GEMSTONE_CRAB_GEM_MINE_MESSAGE = "You mine an uncut ";
	private static final String GEMSTONE_CRAB_TOP16_MESSAGE = "You gained enough understanding of the crab to mine from its remains.";
	private static final String GEMSTONE_CRAB_TOP3_MESSAGE = "The top three crab crushers were ";


	// Configuration keys
	private static final String CONFIG_GROUP = "gemstonecrab";
    private static final String CONFIG_KEY_COUNT = "crabCount";
	private static final String CONFIG_KEY_MINING_ATTEMPTS = "miningAttemptsCount";
	private static final String CONFIG_KEY_MINED = "minedCount";
	private static final String CONFIG_KEY_FAILED = "failedMiningCount";
	private static final String CONFIG_KEY_GEMS_MINED = "gemsMined";
	private static final String CONFIG_KEY_CUMULATIVE_XP = "cumulativeXp";
	
	// Gem tracking keys
	private static final String CONFIG_KEY_OPALS = "opals";
	private static final String CONFIG_KEY_JADES = "jades";
	private static final String CONFIG_KEY_RED_TOPAZ = "redTopaz";
	private static final String CONFIG_KEY_SAPPHIRES = "sapphires";
	private static final String CONFIG_KEY_EMERALDS = "emeralds";
	private static final String CONFIG_KEY_RUBIES = "rubies";
	private static final String CONFIG_KEY_DIAMONDS = "diamonds";
	private static final String CONFIG_KEY_DRAGONSTONES = "dragonstones";
	
	@Inject
	private Client client;

	@Inject
	private GemstoneCrabTimerConfig config;

	@Inject
	private GemstoneCrabConfigStore configStore;
	
	@Inject
	private Notifier notifier;
	
	@Inject
    private ConfigManager configManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
    private GemstoneCrabUtil util;
	
	// Track if we've already sent a notification for this boss fight
	private boolean notificationSent = false;
	
	// Track if the boss is present
	private boolean bossPresent = false;

	private double hpPercent;

	// Track if we should highlight the tunnel
	private boolean shouldHighlightTunnel = false;
	
	// Track the nearest tunnel to highlight
	private GameObject nearestTunnel = null;
	
	// Track all tunnels in the scene
	private final Map<WorldPoint, GameObject> tunnels = new HashMap<>();
	
	// Track the crab shell NPC to highlight
	private NPC crabShell = null;
	
	// Track if we should highlight the crab shell
	private boolean shouldHighlightShell = false;
	
	// Track if player is a top 16 damager (for shell color)
	private boolean isTop16Damager = false;
	
	// Track all crab shells in the scene
	private final Map<WorldPoint, NPC> shells = new HashMap<>();
		
	// Smooth time left tracking
	private int lastHpPercent = 100;
	private long lastHpUpdateTime = 0;
	private static final long HP_UPDATE_INTERVAL = 6000; // HP bar updates every 6 seconds
	
	// DPS tracking variables
	private int totalDamage = 0;
	private long fightStartTime = 0;
	private long fightDuration = 0;
	private double currentDps = 0;
	private boolean fightInProgress = false;
	
	// XP tracking for DPS calculation
	private int totalXpGained = 0; // Total XP gained during the fight
	private int pendingCombatXp = 0;
	// Tracks last XP value per skill
	private final Map<Skill, Integer> lastXp = new EnumMap<>(Skill.class);

	// Kill tracking variables
	private int crabCount;
	private long lastMiningAttempt;
	private long lastKillTime;

	// Mining stats tracking variables
	private int minedCount;
	private int miningAttempts;
	private int miningFailedCount;
	private int gemsMined;
	
	// Gem tracking
	private int opals = 0;
	private int jades = 0;
	private int redTopaz = 0;
	private int sapphires = 0;
	private int emeralds = 0;
	private int rubies = 0;
	private int diamonds = 0;
	private int dragonstones = 0;
	private int mineCountShell = 0;
	
	// Track players interacting with the crab
	private int playersInteractingWithCrab = 0;
	
	// Track times player has been in top 3
	private int top3Count = 0;
	
	// Cumulative XP tracking across all kills
	private int cumulativeXp = 0;

	// Overlay for highlighting tunnels
	@Inject
	private GemstoneCrabTimerOverlay overlay;
	
	@Inject
	private GemstoneCrabTimerDpsOverlay dpsOverlay;

	@Inject
    private ClientThread clientThread;
	
	@Override
	protected void startUp() throws Exception
	{
		log.info("Gemstone Crab Plugin started!");
		notificationSent = false;
		shouldHighlightTunnel = false;
		shouldHighlightShell = false;
		nearestTunnel = null;
		crabShell = null;
		tunnels.clear();
		shells.clear();
		resetDpsTracking();
		configStore.load(config);
		overlayManager.add(overlay);
		overlayManager.add(dpsOverlay);
	}

	public GemstoneCrabConfigStore getConfigStore() {
		return configStore;
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Gemstone Crab Timer stopped!");
		notificationSent = false;
		bossPresent = false;
		shouldHighlightTunnel = false;
		shouldHighlightShell = false;
		nearestTunnel = null;
		crabShell = null;
		tunnels.clear();
		shells.clear();
		resetDpsTracking();
		overlayManager.remove(overlay);
		overlayManager.remove(dpsOverlay);
	}
	
	// Getter methods for the overlay
	public GameObject getNearestTunnel()
	{
		return nearestTunnel;
	}
	
	public NPC getCrabShell()
	{
		return crabShell;
	}
	
	public boolean shouldHighlightShell()
	{
		return shouldHighlightShell;
	}
	
	public boolean isTop16Damager()
	{
		return isTop16Damager;
	}
	
	public int getPlayersInteractingWithCrab() {
		return playersInteractingWithCrab;
	}
	
	/*
	 * Get the number of times the player has been in the top 3 crab crushers
	 */
	public int getTop3Count() {
		return top3Count;
	}
	
	public int getCumulativeXp() {
		return cumulativeXp;
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
	
	/**
	 * Get the total XP gained during the current fight
	 * @return Total XP gained
	 */
	public int getTotalXpGained()
	{
		return totalXpGained;
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
		
		// Get the boss NPC
		NPC boss = null;
		WorldView view = client.getWorldView(-1);
		for (NPC npc : view.npcs())
		{
			if (npc != null && npc.getId() == GEMSTONE_CRAB_ID)
			{
				boss = npc;
				break;
			}
		}
		
		if (boss == null)
		{
			return 0;
		}
		
		try
		{
			// Get health ratio and scale from the NPC
			int healthRatio = boss.getHealthRatio();
			int healthScale = boss.getHealthScale();
			
			// If either value is -1, the health info is not available
			if (healthRatio == -1 || healthScale == -1)
			{
				log.debug("Health ratio or scale not available: {} / {}", healthRatio, healthScale);
				return 0;
			}
			
			// Calculate current HP percentage
			int currentHpPercent = (int) (((double) healthRatio / (double) healthScale) * 100);
			currentHpPercent = Math.max(0, Math.min(currentHpPercent, 100));
			
			// Update our tracking variables when HP changes
			if (currentHpPercent != lastHpPercent)
			{
				lastHpPercent = currentHpPercent;
				lastHpUpdateTime = System.currentTimeMillis();
			}
			
			// Calculate interpolated HP percentage based on time since last update
			long timeSinceUpdate = System.currentTimeMillis() - lastHpUpdateTime;
			// HP decreases at a rate of 1% every 6 seconds
			double interpolatedHpPercent = Math.max(0, lastHpPercent - (timeSinceUpdate / (double) HP_UPDATE_INTERVAL));
			
			// Calculate time left based on interpolated HP
			double timeLeftSeconds = (interpolatedHpPercent / 100.0) * 600;
			return (long) (timeLeftSeconds * 1000);
		}
		catch (Exception e)
		{
			log.debug("Failed to calculate time remaining: {}", e.getMessage());
			return 0;
		}
	}
	
	public boolean isFightInProgress() {
		return fightInProgress;
	}

	public int getCrabCount() {
        return crabCount;
    }

	public int getMinedCount() {
        return minedCount;
    }

	public int getMiningAttemptsCount() {
        return miningAttempts;
    }

	public int getMiningFailedCount() {
        return miningFailedCount;
    }

	public int getGemsCount() {
        return gemsMined;
    }
	
	// Gem tracking getter methods
	public int getOpalsCount()
	{
		return opals;
	}
	
	public int getJadesCount()
	{
		return jades;
	}
	
	public int getRedTopazCount()
	{
		return redTopaz;
	}
	
	public int getSapphiresCount()
	{
		return sapphires;
	}
	
	public int getEmeraldsCount()
	{
		return emeralds;
	}
	
	public int getRubiesCount()
	{
		return rubies;
	}
	
	public int getDiamondsCount()
	{
		return diamonds;
	}
	
	public int getDragonstonesCount()
	{
		return dragonstones;
	}
	
	//Reset all DPS tracking variables
	private void resetDpsTracking()
	{
		totalDamage = 0;
		fightStartTime = 0;
		fightDuration = 0; // Reset fight duration
		currentDps = 0;
		fightInProgress = false;
		totalXpGained = 0; // Reset total XP gained
		pendingCombatXp = 0;
		lastXp.clear();
		
		log.debug("DPS tracking reset, fight duration: {}", fightDuration);
	}

	private void resetTunnel()
	{
		if(!tunnels.isEmpty())
		{
			shouldHighlightTunnel = false;
		}
	}
	
	/*
	 * Load any saved configuration values
	 */
	private void loadSavedConfiguration(){
        crabCount = util.loadConfigValue(CONFIG_GROUP, CONFIG_KEY_COUNT);
		miningAttempts = util.loadConfigValue(CONFIG_GROUP, CONFIG_KEY_MINING_ATTEMPTS);
		minedCount = util.loadConfigValue(CONFIG_GROUP, CONFIG_KEY_MINED);
		miningFailedCount = util.loadConfigValue(CONFIG_GROUP, CONFIG_KEY_FAILED);
		gemsMined = util.loadConfigValue(CONFIG_GROUP, CONFIG_KEY_GEMS_MINED);
		
		// Load individual gem counts
		opals = util.loadConfigValue(CONFIG_GROUP, CONFIG_KEY_OPALS);
		jades = util.loadConfigValue(CONFIG_GROUP, CONFIG_KEY_JADES);
		redTopaz = util.loadConfigValue(CONFIG_GROUP, CONFIG_KEY_RED_TOPAZ);
		sapphires = util.loadConfigValue(CONFIG_GROUP, CONFIG_KEY_SAPPHIRES);
		emeralds = util.loadConfigValue(CONFIG_GROUP, CONFIG_KEY_EMERALDS);
		rubies = util.loadConfigValue(CONFIG_GROUP, CONFIG_KEY_RUBIES);
		diamonds = util.loadConfigValue(CONFIG_GROUP, CONFIG_KEY_DIAMONDS);
		dragonstones = util.loadConfigValue(CONFIG_GROUP, CONFIG_KEY_DRAGONSTONES);
		
		// Load top 3 count
		top3Count = util.loadConfigValue(CONFIG_GROUP, Constants.CONFIG_KEY_TOP3_COUNT);
		
		// Load cumulative XP
		cumulativeXp = util.loadConfigValue(CONFIG_GROUP, CONFIG_KEY_CUMULATIVE_XP);
	}

	
	@Subscribe
	public void onStatChanged(StatChanged statChanged)
	{
		if (!bossPresent)
		{
			return;
		}

		Skill skill = statChanged.getSkill();
		int newXp = statChanged.getXp();

		// Get the previous XP value (default to current XP if missing)
		int previousXp = lastXp.getOrDefault(skill, newXp);
		int delta = newXp - previousXp;

		// Update the stored XP value
		lastXp.put(skill, newXp);

		// Ignore non-combat skills entirely
		switch (skill)
		{
			case ATTACK:
			case STRENGTH:
			case DEFENCE:
			case RANGED:
			case MAGIC:
				if (delta > 0)
				{
					pendingCombatXp += delta;
				}
				break;

			case HITPOINTS:
				log.debug("Hitpoints XP gained: {}", delta);
				break;

			default:
				break;
		}
	}
	
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

				
			resetTunnel();
			// Start a new DPS tracking session
			// This is where we reset stats - when a new boss spawns
			resetDpsTracking();
			isTop16Damager = false;
			fightInProgress = true;
			fightStartTime = System.currentTimeMillis();
			setLastMiningAttempt();
			log.debug("New boss spawned, resetting DPS stats");
		}
		// Track crab shells in the scene
		else if (npc.getId() == GEMSTONE_CRAB_SHELL_ID)
		{
			WorldPoint location = npc.getWorldLocation();
			shells.put(location, npc);
			log.debug("Crab shell NPC spawned at {}", location);

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
			
			// When the boss dies, highlight the nearest tunnel
			if (config.highlightTunnel())
			{
				findNearestTunnel();
				shouldHighlightTunnel = true;
				log.debug("Boss died, highlighting nearest tunnel");
			}
			
			// Also find and highlight the shell in red by default
			updateCrabShell();
			// Set to true but not top 16 yet (will be red)
			shouldHighlightShell = true;
			
			log.debug("Boss died, highlighting shell in red");
			
		}
		else if (npc.getId() == GEMSTONE_CRAB_SHELL_ID)
		{
			// If the despawned NPC is our tracked shell, clear it
			if (crabShell != null && npc.equals(crabShell))
			{
				crabShell = null;
			}
			// Also remove from our shell map
			shells.remove(npc.getWorldLocation());
			mineCountShell = 0;
		}
	}
	
	@Subscribe
	public void onGameTick(GameTick event)
	{
		// Check if player is in any of the Gemstone Crab areas
		boolean playerInArea = isPlayerInGemstoneArea();
		
		// Always find the nearest tunnel when in the area, even during the fight
		if (playerInArea) {
			findNearestTunnel();
			// Update the crab shell reference
			updateCrabShell();
		}

		if(!playerInArea)
		{
			shouldHighlightShell = false;
		}
		else
		{
			shouldHighlightShell = true;
		}
		
		// If player left the area, reset tracking
		if (!playerInArea && (bossPresent || fightInProgress))
		{
			// Player left the area
			bossPresent = false;
			
			// Reset tunnel highlighting
			shouldHighlightTunnel = false;
			nearestTunnel = null;
			
			// If we were tracking a fight, stop tracking
			if (fightInProgress)
			{
				fightInProgress = false;
				resetDpsTracking();
			}
			
			log.debug("Player left Gemstone Crab area, resetting tracking");
			return;
		}
		
		// If we mined 3 gems, reset top 16 to stop shell highlight
		if (mineCountShell >= 3)
		{	
			isTop16Damager = false;
		}

		// Count players interacting with the crab
		if (playerInArea && bossPresent) {
			countPlayersInteractingWithCrab();
		}
		
		// Check for boss HP bar to detect boss presence when re-entering the area
		if (playerInArea && !bossPresent)
		{
			Widget bossHpBar = client.getWidget(BOSS_HP_BAR_WIDGET_ID);
			if (bossHpBar != null && !bossHpBar.isHidden())
			{
				// Boss is present but we weren't tracking it (player just entered area)
				bossPresent = true;
				
				// Only track if there's an actual NPC (not just HP bar)
				log.debug("Re-entered area with boss HP bar visible");
			}
		}

		// Check boss HP for notification
		if (bossPresent && config.hpThresholdNotification().isEnabled() && !notificationSent)
		{
			checkBossHpAndNotify();
		}

		// --- XP-based damage processing ---
        if (bossPresent && pendingCombatXp > 0)
		{
			// Convert combat XP (summed across skills in onStatChanged) to damage
            int estimatedDamage = estimateDamageFromXp(pendingCombatXp);
            totalXpGained += pendingCombatXp;
            // Also accumulate into lifetime tracker and persist
            cumulativeXp += pendingCombatXp;
            saveCrabCounts();
            pendingCombatXp = 0;

			long currentTime = System.currentTimeMillis();
			if (!fightInProgress)
			{
				fightInProgress = true;
				fightStartTime = currentTime;
			}

			totalDamage += estimatedDamage;

			long duration = currentTime - fightStartTime;
			if (duration > 0)
			{
				currentDps = totalDamage / (duration / 1000.0);
				// Update fight duration for valid kill tracking
				fightDuration = duration;
			}

            log.debug("XP-based damage this tick: {} (total damage: {}, DPS: {}, cumulativeXp: {})",
                estimatedDamage, totalDamage, currentDps, cumulativeXp);
		}
	}



	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			// Reset notification state when logging in
			notificationSent = false;
            clientThread.invoke(this::loadSavedConfiguration);
		}
		else if (gameStateChanged.getGameState() == GameState.LOADING)
		{
			// Reset tunnel highlighting when changing areas
			shouldHighlightTunnel = false;
			nearestTunnel = null;
			tunnels.clear();
			shells.clear();
			crabShell = null;
			mineCountShell = 0;
			// Reset DPS tracking when changing areas
			if (fightInProgress)
			{
				log.debug("Area change detected, resetting DPS tracking");
				resetDpsTracking();
			}
		}
	}
	


	/*
	 * Handles Gemstone Crab Mining Events
	 * Mining Attempt, Success, and Failure
	 */
    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        // Check for all relevant message types, including colored messages
        if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE || 
			chatMessage.getType() == ChatMessageType.SPAM) {  	
			// Log all message types for debugging
			log.debug("Chat message received: type={}, message={}", chatMessage.getType(), chatMessage.getMessage());
			String message = chatMessage.getMessage();
			
			if (message.equalsIgnoreCase(GEMSTONE_CRAB_MINE_SUCCESS_MESSAGE)) {
				if (!isMiningBeforeCooldown()) {
					log.debug("Gemstone Crab successfully mined!");
					miningAttempts++;
					minedCount++;
					setLastMiningAttempt();
				}
			} else if (message.equalsIgnoreCase(GEMSTONE_CRAB_MINE_FAIL_MESSAGE)) {
				if (!isMiningBeforeCooldown()) {
					log.debug("Failed to mine Gemstone Crab!");
					miningAttempts++;
					miningFailedCount++;
					setLastMiningAttempt();
				}
			} else if (message.equalsIgnoreCase(GEMSTONE_CRAB_DEATH_MESSAGE)) {
				if (!isKillBeforeCooldown()) {
					log.debug("Gemstone Crab death detected, updating kill stats");
					updateKillStats();
					setLastKillTime();
				} else {
					log.debug("Gemstone Crab death detected, but within cooldown period - ignoring");
				}	
			} else if (message.contains(GEMSTONE_CRAB_TOP16_MESSAGE)) {
				log.debug("You are a top 16 damager on the Gemstone Crab!");
				shouldHighlightShell = true;
				isTop16Damager = true;
				updateCrabShell();
				log.debug("Shell highlighting enabled (green) - isTop16Damager: {}, shell: {}", isTop16Damager, crabShell != null ? crabShell.getId() : "null");
			} else if (message.startsWith(GEMSTONE_CRAB_TOP3_MESSAGE)) {				
				if (isTop3Player(message)) {
					top3Count++;
					saveCrabCounts();
				}
			}
			else if (message.contains(GEMSTONE_CRAB_GEM_MINE_MESSAGE)) {
				log.debug("Gem mined");
				gemsMined++;
				mineCountShell++;
				// Track specific gem types
				if (message.contains("opal")) {
					opals++;
					log.debug("Opal mined");
				} else if (message.contains("jade")) {
					jades++;
					log.debug("Jade mined");
				} else if (message.contains("red topaz")) {
					redTopaz++;
					log.debug("Red topaz mined");
				} else if (message.contains("sapphire")) {
					sapphires++;
					log.debug("Sapphire mined");
				} else if (message.contains("emerald")) {
					emeralds++;
					log.debug("Emerald mined");
				} else if (message.contains("ruby")) {
					rubies++;
					log.debug("Ruby mined");
				} else if (message.contains("diamond")) {
					diamonds++;
					log.debug("Diamond mined");
				} else if (message.contains("dragonstone")) {
					dragonstones++;
					log.debug("Dragonstone mined");
				}
			}
			saveCrabCounts();
        }
		// Handle chat commands
		else if (chatMessage.getType() == ChatMessageType.PUBLICCHAT || 
			chatMessage.getType() == ChatMessageType.PRIVATECHAT || 
			chatMessage.getType() == ChatMessageType.FRIENDSCHAT || 
			chatMessage.getType() == ChatMessageType.CLAN_CHAT || 
			chatMessage.getType() == ChatMessageType.CLAN_GUEST_CHAT ||
			chatMessage.getType() == ChatMessageType.CLAN_GIM_CHAT) {
			
			String message = chatMessage.getMessage();
			
			// Check for the reset stats command
			if (message.equalsIgnoreCase("!Resetgemcrab")) {
				if( Text.sanitize( chatMessage.getName() ).equalsIgnoreCase( Text.sanitize( client.getLocalPlayer().getName() ) ) ){
					log.debug("Reset stats command received");
					resetStats();
				}
			}
		}
	}

	
	/**
	 * Check if the player is within any of the three Gemstone Crab areas
	 * @return true if player is in any of the three areas
	 */
	public boolean isPlayerInGemstoneArea()
	{
		if (client == null || client.getLocalPlayer() == null)
		{
			return false;
		}
		
		// Get player's world location
		WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
		if (playerLocation == null)
		{
			return false;
		}
		
		// Checks if player is close enough to any of the crabs
		return playerLocation.distanceTo2D(EAST_CRAB) <= DISTANCE_THRESHOLD 
			|| playerLocation.distanceTo2D(SOUTH_CRAB) <= DISTANCE_THRESHOLD 
			|| playerLocation.distanceTo2D(NORTH_CRAB) <= DISTANCE_THRESHOLD;
	}
	
	private void checkBossHpAndNotify()
	{
		// Get the boss NPC
		NPC boss = null;
		WorldView view = client.getWorldView(-1);
		for (NPC npc : view.npcs())
		{
			if (npc != null && npc.getId() == GEMSTONE_CRAB_ID)
			{
				boss = npc;
				break;
			}
		}
		
		if (boss == null)
		{
			return;
		}
		
		try
		{
			// Get health ratio and scale from the NPC
			int healthRatio = boss.getHealthRatio();
			int healthScale = boss.getHealthScale();
			
			// If either value is -1, the health info is not available
			if (healthRatio == -1 || healthScale == -1)
			{
				return;
			}
			
			// Calculate current HP percentage (0-100)
			hpPercent = (((double) healthRatio / (double) healthScale) * 100);
			
			// Check if HP is at or below the threshold
			if (hpPercent <= (config.hpThreshold()) && !notificationSent)
			{
				notificationSent = true;
				notifier.notify(config.hpThresholdNotification(), config.notificationMessage() + " (" + config.hpThreshold() + "% HP)");
				log.debug("Sent notification for Gemstone Crab at {}% HP", hpPercent);
			}
		}
		catch (Exception e)
		{
			log.debug("Failed to calculate HP percentage for notification: {}", e.getMessage());
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
		GameObject gameObject = event.getGameObject();
		
		// If the despawned object is our tracked tunnel, clear it
		if (nearestTunnel != null && gameObject.getId() == TUNNEL_OBJECT_ID && gameObject.equals(nearestTunnel))
		{
			nearestTunnel = null;
		}
	}

	/*
	 * Updates configuration store if value is updated
	 */
	@Subscribe
	public void onConfigChanged​(ConfigChanged event) {
		if (event.getGroup().equalsIgnoreCase(CONFIG_GROUP)) {
			log.debug("event in group. Kay: {}", event.getKey());
			configStore.updateValue(event.getKey(), event.getNewValue());
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
	
	// Update the crab shell reference from the shells map
	private void updateCrabShell()
	{
		if (shells.isEmpty())
		{
			crabShell = null;
			return;
		}
		
		// Since there will only ever be one shell NPC around the player,
		// we can just use the first one in the map
		crabShell = shells.values().iterator().next();
		log.debug("Updated crab shell NPC reference: {}", crabShell != null ? crabShell.getWorldLocation() : "null");
	}

	/*
	 * Save all the kill and mining stats
	 */
	private void saveCrabCounts() {
		log.debug("saving crab counts {}", crabCount);
		// Save counts
        configManager.setRSProfileConfiguration(CONFIG_GROUP, CONFIG_KEY_COUNT, crabCount);
		configManager.setRSProfileConfiguration(CONFIG_GROUP, CONFIG_KEY_MINING_ATTEMPTS, miningAttempts);
		configManager.setRSProfileConfiguration(CONFIG_GROUP, CONFIG_KEY_MINED, minedCount);
		configManager.setRSProfileConfiguration(CONFIG_GROUP, CONFIG_KEY_FAILED, miningFailedCount);
		configManager.setRSProfileConfiguration(CONFIG_GROUP, CONFIG_KEY_GEMS_MINED, gemsMined);
		
		// Save gem tracking
		configManager.setRSProfileConfiguration(CONFIG_GROUP, CONFIG_KEY_OPALS, opals);
		configManager.setRSProfileConfiguration(CONFIG_GROUP, CONFIG_KEY_JADES, jades);
		configManager.setRSProfileConfiguration(CONFIG_GROUP, CONFIG_KEY_RED_TOPAZ, redTopaz);
		configManager.setRSProfileConfiguration(CONFIG_GROUP, CONFIG_KEY_SAPPHIRES, sapphires);
		configManager.setRSProfileConfiguration(CONFIG_GROUP, CONFIG_KEY_EMERALDS, emeralds);
		configManager.setRSProfileConfiguration(CONFIG_GROUP, CONFIG_KEY_RUBIES, rubies);
		configManager.setRSProfileConfiguration(CONFIG_GROUP, CONFIG_KEY_DIAMONDS, diamonds);
		configManager.setRSProfileConfiguration(CONFIG_GROUP, CONFIG_KEY_DRAGONSTONES, dragonstones);
		
		// Save top 3 count
		configManager.setRSProfileConfiguration(CONFIG_GROUP, Constants.CONFIG_KEY_TOP3_COUNT, top3Count);
		
		// Save cumulative XP
		configManager.setRSProfileConfiguration(CONFIG_GROUP, CONFIG_KEY_CUMULATIVE_XP, cumulativeXp);
	}
	
	/*
	 * Reset all mining and gem statistics
	 */
	public void resetStats() {
		// Reset all stats variables
		crabCount = 0;
		miningAttempts = 0;
		minedCount = 0;
		miningFailedCount = 0;
		gemsMined = 0;
		
		// Reset gem tracking variables
		opals = 0;
		jades = 0;
		redTopaz = 0;
		sapphires = 0;
		emeralds = 0;
		rubies = 0;
		diamonds = 0;
		dragonstones = 0;
		
		// Reset cumulative XP
		cumulativeXp = 0;

		// Save the reset values to config
		saveCrabCounts();
		
		// Notify the user
		if (client != null) {
			util.sendChatMessage(Color.BLUE, "All Gemstone Crab statistics have been reset.", true);
			log.debug("All Gemstone Crab statistics have been reset");
		}
	}

	/*
	 * Set the last mining attempt time
	 */
	private void setLastMiningAttempt() {
		lastMiningAttempt = System.currentTimeMillis();
	}

	/*
	 * Used to stop mining from counting multiple times
	 */
	private boolean isMiningBeforeCooldown() {
		return isValidKill() && (System.currentTimeMillis() < lastMiningAttempt + KILL_THRESHOLD_MILLISECONDS);
	}

	/*
	 * Used to stop kill stats from updating multiple times
	 */
	private boolean isKillBeforeCooldown() {
		return (System.currentTimeMillis() < lastKillTime + KILL_THRESHOLD_MILLISECONDS);
	}

	/*
	 * Set the last kill time
	 */
	private void setLastKillTime() {
		lastKillTime = System.currentTimeMillis();
	}

	/*
	 * Count players in the area who are interacting with the crab
	 */
	private void countPlayersInteractingWithCrab() {
		// Reset counter
		playersInteractingWithCrab = 0;
		
		// Get all players in the area
		for (net.runelite.api.Player player : client.getWorldView(-1).players()) {
			// Check if the player is interacting with the crab
			net.runelite.api.Actor interacting = player.getInteracting();
			if (interacting instanceof net.runelite.api.NPC) {
				net.runelite.api.NPC npc = (net.runelite.api.NPC) interacting;
				if (npc.getId() == GEMSTONE_CRAB_ID) {
					playersInteractingWithCrab++;
				}
			}
		}
		
		// Debug log
		log.debug("Players interacting with crab: {}", playersInteractingWithCrab);
	}

	/*
	 * Crab was alive at least 5mins and player was attacking it at least once
	 */
	private boolean isValidKill() {
		return fightDuration > KILL_THRESHOLD_MILLISECONDS;
	}

	/*
	 * Update kill stats and send client messages
	 */
	private void updateKillStats() {
		if (isValidKill()) {
			crabCount++;
			saveCrabCounts();
			util.sendChatMessage(Color.RED, String.format("Gemstone Crab Killed! KC: %d", crabCount), config.displayKillMessage());
            log.debug("Gemstone crab killed! KC: {}, XP gained this fight: {}, Cumulative XP: {}", crabCount, totalXpGained, cumulativeXp);
		}
		else {
			util.sendChatMessage(Color.MAGENTA, "Gemstone Crab not fought long enough for kill count.", config.displayKillMessage());
			log.debug("Gemstone crab kill did not count!");
		}
		
	}

	public boolean isTop3Player(String message) {
		log.debug("Top 3 crab crushers message detected: {}", message);
		String playerName = Text.standardize(client.getLocalPlayer().getName());
		String namesSection = Text.standardize(message.substring(GEMSTONE_CRAB_TOP3_MESSAGE.length()));
		
		boolean isTop3Player = Arrays.asList(namesSection.split("[,&!]"))
			.stream()
			.map(String::trim)
            .filter(name -> !name.isEmpty())
            .collect(Collectors.toList())
			.contains(playerName);
		
		return isTop3Player;
	}

	@Provides
	GemstoneCrabTimerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(GemstoneCrabTimerConfig.class);
	}
}
