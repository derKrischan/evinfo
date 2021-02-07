package org.capreolus.evinfo;

public class CameraStateChangedEvent {

	private CameraState cameraState;

	public CameraStateChangedEvent(CameraState cameraState) {
		this.cameraState = cameraState;
	}

	public CameraState getCameraState() {
		return cameraState;
	}
}
