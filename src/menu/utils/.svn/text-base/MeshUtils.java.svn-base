package menu.utils;

import com.jme3.math.Vector3f;
import java.util.ArrayList;

public class MeshUtils
{

    /**
     * Takes a list of vertices as input, ordered to form a triangle list
     * (t1,t2,t3; u1,u2,u3...) and removes duplicates. Then return a list of
     * indices that form the triangles. If the normals are given, it also checks
     * if the normals are the same to remove duplicates.
     */
    public static ArrayList<Integer> trianglesFromVerticesList(ArrayList<Vector3f> rawVertices, ArrayList<Vector3f> rawNormals)
    {
        ArrayList<Integer> triangles = new ArrayList<>();
        ArrayList<Vector3f> refinedVertices = new ArrayList<>();
        ArrayList<Vector3f> refinedNormals = new ArrayList<>();

        // For each vertex: if it already exists in the refined array, simply 
        //add the corresponding index. else add the vertex to the list.
        for (int i = 0; i < rawVertices.size(); i++)
        {
            int foundIndex = -1;
            Vector3f v = rawVertices.get(i);
            Vector3f n = rawNormals.get(i);

            // Check if a vertex corresponds to this one:
            for (int j = 0; j < refinedVertices.size(); j++)
            {
                if (refinedVertices.get(j).equals(v) && rawNormals.get(j).equals(n))
                {
                    foundIndex = j;
                    break;
                }
            }

            if (foundIndex > -1)
            {
                // If it's found:
                triangles.add(foundIndex);
            } else
            {
                // If it's not found:
                triangles.add(refinedVertices.size());
                refinedVertices.add(v);

                //Also save the new normal.
                refinedNormals.add(n);
            }
        }
        // Replace the vertices by the refined ones.
        rawVertices.clear();
        rawVertices.addAll(refinedVertices);

        // Also replace the normals.
        rawNormals.clear();
        rawNormals.addAll(refinedNormals);

        // Return the triangles indices.
        return triangles;
    }
}