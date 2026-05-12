import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.CholeskyDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

public class MenuCopulaGenerator {

	/**
	 * Contiene una realizzazione dell'estrazione. 
	 * Indica quali componenti del pasto (primo, secondo, dolce...) devono essere inclusi nell'ordine e quali no.
	 *
	 */
    public static class CopulaSample {
        private final LinkedHashMap<String, Boolean> values = new LinkedHashMap<>();

        public void put(String variable, boolean value) {
            values.put(variable, value);
        }

        public boolean isOn(String variable) {
            Boolean v = values.get(variable);
            return v != null && v;
        }

        public Map<String, Boolean> getValues() {
            return Collections.unmodifiableMap(values);
        }

        @Override
        public String toString() {
            return values.toString();
        }
    }
    
    /**
     * Classe contenitore delle informazioni
     *
     */
    private static class CopulaModel {
        List<String> variables = new ArrayList<>(); // fixed_menu, course1, ... assumiamo che le variabili delle righe delle matrici siano nello stesso ordine delle variabili nelle colonne.
        double[] marginals;
        double[][] correlation;
        double[][] choleskyL;
        double[] thresholds;
    }

    private final Agent agent;
    private final NormalDistribution stdNormal = new NormalDistribution(0, 1);

    // La mappa DayPart -> CopulaModel contenente un model per momento della giornata
    private final EnumMap<DayPartUtil.DayPart, CopulaModel> models =
            new EnumMap<>(DayPartUtil.DayPart.class);

    
    public MenuCopulaGenerator(Agent agent) {
        this(agent, true);
    }
    
    public MenuCopulaGenerator(Agent agent, boolean autoLoadFromDb) {
        this.agent = agent;
        if (autoLoadFromDb) {
            loadAll();
        }
    }
    
    public CopulaModel getCopulaModel(DayPartUtil.DayPart dayPart) {
    	return models.get(dayPart);
    }    

    /**
     * Restutisce un sample cui le componenti sono correlate secondo la copula.
     * @param dayPart
     * @return CopulaSample sample
     */
    public CopulaSample sample(DayPartUtil.DayPart dayPart) {
        CopulaModel model = models.get(dayPart);
        CopulaSample sample = new CopulaSample();

        if (model == null || model.variables.isEmpty()) {
            return sample;
        }

        // generazione delle normali std indipendenti
        double[] eps = new double[model.variables.size()];
        for (int i = 0; i < eps.length; i++) {
        	eps[i] = agent.normal();
        }

        // introduzione della dipendenza
        double[] z = multiply(model.choleskyL, eps);

        // calcolo dei threshold (0, 1)
        for (int i = 0; i < model.variables.size(); i++) {
            boolean active = z[i] > model.thresholds[i];
            sample.put(model.variables.get(i), active);
        }

        return sample;
    }

