BeaconControl Android Sample Application
=======================

## Overview

This is a sample Android application that presents the capabilities of [BeaconControl Android libarary](https://github.com/upnext/BeaconControl_Android_SDK).

By default it communicates with publicly accessible BeaconControl server instance located at https://beaconcontrol.io.

Application can be downloaded from Google Play Store [here](TODO provide link).

## Usage

### Login

To use the application you need to have an account at https://beaconcontrol.io as you have to provide your credentials on the first application launch.
The cause for this is that the configuration of your test application (listed [here](https://admin.beaconcontrol.io/applications) as `App (Test)`) needs to be fetched.

Once logged in, the configuration is fetched and beacons monitoring starts (beacons are monitored even if the application is in the background).
To stop beacons monitoring, use the _Logout_ button located on the _Info_ tab of the application.

### Beacons tab

This tab shows a list of all beacons fetched from the backend sorted by distance.
Each row contains a beacon name, UUID and range (with additional distance to beacon if available).
If beacon is out of range, the text will be greyed-out.

Additionally at the top there is a _Reload configuration_ button which can be used to fetch new beacons if the configuration at the backend was changed.

### Actions tab

This tab shows a list of actions that have happened during the lifetime of the application (across a single login, after logout the actions are cleared).
The actions are defined on the BeaconControl server in the `App (Test)` configuration (eg. URL actions, Custom actions, Coupon actions).

### Info tab

This tab provides simple application information along with the `Logout` button which can be used to log out of the application (this stops beacons monitoring and allows the user to log in to another account).

## For developers

The code present in this repository should be self-explaining.
The most interesting parts showing how to integrate the BeaconControl Android library is present in [`MainActivity.java`](TODO provide link).
At the same time please be aware that all the code present in package `io.upnext.beaconcontrol.app.s2s` does not have anything to do with BeaconControl SDK library, its purpose it the ease of using the appliction (via login to your account) without having to provide the application client_id and client_secret.

## License

License can be found in LICENSE.txt file.

If you have any troubles, please contact us at feedback@beaconcontrol.io.