package MyGameEngine;

import ray.input.action.AbstractInputAction;
import ray.input.action.Action;
import ray.rage.scene.SceneNode;
import net.java.games.input.Event;

public class RotateYawAxisAction extends AbstractInputAction
{
	private RotateLeftAction left;
	private RotateRightAction right;
	
	private SceneNode dolphin;
	
	public RotateYawAxisAction(Action l, Action r) 
	{
		right = (RotateRightAction) r;
		left = (RotateLeftAction) l;
		//dolphin = d;
	}

	public void performAction(float time, Event e)
	{
		if (e.getValue() < -0.5){
			left.performAction(time, e);
		}
		else if (e.getValue() > 0.5)
			right.performAction(time, e);
	}		
}
