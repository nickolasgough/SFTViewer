package Main;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ResourceBundle;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;
import org.icepdf.ri.common.views.DocumentViewController;
import org.icepdf.ri.common.views.DocumentViewControllerImpl;
import org.icepdf.ri.util.FontPropertiesManager;
import org.icepdf.ri.util.PropertiesManager;

import Control.InterfaceController;
import Frames.ParticipantInfoFrame;


/**
 * The <code>ViewerComponentExample</code> class is an example of how to use
 * <code>SwingController</code> and <code>SwingViewBuilder</code>
 * to build a PDF viewer component.  A file specified at the command line is
 * opened in a JFrame which contains the viewer component.
 *
 * @since 2.0
 * Modified by Nickolas Gough.
 */
public class Main {

	/**
	 * The necessary dependencies to run the program.
	 */
	private static SwingController controller;
	private static InterfaceController interfaceController;
	private static JPanel viewerComponentPanel;
	private static JMenuBar viewerComponentMenu;
	private static JPanel panel;
	private static JMenuItem id_item;
	private static File participant_file;
	private static File batch_file;
	
	/**
	 * The location of the program. TBD.
	 */
	public static String location;// = System.getProperty("user.dir");

	/**
	 * Setup and run the thumbs condition.
	 */
	public static void doThumbsCondition() {
		// Save the state of the controller before beginning the search trials.
		interfaceController.saveState();

		// Reset the state to for accurate logging of the trials.
		interfaceController.resetState();

		// Disable the logging.
		interfaceController.setDoNotLog(true);
		
		// Indicate the condition being tested.
		interfaceController.setCondition("Thumbnails");

		// Initialize the thumbs frame.
		interfaceController.setupSFTCondition();
		interfaceController.getThumbInterface().setVisible(false);

		// Initialize the scrolling thumbs. 
		interfaceController.setupThumbsCondition();
		interfaceController.getThumbsPane().setVisible(false);

		// Set the PDF viewer to be small enough to complete the trial and set the location.
		// Set the view.
		controller.getDocumentViewController().setDocumentViewType(DocumentViewControllerImpl.ONE_COLUMN_VIEW, DocumentViewController.PAGE_FIT_WINDOW_HEIGHT);
		interfaceController.getPDFViewer().setExtendedState(JFrame.MAXIMIZED_VERT);
		interfaceController.sleep(1);
		interfaceController.getPDFViewer().setVisible(true);
		interfaceController.getPDFViewer().setBounds(0, 0, InterfaceController.getScreenWidth()-interfaceController.getFindImageWidth(), InterfaceController.getScreenHeight());
		interfaceController.getPDFViewer().setVisible(false);

		// Hide the scroll bar. 
		JScrollPane scrollPane = (JScrollPane) viewerComponentPanel.getComponent(2);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

		// Hide the top tool bar, menu bar, and bottom components so user cannot "cheat".
		JToolBar toolBar = (JToolBar) viewerComponentPanel.getComponent(0);
		toolBar.setVisible(false);
		viewerComponentMenu.setVisible(false);
		panel.setVisible(false);

		// Run the task.
		interfaceController.runTask();
	}

