#!/bin/bash

adb root
echo Waiting 5 seconds
sleep 5

adb remount
echo Waiting 5 seconds
sleep 5

adb shell rm /system/app/StoreFront.apk
adb shell rm /system/app/com.sonyericsson.androidapp.storefront.apk
adb shell rm /system/app/com.sony.androidapp.recommender.apk
adb shell rm /system/app/com.sony.android.recommender.apk
adb shell rm /system/app/storefront.apk
adb shell rm /system/app/getgames.apk
adb shell rm /system/app/GetGames.apk
adb shell rm /system/app/GamesStartActivity.apk
adb shell rm /system/app/storefront-android.apk
adb shell rm /system/app/storefront-android-it.apk
adb shell rm /system/app/storefront-it.apk
adb shell rm /system/app/storefront-signed-aligned.apk
adb shell rm /system/app/storefront_debug.apk
adb shell rm /system/app/storefront-it_debug.apk


sleep 1

adb uninstall com.sonyericsson.androidapp.storefront.test
adb uninstall com.sonyericsson.androidapp.storefront
adb uninstall com.sony.androidapp.recommender.test
adb uninstall com.sony.androidapp.recommender
adb uninstall com.sony.android.recommender.test
adb uninstall com.sony.android.recommender
adb uninstall com.sonymobile.sonyselect.test
adb uninstall com.sonymobile.sonyselect

echo Done uninstalling
