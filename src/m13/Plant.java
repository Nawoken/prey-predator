package m13;

/**
 * Subclass of Element. Represents a plant in the simulation.
 * 
 * When animals eat plants, their speed is halved for the next move they make.
 * 
 * @author j.leflour
 */
public class Plant extends Element {

	/**
	 * Creates a new plant
	 * 
	 * @param x
	 *            The x coordinate of the plant
	 * 
	 * @param y
	 *            The y coordinate of the plant
	 */
	public Plant(double x, double y) {
		this.addPos(new Position(x, y));
		this.setType("plant");
	}

}
