# ShadowText

**Offline Text-in-Text Steganography Engine for Android**

Hide text inside ordinary-looking text using invisible Unicode characters.
No servers. No internet. No tracking. No accounts.

> **Phase 1 — Proof of Concept.** ShadowText currently implements
> **Text-in-Text** steganography (hiding one text inside another text).
> File-based hiding ("Text in File", "Large Files") is planned for a later
> phase.

---

## What it does

- **Encode** — hide your secret text inside ordinary carrier text using
  invisible Unicode characters.
- **Decode** — paste encoded text and extract the hidden message back.
- **History** — optional, privacy-first activity log (metadata only).
- **Offline** — all processing happens on-device. No network permission.

## Encoding methods

| Method | Identifier | How it works |
| --- | --- | --- |
| **Zero-Width Characters** | `zwc` | 4 invisible characters (`U+200B`, `U+200C`, `U+200D`, `U+FEFF`) encode 2 bits each |
| **Variation Selectors** | `vs256` | 256 Unicode Variation Selectors (`U+FE00–U+FE0F`, `U+E0100–U+E01EF`) encode 1 byte each |
| **Space Homoglyphs** | `spgh` | 8 visually-identical whitespace characters encode 3 bits each |

The core pipeline converts the payload to bytes, wraps it in a versioned
binary packet (magic number + version + type + length + metadata + CRC32
checksum), and encodes those bytes as invisible characters embedded within
the carrier text.

## ⚠️ Important: This is NOT encryption

Phase 1 steganography **hides** data but does **not encrypt** it. The
encoding scheme is deterministic and uses known, fixed Unicode characters.
Anyone who knows the scheme (or has access to this source code) can detect
and extract hidden data.

**Do not treat hidden data as cryptographically secure.** Strong
authentication/encryption is planned as a future phase. Until then, do not
rely on ShadowText to protect truly sensitive information.

## Privacy model

ShadowText is designed for local, offline processing:

- ❌ No backend, no server, no cloud processing.
- ❌ No analytics, no telemetry, no user-content transmission.
- ❌ No INTERNET permission (see `AndroidManifest.xml`).
- ❌ History stores **metadata only** (operation type, input/output length,
  status, timestamp) — never message content, carrier text, encoded text,
  or decoded text.
- ❌ History can be disabled entirely in Settings.
- ✔️ `android:allowBackup="false"` — history is not backed up off-device.

## Building

```bash
# Debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run lint
./gradlew lint

# Release APK (unsigned unless signing is configured — see below)
./gradlew assembleRelease
```

Requirements: JDK 17, Android SDK 34.

## Release signing

The release build reads signing credentials from environment variables (see
`app/build.gradle.kts`). They are **never** committed to the repository.

| Variable | Purpose |
| --- | --- |
| `MYKEYSTORE_PATH` | Path to the release keystore (`.jks`) |
| `MYKEYSTORE_PASSWORD` | Keystore password |
| `MYKEY_ALIAS` | Key alias |
| `MYKEY_PASSWORD` | Key (private-key) password |

- If `MYKEYSTORE_PATH` is **not** set, `assembleRelease` produces an
  **unsigned** APK — it is **never** silently signed with the debug key.
- Do not commit the keystore or its passwords. Use GitHub Secrets (see
  below).

## GitHub Actions

The production workflow (`.github/workflows/android-ci.yml`) runs on every
push to `main`, `develop`, or `new-ui`, and on pull requests:

1. Checkout + JDK 17 + Android SDK + Gradle cache
2. **Lint** (`gradle lint`)
3. **Unit tests** (`gradle test`)
4. **Release build** (`gradle assembleRelease`)
5. **Sign** (only if signing secrets are present)
6. **Verify** the APK is release-signed (not debug)
7. **Upload** the APK as an artifact

The workflow fails if compilation, tests, lint, or signing fails.

### Required GitHub Secrets (for release signing)

| Secret | Description |
| --- | --- |
| `MYKEYSTORE_BASE64` | Your release keystore, base64-encoded |
| `MYKEYSTORE_PASSWORD` | Keystore password |
| `MYKEY_ALIAS` | Key alias |
| `MYKEY_PASSWORD` | Key password |

To create the base64 secret:
```bash
base64 -w0 my-release-key.jks
```
Then paste the output as the value of the `MYKEYSTORE_BASE64` secret.

## Project structure

```
app/src/main/java/ai/zaro/shadowtext/
├── core/
│   ├── format/      # Binary packet format (magic, version, CRC32)
│   ├── encoding/    # Invisible Unicode encoders (ZW, VS, homoglyph)
│   └── engine/      # Stego encoder/decoder orchestration
├── data/            # Repository (file I/O)
├── domain/          # Use cases
├── di/              # Hilt dependency injection
└── ui/              # Jetpack Compose UI (screens, navigation, theme)
```

## Current limitations

- **Text-in-Text only** — file hiding is not yet implemented.
- **No encryption** — see the warning above.
- **Capacity** — each byte requires multiple invisible characters
  (4 for ZW, 1 for VS, ~2.7 for homoglyph), so payloads are limited by
  carrier-text length.
- **Stealth vs. length** — a very long payload on very short carrier text
  is easy to notice.

See [ARCHITECTURE.md](ARCHITECTURE.md) for full technical documentation.
