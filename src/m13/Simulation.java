package m13;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Controls the behavior of the elements displayed by the graphical display.
 * 
 * For simplicity, the simulation elements simply consist in Circle instances,
 * that can be directly displayed by a graphical display.
 * 
 * Every time update() is called, the following sequence happens :
 * 
 * - The animals who are too old or didn't feed for too long die - Every PLANT
 * updates, size/50 new plants spawn - The animals move - The simulation
 * connects to the server and exchanges data regarding exiting and entering
 * animals - The predators eat the preys or plants they can eat (no limit on how
 * much they can eat) - The preys eat the plants they can eat (no limit on how
 * much they can eat) - The reproduction probability is updated and the animals
 * reproduce (only once) - The coordinates of the circles are updated
 * 
 * @author t.perennou (basic java code)
 * @author j.leflour (enhancements)
 *
 */
public class Simulation {

	/** The elements are positioned in a SPACE_SIZE x SPACE_SIZE 2D space */
	public static final int SPACE_SIZE = 400;

	/** Updates per second (Hz) */
	public static final int UPDATE_RATE_HZ = 15;

	/** Complete simulation duration (s) */
	public static final int DURATION_S = 40;

	/** Basic speed of animals */
	public static final int SPEED = 10;

	/** Speed of tired predators */
	public static final int TIRED_SPEED = 5;

	/** Speed of fed predators */
	public static final int FED_SPEED = 20;

	/** Duration of the fed state */
	public static final int FED_DURATION = 3;

	/** Age after which predators get tired */
	public static final int TIRED_AGE = 7;

	/** Predation distance */
	public static final int PRED = 10;

	/** Reproduction distance */
	public static final int REPROD = 10;

	/** Reproduction age */
	public static final int REPROD_AGE = 2;

	/** Reproduction probability for predators (is modified every update) */
	public static double ALPHA_PRED = 0.5;

	/** Reproduction probability for preys (is modified every update) */
	public static double ALPHA_PREY = 0.5;

	/** How long can a predator go without eating */
	public static final double LASTMEAL_PRED = 8;

	/** How long can a prey go without eating */
	public static final double LASTMEAL_PREY = 13;

	/** How long does a predator live */
	public static final double AGE_PRED = 15;

	/** How long does a prey live */
	public static final double AGE_PREY = 20;

	/** How long does it take for new plants to grow */
	public static final double PLANT = 5;

	/** The color of the background */
	public static final Color BACKGROUND = Color.BLACK;

	/** The color of a predator */
	public static final Color PREDATOR_COLOR = Color.RED;

	/** The color of a prey */
	public static final Color PREY_COLOR = Color.ORANGE;

	/** The color of a plant */
	public static final Color PLANT_COLOR = Color.GREEN;

	/** The radius of an element */
	public static final int ELEMENT_RADIUS = 2;

	/** Basic random number generator */
	private Random rand;

	/** Keeps track of the number of updates since model creation */
	private int nbUpdates;

	/** The elements under control */
	private ArrayList<Circle> circles;
	private ArrayList<Element> elements;

	/** Server elements */
	private DataInputStream input;
	private DataOutputStream output;
	private Socket socket;

	/**
	 * Returns the list of elements of the model.
	 */
	public ArrayList<Circle> getElements() {
		return circles;
	}

