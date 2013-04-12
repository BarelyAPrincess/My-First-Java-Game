package menu.utils;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;

public class Materials
{
    // A general-purpose invisible material.
    public static Material invisibleMaterial = null;
    public static Material transparentMaterial = null;

    public static void initMaterials(AssetManager assetManager)
    {
        // If the materials haven't been loaded yet, load them.
        if (invisibleMaterial == null)
        {
            invisibleMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            invisibleMaterial.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.FrontAndBack);

            transparentMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            transparentMaterial.setColor("Color", new ColorRGBA(1, 1, 1, 0.1f));
            transparentMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        }
    }
}
