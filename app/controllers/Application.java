package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import play.api.libs.json.JsObject;
import play.libs.Json;
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

import static play.libs.Json.toJson;

public class Application extends Controller {
    private final TwistRating twistRating;

    @Inject
    public Application(TwistRating twistRating) {
        this.twistRating = twistRating;
    }

    public Result index() {
        return ok(index.render());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getTwists() {
//        String remote = request().remoteAddress();
        List<Twist> twists = twistRating.getTwists();
        Map<String, Object> json = new HashMap<>();
        json.put("twists", twists);
        String data = stringFromJson(json);

        response().setContentType("application/json; charset=utf-8");
        return ok(data);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getTwist(String id) {
        Twist twist = Twist.find.byId(id);
        return ok(toJson(twist));
    }


    @BodyParser.Of(BodyParser.Json.class)
    public Result rateTwist() {
        JsonNode json = request().body().asJson();
        String twistId = json.get("id").asText();
        int rating = json.get("rating").asInt(-1);

        if( ! hasBeenRated(twistId, rating)) {
            System.out.println("RATE " + twistId + " : " + rating);
            twistRating.rateTwist(twistId, rating);
        } else {
            changeRating(twistId, rating);
        }

        Twist twist = Twist.find.byId(twistId);
        return ok(toJson(twist));
    }

    private void changeRating(String twistId, int rating) {
        JsonNode ratings = Json.parse(session("ratings"));
        int previousRating = ratings.get(twistId).asInt();

        if (rating != previousRating){
            twistRating.changeTwistRating(twistId, previousRating, rating);

            ((ObjectNode)ratings).put(twistId, rating);
            session("ratings", Json.stringify(ratings));
        }
    }

    private boolean hasBeenRated(String twistId, int rating) {
        String ratingsString = session("ratings");
        System.out.println(session());
        JsonNode ratings;
        if (ratingsString != null) {
            ratings = Json.parse(ratingsString);
        }else {
            ratings = Json.newObject();
        }
        if (ratings.has(twistId)){
            return true;
        } else {
            ((ObjectNode)ratings).put(twistId, rating);
            session("ratings", Json.stringify(ratings));
            return false;
        }
    }

    private String getSessionId() {
        String uuid = session("uuid");
        if(uuid == null) {
            uuid = java.util.UUID.randomUUID().toString();
            session("uuid", uuid);
        }
        return uuid;
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
