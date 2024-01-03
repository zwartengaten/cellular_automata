package cellular.automata;

import cellular.automata.core.AbsICU;
import math.Point;

public class Snow extends AbsICU {

	public Snow(int rows, int cols, Point[][] w) {
		super(rows, cols, w);
	}

	@Override
	public boolean deathRule(int row, int col) {
		// Keep all cells alive.
		return false;
	}

	@Override
	protected double[] getExpansionData(int row, int col) {
		double[] data = new double[256];
		double x = (col - grid[0].length / 2.0) / (grid[0].length / 2.0);
		double y = (row - grid.length / 2.0) / (grid.length / 2.0);
		for (int i = 0; i < 256; i++)
			data[i] = snowflakeCriteria(x, y, i);
		return data;
	}

	@Override
	public boolean rule(int row, int col, int k, double... data) {
		return data[k] > 0.4;
	}

	private double snowflakeCriteria(double x, double y, int i) {
		double angle = i * (Math.PI / 128); // Use angles for symmetry.
		double radius = Math.sqrt(x * x + y * y);
		double angleDiff = Math.abs(Math.atan2(y, x) - angle);
		return Math.sin(6 * angleDiff) / (1 + radius * radius);
	}

	@Override
	public void setPoints(Point[][] points) {

	}
}
