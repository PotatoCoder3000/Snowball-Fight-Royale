package Networking;

import java.util.Random;

public class NPC {
	
	double locX, locY, locZ;
	double offsetX, offsetY, offsetZ;
	Random r = new Random();
	
	public void randomizeLocation(int x, int y, int z){
		locX = x;
		locY = y;	
		locZ = z;	
	}
	
	public void bamboozle() {
		offsetX = 3 - r.nextInt(6);
		offsetZ = 3 - r.nextInt(6);
	}
	
	public double getX() {
		return locX;
	}
	public double getY() {
		return locY;
	}
	public double getZ() {
		return locZ;
	}
	
	public void updateLocation()
    {
		/*System.out.println("Bamboozle");
		bamboozle();
        locX += offsetX;
        locZ += offsetZ;*/
    }

}
