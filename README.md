Evinfo
======

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [Introduction](#introduction)
- [Running the application in dev mode](#running-the-application-in-dev-mode)
- [Packaging and running the application](#packaging-and-running-the-application)
- [Creating a native executable](#creating-a-native-executable)
  - [Native file optimization](#native-file-optimization)
  - [macOS app creation](#macos-app-creation)
  - [macOS autostart service](#macos-autostart-service)
- [The client side](#the-client-side)
- [Developer information](#developer-information)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Introduction

Little helper tool for the home office. The tool uses quarkus and is designed for the Macbook Pro my company gave me.
It watches the Apple unified logging system for camera events and uses a websocket to signal the current camera state to clients.
This way my wife (Evi) can see whether I'm in a video chat before entering the room.
When the camera is still running and the local time is after a certain threshold (e.g. 5:30pm) the icon changes into a beer glass icon indicating that the camera is still running but my collegues and me have switched over to beer time.
It may be compiled into a native binary and is started at my machine at login time.

## Running the application in dev mode

You can run the application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `evinfo-1.0.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.
You may start this application calling `java -jar target/quarkus-app/quarkus-run.jar`

If you want to build an _über-jar_, execute
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```
... or add the property to application.properties.

The uber-jar application is runnable using `java -jar target/evinfo-1.0.0-SNAPSHOT-runner.jar`.

## Creating a native executable

The native executable is created using the GraalVM. You may install it using [SDKMAN](https://sdkman.io/).

You can create a native executable using:
```shell script
./mvnw -Pnative package
```

Building inside a container makes no sense for this project because it is speciazed for macos (log mechanism) and there is no container build available for macos.

The binary may be executed calling: `./target/evinfo-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.html.

### Native file optimization

The created native binary may be shrinked to approx 30% of its size using [upx](https://upx.github.io/) in a post processing step:

```shell script
upx --best -k target/evinfo-1.0.0-SNAPSHOT-runner
```

The option `-k` or `--keep` keeps the original file.

### macOS app creation

You can transform the binary into a macOS app using tools like [Appify](https://github.com/machinebox/appify). Install it as instructed - the executable is usually located in `$GOPATH` after `go get` execution or in `~/go/bin` if `GOPATH` is not set - and execute it (e.g. with the default icon provided here):

```shell script
$GOPATH/appify -name "Evinfo" -icon evinfo_icon.png target/evinfo-1.0.0-SNAPSHOT-runner
```

### macOS autostart service

In order to autostart the application in background as a service on macOS copy the app to your Application folder. Start the app once manually to allow incoming network connections for Evinfo.
One possibility is to right click the app icon in dock and choose "Options -> Open at Login". You can also create a plist file:

```shell script
vim ~/Library/LaunchAgents/org.capreolus.evinfo.plist
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
        <key>Label</key>
        <string>org.capreolus.Evinfo</string>
        <key>ProgramArguments</key>
        <array>
                <string>/Applications/Evinfo.app/Contents/MacOS/Evinfo</string>
        </array>
        <key>RunAtLoad</key>
        <true/>
        <key>StandardErrorPath</key>
        <string>/dev/null</string>
        <key>StandardOutPath</key>
        <string>/dev/null</string>
</dict>
</plist>
```

If you did not create a macOS app from the binary and just copied it to your Applications folder, the plist-file will look more like this:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
        <key>Label</key>
        <string>org.capreolus.evinfo</string>
        <key>ProgramArguments</key>
        <array>
                <string>/Applications/evinfo</string>
        </array>
        <key>RunAtLoad</key>
        <true/>
        <key>StandardErrorPath</key>
        <string>/dev/null</string>
        <key>StandardOutPath</key>
        <string>/dev/null</string>
</dict>
</plist>
```

## The client side

I use an old Kobo Glo device as client mounted via magnets at my office door frame as a client.
The Kobo got a Nickel Menu (https://pgaskin.net/NickelMenu) extension and a simple browser based menu entry for the camera status (created file `.adds/nm/camera_status`):
```
menu_item:main:Camera Status:nickel_browser:modal:http://my.ip.add:36983
```

It is still possible to use the Kobo reader for it's main purpose but it also a dynamic dislplay panel for the camera status in my office.

## Developer information

This project uses (just out of curiosity) the [pre-commit](https://pre-commit.com/) framework and tries to follow the [conventional commits](https://www.conventionalcommits.org/en/v1.0.0/) specification using [commitizen](https://commitizen.github.io/cz-cli/).
