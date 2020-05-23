package consoleView;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Graph creates a data graph of the past 15 inputted values for a data-type.
 * @author Deline Zent
 */
public class Graph extends JPanel {

    private static final long serialVersionUID = 2263971214879981059L;
    private int labelPad = 30;
    private int padding = 20;
    private static int POINT_WIDTH = 14;
    private static final Stroke STROKE = new BasicStroke();
    private Color lineColor = new Color(224,224,224);
    private Color pointColor = new Color(20,43,219);
    private Color gridColor = new Color(200, 200, 200);
    private int numberYDivisions = 6;
    private ArrayList<Double> weatherData;
   
    /**
     * Initializes a graph with an empty set of data.
     */
    public Graph() {
        weatherData = new ArrayList<Double>();
    }
    
    /**
     * Initalizes a graph with data. 
     */
    public Graph(ArrayList<Double> data) {
        weatherData = lastValues(data);
    }

    /**
     * Returns the min weather data value for the weather list.
     * @return the min weather value in range
     */
    public double getMinData() {
        return Collections.min(weatherData);
    }
    
    /**
     * Return the max weather data value for the weather list.
     * @return the max weather value in range.
     */

    public double getMaxData() {
        return Collections.max(weatherData);
    }

    /**
     * Changes the data on the graph to display the new data and repaints the graph. 
     * @param newData is the new data to draw our graph from
     */
    public void setData(ArrayList<Double> newData) {
        this.weatherData = lastValues(newData);
        invalidate();
        this.repaint();
    }

    /**
     * Returns the weather data array list which is being used to graph. 
     */
    public ArrayList<Double> getWeatherData() {
        return weatherData;
    }

    /**
     * Returns a list with only the last 15 historical data points.
     * @param A list of historical data points
     * @return A list of the last 15 elements of data
     */
    public ArrayList<Double> lastValues(ArrayList<Double> data) {
        if (data.size() > 15) {
            ArrayList<Double> tempList = new ArrayList<Double>();
            // Only graph first last 15 points, so we make a new data list
            for (int i = 15; i > 0; i--) {
                tempList.add(data.get(data.size() - i));
            }
            data = tempList;
        }
        return data;
    }
    
    /** 
     * Creates a graph with graph highs and lows based on the max or min the data set. 
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        // Scales graph to fit the dat set
        double xScale = ((double) getWidth() - (3 * padding) - labelPad) / (weatherData.size() - 1);
        double yScale = ((double) getHeight() - 2 * padding - labelPad) / (getMaxData() - getMinData());

        List<Point> graphPoints = new ArrayList<>();
        for (int i = 0; i < weatherData.size(); i++) {
            int x1 = (int) (i * xScale + padding + labelPad);
            int y1 = (int) ((getMaxData() - weatherData.get(i)) * yScale + padding);
            graphPoints.add(new Point(x1, y1));
        }

        g2.setColor(Color.WHITE);
       
        g2.fillRect(padding + labelPad, padding, getWidth() - (2 * padding) - 
                labelPad, getHeight() - 2 * padding - labelPad);
        g2.setColor(Color.BLUE);

        for (int i = 0; i < numberYDivisions + 1; i++) {
            int x0 = padding + labelPad;
            int x1 = POINT_WIDTH + padding + labelPad;
            int y0 = getHeight() - ((i * (getHeight() - padding * 2 -
                    labelPad)) / numberYDivisions + padding + labelPad);
            int y1 = y0;
            if (weatherData.size() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(padding + labelPad + 1 + POINT_WIDTH, y0, getWidth() - padding, y1);
                g2.setColor(Color.BLACK);
                String yLabel = ((int) ((getMinData() + (getMaxData() - getMinData()) * 
                        ((i * 8.0) / numberYDivisions)) * 100)) / 100.0 + "";
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(yLabel);
                g2.drawString(yLabel, x0 - labelWidth - 6, y0 + (metrics.getHeight() / 2) - 3);
            }
            g2.drawLine(x0, y0, x1, y1);
        }

        for (int i = 0; i < weatherData.size(); i++) {
            if (weatherData.size() > 1) {
                int x0 = i * (getWidth() - padding * 2 - labelPad) / (weatherData.size() - 1) + padding + labelPad;
                int x1 = x0;
                int y0 = getHeight() - padding - labelPad;
                int y1 = y0 - POINT_WIDTH;
                if ((i % ((int) ((weatherData.size() / 8.0)) + 3)) == 0) { 
                    g2.setColor(gridColor);
                    g2.drawLine(x0, getHeight() - padding - labelPad - 1 - POINT_WIDTH, x1, padding);
                    g2.setColor(Color.BLACK);
                    String xLabel = i + "";
                    FontMetrics metrics = g2.getFontMetrics();
                    int labelWidth = metrics.stringWidth(xLabel);
                    g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
                }
                g2.drawLine(x0, y0, x1, y1);
            }
        }
        
        g2.drawLine(padding + labelPad, getHeight() - padding - labelPad, padding + labelPad, padding);
        g2.drawLine(padding + labelPad, getHeight() - padding - labelPad, getWidth() -
                padding, getHeight() - padding - labelPad);

        Stroke oldStroke = g2.getStroke();
        g2.setColor(lineColor);
        g2.setStroke(STROKE);
        for (int i = 0; i < graphPoints.size() - 1; i++) {
            int x1 = graphPoints.get(i).x;
            int y1 = graphPoints.get(i).y;
            int x2 = graphPoints.get(i + 1).x;
            int y2 = graphPoints.get(i + 1).y;
            g2.drawLine(x1, y1, x2, y2);
        }

        g2.setStroke(oldStroke);
        g2.setColor(pointColor);
        for (int i = 0; i < graphPoints.size(); i++) {
            int x = graphPoints.get(i).x - POINT_WIDTH / 2;
            int y = graphPoints.get(i).y - POINT_WIDTH / 2;
            int ovalW = POINT_WIDTH;
            int ovalH = POINT_WIDTH;
            g2.fillOval(x, y, ovalW, ovalH);
        }
    }
    
    /**
     * Initializes practice values to test the GUI graph.
     */
    private static void initializeTestValues() {
        ArrayList<Double> data = new ArrayList<>();
        Random random = new Random();
        int maxDataPoints = 20;
        int maxScore = 8;
        for (int i = 0; i < maxDataPoints; i++) {
            data.add((double) random.nextDouble() * maxScore);
        }
        Graph mainPanel = new Graph(data);
        mainPanel.setPreferredSize(new Dimension(700, 600));
        JFrame frame = new JFrame("Data Graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
     /**
     * Main method to create the GUI based on practice values.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                initializeTestValues();
            }
        });
    }
}

/**
 * CITATIONS:
 * Java Swing Tutorial: https://www.javatpoint.com/java-swing
 * How to Plot a Graph: https://kodejava.org/how-do-i-draw-plot-a-graph/
 * 2D Graphics Using Java Swing Utilities:
 * https://www.youtube.com/watch?v=wJY43Ml6vcQ
 * Finding Mins and Max in an ArrayList: 
 * https://www.javacodeexamples.com/find-minimum-maximum-value-in-arraylist/1010
 */
