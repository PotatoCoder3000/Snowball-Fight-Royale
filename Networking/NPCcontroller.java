package Networking;

import java.util.Random;
import java.util.UUID;
import java.util.Vector;

import ray.ai.behaviortrees.BTCompositeType;
import ray.ai.behaviortrees.BTSequence;
import ray.ai.behaviortrees.BehaviorTree;

import ray.ai.behaviortrees.BTCompositeType;
import ray.ai.behaviortrees.BTSequence;
import ray.ai.behaviortrees.BehaviorTree;

public class NPCcontroller 
{
    //General declarations, counters and behavior tree
    private BehaviorTree bt = new BehaviorTree(BTCompositeType.SELECTOR);
    private GameServerUDP server;
    private long thinkStartTime;
    private long tickStateTime;
    private long lastThinkUpdateTime;
    private long lastTickUpdateTime;
    private int npcCount = 10;
    private Random rn = new Random();
    private NPC[] npc = new NPC[npcCount];
    
    public void start()
    {
        thinkStartTime = System.nanoTime();
        tickStateTime = System.nanoTime();
        lastThinkUpdateTime = thinkStartTime;
        lastTickUpdateTime = tickStateTime;
        //setupNPC();
        //npcLoop();
    }
    
    public void setupNPC()
    {
        for (int i = 0; i < npcCount; i++)
        {
            npc[i] = new NPC();
            npc[i].randomizeLocation(50 - rn.nextInt(100), 15, 50 - rn.nextInt(100));
            setupBehaviorTree(npc[i]);
        }
    }
    
    public void updateNPCs() {
    	for(int i = 0; i < npcCount; i++)
    		npc[i].updateLocation();
    		
    }
    
    public void npcLoop()
    {
    	while(true)
    	{ 
    		long currentTime = System.nanoTime();
    		float elapsedThinkMilliSecs = (currentTime - lastThinkUpdateTime)/(10000000.0f);
    		float elapsedTickMilliSecs = (currentTime - lastTickUpdateTime)/(1000000.0f);
    		
    		if(elapsedTickMilliSecs >= 50.0f)
    		{
    			lastTickUpdateTime = currentTime;
    			//npc.updateLocation();
    			server.sendNPCinfo();
    		}
    		
    		if(elapsedThinkMilliSecs >= 500.0f) {
    			lastThinkUpdateTime = currentTime;
    			bt.update(elapsedThinkMilliSecs);
    		}
    		Thread.yield();
    	}
    }

    public void setupBehaviorTree(NPC npc2)
    {
    	bt.insertAtRoot(new BTSequence(10));
        bt.insertAtRoot(new BTSequence(20));
        //bt.insert(10, new OneSecPassed(this, n, false));
        //bt.insert(10, new GetSmall(n));
        //bt.insert(20, new AvatarNear(server, this, n, false));
        //bt.insert(20, new GetBig(n));
    }
    
    public NPC[] getNPCs()
    {
        return npc;
    }
    
    public int getNumberOfNPCs()
    {
        return npcCount;
    }
}