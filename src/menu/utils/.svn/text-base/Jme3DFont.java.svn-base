package menu.utils;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.GeometryArray;

/**
 * This class generates and lazyloads meshes corresponding to glyphs. One
 * instance corresponds to one Font.
 */
public class Jme3DFont
{
    // Usual fonts.

    public final static Jme3DFont standardExtrudedFont = new Jme3DFont("Arial", true);
    public final static Jme3DFont standardFlatFont = new Jme3DFont("Arial", false);
    // The ratio used to increase distance between characters.
    public final static float spacingRatio = 1.05f;
    // The Java3D 3DFont.
    private Font3D font3D;
    // This map allows to lasyload meshes, holding one mesh per character.
    private Map<Character, Mesh> meshesMap = new HashMap<>();
    // This map stores each character (3D, hence the vector3f) size.
    private Map<Character, Vector3f> sizeMap = new HashMap<>();
    // Determines wether this font is extruded or flat.
    private boolean extruded;

    /**
     * Convenience method to load a font from its name.
     *
     * @param fontName The name of the font used (E.g. "Arial").
     * @param extruded If true, the mesh will be a true 3D letter, else it
     * will be flat and only contain the front of the letter.
     *
     */
    public Jme3DFont(String fontName, boolean extruded)
    {
        this(new Font(fontName, Font.PLAIN, 2), extruded);
    }

    /**
     * Creates a FontToMesh from a java font.
     *
     * @param font The java.awt font to use.
     * @param extruded If true, the mesh will be a true 3D letter, else it
     * will be flat and only contain the front of the letter.
     *
     */
    public Jme3DFont(Font font, boolean extruded)
    {
        font3D = new Font3D(font,
                new FontExtrusion());
        this.extruded = extruded;

        // For the character ' ', set a null geometry...
        meshesMap.put(' ', null);
        // ... With a non-null size
        sizeMap.put(' ', new Vector3f(0.9f, 0, 0));
    }

    /**
     * Gets the mesh for every character, then returns the global geometry, for
     * the given string.
     */
    public Node getStringNode(String string)
    {
        Node node = new Node();

        float offset = 0;
        for (char c : string.toCharArray())
        {
            // For each glyph, retrieve the mesh, space it a bit, add it to the geometry.
            Geometry glyphGeometry = new Geometry("Glyph");

            Mesh mesh = getGlyphMesh(c);
            if (mesh != null)
            {
                glyphGeometry.setMesh(mesh);
                glyphGeometry.setLocalTranslation(offset, 0, 0);

                node.attachChild(glyphGeometry);
            }

            // Increment the offset by the character width, plus a bit.
            offset += sizeMap.get(c).x * spacingRatio;
        }
        return node;
    }

