package MyGameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import net.java.games.input.Event;

public class MoveRightAction extends AbstractInputAction
{
	private SceneNode dolphin;
	
	public MoveRightAction(SceneNode d)
	{ 
		dolphin = d;		
	}

	public void performAction(float time, Event e)
	{
		dolphin.moveRight(-0.10f);
		
	}
}