package twistrating.eventhandlers;

import com.avaje.ebean.Ebean;
import org.axonframework.eventhandling.annotation.EventHandler;
import twistrating.events.TwistRatedEvent;
import twistrating.views.Twist;

public class RatingEventHandler {

    @EventHandler
    public void handle(TwistRatedEvent event) {
        System.out.println("Someone rated " + event.getTwistId() + " to " + event.getRating());

        Twist twist = Twist.find.byId(event.getTwistId());
        Twist clonedTwist = (Twist)twist._ebean_createCopy();
        twist.delete();

        if (clonedTwist != null) {
            System.out.println("Found twist:" + clonedTwist.id + " with likeCount:" + clonedTwist.likeCount);
            switch (event.getRating()) {
                case 1:
                    clonedTwist.likeCount++;
                    break;
                case 0:
                    clonedTwist.neutralCount++;
                    break;
                case -1:
                    clonedTwist.dislikeCount++;
                    break;
            }
            System.out.println("Found twist:" + clonedTwist.id + " with likeCount:" + clonedTwist.likeCount);
            Ebean.save(clonedTwist);
        }
    }
}
