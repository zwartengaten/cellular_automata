package demo;

import java.awt.Color;
import java.awt.Graphics;

public class Projectile {
	private int x, y;
	public boolean fired;

	public Projectile() {
		fired = false;
	}

	public void shoot(int startX, int startY) {
		if (!fired) {
			x = startX;
			y = startY;
			fired = true;
		}
	}

	public void move(double triangleAngle) {
		if (fired) {
			y -= 10;

			final double radians = Math.toRadians(triangleAngle);
			final int deltaX = (int) (Math.sin(radians) * 50); // Horizontal adjustment
			x += deltaX;

			if (y < 0)
				fired = false;
		}
	}
	public boolean checkCollision(int targetX, int targetY) {
		if (fired && (Math.abs(x - targetX) <= 20) && (Math.abs(y - targetY) <= 20)) {
			fired = false;
			return true;
		}
		return false;

	}
	public int[] getInfo(int[] target) {
		return new int[]{(x - target[0]),(y - target[1])};
	}

	public void draw(Graphics g) {
		if (fired) {
			g.setColor(Color.GREEN);
			g.fillRect(x - 3, y - 6, 6, 6);
		}
	}
}
