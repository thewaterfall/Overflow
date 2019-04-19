package waterfall.game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PlayerFactory implements Factory<Player>{
    private Map<String, Class> beanStorage;

    public PlayerFactory() {
        this.beanStorage = new HashMap<>();
    }

    public PlayerFactory(int storageSize) {
        this.beanStorage = new HashMap<>(storageSize);
    }

    @Override
    public Player getBean(Class classType) {
        if(beanStorage.isEmpty()) {
            return new Player();
        } else {
            return instantiateBean(classType);
        }
    }
    @Override
    public Player getBean(String name) {
        if(beanStorage.isEmpty()) {
            return new Player();
        } else {
            return instantiateBean(name);
        }
    }
    @Override
    public void register(String name, Class classType) {
        if(classType.getSuperclass() != Player.class)
            throw new IllegalArgumentException("Class should be subclass of Player class");

        beanStorage.put(name, classType);
    }

    private Player instantiateBean(String name) {
        Class classType = beanStorage.get(name);
        Player player = null;
        try {
            player = (Player) classType.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


        return player;
    }

    private Player instantiateBean(Class classType) {
        Player player = null;
        for(Class clazz: beanStorage.values()) {
            if(clazz.equals(classType)) {
                try {
                    player = (Player) clazz.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }

        return player;
    }
}
