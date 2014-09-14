package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import twistrating.TwistRating;
import twistrating.views.Twist;
import views.html.index;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Application extends Controller {
    private final TwistRating twistRating;

    @Inject
    public Application(TwistRating twistRating) {
        this.twistRating = twistRating;
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getTwists() {
        List<Twist> twists = twistRating.getTwists();
        Map<String, Object> json = new HashMap<>();
        json.put("twists", twists);

        String data = stringFromJson(json);
        return ok(data);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result rateTwist() {
        JsonNode json = request().body().asJson();
        String twistId = json.get("twistId").asText();
        int rating = json.get("rating").asInt(-1);

        twistRating.rateTwist(twistId, rating);

        return ok();
    }

    private static String stringFromJson(Object json) {
        final OutputStream out = new ByteArrayOutputStream();
        final ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValue(out, json);
        } catch (IOException e) {
            return null;
        }

        return out.toString();
    }
}
