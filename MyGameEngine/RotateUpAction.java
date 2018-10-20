package MyGameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rml.*;
import net.java.games.input.Event;

public class RotateUpAction extends AbstractInputAction
{
	private Camera camera;
	private SceneNode dummy;
	private SceneNode dolphin;

	public RotateUpAction(SceneNode n, SceneNode d, Camera c)
	{
		dummy = n;
		dolphin = d;
		camera = c;
	}

	public void performAction(float time, Event e)
	{
		if(dummy.getAttachedObjectCount() != 0)
		{
			Angle rotAmt = Degreef.createFrom(5.0f);
			Vector3 f = camera.getFd();
			Vector3 r = camera.getRt();
			Vector3 u = camera.getUp();
       		Vector3 fn = (f.rotate(rotAmt, r)).normalize();
       		Vector3 un = (u.rotate(rotAmt, r)).normalize();
       		camera.setFd((Vector3f)Vector3f.createFrom(fn.x(), fn.y(), fn.z()));
       		camera.setUp((Vector3f)Vector3f.createFrom(un.x(), un.y(), un.z()));
		}
		else
		{
			Angle rotAmt = Degreef.createFrom(5.0f);
       		dolphin.pitch(rotAmt);
		}		
	}
}