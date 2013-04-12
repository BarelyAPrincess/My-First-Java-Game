package menu.elements;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import menu.utils.Jme3DFont;

/**
 * A label is a simple 3D text, intialized from the given text.
 */
public class Label extends MenuElement
{

    protected String text;
    private Node stringNode;

    public Label(String text)
    {
        setName("Label");
        this.text = text;
    }

    /**
     * Turns the given text into a mesh, that will be the label's physical
     * representation.
     */
    public void setText(String text)
    {
        this.text = text;
        refresh();
    }

    @Override
    public void setMaterial(Material mat)
    {
        // Only change the string material.
        if (stringNode != null)
        {
            stringNode.setMaterial(mat);
        }
    }

    /* Regenerates the mesh. */
    @Override
    public void refresh()
    {
        // Discard the former string node.
        if (stringNode != null && hasChild(stringNode))
        {
            detachChild(stringNode);
        }

        // Generate and attach the text mesh.
        stringNode = getMenuFont().getStringNode(text);

        // If there is a material set, apply it.
        Material mat = getMenuMaterial();

        if (mat != null)
        {
            setMaterial(mat);
        }

        attachChild(stringNode);
    }

    @Override
    protected Vector3f getLocalMinBound()
    {
        if (getMenuFont().isExtruded())
        {
            return new Vector3f(0,0,-0.2f);
        } else
        {
            return Vector3f.ZERO;
        }
    }

    @Override
    protected Vector3f getLocalMaxBound()
    {
        Jme3DFont currentFont = getMenuFont();
        Vector3f result = new Vector3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
        for (char c : text.toCharArray())
        {
            Vector3f glyphSize = currentFont.getGlyphSize(c);
            result.x += glyphSize.x * Jme3DFont.spacingRatio;
            result.y = Math.max(result.y, glyphSize.y);
            result.z = Math.max(result.z, glyphSize.z);
        }

        return result;
    }
}
