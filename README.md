# Garbo Clock

Never forget trash day again! Garbo Clock reminds you when it's garbage day.
Configure your garbage and recycling schedule and when you wish to receive notifications.
Then Garbo Clock will notify you at your desired time when trash day arrives.

[![Build Status](https://travis-ci.org/cberes/garbage-android.svg?branch=master)](https://travis-ci.org/cberes/garbage-android)
[![Coverage Status](https://coveralls.io/repos/github/cberes/garbage-android/badge.svg?branch=master)](https://coveralls.io/github/cberes/garbage-android?branch=master)

## Get the app

You can find the app in the [Google Play Store](https://play.google.com/store/apps/details?id=com.spinthechoice.garbage.android).

## Icon

Thank you [Molly Beres](https://mollyillustration.com/) for the beautiful icon!

## Dependencies

The garbage schedule logic is implemented in the [garbage](//github.com/cberes/garbage) library.

## Package diagram

![package diagram](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.github.com/cberes/garbage-android/master/package-diagram.txt)

Additionally, the `text` and `adapters` packages are meant not to reference any other packages.
The `mixins` package is meant to be used by activities only.
