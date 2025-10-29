package com.gimserenity;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.text.DecimalFormat;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import javax.inject.Inject;

public class GemstoneCrabTimerDpsOverlay extends Overlay
{
    private final GemstoneCrabTimerPlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();
    private static final DecimalFormat DPS_FORMAT = new DecimalFormat("#,##0.0");
    
    @Inject
    private GemstoneCrabTimerConfig config;
    
    // Gem item IDs for icons
    private static final int UNCUT_OPAL_ITEM_ID = net.runelite.api.gameval.ItemID.UNCUT_OPAL;
    private static final int UNCUT_JADE_ITEM_ID = net.runelite.api.gameval.ItemID.UNCUT_JADE;
    private static final int UNCUT_RED_TOPAZ_ITEM_ID = net.runelite.api.gameval.ItemID.UNCUT_RED_TOPAZ;
    private static final int UNCUT_SAPPHIRE_ITEM_ID = net.runelite.api.gameval.ItemID.UNCUT_SAPPHIRE;
    private static final int UNCUT_EMERALD_ITEM_ID = net.runelite.api.gameval.ItemID.UNCUT_EMERALD;
    private static final int UNCUT_RUBY_ITEM_ID = net.runelite.api.gameval.ItemID.UNCUT_RUBY;
    private static final int UNCUT_DIAMOND_ITEM_ID = net.runelite.api.gameval.ItemID.UNCUT_DIAMOND;
    private static final int UNCUT_DRAGONSTONE_ITEM_ID = net.runelite.api.gameval.ItemID.UNCUT_DRAGONSTONE;
    
    @Inject
    private ItemManager itemManager;

    // Uncomment if you want to use the timing helper for testing render times
    // @Inject
    // private GemstoneCrabFunctionTimingHelper timing;

    private GemstoneCrabConfigStore configStore;
    
    @Inject
    private GemstoneCrabTimerDpsOverlay(GemstoneCrabTimerPlugin plugin)
    {
        this.plugin = plugin;
        this.configStore = plugin.getConfigStore();
        setPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }
    
