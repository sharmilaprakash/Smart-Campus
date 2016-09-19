package controllers;

import com.avaje.ebean.Ebean;
import com.google.inject.Inject;
import models.database.Location;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;

import javax.persistence.PersistenceException;
import java.util.List;

/**
 * Created by mallem on 4/2/16.
 */
public class LocationController extends Controller {

    @Inject
    FormFactory formFactory;

    public Result getAll() {
        List<Location> locList = Ebean.find(Location.class).findList();
        return ok(locList.toString());
    }

    public Result getByName(String name) {
        Location loc = Ebean.find(Location.class).where().ieq("name", name).findUnique();
        return ok(loc.toString());
    }

    public Result createLocation() {
        DynamicForm form = formFactory.form().bindFromRequest();
        Location location = Location.builder()
                .name(form.get("name"))
                .description(form.get("description"))
                .build();
        try {
            location.save();
        } catch (PersistenceException e) {
            return badRequest("Location Already Exists!!!");
        }
        return ok();
    }
}
