package cellular.automata;

import cellular.automata.core.AbsICU;
import math.Point;

public class Spiral extends AbsICU {

	private double centerX;
	private double centerY;

	public Spiral(int rows, int cols, Point[][] w) {
		super(rows, cols, w);
		this.centerX = grid[0].length / 2.0;
		this.centerY = grid.length / 2.0;
	}
	@Override
	public boolean deathRule(int row, int col) {
		if((sameTypeNeigh(row, col) >= 2))
			return true;
		return false;
	}

	private int sameTypeNeigh(int row, int col) {
		int sum = 0;
		for (int k = 0; k < ROW_OFFSETS.length; k++) {
			final int _row = row + ROW_OFFSETS[k];
			final int _col = col + COL_OFFSETS[k];
			if (isValidPosition(_row, _col, grid.length, grid[0].length)
					&& (grid[_row][_col] == grid[row][col]))
				sum++;
		}
		return sum;
	}

	@Override
	protected double[] getExpansionData(int row, int col) {
		double[] data = new double[8];
		for (int i = 0; i < 8; i++) {
			double x = col - centerX;
			double y = row - centerY;
			double direction = Math.atan2(y, x);

			if (Math.abs(Math.sin(direction - i * Math.PI / 4)) < 0.6)
				data[i] = linearExpansionCriteria(row, col, i * Math.PI / 4, i);
			else
				data[i] = 0.0;
		}
		return data;
	}

	@Override
	public boolean rule(int row, int col, int k, double... data) {
		if (data[k] < 0.0000001)
			return false;
		return true;
	}

	private double linearExpansionCriteria(double x, double y, double direction, int i) {
		double angleDiff = Math.abs(Math.atan2(y, x) - direction);
		return Math.sin( angleDiff);
	}

	@Override
	public void setPoints(Point[][] points) {
	}

}
