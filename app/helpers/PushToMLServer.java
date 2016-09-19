package helpers;

import com.google.gson.JsonObject;
import io.prediction.EngineClient;
import io.prediction.Event;
import io.prediction.EventClient;
import io.prediction.FutureAPIResponse;
import references.Constants;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by sank on 4/16/16.
 */
public class PushToMLServer {
    public void pushEvent(Event event) {
        EventClient client = new EventClient(Constants.MlConstants.ACCESS_KEY, Constants.Urls.ML_URL_POST);
        try {
            client.createEventAsFuture(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JsonObject getDetails(String userId) {
        EngineClient engineClient = new EngineClient(Constants.Urls.ML_URL_GET,100,100,10);
        Map<String, Object> input = new HashMap<String, Object>();
        input.put("user", userId);
        input.put("num", "10");
        FutureAPIResponse returnResponse = null;
        JsonObject response = null;
        try {
            response = engineClient.sendQuery(input);
//            response = engineClient.sendQuery(returnResponse);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }
}
