package Frames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import Control.InterfaceController;
import Logging.Participant;
import Logging.Participant.Sex;
import Main.Main;

/**
 * A simple window to collect basics demographic information from the participant.
 * @author nvg081
 */
public class ParticipantInfoFrame extends JFrame{

	/**
	 * Default.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The desired width and height of this frame.
	 */
	private int width = 800, height = 500;
	
	/**
	 * Constructor for the participant frame. 
	 */
	public ParticipantInfoFrame(){
		// Initialize the size and location.
		this.setSize(this.width, this.height);
		this.setLocationRelativeTo(null);
		
		// Setup the overall panel.
		JPanel FramePanel = new JPanel();
		FramePanel.setLayout(new BoxLayout(FramePanel, BoxLayout.Y_AXIS));
		
		FramePanel.add(Box.createVerticalGlue());
		
		// Setup the ID panel.
		JPanel IDpanel = new JPanel();
		IDpanel.setLayout(new BoxLayout(IDpanel, BoxLayout.X_AXIS));
		IDpanel.add(new JLabel("ID: "));
		JTextField IDtext = new JTextField();
		IDtext.setMaximumSize(new Dimension(200, 25));
		IDtext.setEditable(true);
		IDpanel.add(IDtext);
		FramePanel.add(IDpanel);
		
		FramePanel.add(Box.createVerticalGlue());
		
		// Setup the age panel.
		JPanel agePanel = new JPanel();
		agePanel.setLayout(new BoxLayout(agePanel, BoxLayout.X_AXIS));
		agePanel.add(new JLabel("Age: "));
		JTextField age = new JTextField();
		age.setMaximumSize(new Dimension(200, 25));
		agePanel.add(age);
		FramePanel.add(agePanel);
		
		FramePanel.add(Box.createVerticalGlue());
		
		// Setup the sex panel.
		JPanel sexPanel = new JPanel();
		sexPanel.setLayout(new BoxLayout(sexPanel, BoxLayout.X_AXIS));
		ButtonGroup sexGroup = new ButtonGroup();
		JRadioButton male = new JRadioButton("Male");
		JRadioButton female = new JRadioButton("Female");
		sexGroup.add(male);
		sexGroup.add(female);
		sexPanel.add(new JLabel("Sex: "));
		sexPanel.add(male);
		sexPanel.add(female);
		FramePanel.add(sexPanel);
		
		FramePanel.add(Box.createVerticalGlue());
		
		// Setup the studentPanel.
		JPanel studentPanel = new JPanel();
		studentPanel.setLayout(new BoxLayout(studentPanel, BoxLayout.X_AXIS));
		ButtonGroup studentGroup = new ButtonGroup();
		JRadioButton yes = new JRadioButton("Yes");
		JRadioButton no = new JRadioButton("No");
		studentGroup.add(yes);
		studentGroup.add(no);
		studentPanel.add(new JLabel("Are you a student? "));
		studentPanel.add(yes);
		studentPanel.add(no);
		FramePanel.add(studentPanel);
		
		FramePanel.add(Box.createVerticalGlue());
		
		// Setup the hours-using-computer panel.
		JPanel computerUsagePanel = new JPanel();
		computerUsagePanel.setLayout(new BoxLayout(computerUsagePanel, BoxLayout.X_AXIS));
		computerUsagePanel.add(new JLabel("How many hours/day do you use a computer? "));
		JTextField hours = new JTextField();
		hours.setMaximumSize(new Dimension(200, 25));
		computerUsagePanel.add(hours);
		FramePanel.add(computerUsagePanel);
		
		FramePanel.add(Box.createVerticalGlue());
		
		// Setup the hours-using-documents panel.
		JPanel documentUsagePanel = new JPanel();
		documentUsagePanel.setLayout(new BoxLayout(documentUsagePanel, BoxLayout.X_AXIS));
		documentUsagePanel.add(new JLabel("How many hours/day do you spend reading documents on a computer? "));
		JTextField usage = new JTextField();
		usage.setMaximumSize(new Dimension(200, 25));
		documentUsagePanel.add(usage);
		FramePanel.add(documentUsagePanel);
		
		FramePanel.add(Box.createVerticalGlue());
		
		// Setup the Submit button.
		JButton submit = new JButton("Submit");
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		submit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Check if any required fields are not filled and give appropriate message.
				if (IDtext.getText() == null || IDtext.getText().equals("")){
					JOptionPane.showMessageDialog(null, "All fields are required.");
				}
				else if (age.getText() == null || age.getText().equals("")){
					JOptionPane.showMessageDialog(null, "All fields are required.");
				}
				else if (!male.isSelected() && !female.isSelected()){
					JOptionPane.showMessageDialog(null, "All fields are required.");
				}
				else if (!yes.isSelected() && !no.isSelected()){
					JOptionPane.showMessageDialog(null, "All fields are required.");
				}
				else if (hours.getText() == null || hours.getText().equals("")){
					JOptionPane.showMessageDialog(null, "All fields are required.");
				}
				else if (usage.getText() == null || usage.getText().equals("")){
					JOptionPane.showMessageDialog(null, "All fields are required.");
				}
				else {
					int ageResult = 0;
					try{
						ageResult = Integer.parseInt(age.getText());
					}
					catch (NumberFormatException exception){
						JOptionPane.showMessageDialog(null, "Only enter an integer in the age field.");
						return;
					}
					
					// Determine the sex of the participant.
					Sex pSex = null;
					if (male.isSelected()){
						pSex = Participant.Sex.MALE;
					}
					else if (female.isSelected()){
						pSex = Participant.Sex.FEMALE;
					}
					
					// Determine the ID of the participant.
					int IDNum = 0;
					try{
						IDNum = Integer.parseInt(IDtext.getText());
					}
					catch (NumberFormatException exception){
						JOptionPane.showMessageDialog(null, "Only enter an integer in the ID field.");
						return;
					}
					
					//Determine if the participant is a student.
					boolean isStudent = false;
					if (yes.isSelected()){
						isStudent = true;
					}
					
					// Determine the number of hours the participant uses a computer a week.
					int hoursPerWeek = 0;
					try{
						hoursPerWeek = Integer.parseInt(hours.getText());
					}
					catch (NumberFormatException exception){
						JOptionPane.showMessageDialog(null, "Only enter an integer for the number of hours you use a computer.");
						return;
					}
					
					// Determine the number of hours per week the participant spends reading documents on a computer.
					int usagePerWeek = 0;
					try{
						usagePerWeek = Integer.parseInt(usage.getText());
					}
					catch (NumberFormatException exception){
						JOptionPane.showMessageDialog(null, "Only enter an integer for the number of hours you spend reading documents.");
						return;
					}
					
					// Try creating the participant file with the entered information.
					PrintWriter writer = null;
					try {
						writer = new PrintWriter(new File(Main.location + File.separator + "participant" + File.separator + "participant_data.txt"));
					} 
					catch (FileNotFoundException e) {
						JOptionPane.showMessageDialog(null, "The participant data file could not be created.\n" + e.getMessage() + ".\nThe location: " + Main.location);
						return;
					}
					
					//Write the participant file.
					writer.println(IDNum);
					writer.println(ageResult);
					writer.println(pSex);
					writer.println(isStudent);
					writer.println(hoursPerWeek);
					writer.println(usagePerWeek);
					writer.close();
					
					// Dispose of the participant info frame.
					dispose();
					
					// Initialize the participant.
					try {
						InterfaceController.initializeParticipant();
					} 
					catch (FileNotFoundException e) {
						JOptionPane.showMessageDialog(null, "There was a problem initializing the participant data. Please report this issue.\n" + e.getMessage() + ".");
					}
					
					// Display the correct pariticipant id.
					Main.display_id();
				}
			}
		});
		submit.setMaximumSize(new Dimension(100, 30));
		buttonPanel.add(submit);
		buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		FramePanel.add(buttonPanel);
		
		FramePanel.add(Box.createVerticalGlue());
		
		// Setup the frame.
		this.setLayout(new BorderLayout());
		this.add(FramePanel, BorderLayout.CENTER);
		FramePanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
	}
}
