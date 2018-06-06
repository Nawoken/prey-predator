package m13;

/**
 * Represents a position with its cartesian coordinates
 * 
 * @author j.leflour
 */
public class Position {

	/** Cartesian coordinate x */
	private double x;

	/** Cartesian coordinate y */
	private double y;

	/**
	 * Creates a new position
	 * 
	 * @param x0
	 *            Cartesian coordinate x
	 * @param y0
	 *            Cartesian coordinate y
	 */
	public Position(double x0, double y0) {
		this.x = x0;
		this.y = y0;
	}

	/**
	 * Creates a new position
	 */
	public Position() {
		x = 0;
		y = 0;
	}

	/**
	 * Returns the x coordinate
	 * 
	 * @return The x coordinate
	 */
	public double getX() {
		return x;
	}

	/**
	 * Returns the y coordinate
	 * 
	 * @return The y coordinate
	 */
	public double getY() {
		return y;
	}

	/**
	 * Checks if the position is within dist range of pos.
	 * 
	 * The range is square, not circular, around the position, and it folds on the
	 * sides of the torus.
	 * 
	 * @param pos
	 *            Position being tested (does it belong to the range or not)
	 * 
	 * @param dist
	 *            Half of the size of a side of the square range
	 * 
	 * @return True if the pos belongs to the range, false if it doesn't
	 */
	public boolean withinRange(Position pos, double dist) {
		double xmoins = this.x - dist;
		double xplus = this.x + dist;
		double ymoins = this.y - dist;
		double yplus = this.y + dist;

		double resx = -1;
		double resy = -1;

		boolean foldplusx = false;
		boolean foldplusy = false;

		if (xmoins < 0) {
			resx = Simulation.SPACE_SIZE + xmoins;
			xmoins = 0;
		}

		if (ymoins < 0) {
			resy = Simulation.SPACE_SIZE + ymoins;
			ymoins = 0;
		}

		if (xplus > Simulation.SPACE_SIZE) {
			resx = xplus % Simulation.SPACE_SIZE;
			xplus = Simulation.SPACE_SIZE;
			foldplusx = true;
		}

		if (yplus > Simulation.SPACE_SIZE) {
			resy = yplus % Simulation.SPACE_SIZE;
			yplus = Simulation.SPACE_SIZE;
			foldplusy = true;
		}

		boolean exprx;
		boolean expry;

		if (resx == -1) {

			exprx = (xmoins <= pos.x) && (pos.x <= xplus);

			if (resy == -1) {
				expry = (ymoins <= pos.y) && (pos.y <= yplus);
			} else {
				if (foldplusy) {
					expry = ((ymoins <= pos.y) && (pos.y <= yplus)) || ((0 <= pos.y) && (pos.y <= resy));
				} else {
					expry = ((0 <= pos.y) && (pos.y <= yplus)) || ((resy <= pos.y) && (pos.y <= Simulation.SPACE_SIZE));
				}
			}

		} else {

			if (foldplusx) {

				exprx = ((xmoins <= pos.x) && (pos.x <= xplus)) || ((0 <= pos.x) && (pos.x <= resx));

				if (resy == -1) {
					expry = (ymoins <= pos.y) && (pos.y <= yplus);
				} else {
					if (foldplusy) {
						expry = ((ymoins <= pos.y) && (pos.y <= yplus)) || ((0 <= pos.y) && (pos.y <= resy));
					} else {
						expry = ((0 <= pos.y) && (pos.y <= yplus))
								|| ((resy <= pos.y) && (pos.y <= Simulation.SPACE_SIZE));
					}
				}

			} else {

				exprx = ((0 <= pos.x) && (pos.x <= xplus)) || ((resx <= pos.x) && (pos.x <= Simulation.SPACE_SIZE));

				if (resy == -1) {
					expry = (ymoins <= pos.y) && (pos.y <= yplus);
				} else {
					if (foldplusy) {
						expry = ((ymoins <= pos.y) && (pos.y <= yplus)) || ((0 <= pos.y) && (pos.y <= resy));
					} else {
						expry = ((0 <= pos.y) && (pos.y <= yplus))
								|| ((resy <= pos.y) && (pos.y <= Simulation.SPACE_SIZE));
					}
				}
			}
		}

		if (exprx && expry) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Computes the norm of the vector (this - pos). Doesn't take the torus into
	 * account.
	 * 
	 * @param pos
	 *            The other position needed to compute the vector
	 * 
	 * @return The norm of the vector (this - pos)
	 */
	public double norme(Position pos) {
		return Math.sqrt(Math.pow(this.x - pos.x, 2) + Math.pow(this.y - pos.y, 2));
	}

	/**
	 * Computes the theta = (Ox, AB) angle, AB being the (pos-this) vector
	 * 
	 * The angle is oriented to that if this moves at a theta angle, this goes
	 * towards pos
	 * 
	 * @param pos
	 *            The other position need to compute the vector
	 * 
	 * @return The norm of the vector (this - pos)
	 */
	public double angle(Position pos) {
		double sinTheta = (pos.y - this.y) / this.norme(pos);// cross product between Ox and AB
		double cosTheta = (pos.x - this.x) / this.norme(pos);// dot product between Ox and AB

		double theta = Math.atan(sinTheta / cosTheta);

		return theta;

	}

}
