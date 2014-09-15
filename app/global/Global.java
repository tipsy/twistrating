package global;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
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
import twistrating.TwistRating;
import twistrating.TwistRatingEventStore;
import twistrating.aggregates.Rating;
import twistrating.commands.CreateTwistCommand;
import twistrating.eventhandlers.RatingEventHandler;
import twistrating.models.providers.TwistProvider;

import java.io.File;

public class Global extends GlobalSettings {
    private static final File eventStoreFolder = new File("./data/events/");
    private static final File twistsDataFile = new File("./conf/twists.json");

    private static final String firebaseUrl = "https://radiant-heat-8671.firebaseio.com/twists/";
    private static final String firebareAuthToken = "bSPSHs8vdZt8SA30MLms69gxb9QqcBkT3KsRZCjC";

    private final Injector injector = createInjector();

    @Override
    public <A> A getControllerInstance(Class<A> controllerClass) throws Exception {
        return injector.getInstance(controllerClass);
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
                // Connect to firebase
                Firebase firebase = new Firebase(firebaseUrl);
                firebase.auth(firebareAuthToken, new Firebase.AuthListener() {
                    @Override
                    public void onAuthError(FirebaseError error) {
                        Logger.debug("Login Failed! " + error.getMessage());
                    }

                    @Override
                    public void onAuthSuccess(Object authData) {
                        Logger.debug("Login Succeeded!");
                    }

                    @Override
                    public void onAuthRevoked(FirebaseError error) {
                        Logger.debug("Authentication status was cancelled! " + error.getMessage());
                    }
                });

                // Event store
                CommandBus commandBus = new SimpleCommandBus();
                CommandGateway commandGateway = new DefaultCommandGateway(commandBus);

                EventStore eventStore = new FileSystemEventStore(new SimpleEventFileResolver(eventStoreFolder));

                EventBus eventBus = new SimpleEventBus();

                EventSourcingRepository repository = new EventSourcingRepository(Rating.class, eventStore);
                repository.setEventBus(eventBus);

                // Event store handlers
                AggregateAnnotationCommandHandler.subscribe(Rating.class, repository, commandBus);
                AnnotationEventListenerAdapter.subscribe(new RatingEventHandler(firebase), eventBus);

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
                bind(Firebase.class).toInstance(firebase);

                Logger.info("Injector configured");
            }
        });
    }
}