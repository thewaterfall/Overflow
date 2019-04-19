package waterfall.game;

public interface Factory<E> {
    public E getBean(Class classType);

    public E getBean(String name);

    public void register(String name, Class classType);
}
