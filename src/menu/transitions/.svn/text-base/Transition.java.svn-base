package menu.transitions;

import menu.elements.Panel;

/**
 * A transition contains 2 Panels - a source and a destination - and allows the
 * root menu Panel to switch from one panel to the other, using one or the other
 * transistion mode.
 */
public abstract class Transition
{

    protected Panel source, destination;

    public Transition(Panel source, Panel destination)
    {
        this.source = source;
        this.destination = destination;
    }

    abstract public boolean isOver();

    abstract public void init();
    abstract public void finish();

    abstract public void update(float tpf);

    /**
     * @return the source
     */
    public Panel getSource()
    {
        return source;
    }

    /**
     * @return the destination
     */
    public Panel getDestination()
    {
        return destination;
    }
}
