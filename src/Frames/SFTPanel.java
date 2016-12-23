package Frames;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.DecimalFormat;

import javax.swing.JPanel;
import javax.swing.Timer;

import Control.InterfaceController;
import Pages.DrawThread;
import Pages.SFTThumbnail;
import Pages.ThumbnailDialog;

/**
 * Simple Frame to display the thumbnail images of the document pages.
 * @author nvg081
 */
public class SFTPanel extends JPanel implements AWTEventListener, ComponentListener, WindowListener{

	/**
	 * Default.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Stores the images to be displayed by the frame.
	 */
	private Image[] images;

	/**
	 * The height and width of the image. 
	 * Stored as field to avoid recomputing. 
	 */
	private int imageHeight, imageWidth;

	/**
	 * The actual width and height of the thumbnails. 
	 * Necessary so the thumbnails do not gradually shrink.
	 */
	private int actualWidth, actualHeight;

	/**
	 * The width/height ratio of the images to maintain.
	 */
	private double aspectRatio = 0.7;

	/**
	 * The minimum vertical and horizontal gaps.
	 */
	private final int minVgap = 7, minHgap = 1;

	/**
	 * The current enlarged thumbnail displayed.
	 */
	private int currentThumb;

	/**
	 * The controller to influence when necessary.
	 */
	private InterfaceController controller;

	/**
	 * The preferred height and width to display the enlarged thumbnail.
	 */
	private int prefHeight = 300, prefWidth = 210;
	
	/**
	 * True if the original thumbnails are larger than the enlarged thumbnail, false otherwise.
	 */
	private boolean isEnlargedAllowed = false;

	/**
	 * The number of columns originally present.
	 */
	private int cols;

	/**
	 * The enlarged thumbnail being displayed.
	 */
	private ThumbnailDialog dialog;

	/**
	 * The collection of thumbnails on the frame.
	 */
	private SFTThumbnail[] thumbnails;

	/**
	 * The thread that will resize the thumbnails.
	 */
	private DrawThread reThread;

	/**
	 * True if the panel is show for the first time, false otherwise.
	 */
	private boolean firstDisplay;

	/**
	 * Constructor for the ThumbsFrame.
	 * @param images - The collection of thumbnail images to be displayed on the screen.
	 * @param pageWidth - The width of the parent frame.
	 * @param pageHeight - The height of the parent frame.
	 * @param pageX - The x-coordinate of the parent frame.
	 * @param pageY - The y-coordinate of the parent frame.
	 * @param controller - The interface controller to influence the application.
	 */
	public SFTPanel(Dimension size, Image[] images, InterfaceController controller){
		// Set size and location.
		this.setSize(size);

		// Initialize the images, the controller, and the thumbnails.
		this.images = images;
		this.controller = controller;
		this.thumbnails = new SFTThumbnail[this.images.length];
		
		// Initialize the correct aspect ratio.
//		Image temp_img = this.images[0];
//		if (temp_img != null) {
//			this.aspectRatio = (double) temp_img.getWidth(null)/ (double) temp_img.getHeight(null);
//		}
				
		// Initialize the optimal dimensions of the thumbnails.
		this.initializeImageDimensions();

		// GridLayout used to position each image in its own cell.
		this.cols = this.columns(this.getWidth(), this.imageWidth);

		// Adjust the results to display the thumbnails in row-major order. 
		int remaining = this.getWidth()-this.cols*this.imageWidth;
		int gap = remaining/(this.cols);
		if (gap >= this.minVgap){
			this.cols += 1;
			this.imageWidth = this.getWidth()/this.cols;
			this.imageHeight = this.computeImageHeight(this.imageWidth);
		}

		// Initialize the grid layout. 
		GridLayout grid = new GridLayout(0, this.cols);
		grid.setHgap(this.minHgap);
		grid.setVgap(this.minVgap);

		// Set the layout.
		this.setLayout(grid);
		
		// Compute the correct preferred width of the enlarged thumbnail.
		this.prefHeight = (int) (this.prefWidth/this.aspectRatio);

		// Add all thumbnails to the panel and set background.
		for (int n = 0; n < images.length; n += 1){
			// Initialize the thumbnail.
			SFTThumbnail thumb = new SFTThumbnail(images[n], this.imageWidth, this.imageHeight, n, controller, this, this.aspectRatio);

			// Retrieve the image necessary for the enlarged thumbnail and store the thumbnail.
			this.images[n] = images[n].getScaledInstance(this.prefWidth, this.prefHeight, Image.SCALE_SMOOTH);
			this.thumbnails[n] = thumb;

			this.add(thumb);
		}

		// Set the background of the interface.
		this.setBackground(Color.GRAY);

		// Initialize the enlarged thumbnail if it is needed. Also maintain the correct aspect ratio for the thumbnail.
		if (this.imageHeight < this.prefHeight || this.imageWidth < this.prefWidth){
			this.dialog = new ThumbnailDialog(null, this.prefWidth, this.prefHeight);
			this.dialog.setResizable(false);
			this.isEnlargedAllowed = true;
		}

		// Inidicate the panel is shown for the first time when first shown.
		this.firstDisplay = true;

		// Initialize the thread that will draw the thumbnails.
		this.reThread = new DrawThread(this.thumbnails);

		// Enable the frame to detect necessary events.
		Toolkit.getDefaultToolkit().addAWTEventListener(this, MouseEvent.MOUSE_MOTION_EVENT_MASK);
		this.addComponentListener(this);
	}

