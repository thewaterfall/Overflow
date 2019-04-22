package waterfall.game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GameFactory implements Factory<Game> {
    private static Map<String, Class> beanStorage = new HashMap<>();

    public GameFactory() {
        if (beanStorage == null) {
            beanStorage = new HashMap<>();
        }
    }

    public GameFactory(int storageSize) {
        this.beanStorage = new HashMap<>(storageSize);
    }

    @Override
    public void register(String name, Class classType) {
        if(!Arrays.asList(classType.getInterfaces()).contains(Game.class))
            throw new IllegalArgumentException("Class should implement Game interface");

        beanStorage.put(name, classType);
    }

    @Override
    public Game getBean(String name) {
        return instantiateBean(name);
    }

    @Override
    public Game getBean(Class classType) {
        return instantiateBean(classType);
    }

    private Game instantiateBean(String name) {
        Class classType = beanStorage.get(name);
        Game game = null;
        try {
             game = (Game) classType.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


        return game;
    }

    private Game instantiateBean(Class classType) {
        Game game = null;

        for(Class clazz: beanStorage.values()) {
            if(clazz.equals(classType)) {
                try {
                    game = (Game) clazz.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }

        return game;
    }
}
