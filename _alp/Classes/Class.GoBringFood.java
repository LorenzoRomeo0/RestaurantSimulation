/**
 * GoBringFood
 */	
public class GoBringFood {

	 Order order;

    /**
     * Default constructor
     */
    public GoBringFood() {
    }

    /**
     * Constructor initializing the fields
     */
    public GoBringFood(Order order) {
		this.order = order;
    }

	@Override
	public String toString() {
		return  
			"order = " + order +" ";
	}

}