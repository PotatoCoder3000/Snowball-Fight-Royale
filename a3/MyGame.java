package a3;

import MyGameEngine.Camera3Pcontroller;
//import MyGameEngine.ChangeCameraModeAction;
import MyGameEngine.MoveBackwardsAction;
import MyGameEngine.MoveForwardAction;
import MyGameEngine.MoveLeftAction;
import MyGameEngine.MoveRightAction;
import MyGameEngine.MovementController;
import MyGameEngine.RotateLeftAction;
import MyGameEngine.RotateRightAction;
import MyGameEngine.RotateYawAxisAction;
import MyGameEngine.MoveOnXAxisAction;
import MyGameEngine.MoveOnYAxisAction;
import MyGameEngine.ScaleController;
import MyGameEngine.ThrowingAction;
import net.java.games.input.Event;
import MyGameEngine.QuitGameAction;

import a3.ProtocolClient;
import MyGameEngine.DisplaySettingsDialog;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import javax.script.Invocable;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;
import java.util.Vector;
import java.lang.Math;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.awt.geom.*;

import ray.audio.AudioManagerFactory;
import ray.audio.AudioResource;
import ray.audio.AudioResourceType;
import ray.audio.IAudioManager;
import ray.audio.Sound;
import ray.audio.SoundType;
import ray.input.*;
import ray.input.action.*;
import ray.networking.IGameConnection.ProtocolType;
import ray.physics.PhysicsEngine;
import ray.physics.PhysicsEngineFactory;
import ray.physics.PhysicsObject;
import ray.physics.JBullet.JBulletUtils;
import ray.rage.*;
import ray.rage.asset.texture.*;
import ray.rage.game.*;
import ray.rage.rendersystem.*;
import ray.rage.rendersystem.Renderable.*;
import ray.rage.rendersystem.gl4.GL4RenderSystem;
import ray.rage.rendersystem.states.*;
import ray.rage.rendersystem.shader.*;
import ray.rage.asset.material.*;
import ray.rage.scene.*;
import ray.rage.scene.Camera.Frustum.*;
import ray.rage.scene.SkeletalEntity.EndType;
import ray.rage.scene.controllers.*;
import ray.rml.*;
import ray.rage.util.BufferUtil;
import ray.rage.rendersystem.states.*;
import ray.rage.asset.texture.*;
import ray.rage.util.*;
import static ray.rage.scene.SkeletalEntity.EndType.*;

public class MyGame extends VariableFrameRateGame
{
	GL4RenderSystem rs;
	private float elapsTime = 0.0f;
	private String elapsTimeStr, scoreStr1, scoreStr2, dispStr1, dispStr2;
	private int elapsTimeSec, score1 = 0;
	private int score2 = 0;
	private float randX, randZ;
	private Random randNum = new Random();
	private Camera camera1;
	private SceneNode avatarN, cameraNodeN1, orangeNG;
	private Light plight, dlight;
	private SceneManager sm;
	private Camera3Pcontroller orbitController1, orbitController2;
	private InputManager im = new GenericInputManager();
	private Viewport topViewport;
	private Action quitGameAction, MoveOnYAxisAction, MoveOnXAxisAction, MoveForwardAction, MoveBackwardsAction, 
				   MoveLeftAction, MoveRightAction, RotateLeftAction1, RotateRightAction1, RotateLeftAction2, RotateRightAction2, RotateYawAxisAction, ThrowingAction;
	private SceneNode[] orangeArray = new SceneNode[15];
	private MovementController[] moveArray = new MovementController[15];
	private static final String SKYBOX_NAME = "MySkyBox";
	private boolean skyBoxVisible = true;
	protected ScriptEngine jsEngine;
	protected ColorAction colorAction;
	protected File scriptFile3;
	private String serverAddress;
	private int serverPort;
	private ProtocolType serverProtocol;
	private ProtocolClient protClient;
	private boolean isClientConnected;
	private boolean running = true;
	private boolean visible = false;
	private PhysicsEngine physicsEngine;
	private GhostAvatar ghost;
	private Vector<UUID> gameObjectsToRemove;
	private SceneNode ball1Node, ball2Node, gndNode;
	private SceneNode cameraPositionNode;
	private final static String GROUND_E = "Ground";
	private final static String GROUND_N = "GroundNode";
	private IAudioManager audioMgr;
	private Sound bgSound, throwSound;
	