	/**
	 * Initializes the dimensions of the images to optimally fit the images on the screen.
	 */
	private void initializeImageDimensions(){
		// Beginning at one, increase the width and recompute height until optimal dimensions achieved.
		this.imageHeight = this.getHeight();
		this.imageWidth = this.computeImageWidth(this.imageHeight);

		while (this.rows(this.getHeight(), this.imageHeight)*this.columns(this.getWidth(), this.imageWidth) < this.images.length){
			this.imageHeight -= 1;
			this.imageWidth = this.computeImageWidth(this.imageHeight);
		}
	}

	/**
	 * Computes the desired height of the image given the width.
	 * @param w - The width of the image.
	 * @return - The height of the image.
	 */
	private int computeImageHeight(int w){
		return (int) (w/this.aspectRatio);
	}

	/**
	 * Computes the desired width of the image.
	 * @param h - The height of the image. 
	 * @return - The width of the image.
	 */
	private int computeImageWidth(int h){
		return (int) (h*this.aspectRatio);
	}

	/**
	 * Determines the number of rows required to fit images with imageHeight.
	 * @param pageHeight - The height of the frame.
	 * @param imageHeight - The height of the image.
	 * @return - The number of rows to fit the image height.
	 */
	private int rows(int pageHeight, int imageHeight){
		return (pageHeight/imageHeight);
	}

	/**
	 * Determines the number of columns needed to fit images with imageWidth.
	 * @param pageWidth - The width of the frame.
	 * @param imageWidth - The width of the images.
	 * @return - The number of columns needed to fit the images.
	 */
	private int columns(int pageWidth, int imageWidth){
		return (pageWidth/imageWidth);
	}

	/**
	 * Setter to set the correct height of the thumbnails. 
	 * @param height - The new height.
	 */
	public void setThumbnailHeight(int height){
		this.actualHeight = height;

		// Enable the enlarged thumbnail if necessary.
		if (this.enlargeThumbnail() && this.dialog == null){
			this.dialog = new ThumbnailDialog(null, this.prefWidth, this.prefHeight);
			this.dialog.setResizable(false);
		}
		// Disable the enlarged thumbnail if necessary.
		else if (!this.enlargeThumbnail() && this.dialog != null){
			this.dialog.dispose();
			this.dialog = null;
		}
	}

	/**
	 * Accessor to return the height of the images displayed on the frame.  
	 * @return - The height of the thumbnail images.
	 */
	public int getThumbnailHeight(){
		return this.actualHeight;
	}

	/**
	 * Setter to set the new width of the image. 
	 * @param width - The new width of the image.
	 */
	public void setThumbnailWidth(int width){
		this.actualWidth = width;

		// Enable the enlarged thumbnail if necessary.
		if (this.enlargeThumbnail() && this.dialog == null){
			this.dialog = new ThumbnailDialog(null, this.prefWidth, this.prefHeight);
			this.dialog.setResizable(false);
		}
		// Disable the enlarged thumbnail if necessary.
		else if (!this.enlargeThumbnail() && this.dialog != null){
			this.dialog.dispose();
			this.dialog = null;
		}
	}

