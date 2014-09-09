#!/bin/bash
echo ""
echo "=================================="
echo "             Jayway               "
echo "                                  "
echo "      Monkey exerciser v0.1       "
echo "                                  "
echo "=================================="

SCRIPT_DIR=`dirname "$0"`
source $SCRIPT_DIR/customize_monkey.sh

echo -e "\n$(date)"

TIME_STAMP=$1

echo ""
echo "----------------------------------"
echo "Parameters used in this run:"
echo "Log folder path:          $LOG_DIR"
echo "Device id:                $ANDROID_SERIAL"
echo "Application name:         $TARGET_PACKAGE"
echo "Adb path:                 $ADB"
echo "Number of events:         $EVENTS"
echo "Throttle:                 $THROTTLE"
echo "Seed:                     $SEED"
echo "Timestamp:                $TIME_STAMP"
echo "----------------------------------"
echo ""

# Wait until device is ready
# timeout command does not work on Mac
#timeout 120 $ADB -s $ANDROID_SERIAL wait-for-device

$ADB -s $ANDROID_SERIAL wait-for-device
if [[ "${?:-1}" -ne 0 ]]; then
  echo "Error: No device found after waiting 60 seconds. Exiting..."
  exit 1
fi

echo -e "Device $ANDROID_SERIAL found, continuing...\n"

echo -e "Preparing to run Monkey...\n"

# Create output dir
OUT_DIR=$LOG_DIR/$TIME_STAMP
if [[ -d "$OUT_DIR" ]]; then
  rm -rf $OUT_DIR
fi
mkdir -p $OUT_DIR

# Erase data from logcat
$ADB -s $ANDROID_SERIAL logcat -c

# Record test start time
echo "Start of Monkey test: $(date +'%Y-%m-%d %H:%M:%S')" > $OUT_DIR/test_time.log
echo "Start of Monkey test: $(date +%Y%m%d_%H%M%S)"

$ADB -s $ANDROID_SERIAL shell monkey -p $TARGET_PACKAGE --pct-touch $TOUCH --pct-motion $MOTION --pct-trackball $TRACKBALL --pct-nav $NAV --pct-majornav $MAJORNAV --pct-appswitch $APPSWITCH --pct-anyevent $ANYEVENT -s $SEED -v --throttle $THROTTLE $EVENTS > $OUT_DIR/screen_log.log

# Record test end time
echo "End of Monkey test: $(date +'%Y-%m-%d %H:%M:%S')" >> $OUT_DIR/test_time.log
echo "End of Monkey test: $(date +%Y%m%d_%H%M%S)"

# Log test results
echo -e "\nFetching logs from device. Please wait..."
$ADB -s $ANDROID_SERIAL shell dumpsys > $OUT_DIR/system.log
$ADB -s $ANDROID_SERIAL shell logcat -d -v threadtime ActivityManager:V $TARGET_PACKAGE:V > $OUT_DIR/logcat.log
$ADB -s $ANDROID_SERIAL shell logcat -d -v threadtime -b events ActivityManager:V $TARGET_PACKAGE:V > $OUT_DIR/logcat_events.log
$ADB -s $ANDROID_SERIAL shell bugreport > $OUT_DIR/bugreport.log

# End run
echo -e "\nMonkey run finished!"
echo -e "Logs can be found in $OUT_DIR\n"

echo "=================================="

exit 0
