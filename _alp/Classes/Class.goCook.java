/**
 * goCook
 */	
public class goCook {

	 Order order = null;

    /**
     * Default constructor
     */
    public goCook() {
    }

    /**
     * Constructor initializing the fields
     */
    public goCook(Order order) {
		this.order = order;
    }

	@Override
	public String toString() {
		return  
			"order = " + order +" ";
	}

}