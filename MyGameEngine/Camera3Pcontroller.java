package MyGameEngine;

import net.java.games.input.Component;
import ray.input.InputManager;
import ray.input.action.AbstractInputAction;
import ray.input.action.Action;
import ray.rage.scene.Camera;
import ray.rage.scene.SceneNode;

import ray.rml.Vector3;
import ray.rml.Vector3f;

public class Camera3Pcontroller 
{
	 private Camera camera;
	 private SceneNode cameraN; //the node the camera is attached to
	 private SceneNode target; //the target the camera looks at
	 private float cameraAzimuth; //rotation of camera around Y axis
	 private float cameraElevation; //elevation of camera above target
	 private float radias; //distance between camera and target
	 private Vector3 targetPos; //target’s position in the world
	 private Vector3 worldUpVec;
	 
	 public Camera3Pcontroller(Camera cam, SceneNode camN, SceneNode targ, String controllerName, InputManager im)
	 { 
		 camera = cam;
		 cameraN = camN;
		 target = targ;
		 cameraAzimuth = 225.0f; // start from BEHIND and ABOVE the target
		 cameraElevation = 20.0f; // elevation is in degrees
		 radias = 2.0f;
		 worldUpVec = Vector3f.createFrom(0.0f, 1.0f, 0.0f);
		 setupInputs(im, controllerName);
		 updateCameraPosition();

	 }
	 
	 private void setupInputs(InputManager im, String controllerName)
	 {
		 if(controllerName == im.getFirstGamepadName())
			 setupInputGamePad(im, controllerName);
		 else if(controllerName == im.getKeyboardName())
			 setupInputKeyboard(im, controllerName);
		 
	 }
	 
