package presentsortingmachine;

import java.io.File;
import java.util.Scanner;

//Reads the file and stores the required component objects in arrays
public class Configuration {

    public static int numberOfBelts = 0;
    public static int numberOfHoppers = 0;
    public static int numberOfSacks = 0;
    public static int numberOfTurntables = 0;
    public static int numberOfPresents = 0;
    public static int timer = 0;
    public static Belt[] beltArray;
    public static Hopper[] hopperArray;
    public static Sack[] sackArray;
    public static TurnTable[] turnTableArray;
    public static Present[] presentArray;

    Configuration(String filePath) throws Exception {
        //Create a new file object
        File file = new File(filePath);
        Scanner fileScanner = new Scanner(file);

        //Initial pass - update the number of items required
        //Read in the contents of the file line by line
        //Get the timer
        while (fileScanner.hasNextLine()) {
            String lineValue = fileScanner.nextLine();

            //Parse the string into an array
            String[] lineArray = lineValue.split(" ");
            
            //Initialise belts
            if("BELTS".equals(lineArray[0])) {
                numberOfBelts = Integer.valueOf(fileScanner.nextLine());
                beltArray = new Belt[numberOfBelts];
                for(int i = 0; i < numberOfBelts; i++) {
                    String[] beltLineArray = fileScanner.nextLine().split(" ");
                    int numberOfDestinations = beltLineArray.length - 4;
                    int[] destinations = new int[numberOfDestinations];
                    //Add in the destinations
                    for (int j = 4; j < beltLineArray.length; j++) {
                        destinations[j - 4] = Integer.valueOf(beltLineArray[j]);
                    }
                    
                    beltArray[i] = new Belt(Integer.valueOf(beltLineArray[0]), Integer.valueOf(beltLineArray[2]) , destinations);
                }
            }
            
            //Initialise hoppers
            if("HOPPERS".equals(lineArray[0])) {
                numberOfHoppers = Integer.valueOf(fileScanner.nextLine());
                hopperArray = new Hopper[numberOfHoppers];
                for(int i = 0; i < numberOfHoppers; i++) {
                    String[] hopperLineArray = fileScanner.nextLine().split(" ");
                    hopperArray[i] = new Hopper(Integer.valueOf(hopperLineArray[0]), Integer.valueOf(hopperLineArray[2]), Integer.valueOf(hopperLineArray[4]), Integer.valueOf(hopperLineArray[6]), beltArray);
                }
            }
            
            //Initialise the sacks
            if("SACKS".equals(lineArray[0])) {
                numberOfSacks = Integer.valueOf(fileScanner.nextLine());
                sackArray = new Sack[numberOfSacks];
                for(int i = 0; i < numberOfSacks; i++) {
                    String[] sackLineArray = fileScanner.nextLine().split(" ");
                    sackArray[i] = new Sack(Integer.valueOf(sackLineArray[0]), Integer.valueOf(sackLineArray[2]), sackLineArray[4]);
                }
            }
            
            //Initialise turntables
            if("TURNTABLES".equals(lineArray[0])) {
                numberOfTurntables = Integer.valueOf(fileScanner.nextLine());
                turnTableArray = new TurnTable[numberOfTurntables];
                for (int i = 0; i < numberOfTurntables; i++) {
                    String[] ttLineArray = fileScanner.nextLine().split(" ");
                    String id, north = "null", east = "null", south = "null", west = "null";
                    int northC = 0, eastC = 0, southC = 0, westC = 0;
                    id = ttLineArray[0];
                    for (int j = 0; j < ttLineArray.length; j++) {
                        //North
                        if ("N".equals(ttLineArray[j])) {
                           //Check if this is a null gate
                           if("null".equals(ttLineArray[j+1])) {
                               north = ttLineArray[j+1];
                               northC = 0;
                           }
                           
                           else {
                               north = ttLineArray[j+1];
                               northC = Integer.valueOf(ttLineArray[j+2]);
                           }
                        }
                        
                        //East
                        if ("E".equals(ttLineArray[j])) {
                           //Check if this is a null gate
                           if("null".equals(ttLineArray[j+1])) {
                               east = ttLineArray[j+1];
                               eastC = 0;
                           }
                           
                           else {
                               east = ttLineArray[j+1];
                               eastC = Integer.valueOf(ttLineArray[j+2]);
                           }
                        }
                        
                        //South
                        if ("S".equals(ttLineArray[j])) {
                           //Check if this is a null gate
                           if("null".equals(ttLineArray[j+1])) {
                               south = ttLineArray[j+1];
                               southC = 0;
                           }
                           
                           else {
                               south = ttLineArray[j+1];
                               southC = Integer.valueOf(ttLineArray[j+2]);
                           }
                        }
                        
                        //West
                        if ("W".equals(ttLineArray[j])) {
                           //Check if this is a null gate
                           if("null".equals(ttLineArray[j+1])) {
                               west = ttLineArray[j+1];
                               westC = 0;
                           }
                           
                           else {
                               west = ttLineArray[j+1];
                               westC = Integer.valueOf(ttLineArray[j+2]);
                           }
                        }
                    }
                    
                    turnTableArray[i] = new TurnTable(id, north, northC, east, eastC, south, southC, west, westC);
                }
            }
            
            //Initialise the presents
            for (int i = 1; i < numberOfHoppers+1; i++) {
                if("PRESENTS".equals(lineArray[0]) && Integer.toString(i).equals(lineArray[1])) {
                    int totalPresents = Integer.valueOf(fileScanner.nextLine());
                    Present[] presentList = new Present[totalPresents];
                    //Inner loop - add the presents
                    for(int j = 0; j < totalPresents; j++) {
                        presentList[j] = new Present(fileScanner.nextLine());
                    }
                    hopperArray[i-1].SetPresentList(presentList);
                }
            }

            //Initialise timer
            if ("TIMER".equals(lineArray[0])) {
                timer = Integer.valueOf(lineArray[1]);
            }
        }
    }
}
