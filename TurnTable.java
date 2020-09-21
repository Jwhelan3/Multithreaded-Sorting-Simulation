package presentsortingmachine;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TurnTable extends Thread {

    //Possible directions for the turntable to face
    enum eDirections {
        ns, ew
    }

    //Store the connection details (type & pointer)
    private String mIdentifier;
    private String mNorth;
    private int mNorthConnection;
    private String mEast;
    private int mEastConnection;
    private String mSouth;
    private int mSouthConnection;
    private String mWest;
    private int mWestConnection;
    private eDirections mCurrentDirection;//Current direction that this turntable is facing
    private long mRotateSpeed = 500;    //Speed for the turntable to turn 90 degrees
    private long mMoveSpeed = 750;     //Speed for the turntable to move a present either on or off
    private Present mPresent;           //The present stored in this turntable
    private boolean mShutdown = false;          //If the shutdown command is issued, the turntable will halt

    //Constructor - initialises the connections and turntable ID
    public TurnTable(String id, String northType, int northConnection, String eastType, int eastConnection, String southType, int southConnection, String westType, int westConnection) {
        mIdentifier = id;
        mNorth = northType;
        mNorthConnection = northConnection;
        mEast = eastType;
        mEastConnection = eastConnection;
        mSouth = southType;
        mSouthConnection = southConnection;
        mWest = westType;
        mWestConnection = westConnection;
        mCurrentDirection = eDirections.ns;     //Default to a north-south orientation
    }

    //Returns the current number of presents on this turntable (will only ever be 1 or 0)
    public int getNumberOfPresents() {
        int result = 0;
        if (mPresent != null) {
            result = 1;
        }
        return result;
    }

    //If the turntable is empty, continuily looks for a present it can take. Otherwise, 
    //the machine must have a present and will therefore continuily try to place it at
    //a connected component
    @Override
    public void run() {

        while (!mShutdown) {
            //Run until the thread is issued with a shutdown command
            //If there is no present on the table, check for an inbound present
            if (mPresent == null) {
                ProcessInbound();
            } //Otherwise, see where the present should go
            else {
                ProcessOutbound();
            }
        }
    }

    //Turn the turntable by 90 degrees (essentially just sleep and alter the direction state)
    private void Rotate() {
        try {
            Thread.sleep(mRotateSpeed); //Sleep to simulate turning time
            //Flip the direction
            if (mCurrentDirection == eDirections.ew) {
                mCurrentDirection = eDirections.ns;
            } else {
                mCurrentDirection = eDirections.ew;
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(TurnTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Look to place a present at one of the outbound connections, going clockwise beginning with north
    private void ProcessOutbound() {
        //Prioritise sacks - look for outbound sacks (os) gates
        //North gate
        if ("os".equals(mNorth)) {
            //Gate is outbound to a sack - check whether it is a suitable destination for this present
            for (int i = 0; i < Configuration.numberOfSacks; i++) {
                if (PresentSortingMachine.sackArray[i].getSackNumber() == mNorthConnection) {
                    String presentType = mPresent.getPresentType();
                    String sackRange = PresentSortingMachine.sackArray[i].getSackType();
                    if (presentType.equals(sackRange)) {
                        //This sack is an appropriate destination

                        //Does this sack have capacity?
                        if (!PresentSortingMachine.sackArray[i].SackFull()) {
                            //The sack has capacity and the process to insert the present can begin
                            //Is the turntable correctly lined up?
                            if (mCurrentDirection != eDirections.ns) {
                                //Not lined up - rotate
                                Rotate();
                            }

                            //Transfer the present
                            PresentSortingMachine.sackArray[i].Insert(mPresent);
                            mPresent = null;
                            try {
                                Thread.sleep(mMoveSpeed);   //Sleep to simulate the present moving
                            } catch (InterruptedException ex) {
                                Logger.getLogger(TurnTable.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            return;
                        }
                    }
                }
            }
        }

        //East gate
        if ("os".equals(mEast)) {
            //Gate is outbound to a sack - check whether it is a suitable destination for this present
            for (int i = 0; i < Configuration.numberOfSacks; i++) {
                if (PresentSortingMachine.sackArray[i].getSackNumber() == mEastConnection) {
                    String presentType = mPresent.getPresentType();
                    String sackRange = PresentSortingMachine.sackArray[i].getSackType();
                    if (presentType.equals(sackRange)) {
                        //This sack is an appropriate destination

                        //Does this sack have capacity?
                        if (!PresentSortingMachine.sackArray[i].SackFull()) {
                            //The sack has capacity and the process to insert the present can begin
                            //Is the turntable correctly lined up?
                            if (mCurrentDirection != eDirections.ew) {
                                //Not lined up - rotate
                                Rotate();
                            }

                            //Transfer the present
                            PresentSortingMachine.sackArray[i].Insert(mPresent);
                            mPresent = null;
                            try {
                                Thread.sleep(mMoveSpeed);   //Sleep to simulate the present moving
                            } catch (InterruptedException ex) {
                                Logger.getLogger(TurnTable.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            return;
                        }
                    }
                }
            }
        }

        //South gate
        if ("os".equals(mSouth)) {
            //Gate is outbound to a sack - check whether it is a suitable destination for this present
            for (int i = 0; i < Configuration.numberOfSacks; i++) {
                if (PresentSortingMachine.sackArray[i].getSackNumber() == mSouthConnection) {
                    String presentType = mPresent.getPresentType();
                    String sackRange = PresentSortingMachine.sackArray[i].getSackType();
                    if (presentType.equals(sackRange)) {
                        //This sack is an appropriate destination

                        //Does this sack have capacity?
                        if (!PresentSortingMachine.sackArray[i].SackFull()) {
                            //The sack has capacity and the process to insert the present can begin
                            //Is the turntable correctly lined up?
                            if (mCurrentDirection != eDirections.ns) {
                                //Not lined up - rotate
                                Rotate();
                            }

                            //Transfer the present
                            PresentSortingMachine.sackArray[i].Insert(mPresent);
                            mPresent = null;
                            try {
                                Thread.sleep(mMoveSpeed);   //Sleep to simulate the present moving
                            } catch (InterruptedException ex) {
                                Logger.getLogger(TurnTable.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            return;
                        }
                    }
                }
            }
        }

        //West gate
        if ("os".equals(mWest)) {
            //Gate is outbound to a sack - check whether it is a suitable destination for this present
            for (int i = 0; i < Configuration.numberOfSacks; i++) {
                if (PresentSortingMachine.sackArray[i].getSackNumber() == mWestConnection) {
                    String presentType = mPresent.getPresentType();
                    String sackRange = PresentSortingMachine.sackArray[i].getSackType();
                    if (presentType.equals(sackRange)) {
                        //This sack is an appropriate destination

                        //Does this sack have capacity?
                        if (!PresentSortingMachine.sackArray[i].SackFull()) {
                            //The sack has capacity and the process to insert the present can begin
                            //Is the turntable correctly lined up?
                            if (mCurrentDirection != eDirections.ew) {
                                //Not lined up - rotate
                                Rotate();
                            }

                            //Transfer the present
                            PresentSortingMachine.sackArray[i].Insert(mPresent);
                            mPresent = null;
                            try {
                                Thread.sleep(mMoveSpeed);   //Sleep to simulate the present moving
                            } catch (InterruptedException ex) {
                                Logger.getLogger(TurnTable.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            return;
                        }
                    }
                }
            }
        }

        Belt pBelt = null;  //Pointer to the destination belt object

        //No associated sack was found to place the present, repeat the process with belts
        //North gate
        if ("ob".equals(mNorth)) {
            //Gate is outbound to a buffer, find a pointer
            for (int i = 0; i < Configuration.numberOfBelts; i++) {
                if (PresentSortingMachine.beltArray[i].GetBeltNumber() == mNorthConnection) {
                    //This is the connected belt
                    pBelt = PresentSortingMachine.beltArray[i];
                }
            }

            //Is this an empty belt? Skip over if not
            if (pBelt.BeltEmpty()) {
                //Belt is empty and can be written to
                //Is the turntable pointing the right way?
                if (mCurrentDirection != eDirections.ns) {
                    //Not lined up correctly
                    Rotate();
                }

                //Transfer the present to the buffer
                try {
                    pBelt.Put(mPresent);
                    mPresent = null;
                    try {
                        Thread.sleep(mMoveSpeed);   //Sleep to simulate the present moving
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TurnTable.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (InterruptedException e) {
                }
                return;

            }
        }

        //East gate
        if ("ob".equals(mEast)) {
            //Gate is outbound to a buffer, find a pointer
            for (int i = 0; i < Configuration.numberOfBelts; i++) {
                if (PresentSortingMachine.beltArray[i].GetBeltNumber() == mEastConnection) {
                    //This is the connected belt
                    pBelt = PresentSortingMachine.beltArray[i];
                }
            }

            //Is this an empty belt? Skip over if not
            if (pBelt.BeltEmpty()) {
                //Belt is empty and can be written to
                //Is the turntable pointing the right way?
                if (mCurrentDirection != eDirections.ew) {
                    //Not lined up correctly
                    Rotate();
                }

                //Transfer the present to the buffer
                try {
                    pBelt.Put(mPresent);
                    mPresent = null;
                    try {
                        Thread.sleep(mMoveSpeed);   //Sleep to simulate the present moving
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TurnTable.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (InterruptedException e) {
                }
                return;
            }
        }

        //South gate
        if ("ob".equals(mSouth)) {
            //Gate is outbound to a buffer, find a pointer
            for (int i = 0; i < Configuration.numberOfBelts; i++) {
                if (PresentSortingMachine.beltArray[i].GetBeltNumber() == mSouthConnection) {
                    //This is the connected belt
                    pBelt = PresentSortingMachine.beltArray[i];
                }
            }

            //Is this an empty belt? Skip over if not
            if (pBelt.BeltEmpty()) {
                //Belt is empty and can be written to
                //Is the turntable pointing the right way?
                if (mCurrentDirection != eDirections.ns) {
                    //Not lined up correctly
                    Rotate();
                }

                //Transfer the present to the buffer
                try {
                    pBelt.Put(mPresent);
                    mPresent = null;
                    try {
                        Thread.sleep(mMoveSpeed);   //Sleep to simulate the present moving
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TurnTable.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (InterruptedException e) {
                }
                return;
            }
        }

        //West gate
        if ("ob".equals(mWest)) {
            //Gate is outbound to a buffer, find a pointer
            for (int i = 0; i < Configuration.numberOfBelts; i++) {
                if (PresentSortingMachine.beltArray[i].GetBeltNumber() == mWestConnection) {
                    //This is the connected belt
                    pBelt = PresentSortingMachine.beltArray[i];
                }
            }

            //Is this an empty belt? Skip over if not
            if (pBelt.BeltEmpty()) {
                //Belt is empty and can be written to
                //Is the turntable pointing the right way?
                if (mCurrentDirection != eDirections.ew) {
                    //Not lined up correctly
                    Rotate();
                }

                //Transfer the present to the buffer
                try {
                    pBelt.Put(mPresent);
                    mPresent = null;
                    try {
                        Thread.sleep(mMoveSpeed);   //Sleep to simulate the present moving
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TurnTable.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (InterruptedException e) {
                }
                return;
            }
        }
    }

    //Try to retrieve a present from an inbound connection, looking clockwise from north
    private void ProcessInbound() {
        //Poll input belts for an available present
        Belt pBelt = null;
        //Try to find a belt that feeds into this turntable AND has an available present to give it
        //North gate
        if ("ib".equals(mNorth)) {
            //Gate is inbound - get a pointer to the belt                   
            for (int i = 0; i < Configuration.numberOfBelts; i++) {
                if (PresentSortingMachine.beltArray[i].GetBeltNumber() == mNorthConnection) {
                    pBelt = PresentSortingMachine.beltArray[i];
                }
            }

            //Does this belt have anything to take?
            if (!pBelt.BeltEmpty()) {
                //Belt has something on it - is the turntable lined up the right way?
                if (mCurrentDirection != eDirections.ns) {
                    //Not lined up correctly - correct this
                    Rotate();
                }

                //Take the present
                try {
                    mPresent = pBelt.Take();
                    try {
                        Thread.sleep(mMoveSpeed);   //Sleep to simulate the present moving
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TurnTable.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (InterruptedException e) {
                    //Present couldn't be taken - the thread is currently locked
                }
                return;
            }
        }

        //East gate
        if ("ib".equals(mEast)) {
            //Gate is inbound - get a pointer to the belt
            for (int i = 0; i < Configuration.numberOfBelts; i++) {
                if (PresentSortingMachine.beltArray[i].GetBeltNumber() == mEastConnection) {
                    pBelt = PresentSortingMachine.beltArray[i];
                }
            }

            //Does this belt have anything to take?
            if (!pBelt.BeltEmpty()) {
                //Belt has something on it - is the turntable lined up the right way?
                if (mCurrentDirection != eDirections.ew) {
                    //Not lined up correctly - correct this
                    Rotate();
                }

                //Take the present
                try {
                    mPresent = pBelt.Take();
                    try {
                        Thread.sleep(mMoveSpeed);   //Sleep to simulate the present moving
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TurnTable.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (InterruptedException e) {
                    //Present couldn't be taken - the thread is currently locked
                }
                return;
            }
        }

        //South gate
        if ("ib".equals(mSouth)) {
            //Gate is inbound - get a pointer to the belt
            for (int i = 0; i < Configuration.numberOfBelts; i++) {
                if (PresentSortingMachine.beltArray[i].GetBeltNumber() == mSouthConnection) {
                    pBelt = PresentSortingMachine.beltArray[i];
                }
            }

            //Does this belt have anything to take?
            if (!pBelt.BeltEmpty()) {
                //Belt has something on it - is the turntable lined up the right way?
                if (mCurrentDirection != eDirections.ns) {
                    //Not lined up correctly - correct this
                    Rotate();
                }

                //Take the present
                try {
                    mPresent = pBelt.Take();
                    try {
                        Thread.sleep(mMoveSpeed);   //Sleep to simulate the present moving
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TurnTable.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return;
                } catch (InterruptedException e) {
                    //Present couldn't be taken - the thread is currently locked
                }
            }
        }

        //West gate
        if ("ib".equals(mWest)) {
            //Gate is inbound - get a pointer to the belt
            for (int i = 0; i < Configuration.numberOfBelts; i++) {
                if (PresentSortingMachine.beltArray[i].GetBeltNumber() == mWestConnection) {
                    pBelt = PresentSortingMachine.beltArray[i];
                }
            }

            //Does this belt have anything to take?
            if (!pBelt.BeltEmpty()) {
                //Belt has something on it - is the turntable lined up the right way?
                if (mCurrentDirection != eDirections.ew) {
                    //Not lined up correctly - correct this
                    Rotate();
                }

                try {
                    //Take the present
                    mPresent = pBelt.Take();
                    try {
                        Thread.sleep(mMoveSpeed);   //Sleep to simulate the present moving
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TurnTable.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (InterruptedException e) {
                    //Present couldn't be taken - the thread is currently locked
                }
                return;
            }
        }
    }
    
    //Issue a shutdown command, allowing the thread to terminate
    public void Shutdown() {
        mShutdown = true;
    }
}
