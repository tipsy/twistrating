package twistrating;

import twistrating.views.Twist;

import java.util.List;

public interface TwistRating {
    void rateTwist(String twistId, int rating);

    List<Twist> getTwists();
}
