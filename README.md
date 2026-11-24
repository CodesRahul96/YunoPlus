# Yuno+

An ad-free, modern YouTube player for Android built with Jetpack Compose and Material 3.

---

## Features

### Playback & Media
* **Ad-Free Streaming:** Clean video and audio streaming without interruptions.
* **Up to 4K (2160p):** Crisp Ultra HD 4K, 1440p, and 1080p 60fps streaming with VP9 and dynamic DASH manifest adaptation.
* **Background Audio:** Full foreground media service playback with lock-screen and status-bar media notification controls.
* **SponsorBlock:** Built-in skipping of sponsored segments, intros, and self-promotions with per-category configuration.
* **On-Video Controls:** Draggable scrubber with storyboard preview, customizable skip buttons, chapter markers, speed control (0.5×–3×), and picture-in-picture (PiP).
* **Mini-Player & Status Bar Dismiss:** Smooth swipe-to-minimize floating dock with play/pause and instant close (`✕`) buttons.

### UI & Experience
* **Liquid Glass Interface:** Frosted translucent bars with custom AGSL runtime shaders and backdrop blurs.
* **Notifications Center:** Dedicated notifications page on the top bar showing new uploads and alerts from subscribed channels with login state handling.
* **Shorts Feed:** Dedicated vertical feed with an on-disk pre-caching engine for instant swipe playback.
* **Adaptive Layouts:** Native support for foldable devices (tabletop / flex mode) and tablets.
* **Dynamic Artwork Palette:** Channel pages and playlist headers adapt to extracted artwork colors.

### Privacy & Account
* **Sign-in Optional:** Full search, discovery, and guest recommendations work without an account.
* **Google Account Sync:** Optional Google sign-in to sync subscriptions, history, likes, and playlists.
* **Hardware-Backed Encryption:** Session cookies encrypted with AES-GCM via the Android Keystore.
* **Self-Updating:** In-app updates from GitHub releases.

---

## Download

Get the latest build from the [Releases](https://github.com/CodesRahul96/YunoPlus/releases) page:
* **[YunoPlus.apk](https://github.com/CodesRahul96/YunoPlus/releases/latest/download/YunoPlus.apk)**

---

## Building from Source

### Prerequisites
* Android SDK (API 36 / 37)
* JDK 21+

### Build Commands
```sh
# Assemble debug APK
./gradlew assembleDebug

# Assemble release APK (R8 shrunk, release signed)
./gradlew assembleRelease
```

---

## License

This project is licensed under the **GNU General Public License v3.0 (GPLv3)**.
