package autosimmune.agents.cells.styles;

import java.awt.Color;
import java.awt.Font;

import repast.simphony.visualizationOGL2D.StyleOGL2D;
import saf.v3d.ShapeFactory2D;
import saf.v3d.scene.Position;
import saf.v3d.scene.VSpatial;
import autosimmune.agents.cells.PC;

public class PCStyle2D implements StyleOGL2D<PC>{

	private ShapeFactory2D shapeFactory;


	public void init(ShapeFactory2D shapeFactory) {
		this.shapeFactory = shapeFactory;
	}
	

	@Override
	public String getLabel(PC object) {
		return null;
	}

	@Override
	public VSpatial getVSpatial(PC obj, VSpatial node) {
		if (node == null)
			node = shapeFactory.createCircle(10, 10);
		return node;
		
	}

	@Override
	public Color getColor(PC object) {

		if(object.isRegenerated()){
			return new Color(255, 200, 200, 255);
		}
		
		switch(object.getState()){
			case STRESSED:
				return Color.ORANGE;
			
			case NORMAL: 
				return Color.WHITE;
			
			case NECROSIS:
				return Color.BLACK;
				
			case APOPTOSIS:
				return new Color(0, 255, 0, 255);
			
			default:
				return Color.WHITE;
		}
	}

	@Override
	public float getRotation(PC object) {
		return 0;
	}


	@Override
	public int getBorderSize(PC object) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public Color getBorderColor(PC object) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public float getScale(PC object) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public Font getLabelFont(PC object) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public float getLabelXOffset(PC object) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public float getLabelYOffset(PC object) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public Position getLabelPosition(PC object) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Color getLabelColor(PC object) {
		// TODO Auto-generated method stub
		return null;
	}

	

	

}
