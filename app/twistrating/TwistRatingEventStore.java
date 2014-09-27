package twistrating;

import com.avaje.ebean.Ebean;
import org.axonframework.commandhandling.gateway.CommandGateway;
import play.Logger;
import twistrating.commands.ChangeTwistRatingCommand;
import twistrating.commands.RateTwistCommand;
import twistrating.models.providers.TwistProvider;
import twistrating.views.Twist;

import java.util.List;

public class TwistRatingEventStore implements TwistRating {
    private final CommandGateway commandGateway;
    private final TwistProvider twistProvider;

    public TwistRatingEventStore(CommandGateway commandGateway, TwistProvider twistProvider) {
        this.commandGateway = commandGateway;
        this.twistProvider = twistProvider;
    }

    public void rateTwist(String twistId, int rating) {
        commandGateway.send(new RateTwistCommand(twistId, rating));
    }

    public void changeTwistRating(String twistId, int previousRating, int newRating) {
        commandGateway.send(new ChangeTwistRatingCommand(twistId, previousRating, newRating));
    }

    public List<Twist> getTwists() {
        bootstrapDatabase();
        return Twist.find.orderBy("(like_count-dislike_count) DESC").findList();
    }

    private void bootstrapDatabase() {
        if (Twist.find.findRowCount() == 0) {
            Logger.info("Adding twists...");
            
            twistProvider.getTwists().stream().forEach(twistData -> {
                Twist twist = new Twist();
                twist.id = twistData.id;
                twist.name = twistData.name;
                twist.imageUrl = twistData.imageUrl;
                twist.charId = twistData.charId;
                Ebean.save(twist);
            });
        }
    }
}
