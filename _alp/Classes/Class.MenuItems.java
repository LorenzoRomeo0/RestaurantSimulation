import java.sql.Connection;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

import com.anylogic.engine.Agent;

/**
 * MenuItems
 */
public class MenuItems {

    Main main;
    ArrayList<MenuItem> menuItems = new ArrayList<>();
    EnumMap<DayPartUtil.DayPart, ArrayList<MenuItem>> itemsByDayPart;
    MenuCopulaGenerator copulaGenerator;

    private final Random rnd = new Random();
  
    public MenuItems(Main main) {
        this.main = main;
        //copulaGenerator = new MenuCopulaGenerator(main);
        copulaGenerator = main.menuCopulaGenerator;
        loadFromBuiltInDb();
        itemsByDayPart = getItemsByDayPart();
    }

    public void reload() {
        loadFromBuiltInDb();
        itemsByDayPart = getItemsByDayPart();
    }

    public ArrayList<MenuItem> getMenuItems() {
        return menuItems;
    }

    private void loadFromBuiltInDb() {
        menuItems.clear();

        Connection con = null;
        java.sql.Statement st = null;
        java.sql.ResultSet rs = null;

        try {
            con = main.getEngine().getModelDatabase().getConnection();
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM menu_items");

            while (rs.next()) {
                MenuItem item = new MenuItem();

                item.name = rs.getString("name");
                item.dayPart = DayPartUtil.DayPart.valueOf(rs.getString("day_part"));
                item.foodCategory = MenuItem.FoodCategory.valueOf(rs.getString("category"));
                item.course = rs.getInt("course");
                item.popularityWeight = rs.getDouble("popularity_weight");
                item.price = rs.getDouble("price");
                item.prepTimeMeanMin = rs.getDouble("prep_time_mean_min");
                item.prepTimeSdMin = rs.getDouble("prep_time_sd_min");
                //item.complexity = rs.getInt("complexity");
                //item.kitchenStation = MenuItem.KitchenStation.valueOf(rs.getString("kitchen_station"));
                //item.serviceTimeMeanMin = rs.getDouble("service_time_mean_min");
                //item.eatingTimeMeanMin = rs.getDouble("eating_time_mean_min");

                menuItems.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (st != null) st.close(); } catch (Exception e) {}
        }
    }

    private EnumMap<DayPartUtil.DayPart, ArrayList<MenuItem>> getItemsByDayPart() {
        EnumMap<DayPartUtil.DayPart, ArrayList<MenuItem>> grouped =
                new EnumMap<>(DayPartUtil.DayPart.class);

        for (DayPartUtil.DayPart dp : DayPartUtil.DayPart.values()) {
            grouped.put(dp, new ArrayList<MenuItem>());
        }

        for (MenuItem item : menuItems) {
            if (item != null && item.dayPart != null) {
                grouped.get(item.dayPart).add(item);
            }
        }

        return grouped;
    }

    public ArrayList<MenuItem> getItemsByDayPart(DayPartUtil.DayPart dayPart) {
        return itemsByDayPart.get(dayPart);
    }

    public MenuItem getRandomMenuItemByDayPart(DayPartUtil.DayPart dayPart) {
        return weightedChoice(getItemsByDayPart(dayPart));
    }

    public Order generateOrderByDayPart(DayPartUtil.DayPart dayPart) {
        //OrderNew order = new OrderNew();
    	Order order = main.add_orders();

        ArrayList<MenuItem> candidates = getItemsByDayPart(dayPart);
        if (candidates == null || candidates.isEmpty()) {
            return order;
        }

        switch (dayPart) {
            case BREAKFAST:
                generateBreakfast(order, candidates);
                break;

            case BREAK:
                generateBreak(order, candidates);
                break;

            case LUNCH:
                generateLunch(order, candidates);
                break;

            case DINNER:
                generateDinner(order, candidates);
                break;

            case CLOSED:
            default:
                break;
        }

        ensureAtLeastOneItem(order, candidates);
        return order;
    }
    
