import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;
import twistrating.aggregates.Rating;
import twistrating.commands.RateTwistCommand;
import twistrating.events.TwistRatedEvent;

public class TwistRatingTest {
    private FixtureConfiguration fixture;

    @Before
    public void setUp() {
        fixture = Fixtures.newGivenWhenThenFixture(Rating.class);
//        MyCommandHandler myCommandHandler = new MyCommandHandler(fixture.createGenericRepository(MyAggregate.class));
//        fixture.registerAnnotatedCommandHandler(myCommandHandler);
    }

    @Test
    public void testRateTwist() throws Exception {
       fixture.given()
        .when(new RateTwistCommand("twist1", 1))
        .expectEvents(new TwistRatedEvent("twist1", 1));
    }

    @Test
    public void testRateTwistTwoTimes() throws Exception {
        fixture.given(new RateTwistCommand("twist1", 1))
                .when(new RateTwistCommand("twist1", 0))
                .expectEvents(new TwistRatedEvent("twist1", 1), new RateTwistCommand("twist1", 0));
    }
}
