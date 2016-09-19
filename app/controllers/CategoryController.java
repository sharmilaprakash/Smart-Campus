package controllers;

import com.avaje.ebean.Ebean;
import com.google.inject.Inject;
import helpers.ConvertToLogFormat;
import helpers.PushToMLServer;
import models.database.Category;
import models.database.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import references.Constants;
import views.html.categoryUpdate;
import views.html.createUser;
import views.html.updateSuccess;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by mallem on 4/2/16.
 */
public class CategoryController extends Controller {

    ConvertToLogFormat logConvertor = new ConvertToLogFormat();
    PushToMLServer pushToMLServer = new PushToMLServer();

    @Inject
    FormFactory formFactory;

    public Result getAll() {
        List<Category> categoryList = Ebean.find(Category.class).findList();
        return ok(categoryList.toString());
    }

    public Result getByName(String name) {
        Category category = Ebean.find(Category.class).where().ieq("name", name).findUnique();
        return ok(category.toString());
    }

    public Result createCategory() {
        DynamicForm form = formFactory.form().bindFromRequest();
        Category category = Category.builder()
                .name(form.get("name"))
                .description(form.get("description"))
                .build();
        try {
            category.save();
        } catch (PersistenceException e) {
            return badRequest("Category Already Exists!!!");
        }
        return ok();
    }

    public Result getCategoriesPage(String userId) {
        User user = Ebean.find(User.class).where().ieq("device_id", userId).findUnique();
        if(user!=null) {
            String[] registeredCat = user.getCategories().split(",");
            List<String> registeredCategories = new ArrayList<>();
            for (int i = 0; i < registeredCat.length; i++) {
                registeredCategories.add(registeredCat[i]);
            }
            List<Category> allCategories = Ebean.find(Category.class).findList();
            return ok(categoryUpdate.render(userId, registeredCategories, allCategories));
        }


        else {
            List<Category> categoryList = Ebean.find(Category.class).findList();
            return ok(createUser.render(userId, categoryList));
        }

    }

    public Result updateCategories(){
        Map<String, String[]> form = request().body().asFormUrlEncoded();
        String[] categoryList = form.get("categories");
        String categories = "";
        if (categoryList != null) {
            for (String category : categoryList) {
                categories += category + ",";
            }
        } else {
            categories += "all";
        }

        User user = Ebean.find(User.class).where().ieq("device_id", form.get("deviceId")[0]).findUnique();
        user.setCategories(categories);
        user.save();

        io.prediction.Event eventToBePushed = logConvertor.convertCreateUser(user);
        Logger.info(Constants.KeyWords.LOG_SEPERATOR +  Json.toJson(eventToBePushed).toString());
        pushToMLServer.pushEvent(eventToBePushed);
        return ok(updateSuccess.render());
    }
}
