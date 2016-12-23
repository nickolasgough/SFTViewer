package Control;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.Timer;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.views.DocumentViewController;
import org.icepdf.ri.common.views.DocumentViewControllerImpl;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWriteMode;

import Frames.FindPageFrame;
import Frames.SFTPanel;
import Frames.ThumbsPanel;
import Listeners.ResizeListener;
import Logging.Participant;
import Logging.Participant.Sex;
import Logging.Task;
import Pages.PaneThumbnail;

/**
 * A simple controller class to control the switch between the PDFViewer and the thumbnail interface.
 * Also stores useful information and tries to reduce passing multiple items to other objects.
 * @author nvg081
 */
public class InterfaceController implements AWTEventListener{

	/**
	 * True if the logging functionality should be disabled, false otherwise.
	 */
	private boolean doNotLog = false;

	/**
	 * True if the page visit has been logged.
	 */
	private boolean visitLogged = false;

	/**
	 * The file that will be used to record user and participant logging information.
	 */
	private File userLogFile;

	/**
	 * The current log file that will be sent to email and dropbox.
	 */
	private File currentLogFile;

	/**
	 * The writer used to write to the file.
	 */
	private FileWriter writer;

	/**
	 * The page that is currently being displayed.
	 */
	private int currentPageNumber;

	/**
	 * Stores the file name used.
	 */
	private String documentName;

	/**
	 * The time to wait to allow the participant time to examine the page to be found.
	 */
	private final int waitTime = 1000;

	/**
	 * The participant of the task. 
	 */
	private static Participant participant;

	/**
	 * The thumbs interface to control.
	 */
	private SFTPanel thumbInterface; 

	/**
	 * The PDFViewer to control.
	 */
	private JFrame PDFViewer;

	/**
	 * The thumbs columns that will be visible on the left side of the viewer.
	 */
	private ThumbsPanel thumbsPanel;

	/**
	 * The scroll pane that will show the thumbs column on the left.
	 */
	private JScrollPane thumbsPane;

	/**
	 * The frame displaying the random page. 
	 */
	private FindPageFrame pageFrame;

	/**
	 * The SwingController that controls the PDFViewer.
	 */
	private SwingController controller;

	/**
	 * Store the screen width and height for easy access.
	 */
	private final static int screenWidth = (int) GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getWidth();
	private final static int screenHeight = (int) GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getHeight();

	/**
	 * Current task.
	 */
	private Task task;

	/**
	 * The start time for the time the windows were opened.
	 * Will be restarted when the time of the windows needs to be logged or the windows change.
	 */
	private long startTime;

	/**
	 * The start time for the thumbs column.
	 */
	private long thumbsStartTime;

	/**
	 * The respective cumulative times the user has used each window.
	 */
	private long scrollTime = 0, SFTtime = 0, thumbsTime = 0;

	/**
	 * The number of pages the user has visited.
	 */
	private int pageCount = 0;

	/**
	 * Stores the current controller state. 
	 */
	private ControllerState controllerState;

	/**
	 * The condition being tested during the trial.
	 */
	private String condition = "None";

	/**
	 * Attempting to avoid a blank image from appearing. 
	 */
	private Image[] images;

	/**
	 * The height and width of the display for the page being searched for.
	 */
	private final int findImageWidth = 490, findImageHeight = 700; 

	/**
	 * The result of the log.
	 */
	private String log_result = "";

	/**
	 * True if the full state should be saved, false otherwise.
	 */
	private boolean saveFullState;
	
	/**
	 * True if a button was used to navigate, false otherwise.
	 */
	private boolean button_nav = false;
	
	/**
	 * The maximum zoom allowed. 
	 */
	private float min_zoom = 0.0f;
	
	/**
	 * The previous zoom. Used to determine if the zoom has been logged.
	 */
	private float prev_zoom;
	
	/**
	 * True if the mouse if down, but not on the scrollbar, false otherwise.
	 */
	private boolean mouse_down;

	/**
	 * The constructor for the controller.
	 * @param thumbInterface - The frame that will display all the thumbnails. 
	 * @param PDFViewer - The frame that displays the actual pdf viewer.
	 */
	public InterfaceController(JFrame PDFViewer, SwingController controller){
		//Initialize fields and setup event.
		this.PDFViewer = PDFViewer;
		this.controller = controller;

		// The first page displayed is the first page.
		this.currentPageNumber = 1;

		// Do not allow logging as a result of the frame being resized.
		ResizeListener rListener = new ResizeListener(this);
		this.PDFViewer.addComponentListener(rListener);
		this.PDFViewer.addWindowStateListener(rListener);
		
		// Listen for events.
		Toolkit.getDefaultToolkit().addAWTEventListener(this, MouseEvent.MOUSE_EVENT_MASK);
		Toolkit.getDefaultToolkit().addAWTEventListener(this, MouseEvent.MOUSE_WHEEL_EVENT_MASK);
		Toolkit.getDefaultToolkit().addAWTEventListener(this, MouseEvent.MOUSE_MOTION_EVENT_MASK);
		Toolkit.getDefaultToolkit().addAWTEventListener(this, KeyEvent.KEY_EVENT_MASK);
		Toolkit.getDefaultToolkit().addAWTEventListener(this, WindowEvent.WINDOW_EVENT_MASK);
	}

	/**
	 * Initializes the images that will be used to display the document pages.
	 * @throws RuntimeException when the controller containing all the images is not instantiated.
	 */
	public void initializeImages() throws RuntimeException{
		// Ensure controller is set.
		if (this.controller == null){
			throw new RuntimeException("Cannot initialize the images without the controller.");
		}
		
		// Get the images of all the pages within the document.
		if (this.images == null){
			// Show the loading cursor so the user does not think the viewer has frozen.
			this.showLoadingCursor();
			
			Document document = this.controller.getDocument();
			int numPages = document.getPageTree().getNumberOfPages();
			this.images = new Image[numPages];
			for (int n = 0; n < numPages; n += 1){
				this.images[n] = document.getPageImage(n, GraphicsRenderingHints.SCREEN, Page.BOUNDARY_CROPBOX, 0.0f, 1.0f);
			}
			
			// Reset the cursor.
			this.showDefaultCursor();
		}
	}

	/**
	 * Displays the specified page in the PDFViewer after switching interfaces.
	 * @param pageNum - The page number of the page to switch to.
	 */
	public void showPage(int pageNum){
		// Update the page number.
		this.currentPageNumber = pageNum+1;

		// Influence the original controller to show the desired page.
		this.controller.showPage(pageNum);

		if (!this.condition.equals("None")){
			// Reset the cursor to avoid confusion about functionality.
			controller.getDocumentViewController().setViewCursor(DocumentViewController.CURSOR_DEFAULT);
		}
	}

	/**
	 * Initializes the frame that will display all the thumbnail images of the document's pages. 
	 */
	public void setupSFTCondition(){
		// Initialize the images.
		this.initializeImages();

		// Establish the correct dimensions. 
		Dimension size = null;
		if (this.condition.equals("None")){
			size = new Dimension(InterfaceController.getScreenWidth(), InterfaceController.getScreenHeight());
		}
		else {
			size = new Dimension(InterfaceController.getScreenWidth()-this.findImageWidth, InterfaceController.getScreenHeight());
		}

		// Initialize the thumbs interface.
		this.thumbInterface = new SFTPanel(size, this.images, this);
		this.PDFViewer.addWindowListener(this.thumbInterface);
		this.PDFViewer.addComponentListener(this.thumbInterface);

		// Set the thumbnails as the glass pane.
		this.PDFViewer.setGlassPane(this.thumbInterface);
	}

