# First Person Camera Height

A Fabric mod for Minecraft 1.21.1 that lets you adjust your first-person camera height without affecting your player hitbox or collision.

Made by [ItsThatNova](https://github.com/ItsThatNova).

---

## What it does

Adds a configurable vertical offset to your first-person camera, from -0.5 to +1.0 blocks in 0.05 increments. Your hands, held items, block targeting crosshair, and projectile spawn position all follow the adjusted height. Your hitbox stays exactly where it normally is.

This is purely a comfort/preference mod — useful if you want a slightly higher or lower perspective than vanilla provides.

---

## Features

- Smooth slider from -0.5 to +1.0 blocks
- Enable/disable toggle
- Hands and held items move with the camera naturally
- Block targeting crosshair matches adjusted camera height
- Projectile spawn position matches adjusted camera height on the server
- No hitbox changes
- Disabled automatically when riding mounts (horses, boats, minecarts, etc.)
- Config persists between sessions
- ModMenu integration for in-game config access
- Debug logging toggle for troubleshooting (off by default)

---

## Installation

### Client only (minimum)
Install on your client only. Camera height, hand position, and block targeting all work. On multiplayer servers without the mod installed, projectiles will still fire from vanilla eye height — only the visual camera moves.

### Client + Server (full experience)
Install on **both** client and server. Projectile spawn position will match your adjusted camera height. This is required for the full effect on multiplayer.

---

## Who this mod is for

This mod is intended for **singleplayer** and **casual/private servers** where all players are aware of and okay with the change.

**It is not recommended for PVP servers.** A player with an elevated offset fires projectiles from a higher position than their visual model suggests, which could be considered an unfair advantage. Please be considerate about where you use it.

> ⚠️ **Anti-cheat notice:** Some public servers run anti-cheat software that may flag the server-side mixin's behaviour (modifying projectile origin position). Use on public servers at your own risk. The mod author is not responsible for bans or other consequences.

---

## The observer mismatch — what others see

When the mod is installed server-side, your projectiles spawn from your adjusted camera height. However, **other players on the server see your hand animation rendered at vanilla height** — their client has no knowledge of your offset. This means observers will see a slight gap between where your hand appears to be and where the arrow or projectile actually comes from.

This is a fundamental cosmetic limitation of the approach and cannot be fixed without forcing the mod on all clients. It does not affect gameplay for the shooter — your own crosshair and aim are accurate. It's simply a visual quirk from the outside.

---

## Requirements

- Minecraft 1.21.1
- Fabric Loader >= 0.16.0
- Fabric API

**Optional:**
- [ModMenu](https://modrinth.com/mod/modmenu) — provides in-game config screen access

---

## Configuration

Access via ModMenu, or edit `.minecraft/config/firstpersoncameraheight.json` directly:

```json
{
  "enabled": true,
  "offset": 0.0,
  "debugLogging": false
}
```

- `enabled` — `true`/`false`, toggles the offset on or off
- `offset` — float between `-0.5` and `1.0` (in blocks)
- `debugLogging` — `true`/`false`, prints sync events to your game log; leave off during normal play

---

## How it works

**CameraOffsetMixin** shifts the camera's Y position after vanilla calculates it each frame. Hands and held items follow naturally since the hand render pass uses the camera as its origin.

**EyeHeightOffsetMixin** offsets `getCameraPosVec` and `getClientCameraPosVec` on the local client player so block-targeting raycasts originate from the adjusted height.

**ServerEyePosOffsetMixin** intercepts `Entity.getEyePos()` and `Entity.getEyeY()` on the server thread, but only for `ServerPlayerEntity` instances that have synced an offset. `getEyePos()` covers targeting and raycasting; `getEyeY()` covers projectile spawn position (used by bows, crossbows, tridents, and all thrown items). Players without the mod are completely unaffected.

**OffsetSyncPayload** is a C2S (client-to-server) packet sent one second after world join and whenever the player changes their config. It carries the enabled state, offset value, and debug logging flag. The server stores offsets per player UUID in a `ConcurrentHashMap` and clears them on disconnect.

---

## Building from source

```bash
git clone https://github.com/ItsThatNova/first-person-camera-height
cd first-person-camera-height
./gradlew build
```

Built jar will be in `build/libs/`.

---

## License

MIT — free to fork, modify, and redistribute with attribution.
