package cellular.automata.visual;

import java.awt.Color;

public class RandCmap implements Icmap {

	@Override
	public Color getColor(short x) {
		if (x < 0)
			x = (short) -x;

		x = (short) (Short.MAX_VALUE - x);

		int r = (x & 0xFF);
		int g = ((x >> 8) & 0xFF);
		int b = ((x >> 16) & 0xFF);

		r = (r + x * 8) % 256;
		g = (g + x * 14126) % 256;
		b = (b + x * 3242) % 256;

		return new Color(r, g, b);
	}
}

