/**
 * Order
 */	
public class Order {

	public CustomerGroup CustomerGroup = null;


	public short complexity = 0;

	public short desserts = 0;

	public short coffees = 0;

    /**
     * Default constructor
     */
    public Order() {
    }

    /**
     * Constructor initializing the fields
     */
    public Order(CustomerGroup CustomerGroup, short complexity, short desserts, short coffees) {
		this.CustomerGroup = CustomerGroup;
		this.complexity = complexity;
		this.desserts = desserts;
		this.coffees = coffees;
    }

	@Override
	public String toString() {
		return  
			"CustomerGroup = " + CustomerGroup +" " +
			"complexity = " + complexity +" " +
			"desserts = " + desserts +" " +
			"coffees = " + coffees +" ";
	}

}