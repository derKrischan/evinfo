EVInfo
======

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [Introduction](#introduction)
- [Running the application in dev mode](#running-the-application-in-dev-mode)
- [Packaging and running the application](#packaging-and-running-the-application)
- [Creating a native executable](#creating-a-native-executable)
- [The client side](#the-client-side)

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

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application is now runnable using `java -jar target/evinfo-1.0.0-SNAPSHOT-runner.jar`.

## Creating a native executable

You can create a native executable using:
```shell script
./mvnw -Pnative package
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/evinfo-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.html.

## The client side

I use an old Kobo Glo device as client mounted via magnets at my office door frame as a client.
The Kobo got a Nickel Menu (https://pgaskin.net/NickelMenu) extension and a simple browser based menu entry for the camera status (created file `.adds/nm/camera_status`):
```
menu_item:main:Camera Status:nickel_browser:modal:http://my.ip.add:36983
```

It is still possible to use the Kobo reader for it's main purpose but it also a dynamic dislplay panel for the camera status in my office.
