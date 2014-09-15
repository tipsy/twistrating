package twistrating.eventhandlers;

import com.firebase.client.*;
import org.axonframework.eventhandling.annotation.EventHandler;
import twistrating.events.TwistRatedEvent;

public class RatingEventHandler {

    private static final String likeCount = "likeCount";
    private static final String neutralCount = "neutralCount";
    private static final String dislikeCount = "dislikeCount";

    private final Firebase firebase;

    public RatingEventHandler(Firebase firebase) {
        this.firebase = firebase;
    }

    @EventHandler
    public void handle(TwistRatedEvent event) {
        String property;
        switch (event.getRating()) {
            case 1:
                property = likeCount;
                break;
            case 0:
                property = neutralCount;
                break;
            case -1:
                property = dislikeCount;
                break;
            default:
                property = neutralCount;
                break;
        }

        String targetPath = event.getTwistId() + "/" + property;

        Firebase firebaseTwist = firebase.child(targetPath);
        if (firebaseTwist != null) {
            firebaseTwist.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    int count = mutableData.getValue(int.class);
                    mutableData.setValue(count + 1);

                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
                }
            });
        }

//        Twist twist = Twist.find.byId(event.getTwistId());
//        if (twist != null) {
//            switch (event.getRating()) {
//                case 1:
//                    twist.likeCount++;
//                    break;
//                case 0:
//                    twist.neutralCount++;
//                    break;
//                case -1:
//                    twist.dislikeCount++;
//                    break;
//            }
//            System.out.println("Found twist:" + twist.id + " with likeCount:" + twist.likeCount);
//            twist.save();
//        }
    }
}
