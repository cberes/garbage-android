# Garbo Clock

Never forget trash day again! Garbo Clock reminds you when it's garbage day.
Configure your garbage and recycling schedule and when you wish to receive notifications.
Then Garbo Clock will notify you at your desired time when trash day arrives.

## Get the app

You can find the app in the [Google Play Store](https://play.google.com/store/apps/details?id=com.spinthechoice.garbage.android).

## Icon

Thank you [Molly Beres](https://mollyillustration.com/) for the beautiful icon!

## Dependencies

The garbage schedule logic is implemented in the [garbage](../garbage) library.

## Package diagram

![package diagram](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.github.com/cberes/garbage-android/master/package-diagram.txt)

Additionally, the `text` and `adapters` packages are meant not to reference any other packages.
The `mixins` package is meant to be used by activities only.
