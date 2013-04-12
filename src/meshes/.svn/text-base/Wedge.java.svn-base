package meshes;

import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

public class Wedge extends Mesh
{

    public enum Orientation
    {

        LEFT, RIGHT, UP, DOWN
    };

    public Wedge(Orientation orientation, Vector3f bounds, boolean isExtruded)
    {
        updateGeometry(orientation, bounds, isExtruded);
    }

    /**
     * Updates the geometry with the given parameters. Bounds are expressed as
     * if the arrow was pointing to the right, i.e.: x = length of the arrow
     * (distance to the pointy bit.) y = height of the arrow z = extrusion
     * length.
     */
    public void updateGeometry(Orientation orientation, Vector3f bounds, boolean isExtruded)
    {
        // Create three points, according to the size and orientation.
        Vector3f p0 = new Vector3f(), p1, p2;

        switch (orientation)
        {
            case LEFT:
                p1 = new Vector3f(0, bounds.y, 0);
                p2 = new Vector3f(-bounds.x, bounds.y / 2, 0);
                break;
            case RIGHT:
                p1 = new Vector3f(bounds.x, bounds.y / 2, 0);
                p2 = new Vector3f(0, bounds.y, 0);
                break;
            case UP:
                p1 = new Vector3f(bounds.y, 0, 0);
                p2 = new Vector3f(bounds.y / 2, bounds.x, 0);
                break;
            case DOWN:
                p1 = new Vector3f(bounds.y / 2, -bounds.x, 0);
                p2 = new Vector3f(bounds.y, 0, 0);
                break;
            default:
                p2 = p1 = p0;
        }


        // Then, make a list of vertices and indices.
        Vector3f[] vertices, normals;
        int[] indices;
        if (!isExtruded)
        {
            // For a flat triangle, easy one, just take the initial point and link them.
            vertices = new Vector3f[3];
            vertices[0] = p0;
            vertices[1] = p1;
            vertices[2] = p2;

            // The normals are all along Z.
            normals = new Vector3f[3];
            normals[0] = Vector3f.UNIT_Z;
            normals[1] = Vector3f.UNIT_Z;
            normals[2] = Vector3f.UNIT_Z;

            indices = new int[]
            {
                0, 1, 2
            };

        } else
        {
            // For an extruded triangle
            vertices = new Vector3f[18];
            // First (front) triangle is regular.
            vertices[0] = p0;
            vertices[1] = p1;
            vertices[2] = p2;
            // Back triangle is easy too: retake first triangle and shift the z-coordinate.
            vertices[3] = new Vector3f(p0.x, p0.y, bounds.z);
            vertices[4] = new Vector3f(p1.x, p1.y, bounds.z);
            vertices[5] = new Vector3f(p2.x, p2.y, bounds.z);

            // Then duplicate side vertices to allow hard edges normals, resulting in 4 quads.
            vertices[6] = vertices[0];
            vertices[7] = vertices[3];
            vertices[8] = vertices[1];
            vertices[9] = vertices[4];

            vertices[10] = vertices[1];
            vertices[11] = vertices[4];
            vertices[12] = vertices[2];
            vertices[13] = vertices[5];

            vertices[14] = vertices[2];
            vertices[15] = vertices[5];
            vertices[16] = vertices[0];
            vertices[17] = vertices[3];

            // Normals:
            normals = new Vector3f[18];

            // The front side normal is still the same as for a flat.
            normals[0] = Vector3f.UNIT_Z;
            normals[1] = Vector3f.UNIT_Z;
            normals[2] = Vector3f.UNIT_Z;

            // Backside is the same, but reversed (d'uh).
            normals[3] = Vector3f.UNIT_Z.negate();
            normals[4] = normals[3];
            normals[5] = normals[3];

            // Finally, sides normals are computed from the triangle sides. 
            Vector3f normal1 = new Vector3f(p1.y - p0.y, p0.x - p1.x, 0);
            Vector3f normal2 = new Vector3f(p2.y - p1.y, p1.x - p2.x, 0);
            Vector3f normal3 = new Vector3f(p0.y - p2.y, p2.x - p0.x, 0);

            normals[6] = normal1;
            normals[7] = normal1;
            normals[8] = normal1;
            normals[9] = normal1;

            normals[10] = normal2;
            normals[11] = normal2;
            normals[12] = normal2;
            normals[13] = normal2;

            normals[14] = normal3;
            normals[15] = normal3;
            normals[16] = normal3;
            normals[17] = normal3;

            // Finally, the indices.
            indices = new int[]
            {
                // Front
                0, 1, 2,
                // Back
                5,4,3,
                // Sides
                6,7,8,9,8,7,
                10,11,12,13,12,11,
                14,15,16,17,16,15
            };

        }

        // Set the mesh data
        setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(normals));
        setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indices));

        updateBound();
        //setStatic();
    }
}