    public Order generateGroupOrderByDayPart(DayPartUtil.DayPart dayPart, int groupSize) {
        Order groupOrder = main.add_orders();

        if (groupSize <= 0) {
            return groupOrder;
        }

        for (int i = 0; i < groupSize; i++) {
            Order tempOrder = generateOrderByDayPart(dayPart);

            if (tempOrder.menuItems != null && !tempOrder.menuItems.isEmpty()) {
                groupOrder.menuItems.addAll(tempOrder.menuItems);
            }

            main.remove_orders(tempOrder);
            // oppure: tempOrder.remove(); se Order è un agente e lo rimuovi dal pool
        }

        return groupOrder;
    }

    private void generateBreakfast(Order order, List<MenuItem> candidates) {
        MenuCopulaGenerator.CopulaSample sample =
                copulaGenerator.sample(DayPartUtil.DayPart.BREAKFAST);
        
        //traceln(sample);
        //traceln("comboon: "+ (sample.isOn("combo")? "on":"off"));

        if (sample.isOn("combo")) {
            addIfAbsent(order, weightedChoiceByCategory(candidates, MenuItem.FoodCategory.COMBO));
            return;
        }

        if (sample.isOn("main")) {
            addIfAbsent(order, weightedChoiceByCourse(candidates, 1));
        }

        if (sample.isOn("drink")) {
            addIfAbsent(order, weightedChoiceByCategory(candidates, MenuItem.FoodCategory.DRINK));
        }
    }
    
    private void generateBreak(Order order, List<MenuItem> candidates) {
        MenuCopulaGenerator.CopulaSample sample =
                copulaGenerator.sample(DayPartUtil.DayPart.BREAK);

        if (sample.isOn("main")) {
            addIfAbsent(order, weightedChoiceByCourse(candidates, 1));
        }

        if (sample.isOn("drink")) {
            addIfAbsent(order, weightedChoiceByCategory(candidates, MenuItem.FoodCategory.DRINK));
        }
    }
    
    private void generateLunch(Order order, List<MenuItem> candidates) {
        MenuCopulaGenerator.CopulaSample sample =
                copulaGenerator.sample(DayPartUtil.DayPart.LUNCH);
        
        traceln(sample);

        if (sample.isOn("fixed_menu")) {
            addIfAbsent(order, weightedChoiceByCategory(candidates, MenuItem.FoodCategory.FIXED_MENU));
            return;
        }

        if (sample.isOn("course1")) {
            addIfAbsent(order, weightedChoiceByCourse(candidates, 1));
        }

        if (sample.isOn("course2")) {
            addIfAbsent(order, weightedChoiceByCourse(candidates, 2));
        }

        if (sample.isOn("side")) {
            addIfAbsent(order, weightedChoiceByCategory(candidates, MenuItem.FoodCategory.SIDE));
        }

        if (sample.isOn("drink")) {
            addIfAbsent(order, weightedChoiceByCategory(candidates, MenuItem.FoodCategory.DRINK));
        }

        if (sample.isOn("dessert")) {
            addIfAbsent(order, weightedChoiceByCategory(candidates, MenuItem.FoodCategory.DESSERT));
        }
    }
    
    private void generateDinner(Order order, List<MenuItem> candidates) {
        MenuCopulaGenerator.CopulaSample sample =
                copulaGenerator.sample(DayPartUtil.DayPart.DINNER);

        if (sample.isOn("fixed_menu")) {
            addIfAbsent(order, weightedChoiceByCategory(candidates, MenuItem.FoodCategory.FIXED_MENU));
            return;
        }

        if (sample.isOn("course1")) {
            addIfAbsent(order, weightedChoiceByCourse(candidates, 1));
        }

        if (sample.isOn("course2")) {
            addIfAbsent(order, weightedChoiceByCourse(candidates, 2));
        }

        if (sample.isOn("side")) {
            addIfAbsent(order, weightedChoiceByCategory(candidates, MenuItem.FoodCategory.SIDE));
        }

        if (sample.isOn("drink")) {
            addIfAbsent(order, weightedChoiceByCategory(candidates, MenuItem.FoodCategory.DRINK));
        }

        if (sample.isOn("dessert")) {
            addIfAbsent(order, weightedChoiceByCategory(candidates, MenuItem.FoodCategory.DESSERT));
        }
    }

