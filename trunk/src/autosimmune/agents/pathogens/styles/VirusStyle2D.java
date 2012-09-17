package autosimmune.agents.pathogens.styles;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import repast.simphony.visualizationOGL2D.StyleOGL2D;
import saf.v3d.ShapeFactory2D;
import saf.v3d.scene.Position;
import saf.v3d.scene.VSpatial;
import autosimmune.agents.pathogens.Virus;
import edu.umd.cs.piccolo.nodes.PImage;

public class VirusStyle2D implements StyleOGL2D<Virus> {

	@Override
	public int getBorderSize(Virus object) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Color getBorderColor(Virus object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getScale(Virus object) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Font getLabelFont(Virus object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getLabelXOffset(Virus object) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getLabelYOffset(Virus object) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Position getLabelPosition(Virus object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getLabelColor(Virus object) {
		// TODO Auto-generated method stub
		return null;
	}

	private ShapeFactory2D shapeFactory;
	private Image image;

	public void init(ShapeFactory2D shapeFactory) {
		this.shapeFactory = shapeFactory;
	}

	public VirusStyle2D(){
		
		String iconFile = "./icons/virus2.png";
		BufferedImage im = null;
		File file = new File(iconFile);
		
		if (!file.exists()){
			System.err.println("Arquivo " + iconFile + " não encontrado.");
		}
		
		try {
			im = ImageIO.read(file);
			PImage pimage = new PImage(im);
			AffineTransform trans = new AffineTransform();
			trans.setToScale(1, -1);
			pimage.transformBy(trans);
			image = pimage.toImage();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String getLabel(Virus object) {
		return null;
	}

	@Override
	public VSpatial getVSpatial(Virus object, VSpatial node) {
		if (node == null)
			node = shapeFactory.createCircle(5, 5);
		if (image == null){
			System.err.println("Erro ao carregar imagem da celula dendritica");
			//return null;
		}
		PImage pimage = new PImage(image);
		pimage.setBounds(new Rectangle2D.Float(0, 0, 5, 5));
		return node;
	}

	@Override
	public Color getColor(Virus object) {
		return Color.WHITE;
	}

	@Override
	public float getRotation(Virus object) {
		return 0;
	}

	
}
