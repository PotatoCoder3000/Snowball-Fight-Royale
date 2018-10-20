package a3;

import java.util.UUID;

import ray.rage.scene.Entity;
import ray.rage.scene.SceneNode;
import ray.rage.scene.SkeletalEntity;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class GhostAvatar 
{
	private UUID id;
	private SceneNode node;
	private SkeletalEntity entity;
	private Vector3 position;
	
	public GhostAvatar(UUID ghostID, Vector3 pos)
	{
		id = ghostID;
		position = pos;
	}
	public void setNode(SceneNode ghostN)
	{
		node = ghostN;
	}
	public void setEntity(SkeletalEntity ghostE)
	{
		entity = ghostE;
	}
	public void setPosition(Vector3 ghostN)
	{
		node.setLocalPosition(ghostN);
	}
	public UUID getID()
	{
		return id;
	}
	public SceneNode getSceneNode()
	{
		return node;
	}
	public SkeletalEntity getEntity()
	{
		return entity;
	}
	public Vector3 getPosition()
	{
		return position;
	}
}