	/**
	 * Initializes the column of thumbs to be drawn on the left side of the viewer.
	 */
	public void setupThumbsCondition(){
		// Get the images ready.
		this.initializeImages();
		
		// Get the viewer panel.
		JPanel viewerComponentPanel = (JPanel) this.PDFViewer.getContentPane().getComponent(0);

		// Initialize the thumbs panel and the scrollbar.
		this.thumbsPanel = new ThumbsPanel(this.images, this);
		if (this.thumbsPane == null){
			this.thumbsPane = new JScrollPane();
			viewerComponentPanel.add(this.thumbsPane, BorderLayout.WEST);
		}
		this.thumbsPane.setPreferredSize(new Dimension(150, 0));
		this.thumbsPane.setViewportView(this.thumbsPanel);
		this.thumbsPanel.setViewport(thumbsPane.getViewport());

		// Set the button on the toolbar to toggle the visibility of the thumbs pane.
		JToolBar toolbar = (JToolBar) viewerComponentPanel.getComponent(0);
		JToolBar first_toolbar = (JToolBar) toolbar.getComponent(0);
		JToggleButton toggleButton = (JToggleButton) first_toolbar.getComponent(2);

		// Make sure the button is not already selected. 
		if (toggleButton.isSelected()){
			int lastIndex = toggleButton.getActionListeners().length-1;
			toggleButton.getActionListeners()[lastIndex].actionPerformed(new ActionEvent(toggleButton, ActionEvent.ACTION_PERFORMED, null));
		}

		// Add the action listener that will toggle the thumbs column.
		if (toggleButton.getActionListeners().length < 2){
			toggleButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event){
					// Determine whether to start or end the thumbs time.
					if (!thumbsPane.isVisible()){
						startThumbsTime();
						thumbsPane.setVisible(true);
					}
					else {
						endThumbsTime();
						thumbsPane.setVisible(false);
					}
				}
			});
		}

		// Add logging functionality to the vertical scrollbar of the thumbs pane.
		JScrollBar scrollbar = (JScrollBar) this.thumbsPane.getVerticalScrollBar();
		if (scrollbar.getAdjustmentListeners().length < 1){
			scrollbar.setUnitIncrement(20);
			scrollbar.addAdjustmentListener(new AdjustmentListener() {

				/**
				 * The beginning of the scroll.
				 */
				private int beginning;

				/**
				 * The current result from the current scroll event.
				 */
				private double result;

				/**
				 * True if the user has begun scrolling.
				 */
				private boolean start = true;

				/**
				 * Log the sub-method used.
				 */
				private String sub = null;

				/**
				 * The time to wait to finish the speed computation in milliseconds.
				 */
				private int speedWait = 200;

				// Wait the specified time after an event and then determine the speed at which the scrollbar was moving.
				private ActionListener speedListener = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e){
						// Finish the computation.
						int ending = scrollbar.getValue();
						result = Math.abs((ending-beginning)/(double) speedWait);
						DecimalFormat decForm = new DecimalFormat("#0.00");

						// Log the relevant information.
						if (result != 0.0 && !doNotLog){
							String x = "" + scrollbar.getX();
							writeToLogFile(x, x, "" + beginning, "" + ending, "" + speedWait, decForm.format(result), "NA", currentPageNumber, "ThumbsColumn", sub);
						}

						// Reset to restart the computation.
						start = true;
					}
				};

				/**
				 * The timer to determine the speed of the scroll bar.
				 */
				private Timer speedTimer = new Timer(this.speedWait, speedListener);

				/** 
				 * Determine the correct page number and the speed at which the user is scrolling.
				 * Also, determine which sub-method was used to do the scrolling.
				 */
				@Override
				public void adjustmentValueChanged(AdjustmentEvent event) {
					// Timers should only repeat when invoked.
					this.speedTimer.setRepeats(false);

					// If beginning of the scroll, determine the starting location.
					if (start && !doNotLog && PDFViewer.isVisible() && controller.getDocument() != null && thumbsPane.isVisible()){
						beginning = scrollbar.getValue();
						start = false;

						// Determine if the scrollbar or the mouse wheel caused the event.
						if (event.getValueIsAdjusting()){
							sub = "ScrollBar";
						}
						else {
							sub = "MouseWheel";
						}

						// Begin the wait period before computing speed.
						this.speedTimer.start();
					}
				}
			});
		}
	}

	/**
	 * Switch between the windows.
	 */
	public void switchWindows(){
		// Assign time to the correct window.
		this.endTime();
		this.startTime();

		// Determine the correct page number.
//		this.currentPageNumber = this.controller.getCurrentPageNumber()+1;

		// Determine which window to open and which to close, then open the window and 
		// set the proper bounds. 
		if (this.thumbInterface != null && !this.thumbInterface.isVisible()){
			this.writeToLogFile("NA", "NA", "NA", "NA", "NA", "NA", "NA", this.currentPageNumber, "PageView", "SFTswitch");

			this.endThumbsTime();
			this.thumbInterface.setVisible(true);
		}
		else if (this.thumbInterface != null && this.thumbInterface.isVisible()){
			this.startThumbsTime();
			this.thumbInterface.setVisible(false);
		}

		// Repaint the thumbnails so the correct border is displayed.
		this.PDFViewer.repaint(0);

		// Reset the cursor.
		if (!this.condition.equals("None")){
			this.controller.getDocumentViewController().setViewCursor(DocumentViewController.CURSOR_DEFAULT);
		}
	}

	/**
	 * A global event to switch between the PDFViewer and the thumbnail interface.
	 * Also listens for other events that take are required to be heard by the interface controller.
	 */
	@Override
	public void eventDispatched(AWTEvent masterEvent) {
		// Check event.
		if (masterEvent.getID() == MouseEvent.MOUSE_CLICKED){
			MouseEvent event = (MouseEvent) masterEvent;

			// Switch interfaces if requested.
			if ((event.getButton() == MouseEvent.BUTTON3 || event.getButton() == MouseEvent.BUTTON2) /*&& event.getClickCount() == 2*/ && this.thumbInterface != null && !this.condition.equals("Scrolling") && !this.condition.equals("ScrollAndThumb")){
				this.switchWindows();
			}
			else if (event.getButton() == MouseEvent.BUTTON1 && this.controller.getDocument() != null && !this.condition.equals("None") && event.getSource() != this.pageFrame && event.getSource() != this.thumbsPane && !(event.getSource() instanceof PaneThumbnail || event.getSource() instanceof JScrollBar) && !this.thumbInterface.isVisible() && this.PDFViewer.isVisible()){
				// Indicate the page has been found.
				this.pageFound();
			}

			// Set the mouse.
			if (this.condition.equals("Thumbnails")){
				this.showDefaultCursor();
			}
		}
		else if (masterEvent.getID() == MouseEvent.MOUSE_DRAGGED) {
			MouseEvent event = (MouseEvent) masterEvent;
			
			// Don't allow the mouse drag to move the view to avoid clashing with the control key.
			if (event.isControlDown() || this.condition.equals("Thumbnails")) {
				if (this.condition.equals("Thumbnails") && !this.thumbInterface.isVisible()) {
					this.showDefaultCursor();
				}
				event.consume();
			}
		}
		else if (masterEvent.getID() == MouseEvent.MOUSE_PRESSED && !(masterEvent.getSource() instanceof JScrollBar)) {
			MouseEvent event = (MouseEvent) masterEvent;
			
			if (this.condition.equals("Thumbnails") && !this.thumbInterface.isVisible() && this.PDFViewer.isVisible() && !(event.getSource() instanceof JButton)) {
				this.showDefaultCursor();
				event.consume();
			}
			this.mouse_down = true;
		}
		else if (masterEvent.getID() == MouseEvent.MOUSE_RELEASED) {
			MouseEvent event = (MouseEvent) masterEvent;
			
			if (this.condition.equals("Thumbnails") && !this.thumbInterface.isVisible() && this.PDFViewer.isVisible() && !(event.getSource() instanceof JButton)) {
				this.showDefaultCursor();
				event.consume();
			}
			this.mouse_down = false;
		}
		else if (masterEvent.getID() == WindowEvent.WINDOW_CLOSING){
			WindowEvent event = (WindowEvent) masterEvent;

			// Terminate the program if any of the windows are closed.
			if (event.getWindow().equals(this.PDFViewer)){
				// End and assign the current time data. 
				this.endTime();

				// Let the user know the viewer did not freeze.
				this.showLoadingCursor();
				
				// Close the page find frame if it is still open.
				if (this.pageFrame != null) {
					this.pageFrame.dispose();
				}

				// Attempt to close the file.
				try {
					if (this.writer != null){
						this.writeToLogFile("NA", "NA", "NA", "NA", "NA", "NA", "NA", (this.currentPageNumber), "PageView", "SessionEnd");
						this.writer.close();
						this.sendResultsToEmailAndDropbox();
					}
				} 
				catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Writer failed to close. The data may not have been written. Please report this issue.\n" + e.getMessage() + ".");
				}
				
				// Reset the cursor.
				this.showDefaultCursor();
				
				// Dispose of the controller and currently open document. 
				this.controller.closeDocument();
				this.controller.dispose();

				System.exit(0);
			}
		}
		else if (masterEvent.getID() == KeyEvent.KEY_PRESSED) {
			KeyEvent event = (KeyEvent) masterEvent;
			
			// Get the scrollbar for logging the usage of the up and down keys. 
			JScrollBar scrollbar = (JScrollBar) this.controller.getDocumentViewController().getVerticalScrollBar();
			
			// Determine if the event is to be consumed or logged in the data file. 
			if (this.thumbInterface != null && this.thumbInterface.isVisible()) {
				event.consume();
				return;
			}
			else if (!this.PDFViewer.isVisible()) {
				return;
			}
			else if (event.getKeyCode() == KeyEvent.VK_PAGE_UP && !event.isControlDown() && this.currentPageNumber > 1) {
				this.writeToLogFile("NA", "NA", "NA", "NA", "NA", "NA", "NA", this.currentPageNumber, "PageView", "PageUpKey");
				this.doNotLog = true;
				this.button_nav = true;
			}
			else if (event.getKeyCode() == KeyEvent.VK_PAGE_DOWN && !event.isControlDown() && this.currentPageNumber < this.getNumPages()) {
				this.writeToLogFile("NA", "NA", "NA", "NA", "NA", "NA", "NA", this.currentPageNumber, "PageView", "PageDownKey");
				this.doNotLog = true;
				this.button_nav = true;
			}
			else if ((event.getKeyCode() == KeyEvent.VK_HOME) && event.isControlDown() && this.currentPageNumber > 1) {
				this.writeToLogFile("NA", "NA", "NA", "NA", "NA", "NA", "NA", this.currentPageNumber, "PageView", "PageHomeKey");
				this.doNotLog = true;
				this.button_nav = true;
			}
			else if ((event.getKeyCode() == KeyEvent.VK_END) && event.isControlDown() && this.currentPageNumber < this.getNumPages()) {
				this.writeToLogFile("NA", "NA", "NA", "NA", "NA", "NA", "NA", this.currentPageNumber, "PageView", "PageEndKey");
				this.doNotLog = true;
				this.button_nav = true;
			}
			else if ((event.getKeyCode() == KeyEvent.VK_UP || event.getKeyCode() == KeyEvent.VK_KP_UP) && !event.isControlDown() && scrollbar.getValue() > scrollbar.getMinimum()) {
				this.writeToLogFile("NA", "NA", "NA", "NA", "NA", "NA", "NA", this.currentPageNumber, "PageView", "UpArrowKey");
				this.doNotLog = true;
				this.button_nav = true;
			}
			else if ((event.getKeyCode() == KeyEvent.VK_DOWN || event.getKeyCode() == KeyEvent.VK_KP_DOWN) && !event.isControlDown() && scrollbar.getValue() < scrollbar.getMaximum()-scrollbar.getVisibleAmount()) {
				this.writeToLogFile("NA", "NA", "NA", "NA", "NA", "NA", "NA", this.currentPageNumber, "PageView", "DownArrowKey");
				this.doNotLog = true;
				this.button_nav = true;
			}
			else if ((event.getKeyCode() == KeyEvent.VK_HOME || event.getKeyCode() == KeyEvent.VK_END) && !event.isControlDown()) {
				event.consume();
			}
			else if ((event.getKeyCode() == KeyEvent.VK_PAGE_UP || event.getKeyCode() == KeyEvent.VK_PAGE_DOWN || event.getKeyCode() == KeyEvent.VK_UP || event.getKeyCode() == KeyEvent.VK_DOWN) && event.isControlDown()) {
				event.consume();
			}
			
			// Do not allow logging when the control is down.
			if (event.getKeyCode() == KeyEvent.VK_CONTROL) {
				this.doNotLog = true;
			}
		}
		else if (masterEvent.getID() == KeyEvent.KEY_RELEASED) {
			KeyEvent event = (KeyEvent) masterEvent;
			
			// Enable the logging after the page up or down is finished.
			if ((event.getKeyCode() == KeyEvent.VK_PAGE_DOWN || event.getKeyCode() == KeyEvent.VK_PAGE_UP || event.getKeyCode() == KeyEvent.VK_UP || event.getKeyCode() == KeyEvent.VK_DOWN) && !event.isControlDown()) {
				this.doNotLog = false;
			}
			if (event.getKeyCode() == KeyEvent.VK_PAGE_DOWN || event.getKeyCode() == KeyEvent.VK_PAGE_UP || event.getKeyCode() == KeyEvent.VK_HOME || event.getKeyCode() == KeyEvent.VK_END || event.getKeyCode() == KeyEvent.VK_UP || event.getKeyCode() == KeyEvent.VK_DOWN) {
				this.button_nav = false;
			}
			
			// Reset the logging when the control key is released. 
			if (event.getKeyCode() == KeyEvent.VK_CONTROL) {
				this.doNotLog = false;
			}
		}
		else if (masterEvent.getID() == MouseEvent.MOUSE_WHEEL) {
			MouseWheelEvent event = (MouseWheelEvent) masterEvent;
			
			// Retrieve the user zoom once.
			float user_zoom = this.controller.getUserZoom();
			
			// Determine the maximum zoom.
			if (this.min_zoom == 0.0f) {
				this.min_zoom = user_zoom-0.2f;
				this.prev_zoom = 0.0f;
			}

			// Determine if zoom should be restricted. 
			if (this.thumbInterface.isVisible() || (event.getSource() == this.thumbsPane && event.isControlDown())) {
				event.consume();
				return;
			}
			else if (event.isControlDown()) {
				float zoom = user_zoom;
				if (zoom <= this.min_zoom && event.getWheelRotation() > 0 && !this.condition.equals("None")) {
					this.controller.setZoom(this.min_zoom);
					zoom = this.min_zoom;
				}
				if (event.getWheelRotation() > 0 && zoom != this.prev_zoom){
					this.writeToLogFile("NA", "NA", "NA", "NA", "NA", "NA", "NA", this.currentPageNumber, "PageView", "DocZoomOut");
				}
				else if (event.getWheelRotation() < 0 && zoom != this.prev_zoom) {
					this.writeToLogFile("NA", "NA", "NA", "NA", "NA", "NA", "NA", this.currentPageNumber, "PageView", "DocZoomIn");
				}
				this.prev_zoom = zoom;
			}
		}
	}

	/**
	 * Accessor to retrieve the PDF viewer frame.
	 * @return - The PDF viewer frame.
	 */
	public JFrame getPDFViewer(){ 
		return this.PDFViewer;
	}

	/**
	 * Accessor to retrieve the thumbs panel.
	 * @return - The side thumbs panel.
	 */
	public ThumbsPanel getThumbsPanel(){
		return this.thumbsPanel;
	}

	/**
	 * Accessor to retrieve the thumbnail interface frame.
	 * @return - The thumbnail interface frame.
	 */
	public SFTPanel getThumbInterface(){
		return this.thumbInterface;
	}

	/**
	 * Accessor to retrieve the thumbs pane that will show the thumbs in one column on the left side of the viewer.
	 * @return - The thumbs pane showing the column of thumbnails.
	 */
	public JScrollPane getThumbsPane(){
		return this.thumbsPane;
	}

	/**
	 * Function to return a random page number.
	 * @return - A random page number.
	 */
	public int generateRandomPageNum(){
		return (int) Math.ceil((Math.random()*this.controller.getDocument().getPageTree().getNumberOfPages()));
	}

	/**
	 * Method to create a random page and display it next to the pdf viewer.
	 * @param pageNum - The page number of the page to display.
	 */
	public void createPageDisplay(int pageNum){
		try{
			// Setup the page frame with the correct image and page number.
			this.pageFrame = new FindPageFrame(this.controller.getDocument().getPageImage(pageNum, GraphicsRenderingHints.SCREEN, Page.BOUNDARY_CROPBOX, 0.0f, 1.0f), pageNum+1, this.findImageWidth, this.findImageHeight);
		} 
		catch (NullPointerException e){
			JOptionPane.showMessageDialog(null, "The trial attempted to display a null image. Please report this issue.\n" + e.getMessage() + ".");
		}

		// Correct the properties of the frame.
		this.pageFrame.setBounds(InterfaceController.getScreenWidth()-this.findImageWidth, 0, this.findImageWidth, this.findImageHeight);
		this.pageFrame.setBackground(Color.DARK_GRAY);
		this.pageFrame.setResizable(false);
		this.pageFrame.pack();

		// Display the frame.
		this.pageFrame.setVisible(true);
	}

	/**
	 * Accessor to retrieve the width of the page that will be displayed during a search.
	 * @return - The width of the page displayed during a search.
	 */
	public int getFindImageWidth(){
		return this.findImageWidth;
	}

	/**
	 * Accessor to retrieve the height of the page being displayed during a search.
	 * @return - The height of the page being displayed during a search.
	 */
	public int getFindImageHeight(){
		return this.findImageHeight;
	}

	/**
	 * Accessor to obtain the screen width.
	 * @return - The screen width.
	 */
	public static int getScreenWidth(){
		return screenWidth;
	}

	/**
	 * Accessor to obtain the screenHeight;
	 * @return - The height of the screen.
	 */
	public static int getScreenHeight(){
		return screenHeight;
	}

	/**
	 * For other components to check if the data should be logged.
	 * @return - True if the data is not to be logged, false otherwise.
	 */
	public boolean doNotLog(){
		return this.doNotLog;
	}

	/**
	 * For other objects to inform controller the page has changed. 
	 */
	public void pageFound(){
		// Determine if the current page is the page being searched for.
		if (!this.condition.equals("None") && this.pageFrame != null && this.pageFrame.getPageNum() == this.currentPageNumber){
			// Determine if the page visit has been logged. 
			if (!this.visitLogged) {
				this.pageVisited("PageView");
			}

			// End the current trial.
			this.task.endCurrentTrial();

			// Log the event.
			this.writeToLogFile("NA", "NA", "NA", "NA", "NA", "NA", "NA", (this.currentPageNumber), "SearchTask", "PageFound");

			// Move onto the next trial.
			this.task.increaseTrialNumber();

			// Go on to the next trial.
			this.nextTrial();
		}
	}

	/**
	 * Method that begins the next trial if there are more trials to attempt.
	 * Otherwise, the results are tabulated.
	 */
	public void nextTrial(){
		// Inform components not to log data while the next trial is being set up.
		this.doNotLog = true;

		// Reset page count for the next trial.
		this.pageCount = 0;

		// Hide the frames before the next trial begins.
		if (this.pageFrame != null){
			this.pageFrame.dispose();
		}

		// Hide both frames.
		this.thumbInterface.setVisible(false);
		this.PDFViewer.setVisible(false);

		// Continue the task.
		if (!this.task.isOver()){
			// Create the current page window.
			this.createPageDisplay(this.task.getNextPageNum());

//			// Position the mouse at the top right corner of the screen.
//			Robot robot = null;
//			try {
//				robot = new Robot();
//				robot.mouseMove(InterfaceController.getScreenWidth(), 0);
//			} 
//			catch (AWTException e1) {
//				JOptionPane.showMessageDialog(null, "Robot failed to generate. Please report this error.");
//			}

			// Delay the next trial.
			ActionListener listener = new ActionListener() {
				// Delay the start of the next trial.
				@Override
				public void actionPerformed(ActionEvent e) {
					// Display a page other than the one that was just found to gain accurate results. 
					int displayPage = task.getNextStartPage();

					// Reset all the thumbnail borders to not-visited.
					thumbInterface.resetBorders();
					thumbsPanel.resetBorders();

					// Set the viewer to be visible.
					PDFViewer.setVisible(true);
					controller.showPage(displayPage);
					currentPageNumber = displayPage+1;

					// Reset the cursor to avoid confusion.
					controller.getDocumentViewController().setViewCursor(DocumentViewController.CURSOR_DEFAULT);

					// Begin the next trial.
					if (condition.equals("ScrollAndThumb")){
						startThumbsTime();
					}
					startTime();
					task.startNextTrial();

					// User technically visits the first page displayed.
					visitLogged = false;
					pageVisited("PageView");

					// Reset the condition to enable logging again.
					doNotLog = false;
				}
			};
			Timer timer = new Timer(this.waitTime, listener);
			timer.setRepeats(false);

			// Wait and start the next trial.
			timer.start();
		}
		else {
			// Close the writer to save the writes. 
			try {
				this.writer.close();
				this.sendResultsToEmailAndDropbox();
			} 
			catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "Writer failed to close. The data may not have been written. Please report this issue.\n" + e1.getMessage() + ".");
			}

			// Inform the user the search task has ended. 
			JOptionPane.showMessageDialog(null, "Congratulations. You have successfully completed the search task.");

			// Restore the saved state of the program.
			this.restoreState();

			// Reset the log functionality after it is safe to do so.
			ActionListener listener = new ActionListener() {
				@Override 
				public void actionPerformed(ActionEvent e){
					doNotLog = false;
				}
			};

			// Begin timer and wait for the safe point to undo the log "lock".
			Timer timer = new Timer(200, listener);
			timer.setRepeats(false);
			timer.start();

			// Reset the start time.
			this.startTime();
			this.startThumbsTime();
		}
	}

	/**
	 * A method to begin the task.
	 * A message will be displayed to the participant before the task actually begins.
	 */
	public void runTask(){
		// The number of trials to complete.
		int numTrials = 12;

		if (this.task == null) {
			// Initialize the task and generate unique random numbers.
			this.task = new Task();
			int[] randNums = new int[numTrials/2];
			int[] randTargs = new int[numTrials/2];

			// Generate unique numbers for the trials so the trials are not repeated.
			boolean unique;
			do{
				unique = true;
				for (int n = 0; n < randNums.length; n += 1){
					randNums[n] = this.generateRandomPageNum();
					randTargs[n] = this.generateRandomPageNum();
					while (randTargs[n] == randNums[n]) {
						randTargs[n] = this.generateRandomPageNum();
					}
				}

				// Ensure the numbers are unique;
				for (int i = 0; i < randNums.length; i += 1){
					for (int n = 0; n < randNums.length; n += 1){
						if (i != n && randNums[i] == randNums[n]){
							unique = false;
						}
					}
				}
			} while (!unique);

			// Generate the trials.
			this.task.generateRandomTrials(randTargs, randNums);
		}

		// Determine which condition is being tested and display the correct message.
		String message = "";
		if (this.thumbInterface != null && this.condition.equals("Thumbnails")){
			message = "\tPlease read the following carefully and ONLY click OK once you have understood all of the information."
					+ "\n\nOnce you click OK, you will be granted " + this.waitTime/1000 + " second to view a"
					+ "\nrandom page from the PDF document that will be displayed on the right of the screen."
					+ "\nWhen the " + this.waitTime/1000 + " second is up, another window will open to the"
					+ "\nleft of the document page. This window will be the PDF Viewer application. Clicking"
					+ "\nthe middle mouse button (scroll wheel) or a right-click will open another window. This window will contain"
					+ "\nall the pages of the PDF document laid out within the single window as thumbnails. These"
					+ "\nthumbnails may be small, but if they are too small, hovering the mouse over the thumbnail"
					+ "\nwill produce an enlarged image. Your objective is to find the document page displayed on"
					+ "\nthe right of the screen. To do this, simply click the left mouse button on the thumbnail"
					+ "\nof the page you wish to select. This will open the page in the left window, the PDF viewer."
					+ "\nTo indicate you think you have found the correct page, click the document page that appears"
					+ "\nin the viewer. If the page is not the correct page, the page on the right will not change."
					+ "\nContinue searching for the correct page until the page on the right changes. When the page"
					+ "\non the right changes, the left window will momentarily disappear and you will have " + this.waitTime/1000 + " seconds"
					+ "\nto examine the new page. Your objective is to again locate the correct page. If you have"
					+ "\nany questions or concerns, do not be afraid to contact the HCI Lab."
					+ "\n\nYou may select the OK button to begin the exercise when you are ready.";
		}
		else if (this.condition.equals("Scrolling")){
			message = "\tPlease read the following carefully and ONLY click OK once you have understood all of the information."
					+ "\n\nOnce you click OK, you will be granted " + this.waitTime/1000 + " second to view a"
					+ "\nrandom page from the PDF document that will be displayed on the right of the screen."
					+ "\nWhen the " + this.waitTime/1000 + " second is up, another window will open to the"
					+ "\nleft of the document page. This window will contain all the pages of the PDF document"
					+ "\nlaid out within a single column, some of which will not be visible on the screen. Your"
					+ "\nobjective is to find the document page displayed on the right of the screen. To do this,"
					+ "\nsimply scroll through the document using the mouse wheel or the scrollbar on the right of"
					+ "\nthe PDF viewer, the window on the left, but do not use the arrows. To indicate you think"
					+ "\nyou have found the correct page, click the document page within the viewer. If the page is"
					+ "\nnot the correct page, the page on the right will not change. Continue searching for the"
					+ "\ncorrect page until the page on the right changes. When the page on the right changes,"
					+ "\nthe left window will momentarily disappear and you will have " + this.waitTime/1000 + " second"
					+ "\nto examine the new page. Your objective is to again locate the correct page. If you have"
					+ "\nany questions or concerns, do not be afraid to contact the HCI Lab."
					+ "\n\nYou may select the OK button to begin the exercise when you are ready.";
		}
		else {
			message = "\tPlease read the following carefully and ONLY click OK once you have understood all of the information."
					+ "\n\nOnce you click OK, you will be granted " + this.waitTime/1000 + " second to view a"
					+ "\nrandom page from the PDF document that will be displayed on the right of the screen."
					+ "\nWhen the " + this.waitTime/1000 + " second is up, another window will open to the"
					+ "\nleft of the document page. This window will contain all the pages of the PDF document"
					+ "\nlaid out within a single column, some of which will not be visible on the screen. Your"
					+ "\nobjective is to find the document page displayed on the right of the screen. To do this,"
					+ "\nsimply scroll through the document using the mouse wheel or the scrollbar on the right of"
					+ "\nthe PDF viewer, the window on the left, but do not use the arrows. You may also use a column"
					+ "\non the left of the viewer in which all the pages are laid out as thumbnails by"
					+ "\nclicking one of the thumbnails within this scrollable column. To indiciate you think"
					+ "\nyou have found the correct page, click the document page within the viewer. If the page is not"
					+ "\nthe correct page, you will simply be shown the page in the viewer. Continue searching for the "
					+ "\ncorrect page until the viewer disappears. Another page will be displayed on the right and "
					+ "\nyou will have " + this.waitTime/1000 + " second to examine the new page. Your objective is to "
					+ "\nagain locate the correct page. If you have any questions or concerns, do not be afraid to contact"
					+ "\nthe HCI Lab."
					+ "\n\nYou may select the OK button to begin the exercise when you are ready.";
		}
		JOptionPane.showMessageDialog(null, message);

		// Close the writer to save the writes. 
		try {
			if (this.writer != null) {
				this.writer.close();
			}
		} 
		catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "Writer failed to close. The data may not have been written. Please report this issue.\n" + e1.getMessage() + ".");
		}

		// Initialize the log file for the subject.
		try {
			this.initializeLogFileAndWriter();
		} 
		catch (FileNotFoundException | RuntimeException e) {
			JOptionPane.showMessageDialog(null, "Log file could not be initialized. Please report this issue.\n" + e.getMessage() + ".");
		}

		// Start the first trial.
		this.nextTrial();
	}

	/**
	 * Initializes the participant by collecting the information from a textfile that should be within the 
	 * current directory. 
	 * @throws FileNotFoundException when the currentParticipant.txt file is not found.
	 */
	@SuppressWarnings("resource")
	public static void initializeParticipant() throws FileNotFoundException{
		// Try to load the participant demographic data. 
		Scanner fileIn = null;
		fileIn = new Scanner(new File(Main.Main.location + File.separator + "participant" + File.separator + "participant_data.txt"));

		try{
			// Extract the participant data. 
			int ID = Integer.parseInt(fileIn.nextLine());
			int age = Integer.parseInt(fileIn.nextLine());
			Sex sex = Participant.Sex.MALE;
			if (fileIn.nextLine().equals("FEMALE")){
				sex = Participant.Sex.FEMALE;
			}
			boolean isStudent = true;
			if (fileIn.nextLine().equals("false")){
				isStudent = false;
			}
			int hoursPerWeek = Integer.parseInt(fileIn.nextLine());
			int usagePerWeek = Integer.parseInt(fileIn.nextLine());

			// Create the participant.
			participant = new Participant(ID, age, sex, isStudent, hoursPerWeek, usagePerWeek);
		}
		catch (Exception e){
			throw new RuntimeException("The participant_data.txt file was not formatted correctly.\n" + e.getMessage() + ".");
		}
	}

	/**
	 * Accessor to return the number of pages within the document.
	 * @return - The number of pages within the document.
	 */
	public int getNumPages(){
		return this.controller.getDocument().getNumberOfPages();
	}

	/**
	 * Increases the number of pages visited by one.
	 */
	public void pageVisited(String method){
		// Set the current page to display as visited. 
		this.thumbInterface.pageVisited(this.currentPageNumber-1);
		this.thumbsPanel.pageVisited(this.currentPageNumber-1);

		// Write the visit to the log file.
		if (method.equals("PageView") && !this.visitLogged) {
			// Increase the page count.
			this.pageCount += 1;

			this.writeToLogFile("NA", "NA", "NA", "NA", "NA", "NA", "NA", (this.currentPageNumber), method, "PageVisit");
		}
		else {
			// Increase the page count.
			this.pageCount += 1;

			this.writeToLogFile("NA", "NA", "NA", "NA", "NA", "NA", "NA", (this.currentPageNumber), method, "PageVisit");
		}

		// Indicate the page visit has been logged.
		this.visitLogged = true;
	}

	/**
	 * Setup the PDFViewer for the scrollCondition.
	 */
	public void setupScrollCondition(){
		// Setup the proper view.
		this.controller.getDocumentViewController().setViewType(DocumentViewControllerImpl.ONE_COLUMN_VIEW);

		// Get the scrollbar and add the adjustment listener to log scroll events. 
		JScrollBar scrollbar = (JScrollBar) this.controller.getDocumentViewController().getVerticalScrollBar();
		scrollbar.addAdjustmentListener(new AdjustmentListener() {

			/**
			 * Temporary store the current page number. 
			 */
			private int tempPage;

			/**
			 * The beginning of the scroll.
			 */
			private int beginning;

			/**
			 * The current result from the current scroll event.
			 */
			private double result;

			/**
			 * True if the user has begun scrolling.
			 */
			private boolean start = true;

			/**
			 * Log the sub-method used.
			 */
			private String sub = null;

			/**
			 * The time to wait to finish the speed computation in milliseconds.
			 */
			private int speedWait = 200;

			// Wait the specified time after an event and then determine the speed at which the scrollbar was moving.
			private ActionListener speedListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e){
					// Finish the computation.
					int ending = scrollbar.getValue();
					result = Math.abs((ending-beginning)/(double) speedWait);
					DecimalFormat decForm = new DecimalFormat("#0.00");

					// Log the relevant information.
					if (result != 0.0 && !doNotLog){
						String x = "" + scrollbar.getX();
						writeToLogFile(x, x, "" + beginning, "" + ending, "" + speedWait, decForm.format(result), "NA", currentPageNumber, "PageView", sub);
					}

					// Reset to restart the computation.
					start = true;
				}
			};

			/**
			 * The timer to determine the speed of the scroll bar.
			 */
			private Timer speedTimer = new Timer(this.speedWait, speedListener);

			/**
			 * The time to wait to determine if a user visited the page in milliseconds. 
			 */
			private int visitWait = 1000; 

			// Wait the specified time to determine if a user has visited a page. 
			private ActionListener visitListener = new ActionListener() {
				public void actionPerformed(ActionEvent event){
					if (tempPage == currentPageNumber /*&& !doNotLog*/ && controller.getDocument() != null && !visitLogged){
						pageVisited("PageView");
					}
				}
			};

			/**
			 * The timer to determine a page visit.
			 */
			private Timer visitTimer = new Timer(this.visitWait, visitListener);

			/** 
			 * Determine the correct page number and the speed at which the user is scrolling.
			 * Also, determine which sub-method was used to do the scrolling.
			 */
			@Override
			public void adjustmentValueChanged(AdjustmentEvent event) {
				// Determine the correct page number.
				int pageNum = 0;
				if (controller.getDocument() != null){
					int max = controller.getDocumentViewController().getVerticalScrollBar().getMaximum();
					double width = (double) max / (double) getNumPages();
					pageNum = (int) Math.round(scrollbar.getValue()/width + 1);
				}

				// Timers should only repeat when invoked.
				this.speedTimer.setRepeats(false);
				this.visitTimer.setRepeats(false);
				
				// Correct the page number.
				boolean page_changed = false;
				if (pageNum != currentPageNumber) {
					currentPageNumber = pageNum;
					page_changed = true;
				}

				// Increase the current page count if a new page is visited.
				if ((page_changed && !doNotLog) || (page_changed && button_nav)){
					// Indicate the new page has not been visited.
					visitLogged = false;

					// Determine if the page is visited.
					this.tempPage = currentPageNumber;
					if (this.visitTimer.isRunning()){
						this.visitTimer.restart();
					}
					else {
						this.visitTimer.start();
					}
				}

				// If beginning of the scroll, determine the starting location.
				if (start && !doNotLog && PDFViewer.isVisible() && controller.getDocument() != null && !mouse_down){
					beginning = scrollbar.getValue();
					start = false;

					// Determine if the scrollbar or the mouse wheel caused the event.
					if (event.getValueIsAdjusting()){
						sub = "ScrollBar";
					}
					else {
						sub = "MouseWheel";
					}
					
					// Begin the wait period before computing speed.
					this.speedTimer.start();
				}
				else if (start && !doNotLog && PDFViewer.isVisible() && controller.getDocument() != null && mouse_down){
					beginning = scrollbar.getValue();
					start = false;
					
					// Log the mouse dragged event.
					sub = "MouseDrag";
					this.speedTimer.start();
				}

				// Repaint so the borders are the correct color.
				if (thumbsPanel != null){
					thumbsPanel.repaint(0);
				}
			}
		});
	}

	/**
	 * Initialize the subject's log file.
	 * @throws RuntimeException when the file already exists. Shouldn't happen because the file should be unique. 
	 * @throws FileNotFoundException when the file cannot be found.
	 */
	public void initializeLogFileAndWriter() throws RuntimeException, FileNotFoundException{
		// Extract the date for the file name.
		DateFormat format = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");

		// Extract the subject's user name to be used in the file name.
		String fileName = "";
		String header = "";

		// Initialize the header.
		header += "ID" + "\t" + "Age" + "\t" + "Sex" + "\t" + "isStudent" + "\t" + "Hours/DayOnComputer" + "\t" + "Hours/DayReadingsDocs";

		// Setup the rest of the file name.
		fileName += participant.getID() + "_" + this.documentName + "_" + this.condition + "_" + format.format(new Date()) + ".txt";

		// Create the file.
		this.currentLogFile = new File(Main.Main.location + File.separator + "data"+ File.separator + this.condition + File.separator + fileName);
		if (this.condition.equals("None")){
			this.userLogFile = this.currentLogFile;
		}

		// Do not overwrite already existing files. 
		if (currentLogFile.exists()){
			throw new RuntimeException("The log file already exists. Please try again.");
		}

		// Includes testing results if necessary.
		String testResults = "";
		if (!this.condition.equals("None")){
			testResults += "CurrentTrialNum" + "\t" + "CurrentTrialTime(s)" + "\t" + "CurrentTaskTime(s)" + "\t" + "DistanceFromPage(pages)" + "\t" + "Target" + "\t" + "StartDistance" + "\t" + "Difficulty" +"\t";
		}

		// Initialize the writer and write out the headings.
		try {
			this.writer = new FileWriter(currentLogFile, true);
		} 
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage() + ". Please report this issue.");
		}

		// Clear the log and determine the header.
		this.log_result = header + "\t" + "Date(dd/mm/yyyy_hh:mm:ss)" + "\t" + "DocName" + "\t" + "NumPages"
				+ "\t" + "x1" + "\t" + "x2" + "\t" + "y1" + "\t" + "y2" + "\t" + "waitTime" + "\t" + "ScrollSpeed(p/ms)" + "\t" + "MouseSpeed(p/ms)" + "\t" + "PageNum" + "\t" + "Method" + "\t" 
				+ "SubMethod" + "\t" + "IsEnlargedThumbVisible" + "\t" + "OriginalThumbSize(WxH)" + "\t" + "WindowSize(WxH)" + "\t" + "IsThumbsColVisible" + "\t" + "ScrollWindowTime(s)" + "\t" 
				+ "SFTInterfaceTime(s)" + "\t" + "ThumbsTime(s)" + "\t" + "TotalInterfaceTime(s)" + "\t" + "NumPagesSearched/Visited" + "\t" + testResults + "Condition" + "\t" + "Zoom(%)" + System.getProperty("line.separator");

		// Try to write the header.
		try {
			this.writer.write(this.log_result);
		} 
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Writer failed to write the header. Please report this issue.\n" + e.getMessage() + ".");
		}
	}

	/**
	 * Writes the results of the session to the log file.
	 */
	public void writeToLogFile(String x1, String x2, String y1, String y2, String waitTime, String scroll_speed, String mouse_speed, int page_num, String method, String sub_method){
		// Record the correct time for each interface before logging.
		if (!sub_method.equals("SFTswitch")) {
			this.endTime();
			this.startTime();

			this.endThumbsTime();
			this.startThumbsTime();
		}

		// The date and time of day.
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss");

		// Setup for user or participant.
		String id = "", age = "", sex = "", is_student = "", hours_comp = "", hours_docs = "";
		id += participant.getID() + "\t";
		age += participant.getAge() + "\t";
		sex += participant.getSex() + "\t";
		is_student += participant.isStudent() + "\t";
		hours_comp += participant.getHoursComp() + "\t";
		hours_docs += participant.getHoursDocs() + "\t";

		// Define the format when logging double values.
		DecimalFormat decForm = new DecimalFormat("#0.00");

		// Determine if testing results are needed.
		String trialNum = "", trialTime = "", totalTime = "", distance = "", target = "", start_dist = "", difficulty = "";
		if (!this.condition.equals("None")){
			trialNum = this.task.getCurrentTrialNumber() + "\t";
			trialTime = decForm.format(this.task.getCurrentTrial().getCurrentTrialTime()/1000.0) + "\t";
			if (method.equals("PageFound")) {
				totalTime = decForm.format(this.task.getTotalTime()/1000.0) + "\t";
			}
			else {
				totalTime = decForm.format(this.task.getCurrentTotalTime()/1000.0) + "\t";
			}
			distance = Math.abs((page_num-this.pageFrame.getPageNum())) + "\t";
			target = this.pageFrame.getPageNum() + "\t";
			start_dist = this.task.getCurrentTrial().getStartDist() + "\t";
			difficulty = this.task.getCurrentTrial().getDifficulty() + "\t";
		}

		// Determine the time each interface has been open.
		double scrollTime = this.scrollTime/1000.0;
		double sftTime = this.SFTtime/1000.0;
		double thumbsTime = this.thumbsTime/1000.0;
		double interfaceTime = (this.scrollTime + this.SFTtime)/1000.0;

		// Determine if the enlarged thumbnails are visible.
		boolean isEnlargedThumbVisible = false;
		if (this.thumbInterface != null && this.thumbInterface.isEnlargedThumbnailVisible()) {
			isEnlargedThumbVisible = true;
		}

		// Determine if thumbs are visible and thumb dimensions if necessary.
		String thumbDimens = "";
		if (this.thumbInterface != null && !this.condition.equals("Scrolling") && this.thumbInterface.isVisible()){
			thumbDimens = this.thumbInterface.getThumbnailWidth() + "x" + this.thumbInterface.getThumbnailHeight();
		}
		else {
			thumbDimens = "NA";
		}

		// Determine the current frame size. 
		String windowDimens = this.PDFViewer.getWidth() + "x" + this.PDFViewer.getHeight();

		boolean paneVisible = false;
		if (this.thumbsPane != null && this.thumbsPane.isVisible() && !this.thumbInterface.isVisible()){
			paneVisible = true;
		}
		
		// Determine the user zoom in percentage.
		String zoom = "NA";
		if (this.thumbInterface != null && !this.thumbInterface.isVisible() && this.controller.getDocument() != null) {
			zoom = "" +  Math.round((this.controller.getUserZoom()*100));
		}

		// Concatenate the entire log result.
		this.log_result = /*id + age + sex + is_student + hours_comp + hours_docs + format.format(new Date()) + "\t"
				+ this.documentName + "\t" + this.getNumPages() + "\t" + x1 + "\t" + x2 + "\t" + y1 + "\t" + y2 + "\t" + waitTime + "\t" + scroll_speed + "\t" + mouse_speed + "\t" + page_num + "\t" + */method + "\t" + sub_method/* + "\t" + isEnlargedThumbVisible
				+ "\t" + thumbDimens + "\t" + windowDimens + "\t" + paneVisible + "\t" + decForm.format(scrollTime) + "\t" + decForm.format(sftTime) + "\t" + decForm.format(thumbsTime) + "\t" + decForm.format(interfaceTime)
				+ "\t" + this.pageCount + "\t" + trialNum + trialTime + totalTime + distance + target + start_dist + difficulty + this.condition + "\t" + zoom*/ + System.getProperty("line.separator");

		// Write the logging data.
		try {
			if (this.writer != null) {
				this.writer.write(this.log_result);
				System.out.println(this.log_result);
			}
		} 
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Writer failed to write some data. Please report this issue.\n" + e.getMessage() + ".");
		}
	}

	/**
	 * Start the "timer" to compute how long each window is open.
	 */
	public void startTime(){
		this.startTime = System.currentTimeMillis();
	}

	/**
	 * Determines which window was previously open and gives the appropriate time to the window.
	 */
	public void endTime(){
		// Determine which interface is visible and assign the time.
		long end = System.currentTimeMillis();
		long time = end-this.startTime;
		if (this.thumbInterface != null && this.PDFViewer.isVisible() && this.thumbInterface.isVisible()){
			this.SFTtime += time;
		}
		else if (this.thumbInterface != null && this.PDFViewer.isVisible() && !this.thumbInterface.isVisible()){
			this.scrollTime += time;
		}
	}

	/**
	 * Start the "timer" for the thumbs condition.
	 */
	public void startThumbsTime(){
		this.thumbsStartTime = System.currentTimeMillis();
	}

	/**
	 * End the timer for the thumbs column and assign the time.
	 */
	public void endThumbsTime(){
		long end = System.currentTimeMillis();
		long time = end-this.thumbsStartTime;
		if (this.thumbsPane != null && this.thumbsPane.isVisible() && !this.thumbInterface.isVisible()){
			this.thumbsTime += time;
		}
	}

	/**
	 * Mutator to set the path name of the document being opened.
	 * @param pathName - A string representation of the path name for the file used.
	 */
	public void setDocumentName(String pathName, boolean parse){
		String doc_name = "";
		// Extract the name of the file.
		if (parse) {
			int start = 0;
			int end = 0;
			for (int n = 0; n <  pathName.length(); n += 1){
				// Look for both Unix and Windows directory separators.
				if (((Character) pathName.charAt(n)).equals('/') || ((Character) pathName.charAt(n)).equals(':') || ((Character) pathName.charAt(n)).equals('\\')){
					start = n+1;
				}
				if (pathName.substring(n).equals(".pdf")){
					end = n;
				}
			}

			doc_name = pathName.substring(start, end);
		}
		else {
			doc_name = pathName;
		}

		// Set the doc name.
		this.documentName = doc_name;
	}

	/**
	 * Resets the current state of the controller to allow the user to participate in a search task.
	 */
	public void resetState(){
		this.SFTtime = 0;
		this.scrollTime = 0;
		this.thumbsTime = 0;
		this.pageCount = 0;
	}

	/**
	 * Saves the current state of the controller to return to when a search task is done.
	 */
	public void saveState(){
		// End the current time states of the two interfaces.
		this.endTime();

		boolean thumbsVisible = false;
		if (this.saveFullState && this.thumbInterface != null) {
			thumbsVisible = this.thumbInterface.isVisible();
		}

		boolean paneVisible = false;
		if (this.thumbsPane != null){
			paneVisible = this.thumbsPane.isVisible();
		}

		// Save the important state information.
		this.controllerState = new ControllerState(this.saveFullState, thumbsVisible, this.currentPageNumber, this.SFTtime, this.scrollTime, this.thumbsTime, this.pageCount, this.PDFViewer.getBounds(), this.thumbInterface, this.PDFViewer.getExtendedState(), this.thumbsPanel, this.thumbsPane, paneVisible);
	}

	/**
	 * Restores the state of interface controller.
	 */
	public void restoreState(){
		// Reset the task so another can be performed. 
		this.task = null;

		// Restore the state of the interface controller.
		this.SFTtime = this.controllerState.getSFTtime();
		this.scrollTime = this.controllerState.getScrollTime();
		this.thumbsTime = this.controllerState.getThumbsTime();
		this.pageCount = this.controllerState.getPageCount();

		// Try to reset the writer if necessary.
		this.currentLogFile = this.userLogFile;
		if (this.controllerState.restoreFull()) {
			try {
				this.writer = new FileWriter(this.userLogFile, true);
			} 
			catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Unable to restore the user log file. Please report this issue.\n" + e.getMessage() + ".");
			} 
		}
		else {
			this.controller.closeDocument();
			this.writer = null;
		}

		// Restore the use of the scrollbar.
		JPanel viewerComponentPanel = (JPanel) this.PDFViewer.getContentPane().getComponent(0);
		JScrollPane scrollPane = (JScrollPane) viewerComponentPanel.getComponent(2);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		// Restore the visibility of the toolbar, lower components, and the menubar.
		JToolBar toolBar = (JToolBar) viewerComponentPanel.getComponent(0);
		JMenuBar viewerComponentMenu = (JMenuBar) this.PDFViewer.getContentPane().getComponent(1);
		JPanel panel = (JPanel) viewerComponentPanel.getComponent(1);
		toolBar.setVisible(true);
		viewerComponentMenu.setVisible(true);
		panel.setVisible(true);

		// Restore the thumbnail interface.
		this.thumbInterface = this.controllerState.getThumbsInterface();
		if (this.thumbInterface != null) {
			this.PDFViewer.setGlassPane(this.thumbInterface);
			this.thumbInterface.setVisible(false);
		}

		// Restore the thumbnails column.
		this.thumbsPanel = this.controllerState.getThumbsPanel();
		if (this.thumbsPanel != null){
			this.thumbsPane.setViewportView(this.thumbsPanel);
		}
		this.thumbsPane.setVisible(this.controllerState.isPaneVisible());

		// Set the button on the toolbar to toggle the visibility of the thumbs pane.
		JToolBar toolbar = (JToolBar) viewerComponentPanel.getComponent(0);
		JToolBar first_toolbar = (JToolBar) toolbar.getComponent(0);
		JToggleButton toggleButton = (JToggleButton) first_toolbar.getComponent(2);

		// Make sure the button is not already selected. 
		int lastIndex = toggleButton.getActionListeners().length-1;
		if (this.thumbsPane != null && toggleButton.isSelected() && !this.thumbsPane.isVisible()){
			toggleButton.getActionListeners()[lastIndex].actionPerformed(new ActionEvent(toggleButton, ActionEvent.ACTION_PERFORMED, null));
		}
		else if (this.thumbsPane != null && !toggleButton.isSelected() && this.thumbsPane.isVisible()){
			toggleButton.getActionListeners()[lastIndex].actionPerformed(new ActionEvent(toggleButton, ActionEvent.ACTION_PERFORMED, null));
		}
		else if (toggleButton.isSelected()){
			toggleButton.getActionListeners()[lastIndex].actionPerformed(new ActionEvent(toggleButton, ActionEvent.ACTION_PERFORMED, null));
		}

		//Restore the states of the frames.
		this.PDFViewer.setExtendedState(this.controllerState.getState());
		this.sleep(1);

		// Restore the window visibility and size.
		this.PDFViewer.setBounds(this.controllerState.getBounds());

		// Restore the visibility of the interfaces.
		this.PDFViewer.setVisible(true);

		// Restore the last page being viewed (adjust for index).
		if (this.controllerState.restoreFull()) {
			this.currentPageNumber = this.controllerState.getCurrentPageNumber();
			this.controller.showPage(this.controllerState.getCurrentPageNumber()-1);
		}
		else {
			this.currentPageNumber = 1;
			this.images = null;
		}

		// Reset to allow the user to use the thumbnail interface after the trial has ended.
		this.condition = "None";

		// Repaint the viewer.
		this.PDFViewer.repaint(0);
	}

	/**
	 * Sets the current condition being tested.
	 * @param condition - The condition being tested.
	 */
	public void setCondition(String condition){
		this.condition = condition;
	}

	/**
	 * Accessor to retrieve the current condition.
	 * @return - The current condition.
	 */
	public String getCondition(){
		return this.condition;
	}

	/**
	 * Setter to set the current page number.
	 * @param num - The page number.
	 */
	public void setCurrentNumber(int num){
		this.currentPageNumber = num;
	}

	/**
	 * Accessor to retrieve the current page number.
	 * @return - The current page number.
	 */
	public int getCurrentNumber() {
		return this.currentPageNumber;
	}

	/**
	 * End the current session.
	 */
	public void endSession(){
		// Log the event.
		this.writeToLogFile("NA", "NA", "NA", "NA", "NA", "NA", "NA", (this.currentPageNumber), "PageView", "SessionEnd");

		// Attempt to close the writer.
		try {
			this.writer.close();
			this.sendResultsToEmailAndDropbox();
		} 
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Writer failed to close. Please report this issue.\n" + e.getMessage() + ".");
		}

		// Do not allow logging while the session is being ended.
		this.doNotLog = true;

		// Reset the state for accurate logging.
		this.resetState();

		// Close the thumbs column. 
		JPanel viewerComponentPanel = (JPanel) this.PDFViewer.getContentPane().getComponent(0);
		this.thumbsPane.remove(this.thumbsPanel);
		this.thumbsPanel = null;
		this.thumbsPane.setVisible(false);

		// Retrieve the toggle button.
		JToolBar toolbar = (JToolBar) viewerComponentPanel.getComponent(0);
		JToolBar first_toolbar = (JToolBar) toolbar.getComponent(0);
		JToggleButton toggleButton = (JToggleButton) first_toolbar.getComponent(2);

		// Remove the action listener from the toggle button if necessary.
		if (toggleButton.getActionListeners().length > 1){
			toggleButton.removeActionListener(toggleButton.getActionListeners()[0]);
		}

		// Make sure the button is not already selected. 
		if (toggleButton.isSelected()){
			int lastIndex = toggleButton.getActionListeners().length-1;
			toggleButton.getActionListeners()[lastIndex].actionPerformed(new ActionEvent(toggleButton, ActionEvent.ACTION_PERFORMED, null));
		}


		// Reset the thumbnail interface.
		this.thumbInterface = null;

		// Set the current log file to be empty because no document is open.
		this.userLogFile = null;
		this.currentLogFile = null;

		// Reset the visit logged bool.
		this.visitLogged = false;

		// Reset the page number.
		this.currentPageNumber = 1;

		// Reset the images. 
		this.images = null;
	}

	/**
	 * If set to true, logging is disabled. 
	 * @param doNotLog - True if logging is to be disabled, false otherwise.
	 */
	public void setDoNotLog(boolean doNotLog){
		this.doNotLog = doNotLog;
	}

	/**
	 * Send the data over the server to be written into a file. 
	 */
	public void sendResultsToServer(){
		// Establish the client and post method. 
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost("http://localhost:5000/submit_labyrinth");

		Scanner in = null;
		String result = "";
		try {
			in = new Scanner(this.currentLogFile);

			while (in.hasNext()){
				result += in.nextLine();
			}
		} 
		catch (FileNotFoundException e1) {
			JOptionPane.showMessageDialog(null, "Failed to send file over server. Please send the text document that will be generated. The document's name is: " + this.currentLogFile.getName() + ".\n" + e1.getMessage() + ".");
		}

		// Setup the form message.
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("message", "text_file"));
		nvps.add(new BasicNameValuePair("filename", this.currentLogFile.getName()));
		nvps.add(new BasicNameValuePair("log_result", result));

		// Try creating the form to be sent
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
		} 
		catch (UnsupportedEncodingException e) {
			JOptionPane.showMessageDialog(null, "Failed to send the data over the server. Please send the text document that will be generated. The document's name is: " + this.currentLogFile.getName() + ".\n" + e.getMessage() + ".");
		}

		// Try executing the request.
		try {
			httpClient.execute(httpPost);
		} 
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, "The program failed to send the data over the server. Please send the text document that will be generated. The document's name is: " + this.currentLogFile.getName() + ".\n" + e.getMessage() + ".");
		}
	}

	/**
	 * Sends the produced data file to a dropbox location.
	 */
	public void sendResultsToDropbox(){
		// Get your app configurations.
		DbxRequestConfig config = new DbxRequestConfig(
				"JavaTutorial/1.0", Locale.getDefault().toString());

		// The access token to access the specific dropbox location.
		String accessToken = "K98DJVtbLLAAAAAAAAAAEGywQeryGerEqnwF6nRJOZ3U9aW2ihfN0unc52IJcL3I";

		// My client information.
		DbxClient client = new DbxClient(config, accessToken);
		try {
			if (!client.getAccountInfo().displayName.equals("Nickolas Gough")){
				JOptionPane.showMessageDialog(null, "Failed to send the data file to the correct dropbox location. Please send the text file that will be generated. The document's name is: " + this.currentLogFile.getName() + ".");
			}
		} 
		catch (DbxException e) {
			JOptionPane.showMessageDialog(null, "Failed to send the data file to the correct dropbox location. Please send the text file that will be generated. The document's name is: " + this.currentLogFile.getName() + ".\n" + e.getMessage() + ".");
		}

		// Upload the file.
		FileInputStream inputStream = null;
		String condition = "";
		if (this.condition.equals("Thumbnails")) {
			condition = "thumbs_condition/";
		}
		else if (this.condition.equals("Scrolling")) {
			condition = "scroll_condition/";
		}
		else if (this.condition.equals("ScrollAndThumb")) {
			condition = "scroll_and_thumbs_condition/";
		}
		else {
			condition = "none/";
		}
		try {
			inputStream = new FileInputStream(this.currentLogFile);
			client.uploadFile("/" + condition + this.currentLogFile.getName(),
					DbxWriteMode.add(), this.currentLogFile.length(), inputStream);
			inputStream.close();
		} 
		catch (IOException | DbxException e) {
			JOptionPane.showMessageDialog(null, "Failed to upload the data file to dropbox. Please report this issue and send the text file that will be generated. The document's name is: " + this.currentLogFile.getName() + ".\n" + e.getMessage() + ".");
		}
	}

	/**
	 * Sends the data results to both email and dropbox.
	 */
	public void sendResultsToEmailAndDropbox(){
		if (this.currentLogFile != null){
			// Send results to dropbox.
			this.sendResultsToDropbox();
		}
	}

	/**
	 * Sleep for the specified number of milliseconds. 
	 * @param ms - The time to sleep in milliseconds.
	 */
	public void sleep(int ms){
		try {
			Thread.sleep(ms);
		} 
		catch (InterruptedException e) {
			// Do nothing.
		}
	}

	/**
	 * Show the current page. 
	 */
	public void showCurrentPage(){
		if (this.PDFViewer.isVisible() && this.controller.getDocument() != null) {
			this.controller.showPage(this.currentPageNumber-1);
		}
	}

	/**
	 * Indicate the full state should be saved.
	 * @param set - True if the full state should be saved, false otherwise.
	 */
	public void setFullSave(boolean set) {
		this.saveFullState = set;
	}

	/**
	 * Generates the task trials from the doc.
	 * @param doc - The document name that is being used to complete the trial.
	 * @throws FileNotFoundException when the file is not found. 
	 */
	public void generateTaskFromFile(String folder, String doc, String condition) throws FileNotFoundException{
		this.task = new Task();
		this.task.generateDocTrials(folder, doc, condition);
	}
	
	/**
	 * Show the loading cursor.
	 */
	public void showLoadingCursor() {
		controller.getDocumentViewController().setViewCursor(DocumentViewController.CURSOR_WAIT);
	}
	
	/**
	 * Show the default cursor.
	 */
	public void showDefaultCursor() {
		controller.getDocumentViewController().setViewCursor(DocumentViewController.CURSOR_DEFAULT);
	}
}
