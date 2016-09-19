package helpers;

import models.database.Event;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by sank on 4/17/16.
 */
public class Utilities {
    public List<Event> removeDuplicate(List<Event> eventList) {

        List<Event> tmp1 = new ArrayList<>();
        Iterator<Event> iter = eventList.iterator();
        while (iter.hasNext()) {
            Event currEvent = iter.next();
            if(!tmp1.contains(currEvent)) {
                tmp1.add(currEvent);
            }
        }

        return tmp1;
    }
}
