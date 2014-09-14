package twistrating.models.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import twistrating.models.TwistData;
import play.api.libs.Files;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TwistProvider {
    private final File twistsDataFile;

    private List<TwistData> twists;
    private Map<String, TwistData> twistsLookup;

    public TwistProvider(File twistsDataFile) {
        this.twistsDataFile = twistsDataFile;
    }

    public List<TwistData> getTwists() {
        // TODO: Make it thread-safe
        if (twists == null) {
            String data = Files.readFile(twistsDataFile);

            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> listOfTwists;
            try {
                listOfTwists = mapper.readValue(data, List.class);
            } catch (IOException e) {
                return new ArrayList<>();
            }

            twists = new ArrayList<>();
            for (Map<String, Object> twist : listOfTwists) {
                String id = (String)twist.get("id");
                String name = (String)twist.get("name");
                String imageUrl = (String)twist.get("imageUrl");
                String charId = (String)twist.get("charId");

                twists.add(new TwistData(id, name, imageUrl, charId));
            }
        }
        return twists;
    }

    public Map<String, TwistData> getTwistsLookup() {
        // TODO: Make it thread-safe
        if (twistsLookup == null) {
            twistsLookup = new HashMap<>();
            for (TwistData twist : getTwists()) {
                twistsLookup.put(twist.id, twist);
            }
        }
        return twistsLookup;
    }
}
