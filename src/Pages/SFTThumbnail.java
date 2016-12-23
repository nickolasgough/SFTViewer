package Pages;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;

import Control.InterfaceController;
import Frames.SFTPanel;

/**
 * Simple component class to enable drawing of a page thumbnail onto the screen.
 * @author nvg081
 */
public class SFTThumbnail extends JComponent implements MouseListener, ComponentListener{

	/**
	 * Default.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The image of the thumbnail.
	 */
	private Image image;

	/**
	 * The original image given to the thumbnail.
	 * Kept for resizing purposes.
	 */
	private Image originalImage;

	/**
	 * The index of the corresponding page. 
	 */
	private int pageNum;

	/**
	 * The controller to influence when the thumbnail is clicked.
	 */
	private InterfaceController controller;

	/**
	 * The parent frame on which the thumbnail is displayed.
	 */
	private SFTPanel parentFrame;

	/**
	 * The height and width of the border.
	 */
	private final int borderThickness = 3;

	/**
	 * The width and height of the image. 
	 */
	private int imageWidth, imageHeight;

	/**
	 * The color the border surrounding the thumbnail. 
	 */
	private Color color;
	
	/**
	 * The aspect ratio to maintain when resizing the images.
	 */
	private double aspectRatio;

	/**
	 * Constructor for the component.
	 * @param image - The image of the thumbnail to draw to the screen.
	 */
	public SFTThumbnail(Image image, int width, int height, int index, InterfaceController controller, SFTPanel parent, double aspectRatio){
		// Store the original image and the width and height.
		this.originalImage = image;
		
		// Set the correct aspect ratio to maintain.
		this.aspectRatio = aspectRatio;

		// Track the controller and thumbnail frame.
		this.controller = controller;
		this.parentFrame = parent;

		// Set the original color of the border.
		this.color = Color.LIGHT_GRAY;

		// Get the image and the index number.
		this.pageNum = index;

		// Initialize the image dimensions.
		this.imageWidth = width-2*this.borderThickness;
		this.imageHeight = height-2*this.borderThickness;

		// Set the maximum size of the component.
		this.setMaximumSize(new Dimension(this.imageWidth, this.imageHeight));

		// Get the scaled image.
		this.image = this.originalImage.getScaledInstance(this.imageWidth, this.imageHeight, Image.SCALE_FAST);

		// Set the correct width and height to be logged.
		if (this.parentFrame != null) {
			this.parentFrame.setThumbnailWidth(this.imageWidth);
			this.parentFrame.setThumbnailHeight(this.imageHeight);
		}

		// Record the mouse listener.
		this.addMouseListener(this);
	}

	/**
	 * Paints the thumbnail image to the screen.
	 */
	public void paintComponent(Graphics g){
		// Increase the thickness of the border.
		Graphics2D g2 = (Graphics2D) g;
		Stroke oldStroke = g2.getStroke();
		g2.setStroke(new BasicStroke(3));

		// Draw the component with the correct border color and size to accomodate for the border.
		Color color = this.color;
		if ((this.controller.getCurrentNumber()-1) == this.pageNum){
			color = Color.BLUE;
		}
		g2.setColor(color);
		g2.drawRect(0, 0, this.image.getWidth(null)+2*this.borderThickness, this.image.getHeight(null)+2*this.borderThickness);

		// Reset the stroke.
		g2.setStroke(oldStroke);

		// Draw the image and dispose of the graphics object.
		g2.drawImage(this.image, this.borderThickness, this.borderThickness, null);
		g2.dispose();
	}	
	
	/**
	 * Accessor to retrieve the page number associated with this thumbnail.
	 * @return - The thumbnail's page number.
	 */
	public int getPageNum(){
		return this.pageNum;
	}

	/**
	 * Surrounds the image with a red border if it has been visited. 
	 */
	public void visited(){
		if (this.color == Color.LIGHT_GRAY){
			this.color = Color.RED;
		}
	}

	/**
	 * Resets the border surrounding the image to a black border.
	 */
	public void resetBorder(){
		if (this.color != Color.LIGHT_GRAY){
			this.color = Color.LIGHT_GRAY;
		}
	}

	/**
	 * MouseEvent method to show the page of the corresponding image in
	 * the PDFViewer.
	 */
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (arg0.getButton() == MouseEvent.BUTTON1){
			// Hide the SFT frame.
			if (this.parentFrame != null){
				this.parentFrame.setVisible(false);
			}
			
			// Display the clicked page.
			this.controller.showPage(this.pageNum);
			
			// Indicate the page was visited and log it.
			this.controller.pageVisited("SFTInterface");
			
			// Indicate the page has been visited if not already visited. 
			if (this.color == Color.LIGHT_GRAY){
				this.visited();
			}
		}
	}

	/**
	 * Update the current thumbnail number the mouse is hovering over.
	 * @param arg0 - The generated mouseEvent.
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
		if (this.parentFrame != null){
			this.parentFrame.setCurrentNumber(this.pageNum);
		}
	}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}

	/**
	 * Should the thumbnail border be reset?
	 * @return - True if the border color is red.
	 */
	public boolean isVisited(){
		return this.color == Color.RED;
	}

	/**
	 * Compute the appropriate size of the image.
	 */
	public void computeSize(){
		// Determine the initial size of the image.
//		int imageHeight = this.getHeight()-2*this.borderThickness;
		int imageWidth = this.getWidth()-2*this.borderThickness;
		int imageHeight = (int) (imageWidth/this.aspectRatio);
		
		// Determine if the image should be rescaled. 
		if (imageHeight > this.getMaximumSize().getHeight()){
			imageHeight = (int) this.getMaximumSize().getHeight();
		}
		if (imageWidth > this.getMaximumSize().getWidth()){
			imageWidth = (int) this.getMaximumSize().getWidth();
		}

		// Assign the new values. 
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		if (this.parentFrame != null){
			this.parentFrame.setThumbnailWidth(this.imageWidth);
			this.parentFrame.setThumbnailHeight(this.imageHeight);
		}
	}

	/**
	 * Resize the image quickly.
	 */
	public void resizeImageFast(){
		// Obtain the scaled instance if necessary.
		this.computeSize();
		this.image = this.originalImage.getScaledInstance(this.imageWidth, this.imageHeight, Image.SCALE_FAST);
		Toolkit.getDefaultToolkit().prepareImage(this.image, this.imageWidth, this.imageHeight, null);
	}

	/**
	 * Resize the image smoothly. 
	 */
	public void resizeImageSmooth(){
		// Obtain the scaled instance if necessary.
		this.computeSize();
		this.image = this.originalImage.getScaledInstance(this.imageWidth, this.imageHeight, Image.SCALE_SMOOTH);
		Toolkit.getDefaultToolkit().prepareImage(this.image, this.imageWidth, this.imageHeight, null);
	}
	
	/**
	 * Retrieves a smooth image.
	 */
	public void getSmoothImage(){
		this.image = this.originalImage.getScaledInstance(this.imageWidth, this.imageHeight, Image.SCALE_SMOOTH);
		Toolkit.getDefaultToolkit().prepareImage(this.image, this.imageWidth, this.imageHeight, null);
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {}

	@Override
	public void componentMoved(ComponentEvent arg0) {}

	@Override
	public void componentResized(ComponentEvent arg0) {}

	@Override
	public void componentShown(ComponentEvent arg0) {}
}
