# Gemstone Crab

An all-in-one RuneLite plugin for enhancing the Gemstone Crab boss experience.

## Features

### Boss HP Notifications
- Tracks the Gemstone Crab boss HP bar
- Sends desktop notifications when HP reaches a user-defined threshold (default 2%)
- Helps with AFK training by alerting you when to tab back in
  
<img width="456" height="141" alt="image" src="https://github.com/user-attachments/assets/643c11c6-a8c2-4f6e-8ecc-f76d18dde7b3" />

### Tunnel Highlighting
- Automatically highlights the tunnel after the boss dies
- Configurable highlight color
- Optional screen pulse effect when the boss dies to draw attention to the tunnel
- Resets when the boss respawns or you leave the area

### Current Fight Stats Overlay
- Tracks your damage per second (DPS) against the Gemstone Crab boss
- Shows total damage dealt, current DPS, XP gained, and fight duration
- Displays estimated time left in the fight with smooth updates every tick
- Uses XP drops to accurately track damage in all combat styles
- Supports all combat types: Melee (Attack, Strength, Defence), Ranged, and Magic
- Tracks Hitpoints XP separately for display purposes
- Stats remain visible after boss death until a new boss spawns or you leave the area
- Position-based visibility ensures overlay only appears in Gemstone Crab areas
- Each stat can be individually toggled on/off in the configuration

<img width="216" height="156" alt="image" src="https://github.com/user-attachments/assets/4d5446b8-5853-4737-a7fb-258b843822e9" />

### Gem Tracking
- Visual gem tracking overlay displays all gems mined from the boss
- Shows gem icons with their counts in a compact two-row grid
- Each gem type can be individually toggled on/off
- First row (opal, jade, red topaz, sapphire) and second row (emerald, ruby, diamond, dragonstone) can be independently configured
- Tracks and persists gem counts between sessions
- Displays actual counts for all gems, including zero counts

<img width="215" height="134" alt="image" src="https://github.com/user-attachments/assets/21b32805-0a17-48b5-ad79-765181547a20" />

### Kill Stats
- Tracks total Gemstone Crabs killed
- Optional tracking for mining attempts, successful mines, failed mines, and total gems mined
- Stats persist between sessions
- Chat command to reset all stats, including gem tracking (!resetgemcrab)

<img width="213" height="149" alt="image" src="https://github.com/user-attachments/assets/7a27de20-5309-4dd8-9647-88e11f44711c" />

## Configuration Options

### Notifications
- **HP Threshold Notification**: Update Notification settings
- **HP Threshold**: Set the boss HP percentage at which to receive a notification (1-99%)
- **Notification Message**: Customize the message displayed in the notification

### Pulse Screen
- **Pulse Screen**: Toggle the screen pulse effect when the boss dies
- **Pulse Color**: Choose the color for the screen pulse effect

### Highlights
- **Highlight Tunnel**: Toggle tunnel highlighting on/off
- **Tunnel Highlight Color**: Choose the color for tunnel highlighting

### Current Fight Tracking
- **Show overlay**: Toggle the current fight stats section on/off
- **Display total damage**: Toggle display of total damage dealt
- **Display DPS**: Toggle display of damage per second
- **Display XP gained**: Toggle display of XP gained during fight
- **Display duration**: Toggle display of fight duration
- **Display time left**: Toggle display of estimated time remaining

### Stat Tracking
- **Show overlay**: Toggle the kill stats section on/off
- **Display kill count**: Toggle display of total kills
- **Display Kill message**: Toggle display of the kill count in a chat message
- **Display total mining attempts**: Toggle display of mining attempts
- **Display total successful**: Toggle display of successful mining attempts
- **Display total failed**: Toggle display of failed mining attempts
- **Display total gems mined**: Toggle display of total gems mined

### Gem Tracking
- **Show overlay**: Toggle the gem tracking section on/off
- **Display opals/jades/red topaz/sapphires/emeralds/rubies/diamonds/dragonstones**: Toggle display of each gem type

## Installation

1. Open RuneLite
2. Go to Plugin Hub
3. Search for "Gemstone Crab Timer"
4. Click Install

## Authors

GIM Serenity, Pino

## Contributors

Special thanks to: 
- AhDoozy for estimated time left and screen pulse options
- trouttdev for optimising damage/dps calculation

## Support

For issues, suggestions, or contributions, please open an issue on the GitHub repository.

## Changelog

### V1.1.2
- Message now show [Gemstone Crab] in front of them

### V1.1.1
- Fix issue with multiple notificaitons
- Add configuration option to show or hide the kill count chat message

### V1.1.0
- Tracking
    - Gem stats
    - Kill stats 
    - Tunnel highlights
- Configuration options
- Pulse Screen

### V1.0.0
Initial Release
