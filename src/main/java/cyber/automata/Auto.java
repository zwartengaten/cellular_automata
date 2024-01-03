package cyber.automata;

import java.awt.Color;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import cyber.Point;
import cyber.automata.core.IICU;
import cyber.automata.visual.ICUCollorMapper;
import cyber.automata.visual.ICUVizualiz;
import cyber.automata.visual.Icmap;
import cyber.automata.visual.RandCmap;

public class Auto {

	private Point[][] points;
	private IICU engine;
	private Icmap colors;
	private int cell_size = 5;

	public Auto(Point[][] points, IICU engine, Icmap colors) {
		this.points = points;
		this.engine = engine;
		this.colors = colors;
	}

	public Auto(Point[][] points, IICU engine, cmap type) {
		this.points = points;
		this.engine = engine;
		switch(type) {
		case DEFAULT -> colors =  color_set_0();
		case RAND -> colors = new RandCmap();
		}
	}

	public void set_cell_size(int s) {
		this.cell_size = s;
	}

	public void run() {
		engine.init();
		final CompletableFuture<Void> future1 = CompletableFuture
				.runAsync(()->{engine.setPoints(points);});
		final CompletableFuture<Void> future2 = CompletableFuture
				.runAsync(()->{
					final ICUVizualiz gui = new ICUVizualiz(engine, colors,cell_size);
					ICUVizualiz.exe(gui);
				});
		final CompletableFuture<Void> future = CompletableFuture.allOf(future1, future2);
		try {
			future.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

	}

	public static Icmap color_set_0() {
		ICUCollorMapper map = new ICUCollorMapper();
		map.addColor(1, Color.MAGENTA);
		map.addColor(-1, Color.MAGENTA);
		map.addColor(2, Color.GREEN);
		map.addColor(-2, Color.GREEN);
		map.addColor(3, Color.BLUE);
		map.addColor(-3, Color.BLUE);
		map.addColor(4, Color.ORANGE);
		map.addColor(-4, Color.ORANGE);
		map.addColor(5, Color.PINK);
		map.addColor(-5, Color.PINK);
		map.addColor(6, Color.RED);
		map.addColor(-6, Color.RED);
		map.addColor(7, Color.WHITE);
		map.addColor(-7, Color.WHITE);
		map.addColor(0, Color.GRAY);
		return map;
	}

	public static Point p (int x, int y) {
		return new Point(x,y);
	}

	public enum cmap{
		DEFAULT,
		RAND
	}
}
