package menu.transitions;

import menu.elements.Panel;

/**
 * A direct transition directly switches from the source to destination panel.
 */
public class DirectTransition extends Transition
{

    public DirectTransition(Panel source, Panel destination)
    {
        super(source, destination);
        destination.setLocalTranslation(source.getLocalTranslation());
    }

    @Override
    public boolean isOver()
    {
        return true;
    }

    @Override
    public void update(float tpf)
    {
    }

    @Override
    public void finish()
    {
        // Nothing to do.
    }

    @Override
    public void init()
    {
         // Nothing to do.
    }
}
