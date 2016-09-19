package models.database;

import com.avaje.ebean.Model;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * Created by mallem on 3/19/16.
 */

/**
 * This Table Would be prepopulated
 */
@Data
@Entity
@Builder
@Table(name = "location")
public class Location extends Model {

    @Id
    @GeneratedValue
    private int id;

    private String name;

    private String description;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "location")
    private List<Beacon> beacons;

    @Override
    public String toString() {
        return "id=" + id + "desc=" + description + "name=" + name;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }
}

