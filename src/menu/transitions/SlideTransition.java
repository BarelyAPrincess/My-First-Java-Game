package menu.transitions;

import com.jme3.math.Vector3f;
import java.util.Random;
import menu.elements.Panel;

/**
 * A sliding transition makes the source and destination panel slide into place.
 */
public class SlideTransition extends Transition
{

    public enum Direction
    {

        LEFT, RIGHT, UP, DOWN
    };
    private Direction direction;
    private Vector3f origin, finish;

    public SlideTransition(Panel source, Panel destination, Direction direction)
    {
        super(source, destination);
        this.direction = direction;
    }

    /**
     * Creates a sliding transition in a random direction.
     *
     */
    public SlideTransition(Panel source, Panel destination)
    {
        this(source, destination, Direction.values()[new Random().nextInt(4)]);
    }

    @Override
    public boolean isOver()
    {
        switch (direction)
        {
            case LEFT:
                return destination.getLocalTranslation().x < finish.x;
            case RIGHT:
                return destination.getLocalTranslation().x > finish.x;
            case DOWN:
                return destination.getLocalTranslation().y < finish.y;
            case UP:
                return destination.getLocalTranslation().y > finish.y;
            default:
                return true;
        }
    }

    @Override
    public void init()
    {
        // Put the destination on the good side of the source.
        Vector3f location;
        if (direction == Direction.LEFT || direction == Direction.DOWN)
        {
            // The start location is the farthest bound of the source, plus the
            // destination width ( or height...). Also add a 5% margin.
            location = source.getAbsoluteMaxBound();
            location.subtractLocal(destination.getRelativeMinBound().multLocal(1.05f));
        } else
        {
            location = source.getAbsoluteMinBound();
            location.subtractLocal(destination.getRelativeMaxBound().multLocal(1.05f));
        }

        // Set the destination initial location.
        Vector3f newLocation = source.getLocalTranslation().clone();
        switch (direction)
        {
            case LEFT:
            case RIGHT:
                newLocation.x = location.x;
                break;
            case UP:
            case DOWN:
                newLocation.y = location.y;
                break;
        }

        destination.setLocalTranslation(newLocation);


        // Save the source positions.
        origin = destination.getLocalTranslation().clone();
        finish = source.getLocalTranslation().clone();
    }

    @Override
    public void finish()
    {
        // On finish, replace the source on its original location.
        source.setLocalTranslation(origin);
        // Also replace the destination right on place.
        destination.setLocalTranslation(finish);
    }

    @Override
    public void update(float tpf)
    {
        Vector3f destinationLocation = destination.getLocalTranslation();
        Vector3f sourceLocation = source.getLocalTranslation();


        // First compute the progress of the transition (from 0 to 1).
        float progress = getProgress();
        // Make it span over [-0.5,0.5] then take abs value:
        progress = Math.abs(progress - 0.5f);
        // Finally, compute the speed from that value:
        float speed = (0.7f - progress) * tpf*5f;
        


        switch (direction)
        {
            case LEFT:
            case RIGHT:
                speed *= (finish.x - origin.x);
                destinationLocation.x += speed;
                sourceLocation.x += speed;
                break;
            case DOWN:
            case UP:
                speed *= (finish.y - origin.y);
                destinationLocation.y += speed;
                sourceLocation.y += speed;
                break;
        }


        // Re-set the location to force a geometry refresh.
        source.setLocalTranslation(sourceLocation);
        destination.setLocalTranslation(destinationLocation);
    }

    private float getProgress()
    {
        switch (direction)
        {
            case LEFT:
            case RIGHT:
                return Math.abs((destination.getLocalTranslation().x - origin.x) / (finish.x - origin.x));

            case DOWN:
            case UP:
                return Math.abs((destination.getLocalTranslation().y - origin.y) / (finish.y - origin.y));
            default:
                return 0;
        }
    }
}
