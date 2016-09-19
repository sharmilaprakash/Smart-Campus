package controllers;


import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import helpers.ConvertToLogFormat;
import helpers.PushToMLServer;
import helpers.Utilities;
import models.database.*;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Result;
import references.Constants;
import views.html.*;

import javax.persistence.PersistenceException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by mallem on 3/19/16.
 */
public class EventController extends Controller {

    @Inject
    FormFactory formFactory;
    @Inject
    WSClient ws;
    ConvertToLogFormat logConvertor = new ConvertToLogFormat();
    PushToMLServer pushToMLServer = new PushToMLServer();
    Utilities utilities = new Utilities();

    public Result getEventsByLocation(String location) {
        List<Event> eventList = Ebean.find(Event.class).where().ieq("location", location).findList();
        return ok(events.render(utilities.removeDuplicate(eventList), "abc"));
    }

    public Result getEventsByCategories() {
        DynamicForm form = formFactory.form().bindFromRequest();
        String[] categories = form.get("categories").split(",");

        HashSet<Event> events = new HashSet<>();

        for (String category : categories) {
            //events.addAll(Ebean.find(Event.class).where().ieq("category", category).ieq("is_active", "1").findList());
            events.addAll(Ebean.find(Event.class).where().in("category", categories).ieq("is_active", "1").findList());
        }

        return ok(events.toString());
    }

    public Result createEventPage(String deviceId) {
        boolean isAdmin = false;
        User user = Ebean.find(User.class).where().ieq("device_id", deviceId).findUnique();
        List<Category> categories = Ebean.find(Category.class).findList();
        if (user != null) {
            String[] roles = user.getRole().split(",");
            for (int i = 0; i < roles.length; i++) {
                if (roles[i].equalsIgnoreCase("ADMIN")) {
                    isAdmin = true;
                    break;
                }
            }
            if (isAdmin) {
                List<Location> locations = Ebean.find(Location.class).findList();
                return ok(createEvent.render(locations, categories, deviceId));
            } else {
                return ok(nopermission.render("You do not have reqd permissions"));
            }
        } else {
            return ok(createUser.render(deviceId, categories));
        }
    }

    /*
        TODO : Fix start/end time
        TODO : prof suggests that we should show events even though the user is not subscribed to them based on timings (e.g., say showing food related events to most of the users at 4 pm.) Make a plan of action for the same.
     */
    public Result createEvent() {
        Map<String, String[]> form = request().body().asFormUrlEncoded();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        try {
            Timestamp startTime = new Timestamp(format.parse(form.get("startTime")[0]).getTime());
            Timestamp endTime = new Timestamp(format.parse(form.get("endTime")[0]).getTime());
            Event event = Event.builder()
                    .name(form.get("name")[0])
                    .description(form.get("description")[0])
                    .externalLink(form.get("externalLink")[0])
                    .category(getCategories(form.get("categories")))
                    .startTime(startTime)
                    .endTime(endTime)
                    .isActive(Boolean.valueOf(form.get("isActive")[0]))
                    .location(form.get("location")[0])
                    .beacons(getBeaconsForEvent(form.get("beaconLocations")))
                    .createdBy(form.get("createdBy")[0])
                    .build();
            event.save();
            //todo: push to mlserver
            io.prediction.Event eventToBePushed = logConvertor.convertCreatedEvent(event);
            Logger.info(Constants.KeyWords.LOG_SEPERATOR + Json.toJson(eventToBePushed).toString());
            pushToMLServer.pushEvent(eventToBePushed);


        } catch (PersistenceException p) {
            return badRequest("Event Already Exists");
        } catch (ParseException e) {
            return badRequest("Bad Request From UI");
        }
        return redirect("/events/" + form.get("createdBy")[0]);
    }

    private String getCategories(String[] categories) {
        String result = "";
        if (categories != null) {
            for (String category : categories) {
                result += category + ",";
            }
        }
        return result;
    }

    private List<Beacon> getBeaconsForEvent(String[] locations) {
        List<Beacon> beacons = new ArrayList<>();
        if (locations != null) {
            for (String location : locations) {
                beacons.addAll(Ebean.find(Location.class).where().ieq("name", location).findUnique().getBeacons());
            }
        }
        return beacons;
    }

    public Result getEventsByAdmin(String user) {
        User curUser = Ebean.find(User.class).where().ieq("device_id", user).findUnique();
        if(curUser != null) {
            List<Event> eventList = Ebean.find(Event.class).where().ieq("createdBy", user).findList();
            if(eventList.size() != 0) {
                return ok(events.render(utilities.removeDuplicate(eventList), user));
            }
            else return ok(nocreatedevents.render());
        }
        else{
            List<Category> categoryList = Ebean.find(Category.class).findList();
            return ok(createUser.render(user, categoryList));
        }

    }

    public Result testUI() {
        return ok(main.render("Test", null));
    }

    public Result getRecommendedEvents(String deviceId) throws IOException {

        User user = Ebean.find(User.class).where().ieq("device_id", deviceId).findUnique();
        if (user != null) {
            List<String> eventIds = new ArrayList<>();
            JsonObject temp = new PushToMLServer().getDetails(deviceId);
            JsonArray itemScores = temp.getAsJsonArray("itemScores");
            for(int i = 0; i< itemScores.size(); i++) {
                eventIds.add(itemScores.get(i).getAsJsonObject().get("item").getAsString());
            }
            List<Event> eventList = Ebean.find(Event.class).where().in("id", eventIds).findList();
            return ok(events.render(utilities.removeDuplicate(eventList), deviceId));
        } else {
            List<Category> categoryList = Ebean.find(Category.class).findList();
            return ok(createUser.render(deviceId, categoryList));
        }
    }

    public Result getStarredEvents(String deviceId) {
        System.out.println("Starred Req Received");
        User user = Ebean.find(User.class).where().ieq("device_id", deviceId).findUnique();

        if (user != null) {
            return ok(events.render(utilities.removeDuplicate(user.getEvents()), deviceId));
        } else {
            List<Category> categoryList = Ebean.find(Category.class).findList();
            return ok(createUser.render(deviceId, categoryList));
        }
    }

    public Result getSingleEventPage(String deviceId, String eventId) {
        Boolean isStarred = false;
        Event curEvent = Ebean.find(Event.class).where().ieq("id", eventId).findUnique();
        User user = Ebean.find(User.class).where().ieq("deviceId", deviceId).findUnique();
        List<User> users = new ArrayList<>();
        users.addAll(curEvent.getUsers());
        if (users.contains(user)) {
            isStarred = true;
        }
        io.prediction.Event eventToBePushed = logConvertor.convertViewedEvent(deviceId, eventId);
        Logger.info(Constants.KeyWords.LOG_SEPERATOR + Json.toJson(eventToBePushed).toString());
        pushToMLServer.pushEvent(eventToBePushed);
        return ok(event.render(deviceId, curEvent, isStarred));
    }

    public Result getNoLiveEventsPage() {
        return ok(noevents.render());
    }

}
