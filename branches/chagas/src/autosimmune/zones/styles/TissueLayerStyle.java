package autosimmune.zones.styles;

import java.awt.Color;

import repast.simphony.valueLayer.ValueLayer;
import repast.simphony.visualizationOGL2D.ValueLayerStyleOGL;


public class TissueLayerStyle implements ValueLayerStyleOGL{

	private ValueLayer vl;
	
	@Override
	public void init(ValueLayer layer) {
		vl = layer;
		System.out.println(layer.getName());
	}


	@Override
	public float getCellSize() {
		return 15;
	}

	
	@Override
	public Color getColor(double... coordinates) {
		int v = (int) vl.get(coordinates);
		if (v > 255) return new Color(255,0,0,255);
		return new Color(v, 0, 0, 255);	
	}

	

}
