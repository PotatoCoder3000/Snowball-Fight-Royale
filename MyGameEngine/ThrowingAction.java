package MyGameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.Node;
import a3.MyGame;
import net.java.games.input.Event;

public class ThrowingAction extends AbstractInputAction
{
	private Node dolphin;
	private MyGame game;
	public ThrowingAction(Node d, MyGame g) {
		dolphin = d;
		game = g;
	}

	public void performAction(float time, Event e)
	{
		System.out.println("Hello");	
		System.out.println("World");
		game.throwingAction();
	}
}

