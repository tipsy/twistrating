package twistrating;

import twistrating.views.Twist;

import java.util.List;

public interface TwistRating {
    void rateTwist(String twistId, int rating);
    public void changeTwistRating(String twistId, int previousRating, int newRating);

    List<Twist> getTwists();
}
