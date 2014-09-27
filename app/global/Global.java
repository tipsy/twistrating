package global;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.annotation.AggregateAnnotationCommandHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.SimpleEventBus;
import org.axonframework.eventhandling.annotation.AnnotationEventListenerAdapter;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventstore.EventStore;
import org.axonframework.eventstore.fs.FileSystemEventStore;
import org.axonframework.eventstore.fs.SimpleEventFileResolver;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.api.mvc.RequestHeader;
import play.libs.F.Promise;
import play.mvc.Result;
import twistrating.TwistRating;
import twistrating.TwistRatingEventStore;
import twistrating.aggregates.Rating;
import twistrating.commands.CreateTwistCommand;
import twistrating.eventhandlers.RatingEventHandler;
import twistrating.models.providers.TwistProvider;
import java.io.File;
import static play.mvc.Results.notFound;

public class Global extends GlobalSettings {
    private static final File eventStoreFolder = new File("./data/events/");
    private static final File twistsDataFile = new File("./conf/twists.json");

    private final Injector injector = createInjector();

    @Override
    public <A> A getControllerInstance(Class<A> controllerClass) throws Exception {
        return injector.getInstance(controllerClass);
    }

    public Promise<Result> onHandlerNotFound(RequestHeader request) {
        return Promise.<Result>pure(notFound(
                views.html.notFound.render(request.uri())
        ));
    }

    public void onStart(Application app) {
        Logger.info("Application has started");
    }

    public void onStop(Application app) {
        Logger.info("Application shutdown...");
    }

    private Injector createInjector() {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                // Event store
                CommandBus commandBus = new SimpleCommandBus();
                CommandGateway commandGateway = new DefaultCommandGateway(commandBus);

                EventStore eventStore = new FileSystemEventStore(new SimpleEventFileResolver(eventStoreFolder));

                EventBus eventBus = new SimpleEventBus();

                EventSourcingRepository repository = new EventSourcingRepository(Rating.class, eventStore);
                repository.setEventBus(eventBus);

                // Event store handlers
                AggregateAnnotationCommandHandler.subscribe(Rating.class, repository, commandBus);
                AnnotationEventListenerAdapter.subscribe(new RatingEventHandler(), eventBus);

                // Load twist config
                TwistProvider twistProvider = new TwistProvider(twistsDataFile);

                // Twist rating
                TwistRating twistRating = new TwistRatingEventStore(commandGateway, twistProvider);


                // Bootstrap aggregates
                twistProvider.getTwists().stream().forEach(twist -> {
                    commandGateway.send(new CreateTwistCommand(twist.id));
                });

                // Set up bindings
                bind(TwistRating.class).toInstance(twistRating);
                bind(TwistProvider.class).toInstance(twistProvider);

                Logger.info("Injector configured");
            }
        });
    }
}