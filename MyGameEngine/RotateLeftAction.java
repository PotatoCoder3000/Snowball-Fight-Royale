package MyGameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rml.*;
import net.java.games.input.Event;

public class RotateLeftAction extends AbstractInputAction
{
	private SceneNode dolphin;	

	public RotateLeftAction(SceneNode d)
	{
		dolphin = d;
	}	

	public void performAction(float time, Event e)
	{
		Angle rotAmt = Degreef.createFrom(3.0f);
		dolphin.yaw(rotAmt);			
	}
}