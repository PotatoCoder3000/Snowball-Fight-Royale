package Networking;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

import ray.networking.server.GameConnectionServer;
import ray.networking.server.IClientInfo;

public class GameServerUDP extends GameConnectionServer<UUID>
{	
    private NPCcontroller npcCtrl;
    private NPC[] npc;
	public GameServerUDP(int localPort, NPCcontroller npcCtrl2) throws IOException
    { 
        super(localPort, ProtocolType.UDP); 
        npcCtrl = npcCtrl2;
    }
    @Override
    public void processPacket(Object o, InetAddress senderIP, int sndPort)
    {
        String message = (String) o;
        String[] msgTokens = message.split(",");
 
        if(msgTokens.length > 0)
        {
            // case where server receives a JOIN message
            // format: join,localid
            if(msgTokens[0].compareTo("join") == 0)
            { 
                try
				{ 
                    IClientInfo ci;
                    ci = getServerSocket().createClientInfo(senderIP, sndPort);
                    UUID clientID = UUID.fromString(msgTokens[1]);
                    addClient(ci, clientID);
                    sendJoinedMessage(clientID, true);
                }
                catch (IOException e)
                { 
                    e.printStackTrace();
                }
            }
            // case where server receives a CREATE message
            // format: create,localid,x,y,z
            if(msgTokens[0].compareTo("create") == 0)
            { 
                System.out.println("Creating");
                UUID clientID = UUID.fromString(msgTokens[1]);
                String[] pos = {msgTokens[2], msgTokens[3], msgTokens[4]};
                sendCreateMessage(clientID, pos);
                sendWantsDetailsMessage(clientID);
            }
         // case where server receives a BYE message
            // format: bye,localid
            if(msgTokens[0].compareTo("bye") == 0)
            { 
                UUID clientID = UUID.fromString(msgTokens[1]);
                sendByeMessage(clientID);
                removeClient(clientID);
            }
            // case where server receives a DETAILS-FOR message
            if(msgTokens[0].compareTo("dsfr") == 0)
            { // etc….. 
                UUID clientID = UUID.fromString(msgTokens[1]);
                UUID remoteid = UUID.fromString(msgTokens[2]);
                String[] pos = {msgTokens[3], msgTokens[4], msgTokens[5]};
                sendDetailsMessage(clientID, remoteid, pos);
            }
            // case where server receives a MOVE message
            if(msgTokens[0].compareTo("move") == 0)
            { // etc….. 
                System.out.println("Recieved");
                UUID clientID = UUID.fromString(msgTokens[1]);
                System.out.println(clientID);
                String[] pos = {msgTokens[2], msgTokens[3], msgTokens[4]};
                sendMoveMessage(clientID, pos);
            } 
            if (msgTokens[0].compareTo("spear") == 0)
            {
                UUID clientID = UUID.fromString(msgTokens[1]);
                String[] pos = {msgTokens[2], msgTokens[3], msgTokens[4]};
                String[] dir = {msgTokens[5], msgTokens[6], msgTokens[7]};
                throwSnowballMessage(clientID, dir, pos);
            }
            //send NPC info
            if(msgTokens[0].compareTo("mnpc") == 0)
            {
                UUID npcID = UUID.fromString(msgTokens[1]);
                String[] pos = {msgTokens[2], msgTokens[3], msgTokens[4]};
                sendNPCinfo();
            }
        }
    }
    
    public void throwSnowballMessage(UUID clientID, String[] dir, String[] pos)
    {
		try
		{
			String message = new String("spear," + clientID.toString());
			message += "," + pos[0] + "," + pos[1] + "," + pos[2];
			message += "," + dir[0] + "," + dir[1] + "," + dir[2];
			forwardPacketToAll(message, clientID);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
    }
    
	public void sendJoinedMessage(UUID clientID, boolean success)
	{ // format: join, success or join, failure
		try
		{ 
		    String message = new String("join," + clientID.toString());
		    if (success) 
		    {
		    	message += ",success";
		    }
		    else 
		    {
		    	message += ",failure";
		    }
		    sendPacket(message, clientID);
		}
		catch (IOException e) 
		{ 
		    e.printStackTrace(); 
	}
	}
	public void sendCreateMessage(UUID clientID, String[] position)
	{ // format: create, remoteId, x, y, z
		try
		{
		    String message = new String("create," + clientID.toString());
		    message += "," + position[0];
		    message += "," + position[1];
		    message += "," + position[2];
		    forwardPacketToAll(message, clientID);
		}
		catch (IOException e) 
		{ 
		    e.printStackTrace();
		}
	}
	public void sendDetailsMessage(UUID clientID, UUID remoteid, String[] position)
	{
		try
		{
		    String message = new String("dsfr," + clientID.toString());
		    message += "," + position[0];
		    message += "," + position[1];
		    message += "," + position[2];
		    sendPacket(message, remoteid);
		}
		catch (IOException e) 
		{
		    e.printStackTrace();
		}
	}
	public void sendWantsDetailsMessage(UUID clientID)
	{
		try
		{
		    String message = new String("wsds," + clientID.toString());
		    forwardPacketToAll(message, clientID);
		}
		catch (IOException e) 
		{
		    e.printStackTrace();
		}
	}
	public void sendMoveMessage(UUID clientID, String[] position)
	{
		System.out.println("Sent");
		System.out.println(clientID);
		try
		{
			String message = new String("move," + clientID.toString());
		    message += "," + position[0];
		    message += "," + position[1];
		    message += "," + position[2];
		    forwardPacketToAll(message, clientID);
		}
		catch (IOException e) 
		{ 
		    e.printStackTrace();
		}
	}
	public void sendByeMessage(UUID clientID)
	{
		try 
		{
			String message = new String("bye," + clientID.toString());
			forwardPacketToAll(message, clientID);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void sendNPCinfo()
	{
		npc = npcCtrl.getNPCs();
		for (int i = 0; i < npcCtrl.getNumberOfNPCs(); i++)
		{
			try
			{
				String message = new String("mnpc," + Integer.toString(i));
				message += "," + (npc[i].getX());
				message += "," + (npc[i].getY());
				message += "," + (npc[i].getZ());
				sendPacketToAll(message);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}