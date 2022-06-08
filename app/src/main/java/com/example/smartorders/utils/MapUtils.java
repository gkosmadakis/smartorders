package com.example.smartorders.utils;

import com.example.smartorders.models.MenuData;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapUtils {

    public MapUtils() {
    }

    public String convertCamelCaseToHumanRead(String text){
        return text.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
    }

    public LinkedHashMap modifyMenuDetailMap(Map<String, Map<Integer, MenuData>> menuDetailMap) {
        MapUtils mapUtils = new MapUtils();
        Map<String, Map<Integer, MenuData>> modifiedMenuDetailHashMap = new HashMap<>();
        Iterator<Map.Entry<String, Map<Integer, MenuData>>> iterator = menuDetailMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Map<Integer, MenuData>> entry = iterator.next();
            String key = entry.getKey();
            iterator.remove();
            modifiedMenuDetailHashMap.put(mapUtils.convertCamelCaseToHumanRead(key), entry.getValue());
        }
        /*Here i put in the map first the Picked for you to appear first in the tabs and in the menu list */
        LinkedHashMap<String, Map<Integer, MenuData>> modifiedMenuDetailLinkedHashMap = new LinkedHashMap<String, Map<Integer, MenuData>>(modifiedMenuDetailHashMap.size());
        Map<Integer, MenuData> pickedForYou = modifiedMenuDetailHashMap.remove("Picked For You");
        modifiedMenuDetailLinkedHashMap.put("Picked For You", pickedForYou);
        modifiedMenuDetailLinkedHashMap.putAll(modifiedMenuDetailHashMap);
        return modifiedMenuDetailLinkedHashMap;
    }
}
