package menu.elements;

import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import java.util.ArrayList;
import menu.utils.Materials;

/**
 * A Button is a clickable Label, with an invisible box to catch clicks between
 * characters.
 */
public class Button extends Label
{
    private ArrayList<ActionListener> actionListeners = new ArrayList<ActionListener>();

    /**
     * Creates a button with the specified text as a label.
     */
    public Button(String text)
    {
        super(text);
        setName("Button");
    }

    /**
     * When clicked, fire all action listeners.
     */
    @Override
    public void processClick(boolean pressedOrReleased, Vector3f cursorPosition)
    {
        // If the button has been released, and the button is enabled:
        if (enabled && !pressedOrReleased)
        {
            for (ActionListener listener : actionListeners)
            {
                listener.onAction(name, true, queueDistance);
            }
        }
    }

    /**
     * Adds an action listener to this button, that will be fired at each click.
     */
    public void addActionListener(ActionListener listener)
    {
        actionListeners.add(listener);
    }

    @Override
    public void refresh()
    {
        // Delete everything, not just the label.
        detachAllChildren();
        // Refresh the label...
        super.refresh();

        // Then (re-)create an invisible Box or Quad to intercept clicks.
        Geometry geom = null;
        if (getMenuFont().isExtruded())
        {
            // Generate an invisible box to intercept clicks.
            Box invisibleBox = new Box(getLocalMinBound(), getLocalMaxBound());
            geom = new Geometry("invisibleBox", invisibleBox);

        }
        else
        {
            Vector3f maxBound = getLocalMaxBound();
            // For a 2D font, only create a quad.
            Quad invisibleQuad = new Quad(maxBound.x, maxBound.y);
            geom = new Geometry("invisibleQuad", invisibleQuad);

        }

        geom.setMaterial(Materials.invisibleMaterial);
        attachChild(geom);
    }
}
