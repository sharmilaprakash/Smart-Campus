package services;

import com.avaje.ebean.Ebean;
import models.database.User;

/**
 * Created by sank on 3/19/16.
 */
public class Experiments {

    public void method() {
        User user = User.builder().userName("asdas").role("asdas").categories("asdfashjgdas").build();
//        Beacon beacon = Beacon.builder().id(1).description("asdasd").build();


        Ebean.save(user);
    }
}
