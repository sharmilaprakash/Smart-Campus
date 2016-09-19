package models.database;

import com.avaje.ebean.Model;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * Created by mallem on 4/5/16.
 */
@Entity
@Builder
@Data
@Table(name = "beacon")
public class Beacon extends Model {

    @Id
    private String id;

    private String description;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "beacon_events")
    private List<Event> events;

    @ManyToOne(fetch = FetchType.LAZY)
    private Location location;

    @Override
    public String toString() {
        return "id=" + id + "desc=" + description;
    }
}
