# ShadowText

**Offline Text Steganography Engine for Android**

Hide any file inside ordinary-looking text using invisible Unicode characters. No servers. No internet. No tracking.

## Phase 1 — Proof of Concept ✓

### What it does
- **Encode**: Select any file (image, video, audio, PDF, ZIP, APK, any file) and encode it into text
- **Decode**: Paste any stego text and extract the hidden file
- **Detect**: Automatically scans text for hidden payloads

### How it works
Files are converted to bytes, wrapped in a versioned binary format with CRC32 integrity checks, then encoded using invisible Zero-Width Unicode characters (U+200B, U+200C, U+200D, U+FEFF). The invisible payload is embedded inside ordinary English carrier text. The result looks like normal sentences but carries hidden data.

### Technology
- Kotlin + Jetpack Compose
- MVVM + Clean Architecture
- Hilt Dependency Injection
- Minimum SDK 24 (Android 7.0)
- Fully offline — no network permission

## Building

### Local Build
```bash
# Debug APK
./gradlew assembleDebug

# Run tests
./gradlew test

# Release APK
./gradlew assembleRelease
```

### CI/CD
GitHub Actions builds on every push to `main`/`develop`. See `.github/workflows/android-ci.yml`.
Artifacts: debug APK (every build), release APK (main branch only).

## Project Structure

```
ShadowText/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/ai/zaro/shadowtext/
│   │   │   │   ├── core/          # Core engine
│   │   │   │   │   ├── format/    # Binary packet format
│   │   │   │   │   ├── encoding/  # Invisible Unicode encoding
│   │   │   │   │   └── engine/    # Stego encoder/decoder
│   │   │   │   ├── data/          # Data layer
│   │   │   │   │   └── repository/
│   │   │   │   ├── domain/        # Use cases
│   │   │   │   │   └── usecase/
│   │   │   │   ├── di/            # Dependency injection
│   │   │   │   └── ui/            # Compose UI
│   │   │   │       ├── screens/
│   │   │   │       └── viewmodel/
│   │   │   └── res/
│   │   └── test/                  # Unit tests
│   └── build.gradle.kts
├── .github/workflows/             # CI/CD
├── ARCHITECTURE.md                # Full architecture docs
└── README.md
```

## Architecture

See [ARCHITECTURE.md](ARCHITECTURE.md) for the complete architecture documentation including:
- Layer diagram
- Data flow (encode/decode pipelines)
- Binary packet format specification
- Invisible character encoding tables
- Future phase plans (encryption, optimization)
