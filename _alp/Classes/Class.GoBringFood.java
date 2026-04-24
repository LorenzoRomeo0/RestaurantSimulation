/**
 * GoBringFood
 */	
public class GoBringFood {

	 OrderNew order;

    /**
     * Default constructor
     */
    public GoBringFood() {
    }

    /**
     * Constructor initializing the fields
     */
    public GoBringFood(OrderNew order) {
		this.order = order;
    }

	@Override
	public String toString() {
		return  
			"order = " + order +" ";
	}

}