package cellular.automata.visual;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class ICUCollorMapper implements Icmap {
	private Map<Short,Color> map ;

	public ICUCollorMapper(Map<Short, Color> map) {
		super();
		this.map = map;
	}

	public ICUCollorMapper() {
		super();
		this.map = new HashMap<>();
	}

	@Override
	public Color getColor(short x) {
		if(!map.containsKey(x))
			return Color.BLACK;
		return map.get(x);
	}

	public void addColor(short x, Color col) {
		this.map.put(x, col);
	}

	public void addColor(int x, Color col) {
		this.map.put((short) x, col);
	}


}
