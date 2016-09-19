package models.logformats;

import lombok.Data;

/**
 * Created by sank on 4/16/16.
 */
@Data
public class LogFormat2 {
    private String event;
    private String entityType;
    private String entityId;
    private Properties properties;
}