	/**
	 * Setup and run the scroll condition.
	 */
	public static void doScrollCondition(){
		// Save the state of the controller before beginning the search trials.
		interfaceController.saveState();

		// Reset the state to for accurate logging of the trials.
		interfaceController.resetState();

		// Disable the logging.
		interfaceController.setDoNotLog(true);
		
		// Indicate the condition being tested.
		interfaceController.setCondition("Scrolling");

		// Initialize the thumbs frame.
		interfaceController.setupSFTCondition();
		interfaceController.getThumbInterface().setVisible(false);
		
		// Initialize the scrolling thumbs. 
		interfaceController.setupThumbsCondition();
		interfaceController.getThumbsPane().setVisible(false);

		// Set the PDF viewer to be small enough to complete the trial and set the location.
		// Set the view.
		controller.getDocumentViewController().setDocumentViewType(DocumentViewControllerImpl.ONE_COLUMN_VIEW, DocumentViewController.PAGE_FIT_WINDOW_HEIGHT);
		interfaceController.getPDFViewer().setExtendedState(JFrame.MAXIMIZED_VERT);
		interfaceController.sleep(1);
		interfaceController.getPDFViewer().setVisible(true);
		interfaceController.getPDFViewer().setBounds(0, 0, InterfaceController.getScreenWidth()-interfaceController.getFindImageWidth(), InterfaceController.getScreenHeight());
		interfaceController.getPDFViewer().setVisible(false);

		// Hide the top tool bar, menu bar, and bottom components so user cannot "cheat".
		JToolBar toolBar = (JToolBar) viewerComponentPanel.getComponent(0);
		toolBar.setVisible(false);
		viewerComponentMenu.setVisible(false);
		panel.setVisible(false);

		// Run the task.
		interfaceController.runTask();
	}

	/**
	 * Setup and run the scroll and thumbs condition.
	 */
	public static void doThumbsAndScrollCondition(){
		// Save the state of the controller before beginning the search trials.
		interfaceController.saveState();

		// Reset the state to for accurate logging of the trials.
		interfaceController.resetState();

		// Disable the logging.
		interfaceController.setDoNotLog(true);
		
		// Indicate the condition being tested.
		interfaceController.setCondition("ScrollAndThumb");
		
		// Initialize the thumbs frame.
		interfaceController.setupSFTCondition();
		interfaceController.getThumbInterface().setVisible(false);
		
		// Initialize the scrolling thumbs. 
		interfaceController.setupThumbsCondition();
		interfaceController.getThumbsPane().setVisible(true);
		
		// Set the PDF viewer to be small enough to complete the trial and set the location.
		// Set the view.
		controller.getDocumentViewController().setDocumentViewType(DocumentViewControllerImpl.ONE_COLUMN_VIEW, DocumentViewController.PAGE_FIT_WINDOW_HEIGHT);
		interfaceController.getPDFViewer().setExtendedState(JFrame.MAXIMIZED_VERT);
		interfaceController.sleep(1);
		interfaceController.getPDFViewer().setVisible(true);
		interfaceController.getPDFViewer().setBounds(0, 0, InterfaceController.getScreenWidth()-interfaceController.getFindImageWidth(), InterfaceController.getScreenHeight());
		interfaceController.getPDFViewer().setVisible(false);

		// Hide the top tool bar, menu bar, and bottom components so user cannot "cheat".
		JToolBar toolBar = (JToolBar) viewerComponentPanel.getComponent(0);
		toolBar.setVisible(false);
		viewerComponentMenu.setVisible(false);
		panel.setVisible(false);
		
		// Run the task.
		interfaceController.runTask();
	}
	
	// Set the id to be displayed in the help menu.
	public static void display_id(){
		// Get the ID and initialize it.
		Scanner in = null;
		try {
			in = new Scanner(participant_file);
			id_item.setText("Your ID: " + in.nextInt());
		} 
		catch (FileNotFoundException e){
			JOptionPane.showMessageDialog(null, "The participant data files could not be located. Report this issue.\n" + e.getMessage() + ".");
		}
		
		// Show the viewer.
		interfaceController.getPDFViewer().setVisible(true);
	}

