package demo.cell;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class CellularAutomaton extends JPanel {
	private static final Color A_PARENT = Color.RED;
	private static final Color B_PARENT = Color.BLACK;
	private static final int PROCCESSORS = 100;
	private static final Color A_COLO = Color.decode("#FFA500");//Color.GREEN; ////
	private static final Color B_COLO = Color.decode("#000003");//Color.MAGENTA;//Color.decode("#000003");////Color.decode("#4A0063");
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static final int WIDTH = 1000;
	private static final int HEIGHT = 1000;
	private static final int CELL_SIZE = 1;
	private static final int SLEEP_TIME = 100;
	private static final int w = WIDTH / CELL_SIZE;
	//private static final int h = HEIGHT / CELL_SIZE;
	private static final int RATIO = 10;
	private ExecutorService executorService;
	//private Future<?> simulationFuture;

	private int[][] grid;
	private boolean running = true;
	private Random random = new Random();
	private int[][] oldgrid;

	public CellularAutomaton(int rows, int cols) {
		grid = new int[rows][cols];
		oldgrid = grid;
		initializeGrid();
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		//startSimulation();
	}

	private void initializeGrid() {
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++)
				grid[i][j] = Color.GRAY.getRGB();

		final int numRed = random.nextInt((grid.length * grid[0].length) / RATIO);
		final int numBlack = random.nextInt((grid.length * grid[0].length) / RATIO);

		for (int i = 0; i < numRed; i++) {
			final int x = random.nextInt(grid.length);
			//final int y = random.nextInt(grid[0].length);//(3*x) % w;//((50*x) + 40) % w;//

			final int y = (int) ((w/2)+Math.sqrt(((w*w)/4) - ((x-(w/2))*(x-(w/2))))) % w;
			//final int y = (random.nextInt(grid[0].length) % (w/2)) + (w/2);
			grid[x][y] = A_PARENT.getRGB();
			grid[x][(w - y)%w] = A_PARENT.getRGB();
			//grid[(w/2) - (x/2)][(w/2) - (y/2)] = A_PARENT.getRGB();
		}

		for (int i = 0; i < numBlack; i++) {
			final int x = random.nextInt(grid.length);
			//final int y = (int) ((w/2)-Math.sqrt(((w*w)/4) - ((x-(w/2))*(x-(w/2))))) % w;
			final int y = random.nextInt(grid[0].length);
			//final int y = - (random.nextInt(grid[0].length) % (w/2)) + (w/2);
			grid[x][y] = B_PARENT.getRGB();
			//grid[(w/4) + (x/2)][(w/4) + (y/2)] = B_PARENT.getRGB();
			//			grid[x][(w - y)%w] = B_PARENT.getRGB();
			//			grid[x / 6][y / 6] = B_PARENT.getRGB();
			//			grid[x / 6][((w - y)%w) / 6] = B_PARENT.getRGB();
		}
	}

	private Color getCellColor(int x, int y) {
		if ((x < 0) || (x >= grid.length) || (y < 0) || (y >= grid[0].length))
			return Color.GRAY;
		return new Color(grid[x][y]);
	}

	private Color getCellColor(int[][] gridd,int x, int y) {
		if ((x < 0) || (x >= gridd.length) || (y < 0) || (y >= gridd[0].length))
			return Color.GRAY;
		return new Color(gridd[x][y]);
	}

	private void updateGridParallelRegions() {
		final int[][] newGrid = new int[grid.length][grid[0].length];
		for(int i = 0; i < grid.length; i++)
			for(int j = 0; j < grid[i].length; j++)
				newGrid[i][j] = grid[i][j];
		//System.out.println(grid.length);

		final List<Future<?>> futures = new ArrayList<>();
		final int regionSize = grid.length / PROCCESSORS; // Adjust as needed

		for (int i = 0; i < grid.length; i += regionSize) {
			final int rowStart = i;
			final int rowEnd = Math.min(i + regionSize, grid.length);
			futures.add(executorService.submit(() -> {
				for (int row = rowStart; row < rowEnd; row++)
					for (int j = 0; j < (grid[row].length ); j++)
						processCell(newGrid, row, j);
			}));
		}

		for (final Future<?> future : futures)
			try {
				future.get(); // Wait for all tasks to complete
			} catch (final Exception e) {
				e.printStackTrace();
			}

		if (compareGrids(oldgrid, newGrid))
			running = false;
		if (compareGrids(grid, newGrid))
			running = false;


		oldgrid = grid;
		grid = newGrid;

		repaint();
	}


	private static final int[] ROW_OFFSETS = {-1, -1, -1, 0, 0, 1, 1, 1};
	private static final int[] COL_OFFSETS = {-1, 0, 1, -1, 1, -1, 0, 1};

	private void processCell(int[][] grid, int row, int col) {
		final Color currentColor = getCellColor(row, col);
		if (!currentColor.equals(Color.GRAY)) {
			Color newColor = currentColor;
			if(!currentColor.equals(A_PARENT) && !currentColor.equals(B_PARENT)&&(dir_neigh(grid, row, col, newColor, 0) <= 1))
				grid[row][col] = Color.GRAY.getRGB();
			else {
				Color opColor = currentColor;

				if (currentColor.equals(A_PARENT) || currentColor.equals(A_COLO)) {
					newColor = A_COLO;
					opColor = B_COLO;
				} else if (currentColor.equals(B_COLO) || currentColor.equals(B_PARENT)) {
					newColor = B_COLO;
					opColor = A_COLO;
				}

				for (int k = 0; k < ROW_OFFSETS.length; k++) {
					final int newRow = row + ROW_OFFSETS[k];
					final int newCol = col + COL_OFFSETS[k];

					if (isValidPosition(newRow, newCol, grid.length, grid[0].length)
							&& colorCheck(this.grid, newRow, newCol, opColor))
						grid[newRow][newCol] = newColor.getRGB();
				}
			}
		}
	}

	private boolean isValidPosition(int row, int col, int rowCount, int colCount) {
		return (row >= 0) && (row < rowCount) && (col >= 0) && (col < colCount);
	}

	public boolean colorCheck(final int[][] newGrid, int i, int j, Color opColor) {
		return getCellColor(newGrid,i, j).equals(Color.GRAY)
				|| (getCellColor(newGrid,i, j).equals(opColor) && weakCell(newGrid,i, j,opColor));
	}

	private boolean weakCell(int[][] a, int i, int j, Color c) {
		if(!getCellColor(a,i,j).equals(c))
			return false;
		int s = 0;
		int extr = 0;
		s = dir_neigh(a, i, j, c, s);
		extr = in_neigh(a, i, j, c, extr);
		//		final int g = dir_neigh(a, i, j, getParent(c), 0) + in_neigh(a, i, j, getParent(c), 0);
		//		final int f = dir_neigh(a, i, j, getParent(invr(c)), 0) + in_neigh(a, i, j, getParent(invr(c)), 0);
		//		if(f > 0) return true;
		//		if(g > 0) return false;
		//		if(g == 0)
		return  (s >= 2);// >= 2);
		//		return  (((s + extr) < 3));
	}
	//	private boolean weakCell1(int[][] a, int i, int j, Color c) {
	//		if(!getCellColor(a,i,j).equals(c))
	//			return false;
	//		int s = 0;
	//		int extr = 0;
	//		s = dir_neigh(a, i, j, c, s);
	//		extr = in_neigh(a, i, j, c, extr);
	//		final int g = dir_neigh(a, i, j, getParent(c), 0) + in_neigh(a, i, j, getParent(c), 0);
	//		final int f = dir_neigh(a, i, j, getParent(invr(c)), 0) + in_neigh(a, i, j, getParent(invr(c)), 0);
	//		if(f > 0) return true;
	//		if(g > 0) return false;
	//		if(c.equals(B_COLO)) {
	//		}//NOT THIS ONE
	//		return (((s + extr) < 4));// >= 2);
	//	}


	public int in_neigh(int[][] a, int i, int j, Color c, int extr) {
		if(getCellColor(a,i+1,j+1).equals(c)) extr++;
		if(getCellColor(a,i+1,j-1).equals(c)) extr++;
		if(getCellColor(a,i-1,j+1).equals(c)) extr++;
		if(getCellColor(a,i-1,j-1).equals(c)) extr++;
		return extr;
	}

	public int dir_neigh(int[][] a, int i, int j, Color c, int s) {
		if(getCellColor(a,i-1,j).equals(c)) s++;
		if(getCellColor(a,i+1,j).equals(c)) s++;
		if(getCellColor(a,i,j-1).equals(c)) s++;
		if(getCellColor(a,i,j+1).equals(c)) s++;
		return s;
	}
	@SuppressWarnings("unused")
	private Color getParent(Color x) {
		if( x.equals(A_COLO)) return A_PARENT; return B_PARENT;
	}
	@SuppressWarnings("unused")
	private Color invr(Color x) {
		if(x.equals(A_COLO)) return B_COLO; return A_COLO;
	}


	private boolean compareGrids(int[][] grid1, int[][] grid2) {
		for (int i = 0; i < grid1.length; i++)
			for (int j = 0; j < grid1[i].length; j++)
				if (grid1[i][j] != grid2[i][j])
					return false;


		return true;
	}


	public void startSimulationParallel() {
		final JFrame frame = new JFrame("Parallel Cellular Automaton");
		frame.setSize(WIDTH, HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		frame.setVisible(true);
		while (running) {
			//updateGridParallel();
			updateGridParallelRegions();
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			repaint();
		}

		executorService.shutdown();
		System.out.println("Over");
	}


	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++) {
				g.setColor(new Color(grid[i][j]));
				g.fillRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
			}
	}

	public static void main(String[] args) {
		//		SwingUtilities.invokeLater(() -> {
		//			final CellularAutomaton cellularAutomaton = new CellularAutomaton(WIDTH / CELL_SIZE, HEIGHT / CELL_SIZE);
		//			final Thread simulationThread = new Thread(cellularAutomaton::startSimulation);
		//			simulationThread.start();
		//		});
		SwingUtilities.invokeLater(() -> {
			final CellularAutomaton cellularAutomaton = new CellularAutomaton(WIDTH / CELL_SIZE, HEIGHT / CELL_SIZE);
			final Thread simulationThread = new Thread(cellularAutomaton::startSimulationParallel);
			simulationThread.start();
			//cellularAutomaton.updateGridParallelRegions();
		});
	}
}
