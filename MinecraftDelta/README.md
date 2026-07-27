# Delta Client — Minecraft 1.21.4 Utility Mod

Advanced utility client for Minecraft anarchy servers, inspired by Delta and Nursultan clients.

## Features

### Combat
- **KillAura** — Auto-attack with range/CPS config, multi-target, autoblock
- **Velocity** — Modify/remove incoming knockback
- **Criticals** — Always land critical hits (Packet/MiniJump/Velocity)
- **AutoTotem** — Smart offhand management with health threshold

### Movement
- **Speed** — Strafe, YPort, NCPHop, and Ground modes
- **Flight** — Vanilla, Velocity, and Creative flight modes
- **NoFall** — Prevent fall damage (Packet mode)
- **Sprint** — Legit, Rage, and Omni sprint
- **Step** — Custom step height (up to 5 blocks)

### Render
- **ESP** — Glow/Box outline through walls
- **Chams** — Colored entity overlay
- **Tracers** — Line-of-sight entity pointers
- **FullBright** — Full gamma
- **StorageESP** — Chest/container highlighting

### Player
- **Scaffold** — Auto block placement beneath
- **NoSlow** — No slowdown from items/webs
- **AntiVoid** — Void safety catch
- **FastUse** — Accelerated item usage

### Misc
- **AntiHunger** — Reduced hunger drain
- **NoPacketKick** — Anti-timeout packets
- **AutoTool** — Best tool selection

## Build

```bash
# Requirements: Java 21 JDK

# Unix/macOS
./gradlew build

# Windows
gradlew.bat build
```

Output will be in `build/libs/DeltaClient-1.0.0.jar`. Place in your `.minecraft/mods/` folder with Fabric Loader 0.19.3+ installed.

## Controls

| Action | Key |
|--------|-----|
| ClickGUI | Right Shift |
| Toggle HUD | Left Alt |
| Toggle Module | Click in GUI |
| Module Settings | Right-click module in GUI |

## Credits

Built with Fabric Loom for Minecraft 1.21.4.
