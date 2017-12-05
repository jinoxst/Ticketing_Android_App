package jp.ne.jinoxst.mat.itg.util;

import java.util.HashMap;

public class GlobalRegistry {
    private static GlobalRegistry instance;
    private static HashMap<String, Object> registry;

    public synchronized static GlobalRegistry getInstance(){
        if(instance == null){
            instance = new GlobalRegistry();
        }

        return instance;
    }

    private GlobalRegistry(){
        registry = new HashMap<String, Object>();
    }

    public synchronized void setRegistry(String key, String value){
        registry.put(key, value);
    }

    public synchronized void setRegistry(String key, int value){
        registry.put(key, value);
    }

    public synchronized String getString(String key){
        Object obj = registry.get(key);
        if(obj != null){
            if(obj instanceof String){
                return (String)obj;
            }else{
                return String.valueOf(obj);
            }
        }else{
            return null;
        }
    }

    public synchronized int getInt(String key){
        Object obj = registry.get(key);
        if(obj != null){
            if(obj instanceof Integer){
                return (Integer)obj;
            }else{
                return -1;
            }
        }else{
            return -1;
        }
    }
}
