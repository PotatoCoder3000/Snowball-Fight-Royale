package MyGameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rml.*;
import net.java.games.input.Event;

public class RotateRightAction extends AbstractInputAction
{
	private SceneNode dolphin;	
	
	public RotateRightAction(SceneNode d)
	{
		dolphin = d;		
	}

	public void performAction(float time, Event e)
	{
		Angle rotAmt = Degreef.createFrom(-3.0f);
       	dolphin.yaw(rotAmt);
	}
}