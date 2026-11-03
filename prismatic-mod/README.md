# Prismatic Client (Fabric 1.21.11)

HUD modules + a Right Shift menu + a synced Spotify lyrics HUD.

## Build (GitHub Actions)
1. Create a new GitHub repo and upload everything in this folder (keep the `.github` folder).
2. Open the **Actions** tab. The "Build Prismatic Client" run starts by itself (or press "Run workflow").
3. When it turns green, open the run and download the **prismatic-client** artifact. Unzip it: the `.jar` inside is the mod.

## Install
Put these in `.minecraft/mods`: Fabric Loader 0.18.x, **Fabric API for 1.21.11**, and `prismatic-client-1.0.0.jar`.

## Use
Press **Right Shift** in game. Toggle modules on, and click **Edit** on "Now playing" for the lyrics font size/style/colors.
Settings are saved to `.minecraft/config/prismatic_client.json`.

## Now playing (Spotify lyrics)
- Windows only. It reads what Spotify is playing from Windows' media controls (no Spotify login) and gets synced lyrics from LRCLIB.
- Open the Spotify desktop app and play a song. Songs without synced lyrics show the title instead.
- If lyrics are a little early/late, use the Lyrics delay buttons in Edit.
- The Smooth font is Liberation Sans Bold (SIL OFL). It has Latin letters only; other scripts may show boxes. Pick Minecraft or Uniform for those.

## Working modules
FPS, Coordinates, Ping, Clock, Direction, Speed, Memory, Keystrokes, Clicks per second, Combo counter, Armor status,
Potion status, Pot counter, Server info, Biome, World clock, Reach display, Toggle sneak, Now playing.
All other cards in the menu are marked SOON.
