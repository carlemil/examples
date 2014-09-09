#!/bin/bash
##########################################################
#
# customize_script.sh
#
# Change values in this file to customize your monkey run
#
##########################################################

# Number of times to run Monkey
export NBRRUNS=5

# Path to log dir (output dir)
export LOG_DIR=reports/monkey

# Target device
export ANDROID_SERIAL=$(adb devices|head -n 2|tail -n 1|cut -f 1)

# Target application
export TARGET_PACKAGE=com.sonymobile.sonyselect

# Adb binary (if not in your PATH)
export ADB=$(which adb)

# Number of events to run
export EVENTS=10000

# The throttle to use
export THROTTLE=100

# The seed
export SEED=0

######################################################
# Static values
# DONT CHANGE!
######################################################
export TOUCH=30
export MOTION=30
export TRACKBALL=0
export NAV=0
export MAJORNAV=20
export APPSWITCH=10
export ANYEVENT=10

