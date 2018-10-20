package MyGameEngine;

import a3.MyGame;
import ray.rage.scene.*;
import ray.rage.scene.controllers.*;
import ray.rml.*;

public class ScaleController extends AbstractController
{
	private int i = 0;
	private float scaleRate = .05f;
	private float direction = -1.0f;
	private MyGame game;
	private SceneManager manager;
	private Node[] removeList = new Node[15]; 
	
	public ScaleController(MyGame g, SceneManager sm)
	{
		manager = sm;
		game = g;
	}
	protected void updateImpl(float elapsedTimeMillis)
	{
		float scaleAmt = 1.0f + direction * scaleRate;
		
		for (Node n : super.controlledNodesList)
		{
			/*Vector3 curScale = n.getLocalScale();
			if((game.getOrange(n) == true) || curScale.x() < 0.2f)
			{
				if(curScale.x() <= 0.01f)
				{		
					manager.destroySceneNode(n.getName());
					removeList[i] = n;
					i++;
					break;
				}
				else
				{
					curScale = Vector3f.createFrom(curScale.x()*scaleAmt, curScale.y()*scaleAmt, curScale.z()*scaleAmt);
					n.setLocalScale(curScale);
				}
			
			}*/
		}
		
		if(removeList[0] != null)
		{
			for(int j = 0; j < i; j++)
				super.removeNode(removeList[j]);
		}
		i = 0;
	}
}
