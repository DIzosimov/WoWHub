# WoWHub

## Summary

WoWHub is a Guild Management Application for World of Warcraft — a finished Android app. It is a Gradle project with a single `app` module (source under `app/src/main/java/com/example/wowHub`, resources under `app/src/main/res`) and both unit and instrumented test folders.

## What it does

WoWHub is an Android companion app for World of Warcraft guilds. It has four
screens — chat, events, logs and roster — for keeping a guild's conversation,
schedule, activity history and member list in one place.

## Role

Creator and developer.

## Tech

- Android (Gradle Kotlin DSL: `build.gradle.kts`, `settings.gradle.kts`)
- Gradle wrapper (`gradlew`, `gradlew.bat`)
- Android Studio project (`.idea/` is included)

## Setup / Run

1. Install [Android Studio](https://developer.android.com/studio) (free) with an Android SDK.
2. Open the repository root folder in Android Studio and let Gradle sync (it reads `settings.gradle.kts` and `gradle.properties`).
3. Run the `app` configuration on an emulator or a USB-connected phone.

From the command line, in the repository root:

```bash
./gradlew assembleDebug
```

On Windows use `gradlew.bat assembleDebug`. The debug APK is written under `app/build/outputs/apk/debug/`.

Tests: `./gradlew test` for the unit tests in `app/src/test`.

## Status

Finished.
