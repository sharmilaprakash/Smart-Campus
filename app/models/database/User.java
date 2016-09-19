package models.database;

import com.avaje.ebean.Model;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * Created by mallem on 3/19/16.
 */

@Data
@Entity
@Builder
@Table(name = "user")
public class User extends Model {

    @Id
    private String deviceId;

    private String userName;

    private String role;

    private String categories;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinTable(name = "user_events")
    private List<Event> events;


    @Override
    public String toString() {
        return "name=" + userName + "description=" + role;
    }

    @Override
    public boolean equals(Object obj) {
        User cmp = (User) obj;
        if (this.deviceId.equals(cmp.getDeviceId())) {
            return true;
        }
        return false;
    }

}