	/**
	 * Accessor to return the width of the images displayed on the frame.
	 * @return - The width of the thumbnails images.
	 */
	public int getThumbnailWidth(){
		return this.actualWidth;
	}

	/**
	 * Determine if the enlarged thumbnail is visible.
	 * @return - True if the enlarged thumbnail is visible, false otherwise.
	 */
	public boolean enlargeThumbnail() {
		return ((this.actualHeight < this.prefHeight || this.actualWidth < this.prefWidth) && this.isEnlargedAllowed);
	}

	/**
	 * Is the enlarged thumbnail visible?
	 * @return - True if the enlarged thumbnail is visible, false otherwise. 
	 */
	public boolean isEnlargedThumbnailVisible() {
		return this.enlargeThumbnail() && this.dialog != null && this.dialog.isVisible() && this.isVisible();
	}

	/**
	 * Mutator to set the current dialog being displayed by the frame.
	 * @param dialog - The dialog to be displayed by the frame.
	 */
	public void setCurrentNumber(int num){
		// Adjust the current number (page) of the thumbnail the mouse is currently over.
		this.currentThumb = num;

		// Display the correct enlarged image of the thumbnail.
		if (this.dialog != null){
			this.dialog.setImage(this.images[num]);
			this.dialog.revalidate();
			this.dialog.setVisible(true);
		}
	}

	/**
	 * Indicate a page has been visited, but possibly not through the thumbnail.
	 * @param pageIndex - The index of the page that has been visited.
	 */
	public void pageVisited(int pageIndex){
		if (pageIndex >= 0 && pageIndex < this.thumbnails.length){
			this.thumbnails[pageIndex].visited();
		}
	}

	/**
	 * Resets the borders of all the thumbnails.
	 */
	public void resetBorders(){
		// Reset all necessary borders.
		for (int n = 0; n < this.thumbnails.length; n += 1){
			this.thumbnails[n].resetBorder();
		}
	}

	/** 
	 * The original x and y coordinates of the mouse.
	 */
	private int x1, y1;

	/**
	 * The new x and y coordinates of the mouse. 
	 */
	private int x2, y2;

	/**
	 * True if the start of the computation, false otherwise. 
	 */
	private boolean start = true;

	/**
	 * The wait time to complete the mouse velocity calculation in milliseconds.
	 */
	private int waitTime = 200;

