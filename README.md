# Crosswalk performance comparison

This project contains test applications to compare the performance of
two mobile application frameworks:

* HTML5 stack with the Chromium-based Crosswalk runtime and the Polymer
  material design toolkit

* native Android apps using the Java language and Android's standard
  UI libraries

See <https://github.com/smcv/xw-perf/wiki> for more information.

## Building

The build system expects Linux; I used Debian 8.

### Setup

* `sudo apt-get install ant build-essential cssmin git npm python uglifyjs zip`
  (or the equivalent for non-Debian)
* Unpack the Android SDK to ~/android-sdk\_r24.0.1-linux/
* Run `~/android-sdk\_r24.0.1-linux/tools/android sdk`
  (requires X11 GUI or display forwarding via e.g. `xpra`)
* Install the latest Android SDK Tools (I used revision 21),
  Android SDK Platform-tools (I used revision 21),
  Android SDK Build-tools (I used revision 21.1.2),
* Install Android 5.0.1 (API 21) SDK Platform (I used revision 2)
* Install the latest Android Support Repository (I used revision 10)
  and Android Support Library (I used revision 21.0.3)
* Run `make init`

### Build

* Run `make`
