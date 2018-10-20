package MyGameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import a3.MyGame;
import a3.ProtocolClient;
import net.java.games.input.Event;

public class MoveForwardAction extends AbstractInputAction
{
	private Node dolphin;
	private ProtocolClient protClient;
	private MyGame game;
	
	public MoveForwardAction(Node d, ProtocolClient p, MyGame g)
	{ 
		dolphin = d;
		protClient = p;
		game = g;
	}

	public void performAction(float time, Event e)
    {
		dolphin.moveForward(0.10f);
		protClient.sendMoveMessage(dolphin.getWorldPosition());
    }
}