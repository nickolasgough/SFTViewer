package Logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Simple class to model a task a participant is meant to complete.
 * @author nvg081
 */
public class Task {

	/**
	 * Record all the incomplete trials. 
	 */
	private ArrayList<Trial> incomplete_trials;

	/**
	 * The current trial number.
	 */
	private Trial currentTrial;
	
	/**
	 * Record all the complete trials.
	 */
	private ArrayList<Trial> complete_trials;
	
	/**
	 * The count at which the task is to end.
	 */
	private int end_count;
	
	/**
	 * Constructor for the task.
	 * @param numTrials - The number of trials to be completed.
	 */
	public Task(){
		this.incomplete_trials = new ArrayList<Trial>();
		this.complete_trials = new ArrayList<Trial>();
		this.end_count = 0;
	}

	/**
	 * Determines if the participant has reached the end of the task.
	 * @return - True if all trials have been completed, false otherwise.
	 */
	public boolean isOver(){
		return this.end_count == this.complete_trials.size();
	}

	/**
	 * Starts the next trial.
	 * @precond - The participant cannot have already reached the end of the task.
	 * @throws RuntimeException when the task is over.
	 */
	public void startNextTrial() throws RuntimeException{
		if (this.isOver()){
			throw new RuntimeException("Cannot start next trial; task is over.");
		}
		
		this.currentTrial.trialStart();
	}
	
	/**
	 * Retrieves the page number associated with the next trial.
	 * @return - The page number associated with the next trial.
	 * @throws RuntimeExcpetion when the task is over.
	 */
	public int getNextPageNum() throws RuntimeException{
		if (this.isOver()){
			throw new RuntimeException("Cannot get next page number; task is over.");
		}
		
		return this.currentTrial.getPageNum()-1;
	}
	
	/**
	 * Accessor to retrieve the next start page. 
	 * @return - The start page of the current trial.
	 */
	public int getNextStartPage(){
		if (this.isOver()){
			throw new RuntimeException("Cannot get next start page; task is over.");
		}
		
		return this.currentTrial.getStartPage()-1;
	}
	
	/**
	 * Ends the current trial.
	 * @throws RuntimeException when the task is over.
	 */
	public void endCurrentTrial() throws RuntimeException{
		if (this.isOver()){
			throw new RuntimeException("Cannot end the current trial; the task is over.");
		}
		
		this.currentTrial.trialEnd();
	}
	
	/**
	 * Increase the trial number to move on to the next trial.
	 */
	public void increaseTrialNumber(){
		// Add the current trial to the list of completed trials.
		if (this.currentTrial != null){
			this.complete_trials.add(this.currentTrial);
		}
		
		// Generate a random index.
		int rand_ind = (int) Math.floor(Math.random()*this.incomplete_trials.size());
		
		// Set the current trial to be the next trial.
		if (!this.isOver()){
			this.currentTrial = this.incomplete_trials.remove(rand_ind);
		}
		else {
			this.currentTrial = null;
		}
		
	}
	
	/**
	 * Accessor to access to the current running trial.
	 * @precond - !isOver
	 * @return - The current running trial.
	 * @throws RuntimeException when the task is not over. 
	 */
	public Trial getCurrentTrial() throws RuntimeException{
		if (this.isOver()){
			throw new RuntimeException("Cannot get current trial; task is over.");
		}
		
		return this.currentTrial;
	}
	
	/**
	 * Accessor to retrieve the current trial number.
	 * @return - The current trial number.
	 * @throws RuntimeException when the task is over.
	 */
	public int getCurrentTrialNumber() throws RuntimeException{
		if (this.isOver()){
			throw new RuntimeException("Cannot get trial number; the task is over.");
		}
		
		return this.complete_trials.size()+1;
	}
	
	/**
	 * Retrieves the current total time of the task.
	 * @return - The current total time of the task.
	 */
	public long getCurrentTotalTime(){
		long result = 0;
		for (Trial curr_trial : this.complete_trials){
			result += curr_trial.getTrialTime();
		}
		result += this.currentTrial.getCurrentTrialTime();
		
		return result;
	}
	
	/**
	 * Determines the time for the task to be completed, not including the delays.
	 * @return - The time to complete the task.
	 */
	public long getTotalTime(){
		long time = 0;
		for(Trial curr_trial : this.complete_trials){
			time += curr_trial.getTrialTime();
		}
		
		return time;
	}
	
	/**
	 * Generates the trials for the task.
	 * @param doc - A string representation of the doc to use.
	 * @throws FileNotFoundException when the file does not exist.
	 */
	public void generateDocTrials(String folder, String doc, String condition) throws FileNotFoundException{
		// Find the file and setup input.
		File doc_file = new File(Main.Main.location + File.separator + "docs" + File.separator + folder + File.separator + "." + doc + "_" + condition);
		Scanner in = new Scanner(doc_file);
		
		// Generate the tasks from the input.
		while (in.hasNext()) {
			int start = in.nextInt();
			int end = in.nextInt();
			String dist = in.next();
			String diff = in.next(); 
			this.incomplete_trials.add(new Trial(start, end, dist, diff));
		}
		
		// Close the file IO.
		in.close();
		
		// Set the end count.
		this.end_count = this.incomplete_trials.size();
		
		// Begin with the first trial.
		this.increaseTrialNumber();
	}
	
	/**
	 * Generates the trials from the random entries. 
	 * @param rand_starts - The starts that correspond with each target.
	 * @param rand_targets - The target with a corresponding start.
	 */
	public void generateRandomTrials(int[] rand_starts, int[] rand_targets){
		// Ensure there are enough targets and starts. 
		if (rand_targets.length != rand_starts.length) {
			throw new RuntimeException("Cannot generate random trials; there must be as many targets as there are starts.");
		}
		
		// Create the trials. 
		for (int n = 0; n < rand_targets.length; n += 1){
			this.incomplete_trials.add(new Trial(rand_starts[n], rand_targets[n], "NA", "NA"));
			this.incomplete_trials.add(new Trial(rand_starts[n], rand_targets[n], "NA", "NA"));
		}
		
		// Set the end count.
		this.end_count = this.incomplete_trials.size();
		
		// Begin with the first trial.
		this.increaseTrialNumber();
	}
}
