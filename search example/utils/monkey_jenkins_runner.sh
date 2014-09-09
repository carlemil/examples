#!/bin/bash
echo ""
echo "#################################"
echo "           Jayway                "
echo "                                 "
echo "  Monkey exerciser for Jenkins   "
echo "                                 "
echo "#################################"


echo -e "\nMonkey run started!"
echo -e "Please wait, restarting device..."
$ADB reboot
# Sleep to make sure device restarts properly
sleep 90
export NOW=$(date +%Y%m%d_%H%M%S)
echo -e "Starting monkey tests!"
./run_monkey.sh $NOW
echo -e "Monkey tests finished! Outputs: $LOG_DIR/$NOW"

if grep -e "$LOGCAT_SEARCH_TXT" $LOG_DIR/$NOW/logcat.log; then
  echo -e "\nError found in logcat. "
  echo -e "************* Start Clip! clip! from logcat ****************"
  grep -B8 -A20 "$LOGCAT_SEARCH_TXT" $LOG_DIR/$NOW/logcat.log
  echo -e "************* Finish Clip from logcat **********************\n"
  echo "##################################"
  exit 1 ;
else
  echo "No error found in logcat."
fi ;

echo "##################################"

exit 0