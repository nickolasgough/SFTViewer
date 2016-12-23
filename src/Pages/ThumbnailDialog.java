package Pages;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;

public class ThumbnailDialog extends JDialog{

	/**
	 * Default.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The thumbnail image to display using the dialog.
	 */
	private SFTThumbnail thumbImage;
	
	/**
	 * The preferred height and width to display the image dialog.
	 */
	private int prefHeight = 300, prefWidth = 210;
	
	/**
	 * The label used to set the image.
	 */
	private JLabel label;
	
	/**
	 * The width of the border surrounding the dialog.
	 */
	private final int borderWidth = 1;
	
	/**
	 * The constructor for the ThumbnailDialog.
	 * Creates the dialog that will display an enlarged image of the page when the thumbnail is too small.
	 * @param image - The image to be displayed by the Dialog.
	 * @param width - The desired width of the image dialog.
	 * @param height - The desired height of the image dialog. 
	 */
	public ThumbnailDialog(Image image, int width, int height){
		// Store the correct height and width.
		this.prefWidth = width;
		this.prefHeight = height;
		
		if (thumbImage != null){
			// Initialize the enlarged image.
			image = image.getScaledInstance(this.prefWidth, this.prefHeight, Image.SCALE_SMOOTH);

			// Initialize the component.
			this.label = new JLabel(new ImageIcon(image));
			this.add(label);
		}
		
		// Adjust the look and feel of the enlarged thumbnail.
		this.setUndecorated(true);
		this.setPreferredSize(new Dimension(this.prefWidth, this.prefHeight));
		this.pack();
		
		// Ensure the enalrged thumbnail is visible only when necessary.
		this.setVisible(false);
	}

	/**
	 * Sets the image to be displayed by the dialog.
	 * @param image - The image to be displayed.
	 */
	public void setImage(Image image){
		// Set the image to be displayed.
		if (this.label != null){
			this.remove(this.label);
		}
		
		// Display the correct image.
		this.label = new JLabel(new ImageIcon(image));
		this.label.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, this.borderWidth));
		this.add(this.label);
	}
}
