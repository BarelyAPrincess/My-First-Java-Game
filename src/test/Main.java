package test;

import com.jme3.app.SimpleApplication;
import com.jme3.input.controls.ActionListener;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import menu.elements.Button;
import menu.elements.Carousel;
import menu.elements.Label;
import menu.elements.NSlider;
import menu.elements.Panel;
import menu.elements.ValueChooser;
import menu.transitions.DirectTransition;
import menu.utils.Jme3DFont;

/**
 * test
 * 
 * @author normenhansen
 */
public class Main extends SimpleApplication
{
	
	private Panel mainPanel = new Panel();
	
	public static void main( String[] args )
	{
		Logger.getLogger( "" ).setLevel( Level.SEVERE );
		Main app = new Main();
		app.start();
	}
	
	@Override
	public void simpleInitApp()
	{
		// Enable the cursor.
		flyCam.setEnabled( false );
		inputManager.setCursorVisible( true );
		
		// Init the 3D menu. This should be done for each new menu root.
		mainPanel.register( this );
		
		// Also set straight ahead the panel default material - it can
		// be overriden by elements inside this panel.
		Material simpleLightMaterial = new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md" );
		mainPanel.setMenuMaterial( simpleLightMaterial );
		// Same thing for the font.
		mainPanel.setMenuFont( Jme3DFont.standardExtrudedFont );
		
		// Attach the menu node.
		rootNode.attachChild( mainPanel );
		mainPanel.setLocalTranslation( 0, 0, 0 );
		
		// Add a title label.
		Label label = new Label( "3D Menu Demo" );
		mainPanel.add( label );
		// Scale it down a bit.
		label.scale( 0.7f );
		// Center it and move it to the top of the menu.
		label.setLocalTranslation( label.getRelativeMaxBound().x * -0.5f, 2.5f, 0 );
		
		// Add two panels. The first will trigger a transition to the second.
		final Panel panel1 = new Panel();
		final Panel panel2 = new Panel();
		mainPanel.add( panel1 );
		panel1.setLocalTranslation( -2f, 0, 0 );
		
		// Add a carousel to the right.
		Carousel<Node> carousel = new Carousel( true );
		// Add 3 boxes to the carousel.
		for ( char c : "Carousel".toCharArray() )
		{
			Node node = new Node();
			Geometry geometry = new Geometry();
			// Add a letter of the word "carousel".
			geometry.setMesh( Jme3DFont.standardExtrudedFont.getGlyphMesh( c ) );
			// Center it.
			geometry.getLocalTranslation().subtractLocal( Jme3DFont.standardExtrudedFont.getGlyphSize( c ).mult( 0.5f ) );
			geometry.setMaterial( simpleLightMaterial );
			node.attachChild( geometry );
			
			node.attachChild( node );
			carousel.addElements( node );
		}
		carousel.setLocalTranslation( 2f, 0, 0 );
		panel1.add( carousel );
		
		panel1.setMenuFont( Jme3DFont.standardFlatFont );
		
		// Also add a button to trigger a transition.
		Button button = new Button( "Button test!" );
		// Move the label on the left of the menu.
		button.setLocalTranslation( -3f, 1f, 0 );
		button.scale( 0.5f, 0.4f, 0.5f );
		// Add the button to the first panel.
		panel1.add( button );
		
		// Add a listener, so that a click on the button will trigger a transition.
		button.addActionListener( new ActionListener()
		{
			@Override
			public void onAction( String name, boolean isPressed, float tpf )
			{
				// On clicking, start the transition.
				mainPanel.addTransition( new DirectTransition( panel1, panel2 ) );
			}
		} );
		
		// Create the 2nd panel.
		// Add a label.
		final Label slideLabel = new Label( "<= Move this!" );
		// Scale it down a bit.
		slideLabel.scale( 0.5f );
		
		// Center it and move it to the top of the menu.
		slideLabel.setLocalTranslation( -2, 0, 0 );
		panel2.add( slideLabel );
		
		// Add a standard slider.
		final NSlider slider = new NSlider( 2 );
		slider.setLocalTranslation( -4, 0, 0 );
		panel2.add( slider );
		slider.addActionListener( new ActionListener()
		{
			@Override
			public void onAction( String name, boolean isPressed, float tpf )
			{
				// When the slider is moved, change the label string.
				slideLabel.setText( String.format( "Value : %1.2f", slider.getValues()[0] ) );
			}
		} );
		
		ArrayList<Integer> ints = new ArrayList<Integer>();
		for ( int i = 0; i < 10; i++ )
		{
			ints.add( i );
		}
		
		ValueChooser v = new ValueChooser( Jme3DFont.standardExtrudedFont, ints, true );
		v.setLocalTranslation( -1, -3, 0 );
		panel2.add( v );
		
		v = new ValueChooser( Jme3DFont.standardExtrudedFont, ints, false );
		v.setLocalTranslation( 2, -3, 0 );
		panel2.add( v );
		
		// Add a sun (on the right)
		DirectionalLight sun = new DirectionalLight();
		sun.setColor( ColorRGBA.White );
		sun.setDirection( new Vector3f( 0.5f, 0, -0.8f ).normalizeLocal() );
		rootNode.addLight( sun );
		
		// Add an ambient light to see a minimum of things
		AmbientLight al = new AmbientLight();
		al.setColor( ColorRGBA.White.mult( 0.7f ) );
		rootNode.addLight( al );
	}
	
	/**
	 * The menu root needs to be updated, so that moving objects can be refreshed.
	 */
	float x = 0f;
	
	@Override
	public void simpleUpdate( float tpf )
	{
		super.simpleUpdate( tpf );
		mainPanel.update( tpf );
		
		// To add to the 3D effect, make the menu swing back and forth.
		x += tpf;
		mainPanel.setLocalRotation( new Quaternion().fromAngles( 0.2f * FastMath.sin( x ), 0.2f * FastMath.cos( x ), 0 ) );
		// mainPanel.getLocalRotation().set(0.4f*FastMath.sin(x % FastMath.PI)+1, 0, 0, 1);
		
		mainPanel.update( tpf );
		
		// mainPanel.setLocalTranslation( player.getPhysicsLocation().add( cam.getDirection().mult( 20 ) ) );
		
		// mainPanel.setLocalRotation( cam.getRotation() );
	}
}
