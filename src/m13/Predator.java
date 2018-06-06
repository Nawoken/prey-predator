package m13;

/**
 * Subclass of Animal. Represents a predator in the simulation.
 * 
 * @author j.leflour
 */
public class Predator extends Animal {

	/**
	 * Creates a new predator
	 * 
	 * @param x
	 *            The x coordinate of the predator
	 * 
	 * @param y
	 *            The y coordinate of the predator
	 */
	public Predator(double x, double y) {
		this.setType("predator");
		this.addPos(new Position(x, y));
	}

	/**
	 * Creates a new predator
	 * 
	 * @param x
	 *            The x coordinate of the predator
	 * 
	 * @param y
	 *            The y coordinate of the predator
	 * 
	 * @param age
	 *            The age of the predator
	 * 
	 * @param lastate
	 *            The last time the predator ate
	 */
	public Predator(double x, double y, int age, int lastate) {
		this.setType("predator");
		this.addPos(new Position(x, y));
		this.setLastate(lastate);
		this.setAge(age);
	}

}