	 private void setupInputGamePad (InputManager im, String cn)
	 { 
		 Action orbitAroundAction = new OrbitAroundAction();
		 Action orbitRadiusAction = new OrbitRadiusAction();
		 Action orbitElevationAction = new OrbitElevationAction();
		 Action orbitElevationUpAction = new OrbitElevationUpAction();
		 Action orbitElevationDownAction = new OrbitElevationDownAction();
		 im.associateAction(cn,net.java.games.input.Component.Identifier.Axis.RX,  orbitAroundAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		 im.associateAction(cn,net.java.games.input.Component.Identifier.Axis.RY,  orbitElevationAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);	 
		 im.associateAction(cn,net.java.games.input.Component.Identifier.Axis.Z, orbitRadiusAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		 // similar input set up for OrbitRadiasAction, OrbitElevationAction
	 }
	 
	 private void setupInputKeyboard(InputManager im, String kn)
	 {
		 Action orbitRadiasZoomAction = new OrbitRadiasZoomAction();
		 Action orbitRadiasZoomOutAction = new OrbitRadiasZoomOutAction();
		 //Action orbitRadiusAction = new OrbitRadiusAction();
		 Action orbitAroundLeftAction = new OrbitAroundLeftAction();
		 Action orbitAroundRightAction = new OrbitAroundRightAction();
		 Action orbitElevationUpAction = new OrbitElevationUpAction();
		 Action orbitElevationDownAction = new OrbitElevationDownAction();
		 im.associateAction(kn,net.java.games.input.Component.Identifier.Key.I,  orbitRadiasZoomAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		 im.associateAction(kn,net.java.games.input.Component.Identifier.Key.O,  orbitRadiasZoomOutAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		 im.associateAction(kn,net.java.games.input.Component.Identifier.Key.LEFT,  orbitAroundLeftAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		 im.associateAction(kn,net.java.games.input.Component.Identifier.Key.RIGHT,  orbitAroundRightAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		 im.associateAction(kn,net.java.games.input.Component.Identifier.Key.UP,  orbitElevationUpAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		 im.associateAction(kn,net.java.games.input.Component.Identifier.Key.DOWN,  orbitElevationDownAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	 }
	 
	 private class OrbitAroundAction extends AbstractInputAction
	 { 
	  // Moves the camera around the target (changes camera azimuth).
		 public void performAction (float time, net.java.games.input.Event evt)
		 { 
			 float rotAmount;
			 
			 if (evt.getValue() < -0.5 || evt.getComponent().getIdentifier() == Component.Identifier.Key.LEFT){ 
				rotAmount= -5.0f; 
			 }
			 else if (evt.getValue() > 0.5 || evt.getComponent().getIdentifier() == Component.Identifier.Key.RIGHT){ 
				 rotAmount = 5.0f; 
			 }
			 else{ 
				rotAmount = 0.0f; 
			 }
			 
			 cameraAzimuth += rotAmount;
			 cameraAzimuth = cameraAzimuth % 360;
			 updateCameraPosition();
		 } 
	 }
	 //  similar for OrbitRadiasAction 
	 
	 private class OrbitRadiasAction extends AbstractInputAction
	 {
		 public void performAction(float time, net.java.games.input.Event evt)
		 {
			 float zoomAmount;
			 
//			 if((radias < 2.0) && (radias > 0.0))
//			 {
				 if(evt.getValue() < -0.5){
					 zoomAmount = -0.1f;
				 }
				 else if (evt.getValue() > 0.5){
					 zoomAmount = 0.1f;
				 }
				 else {
					 zoomAmount = 0.0f;
				 }
//			 }
			 
			 radias += zoomAmount;
			 updateCameraPosition();
		 }
	 }
	 
	 private class OrbitRadiusAction extends AbstractInputAction
	 {
		 public void performAction(float time, net.java.games.input.Event event)
		 {
			 if ((event.getValue() < -0.2 || event.getComponent().getIdentifier() == Component.Identifier.Key.I) && radias >= 0.5f)
			 {
				 radias -= 0.1f;
			 }
			 else if ((event.getValue() > 0.2f || event.getComponent().getIdentifier() == Component.Identifier.Key.O) && radias <= 3.5f)
			 {
				 radias += 0.1f;
			 }
			 updateCameraPosition();
		 }
	 }
	 
	 private class OrbitRadiasZoomAction extends AbstractInputAction
	 {
		 public void performAction(float time, net.java.games.input.Event evt)
		 {
			 radias += 0.1f;
			 updateCameraPosition();
		 }
	 }

	 private class OrbitRadiasZoomOutAction extends AbstractInputAction
	 {
		 public void performAction(float time, net.java.games.input.Event evt)
		 {
			 radias += -0.1f;
			 updateCameraPosition();
		 }
	 }
	 
	 private class OrbitAroundLeftAction extends AbstractInputAction
	 {
		 public void performAction(float time, net.java.games.input.Event evt)
		 {
			 cameraAzimuth += 5.0f;
			 cameraAzimuth = cameraAzimuth % 360;			 
			 updateCameraPosition();
		 }		 
	 }
	 
	 private class OrbitAroundRightAction extends AbstractInputAction
	 {
		 public void performAction(float time, net.java.games.input.Event evt)
		 {
			 cameraAzimuth += -5.0f;
			 cameraAzimuth = cameraAzimuth % 360;			 
			 updateCameraPosition();
		 }		 
	 }	 
	 //	 similar forOrbitElevationAction
	 
	 private class OrbitElevationAction extends AbstractInputAction
	 {
		 public void performAction(float time, net.java.games.input.Event evt)
		 {
			 if (evt.getValue() <-0.5 && cameraElevation <= 50.0f){
				 cameraElevation += 1.0f;
				 cameraElevation = cameraElevation % 360;
				 updateCameraPosition();
			 } else if (evt.getValue() > 0.5 && cameraElevation >= 2.0f) {
				 cameraElevation -= 1.0f;
				 cameraElevation = cameraElevation % 360;
				 updateCameraPosition();
			 }
		 }
	 }
	 
	 private class OrbitElevationUpAction extends AbstractInputAction
	 {
		 public void performAction(float time,net.java.games.input.Event evt)
		 {
			 cameraElevation += 1.0f;
			 cameraElevation = cameraElevation % 360;
			 updateCameraPosition();
		 }
	 }
	 
	 private class OrbitElevationDownAction extends AbstractInputAction
	 {
		 public void performAction(float time,net.java.games.input.Event evt)
		 {
			 cameraElevation -= 1.0f;
			 cameraElevation = cameraElevation % 360;
			 updateCameraPosition();
		 }
	 }
	 
	  public void updateCameraPosition() 
	  { 
		  double theta = Math.toRadians(cameraAzimuth);// rot around target
		  double phi = Math.toRadians(cameraElevation);// altitude angle
	      double x = radias * Math.cos(phi) * Math.sin(theta);
	      double y = radias * Math.sin(phi);
	      double z = radias * Math.cos(phi) * Math.cos(theta);
	      cameraN.setLocalPosition(Vector3f.createFrom((float)x, (float)y, (float)z).add(target.getWorldPosition()));
	      cameraN.lookAt(target, worldUpVec);
	  }   	 
}