	private PhysicsObject ball1PhysObj, ball2PhysObj, gndPlaneP;
	

	public MyGame(String serverAddr, int sPort) {
		super();
		this.serverAddress = serverAddr;
		this.serverPort = sPort;
		this.serverProtocol = ProtocolType.UDP;
		System.out.println("W,A,S,D to move forward, backwards, left and right");	
	}

	public static void main(String[] args)
	{ 
		Game game = new MyGame(args[0], Integer.parseInt(args[1]));

		try
		{ 
			game.startup();
			game.run();
		}

		catch (Exception e){ 
			e.printStackTrace(System.err);
		}

		finally
		{ 
			game.shutdown();
			game.exit();
		} 
	}

	@Override
	protected void setupWindow(RenderSystem rs, GraphicsEnvironment ge) {
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		DisplaySettingsDialog dsd = new DisplaySettingsDialog(ge.getDefaultScreenDevice());
		dsd.showIt();
		RenderWindow rw = rs.createRenderWindow(dsd.getSelectedDisplayMode(),
		dsd.isFullScreenModeSelected());
	}

	protected void setupWindowViewports(RenderWindow rw)
	{ 
		rw.addKeyListener(this);
		topViewport = rw.getViewport(0);
	}
	
    @Override
    protected void setupCameras(SceneManager sm, RenderWindow rw) 
    {
        camera1 = sm.createCamera("MainCamera1", Projection.PERSPECTIVE);
        camera1.setMode('r');
        rw.getViewport(0).setCamera(camera1);
        
    }
    

