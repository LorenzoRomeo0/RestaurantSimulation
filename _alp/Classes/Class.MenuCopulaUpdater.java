import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

public class MenuCopulaUpdater {

	/**
	 * Classe contenitore
	 * @author loren
	 *
	 */
    public static class SyntheticOrder {
        public String dayPart;
        public Map<String, Integer> items = new HashMap<>();

        public SyntheticOrder(String dayPart) {
            this.dayPart = dayPart;
        }

        public SyntheticOrder with(String variable, int value) {
            items.put(variable, value);
            return this;
        }

        public int get(String variable) {
            Integer v = items.get(variable);
            return v == null ? 0 : v;
        }

        @Override
        public String toString() {
            return "SyntheticOrder{" +
                   "dayPart='" + dayPart + '\'' +
                   ", items=" + new TreeMap<>(items) +
                   '}';
        }
    }

    private final Agent agent;

    
    //TODO: rendere dinamici (?)
    private final Map<String, List<String>> varsByDayPart = new LinkedHashMap<String, List<String>>() {{
        put("BREAKFAST", Arrays.asList("main", "drink", "combo"));
        put("BREAK", Arrays.asList("main", "drink"));
        put("LUNCH", Arrays.asList("fixed_menu", "course1", "course2", "side", "drink", "dessert"));
        put("DINNER", Arrays.asList("fixed_menu", "course1", "course2", "side", "drink", "dessert"));
    }};

    private final Map<String, String> corrTableByDayPart = new LinkedHashMap<String, String>() {{
        put("BREAKFAST", "copula_breakfast_correlation");
        put("BREAK", "copula_break_correlation");
        put("LUNCH", "copula_lunch_correlation");
        put("DINNER", "copula_dinner_correlation");
    }};

    public MenuCopulaUpdater(Agent agent) {
        this.agent = agent;
    }

    /*
    public Map<String, List<String>> getVarsByDayPart() {
        return varsByDayPart;
    }
    */

    /**
     * Given a List<SyntheticOrder> grups into a Map<String, List<SyntheticOrder>> by dayPart
     * @param allOrders
     * @return
     */
    public Map<String, List<SyntheticOrder>> splitByDayPart(List<SyntheticOrder> allOrders) {
        Map<String, List<SyntheticOrder>> grouped = new LinkedHashMap<>();
        for (String dp : varsByDayPart.keySet()) {
            grouped.put(dp, new ArrayList<>());
        }

        for (SyntheticOrder o : allOrders) {
            if (o != null && o.dayPart != null && grouped.containsKey(o.dayPart)) {
                grouped.get(o.dayPart).add(o);
            }
        }
        return grouped;
    }

    
    /**
     * Coputes empirical marginals for every variable
     * @param orders
     * @param vars
     * @return
     */
    public Map<String, Double> computeMarginals(List<SyntheticOrder> orders, List<String> vars) {
        Map<String, Double> res = new LinkedHashMap<>();
        int n = orders == null ? 0 : orders.size();

        if (n == 0) {
            for (String v : vars) {
                res.put(v, 0.0);
            }
            return res;
        }

        for (String v : vars) {
            double sum = 0.0;
            for (SyntheticOrder o : orders) {
                sum += o.get(v);
            }
            res.put(v, sum / n);
        }
        return res;
    }
    
    
    /**
     * Coputes the covariance between the variables
     * @param orders
     * @param vars
     * @return
     */
    /*
    public double[][] computeCovarianceMatrix(List<SyntheticOrder> orders, List<String> vars) {
        if (orders == null || orders.size() < 2) {
            return new double[vars.size()][vars.size()];
        }

        double[][] data = buildDataMatrix(orders, vars);
        RealMatrix rm = new Covariance(data, true).getCovarianceMatrix();
        return rm.getData();
    }
    */

    
    /**
     * computes the correlation (normalized covariance) between the variables 
     * @param orders
     * @param vars
     * @return a square matrix of size vars.size
     */
    public double[][] computeCorrelationMatrix(List<SyntheticOrder> orders, List<String> vars) {
        int m = vars.size();
        
        if (orders == null || orders.size() < 2) {
            double[][] empty = new double[m][m];
            for (int i = 0; i < m; i++) empty[i][i] = 1.0;
            return empty;
        }

        double[][] data = buildDataMatrix(orders, vars); //double[orders.size][vars.size]
        RealMatrix rm = new PearsonsCorrelation().computeCorrelationMatrix(data);
        return sanitizeCorrelationMatrix(rm.getData());
    }
    
    

