package twistrating;

import com.avaje.ebean.Ebean;
import org.axonframework.commandhandling.gateway.CommandGateway;
import play.Logger;
import twistrating.commands.CreateTwistCommand;
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
        commandGateway.send(new CreateTwistCommand(twistId));
        commandGateway.send(new RateTwistCommand(twistId, rating));
    }

    public List<Twist> getTwists() {
        bootstrapDatabase();
        return Twist.find.all();
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
