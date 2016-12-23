package Logging;

public class Participant {

	/**
	 * Enumeration to represent a participant's sex.
	 * @author nvg081
	 */
	public static enum Sex{
		MALE, FEMALE
	}
	
	/**
	 * The participant ID.
	 */
	private int ID;
	
	/**
	 * The participant age.
	 */
	private int age;
	
	/**
	 * The participant sex.
	 */
	private Sex sex;
	
	/**
	 * Is the participant a student?
	 */
	private boolean isStudent;
	
	/**
	 * The number of hours per week the participant uses a computer.
	 */
	int hoursPerDay;
	
	/**
	 * The number of hours per week the participant spends reading documents on a computer. 
	 */
	int usagePerDay;
	
	/**
	 * Constructor to instantiate this instance of participant.
	 * @param ID - The participant's ID.
	 * @param name - The participant's name.
	 * @param age - The participant's age.
	 * @param sex - The participant's sex.
	 * @param isStudent - True if the participant is a student.
	 * @param hoursPerWeek - The number of hours per week the participant uses a computer.
	 * @param usagePerWeek - The number of hours per week the participant views documents on their computer.
	 */
	public Participant(int ID, int age, Sex sex, boolean isStudent, int hoursPerWeek, int usagePerWeek){
		this.ID = ID;
		this.age = age;
		this.sex = sex;
		this.isStudent = isStudent;
		this.hoursPerDay = hoursPerWeek;
		this.usagePerDay = usagePerWeek;
	}
	
	/**
	 * Accessor to retrieve the ID of the participant.
	 * @return - The ID of the participant.
	 */
	public int getID(){
		return this.ID;
	}
	
	/**
	 * Accessor to retrieve the age of the participant.
	 * @return - The age of the participant.
	 */
	public int getAge(){
		return this.age;
	}
	
	/**
	 * Accessor to indicate the participant is a student.
	 * @return - True if the student is a student, false otherwise.
	 */
	public boolean isStudent(){
		return this.isStudent;
	}
	
	/**
	 * Accessor to retrieve the sex of the participant.
	 * @return - The sex of the participant.
	 */
	public Sex getSex(){
		return this.sex;
	}
	
	/**
	 * Accessor to retrieve the number of hours per week the participant uses a computer.
	 * @return - The number of hours per week the participant uses a computer. 
	 */
	public int getHoursComp(){
		return this.hoursPerDay;
	}
	
	/**
	 * Accessor to retrieve the hours per week the participant views documents on their computer. 
	 * @return - The number of hours per week the participant views documents on a computer.
	 */
	public int getHoursDocs(){
		return this.usagePerDay;
	}
	
	@Override
	/**
	 * A toString() method to return a String representation of the participant.
	 */
	public String toString(){
		return this.ID + "\t" + this.age + "\t" + this.sex + "\t" + this.isStudent + "\t" + this.hoursPerDay + "\t" + this.usagePerDay; 
	}
}
