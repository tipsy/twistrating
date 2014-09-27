package twistrating.events;

public class TwistRatingChangedEvent {
    private final String twistId;
    private final int previousRating;
    private final int newRating;

    public TwistRatingChangedEvent(String twistId, int previousRating, int newRating) {
        this.twistId = twistId;
        this.previousRating = previousRating;
        this.newRating = newRating;
    }

    public String getTwistId() {
        return twistId;
    }

    public int getPreviousRating() {
        return previousRating;
    }

    public int getNewRating() {
        return newRating;
    }
}
