#!/bin/bash

adb root
sleep 4

adb remount
sleep 4

adb push sacco_settings/ /system/etc/customization/settings/com/sonymobile/sonyselect
