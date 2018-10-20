package MyGameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import a3.ProtocolClient;
import net.java.games.input.Event;

public class MoveBackwardsAction extends AbstractInputAction
{
	private Node dolphin;
	private ProtocolClient protClient;
	
	public MoveBackwardsAction(Node d, ProtocolClient p)
	{ 
		dolphin = d;
		protClient = p;
	}

	public void performAction(float time, Event e)
    {
		dolphin.moveBackward(0.10f);
		protClient.sendMoveMessage(dolphin.getWorldPosition());
    }
}