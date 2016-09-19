package controllers;

import com.avaje.ebean.Ebean;
import com.google.inject.Inject;
import helpers.Utilities;
import models.database.*;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import references.Constants;
import views.html.createUser;
import views.html.events;
import views.html.nopermission;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by mallem on 4/5/16.
 */
public class BeaconController extends Controller {

    @Inject
    FormFactory formFactory;
    Utilities utilities =new Utilities();
    public Result getBeaconById(String id) {

        Beacon beacon = Ebean.find(Beacon.class).where().ieq("id", id).findUnique();
        return ok(events.render(utilities.removeDuplicate(beacon.getEvents()), "abc"));
    }

    public Result createBeacon() {
        DynamicForm form = formFactory.form().bindFromRequest();
        Location loc = Ebean.find(Location.class).where().ieq("name", form.get("location")).findUnique();
        List<Event> events = Ebean.find(Event.class).findList();
        Beacon beacon = Beacon.builder().description(form.get("description"))
                .id(form.get("id")).location(loc).events(events).build();
        beacon.save();
        return ok();
    }

    public Result noBeacons() {
        return ok(nopermission.render("No Beacons Around"));
    }

    public Result getEventsForUser(String id, String userId) {
        User user = Ebean.find(User.class).where().ieq("device_id", userId).findUnique();
        List<Event> returnEvents = new ArrayList<>();
        Event currEvent;
        Beacon beacon = Ebean.find(Beacon.class).where().ieq("id", id).findUnique();
        List<Event> allEvents = beacon.getEvents();
        Iterator<Event> eventIter = allEvents.iterator();
        while (eventIter.hasNext()) {
            currEvent = eventIter.next();
            if (ifCategoryMatches(user.getCategories(), currEvent.getCategory())) {
                returnEvents.add(currEvent);
            }
        }

        return ok(events.render(utilities.removeDuplicate(returnEvents), userId));
    }

    public Result getEventsByBeacons() {

        String deviceId = "";
        String[] beaconIds = new String[Constants.KeyWords.TOTAL_NUMBER_OF_BEACONS];
        List<String> categories = new ArrayList<>();
        List<Event> returnEvents = new ArrayList<>();
        final Set<Map.Entry<String, String[]>> entries = request().queryString().entrySet();
        for (Map.Entry<String, String[]> entry : entries) {
            if (entry.getKey().equals(Constants.KeyWords.DEVICE_ID)) {
                deviceId = entry.getValue()[0];
            } else if (entry.getKey().equals(Constants.KeyWords.BEACON_ID)) {
                beaconIds = entry.getValue();
            } else {
                System.out.println("Invalid query parameter");
            }
        }
        System.out.println("Req Received " + deviceId);
        User user = Ebean.find(User.class).where().ieq("deviceId", deviceId).findUnique();
        if (user != null) {
            List<Beacon> beacons = Ebean.find(Beacon.class).where().in("id", beaconIds).findList();
            if(beacons.size() != 0) {
                Iterator<Beacon> beaconIter = beacons.iterator();
                while (beaconIter.hasNext()) {
                    Beacon currBeacon = beaconIter.next();
                    Iterator<Event> currEventsIterator = currBeacon.getEvents().iterator();
                    while (currEventsIterator.hasNext()) {
                        Event currEvent = currEventsIterator.next();
                        if (ifCategoryMatches(user.getCategories(), currEvent.getCategory())) {
                            returnEvents.add(currEvent);
                        }
                    }
                }


//                return ok(events.render(returnEvents, deviceId));

                return ok(events.render(utilities.removeDuplicate(returnEvents), deviceId));
            }
            else {
                return ok(nopermission.render("You do not have required permissions"));
            }
        } else {
            // Redirect the Page to Create the User
            List<Category> categoryList = Ebean.find(Category.class).findList();
            return ok(createUser.render(deviceId, categoryList));
        }
    }

    public boolean ifCategoryMatches(String userCategories, String eventCategory) {
        String[] eventCategories = eventCategory.split(",");
        String[] dbCategories = userCategories.split(",");

        for (int i = 0; i < dbCategories.length; i++) {
            for (int j = 0; j < eventCategories.length; j++) {
                if (dbCategories[i].equals(eventCategories[j]))
                    return true;
            }
        }
        return false;
    }

}
