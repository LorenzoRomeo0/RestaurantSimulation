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

    public enum KitchenStation {
        BAR,
        HOT,
        COLD
    }

    String name;
    DayPartUtil.DayPart dayPart;
    FoodCategory foodCategory;
    int course;
    double popularityWeight;
    double price;
    double prepTimeMeanMin;
    double prepTimeSdMin;
    int complexity;
    KitchenStation kitchenStation;
    double serviceTimeMeanMin;
    double eatingTimeMeanMin;

    /**
     * Default constructor
     */
    public MenuItem() {
    }

    @Override
    public String toString() {
        return "MenuItem[" +
                "name=" + name +
                ", dayPart=" + dayPart +
                ", category=" + foodCategory +
                ", course=" + course +
                ", price=" + price +
                ", popularityWeight=" + popularityWeight +
                ", prepMeanMin=" + prepTimeMeanMin +
                ", prepSdMin=" + prepTimeSdMin +
                ", complexity=" + complexity +
                ", station=" + kitchenStation +
                ", serviceMin=" + serviceTimeMeanMin +
                ", eatingMin=" + eatingTimeMeanMin +
                "]";
    }
}