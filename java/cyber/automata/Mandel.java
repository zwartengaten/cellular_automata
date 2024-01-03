package cyber.automata;

import cyber.Point;
import cyber.automata.core.AbsICU;

public class Mandel extends AbsICU{

	public Mandel(int rows, int cols, Point[][] w) {
		super(rows, cols, w);
		// TODO Auto-generated constructor stub
	}


	@Override
	public boolean deathRule(int row, int col) {
		if((sameTypeNeigh(row, col) >= 4)
				)//|| getDistanceHolomorphic((int)centerX, (int)centerY, col, row) > centerX/10 )
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
		// Demonstrative expansion data: Use the cell's coordinates for Mandelbrot-like criteria.
		double[] data = new double[256];
		// Calculate Mandelbrot-like criteria based on the cell's coordinates.
		double x = (col - grid[0].length / 2.0) / (grid[0].length / 4.0);
		double y = (row - grid.length / 2.0) / (grid.length / 4.0);
		for (int i = 0; i < 256; i++) {
			data[i] = mandelbrotCriteria(x, y);
			// Adjust the Mandelbrot-like criteria based on the direction.
			x += COL_OFFSETS[i % 8] * 0.1;
			y += ROW_OFFSETS[i % 8] * 0.1;
		}
		return data;
	}

	@Override
	public boolean rule(int row, int col, int k, double... data) {
		// Demonstrative expansion rule: Cells expand based on the Mandelbrot-like criteria.
		return data[k] > 0.3;
	}

	private double mandelbrotCriteria(double x, double y) {
		// Calculate a Mandelbrot-like criteria for a given (x, y) coordinate.
		double real = x;
		double imag = y;
		int maxIterations = 70;
		int n;
		for (n = 0; n < maxIterations; n++) {
			double real2 = real * real;
			double imag2 = imag * imag;
			if (real2 + imag2 > 4.0)
				break;
			imag = 2.0 * real * imag + y;
			real = real2 - imag2 + x;
		}
		return (double) n / maxIterations;
	}


	@Override
	public void setPoints(Point[][] points) {
		// TODO Auto-generated method stub

	}


}
