package twistrating.eventhandlers;

import org.axonframework.eventhandling.annotation.EventHandler;
import twistrating.events.TwistRatedEvent;
import twistrating.views.Twist;

public class RatingEventHandler {

    private static final String likeCount = "likeCount";
    private static final String neutralCount = "neutralCount";
    private static final String dislikeCount = "dislikeCount";

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
            System.out.println("Found twist:" + twist.id + " with likeCount:" + twist.likeCount);
            twist.save();
        }
    }
}
