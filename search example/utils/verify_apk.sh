#!/bin/bash

if [ "$#" != "1" ]; then
  echo -e "\nError, Wrong input params!"
  exit 1
fi

echo -e "\nVerifying $1"

# Check signing
echo -e "\n---------- signing ----------\n"
jarsigner -verify -verbose -certs $1

# Check zipalignment
echo -e "\n---------- zip alignment ----------\n"
zipalign -c -v 4 $1

# Check proguarding
echo -e "\n---------- proguard ----------\n"
echo "Nothing yet..."