    /**
     * Costruisce una matrice con gli ordini sulle righe con variabili sulle colonne ordinate secondo vars.
     * @param orders
     * @param vars
     * @return
     */
    private double[][] buildDataMatrix(List<SyntheticOrder> orders, List<String> vars) {
        int n = orders.size();
        int m = vars.size();
        double[][] data = new double[n][m];

        for (int i = 0; i < n; i++) {
            SyntheticOrder o = orders.get(i);
            for (int j = 0; j < m; j++) {
                data[i][j] = o.get(vars.get(j));
            }
        }
        return data;
    }

    /**
     * sets to 0 nan or inf values
     * @param mat
     * @return
     */
    private double[][] sanitizeCorrelationMatrix(double[][] mat) {
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[i].length; j++) {
                if (i == j) {
                    mat[i][j] = 1.0;
                } else if (Double.isNaN(mat[i][j]) || Double.isInfinite(mat[i][j])) {
                    mat[i][j] = 0.0;
                }
            }
        }
        return mat;
    }

    //TODO: leggere dalla mappa creata prima i nomi delle tabelle
    /**
     * Empties copula_menu_marginals, copula_breakfast_correlation, copula_break_correlation, 
     * copula_lunch_correlation, copula_dinner_correlation db tables.
     */
    public void clearAllCopulaTables() {
        java.sql.Connection con = null;
        java.sql.Statement st = null;

        try {
            con = agent.getEngine().getModelDatabase().getConnection();
            st = con.createStatement();

            st.executeUpdate("DELETE FROM copula_menu_marginals");
            st.executeUpdate("DELETE FROM copula_breakfast_correlation");
            st.executeUpdate("DELETE FROM copula_break_correlation");
            st.executeUpdate("DELETE FROM copula_lunch_correlation");
            st.executeUpdate("DELETE FROM copula_dinner_correlation");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (st != null) st.close(); } catch (Exception e) {}
        }
    }
    
    /**
     * Renames the "copula_menu_marginals",
     *       "copula_breakfast_correlation",
     *       "copula_break_correlation",
     *       "copula_lunch_correlation",
     *       "copula_dinner_correlation" tables as tablename_yyyyMMdd_HHmmss
     */
    public void backupAndDeleteAllCopulaTables() {
        java.sql.Connection con = null;
        java.sql.Statement st = null;

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String[] tableNames = new String[] {
            "copula_menu_marginals",
            "copula_breakfast_correlation",
            "copula_break_correlation",
            "copula_lunch_correlation",
            "copula_dinner_correlation"
        };

        try {
            con = agent.getEngine().getModelDatabase().getConnection();
            st = con.createStatement();

            for (String tableName : tableNames) {
                String backupTableName = "bak_" + timestamp + "_" + tableName;
                st.executeUpdate("ALTER TABLE " + tableName + " RENAME TO " + backupTableName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (st != null) st.close(); } catch (Exception e) {}
        }
    }
    
    /**
     * Metodo statico che crea una copia di backup delle copula tables aggiungendovi il timestamp corrente. 
     * Non rimuove le tabelle già esistenti.
     */
    public static boolean backupAllCopulaTables(Agent agent, boolean isManual) {
        java.sql.Connection con = null;
        java.sql.Statement st = null;

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String[] tableNames = new String[] {
            "copula_menu_marginals",
            "copula_breakfast_correlation",
            "copula_break_correlation",
            "copula_lunch_correlation",
            "copula_dinner_correlation"
        };

        try {
            con = agent.getEngine().getModelDatabase().getConnection();
            st = con.createStatement();

            for (String tableName : tableNames) {
                String backupTableName = (isManual ? "manbak_" : "bak_") + timestamp + "_" + tableName;

                st.executeUpdate(
                    "CREATE TABLE " + backupTableName +
                    " AS (SELECT * FROM " + tableName + ") WITH DATA"
                );
                /*
                st.executeUpdate(
                	"ALTER TABLE " + backupTableName + " DROP COLUMN al_id"
                );
                */
                String columnName = "AL_ID";
                java.sql.DatabaseMetaData md = con.getMetaData();
                java.sql.ResultSet rs = null;
                rs = md.getColumns(null, null, tableName.toUpperCase(), columnName);

                if (rs.next()) {
                    st = con.createStatement();
                    st.executeUpdate("ALTER TABLE " + tableName + " DROP COLUMN " + columnName);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (st != null) st.close(); } catch (Exception e) {}
        }
        return true;
    }

    
    /**
     * Inserts, for a specific dayPart, all the associated marginals into the copula_menu_marginals table.
     * @param dayPart
     * @param marginals
     */
    public void updateMarginalsTable(String dayPart, Map<String, Double> marginals) {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = agent.getEngine().getModelDatabase().getConnection();
            ps = con.prepareStatement(
                "INSERT INTO copula_menu_marginals(day_part, variable, marginal_probability) VALUES (?,?,?)"
            );

            for (Map.Entry<String, Double> e : marginals.entrySet()) {
                ps.setString(1, dayPart);
                ps.setString(2, e.getKey());
                ps.setDouble(3, e.getValue());
                ps.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (ps != null) ps.close(); } catch (Exception e) {}
        }
    }

    /**
     * Aggiorna la tabella del database inserendo una riga per ogni riga di mat ed una colonna per ogni elemento di vars.
     * @param tableName
     * @param vars
     * @param mat
     */
    /*
    public void updateCorrelationTableWide(String tableName, List<String> vars, double[][] mat) {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = agent.getEngine().getModelDatabase().getConnection();

            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO ").append(tableName).append(" (");
            for (int i = 0; i < vars.size(); i++) {
                if (i > 0) sql.append(", ");
                sql.append(vars.get(i));
            }
            sql.append(") VALUES (");
            for (int i = 0; i < vars.size(); i++) {
                if (i > 0) sql.append(", ");
                sql.append("?");
            }
            sql.append(")");

            ps = con.prepareStatement(sql.toString());

            for (int i = 0; i < vars.size(); i++) {
                for (int j = 0; j < vars.size(); j++) {
                    ps.setDouble(j + 1, mat[i][j]);
                }
                ps.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (ps != null) ps.close(); } catch (Exception e) {}
        }
    }*/
    public void updateCorrelationTableWide(String tableName, List<String> vars, double[][] mat) {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = agent.getEngine().getModelDatabase().getConnection();

            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO ").append(tableName).append(" (variable");

            for (int i = 0; i < vars.size(); i++) {
                sql.append(", ").append(vars.get(i));
            }

            sql.append(") VALUES (?");

            for (int i = 0; i < vars.size(); i++) {
                sql.append(", ?");
            }

            sql.append(")");

            ps = con.prepareStatement(sql.toString());

            for (int i = 0; i < vars.size(); i++) {
                ps.setString(1, vars.get(i));

                for (int j = 0; j < vars.size(); j++) {
                    ps.setDouble(j + 2, mat[i][j]);
                }

                ps.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (ps != null) ps.close(); } catch (Exception e) {}
        }
    }

    
    /**
     * Orchestra l'aggiornamento delle tabelle delle copulas. Rimuove tutte le tabelle (effettuandone un backup) ed
     * calcola le nuove matrici e le inserisce nelle rispettive tabelle.
     * @param
     */
    public void rebuildCopulaTables(List<SyntheticOrder> allOrders) {
    	backupAllCopulaTables(agent, false);
        clearAllCopulaTables();
    	//backupAndDeleteAllCopulaTables();
        Map<String, List<SyntheticOrder>> grouped = splitByDayPart(allOrders);

        for (String dayPart : grouped.keySet()) {
            List<SyntheticOrder> orders = grouped.get(dayPart);
            List<String> vars = varsByDayPart.get(dayPart);

            Map<String, Double> marginals = computeMarginals(orders, vars);
            updateMarginalsTable(dayPart, marginals);

            double[][] corr = computeCorrelationMatrix(orders, vars);
            updateCorrelationTableWide(corrTableByDayPart.get(dayPart), vars, corr);
        }
    }

    
    /*
    public List<SyntheticOrder> loadOrdersFromHistoryTable(String historyTableName) {
        List<SyntheticOrder> orders = new ArrayList<>();
        java.sql.Connection con = null;
        java.sql.Statement st = null;
        java.sql.ResultSet rs = null;

        try {
            con = agent.getEngine().getModelDatabase().getConnection();
            st = con.createStatement();
            rs = st.executeQuery(
                "SELECT order_id, dayPart, variable, value FROM " + historyTableName + " ORDER BY order_id"
            );

            Map<String, SyntheticOrder> byId = new LinkedHashMap<>();

            while (rs.next()) {
                String orderId = rs.getString("order_id");
                String dayPart = rs.getString("dayPart");
                String variable = rs.getString("variable");
                int value = rs.getInt("value");

                SyntheticOrder o = byId.get(orderId);
                if (o == null) {
                    o = new SyntheticOrder(dayPart);
                    byId.put(orderId, o);
                }
                o.items.put(variable, value);
            }

            orders.addAll(byId.values());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (st != null) st.close(); } catch (Exception e) {}
        }

        return orders;
    }
    

    public void updateFromHistory(String historyTableName) {
        List<SyntheticOrder> orders = loadOrdersFromHistoryTable(historyTableName);
        rebuildCopulaTables(orders);
    }
    */

    
    /**
     * Dato un csv contenente una cronologia di ordini crea una lista di SyntheticOrders.
     * @param csvPath
     * @return
     */
    public List<SyntheticOrder> loadOrdersFromCsv(String csvPath) {
        List<SyntheticOrder> orders = new ArrayList<>();

        try (
            Reader reader = Files.newBufferedReader(Paths.get(csvPath));
            CSVParser csvParser = new CSVParser(
                reader,
                CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim()
            )
        ) {
            for (CSVRecord record : csvParser) {
                String dayPart = record.get("dayPart");
                if (dayPart == null || dayPart.isEmpty()) continue;
                if (!varsByDayPart.containsKey(dayPart)) continue;

                SyntheticOrder o = new SyntheticOrder(dayPart);

                for (String var : varsByDayPart.get(dayPart)) {
                    String raw = record.isMapped(var) ? record.get(var) : "";
                    o.items.put(var, parseBinary(raw));
                }

                orders.add(o);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while loading CSV: " + csvPath, e);
        }

        return orders;
    }

    /**
     * Dato un csv aggiorna le tabelle del database delle copulas.
     * @param csvPath
     */
    public boolean updateFromCsv(String csvPath) {
        List<SyntheticOrder> orders = loadOrdersFromCsv(csvPath);
        rebuildCopulaTables(orders);
        return true;
    }

    /**
     * Converte "0", "1", "1.0" in int.
     * @param s
     * @return
     */
    private int parseBinary(String s) {
        if (s == null) return 0;
        s = s.trim();
        if (s.isEmpty()) return 0;
        if (s.equals("1") || s.equals("1.0")) return 1;
        return 0;
    }
    
    /**
     * Restituisce un prettyString dato un dayPart delle copule risultanti da una lista di SyntheticOrders. 
     * Non modifica i dati già caricati sul db.
     * @param dayPart
     * @param allOrders
     * @param menuCopulaGenerator
     * @return
     */
    public String dryRunPrettyString(
            DayPartUtil.DayPart dayPart,
            List<SyntheticOrder> allOrders,
            MenuCopulaGenerator menuCopulaGenerator) {

        if (dayPart == null || allOrders == null || menuCopulaGenerator == null) {
            return "Model: null\n";
        }

        String dayPartName = dayPart.name();

        Map<String, List<SyntheticOrder>> grouped = splitByDayPart(allOrders);
        List<SyntheticOrder> orders = grouped.get(dayPartName);
        List<String> vars = varsByDayPart.get(dayPartName);

        Map<String, Double> marginalsMap = computeMarginals(orders, vars);
        double[][] correlation = computeCorrelationMatrix(orders, vars);

        double[] marginals = new double[vars.size()];
        for (int i = 0; i < vars.size(); i++) {
            Double p = marginalsMap.get(vars.get(i));
            marginals[i] = p == null ? 0.0 : p;
        }

        return menuCopulaGenerator.previewPrettyString(
            dayPart,
            vars,
            marginals,
            correlation
        );
    }
}
