package cellular.automata;
import cellular.automata.Auto.cmap;
import cellular.automata.core.IICU;
import math.Point;
public class VisualDebug {
	public static void main(String... args) {
		//mandel();
		//snow();
		entropy();
	}

	@SuppressWarnings("unused")
	private static void mandel() {
		final Point[][] points = pointSet2();
		IICU engine = new Mandel(300, 300, points);
		Auto auto = new Auto(points,engine,cmap.RAND);
		auto.set_cell_size(2);
		auto.run();
	}

	@SuppressWarnings("unused")
	private static void snow() {
		final Point[][] points = pointSet2();
		IICU engine = new Snow(300, 300, points);
		Auto auto = new Auto(points,engine,cmap.RAND);
		auto.set_cell_size(2);
		auto.run();
	}

	@SuppressWarnings("unused")
	private static void entropy() {
		final Point[][] points = pointSet2();
		IICU engine = new Spiral(300, 300, points);
		Auto auto = new Auto(points,engine,cmap.RAND);
		auto.set_cell_size(2);
		auto.run();
	}

	public static Point[][] pointSet() {
		final Point[][] points = new Point[10][1];
		points[0][0] = Auto.p(99,0);

		points[1][0] = Auto.p(0,49);
		points[2][0] = Auto.p(99,50);
		points[3][0] = Auto.p(49,0);
		points[4][0] = Auto.p(50,50);
		points[5][0] = Auto.p(99,99);
		points[6][0] = Auto.p(50,99);
		points[7][0] = Auto.p(51,25);
		points[8][0] = Auto.p(25,50);
		points[9][0] = Auto.p(0,75);
		return points;
	}
	public static Point[][] pointSet2() {
		int n = 150;
		final Point[][] points = new Point[n][n];

		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				points[i][j] = Auto.p(i * 2, j * 2); // Adjust the spacing as needed

		return points;
	}

}
