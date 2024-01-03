package cyber.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class ShootingGame extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private Timer timer;
	private Triangle triangle;
	private GameObject object;
	private Projectile projectile;

	public ShootingGame() {
		setPreferredSize(new Dimension(800, 600));
		setBackground(Color.BLACK);

		timer = new Timer(10, this);
		timer.start();

		triangle = new Triangle(400, 500, this);
		object = new GameObject((int) (Math.random() * 700) + 50, 50);
		projectile = triangle.projectile;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == timer) {
			projectile.move(triangle.angle);

			if (projectile.checkCollision(object.getX(), object.getY())) {
				System.out.println("Target Hit!");
				object.reposition();
			}


			repaint();
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		triangle.draw(g);
		object.draw(g);
		projectile.draw(g);
	}



	class GameObject {
		private int x, y;

		public GameObject(int initialX, int initialY) {
			x = initialX;
			y = initialY;
		}

		public void reposition() {
			x = (int) (Math.random() * 700) + 50;
			y = (int) (Math.random() * 500) + 50;
		}

		public void draw(Graphics g) {
			g.setColor(Color.RED);
			g.fillRect(x - 10, y - 10, 20, 20);
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
	}
	private int[] getTargetInfo() {
		return new int[]{this.object.getX(),this.object.getY()};
	}
	public int[] getInfo() {
		return projectile.getInfo(getTargetInfo());
	}

	public static void main(String[] args) throws InterruptedException {
		final JFrame frame = new JFrame("Shooting Game");
		final ShootingGame game = Context.context.getBean("game", ShootingGame.class);
		frame.add(game);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		// Call the methods based on your logic
		game.triangle.start();
	}
}