    @Override
    public Dimension render(Graphics2D graphics)
    {
        // Used for Testing Render Timing
        // var startTime = System.nanoTime();

        // Don't render if player is not in a Gemstone Crab area
        if (!plugin.isPlayerInGemstoneArea())
        {
            return null;
        }
        
        // Set panel properties for proper sizing
        panelComponent.setPreferredSize(new Dimension(165, 0));
        // Ensure proper spacing between components
        panelComponent.setGap(new Point(0, 4));
        
        // Set up the panel
        panelComponent.getChildren().clear();
        panelComponent.setBackgroundColor(config.overlayBackgroundColor());
        
        // Resolve text colors
        Color headerColor = config.overlayHeaderTextColor();
        Color itemTextColor = config.overlayItemTextColor();
        
        // Show main stats section if enabled
        if (configStore.getValue(Constants.SHOW_MAIN_STATS))
        {
            // Add title
            panelComponent.getChildren().add(TitleComponent.builder()
                .text("Current Fight")
                .color(headerColor)
                .build());
                
            // Add players interacting with crab
            if (configStore.getValue(Constants.DISPLAY_PLAYER_COUNT)) {
                panelComponent.getChildren().add(LineComponent.builder()
                    .left("Players Fighting:")
                    .leftColor(itemTextColor)
                    .right(String.valueOf(plugin.getPlayersInteractingWithCrab()))
                    .rightColor(itemTextColor)
                    .build());
            }
            
            // Add total damage if enabled
            if (configStore.getValue(Constants.DISPLAY_TOTAL_DAMAGE)) {
                panelComponent.getChildren().add(LineComponent.builder()
                    .left("Total Damage:")
                    .leftColor(itemTextColor)
                    .right(String.format("%,d", plugin.getTotalDamage()))
                    .rightColor(itemTextColor)
                    .build());
            }
            
            // Add DPS if enabled
            if (configStore.getValue(Constants.DISPLAY_DPS)) {
                panelComponent.getChildren().add(LineComponent.builder()
                    .left("DPS:")
                    .leftColor(itemTextColor)
                    .right(DPS_FORMAT.format(plugin.getCurrentDps()))
                    .rightColor(itemTextColor)
                    .build());
            }
            
            // Add XP gained if enabled
            if (configStore.getValue(Constants.DISPLAY_XP_GAINED)) {
                panelComponent.getChildren().add(LineComponent.builder()
                    .left("XP Gained:")
                    .leftColor(itemTextColor)
                    .right(String.format("%,d", plugin.getTotalXpGained()))
                    .rightColor(itemTextColor)
                    .build());
            }
            
            // Add fight duration if enabled
            if (configStore.getValue(Constants.DISPLAY_DURATION)) {
                long seconds = plugin.getFightDuration() / 1000;
                panelComponent.getChildren().add(LineComponent.builder()
                    .left("Duration:")
                    .leftColor(itemTextColor)
                    .right(String.format("%d:%02d", seconds / 60, seconds % 60))
                    .rightColor(itemTextColor)
                    .build());
            }
            
            // Add estimated time left if enabled
            if (configStore.getValue(Constants.DISPLAY_TIME_LEFT) && plugin.isFightInProgress()) {
                long timeLeftMillis = plugin.getEstimatedTimeRemainingMillis();
                if (timeLeftMillis > 0) {
                    long secondsLeft = timeLeftMillis / 1000;
                    panelComponent.getChildren().add(LineComponent.builder()
                        .left("Time Left:")
                        .leftColor(itemTextColor)
                        .right(String.format("%d:%02d", secondsLeft / 60, secondsLeft % 60))
                        .rightColor(itemTextColor)
                        .build());
                }
            }
        }

        if (configStore.getValue(Constants.SHOW_STAT_TRACKING)) {
            panelComponent.getChildren().add(TitleComponent.builder()
                .text("Kill Stats")
                .color(headerColor)
                .build());

            if (configStore.getValue(Constants.DISPLAY_KILL_COUNT)) {
                panelComponent.getChildren().add(LineComponent.builder()
                    .left("Crabs Killed:")
                    .leftColor(itemTextColor)
                    .right(String.valueOf(plugin.getCrabCount()))
                    .rightColor(itemTextColor)
                    .build());
            }

            if (configStore.getValue(Constants.DISPLAY_MINING_ATTEMPTS)) {
                panelComponent.getChildren().add(LineComponent.builder()
                    .left("Mining Attempts:")
                    .leftColor(itemTextColor)
                    .right(String.valueOf(plugin.getMiningAttemptsCount()))
                    .rightColor(itemTextColor)
                    .build());
            }


            if (configStore.getValue(Constants.DISPLAY_MINED_COUNT)) {
                panelComponent.getChildren().add(LineComponent.builder()
                    .left("Successful:")
                    .leftColor(itemTextColor)
                    .right(String.valueOf(plugin.getMinedCount()))
                    .rightColor(itemTextColor)
                    .build());
            }

            if (configStore.getValue(Constants.DISPLAY_FAILED_MINING_COUNT)) {
                panelComponent.getChildren().add(LineComponent.builder()
                    .left("Failed:")
                    .leftColor(itemTextColor)
                    .right(String.valueOf(plugin.getMiningFailedCount()))
                    .rightColor(itemTextColor)
                    .build());
            }

            if (configStore.getValue(Constants.DISPLAY_GEM_COUNT)) {
                panelComponent.getChildren().add(LineComponent.builder()
                    .left("Gems Mined:")
                    .leftColor(itemTextColor)
                    .right(String.valueOf(plugin.getGemsCount()))
                    .rightColor(itemTextColor)
                    .build());
            }
            
            if (configStore.getValue(Constants.DISPLAY_TOP3_COUNT)) {
                panelComponent.getChildren().add(LineComponent.builder()
                    .left("Top 3:")
                    .leftColor(itemTextColor)
                    .right(String.valueOf(plugin.getTop3Count()))
                    .rightColor(itemTextColor)
                    .build());
            }
            
            if (configStore.getValue(Constants.DISPLAY_CUMULATIVE_XP)) {
                panelComponent.getChildren().add(LineComponent.builder()
                    .left("Total Cumulative XP:")
                    .leftColor(itemTextColor)
                    .right(String.format("%,d", plugin.getCumulativeXp()))
                    .rightColor(itemTextColor)
                    .build());
            }
        }
        
        // Show gem tracking section if enabled
        if (configStore.getValue(Constants.SHOW_GEM_TRACKING)) {
            
            panelComponent.getChildren().add(TitleComponent.builder()
                .text("Gem Tracking")
                .color(headerColor)
                .build());
            
            // First row panel for first 4 gems
            PanelComponent firstRowPanel = new PanelComponent();
            firstRowPanel.setOrientation(ComponentOrientation.HORIZONTAL);
            // Set width but let height adjust automatically
            firstRowPanel.setPreferredSize(new Dimension(175, 0));
            // Small gap between gems in the row
            firstRowPanel.setGap(new Point(4, 0));
            
            // First row gems (opal, jade, topaz, sapphire)
            if (configStore.getValue(Constants.DISPLAY_OPALS)) {
                firstRowPanel.getChildren().add(new ImageComponent(
                    itemManager.getImage(UNCUT_OPAL_ITEM_ID, plugin.getOpalsCount(), true)));
            }
            
            if (configStore.getValue(Constants.DISPLAY_JADES)) {
                firstRowPanel.getChildren().add(new ImageComponent(
                    itemManager.getImage(UNCUT_JADE_ITEM_ID, plugin.getJadesCount(), true)));
            }
            
            if (configStore.getValue(Constants.DISPLAY_RED_TOPAZ)) {
                firstRowPanel.getChildren().add(new ImageComponent(
                    itemManager.getImage(UNCUT_RED_TOPAZ_ITEM_ID, plugin.getRedTopazCount(), true)));
            }
            
            if (configStore.getValue(Constants.DISPLAY_SAPPHIRES)) {
                firstRowPanel.getChildren().add(new ImageComponent(
                    itemManager.getImage(UNCUT_SAPPHIRE_ITEM_ID, plugin.getSapphiresCount(), true)));
            }
            
            // Only add the first row panel if it has gems to display
            if (!firstRowPanel.getChildren().isEmpty()) {
                panelComponent.getChildren().add(firstRowPanel);
                
                // Add a small spacer before the second row if both rows will be shown
                if (configStore.getValue(Constants.DISPLAY_DIAMONDS) || 
                    configStore.getValue(Constants.DISPLAY_RUBIES) || 
                    configStore.getValue(Constants.DISPLAY_DIAMONDS) || 
                    configStore.getValue(Constants.DISPLAY_DRAGONSTONES)) {
                    panelComponent.getChildren().add(LineComponent.builder().build());
                }
            }
            
            // Second row 
            PanelComponent secondRowPanel = new PanelComponent();
            secondRowPanel.setOrientation(ComponentOrientation.HORIZONTAL);
            // Set width but let height adjust automatically
            secondRowPanel.setPreferredSize(new Dimension(175, 0));
            // Small gap between gems in the row
            secondRowPanel.setGap(new Point(4, 0));
            
            // Second row gems (emerald, ruby, diamond, dragonstone) - always show all gems
            if (configStore.getValue(Constants.DISPLAY_EMERALDS)) {
                secondRowPanel.getChildren().add(new ImageComponent(
                    itemManager.getImage(UNCUT_EMERALD_ITEM_ID, plugin.getEmeraldsCount(), true)));
            }
            
            if (configStore.getValue(Constants.DISPLAY_RUBIES) ) {
                secondRowPanel.getChildren().add(new ImageComponent(
                    itemManager.getImage(UNCUT_RUBY_ITEM_ID, plugin.getRubiesCount(), true)));
            }
            
            if (configStore.getValue(Constants.DISPLAY_DIAMONDS)) {
                secondRowPanel.getChildren().add(new ImageComponent(
                    itemManager.getImage(UNCUT_DIAMOND_ITEM_ID, plugin.getDiamondsCount(), true)));
            }
            
            if (configStore.getValue(Constants.DISPLAY_DRAGONSTONES)) {
                secondRowPanel.getChildren().add(new ImageComponent(
                    itemManager.getImage(UNCUT_DRAGONSTONE_ITEM_ID, plugin.getDragonstonesCount(), true)));
            }
            
            // Only add the second row if it has gems to display
            if (!secondRowPanel.getChildren().isEmpty()) {
                panelComponent.getChildren().add(secondRowPanel);
            }
            
            // Padding
            panelComponent.getChildren().add(LineComponent.builder().build());
            panelComponent.getChildren().add(LineComponent.builder()
                .left(" ")
                .build());
            
            // Keep main panel in vertical orientation for the rest of the overlay
            panelComponent.setOrientation(ComponentOrientation.VERTICAL);
        }

        // Used for Testing Render Timing
        // var endTime = System.nanoTime();
        // long totalTimeNanos = endTime - startTime;
        // timing.addRender(totalTimeNanos);
        return panelComponent.render(graphics);
    }
}
