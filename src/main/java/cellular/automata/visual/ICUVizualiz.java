package cellular.automata.visual;

import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import cellular.automata.core.CyclicException;
import cellular.automata.core.IICU;
import math.Point;

public class ICUVizualiz extends JPanel{
	private int CELL_SIZE = 5;
	/**
	 *
	 */
	private static final long serialVersionUID = -882757079372131993L;
	private static final int SLEEP_TIME = 20;
	private static void executionDone(JFrame parentFrame) {
		JOptionPane.showMessageDialog(parentFrame,
				"Execution Completed!",
				"Success",
				JOptionPane.INFORMATION_MESSAGE);

		final int option = JOptionPane.showConfirmDialog(parentFrame,
				"Do you want to exit?",
				"Exit Confirmation",
				JOptionPane.YES_NO_OPTION);

		if (option == JOptionPane.YES_OPTION)
			System.exit(0);
	}

	public static Point p (int x, int y) {
		return new Point(x,y);
	}

	private IICU icu;

	private Icmap map;


	public ICUVizualiz(IICU icu, Icmap map) {
		this.icu = icu;
		this.map = map;
	}
	public ICUVizualiz(IICU icu, Icmap map, int cellsize) {
		this.icu = icu;
		this.map = map;
		this.CELL_SIZE = cellsize;
	}
	public static void exe(ICUVizualiz gui) {
		SwingUtilities.invokeLater(() -> {
			final Thread simulationThread = new Thread(gui::start);
			simulationThread.start();
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		final short[][] grid = icu.getGrid();
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++) {
				g.setColor(map.getColor(grid[i][j]));
				g.fillRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
			}
	}

	public void start() {
		final JFrame frame = new JFrame("Parallel Cellular Automaton");
		frame.setSize((icu.getGrid().length + 20)*CELL_SIZE, (icu.getGrid()[0].length + 40)*CELL_SIZE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		frame.setVisible(true);
		icu.init();
		while (icu.isRunning()) {
			try {
				icu.cycle();
			} catch (final CyclicException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			repaint();
		}

		icu.shutdown();
		executionDone(frame);
	}


}
