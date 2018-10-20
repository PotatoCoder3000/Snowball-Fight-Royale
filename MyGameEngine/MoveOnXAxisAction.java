package MyGameEngine;

import ray.input.action.AbstractInputAction;
import a3.ProtocolClient;
import net.java.games.input.Event;
import ray.rage.scene.Node;

public class MoveOnXAxisAction extends AbstractInputAction
{
	private Node dolphin;
	private ProtocolClient protClient;
	
	public MoveOnXAxisAction(Node d, ProtocolClient p) {
		dolphin = d;
		protClient = p;
	}

	public void performAction(float time, Event e)
	{
		if (e.getValue() < -0.5){
			dolphin.moveLeft(-0.10f);
		}
		else if (e.getValue() > 0.5) {
			dolphin.moveRight(-0.10f);
		}
		protClient.sendMoveMessage(dolphin.getWorldPosition());
	}
}
