package presentsortingmachine;

import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

//This class handles all outputs to the screen, seperating the interface logic from the rest of the program
public class Reporter extends TimerTask {

    private static int mPresentsInHoppers = 0;
    private static int mPresentsInSacks = 0;
    private static int mPresentsOnTurnTables = 0;
    private static int mPresentsOnBelts = 0;
    private Hopper[] pHoppers;
    private Belt[] pBelts;
    private Sack[] pSacks;
    private TurnTable[] pTts;

    //Initialise with the time that the machine was started, and a pointer to the component objects
    Reporter(long time, Hopper[] h, Belt[] b, Sack[] s, TurnTable[] tt) {
        Started(time);
        pHoppers = h;
        pBelts = b;
        pSacks = s;
        pTts = tt;
    }

    //Handles the scheduled task
    @Override
    public void run() {
        Update(PresentSortingMachine.mTimeDiff);
    }

    //Poll the system to see the number of gifts currently in the sacks and hoppers
    private void GetStatus() {
        
        mPresentsInHoppers = 0; //Set to 0 to avoid the += inflating the number wrongly
        
        //Iterate through each component array to take a tally
        for (int i = 0; i < pHoppers.length; i++) {
            mPresentsInHoppers += pHoppers[i].getNumberOfPresents();
        }

        mPresentsInSacks = 0;
        for (int i = 0; i < pSacks.length; i++) {
            mPresentsInSacks += pSacks[i].getNumberOfPresents();
        }

        mPresentsOnTurnTables = 0;
        for (int i = 0; i < pTts.length; i++) {
            mPresentsOnTurnTables += pTts[i].getNumberOfPresents();
        }

        mPresentsOnBelts = 0;
        for (int i = 0; i < pBelts.length; i++) {
            mPresentsOnBelts += pBelts[i].getNumberOfPresents();
        }

    }

    //Utility to turn the time the machine has run for into a human-readable format
    private String ParseTime(long time) {
        long seconds = TimeUnit.SECONDS.convert(time, TimeUnit.NANOSECONDS) % 60;
        long minutes = TimeUnit.MINUTES.convert(time, TimeUnit.NANOSECONDS) % 60;
        long hours = TimeUnit.HOURS.convert(time, TimeUnit.NANOSECONDS) % 60;

        //Use '*= -1' to turn the numbers positive
        String result = (hours *= -1) + "h:" + (minutes *= -1) + "m:" + (seconds *= -1) + "s: ";
        return result;
    }

    //Issued on start up of the machine to indicate the beginning time
    public void Started(long time) {
        String prefix = ParseTime(time);
        System.out.println(prefix + "Machine started");
    }

    //Issue the shutdown message and cancel the scheduled task associated with this class
    public void Shutdown(long time) {
        String prefix = ParseTime(time);
        System.out.println(prefix + "Machine shutdown");
        this.cancel();
    }

    //Serialize the current state of the machine to check for changes
    public String StatusKey() {
        String result = "status:";
        //Get the status of the belts
        for (int i = 0; i < Configuration.numberOfBelts; i++) {
            int presentsOnBelt = pBelts[i].getNumberOfPresents();
            result = result.concat(Integer.toString(presentsOnBelt));
        }

        //Get the stauts of the turntables
        for (int i = 0; i < Configuration.numberOfTurntables; i++) {
            int hasPresent = pTts[i].getNumberOfPresents();
            result = result.concat(Integer.toString(hasPresent));
        }

        return result;
    }

    //Create the final report detailing the status of the system
    public void MakeFinalReport(long time, String configFile) {
        //Get a final update
        GetStatus();
        //Print the report
        String runTime = ParseTime(time);
        System.out.println("--------------------");
        System.out.println("Final report: ");
        System.out.println("Configuration file used: " + configFile);
        System.out.println("Total runtime: " + runTime);
        int presentsProcessed = 0;
        for (int i = 0; i < Configuration.numberOfHoppers; i++) {
            //Loop through the hoppers and output their final stats
            System.out.println("Hopper " + pHoppers[i].getHopperNumber() + " waited for " + pHoppers[i].getTotalWaitTime() + " seconds and processed " + pHoppers[i].getTotalPresents() + " presents.");
            presentsProcessed += pHoppers[i].getTotalPresents();    //Increment this for the later check
        }
        //Get the total presents still in the machine
        int presentsOnMachine = mPresentsOnTurnTables + mPresentsOnBelts;
        System.out.println("There are " + presentsOnMachine + " presents still on the machine. ");
        int check = presentsProcessed - presentsOnMachine - mPresentsInSacks;
        System.out.println("Check result: " + check);
    }

    //Output the current status, associated with the timer task to output the 10 secondly message
    public void Update(long time) {
        GetStatus();
        String prefix = ParseTime(time);
        System.out.println(prefix + "Presents remaining in hoppers: " + mPresentsInHoppers + ". Presents delivered to sacks: " + mPresentsInSacks);
    }
}
