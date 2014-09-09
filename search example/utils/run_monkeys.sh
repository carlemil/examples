#!/bin/bash
echo ""
echo "##################################"
echo "             Jayway               "
echo "                                  "
echo "  Multiple Monkey exerciser v0.1  "
echo "                                  "
echo "##################################"

source customize_monkey.sh

echo -e "\nRunning $NBRRUNS Monkey runs..."

# Run command
for i in `seq 1 $NBRRUNS`; do
  echo -e "\nMonkey run $i started!"
  echo -e "Please wait, restarting device..."
  $ADB reboot
  # Sleep for 2 minutes to make sure device restarts properly
  sleep 120
  export NOW=$(date +%Y%m%d_%H%M%S)
  echo -e "Monkey run $i starting monkey tests!"
  ./run_monkey.sh $NOW
  echo -e "\nMonkey run $i monkey tests finished!"
done

echo -e "\nAll $NBRRUNS runs finished!\n"

echo "##################################"

exit 0
