# LockCountdown

Android widget counting down to May 17, 2027, 4:30 PM Central.

## Install
1. Unzip, then in Android Studio: File > Open > pick the `LockCountdown` folder. Let Gradle sync (accept any suggested AGP upgrade).
2. On the phone: Settings > About phone > tap Build number 7 times, then turn on USB debugging in Developer options.
3. Plug the phone into the Mac, pick it in the device dropdown, press Run.

## Put it on the lock screen
- Pixel (Android 16 QPR2+): Settings > Display & touch > Widgets on lock screen. Swipe left on the lock screen, tap add, pick Countdown.
- Samsung (One UI 8+): long-press the lock screen > Widgets > look for Countdown.

## Change the date
Edit `app/src/main/java/com/jonathan/lockcountdown/Countdown.kt`.
