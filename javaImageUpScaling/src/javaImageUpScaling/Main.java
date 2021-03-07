package javaImageUpScaling;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {

	public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {

    	UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    	ImageIcon icon = new ImageIcon("src/icon.png");
    	
    	JOptionPane.showMessageDialog(null, "Java Image Upscaling tool\nClick OK to proceed to select a photo (JPEG/PNG)\nSelected photo will be 2x upscaled\nSaved photo will be in PNG format", "Java Imageupscaling Tool", JOptionPane.INFORMATION_MESSAGE, icon);

		//read image
		final JFileChooser chooser = new JFileChooser();
		chooser.showOpenDialog(null);
		File raw = chooser.getSelectedFile();
		if (raw == null) {
			JOptionPane.showMessageDialog(null, "Action terminated");
            System.exit(1);
		}
		BufferedImage rawImage = ImageIO.read(raw);
		if(rawImage == null) {
			JOptionPane.showMessageDialog(null, "Incorrect file format", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
		}

		//initialize new image
		BufferedImage newImage = new BufferedImage(2 * rawImage.getWidth() ,
				2 * rawImage.getHeight() , BufferedImage.TYPE_INT_RGB);

		//old image dimensions
		System.out.println(rawImage.getHeight());
		System.out.println(rawImage.getWidth());

		//new image dimensions
		System.out.println(newImage.getHeight());
		System.out.println(newImage.getWidth());

		//placing the old image in a new image
		for (int i = 0; i <= rawImage.getWidth() - 2; i++) {
			for (int j = 0; j <= rawImage.getHeight() - 2; j++) {
				newImage.setRGB(2 * i, 2 * j, rawImage.getRGB(i, j));
			}
		}

		//vertical sampling
		for (int i = 0; i <= newImage.getWidth() - 8; i += 2) {
			for (int j = 0; j <= newImage.getHeight() - 8; j += 2) {

				Color c = new Color(newImage.getRGB(i, j));
				Color d = new Color(newImage.getRGB(i, j + 2));
				Color e = new Color((c.getRed() + d.getRed()) / 2,
						(c.getGreen() + d.getGreen()) / 2,
						(c.getBlue() + d.getBlue()) / 2);

				newImage.setRGB(i, j + 1, e.getRGB());
			}
		}

		//horizontal sampling
		for (int i = 0; i <= newImage.getWidth() - 8; i += 2) {
			for (int j = 0; j <= newImage.getHeight() - 8; j += 2) {

				Color c = new Color(newImage.getRGB(i, j));
				Color d = new Color(newImage.getRGB(i + 2, j));
				Color e = new Color((c.getRed() + d.getRed()) / 2,
						(c.getGreen() + d.getGreen()) / 2,
						(c.getBlue() + d.getBlue()) / 2);

				newImage.setRGB(i + 1, j, e.getRGB());
			}
		}

		//final sampling
		for (int i = 1; i <= newImage.getWidth() - 2; i += 2) {
			for (int j = 1; j <= newImage.getHeight() - 2; j += 2) {

				if (newImage.getRGB(i, j) == -16777216) {

					Color c = new Color(newImage.getRGB(i, j + 1));
					Color d = new Color(newImage.getRGB(i, j - 1));
					Color e = new Color(newImage.getRGB(i + 1, j));
					Color f = new Color(newImage.getRGB(i - 1, j));				
					Color w = new Color(newImage.getRGB(i + 1, j + 1));
					Color x = new Color(newImage.getRGB(i - 1, j - 1));
					Color y = new Color(newImage.getRGB(i + 1, j - 1));
					Color z = new Color(newImage.getRGB(i - 1, j + 1));

					Color g = new Color(
							(c.getRed() + d.getRed() + e.getRed() + f.getRed() + w.getRed() + x.getRed() + y.getRed() + z.getRed()) / 8,
							(c.getGreen() + d.getGreen() + e.getGreen() + f.getGreen() + w.getGreen() + x.getGreen() + y.getGreen() + z.getGreen()) / 8,
							(c.getBlue() + d.getBlue() + e.getBlue() + f.getBlue() + w.getBlue() + x.getBlue() + y.getBlue() + z.getBlue()) / 8);

					newImage.setRGB(i, j, g.getRGB());

				}

			}
		}

		//write on the new image
		try {
			String name = null;
			JFileChooser save = new JFileChooser();
			save.setCurrentDirectory(new java.io.File("."));
		    save.setDialogTitle("Save in folder");
		    save.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    save.setAcceptAllFileFilterUsed(false);
			 if (save.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				  name = JOptionPane.showInputDialog(null, "Save As", "File Name", JOptionPane.INFORMATION_MESSAGE);
					if(name == null) {
						JOptionPane.showMessageDialog(null, "Action terminated");
			            System.exit(1);
					}
			    }
			 else {
				 JOptionPane.showMessageDialog(null, "Action terminated");
		            System.exit(1);
			 }
			String s = save.getSelectedFile() + "\\" + name + ".png";
			ImageIO.write(newImage, "png", new File(s));
			JOptionPane.showMessageDialog(null, "Successfully upscaled and saved photo\nFilepath: "+s,"Success!", JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
