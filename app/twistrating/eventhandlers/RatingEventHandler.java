package twistrating.eventhandlers;

import org.axonframework.eventhandling.annotation.EventHandler;
import twistrating.events.TwistRatedEvent;
import twistrating.views.Twist;

public class RatingEventHandler {

    @EventHandler
    public void handle(TwistRatedEvent event) {
        System.out.println("Someone rated " + event.getTwistId() + " to " + event.getRating());

        Twist twist = Twist.find.byId(event.getTwistId());
        if (twist != null) {
            System.out.println("Found twist:" + twist.id + " with likeCount:" + twist.likeCount);
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
