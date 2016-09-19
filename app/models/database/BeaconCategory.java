package models.database;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by mallem on 3/20/16.
 */
@Data
@Entity
@Table(name = "beacon_category")
public class BeaconCategory {

    @Id
    private String beaconId;

    private String category;
}
