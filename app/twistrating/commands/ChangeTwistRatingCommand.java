package twistrating.commands;

import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;

public class ChangeTwistRatingCommand {
    @TargetAggregateIdentifier
    private final String twistId;

    private final int previousRating;
    private final int newRating;

    public ChangeTwistRatingCommand(String twistId, int previousRating, int newRating) {
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
