package com.gimserenity;

import java.util.HashMap;

public class GemstoneCrabConfigStore {
    private HashMap<String, Boolean> configStore = new HashMap<>();

    public void load(GemstoneCrabTimerConfig config) {
        configStore = new HashMap<>();
        configStore.put(Constants.SHOW_MAIN_STATS, config.showMainStats());
        configStore.put(Constants.DISPLAY_TOTAL_DAMAGE, config.displayTotalDamage());
        configStore.put(Constants.DISPLAY_DPS, config.displayDps());
        configStore.put(Constants.DISPLAY_XP_GAINED, config.displayXpGained());
        configStore.put(Constants.DISPLAY_DURATION, config.displayDuration());
        configStore.put(Constants.DISPLAY_PLAYER_COUNT, config.displayPlayerCount());
        configStore.put(Constants.DISPLAY_TIME_LEFT, config.displayTimeLeft());
        configStore.put(Constants.SHOW_STAT_TRACKING, config.showOverlay());
        configStore.put(Constants.SHOW_GEM_TRACKING, config.showGemTracking());
        configStore.put(Constants.DISPLAY_KILL_COUNT, config.displayKillCount());
        configStore.put(Constants.DISPLAY_MINING_ATTEMPTS, config.displayMiningAttempts());
        configStore.put(Constants.DISPLAY_MINED_COUNT, config.displayMinedCount());
        configStore.put(Constants.DISPLAY_FAILED_MINING_COUNT, config.displayFailedMiningCount());
        configStore.put(Constants.DISPLAY_GEM_COUNT, config.displayGemCount());
        configStore.put(Constants.DISPLAY_TOP3_COUNT, config.displayTop3Count());
        configStore.put(Constants.DISPLAY_CUMULATIVE_XP, config.displayCumulativeXp());
        configStore.put(Constants.DISPLAY_OPALS, config.displayOpals());
        configStore.put(Constants.DISPLAY_JADES, config.displayJades());
        configStore.put(Constants.DISPLAY_RED_TOPAZ, config.displayRedTopaz());
        configStore.put(Constants.DISPLAY_SAPPHIRES, config.displaySapphires());
        configStore.put(Constants.DISPLAY_EMERALDS, config.displayEmeralds());
        configStore.put(Constants.DISPLAY_RUBIES, config.displayRubies());
        configStore.put(Constants.DISPLAY_DIAMONDS, config.displayDiamonds());
        configStore.put(Constants.DISPLAY_DRAGONSTONES, config.displayDragonstones());
    }   

    public void updateValue(String key, String value) {
        if (configStore.containsKey(key)){
            boolean newValue = Boolean.parseBoolean(value);
            configStore.put(key, newValue);
        }
    }

    public boolean getValue(String key) {
        return configStore.get(key);
    }
}
