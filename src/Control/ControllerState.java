package Control;

import java.awt.Rectangle;

import javax.swing.JScrollPane;

import Frames.SFTPanel;
import Frames.ThumbsPanel;

public class ControllerState {

	/**
	 * The state of the thumbnail interface and the PDF viewer itself.
	 */
	private boolean isThumbsVisible;
	
	/**
	 * The state of the current page number.
	 */
	private int currentPageNumber;
	
	/**
	 * The state of the current thumbs and scrolling time.
	 */
	private long sftTime, scrollTime, thumbsTime;
	
	/**
	 * The state of the current page count.
	 */
	private int pageCount;
	
	/**
	 * The bounds of the viewer.
	 */
	private Rectangle bounds;
	
	/**
	 * The current thumbs interface. Saved to restore the viewed thumbnails. 
	 */
	private SFTPanel thumbsInterface;
	
	/**
	 * The state of the frames.
	 */
	private int state;
	
	/**
	 * True if the full state should be restored, false otherwise. 
	 */
	private boolean restoreFull;
	
	/**
	 * The scrollpane to save.
	 */
	private JScrollPane scrollpane;
	
	/**
	 * The thumbs panel to save.
	 */
	private ThumbsPanel thumbsPanel;
	
	/**
	 * True if the thumbs pane is visible, false otherwise.
	 */
	private boolean isPaneVisible;
	
	/**
	 * Constructor for the ControllerState. All necessary information is taken as a parameter upon creation.
	 * @param thumbsVisibility - The visibility of the thumbnail interface.
	 * @param viewerVisibility - The visibility of the PDF viewer.
	 * @param currentPage - The current page number that is being viewed.
	 * @param thumbsTime - The time that the thumbnails have been visible, mneasured in seconds.
	 * @param scrollTime - The time that the PDF viewer (scroll) has been visible.
	 * @param pageCount - The number of pages that have been visited.
	 * @param width - The width of the windows.
	 * @param height - The height of the windows.
	 */
	public ControllerState(boolean restoreFull, boolean thumbsVisibility, int currentPage, long sftTime, long scrollTime, long thumbsTime, int pageCount, Rectangle bounds, SFTPanel thumbsInterface, int state, ThumbsPanel thumbsPanel, JScrollPane scrollpane, boolean paneVisible){
		// Shoud the full state be restored?
		this.restoreFull = restoreFull;
		
		// Save the state of the controller.
		this.isThumbsVisible = thumbsVisibility;
		this.currentPageNumber = currentPage;
		this.sftTime = sftTime;
		this.scrollTime = scrollTime;
		this.thumbsTime = thumbsTime;
		this.pageCount = pageCount;
		
		// Save the bounds of the viewer.
		this.bounds = bounds;
		
		// Save the current thumbs interface.
		this.thumbsInterface = thumbsInterface;
		
		// Store the state of the frames.
		this.state = state;
		
		// Store the thumbs panel and scrollpane.
		this.thumbsPanel = thumbsPanel;
		this.scrollpane = scrollpane;
		this.isPaneVisible = paneVisible;
	}

	/**
	 * Retrieve the stored visibility of the thumbnail interface.
	 * @return - The stored visibility of the thumbnail interface.
	 */
	public boolean isThumbsVisible() {
		return this.isThumbsVisible;
	}
	
	/**
	 * Retrieve the stored page number.
	 * @return - The stored page number.
	 */
	public int getCurrentPageNumber() {
		return this.currentPageNumber;
	}

	/**
	 * Retrieve the stored current thumbnail visibility time.
	 * @return - The stored time the thumbnail interface was visible.
	 */
	public long getSFTtime() {
		return this.sftTime;
	}
	
	/**
	 * Retrieve the stored time current PDF viewer visibility time.
	 * @return - The stored time the PDF viewer was visible.
	 */
	public long getScrollTime() {
		return this.scrollTime;
	}
	
	/**
	 * Retrieve the stored thumbs visibility time.
	 * @return - The stored thumbs time.
	 */
	public long getThumbsTime(){
		return this.thumbsTime;
	}

	/**
	 * Retrieve the stored page count.
	 * @return - The stored page count.
	 */
	public int getPageCount() {
		return this.pageCount;
	}
	
	/**
	 * Accessor to retrieve the stored bounds.
	 * @return - The stored bounds of the viewer.
	 */
	public Rectangle getBounds() {
		return this.bounds;
	}
	
	/**
	 * Accessor to retrieve the stored thumbs interface. 
	 * @return - The stored thumbs interface.
	 */
	public SFTPanel getThumbsInterface(){
		return this.thumbsInterface;
	}
	
	/**
	 * Accessor to retrieve the state of the frames.
	 * @return - The stored state of the frames.
	 */
	public int getState() {
		return this.state;
	}
	
	/**
	 * Accessor to determine if the full state shoud be restored.
	 * @return - True if the full state should be restored, false otherwise.
	 */
	public boolean restoreFull() {
		return this.restoreFull;
	}
	
	/**
	 * Accessor to retrieve the scrollpane.
	 * @return - The stored scrollpane.
	 */
	public JScrollPane getScrollpane(){
		return this.scrollpane;
	}
	
	/**
	 * Accessor to retrieve the thumbs panel.
	 * @return - The stored thumbs panel.
	 */
	public ThumbsPanel getThumbsPanel(){
		return this.thumbsPanel;
	}
	
	/**
	 * True if the thumbs pane was visible, false otherwise.
	 * @return - True if the pane is to be visible, false otherwise.
	 */
	public boolean isPaneVisible(){
		return isPaneVisible;
	}
}
