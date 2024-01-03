package cellular.automata.electro;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import cellular.automata.visual.ICUVizualiz;
import cellular.automata.visual.Icmap;
import cellular.automata.visual.RandCmap;
import math.Point;

public class ElectroVisualDebug {
	public static Point p (int x, int y) {
		return new Point(x,y);
	}
	public static void main(String... args) {
		final Point[][] points = new Point[10][1];
		pointSet(points);

		final ElectruICU electro = new ElectruICU(100, 100, points);
		electro.init();
		final Icmap map = colorSetter();

		final CompletableFuture<Void> future1 = CompletableFuture
				.runAsync(()->{electroSet(points, electro);});
		final CompletableFuture<Void> future2 = CompletableFuture
				.runAsync(()->{
					final ICUVizualiz gui = new ICUVizualiz(electro, map);

					ICUVizualiz.exe(gui);
				});
		final CompletableFuture<Void> future = CompletableFuture.allOf(future1, future2);
		try {
			future.get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void electroSet(final Point[][] points, final ElectruICU electro) {

		electro.negativeCharge(points[9][0]);
		electro.negativeCharge(points[5][0]);
		electro.negativeCharge(points[4][0]);
		electro.positiveCharge(points[2][0]);
		electro.positiveCharge(points[1][0]);
		electro.positiveCharge(points[7][0]);
		//electro.negativeCharge(points[0][0]);
		electro.positiveCharge(points[3][0]);
		//electro.positiveCharge(points[6][0]);
		electro.negativeCharge(points[8][0]);
		//final Point p = points[0][0];
		//		final Point a = p(0,0);
		//		final Point b = p(99,99);
		//electro.negativeCharge(p);
		//electro.positiveCharge(points[4][0]);
		//		new Thread(() -> {
		//
		//			while(true) {
		//				//electro.negativeCharge(points[3][0]);
		//				//electro.negativeCharge(points[2][0]);
		//				//electro.positiveCharge(points[1][0]);
		//
		//				electro.movePoint(p,a);
		//				try {
		//					Thread.sleep(5);
		//				} catch (final InterruptedException e) {
		//					e.printStackTrace();
		//				}
		//				electro.movePoint(a,b);
		//				try {
		//					Thread.sleep(5);
		//				} catch (final InterruptedException e) {
		//					e.printStackTrace();
		//				}
		//				electro.movePoint(b,p);
		//
		//			}
		//		}).run();


	}

	public static void pointSet(final Point[][] points) {
		points[0][0] = p(99,0);

		points[1][0] = p(0,0); //
		points[2][0] = p(99,0); //
		points[3][0] = p(49,0); //
		points[4][0] = p(50,50);
		points[5][0] = p(99,99);
		points[6][0] = p(50,99); //
		points[7][0] = p(0,7); //
		points[8][0] = p(25,50);
		points[9][0] = p(0,99);


		//
		//		points[4][0] = p(100,100);
		//		points[5][0] = p(99,99);
		//		points[6][0] = p(101,100);
		//		points[7][0] = p(99,101);
		//		points[8][0] = p(101,101);
		//		points[9][0] = p(100,99);
		//		points[10][0] = p(101,99);
		//		points[11][0] = p(100,101);
		//		points[12][0] = p(99,100);
		//		//
		//		points[13][0] = p(1,100);
		//		points[14][0] = p(0,99);
		//		points[15][0] = p(2,100);
		//		points[16][0] = p(0,101);
		//		points[17][0] = p(2,101);
		//		points[18][0] = p(1,99);
		//		points[19][0] = p(2,99);
		//		points[20][0] = p(1,101);
		//		points[21][0] = p(0,100);

		//		points[13][0] = p(299,199);
		//		points[14][0] = p(299,198);
		//		points[15][0] = p(299,197);
		//		points[16][0] = p(298,199);
		//		points[17][0] = p(298,198);
		//		points[18][0] = p(298,197);
		//		points[19][0] = p(297,199);
		//		points[20][0] = p(297,198);
		//		points[21][0] = p(297,197);
	}

	public static Icmap colorSetter() {
		return new RandCmap();
	}
}
