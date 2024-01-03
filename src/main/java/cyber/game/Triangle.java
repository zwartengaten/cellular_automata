package cyber.game;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class Triangle {
	public int x, y;
	public double angle; // Current rotation angle in radians
	public Projectile projectile;
	private Senzor senzor;
	//private ShootingGame game;
	public Triangle(int initialX, int initialY,ShootingGame game) {
		x = initialX;
		y = initialY;
		angle = 0.0;
		projectile = new Projectile();
		senzor = new Senzor(game);
	}

	public void moveLeft() {
		if (x > 20)
			x -= 10;
	}
	public void moveUp() {
		if (y > 20)
			y -= 10;
	}
	public void moveDown() {
		if (y < 580)
			y += 10;
	}

	public void moveRight() {
		if (x < 780)
			x += 10;
	}

	public void rotate(double deltaAngle) {
		angle += deltaAngle;
	}

	public void draw(Graphics g) {
		g.setColor(Color.WHITE);
		final int[] rotatedX = new int[]{x, x - 15, x + 15};
		final int[] rotatedY = new int[]{y, y + 20, y + 20};
		final int[] translatedX = new int[3];
		final int[] translatedY = new int[3];

		for (int i = 0; i < 3; i++) {
			final double tempX = rotatedX[i] - x;
			final double tempY = rotatedY[i] - y;
			translatedX[i] = (int) ((tempX * Math.cos(angle)) - (tempY * Math.sin(angle))) + x;
			translatedY[i] = (int) ((tempX * Math.sin(angle)) + (tempY * Math.cos(angle))) + y;
		}

		g.fillPolygon(translatedX, translatedY, 3);
	}
	public void shoot() {
		projectile.shoot(x,y);
	}

	public void start() {
		System.gc();
		shoot();
		senzor.open();
		while(projectile.fired) {
			senzor.observe();
			try {
				Thread.sleep(10);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		recalibrate();
	}
	private int[] prevprevData = new int[2];
	private int[] prevData = new int[2];
	private int times = 0;
	private void recalibrate() {
		final int[] data = senzor.get();
		//System.out.println(Arrays.toString(data));
		if(!Senzor.checkCollision(data[0], data[1])) {
			//System.out.println("yes");
			if((prevprevData[0] == data[0]) && (prevprevData[1] == data[1])) {
				times++;
				switch(times%5) {
				case 0:
					for(int i = 0; i < times; i++)
						this.moveUp();
					this.rotate(3.0);
					break;
				case 1:
					for(int i = 0; i < (times * 2); i++)
						this.moveDown();
					this.rotate(3.0);
					break;
				case 2:
					for(int i = 0; i < times; i++)
						this.moveLeft();
					this.rotate(3.0);
					break;
				case 3:
					for(int i = 0; i < times; i++)
						this.moveUp();
					this.rotate(3.0);
					break;
				default:
					for(int i = 0; i < times; i++)
						this.moveRight();
					this.rotate(3.0);
				}

			}

			if((data[0] < 0) && (data[1] < 0)){
				this.moveRight();
				this.rotate(1.0);
			}
			else
				if((data[0] > 0) && (data[1] < 0)) {
					this.moveLeft();
					this.rotate(-1.0);
				}

				else
					if((data[0] < 0) && (data[1] > 0)) {
						this.moveDown();
						this.rotate(4.17);
					}
					else{
						this.moveUp();
						this.rotate(-3.17);
					}
			prevprevData = prevData;
			prevData = data;
			start();
		}
	}
}
class Senzor{
	private List<int[]> data;
	private ShootingGame game;
	public Senzor(ShootingGame game) {
		this.data = new ArrayList<>();
		this.game = game;
	}
	public void open() {
		this.data = new ArrayList<>();
	}
	public void observe() {
		//		System.out.println(Arrays.toString(game.getInfo()));
		this.data.add(game.getInfo());
	}
	public int[] get() {
		final int[] result = new int[2];
		data.forEach(e ->{
			int mini = Integer.MAX_VALUE;
			final int k = (e[0]*e[0]) + (e[1]*e[1]);
			if(k < mini) {
				mini = k;
				result[0] = e[0];
				result[1] = e[1];
			}
		});
		return result;
	}
	public static boolean checkCollision(int a, int b) {
		return ((Math.abs(a) <= 20) && (Math.abs(b) <= 20));
	}

}