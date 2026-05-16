# 🎬 Pocket Studios

> Your pocket-sized professional video editor for Android

[![CI/CD](https://img.shields.io/gitlab/pipeline/pocketstudios/android/main)](https://gitlab.com/pocketstudios/android)
[![API](https://img.shields.io/badge/API-26%2B-green.svg)](https://android-arsenal.com/api?level=26)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-purple.svg)](https://kotlinlang.org)
[![License](https://img.shields.io/badge/License-Proprietary-red.svg)](#)

## 📋 Overview

Pocket Studios is a professional-grade Android video editor built with Kotlin + Jetpack Compose. It delivers CapCut-level editing capabilities while prioritising user privacy — all video processing happens on-device.

### Key Features
- 🎬 Multi-track timeline (video + audio + text overlays)
- ✂️ Precision trim, split, reorder with undo/redo (50 steps)
- 🎨 50+ free cinematic color filters + LUT support (Pro)
- 🔄 30+ transitions with real-time preview
- ⚡ Speed control: 0.1x to 10x
- 🤖 AI auto-captions (on-device Whisper ONNX — no internet)
- 🤖 AI background removal (ML Kit Selfie Segmentation)
- 📤 Export up to 1080p free — no watermark, no login required
- 📤 4K export for Pro users
- 🔐 Privacy-first: footage never uploaded without consent

## 🏗️ Architecture

```
MVVM + Clean Architecture + Multi-Module Gradle
├── :app                  — Entry point, navigation
├── :core:common          — Result, UseCase, extensions
├── :core:ui              — PocketStudiosTheme, design system
├── :core:database        — Room DB, DAOs, entities
├── :core:media           — ExportEngine, MediaCodec utils
├── :core:security        — EncryptedPrefs, integrity check
├── :feature:editor       — Multi-track editor (main feature)
├── :feature:gallery      — Project list screen
├── :feature:ai           — Auto-captions, BG removal
├── :feature:settings     — Settings, Pro upgrade
└── :backend:firebase     — Cloud Functions, rules
```

**Tech Stack:**
| Layer | Technology |
|-------|-----------|
| UI | Jetpack Compose + Material3 |
| Language | Kotlin 2.0 |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt (Dagger) |
| Database | Room 2.6 |
| Video Engine | Media3 ExoPlayer + Transformer |
| AI (Captions) | Whisper Tiny (ONNX Runtime) |
| AI (BG Remove) | ML Kit Selfie Segmentation |
| Backend | Firebase (Auth, Firestore, Storage, FCM) |
| Monetization | RevenueCat |
| CI/CD | GitLab CI + Firebase App Distribution |

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog 2023.1.1+ (or Ladybug)
- JDK 17
- Android SDK 35
- Firebase account (free tier works for development)

### 1. Clone the repo
```bash
git clone https://gitlab.com/your-username/pocket-studios-android.git
cd pocket-studios-android
```

### 2. Firebase Setup
1. Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com)
2. Add an Android app with package name `com.pocketstudios.app`
3. Download `google-services.json` and place it in `app/`
4. Enable Authentication (Email + Google)
5. Create Firestore database in your preferred region
6. Deploy security rules: `firebase deploy --only firestore:rules,storage:rules`

### 3. Configure Secrets (local development)
Create `local.properties` (already in `.gitignore`):
```properties
REVENUECAT_API_KEY=your_revenuecat_key_here
```

### 4. Build & Run
```bash
# Debug build (dev flavor)
./gradlew assembleDevDebug

# Install on connected device
./gradlew installDevDebug

# Run unit tests
./gradlew testDevDebugUnitTest

# Lint
./gradlew lint
```

### 5. Release Build
```bash
# Set environment variables
export KEYSTORE_PATH=/path/to/release.jks
export STORE_PASSWORD=your_store_password
export KEY_ALIAS=pocketstudios
export KEY_PASSWORD=your_key_password

# Build release AAB
./gradlew bundleProductionRelease
```

## 🤖 AI Features Setup

### Auto-Captions (Whisper ONNX)
1. Download `whisper_tiny.onnx` from HuggingFace (~39MB):
   ```
   https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-tiny.en.bin
   ```
2. Convert to ONNX format using `whisper-onnx` tool
3. Place in `app/src/main/assets/models/whisper_tiny.onnx`
4. Add ONNX Runtime dependency: `com.microsoft.onnxruntime:onnxruntime-android:1.18.0`

### Background Removal (ML Kit)
- Automatically downloaded on first use via Google Play Services
- No setup required — works offline after initial download (~5MB model)

## ☁️ Cloud Functions Deployment

```bash
cd backend/firebase/functions
npm install
npm run build
firebase deploy --only functions
```

## 🔑 GitLab CI/CD Setup

Add these CI/CD variables in GitLab → Settings → CI/CD → Variables:

| Variable | Description | Protected | Masked |
|----------|-------------|-----------|--------|
| `KEYSTORE_BASE64` | Base64-encoded release keystore | ✅ | ✅ |
| `STORE_PASSWORD` | Keystore store password | ✅ | ✅ |
| `KEY_ALIAS` | Key alias in keystore | ✅ | ❌ |
| `KEY_PASSWORD` | Key password | ✅ | ✅ |
| `REVENUECAT_API_KEY` | RevenueCat public API key | ✅ | ❌ |
| `FIREBASE_APP_ID` | Firebase App ID (for App Distribution) | ✅ | ❌ |
| `FIREBASE_SERVICE_CREDENTIALS` | Firebase service account JSON | ✅ | ✅ |
| `PLAY_STORE_SERVICE_ACCOUNT_JSON` | Play Store API service account JSON | ✅ | ✅ |

**Encode keystore for CI:**
```bash
base64 -i release.jks | pbcopy  # macOS
base64 release.jks | xclip      # Linux
```

## 📐 Project Conventions

- **Branches:** `main` (production), `develop` (integration), `feature/*`, `fix/*`
- **Commits:** Conventional Commits (`feat:`, `fix:`, `refactor:`, `test:`)
- **Code Style:** ktlint (run `./gradlew ktlintCheck`)
- **Architecture:** Domain entities only in domain layer — no Android imports
- **Tests:** Unit test every ViewModel and UseCase. UI test critical paths.

## 💰 Monetization

Managed via **RevenueCat** — handles cross-platform subscription management.

| Product ID | Type | Price |
|-----------|------|-------|
| `pocketstudios.pro.monthly` | Monthly subscription | $4.99/mo |
| `pocketstudios.pro.annual` | Annual subscription | $29.99/yr |
| `pocketstudios.pro.lifetime` | One-time purchase | $49.99 |

Free tier: Full editor, 1080p export, 50 filters, no watermark, no account required.

## 🏪 Play Store

- **Package:** `com.pocketstudios.app`
- **App Title:** Pocket Studios — Video Editor
- **Category:** Photography
- **Content Rating:** Everyone
- **Min Android:** 8.0 (API 26)

## 📝 License

Proprietary — All rights reserved. © 2024 Pocket Studios.

---

Built with ❤️ using Kotlin + Jetpack Compose
