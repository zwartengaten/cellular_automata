package cellular.automata.core;

import math.Point;

public interface IICU {
	short[][] getGrid();

	boolean rule(int row, int col, int k, double... data);

	void cycle() throws CyclicException;

	boolean isRunning();

	void init();

	void shutdown();

	boolean deathRule(int row, int col);

	void setPoints(Point[][] points);

}