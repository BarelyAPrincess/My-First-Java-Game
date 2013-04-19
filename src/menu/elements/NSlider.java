package menu.elements;

import com.jme3.input.controls.ActionListener;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import java.awt.Color;
import java.util.ArrayList;
import menu.utils.Materials;

/**
 * A N-Slider is a slider that allows to adjust N values. Your usual slider is a
 * 1-Slider.
 */
public class NSlider extends MenuElement
{

    private ArrayList<ActionListener> actionListeners = new ArrayList<ActionListener>();
    private float minValue = 0.05f;
    private int n = 0;
    private int draggedValue = -1;
    private float values[];
    private Box boxes[];
    // A slider is 1.5f units long by default.
    private static final float baseLength = 1.5f;
    // 10-slider max. TODO: turn it into a map to improve scalability
    private static Material materials[][] =
    {
        null, null, null, null, null, null, null, null, null, null
    };

    public NSlider(int n)
    {
        // First off, if the materials are not initialized, init them.
        if (materials[n] == null)
        {
            initMaterials(n);
        }

        // Set a material as used so that the panel won't override it.
        setMaterial(materials[n][0]);

        this.n = n;
        // N-1 values only. If you have two boxes, you only get one value.
        values = new float[n];
        boxes = new Box[n + 1];


        for (int i = 0; i < n; i++)
        {
            // Default value: (i+1)/n for each.
            values[i] = (i + 1f) / (n + 1);
        }

        for (int i = 0; i < (n + 1); i++)
        {
            // Create the N boxes.
            boxes[i] = new Box(1, 1, 1);

            // Attach the box
            Geometry geometry = new Geometry();
            geometry.setMesh(boxes[i]);

            // Set the relevant material
            geometry.setMaterial(materials[n][i]);

            // Objects with transparency need to be in the render bucket for transparent objects:
            geometry.setQueueBucket(Bucket.Transparent);

            attachChild(geometry);
        }

        refresh();

        setName("Slider");
    }

    public void addActionListener(ActionListener listener)
    {
        actionListeners.add(listener);
    }

    @Override
    public void setMaterial(Material mat)
    {
        // Can't be changed for now.
    }

    private void setDraggedValue(float newValue)
    {

        // Set the dragged value to the cursor current position (if possible).
        float previousValue = draggedValue == 0 ? 0f : getValues()[draggedValue - 1];
        float nextValue = draggedValue == (n - 1) ? 1f : getValues()[draggedValue + 1];

        // If the new value is "too close" to the adjacent value, snap it to the edge.
        if (newValue - previousValue > minValue)
        {
            if (nextValue - newValue > minValue)
            {
                values[draggedValue] = newValue;
            } else
            {
                values[draggedValue] = nextValue - minValue;
            }
        } else
        {
            values[draggedValue] = previousValue + minValue;
        }

        // Refresh the boxes display.
        refresh();

        // Fire the listeners.
        for (ActionListener listener : actionListeners)
        {
            listener.onAction(name, true, queueDistance);
        }
    }

    @Override
    public void processClick(boolean pressedOrReleased, Vector3f cursorPosition)
    {
        if (pressedOrReleased)
        {
            // Get the local X coord of the pointer.
            // First, clamp the value to [0,1].
            float value = cursorPosition.x / baseLength;
            value = Math.min(value, 1f);
            value = Math.max(value, 0f);


            // Mark as "dragged" the relevant value.
            float distance = 10f;
            for (int i = 0; i < n; i++)
            {
                float currentDistance = Math.abs(getValues()[i] - value);
                if (currentDistance < distance)
                {
                    distance = currentDistance;
                    draggedValue = i;
                }
            }

            // Immediately set the value clicked, if possible.
            setDraggedValue(value);
        } else
        {
            // "Release" the dragged value
            // draggedValue = -1;
        }

    }

    @Override
    public void processDrag(Vector3f cursorPosition)
    {
        super.processDrag(cursorPosition);

        if (draggedValue != -1)
        {
            // First, clamp the value to [0,1].
            float value = cursorPosition.x / baseLength;
            value = Math.min(value, 1f);
            value = Math.max(value, 0f);
            setDraggedValue(value);
        }
    }

    @Override
    public void processWheel(int step)
    {
        if (draggedValue != -1)
        {
            setDraggedValue(values[draggedValue] + step * 0.05f);
        }
    }

    /**
     * Refreshes the box positions according to the selected values.
     */
    @Override
    protected void refresh()
    {
        // First Box:
        boxes[0].updateGeometry(new Vector3f(0f, 0f, 0f), new Vector3f(baseLength * getValues()[0], 0.5f, -0.5f));

        // Middle Boxes:
        for (int i = 0; i < n - 1; i++)
        {
            boxes[i + 1].updateGeometry(new Vector3f(baseLength * getValues()[i], 0f, 0f), new Vector3f(baseLength * getValues()[i + 1], 0.5f, -0.5f));
        }

        // Last Box:
        boxes[n].updateGeometry(new Vector3f(baseLength * getValues()[n - 1], 0f, 0f), new Vector3f(baseLength, 0.5f, -0.5f));

    }

    /**
     * In order to color each box in a bright color, make a set of "rainbow"
     * transparent materials.
     */
    private static void initMaterials(int n)
    {
        materials[n] = new Material[n + 1];

        for (int i = 0; i < n + 1; i++)
        {
            materials[n][i] = Materials.transparentMaterial.clone();
            // Use AWT color to convert from convenient HSB to RGB.
            Color awtColor = new Color(Color.HSBtoRGB(i * 1f / (n + 1), 0.8f, 1f));
            ColorRGBA color = new ColorRGBA(awtColor.getRed() * 1f / 255, awtColor.getGreen() * 1f / 255, awtColor.getBlue() * 1f / 255, 0.7f);
            materials[n][i].setColor("Color", color);
        }
    }

    /**
     * @return the values
     */
    public float[] getValues()
    {
        return values;
    }

    @Override
    protected Vector3f getLocalMinBound()
    {
        Vector3f result = new Vector3f(0, 0, -0.5f);

        return result;
    }

    @Override
    protected Vector3f getLocalMaxBound()
    {
        Vector3f result = new Vector3f(baseLength, 0.5f, 0);

        return result;
    }
}
