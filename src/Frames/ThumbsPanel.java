package Frames;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;

import javax.swing.JPanel;
import javax.swing.JViewport;

import Control.InterfaceController;
import Pages.PaneThumbnail;

/**
 * A simple panel containing all the pages of the document laid out in a single column.
 * @author nvg081
 */
public class ThumbsPanel extends JPanel{

	/**
	 * Default.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The images of the document pages.
	 */
	private Image[] images;
	
	/**
	 * The thumbnails placed onto the panel.
	 */
	private PaneThumbnail[] thumbs;
	
	/**
	 * The controller that must be passed to thumbnails. 
	 */
	private InterfaceController controller;
	
	/** 
	 * The width and height of the thumbnails to be placed on the panel.
	 */
	private int width = 119, height= 170;

	/**
	 * The viewport for the thumbs column.
	 */
	private JViewport viewport;
	
	/**
	 * The panel constructor. Lays out the thumbnails in a single column.
	 * @param images - The images to be used to draw the thumbnails.
	 * @param controller - The controller to be passed to the thumbnails. 
	 */
	public ThumbsPanel(Image[] images, InterfaceController controller) {
		// Initialize the images, thumbs, and the controller. 
		this.images = images;
		this.controller = controller;
		this.thumbs = new PaneThumbnail[this.images.length];
		
		// Set the one-column layout.
		GridLayout col_layout = new GridLayout(0, 1, 0, 7);
		this.setLayout(col_layout);
		
		// Add the thumbnails to the panel.
		for (int n = 0; n < this.images.length; n += 1) {
			PaneThumbnail thumb = new PaneThumbnail(this.images[n], this.width, this.height, n, this.controller, this);
			thumb.setPreferredSize(new Dimension(this.width, this.height));
			this.thumbs[n] = thumb;
			this.add(thumb);
		}
		
		// Set the background. 
		this.setBackground(Color.GRAY);
		
		// Start the redraw thread to draw the smooth thumbnails.
		DrawPaneThread paneThread = new DrawPaneThread(this.thumbs);
		paneThread.run();
	}
	
	/**
	 * Indicate the page has been visited.
	 * @param index - The index of the page that was visited.
	 */
	public void pageVisited(int index){
		this.thumbs[index].visited();
		this.repaint(0);
	}

	// Set the viewport for the thumbs column.
	public void setViewport(JViewport viewport){
		this.viewport = viewport;
	}
	
	/**
	 * Override the repaint method to show the correct thumbnail within the viewport.
	 */
	@Override
	public void repaint(long time){
		// Call the super method. 
		super.repaint(time);
		
		// Do not allow logging as a result of the changing viewport.
		boolean reset = false;
		if (!this.controller.doNotLog()){
			this.controller.setDoNotLog(true);
			reset = true;
		}
		
		// Adjust the viewable area as needed.
		if (this.viewport != null){
			int page_index = this.controller.getCurrentNumber()-1;
			if (page_index >= this.thumbs.length) {
				return;
			}
			
			PaneThumbnail thumb = this.thumbs[page_index];
			int thumbRegion = thumb.getY()+thumb.getHeight();
			int viewRegion = (int) this.viewport.getViewPosition().getY()+this.viewport.getHeight();
			if (viewRegion < thumbRegion) {
				this.viewport.setViewPosition(new Point(this.viewport.getX(), thumb.getY()-this.viewport.getHeight()+thumb.getHeight()));
			}
			if (this.viewport.getViewPosition().getY() > thumb.getY()){
				this.viewport.setViewPosition(new Point(this.viewport.getX(), thumb.getY()));
			}
		}
		
		// Enable logging functionality.
		if (reset){
			this.controller.setDoNotLog(false);
		}
	}
	
	/**
	 * Reset the borders of all the thumbnails.
	 */
	public void resetBorders(){
		for (int n = 0; n < this.thumbs.length; n += 1){
			this.thumbs[n].resetBorder();
		}
	}
}
