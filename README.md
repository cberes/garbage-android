# Garbo Clock

Never forget trash day again! Garbo Clock reminds you when it's garbage day.
Configure your garbage and recycling schedule and when you wish to receive notifications.
Then Garbo Clock will notify you at your desired time when trash day arrives.

## Project moved

The project has moved to [Codeberg](https://codeberg.org/cberes/garbage).

## Get the app

<a href="https://codeberg.org/cberes/garbage/releases">
    <img alt="Get it on Codeberg" src="https://get-it-on.codeberg.org/get-it-on-white-on-black.png" height="60">
</a>

## Icon

Thank you [Molly Beres](https://mollyillustration.com/) for the beautiful icon!

## Dependencies

The garbage schedule logic is implemented in the [garbage](//github.com/cberes/garbage) library.

## Package diagram

![package diagram](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.github.com/cberes/garbage-android/master/package-diagram.txt)

Additionally, the `text` and `adapters` packages are meant not to reference any other packages.
The `mixins` package is meant to be used by activities only.
