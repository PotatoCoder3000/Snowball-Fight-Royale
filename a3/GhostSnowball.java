package a3;

import java.util.UUID;

import ray.rage.scene.Entity;
import ray.rage.scene.SceneNode;
import ray.rml.Matrix3;
import ray.rml.Vector3;

public class GhostSnowball
{ 
	private int id;
	private SceneNode node;
	private Entity entity;
	private Vector3 position;
	private Matrix3 direction;
	
	public GhostSnowball(int id, Vector3 pos) // constructor
	{ 
		this.id = id;
		position = pos;
	}
	
	public void setPosition(Vector3 position)
	{ 
		node.setLocalPosition(position);
	}
	
	public void setDirection(Matrix3 direction)
	{
		node.setLocalRotation(direction);
	}
	
	public Matrix3 getDirection() {
		return direction;
	}
	
	public Vector3 getPosition()
	{ 
		return position;
	}
	public void setNode(SceneNode ghostN)
	{
		node = ghostN;
	}
	public void setEntity(Entity ghostE)
	{
		entity = ghostE;
	}
	public int getID()
	{
		return id;
	}
	public SceneNode getSceneNode()
	{
		return node;
	}
	public Entity getEntity()
	{
		return entity;
	}
}	