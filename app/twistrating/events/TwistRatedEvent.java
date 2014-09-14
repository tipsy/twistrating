package twistrating.events;

public class TwistRatedEvent {
    private final String twistId;
    private final int rating;

    public TwistRatedEvent(String twistId, int rating) {
        this.twistId = twistId;
        this.rating = rating;
    }

    public String getTwistId() {
        return twistId;
    }

    public int getRating() {
        return rating;
    }
}
