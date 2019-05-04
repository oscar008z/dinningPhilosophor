import java.util.Random;
import java.util.ArrayList;
import common.BaseThread;

/**
 * Class Philosopher.
 * Outlines main subrutines of our virtual philosopher.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class DynamicPhilosopher extends BaseThread
{
	ArrayList<DynamicPhilosopher> threadsArrlist; 
	DynamicPhilosopher(int pTID, ArrayList<DynamicPhilosopher> threadsArrlist){
		super(pTID);
		this.threadsArrlist = threadsArrlist;
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
		if(DiningPhilosophersDynamic.soMonitor.getIfLeave().get(this.getTID())==0) {
			DiningPhilosophersDynamic.soMonitor.pickUpDynamic(this.getTID());
			System.out.println("The philosopher with ID: " + this.getTID() + " has started eating.");
			
			
			//DiningPhilosophersDynamic.soMonitor.putDownDynamic(this.getTID());
			//System.out.println("The philosopher with ID: " + this.getTID() + " has ended eating.");
			
			
			boolean flag = true;
			if(Math.random() < 0.5) {
				flag = false;
			}
			if(flag==false) {
				leave();
			}
			else {
				DiningPhilosophersDynamic.soMonitor.putDownDynamic(this.getTID());
				System.out.println("The philosopher with ID: " + this.getTID() + " has ended eating.");
			}
		};	
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
		if(DiningPhilosophersDynamic.soMonitor.getIfLeave().get(this.getTID())==0) {
			System.out.println("The philosopher with ID: " + this.getTID() + " is thinking.");
			
			boolean flag = true;
			if(Math.random() < 0.5) {
				flag = false;
			}
			if(flag==false) {
				leave();
			}
		}
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
		if(DiningPhilosophersDynamic.soMonitor.getIfLeave().get(this.getTID())==0) {
			System.out.println("The philosopher with ID: " + this.getTID() + " request talking.");
			
			DiningPhilosophersDynamic.soMonitor.requestTalkDynamic(this.getTID());
			saySomething();
			
			//DiningPhilosophersDynamic.soMonitor.endTalkDynamic(this.getTID());
			//System.out.println("The philosopher with ID: " + this.getTID() + " has ended eating.");
			boolean flag = true;
			if(Math.random() < 0.5) {
				flag = false;
			}
			if(flag==false) {
				leave();
			}
			else {
				DiningPhilosophersDynamic.soMonitor.endTalkDynamic(this.getTID());
				System.out.println("The philosopher with ID: " + this.getTID() + " has ended eating.");
			}
		}
	}
	
	public void sleep(){
		if(DiningPhilosophersDynamic.soMonitor.getIfLeave().get(this.getTID())==0) {
			System.out.println("The philosopher with ID: " + this.getTID() + " request sleeping.");
			DiningPhilosophersDynamic.soMonitor.requestSleepDynamic(this.getTID());
			try
			{
				System.out.println("The philosopher with ID: " + this.getTID() + " is about to sleep for some time.");
				sleep((long)(Math.random() * TIME_TO_WASTE));
			}
			catch(InterruptedException e)
			{
				System.err.println("Philosopher.eat():");
				DiningPhilosophersDynamic.reportException(e);
				System.exit(1);
			}
			DiningPhilosophersDynamic.soMonitor.endSleepDynamic(this.getTID());
			System.out.println("The philosopher with ID: " + this.getTID() + " wakes up.");
		}
	}
	
	public void leave(){
		System.out.println("The philosopher with ID: " + this.getTID() + " wants to leave.");
		DiningPhilosophersDynamic.soMonitor.leavingDynamic(this.getTID());
		System.out.println("The philosopher with ID: " + this.getTID() + " has left the table.");
	}
	
	public synchronized void newJoin() {
		try {
			Random rm = new Random();
			int min = 1;
			int max = 5;
			int numNewPhilosophers = rm.nextInt((max - min) + 1) + min;
			
			
			System.out.println("There are " + numNewPhilosophers + " more philosophers want to join.");
			int oldUpperBoundary = DiningPhilosophersDynamic.soMonitor.getNumberOfPhilosophers();
			int newUpperBoundary = oldUpperBoundary;
			for(int j=0 ; j<numNewPhilosophers; j++) {	
				this.threadsArrlist.add(newUpperBoundary, new DynamicPhilosopher(newUpperBoundary, this.threadsArrlist));
				DiningPhilosophersDynamic.soMonitor.joinningDynamic(this.threadsArrlist.size()-1);
				this.threadsArrlist.get(newUpperBoundary).start();
				newUpperBoundary++;
			}
			
			for(int j=oldUpperBoundary ; j<newUpperBoundary; j++) {
				System.out.println("new philosopher with ID " + (this.threadsArrlist.get(j).getTID()) + " has come to the table");
				this.threadsArrlist.get(j).join();
			}
		}
		catch(InterruptedException e)
		{
			System.err.println("newJoin():");
			DiningPhilosophersDynamic.reportException(e);
			System.exit(1);
		}
	}

	/**
	 * No, this is not the act of running, just the overridden Thread.run()
	 */
	public void run()
	{
		
		for(int i = 0; i < DiningPhilosophersDynamic.DINING_STEPS; i++)
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
			if(Math.random() < 0.3) {
				flag = false;
			}
			if(flag)
			{
				talk();
				// ...
			}

			sleep();
			//System.out.println(this.iTID + " is about to terminate.");
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
