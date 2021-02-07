package org.capreolus.evinfo;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Scanner;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.Startup;

@Startup
@ApplicationScoped
public class CameraStateWatcher {

	private static final Logger log = Logger.getLogger(CameraStateWatcher.class);

	private CameraState currentCameraState = CameraState.UNKNOWN;

	@ConfigProperty(name = "evinfo.beertime")
	String beerTime;

	@Inject
	Event<CameraStateChangedEvent> cameraStateChangedEvent;

	public CameraStateWatcher() {
		init();
	}

	private void init() {
		Process logStreamer;
		try {
			logStreamer = new ProcessBuilder("/usr/bin/log", "stream").start();
		} catch (IOException e) {
			log.fatal("Unable to watch Apple unified log stream for camera events.", e);
			return;
		}

		Executors.newSingleThreadExecutor().submit(() -> {
			try (Scanner scanner = new Scanner(logStreamer.getInputStream())) {
				while (true) {
					String logLine = scanner.useDelimiter(System.lineSeparator()).next();
					if (logLine.contains("Post event kCameraStreamStart")) {
						log.debug("Camera started");
						if (LocalTime.now().isAfter(LocalTime.parse(beerTime))) {
							currentCameraState = CameraState.BEER;
							cameraStateChangedEvent.fire(new CameraStateChangedEvent(CameraState.BEER));
						} else {
							currentCameraState = CameraState.ACTIVE;
							cameraStateChangedEvent.fire(new CameraStateChangedEvent(CameraState.ACTIVE));
						}
					}
					if (logLine.contains("Post event kCameraStreamStop")) {
						log.debug("Camera stopped");
						currentCameraState = CameraState.DISABLED;
						cameraStateChangedEvent.fire(new CameraStateChangedEvent(CameraState.DISABLED));
					}
				}
			}
		});
	}

	void onStop(@Observes ShutdownEvent ev) {
		currentCameraState = CameraState.UNKNOWN;
		cameraStateChangedEvent.fire(new CameraStateChangedEvent(CameraState.UNKNOWN));
	}

	public CameraState getCurrentCameraState() {
		return currentCameraState;
	}
}
