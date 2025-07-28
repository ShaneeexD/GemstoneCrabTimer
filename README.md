# Gemstone Crab Timer

A RuneLite plugin for enhancing the Gemstone Crab boss afk experience.

## Features

### Boss HP Notifications
- Tracks the Gemstone Crab boss HP bar
- Sends desktop notifications when HP reaches a user-defined threshold (default 2%)
- Helps with AFK training by alerting you when to tab back in
  
<img width="456" height="141" alt="image" src="https://github.com/user-attachments/assets/643c11c6-a8c2-4f6e-8ecc-f76d18dde7b3" />

### Tunnel Highlighting
- Automatically highlights the tunnel after the boss dies
- Configurable highlight color
- Resets when the boss respawns or you leave the area

### DPS Tracker
- Tracks your damage per second (DPS) against the Gemstone Crab boss
- Shows total damage dealt, current DPS, XP gained, and fight duration
- Uses XP drops to accurately track damage in all combat styles
- Supports all combat types: Melee (Attack, Strength, Defence), Ranged, and Magic
- Tracks Hitpoints XP separately for display purposes
- Stats remain visible after boss death until a new boss spawns or you leave the area
- Position-based visibility ensures overlay only appears in Gemstone Crab areas
- Can be toggled on/off in the configuration

<img width="193" height="117" alt="image" src="https://github.com/user-attachments/assets/284365cc-4655-41cc-8643-d652ee9c1e84" />

## Configuration Options

- **HP Threshold**: Set the boss HP percentage at which to receive a notification
- **Notification Message**: Customize the message displayed in the notification
- **Show DPS Tracker**: Toggle the DPS tracker overlay on or off
- **Tunnel Highlight Color**: Choose the color for tunnel highlighting

## Installation

1. Open RuneLite
2. Go to Plugin Hub
3. Search for "Gemstone Crab Timer"
4. Click Install

## Support

For issues, suggestions, or contributions, please open an issue on the GitHub repository.


## Recent Updates

### Estimated Time Left Countdown
- Added an "Est Time Left" timer to the DPS overlay, shown below the "Duration"
- Countdown uses Jagex’s base 10-minute timer for the Gemstone Crab and updates in real-time based on remaining HP
- Helps players better gauge how much time remains in the current fight

### Screen Pulse Highlighting
- "Pulse Screen" option now available in config to enable/disable the visual effect
- "Pulse Color" config allows users to customize the highlight color used when the tunnel is active
- Includes a built-in pulse delay to prevent rapid flashing, making the effect more comfortable for users