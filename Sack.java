package presentsortingmachine;

//The sack is essentially a fixed-size stack, storing presents that match its criteria
public class Sack {

    private int mSackNumber;        //Identifier for this sack
    private int mSackCapacity;      //Maximum number of presents it can hold
    private int mNumberOfPresents;  //The current number of presents it holds
    private String mSackAgeRange;   //The type of present this sack accepts
    public Present[] mSackContents; //The physical contents of this sack

    //Set the private data attributes on initialisation
    public Sack(int number, int capacity, String ageRange) {
        //Set the private attributes
        mSackNumber = number;
        mSackCapacity = capacity;
        mSackAgeRange = ageRange;

        //Initialise the array to store the presents
        mSackContents = new Present[capacity];
    }

    //Insert a gift in to the sack
    public void Insert(Present present) {
        mSackContents[mNumberOfPresents] = present;
        mNumberOfPresents++;
    }

    //Check whether the sack is already at its capacity
    public boolean SackFull() {
        return mNumberOfPresents >= mSackCapacity;
    }

    //Getters for private data members
    public int getSackNumber() {
        return mSackNumber;
    }

    public int getNumberOfPresents() {
        return mNumberOfPresents;
    }

    public String getSackType() {
        return mSackAgeRange;
    }

}
