package waterfall.game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GameFactoryImpl implements GameFactory {

    private static Map<String, Class> gameStorage = new HashMap<>();
    private static Map<String, Class> playerStorage = new HashMap<>();

    public GameFactoryImpl() {

    }

    @Override
    public Player getPlayer(String name) {
        return instantiateBean(name, Player.class);
    }

    @Override
    public Game getGame(String name) {
        return instantiateBean(name, Game.class);
    }

    @Override
    public void register(String name, Class classType) {
        if(!Arrays.asList(classType.getInterfaces()).contains(Game.class) &&
           !Arrays.asList(classType.getInterfaces()).contains(Player.class))
            throw new IllegalArgumentException("Class should implement either Game or Player interface");

        saveBean(name, classType);
    }

    private <T> T instantiateBean(String name, Class<T> toInstantiate) {
        Class classType = getStorage(toInstantiate).get(name);

        Object bean = null;
        try {
            bean = classType.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return (T) bean;
    }


    private void saveBean(String name, Class classType) {
        Map<String, Class> tempStorage;

        if(Arrays.asList(classType.getInterfaces()).contains(Player.class)) {
            tempStorage = getStorage(Player.class);
        } else {
            tempStorage = getStorage(Game.class);
        }

        if(tempStorage.containsKey(name)) {
            tempStorage.remove(name);
        }

        tempStorage.put(name, classType);

    }

    private Map<String, Class> getStorage(Class storageClass) {
        if(storageClass == Player.class) {
            return playerStorage;
        } else {
            return gameStorage;
        }
    }

}
