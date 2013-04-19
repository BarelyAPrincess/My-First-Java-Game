package menu.elements;

import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A rotating carousel to display and choose elements (nodes).
 */
public class Carousel<T extends Node> extends MenuElement
{
    private ArrayList<T> elements = new ArrayList<T>();
    private float radius = 2f;
    private int currentIndex = 0;
    private boolean horizontal = true;

    public Carousel(boolean horizontal)
    {
        this.horizontal = horizontal;
        //Push the node back so that the front node will end up at Z = 0.
        setLocalTranslation(0, 0, -radius);
    }

    /**
     * Adds an element to the list and refreshes the nodes.
     */
    public void addElements(T... elementsToAdd)
    {
        elements.addAll(Arrays.asList(elementsToAdd));

        refresh();
    }

    /**
     * Refreshes the nodes on the carousel.
     */
    @Override
    protected void refresh()
    {
        detachAllChildren();

        // Add a small rotation to see the node "from above".
        setLocalRotation(Quaternion.IDENTITY.clone().fromAngles(0, 0.002f, 0));

        // Prepare a rotation matrix and a simple vector.
        Quaternion q = new Quaternion(Quaternion.IDENTITY);
        // The carousel is around X or Y axis, according to the orientation.
        if (horizontal)
        {
            q.fromAngles(0, FastMath.TWO_PI / elements.size(), 0);
        }
        else
        {
            q.fromAngles(FastMath.TWO_PI / elements.size(), 0, 0);
        }


        Matrix3f m = q.toRotationMatrix();
        Vector3f vector = new Vector3f(0, 0, radius);
        Vector3f up = new Vector3f(0, 1, 0);

        for (T element : elements)
        {
            attachChild(element);
            element.setLocalTranslation(vector);
            element.rotateUpTo(up);
            // Rotate the vector each time.
            m.multLocal(vector);
            m.multLocal(up);
        }
    }

    /**
     * Updates the carousel (orient it selected node - front, and make the
     * selected node rotate. )
     */
    @Override
    public void update(float tpf)
    {
        // On updating, make the carousel reach the right position:
        float angleTarget = -currentIndex * FastMath.TWO_PI / elements.size();
        // substract current position by selecting the relevant angle rotation:
        if (horizontal)
        {
            angleTarget -= getLocalRotation().toAngles(null)[1];
        }
        else
        {
            angleTarget -= getLocalRotation().toAngles(null)[0];
        }


        // Simplify too large rotations
        if (angleTarget > FastMath.PI)
        {
            angleTarget = angleTarget - FastMath.TWO_PI;
        }
        else if (angleTarget < -FastMath.PI)
        {
            angleTarget = FastMath.TWO_PI + angleTarget;
        }

        // Avoid useless movements.
        if (FastMath.abs(angleTarget) > 0.01f)
        {
            // Finally, rotate the carousel towards the front.
            if (horizontal)
            {
                rotate(0,angleTarget * tpf * 5, 0);
            }
            else
            {
                 rotate(angleTarget * tpf * 5, 0, 0);
            }    
        }

        // Rotate the active node.
        elements.get(currentIndex).rotate(0, tpf, 0);
    }

    /**
     * Returns a new instance of the currently selected element.
     */
    public T getNewInstanceOfSelection()
    {
        return (T) elements.get(currentIndex).clone();
    }

    /**
     * Returns the currently selected element.
     */
    public T getSelected()
    {
        return (T) elements.get(currentIndex);
    }

    @Override
    public void processWheel(int step)
    {
        currentIndex += step;
        currentIndex %= elements.size();
        // If the modulus is negative:
        if (currentIndex < 0)
        {
            currentIndex += elements.size();
        }
    }

    @Override
    protected Vector3f getLocalMinBound()
    {
        return new Vector3f(-0.1f, -radius, -radius);
    }

    @Override
    protected Vector3f getLocalMaxBound()
    {
        return new Vector3f(0.1f, radius, radius);
    }
}
