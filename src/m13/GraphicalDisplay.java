package m13;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Main class launching a graphical display associated with a simulation. This
 * class creates the simulation and updates it periodically until the simulation
 * terminates.
 * 
 * DO NOT EDIT THIS CLASS. Edit the Simulation class instead.
 * 
 * This class is based on the Java FX toolkit included in Oracle Java.
 * 
 * @author t.perennou
 *
 */
public class GraphicalDisplay extends Application {

	/** Refresh image period (milliseconds) */
	static public final Duration PERIOD_MS = Duration.millis(1000 / Simulation.UPDATE_RATE_HZ);

	/** The simulation that contains and updates elements to display */
	private Simulation simulation;

	/** Root of the Java FX scene graph containing all the elements to display */
	private Group root;

	/** Serves as a clock that periodically triggers simulation updates */
	private Timeline timeline;

	/**
	 * Initialize the graphical display.
	 * 
	 * In a Java FX application, this is a mandatory replacement of the constructor.
	 */
	@Override
	public void start(Stage primaryStage) {
		// Create the root of the graph scene. It is mainly used in the updateScene()
		// method.
		root = new Group();

		// Create a simulation with 220 elements
		simulation = new Simulation(220);

		// Configure and start periodic scene update: after PERIOD_MS ms, updateScene()
		// is called.
		timeline = new Timeline(new KeyFrame(PERIOD_MS, ae -> updateScene())); // "->" is a Java 8 specific construction
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();

		// Show a graphical window with all the graph scene content
		primaryStage.setScene(new Scene(root, Simulation.SPACE_SIZE, Simulation.SPACE_SIZE, Simulation.BACKGROUND));
		primaryStage.setTitle("Swaggest Animation Ever");
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	/**
	 * Update the Java FX scene graph
	 */
	public void updateScene() {
		// check whether updates should be stopped
		if (simulation.isTerminated()) {
			timeline.stop();
			simulation.exit();
		} else {
			// first update the elements coordinates
			simulation.update();

			// then update the scene graph (in a rather brutal way)
			root.getChildren().clear();
			root.getChildren().addAll(simulation.getElements());
		}
	}

	/**
	 * Application main method: launch() creates an instance of this class and calls
	 * start().
	 * 
	 * @param args
	 *            dUnused
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
