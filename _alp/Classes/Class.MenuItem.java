/**
 * MenuItem
 */
public class MenuItem {

    public enum DayPart {
        BREAKFAST,
        BREAK,
        LUNCH,
        DINNER
    }

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
    DayPart dayPart;
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
        return "MenuItem{" +
                "name='" + name + '\'' +
                ", dayPart=" + dayPart +
                ", foodCategory=" + foodCategory +
                ", course=" + course +
                ", popularityWeight=" + popularityWeight +
                ", price=" + price +
                ", prepTimeMeanMin=" + prepTimeMeanMin +
                ", prepTimeSdMin=" + prepTimeSdMin +
                ", complexity=" + complexity +
                ", kitchenStation=" + kitchenStation +
                ", serviceTimeMeanMin=" + serviceTimeMeanMin +
                ", eatingTimeMeanMin=" + eatingTimeMeanMin +
                '}';
    }

}