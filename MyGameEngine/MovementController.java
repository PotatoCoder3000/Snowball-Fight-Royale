package MyGameEngine;

import java.util.Random;

import ray.rage.scene.*;
import ray.rage.scene.controllers.*;
import ray.rml.*;

public class MovementController extends AbstractController
{
	private float cycleTime;
	private float totalTime = 0.0f;
	private Random randNum = new Random();
	private float direction;
	
	public MovementController()
	{
		cycleTime =  randNum.nextInt(500) + 2000;
		direction = randNum.nextFloat() - 0.5f;
	}
	
	protected void updateImpl(float elaspedTimeMillis)
	{
		totalTime += elaspedTimeMillis;
		if(totalTime > cycleTime)
		{
			direction = -direction;
			totalTime = 0.0f;
		}
		
		for (Node n : super.controlledNodesList)
		{ 
			n.translate(direction, 0.0f, direction);
		}
	}
}
