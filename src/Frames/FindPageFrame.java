package Frames;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

/**
 * A class to display an image during a find-page exercise.
 * @author nvg081
 */
public class FindPageFrame extends JFrame{

	/**
	 * Default.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The page number associated with this page image. 
	 */
	private int pageNum;
	
	/**
	 * Constructor to initialize the frame to display the appropriate image.
	 * @param image - The image to display on this frame.
	 * @param pageNum - The page number associated with the image.
	 * @throws RuntimeException when the image to be displayed is null.
	 */
	public FindPageFrame(Image image, int pageNum, int imageWidth, int imageHeight){
		// Ensure no null image is displayed. 
		if (image == null){
			throw new RuntimeException("Cannot display a null image on the page frame.");
		}
		
		// Remember the page number being displayed.
		this.pageNum = pageNum;
		
		// Setup the image to be displayed.
		ImageIcon imageIcon = new ImageIcon(image.getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH));
		
		// Display the image.
		JLabel imageLabel = new JLabel(imageIcon);
		this.add(imageLabel);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
	
	/**
	 * Accessor to get the page number of the displayed image.
	 * @return - The page number of the displayed image.
	 */
	public int getPageNum(){
		return this.pageNum;
	}
}
