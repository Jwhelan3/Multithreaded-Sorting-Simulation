package presentsortingmachine;

//The present object is the physical present, which will be passed along the machine
public class Present {

    private String mPresentType;    //The age group that this present is destined for

    //Constructor - sets the destination of the present
    public Present(String presentType) {
        this.mPresentType = presentType;
    }

    //Get the present type currently associated with this object
    public String getPresentType() {
        return mPresentType;
    }
}
