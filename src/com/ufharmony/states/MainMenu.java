package com.ufharmony.states;

import org.bushe.swing.event.EventTopicSubscriber;

import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.ufharmony.Main;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Menu;
import de.lessvoid.nifty.controls.MenuItemActivatedEvent;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.SizeValue;

public class MainMenu extends AbstractAppState
{
	private Nifty nifty;
	private Main app;
	
	private ViewPort viewPort;
	private Node rootNode;
	private Node guiNode;
	private AssetManager assetManager;
	private Node localRootNode = new Node( "Start Screen RootNode" );
	private Node localGuiNode = new Node( "Start Screen GuiNode" );
	private final ColorRGBA backgroundColor = ColorRGBA.Gray;
	
	public MainMenu( Main app )
	{
		this.app = app;
		rootNode = app.getRootNode();
		viewPort = app.getViewPort();
		guiNode = app.getGuiNode();
		assetManager = app.getAssetManager();
	}
	
	@Override
	public void update( float tpf )
	{
		/** any main loop action happens here */
	}
	
	@Override
	public void stateAttached( AppStateManager stateManager )
	{
		rootNode.attachChild( localRootNode );
		guiNode.attachChild( localGuiNode );
		viewPort.setBackgroundColor( backgroundColor );
		
		app.setPauseOnLostFocus( false );
		
		/*
		 * Box b = new Box( Vector3f.ZERO, 1, 1, 1 ); Geometry geom = new Geometry( "Box", b ); Material mat = new
		 * Material( Main.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md" ); mat.setTexture(
		 * "ColorMap", Main.getInstance().getAssetManager().loadTexture( "Interface/Logo/Monkey.jpg" ) );
		 * geom.setMaterial( mat ); Main.getInstance().getRootNode().attachChild( geom );
		 */
		
		NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay( Main.getInstance().getAssetManager(), Main.getInstance().getInputManager(), Main.getInstance().getAudioRenderer(), Main.getInstance().getGuiViewPort() );
		nifty = niftyDisplay.getNifty();
		nifty.fromXml( "Interface/HomeScreen/layout.xml", "start" );
		
		// attach the nifty display to the gui view port as a processor
		app.getGuiViewPort().addProcessor( niftyDisplay );
		
		app.getFlyByCamera().setEnabled( false );
		app.getFlyByCamera().setDragToRotate( true );
		app.getInputManager().setCursorVisible( true );
	}
	
	@Override
	public void stateDetached( AppStateManager stateManager )
	{
		rootNode.detachChild( localRootNode );
		guiNode.detachChild( localGuiNode );
	}
	
	public void bind( Nifty nifty, Screen screen )
	{
		System.out.println( "bind( " + screen.getScreenId() + ")" );
	}
	
	public void onStartScreen()
	{
		System.out.println( "onStartScreen" );
	}
	
	public void onEndScreen()
	{
		System.out.println( "onEndScreen" );
	}
	
	public void quit()
	{
		showMenu();
		
		nifty.gotoScreen( "end" );
	}
	
	private Element popup;
	
	public void createMyPopupMenu()
	{
		popup = nifty.createPopup( "niftyPopupMenu" );
		Menu myMenu = popup.findNiftyControl( "#menu", Menu.class );
		myMenu.setWidth( new SizeValue( "100px" ) ); // must be set
		myMenu.addMenuItem( "Click me!", "menuItemIcon.png", new menuItem( "menuItemid", "blah blah" ) ); // menuItem is a
																																			// custom class
		nifty.subscribe( nifty.getCurrentScreen(), myMenu.getId(), MenuItemActivatedEvent.class, new MenuItemActivatedEventSubscriber() );
	}
	
	public void showMenu()
	{
		createMyPopupMenu();
		nifty.showPopup( nifty.getCurrentScreen(), popup.getId(), null );
	}
	
	private class menuItem
	{
		public String id;
		public String name;
		
		public menuItem(String id, String name)
		{
			this.id = id;
			this.name = name;
		}
	}
	
	private class MenuItemActivatedEventSubscriber implements EventTopicSubscriber<MenuItemActivatedEvent>
	{
		
		@Override
		public void onEvent( final String id, final MenuItemActivatedEvent event )
		{
			menuItem item = (menuItem) event.getItem();
			if ( "menuItemid".equals( item.id ) )
			{
				// do something !!!
			}
		}
	};
}
