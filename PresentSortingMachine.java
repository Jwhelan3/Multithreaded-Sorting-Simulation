package presentsortingmachine;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

//Entry point of the program
public class PresentSortingMachine {

    /**
     * @param args the command line arguments
     */

    //Variables to support the timer for reporting and machine operation length
    private static boolean terminate = false;
    public static long mTimeDiff = 0;
    private static final long mStartTime = System.nanoTime(); //Get the time that the program began running to calculate elapsed time
    private final static String mConfigFilePath = "C:\\Users\\Josh\\Desktop\\configuration.txt";

    private static Configuration mConfig;   //Reads the file

    //Store the components of the machine inside arrays of objects
    public static Belt[] beltArray;
    public static Hopper[] hopperArray;
    public static Sack[] sackArray;
    public static TurnTable[] turnTableArray;
    public static Present[] presentArray;

    //Main program driver
    public static void main(String[] args) {

        //Read in the configuration file
        try {
            mConfig = new Configuration(mConfigFilePath);
        } catch (Exception e) {
            //Configuration file could not be read
            System.out.println("Unable to find configuration file");
            return;
        }

        //Store pointers to the machine components
        beltArray = Configuration.beltArray;
        hopperArray = Configuration.hopperArray;
        sackArray = Configuration.sackArray;
        turnTableArray = Configuration.turnTableArray;
        presentArray = Configuration.presentArray;

        //Machine initialised - begin reporting
        Reporter mReporter = new Reporter(0, hopperArray, beltArray, sackArray, turnTableArray);

        //Start the threads
        for (int i = 0; i < hopperArray.length; i++) {
            //Hoppers
            hopperArray[i].start();
        }

        for (int i = 0; i < turnTableArray.length; i++) {
            //Turntables
            turnTableArray[i].start();
        }

        //Call the reporter once every ten seconds using a scheduled timer task
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(mReporter, 0, 10 * 1000);

        boolean shutdown = false;   //Flag: shutdown commands have already been issued

        //Main program loop - outer loop runs until the machine has indicated it is ready to end
        while (!terminate) {
            mTimeDiff = mStartTime - System.nanoTime();  //Update the elapsed time

            //Store the elapsed time and convert it into seconds
            long totalRunTime = TimeUnit.SECONDS.convert(mTimeDiff, TimeUnit.NANOSECONDS);
            totalRunTime *= -1; //Positive conversion

            //Check whether the defined time to run is expired
            if (totalRunTime >= (long) Configuration.timer && !shutdown) {
                //mReporter.Shutdown(mTimeDiff);

                mReporter.Shutdown(mTimeDiff);  //Shut down the reporter
                //Tell all of the hoppers to shut down
                for (int i = 0; i < hopperArray.length; i++) {
                    hopperArray[i].IssueShutdown();
                }

                //Flags to check the machine state
                shutdown = true;    //Timer has expired and shutdown can be issued
                boolean machineFinished = false;//Shutdown was issued and all components have finished their operations

                String statusKey = "";  //Store the current state of the machines and a key to compare
                String previousKey;     //The state of the machine from the previous 'tick'
                int ticksWaited = 0;    //The total number of 'ticks' that the machine waited and nothing changed

                //Inner loop, waits for the machine to finish moving around the components
                while (!machineFinished) {
                    //Poll the components to see the state of the machine
                    previousKey = statusKey;
                    //Create a string containing the current state of the machine
                    statusKey = mReporter.StatusKey();
                    //Ensure the inner loop is keeping the time updated
                    mTimeDiff = mStartTime - System.nanoTime();  //Update the elapsed time

                    //Compares this string
                    if (statusKey.equals(previousKey)) {
                        //Nothing has happened since the previous tick
                        ticksWaited++;
                    } else {
                        //A change occured, reset the tracker
                        ticksWaited = 0;
                    }

                    //Nothing has moved in 3 seconds - the machine is awaiting sack emptying and can terminate
                    if (ticksWaited > 3) {
                        machineFinished = true;
                    }

                    //If the machine has finished, skip the wait below
                    if (!machineFinished) {
                        try {
                            //Pause for one second before polling again
                            TimeUnit.SECONDS.sleep(1);
                        } catch (Exception e) {
                            //Sleep failed
                        }
                    }
                }

                //Machine has finished, make the final report and end the program
                mReporter.MakeFinalReport(mTimeDiff, mConfigFilePath);
                timer.cancel();                 //Timer no longer needed
                terminate = true;               //End the while loop
            }
        }

        //Stop the turntable threads
        for (int i = 0; i < turnTableArray.length; i++) {
            //Turntables
            turnTableArray[i].Shutdown();
        }
    }
}
