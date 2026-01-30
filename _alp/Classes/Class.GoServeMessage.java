/**
 * GoServeMessage
 */	
public class GoServeMessage {

	public CustomerGroup customerGroup = null;

    /**
     * Default constructor
     */
    public GoServeMessage() {
    }

    /**
     * Constructor initializing the fields
     */
    public GoServeMessage(CustomerGroup customerGroup) {
		this.customerGroup = customerGroup;
    }

	@Override
	public String toString() {
		return  
			"customerGroup = " + customerGroup +" ";
	}

}