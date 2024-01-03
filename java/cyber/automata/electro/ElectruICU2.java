package cyber.automata.electro;

import java.util.Arrays;

import cyber.Point;
import cyber.automata.core.AbsICU;

public class ElectruICU2 extends AbsICU{
	public static double maxOf(double[] arr) {
		double maxx = Double.MIN_VALUE;
		final int n = arr.length;
		if(n == 0)
			return 0;
		for(int i = 0; i < n; i++)
			if(arr[i] > maxx)
				maxx = arr[i];
		return maxx;
	}

	public static Point p (int x, int y) {
		return new Point(x,y);
	}
	private short[] charges;
	private short[][] electroField;
	private boolean neutral[];
	private double diagonalSqr;
	private int[] counter;

	public ElectruICU2(int rows, int cols, Point[][] w) {
		super(rows, cols, w);
		this.electroField = new short[rows][cols];
		this.charges = new short[w.length + 2];
		this.neutral = new boolean[w.length + 2];
		this.counter = new int[w.length + 2];
		this.diagonalSqr = (rows * rows) + (cols * cols);
		Arrays.fill(neutral, true);
	}

	public short absTypeOf(Point p) {
		return abs(grid[p.x][p.y]);
	}

	public void applyCharge(Point p, int x) {
		if(grid[p.x][p.y] != 0) {
			charges[absTypeOf(p)] = (short) x;
			neutral[absTypeOf(p)] = isNeutral(x);
		}
		electroField[p.x][p.y] = (short) x;
	}

	/**
	 * the relative charge of the point
	 */
	public short chargeOf(Point p) {
		return typeOf(p) == 0
				? electroField[p.x][p.y]
						: charges[absTypeOf(p)];
	}

	private void circuit(Point p, Point q) {
		neutral[absTypeOf(p)] = true;
		neutral[absTypeOf(q)] = true;
	}

	@Override
	public boolean deathRule(int row, int col) {
		final int type = absTypeOf(p(row,col));
		if(isNeutral(p(row,col)) || himOrNeighGenerators(row,col))
			return false;
		if((counter[type] >= 1000)
				|| (sameTypeNeigh(row, col) >= 3)
				|| (sameTypeNeigh(row, col) <= 0)
				|| (nonnanNeigh(row,col) < 1)) {
			counter[type]--;
			return true;
		}
		return false;
	}


	private boolean himOrNeighGenerators(int row, int col) {
		if (isValidPosition(row, col, grid.length, grid[0].length)
				&& ((grid[row][col] < 0) || ((electroField[row][col]) != 0)))
			return true;
		for (int k = 0; k < ROW_OFFSETS.length; k++) {
			final int _row = row + ROW_OFFSETS[k];
			final int _col = col + COL_OFFSETS[k];
			if (isValidPosition(_row, _col, grid.length, grid[0].length)
					&& ((grid[_row][_col] < 0) || (electroField[_row][_col] != 0)))
				return true;
		}
		return false;
	}

	@Override
	protected double[] getExpansionData(int row, int col) {
		final double[] res = new double[8];
		final int n = grid.length;
		final int m = grid[0].length;
		for(int i = 0; i < n; i++)
			for(int j = 0; j < m; j++)
				if( ((chargeOf(p(i,j)) * chargeOf(p(row,col))) < 0))
					res[getDirection(row,col,i,j)] += holomorphicSummation(row, col, i, j);


		return res;
	}

	public double holomorphicSummation(int row, int col, int i, int j) {
		return abs(chargeOf(p(i,j))) * getDistanceHolomorphic(row,col,i,j);
	}

	public static int getDirection(int orX, int orY, int x, int y) {
		// Calculate the angle alpha in degrees
		final double deltaY =  y - orY;
		final double deltaX = x - orX;

		double alpha = Math.toDegrees(Math.atan2(deltaX, deltaY));
		// Ensure alpha is positive and in the range [0, 360)
		alpha = alpha - 270;
		if (alpha < 0)
			alpha += 360;
		if (alpha < 0)
			alpha += 360;
		// Calculate the direction based on alpha
		final int direction = (int) Math.floor((alpha + 22.5) / 45) % 8;
		return direction;
	}

	private double getDistanceHolomorphic(int orX, int orY, int x, int y) {
		// Calculate the angle alpha in degrees
		final double deltaY =  y - orY;
		final double deltaX = x - orX;

		final double distance =Math.sqrt( diagonalSqr / ((deltaX * deltaX) + (deltaY * deltaY)));
		return distance;
	}

	public boolean isNeutral(int x) {
		return x == 0;
	}

	public boolean isNeutral(Point p) {
		return neutral[absTypeOf(p)];
	}


	public void negativeCharge(Point p) {
		applyCharge(p, -1);
	}

	private int nonnanNeigh(int row, int col) {
		int sum = 0;
		for (int k = 0; k < ROW_OFFSETS.length; k++) {
			final int _row = row + ROW_OFFSETS[k];
			final int _col = col + COL_OFFSETS[k];
			if (isValidPosition(_row, _col, grid.length, grid[0].length)
					&& (grid[_row][_col] != NAN))
				sum++;
		}
		return sum;
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
	public void movePoint(Point p, Point y) {
		grid[y.x][y.y] = typeOf(p); System.out.println(typeOf(p));
		grid[p.x][p.y] = NAN;
		electroField[y.x][y.y] = electroField[p.x][p.y];
		electroField[p.x][p.y] = 0;
	}

	public void nullCharge(Point p) {
		applyCharge(p, 0);
	}

	public void positiveCharge(Point p) {
		applyCharge(p, 1);
	}

	/*
	 * 1. Do not eat any other types, only gray zone allowed.
	 * 2. If null charge, do nothing // neutral, do nothing;
	 * 3. Go in the direction with the most opposite electric
	 * 		charge. If multiple directions have maximum,
	 * 		allowed to go in all directions with maximum.
	 * 4. If neighbor has opposite charge, connect with it,
	 * 		form circuit, neutralize.
	 */
	@Override
	public boolean rule(int row, int col, int k, double... data) {

		final Point p = p(row, col);
		//System.out.println(charges[absTypeOf(p)]);
		if((chargeOf(p) == 0) || isNeutral(p) || (counter[absTypeOf(p)]>=1000))
			return false;
		//System.out.println("f");
		final Point q = p(row + ROW_OFFSETS[k],col + COL_OFFSETS[k]);
		final double max = maxOf(data);
		if(typeOf(q) == NAN)
			if( data[k] == max) {
				counter[absTypeOf(p)] ++;
				return true;
			}
			else return false;

		if((chargeOf(p) * chargeOf(q)) >= 0)
			return false;

		if(!isNeutral(q))
			circuit(p,q);


		return false;
	}

	public short typeOf(Point p) {
		return grid[p.x][p.y];
	}

	@Override
	public void setPoints(Point[][] points) {
		// TODO Auto-generated method stub

	}

}
