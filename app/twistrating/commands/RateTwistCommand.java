package twistrating.commands;

import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;

public class RateTwistCommand {
    @TargetAggregateIdentifier
    private final String twistId;

    private final int rating;

    public RateTwistCommand(String twistId, int rating) {
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
