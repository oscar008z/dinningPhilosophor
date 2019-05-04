import common.BaseThread;

/**
 * Class Philosopher.
 * Outlines main subrutines of our virtual philosopher.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Philosopher extends BaseThread
{
	boolean flag = true;
	Philosopher(int pTID){
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
	public void eat()
	{
		/*
		 * use monitor member of this thread object to invoke pickUp and putDown methods	task 1
		 */
		//System.out.print("the piTID now is " + this.getTID());
		DiningPhilosophers.soMonitor.pickUp(this.getTID());
		System.out.println("The philosopher with ID: " + this.getTID() + " has started eating.");
		boolean flag = true;
		if(Math.random() < 0.5) {
			flag = false;
		}
		if(flag)
		{
			DiningPhilosophers.soMonitor.requestPepperShaker(this.getTID());
			System.out.println("Philosopher with ID " + this.getTID() + " has pick up a pepper shaker.");
			DiningPhilosophers.soMonitor.putDownPepperShaker(this.getTID());
			System.out.println("Philosopher with ID " + this.getTID() + " has put down a pepper shaker.");
		}
		DiningPhilosophers.soMonitor.putDown(this.getTID());
		System.out.println("The philosopher with ID: " + this.getTID() + " has ended eating.");
	}
	
	

	/**
	 * The act of thinking.
	 * - Print the fact that a given phil (their TID) has started thinking.
	 * - yield
	 * - Then sleep() for a random interval.
	 * - yield
	 * - The print that they are done thinking.
	 */
	public void think()
	{
		/*
		 * use monitor member of this thread object to invoke putDown methods	task 1
		 */
		System.out.println("The philosopher with ID: " + this.getTID() + " is thinking.");
		
	}

	/**
	 * The act of talking.
	 * - Print the fact that a given phil (their TID) has started talking.
	 * - yield
	 * - Say something brilliant at random
	 * - yield
	 * - The print that they are done talking.
	 */
	public void talk()
	{
		/*
		 * use monitor member of this thread object to invoke requestTalk and endTalk methods	task 1
		 */
		System.out.println("The philosopher with ID: " + this.getTID() + " request talking.");
		DiningPhilosophers.soMonitor.requestTalk(this.getTID());
		saySomething();
		DiningPhilosophers.soMonitor.endTalk(this.getTID());
		System.out.println("The philosopher with ID: " + this.getTID() + " ended talking.");
	}
	
	public void sleep(){
		System.out.println("The philosopher with ID: " + this.getTID() + " request sleeping.");
		DiningPhilosophers.soMonitor.requestSleep(this.getTID());
		try
		{
			System.out.println("The philosopher with ID: " + this.getTID() + " is about to sleep for some time.");
			sleep((long)(Math.random() * TIME_TO_WASTE));
		}
		catch(InterruptedException e)
		{
			System.err.println("Philosopher.eat():");
			DiningPhilosophers.reportException(e);
			System.exit(1);
		}
		DiningPhilosophers.soMonitor.endSleep(this.getTID());
		System.out.println("The philosopher with ID: " + this.getTID() + " wakes up.");
	}

	/**
	 * No, this is not the act of running, just the overridden Thread.run()
	 */
	public void run()
	{
		
		for(int i = 0; i < DiningPhilosophers.DINING_STEPS; i++)
		{
			//DiningPhilosophers.soMonitor.pickUp(getTID());
			eat();
			//DiningPhilosophers.soMonitor.putDown(getTID());
			think();
			/*
			 * TODO:
			 * A decision is made at random whether this particular
			 * philosopher is about to say something terribly useful.
			 */
			boolean flag = true;
			if(Math.random() < 0.5) {
				flag = false;
			}
			if(flag)
			{
				talk();
				// ...
			}
			sleep();
		}
		
	} // run()

	/**
	 * Prints out a phrase from the array of phrases at random.
	 * Feel free to add your own phrases.
	 */
	public void saySomething()
	{
		String[] astrPhrases =
		{
			"Eh, it's not easy to be a philosopher: eat, think, talk, eat...",
			"You know, true is false and false is true if you think of it",
			"2 + 2 = 5 for extremely large values of 2...",
			"If thee cannot speak, thee must be silent",
			"My number is " + getTID() + ""
		};

		System.out.println
		(
			"Philosopher " + getTID() + " says: " +
			astrPhrases[(int)(Math.random() * astrPhrases.length)]
		);
	}
	
	
}

// EOF