	// On mouse movement, determine the speed of the mouse after it has moved.
	private ActionListener listener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e){
			// Get the current position of the mouse. 
			x2 = MouseInfo.getPointerInfo().getLocation().x;
			y2 = MouseInfo.getPointerInfo().getLocation().y;

			// Compute the speed of the mouse movement.
			double deltaXsq = Math.pow(x2-x1, 2.0);
			double deltaYsq = Math.pow(y2-y1, 2.0);
			double distance = Math.sqrt(deltaXsq+deltaYsq);
			double result = distance/(waitTime);
			DecimalFormat decForm = new DecimalFormat("#0.00");

			// Log the results.
			if (!controller.doNotLog() && isVisible() && result != 0.0 && !start){
				controller.writeToLogFile("" + x1, "" + x2, "" + y1, "" + y2, "" + waitTime, "NA", decForm.format(result), (currentThumb+1), "SFTInterface", "MouseMovement");
			}

			// Reset the mouse speed listener.
			start = true;
		}
	};

	/**
	 * The timer to compute the mouse speed.
	 */
	private Timer timer = new Timer(waitTime, listener);

	/**
	 * Tracks the mouse and determines the mouse speed.
	 */
	@Override
	public void eventDispatched(AWTEvent arg0) {
		// Only react to mouse movement.
		if (arg0.getID() == MouseEvent.MOUSE_MOVED && this.isVisible()){
			MouseEvent me = (MouseEvent) arg0;

			// Adjust the position of the enlarged thumbnail.
			if (dialog != null){
				// Retrieve current coordinates of the mouse. 
				int x = (int) me.getLocationOnScreen().getX();
				int y = (int) me.getLocationOnScreen().getY();

				// Determine if the enlarged thumbnail's location should be switched along the x-axis.
				if (x+this.dialog.getWidth()+5 > InterfaceController.getScreenWidth()){
					x -= this.dialog.getWidth()+5;
				}
				else{
					x += 5;
				}

				// Determine if the enlarged thumbnail's location should be switched along the y-axis.
				if (y+this.dialog.getHeight()+5 > InterfaceController.getScreenHeight()){
					y -= this.dialog.getHeight()+5;
				}
				else {
					y += 5;
				}

				// Assign the new location of the enlarged thumbnail.
				dialog.setLocation(x, y);
				dialog.setVisible(true);
			}
			else if (!this.isVisible()){
				this.dialog.setVisible(false);
			}

			// Only allow the timer to repeat when invoked.
			timer.setRepeats(false);

			// Get the starting point of the mouse.
			if (start && !controller.doNotLog()){
				x1 = MouseInfo.getPointerInfo().getLocation().x;
				y1 = MouseInfo.getPointerInfo().getLocation().y;
				start = false;

				// Wait the time limit and then finish the computation.
				timer.start();
			}
		}
	}

	/**
	 * Redraw the thumbnails smooth
	 */
	public void redrawThumbs(){
		if (this.reThread != null){
			this.reThread.flag();
		}

		this.reThread = new DrawThread(this.thumbnails);
		this.reThread.start();
	}

	/**
	 * Set the visibility of the enlarged thumbnail to be visible when the thumbnail interface is 
	 * shown and adjust the location of the enlarged thumbnail.
	 */
	@Override
	public void componentShown(ComponentEvent arg0) {
		if (this.dialog != null && arg0.getSource() == this){
			// The initial mouse location.
			int x = MouseInfo.getPointerInfo().getLocation().x;
			int y = MouseInfo.getPointerInfo().getLocation().y;

			// Determine if the enlarged thumbnail's location should be switched along the x-axis.
			if (x+this.dialog.getWidth()+5 > InterfaceController.getScreenWidth()){
				x -= this.dialog.getWidth()+5;
			}
			else{
				x += 5;
			}

			// Determine if the enlarged thumbnail's location should be switched along the y-axis.
			if (y+this.dialog.getHeight()+5 > InterfaceController.getScreenHeight()){
				y -= this.dialog.getHeight()+5;
			}
			else {
				y += 5;
			}
			this.dialog.setLocation(x, y);

			// Make sure the enlarged thumb is visible.
			this.dialog.setVisible(true);

			// Redraw all the thumbs smoothly if this is the first viewing.
			if (this.firstDisplay) {
				this.redrawThumbs();
				this.firstDisplay = false;
			}
			
			// Display the correct image in the enlarged thumbnail.

			int mouseX = MouseInfo.getPointerInfo().getLocation().x;
			int mouseY = MouseInfo.getPointerInfo().getLocation().y;
			for (SFTThumbnail thumb : this.thumbnails){
				// Determine the thumb's location on the screen.
				int thumbWidth = (int) thumb.getLocationOnScreen().getX()+thumb.getWidth();
				int thumbHeight = (int) thumb.getLocationOnScreen().getY()+thumb.getHeight();
				if ((mouseX >= thumb.getLocationOnScreen().getX() && mouseX <= thumbWidth) && (mouseY >= thumb.getLocationOnScreen().getY() && mouseY <= thumbHeight)){
					this.setCurrentNumber(thumb.getPageNum());
				}
			}
		}
	}

	/**
	 * Set the visibility of the enlarged thumbnail to be invisible when the thumbnail
	 * interface is not shown.
	 */
	@Override
	public void componentHidden(ComponentEvent arg0) {
		if (this.dialog != null){
			this.dialog.setVisible(false);
		}
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {}

	/**
	 * Repaint the window on resize.
	 */
	@Override
	public void componentResized(ComponentEvent arg0) {
		// Stop the thread.
		if (this.reThread != null) {
			this.reThread.flag();
			this.reThread.interrupt();
		}

		// Redraw all the thumbnails quickly. 
		for (SFTThumbnail thumb : this.thumbnails) {
			thumb.resizeImageFast();
		}

		// Redraw the images smoothly.
		this.reThread = new DrawThread(this.thumbnails);
		this.reThread.start();
	}

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	/**
	 * Get rid of the enlarged thumbnail if the viewer is closed.
	 */
	@Override
	public void windowClosing(WindowEvent arg0) {
		if (this.dialog != null){
			this.dialog.dispose();
			this.dialog = null;
		}
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}
	@Override
	public void windowOpened(WindowEvent arg0) {}
}