	/**
	 * Creates a model with the specified amount of elements.
	 */
	public Simulation(int nbElements) {
		System.out.println("Initializing simulation.");
		rand = new Random();
		nbUpdates = 0;
		circles = new ArrayList<Circle>();
		elements = new ArrayList<Element>();
		double x;
		double y;

		// On introduit initialement les prédateurs dans un coin et les proies dans
		// l'autre
		int nbPredators = nbElements / 2 - 10;
		for (int i = 0; i < nbPredators; i++) {
			x = SPACE_SIZE * rand.nextDouble();
			if (x < SPACE_SIZE / 2) {
				x += SPACE_SIZE / 2;
			}
			y = SPACE_SIZE * rand.nextDouble();
			if (y < SPACE_SIZE / 2) {
				y += SPACE_SIZE / 2;
			}
			elements.add(new Predator(x, y));
		}

		int nbPreys = nbElements / 2 - 10;
		for (int i = 0; i < nbPreys; i++) {
			x = SPACE_SIZE * rand.nextDouble();
			if (x > SPACE_SIZE / 2) {
				x -= SPACE_SIZE / 2;
			}
			y = SPACE_SIZE * rand.nextDouble();
			if (y > SPACE_SIZE / 2) {
				y -= SPACE_SIZE / 2;
			}
			elements.add(new Prey(x, y));
		}

		int nbPlants = nbElements - nbPreys - nbPredators;
		for (int i = 0; i < nbPlants; i++) {
			x = SPACE_SIZE * rand.nextDouble();
			y = SPACE_SIZE * rand.nextDouble();
			elements.add(new Plant(x, y));
		}

		for (Element element : elements) {
			if (element.getType().equals("prey")) {
				circles.add(new Circle(ELEMENT_RADIUS, PREY_COLOR));
			} else if (element.getType().equals("predator")) {
				circles.add(new Circle(ELEMENT_RADIUS, PREDATOR_COLOR));
			} else {
				circles.add(new Circle(ELEMENT_RADIUS, PLANT_COLOR));
			}
		}

		// Opening the socket
		try {
			socket = new Socket("127.0.0.1", 6789);
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Update the model elements. This method is periodically called by the
	 * graphical display.
	 */
	public void update() {

		nbUpdates++;
		System.out.println("Simulation update #" + nbUpdates);
		int size = elements.size();

		// The animals who are too old or didn't feed for too long die
		for (int i = 0; i < size; i++) {

			Element a = elements.get(i);

			if ((a.getType().equals("prey")) || a.getType().equals("predator")) {

				if (a.getType().equals("predator")) {
					if ((((Predator) a).getLastate() > LASTMEAL_PRED) || (((Animal) a).getAge() > AGE_PRED)) {
						elements.remove(a);
						circles.remove(i);
						size--;
						i--;
					}
				}

				if (a.getType().equals("prey")) {
					if ((((Animal) a).getAge() > AGE_PREY) || (((Animal) a).getLastate() > LASTMEAL_PREY)) {
						elements.remove(a);
						circles.remove(i);
						size--;
						i--;
					}
				}
			}
		}

		// Spawns size/50 new plants every PLANT updates

		if (nbUpdates % PLANT == 0) {

			rand = new Random();

			for (int i = 0; i < size / 50; i++) {
				double x = SPACE_SIZE * rand.nextDouble();
				double y = SPACE_SIZE * rand.nextDouble();
				elements.add(new Plant(x, y));
				circles.add(new Circle(ELEMENT_RADIUS, PLANT_COLOR));
				size++;
			}

		}

		// Moves the animals and checks if they are getting out or not

		int outPreys = 0;
		ArrayList<Prey> outPreysList = new ArrayList<Prey>();
		int outPreds = 0;
		ArrayList<Predator> outPredsList = new ArrayList<Predator>();

		for (int i = 0; i < size; i++) {

			Element a = elements.get(i);

			boolean gotOut = false;

			if (a.getType().equals("predator")) {

				if (((Animal) a).getLastate() < FED_DURATION) {
					((Animal) a).setSpeed(FED_SPEED);
				} else if ((((Animal) a).getLastate() >= FED_DURATION) && (((Animal) a).getLastate() < TIRED_AGE)) {
					((Animal) a).setSpeed(SPEED);
				} else {
					((Animal) a).setSpeed(TIRED_SPEED);
				}

				if (((Animal) a).getAtePlant()) {
					((Animal) a).setSpeed(((Animal) a).getSpeed() / 2);
					((Animal) a).setAtePlant(false);
				}

				double distMin = SPACE_SIZE;
				Prey closestPrey = null;

				for (int j = 0; j < size; j++) {

					Element prey = elements.get(j);

					if ((prey.getType().equals("prey")) && (a.getPos().get(a.getPos().size() - 1)
							.norme(prey.getPos().get(prey.getPos().size() - 1)) < distMin)) {
						closestPrey = (Prey) prey;
						distMin = a.getPos().get(a.getPos().size() - 1)
								.norme(prey.getPos().get(prey.getPos().size() - 1));
					}

				}

				Position predatorPos = a.getPos().get(a.getPos().size() - 1);

				if (closestPrey != null) {

					Position closestPreyPos = closestPrey.getPos().get(closestPrey.getPos().size() - 1);

					double visionRange = 2 * ((Animal) a).getSpeed();

					// If the closest prey is within range of the predator, the predator aims for
					// the prey and moves twice as fast; otherwise it just continues in the same
					// direction at its normal speed
					if (predatorPos.withinRange(closestPreyPos, visionRange)) {
						gotOut = ((Predator) a).moveBrownian(2 * ((Animal) a).getSpeed(),
								predatorPos.angle(closestPreyPos));
					} else {

						if (a.getPos().size() > 1) {
							double theta = Math.PI + predatorPos.angle(a.getPos().get(a.getPos().size() - 2));
							gotOut = ((Predator) a).moveBrownian(((Animal) a).getSpeed(), theta);
						} else {
							gotOut = ((Predator) a).moveBrownian(((Animal) a).getSpeed());
						}

					}

				} else {

					if (a.getPos().size() > 1) {
						double theta = Math.PI + predatorPos.angle(a.getPos().get(a.getPos().size() - 2));
						gotOut = ((Predator) a).moveBrownian(((Animal) a).getSpeed(), theta);
					} else {
						gotOut = ((Predator) a).moveBrownian(((Animal) a).getSpeed());
					}

				}

			}

			if (a.getType().equals("prey")) {

				((Animal) a).setSpeed(SPEED);

				if (((Animal) a).getAtePlant()) {
					((Animal) a).setSpeed(((Animal) a).getSpeed() / 2);
					((Animal) a).setAtePlant(false);

				}

				double distMin = SPACE_SIZE;
				Predator closestPred = null;

				for (int j = 0; j < size; j++) {

					Element pred = elements.get(j);

					if ((pred.getType().equals("predator")) && (a.getPos().get(a.getPos().size() - 1)
							.norme(pred.getPos().get(pred.getPos().size() - 1)) < distMin)) {
						closestPred = (Predator) pred;
						distMin = a.getPos().get(a.getPos().size() - 1)
								.norme(pred.getPos().get(pred.getPos().size() - 1));
					}

				}

				if (closestPred != null) {

					Position closestPredPos = closestPred.getPos().get(closestPred.getPos().size() - 1);
					Position preyPos = a.getPos().get(a.getPos().size() - 1);

					double visionRange = 2 * ((Animal) a).getSpeed();

					// If the closest predator is within range of the prey, the prey flees from the
					// predator twice as fast; otherwise it just moves randomly
					if (preyPos.withinRange(closestPredPos, visionRange)) {
						gotOut = ((Prey) a).moveBrownian(2 * ((Animal) a).getSpeed(),
								Math.PI + preyPos.angle(closestPredPos));
						// Preys can't reproduce when a predator is nearby
						((Animal) a).setHasReproduced(true);
					} else {
						gotOut = ((Prey) a).moveBrownian(((Animal) a).getSpeed());
					}

				} else {
					gotOut = ((Prey) a).moveBrownian(((Animal) a).getSpeed());
				}

			}

			if ((gotOut) && ((a.getType().equals("prey")))) {
				outPreys++;
				outPreysList.add(((Prey) a));
				elements.remove(a);
				circles.remove(i);
				i--;
				size--;
			}

			if ((gotOut) && ((a.getType().equals("predator")))) {
				outPreds++;
				outPredsList.add(((Predator) a));
				elements.remove(a);
				circles.remove(i);
				i--;
				size--;
			}
		}

		int remainingPreys = 0;
		int remainingPreds = 0;

		// Computing the number of remaining preys and predators
		for (int i = 0; i < size; i++) {

			Element a = elements.get(i);

			if ((a.getType().equals("prey"))) {
				remainingPreys++;
			}

			if ((a.getType().equals("predator"))) {
				remainingPreds++;
			}
		}

		// The simulation connects to the server
		try {

			// How much additional data is sent : two ints (lastate & age) for predators and
			// two ints (lastate & age) for preys
			// Speed doesn't have to be sent because it is updated right before it is used
			int preyData = 8;
			int predData = 8;
			if (nbUpdates == 1) {
				output.writeInt(preyData);
				output.writeInt(predData);
				output.flush();
			}

			// Writing the number of exiting and remaining preys and predators
			output.writeInt(remainingPreys);
			output.writeInt(remainingPreds);
			output.writeInt(outPreys);
			output.writeInt(outPreds);
			output.flush();

			int enteringPreys = input.readInt();
			int enteringPreds = input.readInt();

			System.out.println("Number of entering preys|predators : " + enteringPreys + "|" + enteringPreds);

			// For each prey, writing its normalized position and additional data, and
			// receiving the information back
			for (Prey p : outPreysList) {
				output.writeDouble(p.getPos().get(p.getPos().size() - 1).getX() / Simulation.SPACE_SIZE);
				output.writeDouble(p.getPos().get(p.getPos().size() - 1).getY() / Simulation.SPACE_SIZE);
				output.flush();

				double x = input.readDouble() * Simulation.SPACE_SIZE;
				double y = input.readDouble() * Simulation.SPACE_SIZE;

				// Additional data has to be sent in bytes
				byte[] ageBytes = ByteBuffer.allocate(4).putInt(p.getAge()).array();
				byte[] lastateBytes = ByteBuffer.allocate(4).putInt(p.getLastate()).array();
				byte[] toSend = new byte[predData];

				for (int i = 0; i < 4; i++) {
					toSend[i] = ageBytes[i];
				}

				for (int i = 4; i < predData; i++) {
					toSend[i] = lastateBytes[i - 4];
				}

				output.write(toSend);
				output.flush();

				byte[] received = new byte[predData];
				byte[] newAgeBytes = new byte[4];
				byte[] newLastateBytes = new byte[4];

				for (int i = 0; i < predData; i++) {
					received[i] = input.readByte();
				}

				for (int i = 0; i < 4; i++) {
					newAgeBytes[i] = received[i];
				}

				for (int i = 0; i < 4; i++) {
					newLastateBytes[i] = received[i + 4];
				}

				int age = ByteBuffer.wrap(newAgeBytes).getInt();

				int lastate = ByteBuffer.wrap(newLastateBytes).getInt();

				elements.add(new Prey(x, y, age, lastate));
				circles.add(new Circle(ELEMENT_RADIUS, PREY_COLOR));

				size++;
			}

			// For each predator, writing its normalized position and additional data
			for (Predator p : outPredsList) {
				output.writeDouble(p.getPos().get(p.getPos().size() - 1).getX() / Simulation.SPACE_SIZE);
				output.writeDouble(p.getPos().get(p.getPos().size() - 1).getY() / Simulation.SPACE_SIZE);
				output.flush();

				double x = input.readDouble() * Simulation.SPACE_SIZE;
				double y = input.readDouble() * Simulation.SPACE_SIZE;

				// Additional data has to be sent in bytes
				byte[] ageBytes = ByteBuffer.allocate(4).putInt(p.getAge()).array();
				byte[] lastateBytes = ByteBuffer.allocate(4).putInt(p.getLastate()).array();
				byte[] toSend = new byte[predData];

				for (int i = 0; i < 4; i++) {
					toSend[i] = ageBytes[i];
				}

				for (int i = 4; i < predData; i++) {
					toSend[i] = lastateBytes[i - 4];
				}

				output.write(toSend);
				output.flush();

				byte[] received = new byte[predData];
				byte[] newAgeBytes = new byte[4];
				byte[] newLastateBytes = new byte[4];

				for (int i = 0; i < predData; i++) {
					received[i] = input.readByte();
				}

				for (int i = 0; i < 4; i++) {
					newAgeBytes[i] = received[i];
				}

				for (int i = 0; i < 4; i++) {
					newLastateBytes[i] = received[i + 4];
				}

				int age = ByteBuffer.wrap(newAgeBytes).getInt();

				int lastate = ByteBuffer.wrap(newLastateBytes).getInt();

				elements.add(new Predator(x, y, age, lastate));
				circles.add(new Circle(ELEMENT_RADIUS, PREDATOR_COLOR));
				size++;

			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		for (int i = 0; i < size; i++) {

			Element a = elements.get(i);

			if ((a.getType().equals("prey")) || a.getType().equals("predator")) {

				// The predators eat the preys or plants they can eat
				if (a.getType().equals("predator")) {
					for (int j = 0; j < size; j++) {
						Element e = elements.get(j);
						if ((e.getType().equals("prey")) || (e.getType().equals("plant"))) {
							if (((Predator) a).edible(e)) {
								((Predator) a).setLastate(-1);
								elements.remove(e);
								circles.remove(j);
								size--;
								j--;

								if (e.getType().equals("plant")) {
									((Predator) a).setAtePlant(true);
								}
							}
						}

					}

				}

				// The preys eat the plants they can eat
				if (a.getType().equals("prey")) {
					for (int j = 0; j < size; j++) {
						Element e = elements.get(j);
						if (e.getType().equals("plant")) {
							if (((Prey) a).edible(e)) {
								((Prey) a).setAtePlant(true);
								((Prey) a).setLastate(-1);
								elements.remove(e);
								circles.remove(j);
								size--;
								j--;
							}
						}

					}

				}

			}
		}

		for (int i = 0; i < size; i++) {
			Element a = elements.get(i);
			if ((a.getType().equals("predator")) || (a.getType().equals("predator"))) {
				((Animal) a).setLastate(((Animal) a).getLastate() + 1);
			}
		}

		// The reproduction probability is a function of the number of animals
		int predNb = 0;
		int preyNb = 0;
		for (int i = 0; i < size; i++) {
			Element a = elements.get(i);
			if (a.getType().equals("predator")) {
				predNb++;
			}
			if (a.getType().equals("prey")) {
				preyNb++;
			}
		}

		ALPHA_PRED = Math.exp(-0.008 * predNb);
		ALPHA_PREY = Math.exp(-0.016 * preyNb);

		for (int i = 0; i < size; i++) {

			Element a = elements.get(i);

			if ((a.getType().equals("prey")) || a.getType().equals("predator")) {

				// The remaining animals of the same species reproduce when possible
				for (int j = 0; j < size; j++) {
					Element e = elements.get(j);

					if (e.getType().equals(a.getType())) {
						if (((Animal) a).isReproductionPossible((Animal) e)
								&& (elements.indexOf(a) != elements.indexOf(e))) {

							double x = a.getPos().get(a.getPos().size() - 1).getX();
							double y = a.getPos().get(a.getPos().size() - 1).getY();

							if (a.getType().equals("prey")) {
								elements.add(new Prey(x, y));
								circles.add(new Circle(ELEMENT_RADIUS, PREY_COLOR));
								size++;

							} else {
								elements.add(new Predator(x, y));
								circles.add(new Circle(ELEMENT_RADIUS, PREDATOR_COLOR));
								size++;
							}

							((Animal) a).setHasReproduced(true);
							((Animal) e).setHasReproduced(true);
						}
					}
				}
			}
		}

		for (Element a : elements) {

			// update the element coordinates
			Circle circle = circles.get(elements.indexOf(a));

			circle.setCenterX(a.getPos().get(a.getPos().size() - 1).getX());
			circle.setCenterY(a.getPos().get(a.getPos().size() - 1).getY());

		}

		for (Element a : elements) {
			if ((a.getType().equals("prey")) || a.getType().equals("predator")) {
				((Animal) a).setHasReproduced(false);
				((Animal) a).setAge(((Animal) a).getAge() + 1);
			}
		}

	}

	/**
	 * Indicates whether model updates are terminated.
	 */
	public boolean isTerminated() {
		return nbUpdates >= DURATION_S * UPDATE_RATE_HZ;
	}

	/**
	 * Cleanly terminates the simulation.
	 * 
	 * Here termination consists in stopping the program with System.exit().
	 */
	public void exit() {
		System.out.println("Exiting simulation and program. Bye.");
		try {
			input.close();
			output.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.exit(0);
	}

}