    private void ensureAtLeastOneItem(Order order, List<MenuItem> candidates) {
        if (order.menuItems.isEmpty()) {
            addIfAbsent(order, weightedChoice(candidates));
        }
    }

    private void addIfAbsent(Order order, MenuItem item) {
        if (item == null) {
            return;
        }

        for (MenuItem existing : order.menuItems) {
            if (existing == item) {
                return;
            }
            if (existing != null && existing.name != null && existing.name.equals(item.name)) {
                return;
            }
        }

        order.menuItems.add(item);
    }

    private MenuItem weightedChoiceByCourse(List<MenuItem> items, int course) {
        ArrayList<MenuItem> filtered = new ArrayList<>();
        for (MenuItem item : items) {
            if (item != null && item.course == course) {
                filtered.add(item);
            }
        }
        return weightedChoice(filtered);
    }

    private MenuItem weightedChoiceByCategory(List<MenuItem> items, MenuItem.FoodCategory category) {
        ArrayList<MenuItem> filtered = new ArrayList<>();
        for (MenuItem item : items) {
            if (item != null && item.foodCategory == category) {
                filtered.add(item);
            }
        }
        return weightedChoice(filtered);
    }

    private MenuItem weightedChoice(List<MenuItem> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }

        double totalWeight = 0.0;
        for (MenuItem item : items) {
            if (item != null && item.popularityWeight > 0) {
                totalWeight += item.popularityWeight;
            }
        }

        if (totalWeight <= 0) {
            return items.get(rnd.nextInt(items.size()));
        }

        double r = rnd.nextDouble() * totalWeight;
        double cumulative = 0.0;

        for (MenuItem item : items) {
            if (item != null && item.popularityWeight > 0) {
                cumulative += item.popularityWeight;
                if (r <= cumulative) {
                    return item;
                }
            }
        }

        return items.get(items.size() - 1);
    }

    @Override
    public String toString() {
        return "menuItems = " + menuItems + " ";
    }

    public static String prettyPrintMenuItems(ArrayList<MenuItem> items) {
        StringBuilder sb = new StringBuilder();

        if (items == null) {
            return "MenuItems: null";
        }

        sb.append("========================================\n");
        sb.append("MENU ITEMS  | count = ").append(items.size()).append("\n");
        sb.append("========================================\n");

        if (items.isEmpty()) {
            sb.append("(empty)\n");
            return sb.toString();
        }

        for (int i = 0; i < items.size(); i++) {
            MenuItem item = items.get(i);

            sb.append("#").append(i + 1).append("\n");

            if (item == null) {
                sb.append("  null\n");
                sb.append("----------------------------------------\n");
                continue;
            }

            sb.append("  Name        : ").append(item.name).append("\n");
            sb.append("  DayPart     : ").append(item.dayPart).append("\n");
            sb.append("  Category    : ").append(item.foodCategory).append("\n");
            sb.append("  Course      : ").append(item.course).append("\n");
            sb.append("  Price       : ").append(item.price).append("\n");
            sb.append("  Popularity  : ").append(item.popularityWeight).append("\n");
            sb.append("  Prep mean   : ").append(item.prepTimeMeanMin).append(" min\n");
            sb.append("  Prep sd     : ").append(item.prepTimeSdMin).append(" min\n");
            //sb.append("  Complexity  : ").append(item.complexity).append("\n");
            //sb.append("  Station     : ").append(item.kitchenStation).append("\n");
            //sb.append("  Service     : ").append(item.serviceTimeMeanMin).append(" min\n");
            //sb.append("  Eating      : ").append(item.eatingTimeMeanMin).append(" min\n");
            sb.append("----------------------------------------\n");
        }

        return sb.toString();
    }
}