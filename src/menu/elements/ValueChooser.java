package menu.elements;

import com.jme3.input.controls.ActionListener;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import java.util.ArrayList;
import java.util.List;
import menu.utils.Jme3DFont;
import meshes.Wedge;

/**
 * A ValueChooser lets the user select, using arrows on either side to select
 * values.
 */
public class ValueChooser<T> extends Label
{

    private int index = -1;
    private List<T> values;
    private boolean vertical;
    private ArrayList<ActionListener> actionListeners = new ArrayList<ActionListener>();

    public ValueChooser(Jme3DFont font, List<T> values, boolean vertical)
    {
        super("");
        setName("ValueChooser");
        if (values == null)
        {
            throw new NullPointerException();
        }

        this.vertical = vertical;
        this.values = values;

        // If at least one value is given, set the first:
        if (!values.isEmpty())
        {
            index = 0;
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

        // Set the selection as text:
        this.text = values.get(index).toString();
        super.refresh();

        // Create the triangles.
        Geometry wedge1, wedge2;
        boolean extruded = getMenuFont().isExtruded();


        Vector3f minBound = super.getLocalMinBound();
        Vector3f maxBound = super.getLocalMaxBound();
        float height = maxBound.y;
        float width = maxBound.x;
        float extrusion = minBound.z;

        if (vertical)
        {
            Wedge w = new Wedge(Wedge.Orientation.DOWN, new Vector3f(width / 3, width, extrusion), extruded);
            wedge1 = new Geometry("wedge1", w);
            w = new Wedge(Wedge.Orientation.UP, new Vector3f(width / 3, width, extrusion), extruded);
            wedge2 = new Geometry("wedge2", w);

            wedge1.setLocalTranslation(0, 0, 0);
            wedge2.setLocalTranslation(0, height, 0);
        } else
        {
            Wedge w = new Wedge(Wedge.Orientation.LEFT, new Vector3f(height / 3, height, extrusion), extruded);
            wedge1 = new Geometry("wedge1", w);
            w = new Wedge(Wedge.Orientation.RIGHT, new Vector3f(height / 3, height, extrusion), extruded);
            wedge2 = new Geometry("wedge2", w);

            wedge1.setLocalTranslation(0, 0, 0);
            wedge2.setLocalTranslation(width, 0, 0);
        }
        Material mat = getMenuMaterial();
        wedge1.setMaterial(mat);
        wedge2.setMaterial(mat);

        attachChild(wedge1);
        attachChild(wedge2);

    }

    @Override
    public void processClick(boolean pressedOrReleased, Vector3f cursorPosition)
    {
        if (pressedOrReleased)
        {
            Vector3f minBound = super.getLocalMinBound();
            Vector3f maxBound = super.getLocalMaxBound();
            if (vertical)
            {
                if (cursorPosition.y > maxBound.y)
                {
                    setIndex(index + 1);
                } else if (cursorPosition.y < minBound.y)
                {
                    setIndex(index - 1);
                }
            } else
            {
                if (cursorPosition.x > maxBound.x)
                {
                    setIndex(index + 1);
                } else if (cursorPosition.x < minBound.x)
                {
                    setIndex(index - 1);
                }
            }

            // If the button has been released, and the button is enabled:
            if (enabled && !pressedOrReleased)
            {
                for (ActionListener listener : actionListeners)
                {
                    listener.onAction(name, true, queueDistance);
                }
            }
            // index++; refresh();
        }
    }

    /**
     * Sets the given index as selected.
     */
    public void setIndex(int index)
    {
        if (index < 0)
        {
            index = index % values.size() + values.size();
        }

        this.index = index % values.size();
        refresh();
    }

    @Override
    public void setMaterial(Material mat)
    {
        super.setMaterial(mat);

        //  wedge1.setMaterial(mat);
        // wedge2.setMaterial(mat);
    }
}
