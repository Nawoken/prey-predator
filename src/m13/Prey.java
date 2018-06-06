package m13;

/**
 * Subclass of Animal. Represents a prey in the simulation.
 * 
 * @author j.leflour
 */
public class Prey extends Animal {

	/**
	 * Creates a new prey
	 * 
	 * @param x
	 *            The x coordinate of the prey
	 * 
	 * @param y
	 *            The y coordinate of the prey
	 */
	public Prey(double x, double y) {
		this.setType("prey");
		this.addPos(new Position(x, y));
	}

	/**
	 * Creates a new prey
	 * 
	 * @param x
	 *            The x coordinate of the prey
	 * 
	 * @param y
	 *            The y coordinate of the prey
	 * 
	 * @param age
	 *            The age of the prey
	 * 
	 * @param lastate
	 *            The last time the prey ate
	 */
	public Prey(double x, double y, int age, int lastate) {
		this.setType("prey");
		this.addPos(new Position(x, y));
		this.setAge(age);
		this.setLastate(lastate);
	}

}
