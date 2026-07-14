/**
 * MenuItem
 */
public class MenuItem {

    public enum FoodCategory {
        DRINK,
        MAIN,
        COMBO,
        SIDE,
        DESSERT,
        FIXED_MENU
    }

    String name;
    DayPartUtil.DayPart dayPart;
    FoodCategory foodCategory;
    int course;
    double popularityWeight;
    double price;
    double prepTimeMeanMin;
    double prepTimeSdMin;
    /**
     * Default constructor
     */
    public MenuItem() {
    }

    @Override
    public String toString() {
        return "MenuItem[" +
                "\nname=" + name +
                //", \ndayPart=" + dayPart +
                ", \ncategory=" + foodCategory +
                ", \ncourse=" + course +
                //", price=" + price +
                //", popularityWeight=" + popularityWeight +
                //", prepMeanMin=" + prepTimeMeanMin +
                //", prepSdMin=" + prepTimeSdMin +
                "]\n";
    }
}