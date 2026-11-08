# One Tap Alarm (Android APK)

A lightweight Android app with one-tap preset alarm buttons. Each tap sends an `ACTION_SET_ALARM` request to the phone's Clock app (Samsung Clock, Google Clock, etc.) with the selected hour/minute and requests that the Clock app skip its confirmation UI (`EXTRA_SKIP_UI = true`).

## Presets Included
- 5:00 AM, 5:30 AM
- 6:00 AM, 6:30 AM
- 7:00 AM, 7:30 AM
- 8:00 AM, 8:30 AM
- 9:00 AM, 9:30 AM
- Custom time picker

## How to Build the APK

### Option 1: Instant Cloud Build via GitHub (Free & No Software Needed)
1. Push this folder to a GitHub repository.
2. Click the **"Actions"** tab on GitHub.
3. The included `.github/workflows/build-apk.yml` runs automatically.
4. Download the ready-to-install `OneTapAlarm-debug-apk` artifact in 2 minutes!

### Option 2: Local Build in Android Studio
1. Open this folder in Android Studio.
2. Select **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
3. The APK will be produced at:
   `app/build/outputs/apk/debug/app-debug.apk`
4. Transfer it to your Samsung Galaxy M11 (via USB, QuickShare, or Google Drive) and tap to install!

## Permissions
- `com.android.alarm.permission.SET_ALARM` (Standard official Android Alarm Clock API)
- No internet connection or background tracking required.
