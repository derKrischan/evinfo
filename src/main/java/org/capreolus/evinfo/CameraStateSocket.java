package org.capreolus.evinfo;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.jboss.logging.Logger;

@ServerEndpoint(value = "/camerastate")
@ApplicationScoped
public class CameraStateSocket {

	private static final Logger log = Logger.getLogger(CameraStateSocket.class);

	private Set<Session> sessions = new HashSet<>();

	@Inject
	CameraStateWatcher cameraStateWatcher;

	@OnOpen
	public void onOpen(Session session) {
		sessions.add(session);
		log.debug("Session opened: '" + session.getId() + "'.");
		broadcastCurrentCameraState();
	}

	@OnClose
	public void onClose(Session session) {
		sessions.remove(session);
		log.debug("Session closed: '" + session.getId() + "'.");
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		sessions.remove(session);
		log.warn("Error in session: '" + session.getId() + "' with cause: " + throwable.getMessage());
	}

	private void broadcastCurrentCameraState() {
		broadcastCameraState(cameraStateWatcher.getCurrentCameraState());
	}

	private void broadcastCameraState(CameraState cameraState) {
		StringBuffer response = new StringBuffer();
		response.append(cameraState.getIconName());
		sessions.forEach(s -> {
			s.getAsyncRemote().sendText(response.toString(), result -> {
				if (result.getException() != null) {
					log.warn("Unable to send camera state: " + result.getException());
				}
			});
		});
	}

	public void cameraStateChanged(@Observes CameraStateChangedEvent cameraStateEvent) {
		broadcastCameraState(cameraStateEvent.getCameraState());

	}
}
