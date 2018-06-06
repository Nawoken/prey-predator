package m13;

import java.util.Random;

/**
 * Subclass of Element. Represents animals, e.g. preys and predators.
 * 
 * @author j.leflour
 */
public class Animal extends Element {

	/** Age of the animal */
	private int age;

	/**
	 * Boolean representing if the animal has reproduced during the current update
	 * or not (true if it has, false if it hasn't) x
	 */
	private boolean hasReproduced;

	/** Integer representing how long it has been since the animal ate x */
	private int lastate;

	/** Speed of the animal */
	private double speed;

	/** Boolean representing if the animal ate a plant in the last update */
	private boolean atePlant;

	/**
	 * Creates a new animal
	 */
	public Animal() {
		this.age = 0;
		this.hasReproduced = false;
		this.lastate = 0;
		this.speed = Simulation.SPEED;
		this.atePlant = false;
	}

	/**
	 * Returns the age of the animal
	 * 
	 * @return The age of the animal
	 */
	public int getAge() {
		return this.age;
	}

	/**
	 * Returns the attribute hasReproduced
	 * 
	 * @return The attribute hasReproduced
	 */
	public boolean getHasReproduced() {
		return this.hasReproduced;
	}

	/**
	 * Returns the attribute lastate
	 * 
	 * @return The attribute lastate
	 */
	public int getLastate() {
		return this.lastate;
	}

	/**
	 * Returns the speed of the animal
	 * 
	 * @return The speed of the animal
	 */
	public double getSpeed() {
		return this.speed;
	}

	/**
	 * Returns the atePlant boolean
	 * 
	 * @return The atePlant boolean
	 */
	public boolean getAtePlant() {
		return this.atePlant;
	}

	/**
	 * Sets the age of the animal to age
	 * 
	 * @param age
	 *            The age of the animal
	 */
	public void setAge(int age) {
		this.age = age;
	}

	/**
	 * Sets the attribute hasReproduced of the animal to hasReproduced
	 * 
	 * @param hasReproduced
	 *            Represents whether or not the animal has reproduced this update
	 */
	public void setHasReproduced(boolean hasReproduced) {
		this.hasReproduced = hasReproduced;
	}

	/**
	 * Sets the attribute lastate of the animal to lastate
	 * 
	 * @param lastate
	 *            The last time the animal ate
	 */
	public void setLastate(int lastate) {
		this.lastate = lastate;
	}

	/**
	 * Sets the speed of the animal to speed
	 * 
	 * @param speed
	 *            The speed of the animal
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
	}

	/**
	 * Sets the atePlant attribute to atePlant
	 * 
	 * @param atePlant
	 *            The boolean which value holds true if the animal ate a plant last
	 *            update
	 */
	public void setAtePlant(boolean atePlant) {
		this.atePlant = atePlant;
	}

	/**
	 * Moves the animal following a Brownian pattern e.g. the animal travels a
	 * distance d at a random angle from its previous position
	 * 
	 * @param d
	 *            The distance to travel
	 * @return A boolean checking if the animal got out of the simulation square
	 */
	public boolean moveBrownian(double d) {

		boolean gotOut = false;

		Random rand = new Random();
		double theta = 2 * Math.PI * rand.nextDouble();
		double x = this.getPos().get(this.getPos().size() - 1).getX() + d * Math.cos(theta);
		double y = this.getPos().get(this.getPos().size() - 1).getY() + d * Math.sin(theta);

		if ((x > Simulation.SPACE_SIZE) || (x < 0) || (y > Simulation.SPACE_SIZE) || (y < 0)) {
			gotOut = true;
		}

		this.addPos(new Position(x, y));

		return gotOut;

	}

	/**
	 * Moves the animal following a Brownian pattern e.g. the animal travels a
	 * distance d at a theta angle from its previous position
	 * 
	 * @param d
	 *            The distance to travel
	 * 
	 * @param theta
	 *            The direction the animal shall follow
	 * 
	 * @return A boolean checking if the animal got out of the simulation square
	 */
	public boolean moveBrownian(double d, double theta) {

		boolean gotOut = false;

		double x = this.getPos().get(this.getPos().size() - 1).getX() + d * Math.cos(theta);
		double y = this.getPos().get(this.getPos().size() - 1).getY() + d * Math.sin(theta);

		if ((x > Simulation.SPACE_SIZE) || (x < 0) || (y > Simulation.SPACE_SIZE) || (y < 0)) {
			gotOut = true;
		}

		this.addPos(new Position(x, y));

		return gotOut;

	}

	/**
	 * Checks if reproduction is possible between this animal and animal a.
	 * 
	 * The reproduction is possible if the two animals are within a REPROD square
	 * range from each other, if none of them has reproduced this update and if they
	 * are both above the reproduction age REPROD_AGE.
	 * 
	 * It is subject to an alpha probability that differs for preys and predators.
	 * 
	 * It is checked separately in the Simulation class if both animals are of the
	 * same type.
	 * 
	 * @param a
	 *            The other animal to reproduce with
	 * 
	 * @return A boolean checking if the two animals can reproduce
	 */
	public boolean isReproductionPossible(Animal a) {
		if ((this.getPos().get(this.getPos().size() - 1).withinRange(a.getPos().get(a.getPos().size() - 1),
				Simulation.REPROD)) && (!this.hasReproduced) && (!a.hasReproduced) && (this.age > Simulation.REPROD_AGE)
				&& (a.age > Simulation.REPROD_AGE)) {

			Random rand = new Random();
			double alpha;

			if (((Animal) a).getType().equals("prey")) {
				alpha = Simulation.ALPHA_PREY;
			} else {
				alpha = Simulation.ALPHA_PRED;
			}
			if (rand.nextDouble() < alpha) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Checks if this animal can eat element e. This animal can eat element e if
	 * element e and this animal are within a PRED square range from each other.
	 * 
	 * It is checked separately if the types are compatible for predation.
	 * (predators can eat both plants and preys, preys can only eat plants)
	 * 
	 * @param e
	 *            The element to be eaten
	 * @return A boolean checking if this animal can eat element e
	 */
	public boolean edible(Element e) {
		if (this.getPos().get(this.getPos().size() - 1).withinRange(e.getPos().get(e.getPos().size() - 1),
				Simulation.PRED)) {
			return true;
		} else {
			return false;
		}
	}

}
