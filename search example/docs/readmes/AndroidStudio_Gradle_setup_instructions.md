# Setup instructions
This document will explain how to set up a working environment for Sony Select with Gradle and Android Studio for Somc Ubuntu.

## Prerequisites
Somc Ubuntu.
Repo installed.

## Get Somc Android SDK
`sdkdownload -b <branch>` (branch could be 'kk-rhine')
(See Sony Select wiki: https://wiki.sonyericsson.net/androiki/Sony_Select#Get_somc_SDK)

type `which android` to see what sdk that are on your path.

### SDK proxy settings
Start sdk manager by typing: `android`
Choose tools -> options and enter:
HTTP Proxy server: proxy.seld.sonyericsson.net
HTTP Proxy port: 8080

## Get src
`repo sync platform/vendor/semc/packages/apps/sony-select-client -j5`
(See Sony Select wiki: https://wiki.sonyericsson.net/androiki/Sony_Select#Get_the_code_.26_modify_it)

## Gradle
The project contains a gradle wrapper (gradlew) that is a script that downloads the correct version of gradle if it's not already downloaded.

1. Goto project root
2. run `./gradlew wrapper`
3. run `./gradlew assembleDebug`

Note! You should have a file (local.properties) in the project root pointing out which android sdk you should use.
Example (file content): sdk.dir=/home/CORPUSERS/xp013831/sdk/latest

## Android studio
1. Download the latest Canary build (Version 0.51 as of 2014-03-11).
   http://tools.android.com/download/studio/canary/latest
2. Start from bin-folder with terminal: `./studio.sh`
3. Goto Settings -> HTTP Proxy
   Select 'Manual Proxy settings'
   Host name: proxy.seld.sonyericsson.net
   Port number: 8080
   Select 'Proxy authentication'
   Login: <your username>
   Password: <your password>
4. import project

