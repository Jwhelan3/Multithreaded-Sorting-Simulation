package presentsortingmachine;

import java.util.concurrent.Semaphore;

/* This class acts as a buffer, with the turntables acting as both the 
producers and consumers depending on their current state. The belt has
a maximum capacity, and has been implemented as a circular buffer. This
class has two critical regions and therefore uses a semaphore to ensure
thread safety when writing */
public class Belt {

    private final int mBeltNumber;          //The identifier of this belt
    private final int mLength;              //The maximum capacity of the belt
    private Present[] mContents;            //A list of the presents currently stored on the belt
    private volatile int mNumberOfPresents; //The number of presents that the belt is currently holding
    private int mHead = 0;                  //A pointer to the head of the circular buffer
    private int mTail = 0;                  //Tail of the circular buffer
    final static Semaphore semaphore = new Semaphore(1, true);//Used to ensure only one thread is in the critical region at any one time

    //Initialise the private data members on instantiation
    public Belt(int beltNumber, int length, int[] destinations) {
        mBeltNumber = beltNumber;
        mLength = length;
        mContents = new Present[length];
    }

    //Return true if the belt is full
    public boolean BeltFull() {
        return mNumberOfPresents >= mLength;
    }

    //Return true if the belt is empty
    public boolean BeltEmpty() {
        return mNumberOfPresents == 0;
    }

    //---Critical region--- - only one thread can access this region at any one time
    //Put a present on the belt, provided there is room
    public void Put(Present present) throws InterruptedException {
        try {
            semaphore.acquire();
            mContents[(++mHead) % mLength] = present;
            mNumberOfPresents++;
        } catch (InterruptedException e) {
            throw e;
        } finally {
            semaphore.release();
        }

    }

    //Take a present
    public Present Take() throws InterruptedException {
        try {
            semaphore.acquire();
            //Check first whether a present can be taken
            if (mNumberOfPresents == 0) {
                return null;
            }

            //1 or more presents exist - return the next present in the queue
            mNumberOfPresents--; //Decrement the number of presents
            mTail++;    //Move the tail of the buffer +1 towards the head
            return mContents[mTail % mLength];
        } catch (InterruptedException e) {
            throw e;
        } finally {
            semaphore.release();
        }
    }
    //End of critical region

    //Getters for the private data members
    public int GetBeltNumber() {
        return mBeltNumber;
    }

    public int getNumberOfPresents() {
        return mNumberOfPresents;
    }
}
