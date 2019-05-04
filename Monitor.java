import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.ArrayList;

/**
 * Class Monitor
 * To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Monitor
{
	/*
	 * ------------
	 * Data members
	 * ------------
	 */
	public enum state {THINKING, EATING, HUNGRY, TALKWAITING, TALKING, SLEEPWAITING, SLEEPING, LEAVING, WAITINGJOIN};
	private Lock lock;
	private state [] mState;
	private Condition [] self;
	private Condition selfOrd;
	private ArrayList<state> mdState;
	private ArrayList<Condition> selfMd;
	private ArrayList<Integer> ifLeave;
	private int numberOfPhilosophers;
	private int shakerCount;
	private Condition shakerCondition;

	/**
	 * Constructor
	 */
	public Monitor(int piNumberOfPhilosophers)
	{
		// TODO: set appropriate number of chopsticks based on the # of philosophers
		this.lock = new ReentrantLock();
		this.numberOfPhilosophers = piNumberOfPhilosophers;
		this.mState = new state[piNumberOfPhilosophers];
		this.self = new Condition[piNumberOfPhilosophers];
		this.mdState = new ArrayList<state>(piNumberOfPhilosophers);
		this.selfMd = new ArrayList<Condition>(piNumberOfPhilosophers);
		this.ifLeave = new ArrayList<Integer>(piNumberOfPhilosophers);
		for(int i = 0; i<piNumberOfPhilosophers; i++) {
			mState[i] = state.THINKING;
			self[i] = lock.newCondition();
			mdState.add(i, state.THINKING);
			selfMd.add(i, lock.newCondition());
			ifLeave.add(i, 0);
		}
		this.selfOrd = lock.newCondition();
		shakerCount = 2;
		shakerCondition = lock.newCondition();
	}
	
	

	public ArrayList<state> getMdState() {
		return mdState;
	}

	public ArrayList<Condition> getSelfMd() {
		return selfMd;
	}

	public int getNumberOfPhilosophers() {
		return numberOfPhilosophers;
	}
	public ArrayList<Integer> getIfLeave() {
		return ifLeave;
	}
	/*
	 * -------------------------------
	 * User-defined monitor procedures
	 * -------------------------------
	 */
	//TO BE CONTINUE
	/**
	 * Grants request (returns) to eat when both chopsticks/forks are available.
	 * Else forces the philosopher to wait()
	 */
	public void pickUp(final int piTID)
	{
		lock.lock();
		try {
			mState[piTID] = state.HUNGRY;
			testEat(piTID);
			if(mState[piTID]!=state.EATING) {
				self[piTID].await();
			}
		}
		catch(InterruptedException e) {
			System.err.println("Monitor.pickUp(final int piTID):");
			DiningPhilosophers.reportException(e);
			System.exit(1);
		}
		finally {
			lock.unlock();
		}
	}
	
	public void pickUpOrd(final int piTID, int priority)
	{
		lock.lock();
		
		try {
			mState[piTID] = state.HUNGRY;
			testEat(piTID);
			if(mState[piTID]!=state.EATING) {
				selfOrd.await();
			}
			System.out.println("The priority: " + priority);
		}
		catch(InterruptedException e) {
			System.err.println("Monitor.pickUp(final int piTID):");
			DiningPhilosophers.reportException(e);
			System.exit(1);
		}
		finally {
			lock.unlock();
		}
	}
	

	public void pickUpDynamic(final int piTID)
	{
		
		lock.lock();
		try {
			mdState.set(piTID, state.HUNGRY);
			
			testEatDynamic(piTID);
			if(mdState.get(piTID)!=state.EATING) {
				selfMd.get(piTID).await();
			}
			
		}
		catch(InterruptedException e) {
			System.err.println("Monitor.pickUpDynamic(final int piTID):");
			DiningPhilosophers.reportException(e);
			System.exit(1);
		}
		finally {
			lock.unlock();
		}
	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down
	 * and let others know they are available.
	 */
	public void putDown(final int piTID)
	{
		lock.lock();
		try {
			mState[piTID] = state.THINKING;
			int nextTID = (piTID + 1) % numberOfPhilosophers;
			int previousTID = (piTID + (numberOfPhilosophers-1)) % numberOfPhilosophers;
			testEat(nextTID);
			if(mState[nextTID] == state.EATING)
				self[nextTID].signal();
			testEat(previousTID);
			if(mState[previousTID]==state.EATING)
				self[previousTID].signal();
		}
		
		finally {
			lock.unlock();
		}
	}
	
	public void putDownOrd(final int piTID)
	{
		lock.lock();
		try {
			mState[piTID] = state.THINKING;
			int nextTID = (piTID + 1) % numberOfPhilosophers;
			int previousTID = (piTID + (numberOfPhilosophers-1)) % numberOfPhilosophers;
			testEat(nextTID);
			if(mState[nextTID] == state.EATING)
				selfOrd.signal();
			testEat(previousTID);
			if(mState[previousTID]==state.EATING)
				selfOrd.signal();
		}
		
		finally {
			lock.unlock();
		}
	}

	public void putDownDynamic(final int piTID)
	{
		lock.lock();
		try {
			boolean flagNext = true;
			boolean flagPrevious = true;
			mdState.set(piTID, state.THINKING);
			int nextTID = (piTID + 1) % numberOfPhilosophers;
			int previousTID = (piTID + (numberOfPhilosophers-1)) % numberOfPhilosophers;
			while(flagNext) {
				testEatDynamic(nextTID);
				testJoinDynamic(nextTID);
				if(mdState.get(nextTID) == state.EATING) {
					selfMd.get(nextTID).signal();
					flagNext=false;
				}
				else if(mdState.get(nextTID) == state.THINKING && piTID!=nextTID) {
					selfMd.get(nextTID).signal();
					nextTID = (nextTID + 1) % numberOfPhilosophers;
				}
				else if(piTID == nextTID) {
					selfMd.get(nextTID).signal();
					flagNext=false;
				}
				else {
					nextTID = (nextTID + 1) % numberOfPhilosophers;
				}
			}
			while(flagPrevious) {
				testEatDynamic(previousTID);
				testJoinDynamic(previousTID);
				if(mdState.get(previousTID) == state.EATING) {
					selfMd.get(previousTID).signal();
					flagPrevious=false;
				}
				else if(mdState.get(previousTID) == state.THINKING && piTID!=previousTID) {
					selfMd.get(previousTID).signal();
					previousTID = (previousTID + (numberOfPhilosophers-1)) % numberOfPhilosophers;
				}
				else if(piTID == previousTID) {
					selfMd.get(previousTID).signal();
					flagPrevious=false;
				}
				else {
					previousTID = (previousTID + (numberOfPhilosophers-1)) % numberOfPhilosophers;
				}
			}
		}
		
		finally {
			lock.unlock();
		}
	}
	
	/**
	 * Only one philopher at a time is allowed to philosophy
	 * (while she is not eating).
	 */
	public void requestTalk(final int piTID)
	{
		lock.lock();
		try {
			mState[piTID] = state.TALKWAITING;
			testTalk(piTID);
			if(mState[piTID]!=state.TALKING) {
				self[piTID].await();
			}
		}
		catch(InterruptedException e) {
			System.err.println("Monitor.requestTalk(final int piTID):");
			DiningPhilosophers.reportException(e);
			System.exit(1);
		}
		finally {
			lock.unlock();
		}
	}

	/**
	 * When one philosopher is done talking stuff, others
	 * can feel free to start talking.
	 */
	public void endTalk(final int piTID)
	{
		lock.lock();
		try {
			mState[piTID] = state.THINKING;
			int nextTID = (piTID + 1) % numberOfPhilosophers;
			//int previousTID = (pos + (numberOfPhilosophers-1)) % numberOfPhilosophers;
			testTalk(nextTID);
			if(mState[nextTID] == state.TALKING)
				self[nextTID].signal();
			//testTalk(previousTID);
		}
		finally {
			lock.unlock();
		}
	}
	
	public void requestTalkDynamic(final int piTID)
	{
		
		lock.lock();
		try {
			//System.out.println("testtesttesttesttesttest");
			mdState.set(piTID, state.TALKWAITING);
			
			testTalkDynamic(piTID);
			
			if(mdState.get(piTID)!=state.TALKING) {
				selfMd.get(piTID).await();
			}
		}
		catch(InterruptedException e) {
			System.err.println("Monitor.requestTalkDynamic(final int piTID):");
			DiningPhilosophers.reportException(e);
			System.exit(1);
		}
		finally {
			lock.unlock();
		}
	}
	
	public void endTalkDynamic(final int piTID)
	{
		lock.lock();
		try {
			mdState.set(piTID, state.THINKING);
			int nextTID = (piTID + 1) % numberOfPhilosophers;
			//int previousTID = (pos + (numberOfPhilosophers-1)) % numberOfPhilosophers;
			testTalkDynamic(nextTID);
			if(mdState.get(nextTID) == state.TALKING)
				selfMd.get(nextTID).signal();
			//testTalk(previousTID);
		}
		finally {
			lock.unlock();
		}
	}
	
	public void requestSleep(final int piTID)
	{
		lock.lock();
		
		try {
			mState[piTID] = state.SLEEPWAITING;
			testSleep(piTID);
			if(mState[piTID]!=state.SLEEPING) {
				self[piTID].await();
			}
		}
		catch(InterruptedException e) {
			System.err.println("Monitor.requestSleep(final int piTID):");
			DiningPhilosophers.reportException(e);
			System.exit(1);
		}
		finally {
			lock.unlock();
		}
	}

	/**
	 * When one philosopher is done talking stuff, others
	 * can feel free to start talking.
	 */
	public void endSleep(final int piTID)
	{
		lock.lock();
		try {
			mState[piTID] = state.THINKING;
			int nextTID = (piTID + 1) % numberOfPhilosophers;
			testSleep(nextTID);
			if(mState[nextTID] == state.SLEEPING)
				self[nextTID].signal();
		}
		finally {
			lock.unlock();
		}
	}
	
	public void requestSleepDynamic(final int piTID)
	{
		lock.lock();
		
		try {
			mdState.set(piTID, state.SLEEPWAITING);
			testSleepDynamic(piTID);
			if(mdState.get(piTID)!=state.SLEEPING) {
				selfMd.get(piTID).await();
			}
		}
		catch(InterruptedException e) {
			System.err.println("Monitor.requestSleepDynamic(final int piTID):");
			DiningPhilosophers.reportException(e);
			System.exit(1);
		}
		finally {
			lock.unlock();
		}
	}
	
	public void endSleepDynamic(final int piTID)
	{
		lock.lock();
		try {
			mdState.set(piTID, state.THINKING);
			int nextTID = (piTID + 1) % numberOfPhilosophers;
			testSleepDynamic(nextTID);
			if(mdState.get(nextTID) == state.SLEEPING)
				selfMd.get(nextTID).signal();
		}
		finally {
			lock.unlock();
		}
	}
	
	public void requestPepperShaker(final int piTID) {
		lock.lock();
		boolean flag = testPepperShaker(piTID);
		try {
			if(flag == true) {
				shakerCount--;
			}
			else {
				shakerCondition.await();
			}
		}
		catch(InterruptedException e) {
			System.err.println("Monitor.requestPepperShanker(final int piTID):");
			DiningPhilosophers.reportException(e);
			System.exit(1);
		}
		finally {
			lock.unlock();
		}
	}
	
	public void putDownPepperShaker(final int piTID) {
		lock.lock();
		try {
			shakerCount++;
			shakerCondition.signal();
		}
		finally {
			lock.unlock();
		}
	}
	
	public synchronized boolean testPepperShaker(final int piTID) {
		if(this.mState[piTID] == state.EATING && shakerCount>0) {
			return true;
		}
		else{
			return false;
		}
	}
	
	private synchronized void testEat(int piTID) {
		
		int nextTID = (piTID + 1) % numberOfPhilosophers;
		int previousTID = (piTID + (numberOfPhilosophers-1)) % numberOfPhilosophers;
		if(mState[nextTID]!=state.EATING && mState[piTID]==state.HUNGRY && mState[previousTID]!=state.EATING) {
			mState[piTID] = state.EATING;
		}
	}
	
private synchronized void testEatDynamic(int piTID) {
		
		int nextTID = (piTID + 1) % numberOfPhilosophers;
		int previousTID = (piTID + (numberOfPhilosophers-1)) % numberOfPhilosophers;
		if(mdState.get(nextTID)!=state.EATING && mdState.get(piTID)==state.HUNGRY && mdState.get(previousTID)!=state.EATING) {
			mdState.set(piTID,state.EATING);
		}
	}
	
	private synchronized void testTalk(int piTID) {
		boolean flag = true;
		int curIdx;
		for(int i = (piTID+1); i<piTID+numberOfPhilosophers; i++) {
			curIdx = i % numberOfPhilosophers;
			if(mState[curIdx]==state.TALKING || mState[curIdx]==state.EATING) {
				flag = false;
				break;
			}
		}
		if(mState[piTID]==state.TALKWAITING && flag==true) {
			mState[piTID] = state.TALKING;
		}
	}
	
	private synchronized void testTalkDynamic(int piTID) {
		boolean flag = true;
		int curIdx;
		for(int i = (piTID+1); i<piTID+numberOfPhilosophers; i++) {
			curIdx = i % numberOfPhilosophers;
			if(mdState.get(curIdx)==state.TALKING || mdState.get(curIdx)==state.EATING) {
				flag = false;
				break;
			}
		}
		if(mdState.get(piTID)==state.TALKWAITING && flag==true) {
			mdState.set(piTID,state.TALKING);
		}
	}
	
	private synchronized void testSleep(int piTID) {
		boolean flag = true;
		int curIdx;
		for(int i = piTID+1; i<piTID+numberOfPhilosophers; i++) {
			curIdx = i % numberOfPhilosophers;
			if(mState[curIdx]==state.TALKING) {
				flag = false;
				break;
			}
		}
		if(mState[piTID] == state.SLEEPWAITING && flag==true) {
			mState[piTID] = state.SLEEPING;
		}
	}
	
	private synchronized void testSleepDynamic(int piTID) {
		boolean flag = true;
		int curIdx;
		for(int i = piTID+1; i<piTID+numberOfPhilosophers; i++) {
			curIdx = i % numberOfPhilosophers;
			if(mdState.get(curIdx)==state.TALKING) {
				flag = false;
				break;
			}
		}
		if(mdState.get(piTID) == state.SLEEPWAITING && flag==true) {
			mdState.set(piTID, state.SLEEPING);
		}
	}
	
	public void leavingDynamic(final int piTID)
	{
		lock.lock();
		try {
			state tempState = mdState.get(piTID);
			switch(tempState) {
				case EATING:
					putDownDynamic(piTID);
					break;
				case TALKING:
					endTalkDynamic(piTID);
					break;
				case SLEEPING:
					endSleepDynamic(piTID);
					break;
				case WAITINGJOIN:
					putDownDynamic(piTID);
					break;
				default:
					selfMd.get(piTID).signal();
			}
			mdState.set(piTID, state.LEAVING);
			ifLeave.set(piTID, 1);
			int count=0;
			int activeTID = piTID;
			for(int i=0; i<this.numberOfPhilosophers;i++) {
				if(mdState.get(i)!=state.LEAVING) {
					count++;
					activeTID = i;
				}
			}
			if(count==1)
				selfMd.get(activeTID).signal();	//prevent the deadlock for the last philosopher.
			
		}
		
		finally {
			lock.unlock();
		}
	}
	
	public void joinningDynamic(final int piTID) {
		lock.lock();
		try {
			this.mdState.add(piTID, state.WAITINGJOIN);
			this.selfMd.add(piTID, lock.newCondition());
			this.ifLeave.add(piTID, 0);
			this.numberOfPhilosophers++;
			testJoinDynamic(piTID);
			if(mdState.get(piTID)!=state.THINKING) {
				selfMd.get(piTID).await();
			}
		}
		catch(InterruptedException e) {
			System.err.println("Monitor.joinningDynamic(final int[] piTIDArr:");
			DiningPhilosophers.reportException(e);
			System.exit(1);
		}
		finally {
			lock.unlock();
		}
	}
	
	
	public void testJoinDynamic(int piTID){
		int nextTID = (piTID+1)%this.numberOfPhilosophers;
		int previousTID = (piTID + (numberOfPhilosophers-1)) % numberOfPhilosophers;
		if(mdState.get(nextTID)!=state.EATING && mdState.get(piTID) == state.WAITINGJOIN && mdState.get(previousTID)!=state.EATING)
			this.mdState.set(piTID, state.THINKING);
	}
	
	public synchronized void reState() {
		for(int i = 0; i<this.numberOfPhilosophers; i++) {
			mState[i] = state.THINKING;
		}
	}
}

// EOF
