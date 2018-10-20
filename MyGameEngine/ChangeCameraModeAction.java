package MyGameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rml.*;
import net.java.games.input.Event;

public class ChangeCameraModeAction extends AbstractInputAction
{
	private Camera camera;
	private SceneNode dummy;
	private SceneNode dolphin;
	private SceneNode cameraNodeN;
	
	public ChangeCameraModeAction(SceneNode n, SceneNode d, SceneNode cn, Camera c)
	{
		dummy = n;
		dolphin = d;
		camera = c;
		cameraNodeN = cn;
	}

	public void performAction(float time, Event e)
	{
		if(dummy.getAttachedObjectCount() != 0){
			System.out.println("attaching to dolphin");
			dummy.detachObject(camera);
			cameraNodeN.attachObject(camera);
			dolphin.attachChild(dummy);
			camera.setMode('r');
		}
		else{
			System.out.println("detatching from dolphin");
			cameraNodeN.detachObject(camera);
			Vector3 saddlePosition = cameraNodeN.getWorldPosition();
			Vector3 saddleForward = cameraNodeN.getWorldForwardAxis();
			Vector3 saddleRotation = cameraNodeN.getWorldRightAxis();
			saddleRotation = saddleRotation.negate();
			camera.setFd((Vector3f)Vector3f.createFrom(saddleForward.x(), saddleForward.y(), saddleForward.z()));
			camera.setPo((Vector3f)Vector3f.createFrom(saddlePosition.x(), saddlePosition.y(), saddlePosition.z()));
			camera.setRt((Vector3f)Vector3f.createFrom(saddleRotation.x(), saddleRotation.y(), saddleRotation.z()));
			dummy.attachObject(camera);
			dolphin.detachChild(dummy);
			camera.setMode('c');
		}		
	}
}