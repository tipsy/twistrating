package twistrating.aggregates;

import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import twistrating.commands.ChangeTwistRatingCommand;
import twistrating.commands.CreateTwistCommand;
import twistrating.commands.RateTwistCommand;
import twistrating.events.TwistCreatedEvent;
import twistrating.events.TwistRatedEvent;
import twistrating.events.TwistRatingChangedEvent;

public class Rating extends AbstractAnnotatedAggregateRoot {
    @AggregateIdentifier
    private String twistId;

    public Rating() {
    }

    @CommandHandler
    public Rating(CreateTwistCommand command) {
        apply(new TwistCreatedEvent(command.getTwistId()));
    }

    @CommandHandler
    public void rateTwist(RateTwistCommand command) {
        // TODO: Validate command
        apply(new TwistRatedEvent(command.getTwistId(), command.getRating()));
    }

    @CommandHandler
    public void changeTwistRating(ChangeTwistRatingCommand command) {
        // TODO: Validate command
        apply(new TwistRatingChangedEvent(command.getTwistId(), command.getPreviousRating(), command.getNewRating()));
    }

    @EventHandler
    public void on(TwistCreatedEvent event) {
        twistId = event.getTwistId();
    }

    @EventHandler
    public void on(TwistRatedEvent event) {
        // Do nothing
    }
}
