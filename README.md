# Multithreaded-Sorting-Simulation
A simulation of a sorting machine in Java, demonstrating concurrency

This exercise demonstrates the prevention of deadlock in a multi-threaded application, ensuring that despite the many concurrent threads accessing single objects, the critical region is locked using semaphores as a solution to the issues arising in the producer-consumer problem.



## Classes
*PresentSortingMachine.java - the main class, which reads in a configuration file, initialises all of the required threads and begins the simulation. Waits for the end condition to be met before ending the simulation and outputting a results message.
*Configuration.java - Parses the configuration .txt file.
*Present.java - As the simulation is a present sorting machine, the Present object represents the presents and contains information on its destination.
*Belt.java - A circular buffer with a variable length, which handles adding and removing of the Present object.
*Hopper.java - Has a capacity and a timer defined on initialisation. The timer defines at what intervals the hopper attempts to drop a new present. Each hopper runs a thread.
*Sack.java - Stores all of the presents which have reached their destination. Has a maximum capacity defined in the configuration file.
*TurnTable.java - Each turn table runs on its own thread, essentially controls movement of the Present objects around the machine, deciding which orientation to deposit them based on the Present object's properties.
*Reporter.java - Handles the reporting of the simulation, outputting the status at 10 second intervals and displays a final report on shutdown.
