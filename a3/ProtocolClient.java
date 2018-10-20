package a3;

import ray.networking.client.GameConnectionClient;
import ray.rml.Vector3;
import ray.rml.Vector3f;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;

import javax.vecmath.Tuple3d;

public class ProtocolClient extends GameConnectionClient
{
	private MyGame game;
	private UUID id;
	private Vector<GhostAvatar> ghostAvatars;
	private Vector<GhostNPC> ghostNPCs;
	private int counter = 0;
	
	public ProtocolClient(InetAddress remAddr, int remPort, ProtocolType pType, MyGame game) throws IOException
	{
		super(remAddr, remPort, pType);
		this.game = game;
		this.id = UUID.randomUUID();
		this.ghostAvatars = new Vector<GhostAvatar>();
		this.ghostNPCs = new Vector<GhostNPC>();
		System.out.println("I HATE EVERYTHING RN");
	}
	
	public void processPacket(Object msg)
	{
		String strMessage = (String) msg;
		String[] msgTokens = strMessage.split(",");
		
		if (msgTokens.length > 0)
		{
			if (msgTokens[0].compareTo("join") == 0)
			{
				if (msgTokens[2].compareTo("success") == 0)
				{
					game.setIsConnected(true);
					sendCreateMessage(game.getPlayerPosition());

					
				}
				if (msgTokens[2].compareTo("failure") == 0)
				{
					game.setIsConnected(false);
				}
			}
			if (msgTokens[0].compareTo("bye") == 0)
			{
				UUID ghostID = UUID.fromString(msgTokens[1]);
				removeGhostAvatar(ghostID);
			}
			if (msgTokens[0].compareTo("dsfr") == 0)
			{
				UUID ghostID = UUID.fromString(msgTokens[1]);
				Vector3 ghostPosition = Vector3f.createFrom(Float.parseFloat(msgTokens[2]), Float.parseFloat(msgTokens[3]), Float.parseFloat(msgTokens[4]));
				createGhostAvatar(ghostID, ghostPosition);
			}
			if (msgTokens[0].compareTo("create") == 0)
			{
				System.out.println("Creating");
				UUID ghostID = UUID.fromString(msgTokens[1]);
				Vector3 position = Vector3f.createFrom(Float.parseFloat(msgTokens[2]), Float.parseFloat(msgTokens[3]), Float.parseFloat(msgTokens[4]));
				createGhostAvatar(ghostID, position);
			}
			if (msgTokens[0].compareTo("wsds") == 0)
			{
				UUID ghostID = UUID.fromString(msgTokens[1]);
				sendDetailsForMessage(ghostID, game.getPlayerPosition());
			}
			if (msgTokens[0].compareTo("move") == 0)
			{
				System.out.println("Recieved");
				
				UUID ghostID = UUID.fromString(msgTokens[1]);
				System.out.println(ghostID);
				Vector3 position = Vector3f.createFrom(Float.parseFloat(msgTokens[2]), Float.parseFloat(msgTokens[3]), Float.parseFloat(msgTokens[4]));
				moveGhostAvatar(ghostID, position);
			}
			if (msgTokens[0].compareTo("snowball") == 0)
			{
				UUID ghostID = UUID.fromString(msgTokens[1]);
				Vector3 pos = Vector3f.createFrom(Float.parseFloat(msgTokens[2]), Float.parseFloat(msgTokens[3]), Float.parseFloat(msgTokens[4]));
				Vector3 dir = Vector3f.createFrom(Float.parseFloat(msgTokens[5]), Float.parseFloat(msgTokens[6]), Float.parseFloat(msgTokens[7]));
				createGhostSnowball(ghostID, pos, dir);
			}
			if (msgTokens[0].compareTo("mnpc") == 0)
			{
				Integer npcID = Integer.parseInt(msgTokens[1]);
				Vector3 ghostPosition = Vector3f.createFrom(Float.parseFloat(msgTokens[2]),
															Float.parseFloat(msgTokens[3]),
															Float.parseFloat(msgTokens[4]));
				createGhostNPC(npcID, ghostPosition);
			}
		}
	}
	public void createGhostAvatar(UUID ghostID, Vector3 position)
	{
		GhostAvatar ghostAvatar = new GhostAvatar(ghostID, position);
		ghostAvatars.add(ghostAvatar);
		try
		{
			game.addGhostAvatarToGameWorld(ghostAvatar);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	public void removeGhostAvatar(UUID ghostID)
	{
		Iterator<GhostAvatar> iterator = ghostAvatars.iterator();
		while (iterator.hasNext())
		{
			GhostAvatar ghostAvatar = iterator.next();
			if (ghostAvatar.getID() == ghostID)
			{
				ghostAvatars.removeElement(ghostAvatar);
				game.removeGhostAvatarFromGameWorld(ghostAvatar);
				break;
			}
		}
	}
	public void moveGhostAvatar(UUID ghostID, Vector3 pos)
	{
		Iterator<GhostAvatar> iterator = ghostAvatars.iterator();
		while (iterator.hasNext())
		{
			GhostAvatar ghostAvatar = iterator.next();
			UUID id3 = ghostAvatar.getID();
			System.out.println(id3);
			System.out.println(ghostID);
			if (ghostAvatar.getID().equals(ghostID));
			{
				ghostAvatar.setPosition(pos);
				System.out.println("Moving");
				game.moveGhostAvatarAroundGameWorld(ghostAvatar, pos);
				break;
			}
		}
	}
	public void sendJoinMessage()
	{
		try
		{
			sendPacket(new String("join," + id.toString()));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	public void sendCreateMessage(Vector3 pos)
	{
		try
		{
			String message = new String("create," + id.toString());
			message += "," + pos.x() + "," + pos.y() + "," + pos.z();
			sendPacket(message);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	public void sendByeMessage()
	{
		try
		{
			String message = new String("bye," + id.toString());
			sendPacket(message);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	public void sendDetailsForMessage(UUID remId, Vector3 pos)
	{
		try
		{
			String message = new String("dsfr," + id.toString() + "," + remId.toString());
			message += "," + pos.x() + "," + pos.y() + "," + pos.z();
			sendPacket(message);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void sendWantsDetailsMessages()
	{
		try
		{	
			String message = new String("details," + id.toString());
			sendPacket(message);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMoveMessage(Vector3 pos)
	{
		try
		{
			String message = new String("move," + id.toString());
			message += "," + pos.x() + "," + pos.y() + "," + pos.z();
			sendPacket(message);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void createGhostNPC(int id, Vector3 position)
	{
		GhostNPC newNPC = new GhostNPC(id, position);
		
		for (int i = counter; i < 5; i++) {
			ghostNPCs.add(newNPC);
			if(newNPC.getID() ==  i) {
				try {
					game.addGhostNPCtoGameWorld(newNPC);
					System.out.println("HAHAHAHAHHAHA");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				//update position in game world
				System.out.println("ALREADYEXISTS" + newNPC.getID());
			}
		}
		addCounter();
		
		//create ghost npc in game world 
	}
	
	public void addCounter() {
		counter++;
	}
	
	public void throwSnowballMessage(Vector3 direction, Vector3 pos)
	{
		try
		{
			String message = new String("snowball," + id.toString());
			message += "," + pos.x() + "," + pos.y() + "," + pos.z();
			message += "," + direction.x() + "," + direction.y() + "," + direction.z();
			sendPacket(message);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void createGhostSnowball (UUID id, Vector3 pos, Vector3 dir)
	{
		try
		{game.addGhostSnowballToGameWorld(id, pos,dir);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void updateGhostNPC(UUID id, Vector3 position)
	{
		
	}

	public void askForNPCinfo()
	{
		try
		{
			sendPacket(new String("needNPC" + id.toString()));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	//handle updates to NPC positions
	//format (mnpc, npcID, xy, y, z)
}
