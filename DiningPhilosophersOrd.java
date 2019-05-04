/**
 * Class DiningPhilosophers
 * The main starter.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */

import java.util.Scanner;
public class DiningPhilosophersOrd
{
	/*
	 * ------------
	 * Data members
	 * ------------
	 */

	/**
	 * This default may be overridden from the command line
	 */
	public static final int DEFAULT_NUMBER_OF_PHILOSOPHERS = 4;

	/**
	 * Dining "iterations" per philosopher thread
	 * while they are socializing there
	 */
	public static final int DINING_STEPS = 1;

	/**
	 * Our shared monitor for the philosphers to consult
	 */
	public static Monitor soMonitor = null;

	/*
	 * -------
	 * Methods
	 * -------
	 */
	

	/**
	 * Main system starts up right here
	 */
	public static void main(String[] argv)
	{
		try
		{
			System.out.println("Task 3 and Task 4:");
			System.out.println("please enter an positive integer as the number of philosophers:");
			Scanner scan = new Scanner(System.in);
			
			/*
			 * TODO:
			 * Should be settable from the command line
			 * or the default if no arguments supplied.
			 */
			Object obj = scan.next();
			String objStr = (String) obj;
			if(objStr.matches("[A-Za-z]+[0-9]*||[0-9]*[A-Za-z]+")) {
				System.out.println("character detected, only integer is allowed.");
				scan.close();
				System.exit(1);
			}
			else if(Integer.parseInt(objStr)<=1) {
				System.out.println("the number of philosopher must be bigger than 1.");
				scan.close();
				System.exit(1);
			}
			else {
				int iPhilosophers = Integer.parseInt(objStr);
				if(iPhilosophers<0) {
					reportBadInput(iPhilosophers);
					scan.close();
					System.exit(1);
				}
				else if(iPhilosophers==0){
					iPhilosophers = DEFAULT_NUMBER_OF_PHILOSOPHERS;
				}
				
				
				scan.close();
				// Make the monitor aware of how many philosophers there are
				System.out.println(iPhilosophers);
				soMonitor = new Monitor(iPhilosophers);

				// Space for all the philosophers
				
				
				System.out.println
				(
					"Task 3:\n" + iPhilosophers +
					" philosopher(s) came in for a dinner."
				);
				OrderedPhilosopher OrderedaoPhilosophers[] = new OrderedPhilosopher[iPhilosophers];

				// Let 'em sit down
				for(int j = 0; j < iPhilosophers; j++)
				{
					OrderedaoPhilosophers[j] = new OrderedPhilosopher(j);
					OrderedaoPhilosophers[j].setPriority(j+1);
					OrderedaoPhilosophers[j].start();
				}

				

				// Main waits for all its children to die...
				// I mean, philosophers to finish their dinner.
				for(int j = 0; j < iPhilosophers; j++)
					OrderedaoPhilosophers[j].join();

				System.out.println("All philosophers have left. System terminates normally.");
			}
		}
		
		
		catch(InterruptedException e)
		{
			System.err.println("main():");
			reportException(e);
			System.exit(1);
		}
	} // main()

	/**
	 * Outputs exception information to STDERR
	 * @param poException Exception object to dump to STDERR
	 */
	public static void reportException(Exception poException)
	{
		System.err.println("Caught exception : " + poException.getClass().getName());
		System.err.println("Message          : " + poException.getMessage());
		System.err.println("Stack Trace      : ");
		poException.printStackTrace(System.err);
	}
	
	public static void reportBadInput(int badInt) {
		System.out.println("% java DiningPhilosophers "+ badInt +"\n" + 
				"\"" + badInt + "\" is not a positive decimal integer\n" + 
				"Usage: java DiningPhilosophers [NUMBER_OF_PHILOSOPHERS]\n" + 
				"%");
	}
}

// EOF
