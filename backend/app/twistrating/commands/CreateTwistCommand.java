package twistrating.commands;

import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;

public class CreateTwistCommand {
    @TargetAggregateIdentifier
    private final String twistId;

    public CreateTwistCommand(String twistId) {
        this.twistId = twistId;
    }

    public String getTwistId() {
        return twistId;
    }
}