	/**
	 * The main method to run the program.
	 * @param args - The arguments to the program.
	 */
	public static void main(String[] args) {
		// Build a component controller
		controller = new SwingController();
		controller.setIsEmbeddedComponent(false);
		
		PropertiesManager properties = new PropertiesManager(
				System.getProperties(),
				ResourceBundle.getBundle(PropertiesManager.DEFAULT_MESSAGE_BUNDLE));

		// Read/store the font cache.
		ResourceBundle messageBundle = ResourceBundle.getBundle(
				PropertiesManager.DEFAULT_MESSAGE_BUNDLE);
		new FontPropertiesManager(properties, System.getProperties(), messageBundle);

		properties.set(PropertiesManager.PROPERTY_DEFAULT_ZOOM_LEVEL, "1.00");

		SwingViewBuilder factory = new SwingViewBuilder(controller, properties);

		// Add interactive mouse link annotation support via callback
		controller.getDocumentViewController().setAnnotationCallback(
				new org.icepdf.ri.common.MyAnnotationCallback(controller.getDocumentViewController()));

		// Add the components.
		viewerComponentPanel = factory.buildViewerPanel();
		viewerComponentMenu = factory.buildCompleteMenuBar();
		
		// Remove the left component of the splitpane. 
		JSplitPane splitpane = (JSplitPane) viewerComponentPanel.getComponent(1);
		splitpane.remove(splitpane.getLeftComponent());
		viewerComponentPanel.remove(splitpane);
		viewerComponentPanel.add(splitpane.getRightComponent(), BorderLayout.CENTER);
		
		// Remove the search button.
		JToolBar toolbar = (JToolBar) viewerComponentPanel.getComponent(0);
		JToolBar first_toolbar = (JToolBar) toolbar.getComponent(0);
		JButton button = (JButton) first_toolbar.getComponent(2);
		first_toolbar.remove(button);
		
		// Create the open file menu item.
		JMenu fileMenu = viewerComponentMenu.getMenu(0);
		JMenu fileMenuItem = (JMenu) fileMenu.getItem(0);
		fileMenuItem.removeAll();
		JMenuItem openItem = new JMenuItem("File...");
		fileMenuItem.add(openItem);

		// Retrieve the close menu item.
		JMenuItem closeMenuItem = fileMenu.getItem(2);
		
		// The search tasks menu.
		JMenu search_tasks = new JMenu("<HTML><u>S</u>earch Tasks</HTML>");
		viewerComponentMenu.add(search_tasks);

		// Create the application frame.
		JFrame applicationFrame = new JFrame();
		applicationFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		// Add the viewing components.
		applicationFrame.getContentPane().add(viewerComponentPanel, BorderLayout.CENTER);
		applicationFrame.getContentPane().add(viewerComponentMenu, BorderLayout.NORTH);

		// Add the buttons to begin the search tasks.
		JButton thumbsButton = new JButton("Thumbs Search");
		thumbsButton.setMaximumSize(new Dimension(50, 50));
		JButton scrollButton = new JButton("Scroll Search");
		scrollButton.setMaximumSize(new Dimension(50, 50));
		JButton scrollAndThumbs = new JButton("Scroll and Thumbs Search");
		scrollAndThumbs.setMaximumSize(new Dimension(50, 50));

		panel = (JPanel) viewerComponentPanel.getComponent(1);
		JPanel bottomPanel = (JPanel) panel.getComponent(1);
		bottomPanel.removeAll(); // Remove the different views so usage can be logged.
//		bottomPanel.add(thumbsButton);
//		bottomPanel.add(scrollButton);
//		bottomPanel.add(scrollAndThumbs);

		// Add the window event callback to dispose the controller and
		// currently open document.
//		applicationFrame.addWindowListener(controller);

		// Show the application.
		applicationFrame.pack();
		applicationFrame.setResizable(true);
		applicationFrame.setSize(InterfaceController.getScreenWidth(), InterfaceController.getScreenHeight());
		applicationFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

		// Added controller to control the interfaces.
		interfaceController = new InterfaceController(applicationFrame, controller);
		
		// Determine the location of the file.
		location = interfaceController.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		String parent = "/pdfviewer";
		int last_index = location.lastIndexOf(parent);
		int beginning = 0, end = location.length()-1;
		if (last_index == -1) {
			char folder = '/';
			for (int n = 0; n < location.length(); n += 1) {
				if (location.charAt(n) == folder && n != end) {
					beginning = n;
				}
			}
		}
		else if (location.substring(last_index).equals("/pdfviewer_V7.jar")) {
			beginning = last_index;
		}
		else {
			beginning = last_index+parent.length();
		}
		location = location.substring(0, beginning);
		
		// Initialize the viewer to log scrolling data.
		interfaceController.setupScrollCondition();

		// Make the viewer invisible.
		applicationFrame.setVisible(false);

		// Setup the open file menu item.
		openItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Allow the user to specify and open a file.
				if (controller.getDocument() != null){
					JOptionPane.showMessageDialog(null, "The current document must be closed before another can be opened.");
					return;
				}
				controller.openFile();
				
				if (controller.getDocument() != null){
					// Do not allow logging as a result of opening a document after another has been closed.
					interfaceController.setDoNotLog(true);
					
					// Setup both to be used casually.
					interfaceController.setDocumentName(controller.getDocument().getDocumentLocation(), true);
					
					// Indicate there is no condition being tested.
					interfaceController.setCondition("None");
					
					// Setup the SFT interface.
					interfaceController.setupSFTCondition();
					interfaceController.getThumbInterface().setVisible(false);

					// Setup the side thumbnails column.
					interfaceController.setupThumbsCondition();
					interfaceController.getThumbsPane().setVisible(false);
					
					// Set the view.
					controller.getDocumentViewController().setDocumentViewType(DocumentViewControllerImpl.ONE_COLUMN_VIEW, DocumentViewController.PAGE_FIT_WINDOW_HEIGHT);

					// Initialize the logging file.
					try {
						interfaceController.initializeLogFileAndWriter();
					} 
					catch (FileNotFoundException | RuntimeException e) {
						JOptionPane.showMessageDialog(null, "The file to write data could not be initialized. Please report this issue.\n" + e.getMessage());
					}

					// Set the correct page number upon opening the document.
					interfaceController.setCurrentNumber(1);

					// Begin the timer to collect interface time usage.
					interfaceController.startTime();

					// Increase the page count since the user technically visits the first page.
					interfaceController.pageVisited("PageView");

					// Make the viewer visible.
					interfaceController.getPDFViewer().setVisible(true);
					
					// Begin logging.
					interfaceController.setDoNotLog(false);
				}
			}
		});

		// Setup the close menu item to end the session when a document is closed.
		closeMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event){
				interfaceController.endSession();
			}
		});

		// Setup the thumbs search button.
		thumbsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event){
				// Ensure a document is open.
				if (controller.getDocument() == null){
					JOptionPane.showMessageDialog(null, "A document must be open to perform a search task.");
					return;
				}
				else if (!interfaceController.getCondition().equals("None")){
					JOptionPane.showMessageDialog(null, "You must complete the current search task before performing another.");
					return;
				}

				// Indicate a full save should be done.
				interfaceController.setFullSave(true);

				// Perform the thumbs condition.
				Main.doThumbsCondition();
			}
		});

		// Setup the scroll search button.
		scrollButton.addActionListener(new ActionListener() {
			@Override 
			public void actionPerformed(ActionEvent event){
				// Ensure a document is open.
				if (controller.getDocument() == null){
					JOptionPane.showMessageDialog(null, "A document must be open to perform a search task.");
					return;
				}
				else if (!interfaceController.getCondition().equals("None")){
					JOptionPane.showMessageDialog(null, "You must complete the current search task before performing another.");
					return;
				}

				// Indicate a full save should be done.
				interfaceController.setFullSave(true);

				// Perform the scroll condition.
				Main.doScrollCondition();
			}
		});

		// Setup the thumbs and scroll condition button.
		scrollAndThumbs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				// Ensure a document is open.
				if (controller.getDocument() == null){
					JOptionPane.showMessageDialog(null, "A document must be open to perform a search task.");
					return;
				}
				else if (!interfaceController.getCondition().equals("None")){
					JOptionPane.showMessageDialog(null, "You must complete the current search task before performing another.");
					return;
				}

				// Indicate a full save should be done.
				interfaceController.setFullSave(true);

				// Perform the scroll and thumbs condition.
				Main.doThumbsAndScrollCondition();
			}
		});
		
		// Setup the different pages for which to complete the search tasks. 
		JMenuItem thumbs_condition_0 = new JMenuItem("Thumbs Condition");
		JMenuItem scroll_condition_0 = new JMenuItem("Scroll Condition");
		JMenuItem scroll_and_thumbs_condition_0 = new JMenuItem("Scroll and Thumbs Condition");

		// Setup the ten page doc menu item.
		JMenu ten_page = new JMenu("10 Page Document");
		search_tasks.add(ten_page);
		ten_page.add(thumbs_condition_0);
		ten_page.add(scroll_condition_0);
		ten_page.add(scroll_and_thumbs_condition_0);
		
		// Redefine the menu items.
		JMenuItem thumbs_condition_1 = new JMenuItem("Thumbs Condition");
		JMenuItem scroll_condition_1 = new JMenuItem("Scroll Condition");
		JMenuItem scroll_and_thumbs_condition_1 = new JMenuItem("Scroll and Thumbs Condition");

		// Setup the fifty page doc menu item.
		JMenu fifty_page = new JMenu("50 Page Document");
		search_tasks.add(fifty_page);
		fifty_page.add(thumbs_condition_1);
		fifty_page.add(scroll_condition_1);
		fifty_page.add(scroll_and_thumbs_condition_1);
		
		// Redefine the menu items.
		JMenuItem thumbs_condition_2 = new JMenuItem("Thumbs Condition");
		JMenuItem scroll_condition_2 = new JMenuItem("Scroll Condition");
		JMenuItem scroll_and_thumbs_condition_2 = new JMenuItem("Scroll and Thumbs Condition");


		// Setup the hundred page doc menu item.
		JMenu hundred_page = new JMenu("100 Page Document");
		search_tasks.add(hundred_page);
		hundred_page.add(thumbs_condition_2);
		hundred_page.add(scroll_condition_2);
		hundred_page.add(scroll_and_thumbs_condition_2);

		// Redefine the menu items.
		JMenuItem thumbs_condition_3 = new JMenuItem("Thumbs Condition");
		JMenuItem scroll_condition_3 = new JMenuItem("Scroll Condition");
		JMenuItem scroll_and_thumbs_condition_3 = new JMenuItem("Scroll and Thumbs Condition");
		
		// Setup the two-hundred page doc menu item.
		JMenu twohundred_page = new JMenu("200 Page Document");
		search_tasks.add(twohundred_page);
		twohundred_page.add(thumbs_condition_3);
		twohundred_page.add(scroll_condition_3);
		twohundred_page.add(scroll_and_thumbs_condition_3);
		
		ActionListener menu_listener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// Allow the user to specify and open a file.
				if (controller.getDocument() != null){
					JOptionPane.showMessageDialog(null, "The current document must be closed before a search task can be performed.");
					return;
				}
				else if (!interfaceController.getCondition().equals("None")){
					JOptionPane.showMessageDialog(null, "You must complete the current search task before performing another.");
					return;
				}

				// Determine the menu item and menu that has been selected.
				JMenuItem menu_item = (JMenuItem) event.getSource();
				JPopupMenu popup_menu = (JPopupMenu) menu_item.getParent();
				JMenu menu = (JMenu) popup_menu.getInvoker();

				// Determine which document to open.
				String doc = "", folder = "";
				if (menu.getText().equals("10 Page Document")) {
					folder = "10PageDocuments";
					doc = "10PageDocument";
				}
				else if (menu.getText().equals("50 Page Document")) {
					folder = "50PageDocuments";
					doc = "50PageDocument";
				}
				else if (menu.getText().equals("100 Page Document")) {
					folder = "100PageDocuments";
					doc = "100PageDocument";
				}
				else if (menu.getText().equals("200 Page Document")) {
					folder = "200PageDocuments";
					doc = "200PageDocument";
				}

				// Determine which condition to perform.
				String condition = "";
				if (menu_item.getText().equals("Thumbs Condition")) {
					condition = "Thumbs";
				}
				else if (menu_item.getText().equals("Scroll Condition")) {
					condition = "Scroll";
				}
				else if (menu_item.getText().equals("Scroll and Thumbs Condition")) {
					condition = "ScrollAndThumb";
				}
				
				try {
					interfaceController.generateTaskFromFile(folder, doc, condition);
				} 
				catch (FileNotFoundException e) {
					JOptionPane.showMessageDialog(null, "Could not generate the correct trials. Please report this issue.\n" + e.getMessage() + ".");
					return;
				}
				
				// Open the correct document.
				controller.openDocument(location + File.separator + "docs" + File.separator + folder + File.separator + doc + "_" + condition + ".pdf");
				
				// Setup both to be used casually.
				interfaceController.setDocumentName(doc, false);

				// Indicate the state should not be completely saved. 
				interfaceController.setFullSave(false);
				
				// Perform the correct task. 
				if (condition.equals("Thumbs")){
					Main.doThumbsCondition();
				}
				else if (condition.equals("Scroll")){
					Main.doScrollCondition();
				}
				else if (condition.equals("ScrollAndThumb")){
					Main.doThumbsAndScrollCondition();
				}
			}
		};
		
		// Add the listener to the menu items.
		thumbs_condition_0.addActionListener(menu_listener);
		scroll_condition_0.addActionListener(menu_listener);
		scroll_and_thumbs_condition_0.addActionListener(menu_listener);
		
		thumbs_condition_1.addActionListener(menu_listener);
		scroll_condition_1.addActionListener(menu_listener);
		scroll_and_thumbs_condition_1.addActionListener(menu_listener);
		
		thumbs_condition_2.addActionListener(menu_listener);
		scroll_condition_2.addActionListener(menu_listener);
		scroll_and_thumbs_condition_2.addActionListener(menu_listener);
		
		thumbs_condition_3.addActionListener(menu_listener);
		scroll_condition_3.addActionListener(menu_listener);
		scroll_and_thumbs_condition_3.addActionListener(menu_listener);
		
		// Setup the link to open the website and README in the browser.
		JMenu help_menu = (JMenu) viewerComponentMenu.getComponent(5);
		JMenuItem website_menuitem = new JMenuItem("Go to SFT Study Webpage");
		String website_link = "https://www.cs.usask.ca/faculty/gutwin/sft-hon.php";
		website_menuitem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					Desktop.getDesktop().browse(new URI(website_link));
				} 
				catch (IOException | URISyntaxException e) {
					JOptionPane.showMessageDialog(null, "Something went wrong redirecting you to the study webpage. Please visit " + website_link + " to view the study webpage.\n" + e.getMessage() + ".");
				}
			}
		});
		help_menu.add(website_menuitem);
		
		JMenuItem readme_menuitem = new JMenuItem("Go to README");
		String readme_link = "https://www.dropbox.com/s/ggjh0py7ciq4ts8/README.txt?dl=0";
		readme_menuitem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					Desktop.getDesktop().browse(new URI(readme_link));
				} 
				catch (IOException | URISyntaxException e) {
					JOptionPane.showMessageDialog(null, "Something went wrong redirecting you to the README file. Please visit " + readme_link + " to view the README file.\n" + e.getMessage() + ".");
				}
			}
		});
		help_menu.add(readme_menuitem);
		
		JMenuItem questionnaire = new JMenuItem("Go to the Post-Condition Questionnaire");
		String questionnaire_link = "https://fluidsurveys.usask.ca/s/SFTPost-Condition/";
		questionnaire.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					Desktop.getDesktop().browse(new URI(questionnaire_link));
				} 
				catch (IOException | URISyntaxException e) {
					JOptionPane.showMessageDialog(null, "Something went wrong redirecting you to the post condition qestionnaire. Please visit " + questionnaire_link + " to complete the post condition questionnaire.\n" + e.getMessage() + ".");
				}
			}
		});
		help_menu.add(questionnaire);
		
		// Add a menu item to display the participant's ID.
		id_item = new JMenuItem("Your ID: ");
		help_menu.add(id_item);
		
		// Okay to start?
		boolean start = true;
		
		// Initialize the participant data if necessary.
		participant_file = new File(location + File.separator + "participant" + File.separator + "participant_data.txt");
		if (!participant_file.exists() && !participant_file.isDirectory()){
			ParticipantInfoFrame participantFrame = new ParticipantInfoFrame();
			participantFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			participantFrame.setAlwaysOnTop(true);
			participantFrame.setVisible(true);
			
			// Not okay to start.
			start = false;
		}
		else if (participant_file.exists()){
			try {
				InterfaceController.initializeParticipant();
			} 
			catch (FileNotFoundException e1) {
				JOptionPane.showMessageDialog(null, "There was a problem initializing the participant. Please report this issue.\n" + e1.getMessage() + ".");
			}
			Main.display_id();
		}
		
		// Check if the batch file exists, and if not, write the batch file. 
		batch_file = new File(location + File.separator + "pdfviewer.bat");
		if (!batch_file.exists() && !batch_file.isDirectory()){
			try {
				PrintWriter batch_writer = new PrintWriter(batch_file);
				batch_writer.write("SET path=%~dp0" + System.getProperty("line.separator"));
				batch_writer.write("cd %path%" + System.getProperty("line.separator"));
				batch_writer.write("pdfviewer_V7.jar %1" + System.getProperty("line.separator"));
				batch_writer.write("exit" + System.getProperty("line.separator"));
				batch_writer.close();
			} 
			catch (FileNotFoundException e1) {
				JOptionPane.showMessageDialog(null, "Failed to create batch file. Please report this issue.\n" + e1.getMessage() + ".");
			}
			
			// Not okay to start.
			start = false;
		}
		
		// Open the document if an argument is passed.
		if (args != null && args.length > 0 && start){
			// Allow the user to specify and open a file.
			if (controller.getDocument() != null){
				JOptionPane.showMessageDialog(null, "The current document must be closed before another can be opened.");
				return;
			}
			controller.openDocument(args[0]);

			if (controller.getDocument() != null){
				// Setup both to be used casually.
				interfaceController.setDocumentName(controller.getDocument().getDocumentLocation(), true);
				
				// Indicate there is no condition being tested.
				interfaceController.setCondition("None");
				
				// Setup the SFT interface.
				interfaceController.setupSFTCondition();
				interfaceController.getThumbInterface().setVisible(false);

				// Setup the side thumbnails column.
				interfaceController.setupThumbsCondition();
				interfaceController.getThumbsPane().setVisible(false);
				
				// Set the view.
				controller.getDocumentViewController().setDocumentViewType(DocumentViewControllerImpl.ONE_COLUMN_VIEW, DocumentViewController.PAGE_FIT_WINDOW_HEIGHT);

				// Initialize the logging file.
				try {
					interfaceController.initializeLogFileAndWriter();
				} 
				catch (FileNotFoundException | RuntimeException e) {
					JOptionPane.showMessageDialog(null, "The file to write data could not be initialized. Please report this issue.\n" + e.getMessage() + ".");
				}

				// Set the correct page number upon opening the document.
				interfaceController.setCurrentNumber(1);

				// Begin the timer to collect interface time usage.
				interfaceController.startTime();

				// Increase the page count since the user technically visits the first page.
				interfaceController.pageVisited("PageView");

				// Begin logging.
				interfaceController.setDoNotLog(false);

				// Make the viewer visible.
				interfaceController.getPDFViewer().setVisible(true);
			}
		}
		
		// Only allow the user to see the viewer when appropriate.
		if (start) {
			interfaceController.getPDFViewer().setVisible(true);
		}
	}
}
    