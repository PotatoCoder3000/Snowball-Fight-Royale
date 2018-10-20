package MyGameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.Node;
import a3.MyGame;
import a3.ProtocolClient;
import net.java.games.input.Event;

public class MoveOnYAxisAction extends AbstractInputAction
{
	private Node dolphin;
	private MyGame game;
	private ProtocolClient protClient;
	public MoveOnYAxisAction(Node d, MyGame g, ProtocolClient p) {
		dolphin = d;
		game = g;
		protClient = p;
	}

	public void performAction(float time, Event e)
	{
		if (e.getValue() < -0.5){
			dolphin.moveForward(0.10f);
		}
		else if (e.getValue() > 0.5){
			dolphin.moveBackward(0.10f);
		}
		else {
			game.walkAction();
		}
		protClient.sendMoveMessage(dolphin.getWorldPosition());
	}
}