    @Override
    protected void setupScene(Engine eng, SceneManager sm) throws IOException 
    {
    	this.sm = sm;
    	TextureManager tm = eng.getTextureManager();
    	Configuration conf = eng.getConfiguration();
    	ScriptEngineManager factory = new ScriptEngineManager();
    	SceneNode rootNode = sm.getRootSceneNode();
    	java.util.List<ScriptEngineFactory> list = factory.getEngineFactories();
    	jsEngine = factory.getEngineByName("js");
    	
    	scriptFile3 = new File("UpdateLightColor.js");
    	this.runScript(scriptFile3);
    	
		ManualObject axesO = makeAxes(eng, sm);
		SceneNode axesN = sm.getRootSceneNode().createChildSceneNode("axesNode");
		axesN.attachObject(axesO);
		axesN.moveBackward(2.0f);

        SkeletalEntity avatarE = sm.createSkeletalEntity("manAvatar", "avatar3.rkm", "avatar3.rks");
        Texture avatarTex = sm.getTextureManager().getAssetByPath("ice.jpg");
        TextureState avatarTexState = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
        avatarTexState.setTexture(avatarTex);
        avatarE.setRenderState(avatarTexState);
        avatarN = sm.getRootSceneNode().createChildSceneNode(avatarE.getName() + "Node");
        avatarN.moveBackward(2.0f);
        avatarN.translate(0.0f, 2.0f, 0.0f);
        avatarN.scale(0.1f, 0.1f, 0.1f);
        avatarN.attachObject(avatarE);
        cameraNodeN1 = sm.getRootSceneNode().createChildSceneNode(camera1.getName() + "Node");
        cameraNodeN1.attachObject(camera1);
         
		avatarE.loadAnimation("throwingAnimation", "Throwing.rka");
		avatarE.loadAnimation("walkingAnimation", "Walking.rka");
		
        ScaleController sc = new ScaleController(this, sm);
        
		Tessellation tessE = sm.createTessellation("tessE", 5);
		tessE.setSubdivisions(36f);
		SceneNode tessN = sm.getRootSceneNode().createChildSceneNode("tessN");
		tessN.attachObject(tessE);		
		tessN.scale(500, 400, 500);
		tessE.setHeightMap(this.getEngine(), "heightmap2.png");
		tessE.setTexture(this.getEngine(), "snow.jpg");
		tessE.setTextureTiling(1, 1);
//		tessE.setNormalMap(this.getEngine(), "heightmap2.png");
		
        sm.getAmbientLight().setIntensity(new Color(.50f, .50f, .50f));
   
			
		plight = sm.createLight("testLamp1", Light.Type.POINT);
		plight.setAmbient(new Color(0.3f, 0.3f, 0.3f));
        plight.setDiffuse(new Color(.7f, .7f, .7f));
		plight.setSpecular(new Color(1.0f, 1.0f, 1.0f));
        plight.setRange(100.0f);
        
        dlight = sm.createLight("testLamp2", Light.Type.DIRECTIONAL);
        dlight.setAmbient(new Color(0.7f, 0.7f, 0.7f));
        dlight.setDiffuse(new Color(0.5f, 0.5f, 0.5f));
        dlight.setSpecular(new Color(1.0f, 1.0f, 1.0f));
        dlight.setRange(20.0f);
        dlight.setVisible(false);
        
		
		SceneNode plightNode = sm.getRootSceneNode().createChildSceneNode("plightNode");
        plightNode.attachObject(plight);
        plightNode.moveUp(2.5f);
        
        SceneNode dlightNode = sm.getRootSceneNode().createChildSceneNode("dlightNode");
        dlightNode.attachObject(dlight);
        dlightNode.moveUp(2.5f);
        
        // Ball 1
     	/*Entity ball1Entity = sm.createEntity("ball1", "earth.obj");
     	ball1Node = rootNode.createChildSceneNode("Ball1Node");
     	ball1Node.attachObject(ball1Entity);
     	ball1Node.setLocalPosition(0, 10, -2);
     	// Ball 2
     	Entity ball2Entity = sm.createEntity("Ball2", "earth.obj");
     	ball2Node = rootNode.createChildSceneNode("Ball2Node");
     	ball2Node.attachObject(ball2Entity);
     	ball2Node.setLocalPosition(-1,20,-2);*/
     	// Ground plane
     	Entity groundEntity = sm.createEntity(GROUND_E, "cube.obj");
     	gndNode = rootNode.createChildSceneNode(GROUND_N);
     	gndNode.attachObject(groundEntity);
     	gndNode.setLocalPosition(0, 0, 0);
     	gndNode.moveDown(5.0f);
        
        tm.setBaseDirectoryPath(conf.valueOf("assets.skyboxes.path"));
		Texture front = tm.getAssetByPath("TropicalSunnyDayFront.png");
		Texture back = tm.getAssetByPath("TropicalSunnyDayBack.png");
		Texture left = tm.getAssetByPath("TropicalSunnyDayLeft.png");
		Texture right = tm.getAssetByPath("TropicalSunnyDayRight.png");
		Texture top = tm.getAssetByPath("TropicalSunnyDayTop.png");
		Texture bottom = tm.getAssetByPath("TropicalSunnyDayBottom.png");
		
		AffineTransform xform = new AffineTransform();
		xform.translate(0, front.getImage().getHeight());
		xform.scale(1d, -1d);
		front.transform(xform);
		back.transform(xform);
		left.transform(xform);
		right.transform(xform);
		top.transform(xform);
		bottom.transform(xform);
		
		SkyBox sb = sm.createSkyBox(SKYBOX_NAME);
		sb.setTexture(front, SkyBox.Face.FRONT);
		sb.setTexture(back, SkyBox.Face.BACK);
		sb.setTexture(left, SkyBox.Face.LEFT);
		sb.setTexture(right, SkyBox.Face.RIGHT);
		sb.setTexture(top, SkyBox.Face.TOP);
		sb.setTexture(bottom, SkyBox.Face.BOTTOM);
		sm.setActiveSkyBox(sb);	
		
		initPhysicsSystem();
		createRagePhysicsWorld();
        
		setupNetworking();
		setupInputs();
    	initAudio(sm);
    	setupOrbitCamera(eng, sm);
    	  	 
    	sm.addController(sc);
    }

