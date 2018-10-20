package a3;

import java.util.UUID;

import ray.rage.scene.Entity;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;

public class GhostNPC
{ 
	private int id;
	private SceneNode node;
	private Entity entity;
	private Vector3 position;
	
	public GhostNPC(int id, Vector3 pos) // constructor
	{ 
		this.id = id;
		position = pos;
	}
	
	public void setPosition(Vector3 position)
	{ 
		node.setLocalPosition(position);
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