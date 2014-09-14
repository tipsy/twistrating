package twistrating.events;

public class TwistCreatedEvent {
    private final String twistId;

    public TwistCreatedEvent(String twistId) {
        this.twistId = twistId;
    }

    public String getTwistId() {
        return twistId;
    }
}
