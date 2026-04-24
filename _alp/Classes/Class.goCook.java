/**
 * goCook
 */	
public class goCook {

	 OrderNew order = null;

    /**
     * Default constructor
     */
    public goCook() {
    }

    /**
     * Constructor initializing the fields
     */
    public goCook(OrderNew order) {
		this.order = order;
    }

	@Override
	public String toString() {
		return  
			"order = " + order +" ";
	}

}