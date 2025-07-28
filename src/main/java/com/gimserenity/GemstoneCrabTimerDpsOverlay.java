package com.gimserenity;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.text.DecimalFormat;

public class GemstoneCrabTimerDpsOverlay extends Overlay
{
    private final GemstoneCrabTimerPlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();
    private static final DecimalFormat DPS_FORMAT = new DecimalFormat("#,##0.0");
    
    @Inject
    private GemstoneCrabTimerDpsOverlay(GemstoneCrabTimerPlugin plugin)
    {
        this.plugin = plugin;
        setPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }
    
    @Inject
    private GemstoneCrabTimerConfig config;
    
    @Override
    public Dimension render(Graphics2D graphics)
    {
        // Check if DPS tracker is enabled in config
        if (!config.showDpsTracker())
        {
            return null;
        }
        
        // Don't show if no fight is in progress and no damage has been done
        if (!plugin.isFightInProgress() && plugin.getTotalDamage() == 0)
        {
            return null;
        }
        
        panelComponent.getChildren().clear();
        
        // Add title
        panelComponent.getChildren().add(TitleComponent.builder()
            .text("Gemstone Crab DPS")
            .color(Color.GREEN)
            .build());
        
        // Add total damage
        panelComponent.getChildren().add(LineComponent.builder()
            .left("Total Damage:")
            .right(String.format("%,d", plugin.getTotalDamage()))
            .build());
        
        // Add DPS
        panelComponent.getChildren().add(LineComponent.builder()
            .left("DPS:")
            .right(DPS_FORMAT.format(plugin.getCurrentDps()))
            .build());
        
        // Add countdown timer and duration
        if (plugin.isFightInProgress() || plugin.getFightDuration() > 0)
        {
            long duration = plugin.getFightDuration() / 1000;
            panelComponent.getChildren().add(LineComponent.builder()
                .left("Duration:")
                .right(String.format("%d:%02d", duration / 60, duration % 60))
                .build());

            long millisLeft = plugin.getEstimatedTimeRemainingMillis();
            long secondsLeft = millisLeft / 1000;
            panelComponent.getChildren().add(LineComponent.builder()
                .left("Est Time Left:")
                .right(String.format("%d:%02d", secondsLeft / 60, secondsLeft % 60))
                .build());
        }
        
        return panelComponent.render(graphics);
    }
}
