package Logging;

/**
 * A simple class to represent a trial.
 * @author nvg081
 */
public class Trial {
	
	/**
	 * The page number to display.
	 */
	private int target_page;
	
	/**
	 * The page to start the trial.
	 */
	private int start_page;
	
	/**
	 * The beginning and ending of the trial in milliseconds.
	 */
	private long beginning, ending;
	
	/**
	 * Log the start distance of the task.
	 */
	private String start_dist;
	
	/**
	 * The difficulty of the task.
	 */
	private String difficulty;
	
	/**
	 * Constructor for the trial.
	 * @param trialNum - The trial number.
	 * @param pageNum - The page number of the page that will have to be displayed during this trial.
	 */
	public Trial(int start_page, int target_page, String start_dist, String difficulty){
		this.target_page = target_page;
		this.start_page = start_page;
		this.start_dist = start_dist;
		this.difficulty = difficulty;
	}
	
	/**
	 * Method to record the trial start time.
	 */
	public void trialStart(){
		// Record the start time for the trial.
		this.beginning = System.currentTimeMillis();
	}
	
	/**
	 * Method to record the trial end time.
	 * @throws RuntimeException if the trial has not started.
	 */
	public void trialEnd() throws RuntimeException{
		// Cannot end a trial that has not begun.
		if (this.beginning == 0){
			throw new RuntimeException("The trial did not start.");
		}
		
		// Record the ending date and time.
		this.ending = System.currentTimeMillis();
	}
	
	/**
	 * Retrieves the page number associated with this trial.
	 * @return - This trial's page number.
	 */
	public int getPageNum(){
		return this.target_page;
	}
	
	/**
	 * Accessor to return the start page of the trial.
	 * @return - The start page of the trial.
	 */
	public int getStartPage() {
		return this.start_page;
	}
	
	/**
	 * Determines the total time of the trial.
	 * @return - The time to complete the trial.
	 */
	public long getTrialTime(){
		return (this.ending-this.beginning);
	}
	
	/**
	 * Determines the time the current trial has been running for.
	 * @return - The time the current trial has been running.
	 */
	public long getCurrentTrialTime(){
		return (System.currentTimeMillis()-this.beginning);
	}
	
	/**
	 * Retrieve the difficulty of the trial.
	 * @return - The trial difficulty.
	 */
	public String getStartDist(){
		return this.start_dist;
	}
	
	/**
	 * Retrieve the difficulty of the trial.
	 * @return - The trial difficulty.
	 */
	public String getDifficulty(){
		return this.difficulty;
	}
}
