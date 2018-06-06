package m13;

import java.util.ArrayList;

/**
 * Master class of all the elements in the simulation
 * 
 * @author j.leflour
 */
public class Element {

	/**
	 * List of all the positions the element has had, the last one being the current
	 * one
	 */
	private ArrayList<Position> pos;

	/** Type of the element (plant, prey, predator) */
	private String type;

	/**
	 * Creates a new element with an empty list of positions
	 */
	public Element() {
		pos = new ArrayList<Position>();
	}

	/**
	 * Returns the list of positions
	 * 
	 * @return list of positions
	 */
	public ArrayList<Position> getPos() {
		return this.pos;
	}

	/**
	 * Returns the type of the element
	 * 
	 * @return A string equal to the element's type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Sets the elements type to parameter type
	 * 
	 * @param type
	 *            Type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Adds a position to the list of positions
	 * 
	 * @param p
	 *            Position to add
	 */
	public void addPos(Position p) {
		pos.add(p);
	}

	/**
	 * Removes a position from the list of positions
	 * 
	 * @param p
	 *            Position to remove
	 */
	public void removePos(Position p) {
		pos.remove(p);
	}

	/**
	 * Removes a position from the list of positions
	 * 
	 * @param i
	 *            Index of the position to remove
	 */
	public void removePos(int i) {
		pos.remove(i);
	}

}
