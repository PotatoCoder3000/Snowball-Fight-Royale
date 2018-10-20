package MyGameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import net.java.games.input.Event;

public class MoveLeftAction extends AbstractInputAction
{
	private SceneNode dolphin;
	
	public MoveLeftAction(SceneNode d)
	{ 
		dolphin = d;	
	}

	public void performAction(float time, Event e)
	{
		dolphin.moveLeft(-0.10f);	
	}	 
		
	
}