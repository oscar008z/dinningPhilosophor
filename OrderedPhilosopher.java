import common.BaseThread;

/**
 * Class Philosopher.
 * Outlines main subrutines of our virtual philosopher.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class OrderedPhilosopher extends BaseThread
{
	OrderedPhilosopher(int pTID){
		super(pTID);
	}
	/**
	 * Max time an action can take (in milliseconds)
	 */
	public static final long TIME_TO_WASTE = 1000;

	/**
	 * The act of eating.
	 * - Print the fact that a given phil (their TID) has started eating.
	 * - yield
	 * - Then sleep() for a random interval.
	 * - yield
	 * - The print that they are done eating.
	 */
	
	
	public void eatOrd()
	{
		/*
		 * use monitor member of this thread object to invoke pickUp and putDown methods	task 1
		 */
		//System.out.print("the piTID now is " + this.getTID());
		DiningPhilosophersOrd.soMonitor.reState();
		DiningPhilosophersOrd.soMonitor.pickUpOrd(this.getTID(), this.getPriority());
		System.out.println("The philosopher with ID: " + this.getTID() + " has started eating.");
		DiningPhilosophersOrd.soMonitor.putDownOrd(this.getTID());
		System.out.println("The philosopher with ID: " + this.getTID() + " has ended eating.");
	}
	
	
	/**
	 * No, this is not the act of running, just the overridden Thread.run()
	 */
	public void run()
	{
		for(int i = 0; i < DiningPhilosophersOrd.DINING_STEPS; i++)
		{
			eatOrd();
		}
	} // run()

}

// EOF
