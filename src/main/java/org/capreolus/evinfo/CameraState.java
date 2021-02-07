package org.capreolus.evinfo;

public enum CameraState {
	ACTIVE("fas fa-skull"),
	DISABLED("fas fa-check-circle"),
	BEER("fas fa-beer"),
	UNKNOWN("fas fa-question-circle");

	private String iconName;

	CameraState(String iconName) {
		this.iconName = iconName;
	}

	public String getIconName() {
		return iconName;
	}
}