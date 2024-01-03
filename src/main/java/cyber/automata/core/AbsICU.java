package cyber.automata.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cyber.Point;

public abstract class AbsICU implements IICU {
	public static final int[] COL_OFFSETS = { 0,  1, 1, 1, 0, -1, -1, -1};
	public static final short NAN = 0;
	public static final int[] ROW_OFFSETS = {-1, -1, 0, 1, 1,  1,  0, -1};
	private static final String SHORT_NOT_INT = "More than 32,767 cell types is not allowed.";

	public static Point p (int x, int y) {
		return new Point(x,y);
	}
	private int cpus;
	private ExecutorService executorService;
	public short[][] grid;
	private short[][] prevGrid;

	private boolean running = true;
	private Point[][] w;

	public AbsICU(int rows, int cols, Point[][] w) {
		cpus = 10;
		grid = new short[rows][cols];
		prevGrid = grid;
		this.w = w;
	}

	public short abs(short x) {
		return (short) (x < 0 ? -x : x);
	}

	public boolean compareGrids(short[][] grid1, short[][] grid2) {
		for (int i = 0; i < grid1.length; i++)
			for (int j = 0; j < grid1[i].length; j++)
				if (grid1[i][j] != grid2[i][j])
					return false;
		return true;
	}

	public void copyGrid(final short[][] _grid) {
		for(int i = 0; i < grid.length; i++)
			for(int j = 0; j < grid[i].length; j++)
				_grid[i][j] = grid[i][j];
	}

	@Override
	public void cycle() throws CyclicException {
		System.gc();
		if(!running)
			throw new CyclicException();
		final short[][] _grid = new short[grid.length][grid[0].length];
		copyGrid(_grid);

		final List<Future<?>> futures = new ArrayList<>();
		final int regionSize = Math.max(1,grid.length / cpus); // Adjust as needed

		loadFutures(_grid, futures, regionSize);

		for (final Future<?> future : futures)
			try {
				future.get(); // Wait for all tasks to complete
			} catch (final Exception e) {
				e.printStackTrace();
			}

		epilogue(_grid);
	}

	@Override
	public abstract boolean deathRule(int row, int col);

	public void epilogue(final short[][] _grid) {
		if (compareGrids(prevGrid, _grid))
			running = false;
		if (compareGrids(grid, _grid))
			running = false;
		prevGrid = grid;
		grid = _grid;
	}

	private void evolve(short[][] _grid, int row, int col) {
		final short old_sh = grid[row][col];

		if (old_sh != NAN) {
			//boolean bool = false;
			final short expansionType = abs(old_sh);
			final double[] data = getExpansionData(row,col);
			if(deathRule(row, col))//!bool &&
				_grid[row][col] = NAN;
			else
				for (int k = 0; k < ROW_OFFSETS.length; k++) {
					final int _row = row + ROW_OFFSETS[k];
					final int _col = col + COL_OFFSETS[k];
					if (isValidPosition(_row, _col, _grid.length, _grid[0].length)
							&& rule(row, col, k, data))
						//bool = true;
						_grid[_row][_col] = expansionType;
				}

		}
	}

	protected abstract double[] getExpansionData(int row, int col);

	@Override
	public short[][] getGrid() {
		return this.grid;
	}

	@Override
	public void init() {
		running = true;
		initializeGrid();
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	}

	private void initializeGrid() {
		final int n = w.length;
		if (n > Short.MAX_VALUE)
			throw new RuntimeException(SHORT_NOT_INT);
		for(int j = 0; j < n; j++) {
			final Point[] v = w[j];
			final int n1 = v.length;
			for (int i = 0; i < n1; i++)
				grid[v[i].x][v[i].y] = (short) -(j+1);
		}
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	public boolean isValidPosition(int row, int col, int rowCount, int colCount) {
		return (row >= 0) && (row < rowCount) && (col >= 0) && (col < colCount);
	}

	private void loadFutures(final short[][] _grid, final List<Future<?>> futures, final int regionSize) {
		for (int i = 0; i < grid.length; i += regionSize) {
			final int r_start = i;
			final int r_end = Math.min(i + regionSize, grid.length);
			futures.add(executorService.submit(() -> {
				for (int r = r_start; r < r_end; r++)
					for (int j = 0; j < grid[r].length; j++)
						evolve(_grid, r, j);
			}));
		}
	}

	@Override
	public abstract boolean rule( int row, int col, int k, double... data);

	public void setCPUs(int cpus) {
		this.cpus = cpus;
	}

	@Override
	public void shutdown() {
		executorService.shutdown();
	}

}

