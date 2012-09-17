package autosimmune.agents.styles;

import java.awt.Color;
import java.awt.Font;

import repast.simphony.visualizationOGL2D.StyleOGL2D;
import saf.v3d.ShapeFactory2D;
import saf.v3d.scene.Position;
import saf.v3d.scene.VSpatial;
import autosimmune.agents.Antibody;

public class AntibodyStyle2D implements StyleOGL2D<Antibody> {

	private ShapeFactory2D shapeFactory;

	public void init(ShapeFactory2D shapeFactory) {
		this.shapeFactory = shapeFactory;
	}

	@Override
	public String getLabel(Antibody object) {
		return null;
	}

	@Override
	public VSpatial getVSpatial(Antibody obj, VSpatial node) {
		if (node == null)
			node = shapeFactory.createCircle(5, 5);
		return node;
	}

	@Override
	public Color getColor(Antibody object) {

		if (object.gotVirus()) {
			return Color.GREEN;
		} else {
			return Color.MAGENTA;
		}

	}

	@Override
	public int getBorderSize(Antibody object) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Color getBorderColor(Antibody object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getRotation(Antibody object) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getScale(Antibody object) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Font getLabelFont(Antibody object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getLabelXOffset(Antibody object) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getLabelYOffset(Antibody object) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Position getLabelPosition(Antibody object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getLabelColor(Antibody object) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
