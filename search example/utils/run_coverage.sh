#!/bin/bash

# Must be run with ant:
# http://blog.pboos.ch/2011/06/coverage-fo-android-tests/comment-page-1/#comment-2512

# Adb must run as root to be able to copy instrumentation data from /data partition
adb root

# Run
cd tests
ant coverage

# Show
chromium-browser coverage/coverage.html &

# Clean up
rm -r bin
rm -r gen
rm -r instrumented
cd ..
rm -r bin
rm -r gen
