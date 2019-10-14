package digitaltwin.device;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;

public class JSONHelper {

    public static HashMap<String, Object> getMapFromJSON(String json){
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, Object> res = new HashMap<String, Object>();

        try {
            res = objectMapper.readValue(json, new TypeReference<HashMap<String,Object>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

}
