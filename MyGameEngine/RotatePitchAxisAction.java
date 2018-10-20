package MyGameEngine;

import ray.input.action.AbstractInputAction;
import ray.input.action.Action;
import net.java.games.input.Event;

public class RotatePitchAxisAction extends AbstractInputAction
{
	private RotateUpAction up;
	private RotateDownAction down;
	
	public RotatePitchAxisAction(Action u, Action d) {
		up = (RotateUpAction) u;
		down = (RotateDownAction) d;
	}

	public void performAction(float time, Event e)
	{
		if (e.getValue() < -0.5){
			up.performAction(time, e);
		}
		else if (e.getValue() > 0.5)
			down.performAction(time, e);
	}	
}
