package presentsortingmachine;

import java.util.Timer;
import java.util.TimerTask;

/*The hopper class will try to place its present load on to the associated
belt once every mSpeed seconds, while simultaneously maintaining a list and
refilling the hopper with any presents that didn't fit in initially. When both
the hopper and the list are empty, the hopper will end its scheduled task and
wait to be issued with a shutdown command */
public class Hopper extends Thread {

    private final int mHopperNumber;
    private Belt pBelt;             //A pointer to the belt associated with the hopper
    private final int mCapacity;    //The capacity of this hopper
    private final int mSpeed;       //Intervals between each attempt to drop a present on a conveyor belt
    private int mNumberOfPresents;  //Current number of presents in this hopper
    private int mPresentsRemainingInList = 0;//Indicates whether there are presents in the list that didn't fit in the hopper
    private boolean mShutdown = false;//Flag: Was the shutdown command issued?
    private Present[] mPresentList; //The presents waiting to be loaded in to the hopper, but didn't fit on initialisation
    private Present[] mPresents;    //The current presents in the hopper
    private int mPresentListPointer;//The current progress in the present list
    private int mTotalPresentsDeployed = 0; //Final reporting stat for the total number of gifts placed on to the associated turntable
    private long mTotalWaitTime = 0;//Final reporting stat for the total time this hopper has spent waiting
    private int mPresentToPlace = 0;//Index of the next present to be placed
    Timer timer = new Timer();

    /*The constructor sets up the variables required from the configuration file, gets a pointer to its belt
    but doesn't start any threads, scheduled task or initialisation.*/
    public Hopper(int number, int belt, int capacity, int speed, Belt[] belts) {
        mHopperNumber = number;
        mCapacity = capacity;
        mSpeed = speed;
        mPresents = new Present[capacity];
        mNumberOfPresents = 0;

        //Set the belt pointer
        for (int i = 0; i < belts.length; i++) {
            if (belts[i].GetBeltNumber() == belt) {
                //This is the associated belt
                pBelt = belts[i];
            }
        }
    }

    //Hopper identifier
    public int getHopperNumber() {
        return mHopperNumber;
    }

    //Presents currently in the hopper
    public int getNumberOfPresents() {
        return mNumberOfPresents;
    }

    //Total presents this hopper has dropped off
    public int getTotalPresents() {
        return mTotalPresentsDeployed;
    }

    //Total time that this hopper has been waiting to drop a present
    public long getTotalWaitTime() {
        return mTotalWaitTime;
    }

    //Attempt to place a present on to its associated conveyor belt
    private void PutPresent() {
        //Are there presents waiting?
        if (mNumberOfPresents > 0) {
            //Does the belt have room for the present?
            if (!pBelt.BeltFull()) {
                if ((mPresentToPlace) == mCapacity) {
                    mPresentToPlace--;
                }

                try {
                    pBelt.Put(mPresents[mPresentToPlace]);
                    //Update the tracking variables
                    mPresentToPlace++;
                    mNumberOfPresents--;
                    mTotalPresentsDeployed++;
                } catch (InterruptedException e) {
                    mTotalWaitTime += mSpeed;
                }
            } //There was no room, meaning that on this turn the hopper was waiting
            else {
                mTotalWaitTime += mSpeed;
            }
        }
    }

    @Override
    public void run() {
        //Hopper started - presents can be loaded in
        LoadInPresents();
        //Create a new timer to try and place a new present every mSpeed seconds
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //Are there presents still waiting?
                if (mNumberOfPresents == 0 && mPresentsRemainingInList == 0) {
                    //No presents to process, this task is now redundant
                    this.cancel();
                }

                //There are still presents waiting to enter the hopper, but the hopper is empty
                if (mPresentsRemainingInList > 0 && mNumberOfPresents == 0) {
                    LoadInPresents();   //load them into the hopper
                }

                PutPresent();
            }
        }, 0, (mSpeed * 1000));
    }

    //Sets the list of presents that this hopper needs to create
    public void SetPresentList(Present[] presentList) {
        mPresentList = presentList;
        mPresentsRemainingInList = presentList.length;
        mPresentListPointer = 0;
        if (mPresentsRemainingInList == 0) {
            timer.cancel();
        }
    }

    //Tell the hoppper that the defined run time has expired and to stop adding presents
    public void IssueShutdown() {
        mShutdown = true;
        timer.cancel();
    }

    //Load in the presents from the beginning of the array to the capacity or end, whichever comes first
    private void LoadInPresents() {
        int temp = mPresentsRemainingInList;    //Store the initial value of the number of remaining presents
        if (mPresentsRemainingInList > 0) {
            for (int i = 0; i < temp; i++) {
                //Only do this next step if the hopper has the capacity
                if (mNumberOfPresents < mCapacity) {
                    mPresents[i] = mPresentList[mPresentListPointer];
                    mNumberOfPresents++;
                    mPresentListPointer++;
                    mPresentsRemainingInList--;
                }
            }
            mPresentToPlace = 0;    //Reset the pointer to the beginning of the array
        }
    }

}
