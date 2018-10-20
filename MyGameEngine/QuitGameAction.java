package MyGameEngine;

import a3.MyGame;
import ray.input.action.AbstractInputAction;
import net.java.games.input.Event;
public class QuitGameAction extends AbstractInputAction
{
	private MyGame game;
	public QuitGameAction(MyGame g){ 
		game = g;
	}

	public void performAction(float time, Event event)
	{ 
		System.out.println("shutdown requested");
		game.shutdown();
	}
}