    /**
     * Creates if needed, and returns the mesh for the given glyph.
     */
    public Mesh getGlyphMesh(char c)
    {
        // First check if it has already been computed.
        if (meshesMap.containsKey(c))
        {
            // If we found null, there's no mesh representation; fill the size anyway.
            return meshesMap.get(c);
        }

        // If not, create it. First retrieve all data:
        GeometryArray geometry = font3D.getGlyphGeometry(c);
        float coords[] = new float[geometry.getValidVertexCount() * 3];
        geometry.getCoordinates(0, coords);
        float normalCoords[] = new float[geometry.getValidVertexCount() * 3];
        geometry.getNormals(0, normalCoords);

        // Now, coords contains x,y,z for each vertex of each triangle; hence, 9 floats per triangle.
        // Separate those coordinates into 3 lists:
        ArrayList<Vector3f> frontVertices = new ArrayList<>();
        ArrayList<Vector3f> sideVertices = new ArrayList<>();
        ArrayList<Vector3f> backVertices = new ArrayList<>();
        // Keep track of side normals - if needed - and only them,
        // since front and back are along the Z axis.
        ArrayList<Vector3f> sideNormals = new ArrayList<>();

        // For each triangle (Triplet of points, hence 9-plet of coord): 
        for (int i = 0; i < coords.length; i += 9)
        {
            if (coords[i + 2] > 0.1f && coords[i + 5] > 0.1f && coords[i + 8] > 0.1f)
            {
                // If all the Zs are close to 0, put the triangle in the front category.
                frontVertices.add(new Vector3f(coords[i], coords[i + 1], coords[i + 2]));
                frontVertices.add(new Vector3f(coords[i + 3], coords[i + 4], coords[i + 5]));
                frontVertices.add(new Vector3f(coords[i + 6], coords[i + 7], coords[i + 8]));

            } else if (extruded)
            {
                if (coords[i + 2] < 0.1f && coords[i + 5] < 0.1f && coords[i + 8] < 0.1f)
                {
                    // If all the Zs are not close to 0, put the triangle in the back category.
                    backVertices.add(new Vector3f(coords[i], coords[i + 1], coords[i + 2]));
                    backVertices.add(new Vector3f(coords[i + 3], coords[i + 4], coords[i + 5]));
                    backVertices.add(new Vector3f(coords[i + 6], coords[i + 7], coords[i + 8]));
                } else
                {
                    // If the Zs are not equal, put the triangle in the side category.
                    sideVertices.add(new Vector3f(coords[i], coords[i + 1], coords[i + 2]));
                    sideVertices.add(new Vector3f(coords[i + 3], coords[i + 4], coords[i + 5]));
                    sideVertices.add(new Vector3f(coords[i + 6], coords[i + 7], coords[i + 8]));

                    // Also save the normals.
                    sideNormals.add(new Vector3f(normalCoords[i], normalCoords[i + 1], normalCoords[i + 2]));
                    sideNormals.add(new Vector3f(normalCoords[i + 3], normalCoords[i + 4], normalCoords[i + 5]));
                    sideNormals.add(new Vector3f(normalCoords[i + 6], normalCoords[i + 7], normalCoords[i + 8]));
                }
            }
        }
        // Register Normals.
        ArrayList<Vector3f> normals = new ArrayList<>();
        // For the front, put 0,0,1.
        for (int i = 0; i < frontVertices.size(); i++)
        {
            normals.add(Vector3f.UNIT_Z);
        }

        if (extruded)
        {
            // Add the side and back vertices along with the front ones.
            frontVertices.addAll(sideVertices);
            frontVertices.addAll(backVertices);

            // Save all normals together too.
            // Side normals:
            normals.addAll(sideNormals);

            // For the back, put 0,0,-1.
            Vector3f backNormal = new Vector3f(0, 0, -1f);
            for (int i = 0; i < backVertices.size(); i++)
            {
                normals.add(backNormal);
            }
        }

        // Now turn all that into a mesh.
        ArrayList<Integer> triangles = MeshUtils.trianglesFromVerticesList(frontVertices, normals);
        Mesh mesh = new Mesh();

        // Vertices. 
       Vector3f[] verticesArray = frontVertices.toArray(new Vector3f[0]);

        // If the font is extruded, push it back a little to make it
        // extruded outwards of the users view, so that all fonts are on the 
        //same plane.
        if (extruded)
        {
           for(Vector3f v : verticesArray)
           {
               v.z -= 0.2f;
           }
        }
        // Normals
        Vector3f[] normalsArray = normals.toArray(new Vector3f[0]);

        // Save max positions as the size.
        Vector3f charSize = new Vector3f();
        for (Vector3f v : verticesArray)
        {
            charSize.x = Math.max(charSize.x, v.x);
            charSize.y = Math.max(charSize.y, v.y);
            charSize.z = Math.max(charSize.z, v.z);
        }

        // Drop the triangles to an array.
        int[] triangleList = new int[triangles.size()];
        for (int j = 0; j < triangles.size(); j++)
        {
            triangleList[j] = triangles.get(j);
        }

        mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(verticesArray));
        mesh.setBuffer(VertexBuffer.Type.Normal, 3, BufferUtils.createFloatBuffer(normalsArray));
        mesh.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(triangleList));
        mesh.updateBound();
        mesh.setStatic();

        // Save the mesh for later use.
        meshesMap.put(c, mesh);
        // Also save the character width.
        sizeMap.put(c, charSize);
        return mesh;
    }

    /**
     * Load font from a resource file, ie in a source package.
     */
    public static Font loadTtfFont(String name)
    {
        Font f = null;
        try
        {
            InputStream is = Jme3DFont.class.getResourceAsStream(name);
            f = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException | IOException ex)
        {
            Logger.getLogger(Jme3DFont.class.getName()).log(Level.SEVERE, null, ex);
        }

        return f;
    }

    public Vector3f getGlyphSize(char c)
    {
        return sizeMap.get(c);
    }

    /**
     * @return the isExtruded
     */
    public boolean isExtruded()
    {
        return extruded;
    }
}
