package twistrating.eventhandlers;

import org.axonframework.eventhandling.annotation.EventHandler;
import twistrating.events.TwistRatedEvent;
import twistrating.events.TwistRatingChangedEvent;
import twistrating.views.Twist;

public class RatingEventHandler {
    @EventHandler
    public void handle(TwistRatedEvent event) {
        Twist twist = Twist.find.byId(event.getTwistId());
        if (twist != null) {
            switch (event.getRating()) {
                case 1:
                    twist.likeCount++;
                    break;
                case 0:
                    twist.neutralCount++;
                    break;
                case -1:
                    twist.dislikeCount++;
                    break;
            }
            System.out.println("Added rating for twist:" + event.getTwistId() + " (rating:" + event.getRating() + ")");
            twist.save();
        }
    }

    @EventHandler
    public void handle(TwistRatingChangedEvent event) {
        Twist twist = Twist.find.byId(event.getTwistId());
        if (twist != null) {
            switch (event.getPreviousRating()) {
                case 1:
                    twist.likeCount--;
                    break;
                case 0:
                    twist.neutralCount--;
                    break;
                case -1:
                    twist.dislikeCount--;
                    break;
            }

            switch (event.getNewRating()) {
                case 1:
                    twist.likeCount++;
                    break;
                case 0:
                    twist.neutralCount++;
                    break;
                case -1:
                    twist.dislikeCount++;
                    break;
            }

            System.out.println("Changed rating for twist:" + event.getTwistId() + " (previousRating:" + event.getPreviousRating() + ", newRating:" + event.getNewRating() + ")");
            twist.save();
        }
    }
}
