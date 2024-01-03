package demo.cell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import math.Point;

public class Clusterer {
	private static final String SHORT_NOT_INT = "More than 32,767 cell types is not allowed.";
	private static final int PROCCESSORS = 100;
	private static final int SLEEP_TIME = 10;
	private static final int[] ROW_OFFSETS = {-1, -1, -1, 0, 0, 1, 1, 1};
	private static final int[] COL_OFFSETS = {-1, 0, 1, -1, 1, -1, 0, 1};
	private static final int UPPER_LIMIT = 32;
	private static final short NAN = 0;

	private ExecutorService executorService;
	//private Future<?> simulationFuture;

	private short[][] grid;
	private boolean running = true;
	private short[][] oldgrid;
	private Point[][] w;




	public Clusterer(int rows, int cols, Point[][] w) {
		grid = new short[rows][cols];
		oldgrid = grid;
		this.w = w;
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

	private void updateGridParallelRegions() {
		final short[][] _grid = new short[grid.length][grid[0].length];
		copy_grid(_grid);

		final List<Future<?>> futures = new ArrayList<>();
		final int regionSize = Math.max(10,grid.length / PROCCESSORS); // Adjust as needed

		load_futures(_grid, futures, regionSize);

		for (final Future<?> future : futures)
			try {
				future.get(); // Wait for all tasks to complete
			} catch (final Exception e) {
				e.printStackTrace();
			}

		epilogue(_grid);
	}

	private void copy_grid(final short[][] _grid) {
		for(int i = 0; i < grid.length; i++)
			for(int j = 0; j < grid[i].length; j++)
				_grid[i][j] = grid[i][j];
	}

	private void load_futures(final short[][] _grid, final List<Future<?>> futures, final int regionSize) {
		for (int i = 0; i < grid.length; i += regionSize) {
			final int r_start = i;
			final int r_end = Math.min(i + regionSize, grid.length);
			futures.add(executorService.submit(() -> {
				for (int r = r_start; r < r_end; r++)
					for (int j = 0; j < grid[r].length; j++)
						go_sh(_grid, r, j);
			}));
		}
	}

	private void epilogue(final short[][] _grid) {
		if (compareGrids(oldgrid, _grid))
			running = false;
		if (compareGrids(grid, _grid))
			running = false;
		oldgrid = grid;
		grid = _grid;
	}

	private void go_sh(short[][] _grid, int row, int col) {
		final short old_sh = grid[row][col];
		if (old_sh != NAN) {
			final short new_sh = abs(old_sh);
			for (int k = 0; k < ROW_OFFSETS.length; k++) {
				final int _row = row + ROW_OFFSETS[k];
				final int _col = col + COL_OFFSETS[k];
				if (isValidPosition(_row, _col, _grid.length, _grid[0].length)
						&& coord_check(grid, _row, _col, new_sh))
					_grid[_row][_col] = new_sh;
			}
		}
	}

	private short abs(short x) {
		return (short) (x < 0 ? -x : x);
	}

	private boolean isValidPosition(int row, int col, int rowCount, int colCount) {
		return (row >= 0) && (row < rowCount) && (col >= 0) && (col < colCount);
	}

	public boolean coord_check(final short[][] _grid, int i, int j, short _sh) {
		return (_grid[i][j] == NAN)
				|| ((_grid[i][j] == _sh)
						&& weak_check(_grid,i, j,_sh));
	}

	private boolean weak_check(short[][] a, int i, int j, short c) {
		if(a[i][j] != c)
			return false;
		int s = 0;
		int extr = 0;
		s = dir_neigh(a, i, j, c, s);
		extr = in_neigh(a, i, j, c, extr);
		final int g = dir_neigh(a, i, j, getRoot(c), 0) + in_neigh(a, i, j, getRoot(c), 0);
		if(g > 0) return false;
		return g == 0 ? ((s + extr) <= 3) : ((s + extr) < 3);
	}



	public int in_neigh(short[][] a, int i, int j, short c, int extr) {
		if(((j+1) < a[i].length) && ((i+1) < a.length) &&(a[i+1][j+1] == c)) extr++;
		if(((i+1) < a.length) && (j > 0) && (a[i+1][j-1] == c)) extr++;
		if((i > 0) && ((j+1) < a[i].length) && (a[i-1][j+1] == c)) extr++;
		if((i > 0) && (j > 0) && (a[i-1][j-1] == c)) extr++;
		return extr;
	}

	public int dir_neigh(short[][] a, int i, int j, short c, int s) {
		if((i > 0) && (a[i-1][j] == c)) s++;
		if(((i+1) < a.length) && (a[i+1][j] == c)) s++;
		if((j > 0) && (a[i][j-1] == c)) s++;
		if(((j+1) < a[i].length) && (a[i][j+1] == c)) s++;
		return s;
	}
	private short getRoot(short x) {
		return (short) -abs(x);
	}


	private boolean compareGrids(short[][] grid1, short[][] grid2) {
		for (int i = 0; i < grid1.length; i++)
			for (int j = 0; j < grid1[i].length; j++)
				if (grid1[i][j] != grid2[i][j])
					return false;
		return true;
	}


	public short[][] start() {
		int count = 0;
		while (running) {
			updateGridParallelRegions();
			System.out.println("kk");
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			count++;
			if(count > UPPER_LIMIT) running = false;
		}

		executorService.shutdown();
		return this.grid;
	}
	public static Point p (int x, int y) {
		return new Point(x,y);
	}

	public static void main(String[] args) throws InterruptedException {

		final Point[][] l = new Point[2][2];
		l[0][0] = p(1,1);
		l[0][1] = p(1,2);
		l[1][0] = p(49,49);
		l[1][1] = p(49,48);
		final Clusterer clu = new Clusterer(50, 50, l );
		final Thread simulationThread = new Thread(()-> {clu.start();});
		simulationThread.start();
		while(simulationThread.isAlive())
			Thread.sleep(10000);
		final short[][] sh = clu.grid;
		for(int i = 0; i < 50; i++)
			System.out.println(Arrays.toString(sh[i]));


	}

}