    /**
     * Popola un CopulaModel per momento della giornata con i valori presenti 
     * nel database nelle seguenti tabelle:
     * copula_breakfast_correlation
     * copula_break_correlation
     * copula_lunch_correlation
     * copula_dinner_correlation
     * 
     *  è il metodo che va chiamato per inizializzare correttamente la classe.
     */
    private void loadAll() {
        models.clear();

        loadModel(DayPartUtil.DayPart.BREAKFAST, "copula_breakfast_correlation");
        loadModel(DayPartUtil.DayPart.BREAK, "copula_break_correlation");
        loadModel(DayPartUtil.DayPart.LUNCH, "copula_lunch_correlation");
        loadModel(DayPartUtil.DayPart.DINNER, "copula_dinner_correlation");
    }

    
    /**
     * Dato un DayPart e il nome della tabella del database contentente la rispettiva 
     * matrice di correlazione costruisce il copulaModel e lo inserisce nella mappa
     * models.
     * 
     * @param dayPart
     * @param correlationTable
     */
    private void loadModel(DayPartUtil.DayPart dayPart, String correlationTable) {
        CopulaModel model = new CopulaModel();

        loadMarginals(dayPart, model);
        loadCorrelationMatrix(correlationTable, model);
        
        //printModel(dayPart, model);

        if (model.variables.isEmpty()) {
            return;
        }

        if (model.correlation == null || model.correlation.length != model.variables.size()) {
            throw new RuntimeException("Invalid correlation matrix for " + dayPart);
        }

        model.choleskyL = buildCholesky(model.correlation);
        model.thresholds = buildThresholds(model.marginals);

        models.put(dayPart, model);
    }

    
    /**
     * Legge dalla tabella del database copula_menu_marginals e popola model.variabiles e
     * model.probs
     * @param dayPart
     * @param model
     */
    private void loadMarginals(DayPartUtil.DayPart dayPart, CopulaModel model) {
    	java.sql.Connection con = null;
    	java.sql.Statement st = null;
    	java.sql.ResultSet rs = null;

        try {
            con = agent.getEngine().getModelDatabase().getConnection();
            st = con.createStatement();

            rs = st.executeQuery(
                "SELECT variable, marginal_probability " +
                "FROM copula_menu_marginals " +
                "WHERE day_part = '" + dayPart.name() + "'"
            );

            ArrayList<String> variables = new ArrayList<>();
            ArrayList<Double> probs = new ArrayList<>();

            while (rs.next()) {
                variables.add(rs.getString("variable"));
                probs.add(rs.getDouble("marginal_probability"));
            }

            model.variables = variables;
            model.marginals = new double[probs.size()];

            for (int i = 0; i < probs.size(); i++) {
                model.marginals[i] = probs.get(i);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (st != null) st.close(); } catch (Exception e) {}
        }
    }
    
    
    private void printModel(DayPartUtil.DayPart dayPart, CopulaModel model) {
        System.out.println("=== " + dayPart + " ===");
        System.out.println("Variables: " + model.variables);

        for (int i = 0; i < model.correlation.length; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < model.correlation[i].length; j++) {
                sb.append(model.correlation[i][j]).append("\t");
            }
            System.out.println(sb.toString());
        }
    }
    
    
    public String modelPrettyString(DayPartUtil.DayPart dayPart, CopulaModel model) {
        StringBuilder sb = new StringBuilder();

        sb.append("============================================================\n");
        sb.append("DAY PART: ").append(dayPart).append("\n");
        sb.append("============================================================\n");

        if (model == null) {
            sb.append("Model: null\n");
            return sb.toString();
        }

        sb.append("Variables: ").append(model.variables).append("\n\n");

        if (model.marginals != null) {
            sb.append("Marginals\n");
            sb.append(String.format("%-15s %12s%n", "Variable", "P(X=1)"));
            sb.append(String.format("%-15s %12s%n", "--------", "------"));
            for (int i = 0; i < model.variables.size(); i++) {
                String var = model.variables.get(i);
                double p = model.marginals[i];
                sb.append(String.format("%-15s %12.6f%n", var, p));
            }
            sb.append("\n");
        }

        if (model.thresholds != null) {
            sb.append("Thresholds\n");
            sb.append(String.format("%-15s %12s%n", "Variable", "Threshold"));
            sb.append(String.format("%-15s %12s%n", "--------", "---------"));
            for (int i = 0; i < model.variables.size(); i++) {
                String var = model.variables.get(i);
                double t = model.thresholds[i];
                sb.append(String.format("%-15s %12.6f%n", var, t));
          
            }
            sb.append("\n");
        }

        if (model.correlation != null) {
            sb.append("Correlation matrix\n");
            sb.append(String.format("%-15s", ""));
            for (String var : model.variables) {
                sb.append(String.format("%12s", var));
            }
            sb.append("\n");

            for (int i = 0; i < model.correlation.length; i++) {
                sb.append(String.format("%-15s", model.variables.get(i)));
                for (int j = 0; j < model.correlation[i].length; j++) {
                    sb.append(String.format("%12.4f", model.correlation[i][j]));
                }
                sb.append("\n");
            }
            sb.append("\n");
        }

        if (model.choleskyL != null) {
            sb.append("Cholesky L\n");
            sb.append(String.format("%-15s", ""));
            for (String var : model.variables) {
                sb.append(String.format("%12s", var));
            }
            sb.append("\n");

            for (int i = 0; i < model.choleskyL.length; i++) {
                sb.append(String.format("%-15s", model.variables.get(i)));
                for (int j = 0; j < model.choleskyL[i].length; j++) {
                    sb.append(String.format("%12.4f", model.choleskyL[i][j]));
                }
                sb.append("\n");
            }
            sb.append("\n");
        }

        return sb.toString();
    }
    
    /**
     * Dato il nome della tabella del database di un dayPart e carica in model la correlazione letta.
     * Crea la matrice correlation in maniera coerente con l'ordine delle variabili già presente nel model.
     * La model.variabiles deve essere già popolato prima di poter eseguire questo metodo.
     * @param tableName
     * @param model
     */
    private void loadCorrelationMatrix(String tableName, CopulaModel model) {
        Connection con = null;
        java.sql.Statement st = null;
        java.sql.ResultSet rs = null;

        try {
            con = agent.getEngine().getModelDatabase().getConnection();
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM " + tableName);

            ArrayList<double[]> rows = new ArrayList<>();

            while (rs.next()) {
                double[] row = new double[model.variables.size()];

                for (int j = 0; j < model.variables.size(); j++) {
                    String variable = model.variables.get(j);
                    row[j] = rs.getDouble(variable);
                }

                rows.add(row);
            }

            model.correlation = new double[rows.size()][model.variables.size()];
            for (int i = 0; i < rows.size(); i++) {
                model.correlation[i] = rows.get(i);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (st != null) st.close(); } catch (Exception e) {}
        }
    }

    
    /**
     * Esegue la decomposizione di Cholesky (R = LL') e restituisce L come double[][]. 
     * Fallisce se la matrice non è quadrata, simmetrica e definita positiva (autovalori positivi). !!TODO
     * @param correlation
     * @return
     */
    private double[][] buildCholesky(double[][] correlation) {    	
        RealMatrix matrix = new Array2DRowRealMatrix(correlation);
        CholeskyDecomposition chol = new CholeskyDecomposition(matrix);
        RealMatrix l = chol.getL();
        return l.getData();
    }

    /**
     * Costruisce le soglie utilzzate per trasformare i valori continui in [0,1] in N. 
     * Partendo dalle marginali desiderate li calcoliamo calcolando il quantile corrispondente.
     * Dal momento che vogliamo scegliere threshold (t) in modo che la probabilità di stare sopra di essi sia p
     * dobbiamo invertire la cdf della normale std: P(Z>t) = p -> P(Z<=t) = 1-p e poi ricavare t con l'icdf: t = \phi^-1(1-p).
     * p alta, threshold basso e viceversa.
     * @param marginals
     * @return
     */
    private double[] buildThresholds(double[] marginals) {
        double[] thresholds = new double[marginals.length];

        for (int i = 0; i < marginals.length; i++) {
            double p = marginals[i];
            p = Math.max(0.000001, Math.min(0.999999, p)); // per mantenere i valori gestibili
            thresholds[i] = stdNormal.inverseCumulativeProbability(1.0 - p);
        }

        return thresholds;
    }

    
    private double[] multiply(double[][] matrix, double[] vector) {
        RealMatrix m = new Array2DRowRealMatrix(matrix, false);
        return m.operate(vector);
    }
    
    /**
     * Builds a CopulaModel given its building blocks, does not modify the global model map.
     * @param variables
     * @param marginals
     * @param correlation
     * @return
     */
    public CopulaModel buildModelFromArrays(
            List<String> variables,
            double[] marginals,
            double[][] correlation) {

        CopulaModel model = new CopulaModel();
        model.variables = new ArrayList<>(variables);
        model.marginals = marginals;
        model.correlation = correlation;
        model.choleskyL = buildCholesky(correlation);
        model.thresholds = buildThresholds(marginals);

        return model;
    }
    
    /**
     * Data una lista di componenti di CopulaModel restituisce un prettyString 
     * delle copula risultanti. Utilizzato per i dryRun.
     * @param dayPart
     * @param variables
     * @param marginals
     * @param correlation
     * @return
     */
    public String previewPrettyString(
            DayPartUtil.DayPart dayPart,
            List<String> variables,
            double[] marginals,
            double[][] correlation) {

        CopulaModel model = buildModelFromArrays(variables, marginals, correlation);
        return modelPrettyString(dayPart, model);
    }
}