package com.gimserenity;


import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
// Using OverlayUtil instead of deprecated OverlayPriority
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;

public class GemstoneCrabTimerOverlay extends Overlay
{
    private final GemstoneCrabTimerPlugin plugin;
    private final GemstoneCrabTimerConfig config;
    private final Client client;
    
    @Inject
    public GemstoneCrabTimerOverlay(GemstoneCrabTimerPlugin plugin, GemstoneCrabTimerConfig config, Client client)
    {
        this.plugin = plugin;
        this.config = config;
        this.client = client;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        boolean renderTunnel = config.highlightTunnel() && plugin.getNearestTunnel() != null && plugin.shouldHighlightTunnel();
        boolean showTimeLeft = config.tunnelTimeLeft() && plugin.getNearestTunnel() != null && plugin.getEstimatedTimeRemainingMillis() > 0;
        
        GameObject tunnel = plugin.getNearestTunnel();
        if (tunnel != null)
        {
            Color color = config.tunnelHighlightColor();
            if (tunnel.getCanvasTextLocation(graphics, "Tunnel", 0) != null)
            {
                Point textLocation = tunnel.getCanvasTextLocation(graphics, "Tunnel", 0);
                if (textLocation != null)
                {
                    // Only render "Tunnel" text if we should highlight the tunnel
                    if (renderTunnel)
                    {
                        OverlayUtil.renderTextLocation(graphics, textLocation, "Tunnel", color);
                    }
                    
                    // Always show countdown timer overlay above tunnel when in area
                    if (showTimeLeft)
                    {
                        long timeLeftMillis = plugin.getEstimatedTimeRemainingMillis();
                        long secondsLeft = timeLeftMillis / 1000;
                        long minutes = secondsLeft / 60;
                        long seconds = secondsLeft % 60;
                        String countdownText = String.format("%d:%02d", minutes, seconds);
                        Point countdownLocation = new Point(textLocation.getX(), textLocation.getY() - 15);
                        OverlayUtil.renderTextLocation(graphics, countdownLocation, countdownText, Color.WHITE);
                    }
                }
            
            // Only highlight the tunnel if we should
            if (renderTunnel)
            {
                Shape objectClickbox = tunnel.getConvexHull();
                if (objectClickbox != null)
                {
                    // Semi-transparent fill with the configured color
                    graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
                    graphics.fill(objectClickbox);
                    
                    // Solid yellow outline
                    graphics.setColor(Color.YELLOW);
                    graphics.draw(objectClickbox);
                }
            }
        }
        }
        
        // Pulse the screen as long as the fight is over & player is in area
        if (plugin.shouldPulseScreen())
        {
            long now = System.currentTimeMillis();
            float currentPhase = (float)(Math.sin(now / 1000.0 * Math.PI) + 1) / 2; // cycle every 2s
            int alpha = (int)(50 + currentPhase * 100); // safe range: 50–150
            
            Color pulseColor = config.pulseColor();
            Graphics2D g2d = (Graphics2D) graphics.create();
            g2d.setComposite(AlphaComposite.SrcOver.derive(alpha / 255f));
            g2d.setColor(pulseColor);
            g2d.fillRect(0, 0, client.getCanvas().getWidth(), client.getCanvas().getHeight());
            g2d.dispose();
        }

        return null;
    }
}