    protected ManualObject makeAxes(Engine eng, SceneManager sm) throws IOException
    {
        ManualObject axes = sm.createManualObject("Axes");
        ManualObjectSection axesSec = axes.createManualSection("AxesSection");
        axesSec.setPrimitive(Primitive.LINES);
        axes.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));

        float [] vertices = new float[]
        {
             0.0f, 0.0f, 0.0f, 30.0f, 0.0f, 0.0f, 0.0f,
             0.0f, 0.0f, 0.0f, 0.0f, 30.0f, 0.0f, 0.0f,
             0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 30.0f, 0.0f
        };
        
        int[] indices = new int[] { 0,1,2,3,4,5 };

        FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
        IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
        axesSec.setVertexBuffer(vertBuf);
        axesSec.setIndexBuffer(indexBuf);

        Material mat = sm.getMaterialManager().getAssetByPath("default.mtl");
        mat.setEmissive(Color.BLUE);
        Texture tex = sm.getTextureManager().getAssetByPath(mat.getTextureFilename());
        TextureState tstate = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
        tstate.setTexture(tex);
        axesSec.setRenderState(tstate);
        axesSec.setMaterial(mat);
        
        return axes;
    }

    
    protected ManualObject makeOcean(Engine eng, SceneManager sm) throws IOException
	{ 
		ManualObject ocean = sm.createManualObject("ocean");
		ManualObjectSection oceanSec = ocean.createManualSection("OceanSection");
		ocean.setGpuShaderProgram(sm.getRenderSystem().
		getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));

		float[] vertices = new float[]
		{ 
			-1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 
			1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f 
		};

		float[] texcoords = new float[]
		{
			0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
			1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f
		};

		float[] normals = new float[]
		{ 
			0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f,
			0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f
		};

		int[] indices = new int[] {  0,1,2,3,4,5 };

		FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
		FloatBuffer texBuf = BufferUtil.directFloatBuffer(texcoords);
		FloatBuffer normBuf = BufferUtil.directFloatBuffer(normals);
		IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);

		oceanSec.setVertexBuffer(vertBuf);
		oceanSec.setTextureCoordsBuffer(texBuf);
		oceanSec.setNormalsBuffer(normBuf);
		oceanSec.setIndexBuffer(indexBuf);

		Texture tex = eng.getTextureManager().getAssetByPath("water.jpg");
		TextureState texState = (TextureState)sm.getRenderSystem().
		createRenderState(RenderState.Type.TEXTURE);
		texState.setTexture(tex);
		FrontFaceState faceState = (FrontFaceState) sm.getRenderSystem().
		createRenderState(RenderState.Type.FRONT_FACE);
		faceState.setVertexWinding(FrontFaceState.VertexWinding.CLOCKWISE);

		ocean.setDataSource(DataSource.INDEX_BUFFER);
		ocean.setRenderState(texState);
		ocean.setRenderState(faceState);

		return ocean;
	}
    
   
    protected void setupInputs()
	{	 
    	im = new GenericInputManager();
		String kbName = im.getKeyboardName();
		String gpName = im.getFirstGamepadName();
		quitGameAction = new QuitGameAction(this);
		MoveForwardAction = new MoveForwardAction(avatarN, protClient, this);
		MoveBackwardsAction = new MoveBackwardsAction(avatarN, protClient);
		MoveOnYAxisAction = new MoveOnYAxisAction(avatarN, this, protClient);
		MoveLeftAction = new MoveLeftAction(avatarN);
		MoveRightAction = new MoveRightAction(avatarN);
		RotateLeftAction1 = new RotateLeftAction(avatarN);
		RotateRightAction1 = new RotateRightAction(avatarN);
		RotateLeftAction2 = new RotateLeftAction(avatarN);
		RotateRightAction2 = new RotateRightAction(avatarN);
		RotateYawAxisAction = new RotateYawAxisAction(RotateLeftAction2, RotateRightAction2);
		MoveOnXAxisAction = new MoveOnXAxisAction(avatarN, protClient);
		ThrowingAction = new ThrowingAction(avatarN, this);
		colorAction = new ColorAction(sm);

		// attach the action objects to keyboard and gamepad components
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.W, MoveForwardAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);	
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.S, MoveBackwardsAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.D, RotateRightAction1, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.A, RotateLeftAction1, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.SPACE, colorAction, InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		//im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.A, MoveLeftAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);		
		//im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.D, MoveRightAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.Y, MoveOnYAxisAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.X, RotateYawAxisAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(gpName,  net.java.games.input.Component.Identifier.Button._3, ThrowingAction, InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		//im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.RX, RotateYawAxisAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		//im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._6, RotateRightAction2, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		//im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._5, RotateLeftAction2, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		//im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._8, quitGameAction, InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.ESCAPE,quitGameAction ,InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
	}


	protected void setupOrbitCamera(Engine eng, SceneManager sm)
	{
		SceneNode avatarN = sm.getSceneNode("manAvatarNode");
		SceneNode cameraNodeN1 = sm.getSceneNode("MainCamera1Node");
		String kbName = im.getKeyboardName();
		String gpName = im.getFirstGamepadName();
		Camera camera1 = sm.getCamera("MainCamera1");			
		orbitController1 = new Camera3Pcontroller(camera1, cameraNodeN1, avatarN, kbName, im);
		orbitController2 = new Camera3Pcontroller(camera1, cameraNodeN1, avatarN, gpName, im);
	}
	
	private void createRagePhysicsWorld()
	{ 
		float mass = 1.0f;
		float up[] = {0,1,0};
		double[] temptf;
		temptf = toDoubleArray(gndNode.getLocalTransform().toFloatArray());
		gndPlaneP = physicsEngine.addStaticPlaneObject(physicsEngine.nextUID(),temptf, up, 0.0f);
		gndPlaneP.setBounciness(1.0f);
		gndNode.scale(3f, .05f, 3f);
		gndNode.setLocalPosition(0, -7, -2);
		gndNode.setPhysicsObject(gndPlaneP);
		// can also set damping, friction, etc.
	}
	
	private void initPhysicsSystem()
	{ 
		String engine = "ray.physics.JBullet.JBulletPhysicsEngine";
		float[] gravity = {0, -10f, 0};
		physicsEngine = PhysicsEngineFactory.createPhysicsEngine(engine);
		physicsEngine.initSystem();
		physicsEngine.setGravity(gravity);
	}
	
	public void setEarParameters(SceneManager sm)
	{ 
		SceneNode avatarN = sm.getSceneNode("manAvatarNode");
		Vector3 avDir = avatarN.getWorldForwardAxis();
		// note - should get the camera's forward direction
		// - avatar direction plus azimuth
		audioMgr.getEar().setLocation(avatarN.getWorldPosition());
		audioMgr.getEar().setOrientation(avDir, Vector3f.createFrom(0,1,0));
	}
	
	private void initAudio(SceneManager sm)
	{
		AudioResource resource1;
		AudioResource resource2;
		audioMgr = AudioManagerFactory.createAudioManager("ray.audio.joal.JOALAudioManager");
		if (!audioMgr.initialize())
		{ 
			System.out.println("Audio Manager failed to initialize!");
			return;
		}
		resource1 = audioMgr.createAudioResource("bgSound.wav",AudioResourceType.AUDIO_SAMPLE);
		bgSound = new Sound(resource1,SoundType.SOUND_EFFECT, 50, true);
		bgSound.initialize(audioMgr);
		bgSound.setMaxDistance(1000.0f);
		bgSound.setRollOff(5.0f);
		
		resource2 = audioMgr.createAudioResource("throw.wav", AudioResourceType.AUDIO_SAMPLE);
		throwSound = new Sound(resource2,SoundType.SOUND_EFFECT, 100,false);
		throwSound.initialize(audioMgr);
		throwSound.setMaxDistance(1000.0f);
		throwSound.setRollOff(5.0f);
		
		SceneNode musicN = sm.getSceneNode("manAvatarNode");
		bgSound.setLocation(musicN.getWorldPosition());
		throwSound.setLocation(musicN.getWorldPosition());
		setEarParameters(sm);
		bgSound.play();
	}
	
	private float[] toFloatArray(double[] arr)
	{ 
		if (arr == null) return null;
	
		int n = arr.length;
		float[] ret = new float[n];
		for (int i = 0; i < n; i++)
		{ 
			ret[i] = (float)arr[i];
		}
		return ret;
	}
	
	private double[] toDoubleArray(float[] arr)
	{ if (arr == null) return null;
	int n = arr.length;
	double[] ret = new double[n];
	for (int i = 0; i < n; i++)
	{ ret[i] = (double)arr[i];
	}
	return ret;
	}
	
	protected void updateVerticalPosition()
    {
        SceneNode avatarN = this.getEngine().getSceneManager().getSceneNode("manAvatarNode");
        
        SceneNode tessN = this.getEngine().getSceneManager().getSceneNode("tessN");
        
        Tessellation tessE = ((Tessellation) tessN.getAttachedObject("tessE"));
        
        Vector3 worldAvatarPosition = avatarN.getWorldPosition();
        Vector3 localAvatarPosition = avatarN.getLocalPosition();
        Vector3 newAvatarPosition = Vector3f.createFrom(localAvatarPosition.x(), 
                                                        tessE.getWorldHeight(worldAvatarPosition.x(), worldAvatarPosition.z()),
                                                        localAvatarPosition.z());
        
        avatarN.setLocalPosition(newAvatarPosition);
        avatarN.moveUp(0.9f);    
    }
	
	public void walkAction()
	{
		SkeletalEntity avatarE = (SkeletalEntity) sm.getEntity("manAvatar");
		
		avatarE.stopAnimation();
		avatarE.playAnimation("walkingAnimation", 5.0f, LOOP, 0);
	}
	
	public void throwingAction() 
	{
		try{
			SkeletalEntity avatarE = (SkeletalEntity) sm.getEntity("manAvatar");
			SceneNode avatarN = sm.getSceneNode("manAvatarNode");
			avatarE.stopAnimation();
			avatarE.playAnimation("throwingAnimation", 5.0f, STOP, 0);
			int uid = physicsEngine.nextUID();
			float mass = 1.0f;
			Entity snowballE = getEngine().getSceneManager().createEntity("snowball" + uid,  "sphere.obj");
			SceneNode snowballN = getEngine().getSceneManager().getRootSceneNode().createChildSceneNode("snowball" + uid);
			snowballN.scale(0.05f, 0.05f, 0.05f);
			snowballN.attachObject(snowballE);
			snowballN.setLocalPosition(getPlayerPosition());
			snowballN.setLocalRotation(avatarN.getWorldRotation());
			snowballN.moveUp(0.5f);
			snowballN.moveLeft(0.1f);
			double[] f = JBulletUtils.float_to_double_array(snowballN.getLocalTransform().toFloatArray());
			PhysicsObject snowball = physicsEngine.addSphereObject(uid, mass, f, 2.0f);
			//ball1PhysObj = physicsEngine.addSphereObject(physicsEngine.nextUID(),mass, temptf, 2.0f);
			//Matrix3 throwSnowballForce = Matrix3.createFrom(Vector3f.createFrom(Vector3f.createZeroVector(), Vector3f.createZeroVector(), Vector3f.createZeroVector()));
			Vector3 a = snowballN.getLocalRotation().mult(Vector3f.createFrom(0,0,80));
			snowball.setLinearVelocity(new float[] { a.x(), a.y(), a.z() });
			snowballN.setPhysicsObject(snowball);
			throwSound.play();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}	
	private void runScript(File scriptFile)
	 { try
	 { FileReader fileReader = new FileReader(scriptFile);
	 jsEngine.eval(fileReader);
	 fileReader.close();
	 }
	 catch (FileNotFoundException e1)
	 { System.out.println(scriptFile + " not found " + e1); }
	 catch (IOException e2)
	 { System.out.println("IO problem with " + scriptFile + e2); }
	 catch (ScriptException e3)
	 { System.out.println("Script Exception in " + scriptFile + e3); }
	 catch (NullPointerException e4)
	 { System.out.println ("Null ptr exception reading " + scriptFile + e4); }
	 }
	
	public class ColorAction extends AbstractInputAction
	{ 
	    private SceneManager sm;

	    public ColorAction(SceneManager s) { 
	    	sm = s; 
	    } // constructor

	    public void performAction(float time, Event e)

	    { //cast the engine so it supports invoking functions
	    	System.out.println("Hello");
	    	Invocable invocableEngine = (Invocable) jsEngine ;
	        //get the light to be updated
	    	
	        //Light lgt = sm.getLight("testLamp1");
	        Light lgt = getEngine().getSceneManager().getLight("testLamp1");
	        Light lgt2 = getEngine().getSceneManager().getLight("testLamp2");
	        if(visible == false){
	        	visible = true;
	        	lgt2.setVisible(true);
	        } else {
	        	visible = false;
	        	lgt2.setVisible(false);
	        }

	        // invoke the script function
	        try
	        { 
	            invocableEngine.invokeFunction("updateAmbientColor", lgt); 
	        } catch (ScriptException e1) { 
	            System.out.println("ScriptException in " + scriptFile3 + e1); 
	        } catch (NoSuchMethodException e2) { 
	            System.out.println("No such method in " + scriptFile3 + e2); 
	        } catch (NullPointerException e3) { 
	            System.out.println ("Null ptr exception reading " + scriptFile3 + e3); }
	    } 
	}
	
	public void setIsConnected(boolean b){
		isClientConnected = b;
	}
	
	public Vector3 getPlayerPosition()
	{ 
		SceneNode avatarN = sm.getSceneNode("manAvatarNode");
		return avatarN.getWorldPosition();
	}
	
	public void addGhostAvatarToGameWorld(GhostAvatar avatar) throws IOException
	{
		if (avatar != null)
		{
			/*System.out.println("NEW AVATAR");
			Entity ghostE = sm.createEntity(avatar.getID().toString(), "dolphinHighPoly.obj");
			ghostE.setPrimitive(Primitive.TRIANGLES);*/
			
			SkeletalEntity ghostE = sm.createSkeletalEntity(avatar.getID().toString(), "avatar3.rkm", "avatar3.rks");
	        Texture ghostTex = sm.getTextureManager().getAssetByPath("ice.jpg");
	        TextureState ghostTexState = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
	        ghostTexState.setTexture(ghostTex);
	        ghostE.setRenderState(ghostTexState);
	        ghostE.playAnimation("walkingAnimation", 5.0f, LOOP, 0);
			
			SceneNode ghostN = sm.getRootSceneNode().createChildSceneNode(avatar.getID().toString() + "Node");
			ghostN.scale(0.1f, 0.1f, 0.1f);
			ghostN.attachObject(ghostE);
			ghostN.setLocalPosition(avatar.getPosition());
			avatar.setNode(ghostN);
			avatar.setEntity(ghostE);
			avatar.setPosition(ghostN.getLocalPosition());
		}
	}
	
	public void moveGhostAvatarAroundGameWorld(GhostAvatar avatar, Vector3 pos)
	{
		avatar.getSceneNode().setLocalPosition(pos);
	}
	
	public void removeGhostAvatarFromGameWorld(GhostAvatar ghostID)
	{
		if (ghostID != null)
		{
			gameObjectsToRemove.add(ghostID.getID());
	    }	
	}
	  
	public void addGhostNPCtoGameWorld(GhostNPC npc) throws IOException
	{
		System.out.println("NPC INSTANTIATED");
		Entity npcE = sm.createEntity(Integer.toString(npc.getID()),  "dolphinHighPoly.obj");
		npcE.setPrimitive(Primitive.TRIANGLES);
		SceneNode npcN = sm.getRootSceneNode().createChildSceneNode(npcE.getName() + "Node");
		npcN.attachObject(npcE);
		npcN.setLocalPosition(npc.getPosition());
		npcN.scale(5.0f, 5.0f, 5.0f);
		npc.setNode(npcN);
		npc.setEntity(npcE);
		npc.setPosition(npcN.getLocalPosition());
	}
	    
	public void moveGhostNPCAroundGameWorld(GhostNPC npc, Vector3 pos)
	{
		npc.getSceneNode().setLocalPosition(pos);
	}
	
	public void addGhostSnowballToGameWorld( Vector3 pos, Vector3 throwVector) throws IOException
    {
            
    }
	
	protected void setupNetworking()
    {
    	gameObjectsToRemove = new Vector<UUID>();
    	isClientConnected = true;
    	try {
    		protClient = new ProtocolClient(InetAddress.getByName(serverAddress), serverPort, serverProtocol, this);
    	} catch (UnknownHostException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	if(protClient == null){
    		System.out.println("missing protocol host");
    	} else {
    		protClient.sendJoinMessage();
    	}
    }
	
    public void processNetworking(float elapsTime)
    {
    	if (protClient != null)
        {
            protClient.processPackets();
        }
        Iterator<UUID> iterator = gameObjectsToRemove.iterator();
        while(iterator.hasNext())
        {
            sm.destroySceneNode(iterator.next().toString());
        }
        gameObjectsToRemove.clear();
    }
	
	private class SendCloseConnectionPacketAction extends AbstractInputAction
	{ // for leaving the game... need to attach to an input device
		@Override
		public void performAction(float time, Event evt)
		{ 
			if(protClient != null && isClientConnected == true)
			{ 
				protClient.sendByeMessage();
			} 
		} 
	}
	
	@Override
	protected void update(Engine engine)
	{
		SkeletalEntity avatarE = (SkeletalEntity) engine.getSceneManager().getEntity("manAvatar");
		SceneNode avatarN = sm.getSceneNode("manAvatarNode");
		rs = (GL4RenderSystem) engine.getRenderSystem();
		int height = topViewport.getActualHeight();
		float time = engine.getElapsedTimeMillis();
		if (running)
		{ 
			Matrix4 mat;
			physicsEngine.update(time);
			for (SceneNode s : engine.getSceneManager().getSceneNodes())
			{ 
				if (s.getPhysicsObject() != null)
				{	 
					mat = Matrix4f.createFrom(toFloatArray(s.getPhysicsObject().getTransform()));
					s.setLocalPosition(mat.value(0,3),mat.value(1,3),mat.value(2,3));
				}
			}
		}
		elapsTime += engine.getElapsedTimeMillis();
		elapsTimeSec = Math.round(elapsTime/1000.0f);
		elapsTimeStr = Integer.toString(elapsTimeSec);
		scoreStr1 = Integer.toString(score1);


		orbitController1.updateCameraPosition();
		orbitController2.updateCameraPosition();
		
		im.update(elapsTime);
		
		updateVerticalPosition();
		
		avatarE.update();
		processNetworking(elapsTime);
		
		bgSound.setLocation(avatarN.getWorldPosition());
		setEarParameters(sm);
	}

	
}