package org.example.demo12;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ComplexFourierCalculator extends Application {

    // Constants for the range of integration and plotting
    private static final double LOWER_BOUND = -Math.PI;
    private static final double UPPER_BOUND = Math.PI;

    // Number of sample points for graph plotting
    private static final int SAMPLE_POINTS = 1000;

    // Main UI components
    private TextArea inputArea;
    private LineChart<Number, Number> originalChart;
    private LineChart<Number, Number> fourierChart;
    private ListView<String> coefficientList;
    private Slider termSlider;

    @Override
    public void start(Stage stage) {
        // Root layout
        BorderPane root = new BorderPane();

        // Input area for the function
        inputArea = new TextArea("sin(x)");
        inputArea.setPrefRowCount(3);

        // Fourier coefficients display
        coefficientList = new ListView<>();
        coefficientList.setPrefWidth(200);
        coefficientList.setPrefHeight(300);

        // Create graphs for the original function and its Fourier approximation
        originalChart = createLineChart("Original Function");
        fourierChart = createLineChart("Fourier Series Approximation");

        // Control panel
        termSlider = new Slider(1, 100, 10);
        termSlider.setShowTickLabels(true);
        termSlider.setShowTickMarks(true);

        Button calculateButton = new Button("Calculate Fourier Coefficients");

        VBox controlPanel = new VBox(10, new Label("Number of Terms"), termSlider, calculateButton);
        controlPanel.setPadding(new Insets(10));

        // Layout
        VBox leftPanel = new VBox(10, new Label("Input Function:"), inputArea, new Label("Fourier Coefficients"), coefficientList);
        HBox chartPanel = new HBox(10, originalChart, fourierChart);
        root.setLeft(leftPanel);
        root.setCenter(chartPanel);
        root.setRight(controlPanel);

        // Calculate button event
        calculateButton.setOnAction(e -> calculateFourierSeries());

        // Final setup
        Scene scene = new Scene(root, 1200, 600);
        stage.setScene(scene);
        stage.setTitle("Complex Fourier Coefficients Calculator");
        stage.show();
    }

    // Creates a LineChart for plotting
    private LineChart<Number, Number> createLineChart(String title) {
        NumberAxis xAxis = new NumberAxis(LOWER_BOUND, UPPER_BOUND, Math.PI / 2);
        NumberAxis yAxis = new NumberAxis();
        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(title);
        chart.setLegendVisible(false);
        return chart;
    }

    // Calculate Fourier coefficients and update the charts
    private void calculateFourierSeries() {
        String functionInput = inputArea.getText();
        Function<Double, Double> function = parseFunction(functionInput);

        int terms = (int) termSlider.getValue();
        List<Pair<Double, Double>> coefficients = computeFourierCoefficients(function, terms);

        // Display the Fourier coefficients
        coefficientList.getItems().clear();
        for (int n = 0; n < terms; n++) {
            coefficientList.getItems().add("a" + n + " = " + coefficients.get(n).getKey() +
                    ", b" + n + " = " + coefficients.get(n).getValue());
        }

        // Update the graphs
        plotFunction(originalChart, function, LOWER_BOUND, UPPER_BOUND);
        plotFourierSeries(fourierChart, coefficients, terms);
    }

    // Function parser (to be improved for complex expressions)
    private Function<Double, Double> parseFunction(String functionInput) {
        return x -> Math.sin(x); // Placeholder, extend this to parse user input
    }

    // Compute Fourier coefficients
    private List<Pair<Double, Double>> computeFourierCoefficients(Function<Double, Double> f, int terms) {
        List<Pair<Double, Double>> coefficients = new ArrayList<>();
        for (int n = 0; n < terms; n++) {
            double a_n = computeAn(f, n);
            double b_n = computeBn(f, n);
            coefficients.add(new Pair<>(a_n, b_n));
        }
        return coefficients;
    }

    // Numerical integration for a_n
    private double computeAn(Function<Double, Double> f, int n) {
        double result = 0.0;
        double dx = (UPPER_BOUND - LOWER_BOUND) / SAMPLE_POINTS;
        for (double x = LOWER_BOUND; x <= UPPER_BOUND; x += dx) {
            result += f.apply(x) * Math.cos(n * x) * dx;
        }
        return result / Math.PI;
    }

    // Numerical integration for b_n
    private double computeBn(Function<Double, Double> f, int n) {
        double result = 0.0;
        double dx = (UPPER_BOUND - LOWER_BOUND) / SAMPLE_POINTS;
        for (double x = LOWER_BOUND; x <= UPPER_BOUND; x += dx) {
            result += f.apply(x) * Math.sin(n * x) * dx;
        }
        return result / Math.PI;
    }

    // Plot the original function
    private void plotFunction(LineChart<Number, Number> chart, Function<Double, Double> f, double lower, double upper) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        double dx = (upper - lower) / SAMPLE_POINTS;
        for (double x = lower; x <= upper; x += dx) {
            series.getData().add(new XYChart.Data<>(x, f.apply(x)));
        }
        chart.getData().clear();
        chart.getData().add(series);
    }

    // Plot the Fourier series approximation
    private void plotFourierSeries(LineChart<Number, Number> chart, List<Pair<Double, Double>> coefficients, int terms) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        double dx = (UPPER_BOUND - LOWER_BOUND) / SAMPLE_POINTS;
        for (double x = LOWER_BOUND; x <= UPPER_BOUND; x += dx) {
            double sum = 0.0;
            for (int n = 0; n < terms; n++) {
                double a_n = coefficients.get(n).getKey();
                double b_n = coefficients.get(n).getValue();
                sum += a_n * Math.cos(n * x) + b_n * Math.sin(n * x);
            }
            series.getData().add(new XYChart.Data<>(x, sum));
        }
        chart.getData().clear();
        chart.getData().add(series);
    }

    public static void main(String[] args) {
        launch();
    }
}
