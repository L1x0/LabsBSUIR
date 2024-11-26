package by.astakhau.otis;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.util.*;

import javafx.application.Platform;

public class HelloController {
    @FXML
    private Pane graphPane;

    @FXML
    private Label statusLabel;

    @FXML
    private TextArea infoArea;

    private List<Vertex> vertices = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();
    private boolean isGreen = true; // Флаг для переключения цвета

    private int[][] firstGraphMatrix;
    private int[][] secondGraphMatrix;
    private List<String> firstGraphVertices; // Сохраняем метки вершин
    private List<String> secondGraphVertices;
    private boolean isFirstGraphSaved = false;

    @FXML
    public void initialize() {
        // Устанавливаем Platform.setImplicitExit(true) для корректного завершения
        Platform.setImplicitExit(true);
        
        // Инициализируем остальные компоненты
        graphPane.setFocusTraversable(true);
    }

    @FXML
    protected void onAddVertex() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Добавление Вершины");
        dialog.setHeaderText("Введите имя вершины:");
        dialog.setContentText("Имя:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (name.trim().isEmpty()) {
                showAlert("Ошибка", "Имя вершины не может быть пустым.");
                return;
            }
            // Проверка уникальности имени
            for (Vertex v : vertices) {
                if (v.getLabel().equals(name)) {
                    showAlert("Ошибка", "Вершина с таким именем уже существует.");
                    return;
                }
            }

            double x = 100 + vertices.size() * 50;
            double y = 100 + vertices.size() * 50;
            Vertex vertex = new Vertex(x, y, name);
            vertices.add(vertex);
            graphPane.getChildren().addAll(vertex.getCircle(), vertex.getText());
            statusLabel.setText("Вершина \"" + name + "\" добавлена.");
        });
    }

    @FXML
    protected void onDeleteVertex() {
        if (vertices.isEmpty()) {
            statusLabel.setText("Нет вершин для удаления.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Удаление Вершины");
        dialog.setHeaderText("Введите имя вершины для удаления:");
        dialog.setContentText("Имя:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            Vertex vertexToRemove = null;
            for (Vertex v : vertices) {
                if (v.getLabel().equals(name)) {
                    vertexToRemove = v;
                    break;
                }
            }

            if (vertexToRemove == null) {
                showAlert("Ошибка", "Вершина с именем \"" + name + "\" не найдена.");
                return;
            }

            vertices.remove(vertexToRemove);
            graphPane.getChildren().removeAll(vertexToRemove.getCircle(), vertexToRemove.getText());

            // Удаляем связанные рёбра
            List<Edge> edgesToRemove = new ArrayList<>();
            for (Edge edge : edges) {
                if (edge.getStart() == vertexToRemove || edge.getEnd() == vertexToRemove) {
                    graphPane.getChildren().remove(edge.getLine());
                    edgesToRemove.add(edge);
                }
            }
            edges.removeAll(edgesToRemove);

            statusLabel.setText("Вершина \"" + name + "\" удалена.");
        });
    }

    @FXML
    protected void onAddEdge() {
        if (vertices.size() < 2) {
            statusLabel.setText("Недостаточно вершин для создания ребра.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Добавление Ребра");
        dialog.setHeaderText("Введите имена двух вершин через запятую:");
        dialog.setContentText("Вершины (например, A,B):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(input -> {
            String[] names = input.split(",");
            if (names.length != 2) {
                showAlert("Ошибка", "Необходимо указать ровно две вершины, разделенные запятой.");
                return;
            }

            String name1 = names[0].trim();
            String name2 = names[1].trim();

            if (name1.equals(name2)) {
                showAlert("Ошибка", "Нельзя создать ребро между одной и той же вершиной.");
                return;
            }

            Vertex start = null, end = null;
            for (Vertex v : vertices) {
                if (v.getLabel().equals(name1)) {
                    start = v;
                }
                if (v.getLabel().equals(name2)) {
                    end = v;
                }
            }

            if (start == null || end == null) {
                showAlert("Ошибка", "Одна или обе вершины не найдены.");
                return;
            }

            // Проверка существования ребра
            for (Edge e : edges) {
                if ((e.getStart() == start && e.getEnd() == end) ||
                    (e.getStart() == end && e.getEnd() == start)) {
                    showAlert("Ошибка", "Ребро между \"" + name1 + "\" и \"" + name2 + "\" уже существует.");
                    return;
                }
            }

            Line line = new Line(start.getX(), start.getY(), end.getX(), end.getY());
            line.setStrokeWidth(2);
            if (isGreen) {
                line.setStroke(Color.GREEN);
            } else {
                line.setStroke(Color.RED);
            }
            linesToBack(line);
            Edge edge = new Edge(start, end, line);
            edges.add(edge);
            graphPane.getChildren().add(0, line); // Добавляем линию на задний план
            statusLabel.setText("Ребро между \"" + name1 + "\" и \"" + name2 + "\" добавлено.");
        });
    }

    @FXML
    protected void onDeleteEdge() {
        if (edges.isEmpty()) {
            statusLabel.setText("Нет рёбер для удаления.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Удаление Ребра");
        dialog.setHeaderText("Введите имена двух вершин через запятую для удаления ребра:");
        dialog.setContentText("Вершины (например, A,B):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(input -> {
            String[] names = input.split(",");
            if (names.length != 2) {
                showAlert("Ошибка", "Необходимо указать ровно две вершины, разделенные запятой.");
                return;
            }

            String name1 = names[0].trim();
            String name2 = names[1].trim();

            Vertex start = null, end = null;
            for (Vertex v : vertices) {
                if (v.getLabel().equals(name1)) {
                    start = v;
                }
                if (v.getLabel().equals(name2)) {
                    end = v;
                }
            }

            if (start == null || end == null) {
                showAlert("Ошибка", "Одна или обе вершины не найдены.");
                return;
            }

            Edge edgeToRemove = null;
            for (Edge e : edges) {
                if ((e.getStart() == start && e.getEnd() == end) ||
                    (e.getStart() == end && e.getEnd() == start)) {
                    edgeToRemove = e;
                    break;
                }
            }

            if (edgeToRemove == null) {
                showAlert("Ошибка", "Ребро между \"" + name1 + "\" и \"" + name2 + "\" не найдено.");
                return;
            }

            edges.remove(edgeToRemove);
            graphPane.getChildren().remove(edgeToRemove.getLine());
            statusLabel.setText("Ребро между \"" + name1 + "\" и \"" + name2 + "\" удалено.");
        });
    }

    @FXML
    protected void onColorGraph() {
        // Переключение цветов между зелёным и красным
        if (isGreen) {
            applyColor(Color.RED);
            isGreen = false;
            statusLabel.setText("Цвет графа изменён на красный.");
        } else {
            applyColor(Color.GREEN);
            isGreen = true;
            statusLabel.setText("Цвет графа изменён на зелёный.");
        }
    }

    private void applyColor(Color color) {
        for (Edge edge : edges) {
            edge.getLine().setStroke(color);
        }
        for (Vertex vertex : vertices) {
            vertex.getCircle().setFill(color);
        }
    }

    private void linesToBack(Line line) {
        graphPane.getChildren().remove(line);
        graphPane.getChildren().add(0, line);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    // Внутренние классы для представления вершины и ребра
    private class Vertex {
        private Circle circle;
        private Text text;
        private String label;

        // Текущие координаты для отслеживания перемещения
        private double initialX;
        private double initialY;

        public Vertex(double x, double y, String label) {
            this.label = label;
            circle = new Circle(x, y, 20, isGreen ? Color.GREEN : Color.RED);
            circle.setStroke(Color.BLACK);
            text = new Text(x - 5, y + 5, label);

            // Добавляем обработчики событий для перетаскивания
            circle.setOnMousePressed(this::handleMousePressed);
            circle.setOnMouseDragged(this::handleMouseDragged);
            // Также можно сделать перетаскивание при клике на тексте
            text.setOnMousePressed(this::handleMousePressed);
            text.setOnMouseDragged(this::handleMouseDragged);
        }

        private void handleMousePressed(MouseEvent event) {
            initialX = event.getSceneX();
            initialY = event.getSceneY();
            event.consume();
        }

        private void handleMouseDragged(MouseEvent event) {
            double deltaX = event.getSceneX() - initialX;
            double deltaY = event.getSceneY() - initialY;
            initialX = event.getSceneX();
            initialY = event.getSceneY();

            // Обновляем позицию круга
            circle.setCenterX(circle.getCenterX() + deltaX);
            circle.setCenterY(circle.getCenterY() + deltaY);

            // Обновляем позицию текста
            text.setX(text.getX() + deltaX);
            text.setY(text.getY() + deltaY);

            // Обновляем связанные линии
            updateConnectedEdges();

            event.consume();
        }

        private void updateConnectedEdges() {
            for (Edge edge : edges) {
                if (edge.getStart() == this || edge.getEnd() == this) {
                    edge.updatePosition();
                }
            }
        }

        public Circle getCircle() {
            return circle;
        }

        public Text getText() {
            return text;
        }

        public String getLabel() {
            return label;
        }

        public double getX() {
            return circle.getCenterX();
        }

        public double getY() {
            return circle.getCenterY();
        }

        public void setColor(Color color) {
            circle.setFill(color);
        }
    }

    private class Edge {
        private Vertex start;
        private Vertex end;
        private Line line;

        public Edge(Vertex start, Vertex end, Line line) {
            this.start = start;
            this.end = end;
            this.line = line;
        }

        public Vertex getStart() {
            return start;
        }

        public Vertex getEnd() {
            return end;
        }

        public Line getLine() {
            return line;
        }

        public void updatePosition() {
            line.setStartX(start.getX());
            line.setStartY(start.getY());
            line.setEndX(end.getX());
            line.setEndY(end.getY());
        }
    }

    // Дополнительные функции для расширенного функционала

    @FXML
    protected void onShowAdjacencyMatrix() {
        if (vertices.isEmpty()) {
            infoArea.setText("Граф пуст.");
            return;
        }

        int n = vertices.size();
        int[][] adjMatrix = new int[n][n];
        Map<String, Integer> vertexIndex = new HashMap<>();
        for (int i = 0; i < n; i++) {
            vertexIndex.put(vertices.get(i).getLabel(), i);
        }

        for (Edge edge : edges) {
            int i = vertexIndex.get(edge.getStart().getLabel());
            int j = vertexIndex.get(edge.getEnd().getLabel());
            adjMatrix[i][j] = 1;
            adjMatrix[j][i] = 1; // Неориентированный граф
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Матрица смежности:\n");
        sb.append("\t");
        for (Vertex v : vertices) {
            sb.append(v.getLabel()).append("\t");
        }
        sb.append("\n");
        for (int i = 0; i < n; i++) {
            sb.append(vertices.get(i).getLabel()).append("\t");
            for (int j = 0; j < n; j++) {
                sb.append(adjMatrix[i][j]).append("\t");
            }
            sb.append("\n");
        }

        infoArea.setText(sb.toString());
    }

    @FXML
    protected void onCheckConnected() {
        if (vertices.isEmpty()) {
            infoArea.setText("Граф пуст.");
            return;
        }

        boolean connected = isGraphConnected();
        infoArea.setText("Граф " + (connected ? "связный." : "несвязный."));
    }

    @FXML
    protected void onMakeConnected() {
        if (vertices.isEmpty()) {
            infoArea.setText("Граф пуст.");
            return;
        }

        if (isGraphConnected()) {
            infoArea.setText("Граф уже связный.");
            return;
        }

        // Находим компоненты связности
        List<Set<Vertex>> components = getConnectedComponents();

        // Соединяем компоненты
        for (int i = 1; i < components.size(); i++) {
            Vertex from = components.get(i - 1).iterator().next();
            Vertex to = components.get(i).iterator().next();
            addEdgeConnectingVertices(from, to);
        }

        infoArea.setText("Граф был приведён к связному.");
        statusLabel.setText("Граф приведён к связному.");
    }

    @FXML
    protected void onFindHamiltonianCycle() {
        if (vertices.isEmpty()) {
            infoArea.setText("Граф пуст.");
            return;
        }

        List<Vertex> cycle = findHamiltonianCycle();
        if (cycle.isEmpty()) {
            infoArea.setText("Гамильтоновы циклы не найдены.");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Найденный Гамильтонов цикл:\n");
            for (Vertex v : cycle) {
                sb.append(v.getLabel()).append(" -> ");
            }
            sb.append(cycle.get(0).getLabel());
            infoArea.setText(sb.toString());
        }
    }

    @FXML
    protected void onComputeGraphProperties() {
        if (vertices.isEmpty()) {
            infoArea.setText("Граф пуст.");
            return;
        }

        // Вычисление диаметров и радиусов
        Map<Vertex, Map<Vertex, Integer>> shortestPaths = computeAllPairsShortestPaths();
        int diameter = 0;
        int radius = Integer.MAX_VALUE;
        List<Vertex> centers = new ArrayList<>();

        for (Vertex v : vertices) {
            int eccentricity = Collections.max(shortestPaths.get(v).values());
            diameter = Math.max(diameter, eccentricity);
            radius = Math.min(radius, eccentricity);
        }

        for (Vertex v : vertices) {
            int eccentricity = Collections.max(shortestPaths.get(v).values());
            if (eccentricity == radius) {
                centers.add(v);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Диаметр графа: ").append(diameter).append("\n");
        sb.append("Радиус графа: ").append(radius).append("\n");
        sb.append("Центры графа: ");
        for (int i = 0; i < centers.size(); i++) {
            sb.append(centers.get(i).getLabel());
            if (i < centers.size() - 1) sb.append(", ");
        }

        infoArea.setText(sb.toString());
    }

    @FXML
    protected void onComputeTensorProduct() {
        if (firstGraphMatrix == null || secondGraphMatrix == null) {
            showAlert("Ошибка", "Необходимо сохранить оба графа.");
            return;
        }

        int n1 = firstGraphMatrix.length;
        int n2 = secondGraphMatrix.length;
        int[][] result = new int[n1 * n2][n1 * n2];

        // Вычисление тензорного произведения
        for (int i1 = 0; i1 < n1; i1++) {
            for (int j1 = 0; j1 < n1; j1++) {
                for (int i2 = 0; i2 < n2; i2++) {
                    for (int j2 = 0; j2 < n2; j2++) {
                        result[i1 * n2 + i2][j1 * n2 + j2] = 
                            firstGraphMatrix[i1][j1] * secondGraphMatrix[i2][j2];
                    }
                }
            }
        }

        displayProductMatrix(result, "Тензорное произведение");
    }

    @FXML
    protected void onComputeCartesianProduct() {
        if (firstGraphMatrix == null || secondGraphMatrix == null) {
            showAlert("Ошибка", "Необходимо сохранить оба графа.");
            return;
        }

        int n1 = firstGraphMatrix.length;
        int n2 = secondGraphMatrix.length;
        int[][] result = new int[n1 * n2][n1 * n2];

        // Вычисление декартова произведения
        for (int i1 = 0; i1 < n1; i1++) {
            for (int j1 = 0; j1 < n1; j1++) {
                for (int i2 = 0; i2 < n2; i2++) {
                    for (int j2 = 0; j2 < n2; j2++) {
                        if (i1 == j1 && secondGraphMatrix[i2][j2] == 1) {
                            result[i1 * n2 + i2][j1 * n2 + j2] = 1;
                        }
                        if (firstGraphMatrix[i1][j1] == 1 && i2 == j2) {
                            result[i1 * n2 + i2][j1 * n2 + j2] = 1;
                        }
                    }
                }
            }
        }

        displayProductMatrix(result, "Декартово произведение");
    }

    private void displayProductMatrix(int[][] matrix, String title) {
        StringBuilder sb = new StringBuilder();
        sb.append(title).append(":\n\n");

        // Создаем метки для вершин произведения
        List<String> productVertices = new ArrayList<>();
        for (String v1 : firstGraphVertices) {
            for (String v2 : secondGraphVertices) {
                productVertices.add(v1 + v2);
            }
        }

        // Выводим заголовок
        sb.append("\t");
        for (String vertex : productVertices) {
            sb.append(vertex).append("\t");
        }
        sb.append("\n");

        // Выводим матрицу
        for (int i = 0; i < matrix.length; i++) {
            sb.append(productVertices.get(i)).append("\t");
            for (int j = 0; j < matrix[i].length; j++) {
                sb.append(matrix[i][j]).append("\t");
            }
            sb.append("\n");
        }

        infoArea.setText(sb.toString());
    }

    // Вспомогательные методы

    private boolean isGraphConnected() {
        Set<Vertex> visited = new HashSet<>();
        Queue<Vertex> queue = new LinkedList<>();
        queue.add(vertices.get(0));
        visited.add(vertices.get(0));

        while (!queue.isEmpty()) {
            Vertex current = queue.poll();
            for (Edge edge : edges) {
                Vertex neighbor = null;
                if (edge.getStart() == current) {
                    neighbor = edge.getEnd();
                } else if (edge.getEnd() == current) {
                    neighbor = edge.getStart();
                }
                if (neighbor != null && !visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        return visited.size() == vertices.size();
    }

    private List<Set<Vertex>> getConnectedComponents() {
        List<Set<Vertex>> components = new ArrayList<>();
        Set<Vertex> visited = new HashSet<>();

        for (Vertex v : vertices) {
            if (!visited.contains(v)) {
                Set<Vertex> component = new HashSet<>();
                Queue<Vertex> queue = new LinkedList<>();
                queue.add(v);
                visited.add(v);
                component.add(v);

                while (!queue.isEmpty()) {
                    Vertex current = queue.poll();
                    for (Edge edge : edges) {
                        Vertex neighbor = null;
                        if (edge.getStart() == current) {
                            neighbor = edge.getEnd();
                        } else if (edge.getEnd() == current) {
                            neighbor = edge.getStart();
                        }
                        if (neighbor != null && !visited.contains(neighbor)) {
                            visited.add(neighbor);
                            queue.add(neighbor);
                            component.add(neighbor);
                        }
                    }
                }

                components.add(component);
            }
        }

        return components;
    }

    private void addEdgeConnectingVertices(Vertex from, Vertex to) {
        // Проверка существования ребра
        for (Edge e : edges) {
            if ((e.getStart() == from && e.getEnd() == to) ||
                (e.getStart() == to && e.getEnd() == from)) {
                return; // Ребро уже существует
            }
        }

        Line line = new Line(from.getX(), from.getY(), to.getX(), to.getY());
        line.setStrokeWidth(2);
        if (isGreen) {
            line.setStroke(Color.GREEN);
        } else {
            line.setStroke(Color.RED);
        }
        linesToBack(line);
        Edge edge = new Edge(from, to, line);
        edges.add(edge);
        graphPane.getChildren().add(0, line); // Добавляем линию на задний план
    }

    private List<Vertex> findHamiltonianCycle() {
        List<Vertex> cycle = new ArrayList<>();
        if (vertices.isEmpty()) return cycle;

        Vertex start = vertices.get(0);
        cycle.add(start);
        if (findHamiltonianCycleUtil(start, cycle)) {
            return cycle;
        }
        return new ArrayList<>();
    }

    private boolean findHamiltonianCycleUtil(Vertex current, List<Vertex> cycle) {
        if (cycle.size() == vertices.size()) {
            // Проверяем, есть ли ребро обратно к началу
            for (Edge edge : edges) {
                if ((edge.getStart() == current && edge.getEnd() == cycle.get(0)) ||
                    (edge.getEnd() == current && edge.getStart() == cycle.get(0))) {
                    return true;
                }
            }
            return false;
        }

        for (Edge edge : edges) {
            Vertex neighbor = null;
            if (edge.getStart() == current) {
                neighbor = edge.getEnd();
            } else if (edge.getEnd() == current) {
                neighbor = edge.getStart();
            }

            if (neighbor != null && !cycle.contains(neighbor)) {
                cycle.add(neighbor);
                if (findHamiltonianCycleUtil(neighbor, cycle)) {
                    return true;
                }
                cycle.remove(cycle.size() - 1);
            }
        }

        return false;
    }

    private Map<Vertex, Map<Vertex, Integer>> computeAllPairsShortestPaths() {
        Map<Vertex, Map<Vertex, Integer>> distances = new HashMap<>();

        for (Vertex v : vertices) {
            Map<Vertex, Integer> dist = new HashMap<>();
            for (Vertex u : vertices) {
                dist.put(u, Integer.MAX_VALUE);
            }
            dist.put(v, 0);

            Queue<Vertex> queue = new LinkedList<>();
            queue.add(v);

            while (!queue.isEmpty()) {
                Vertex current = queue.poll();
                for (Edge edge : edges) {
                    Vertex neighbor = null;
                    if (edge.getStart() == current) {
                        neighbor = edge.getEnd();
                    } else if (edge.getEnd() == current) {
                        neighbor = edge.getStart();
                    }

                    if (neighbor != null && dist.get(neighbor) == Integer.MAX_VALUE) {
                        dist.put(neighbor, dist.get(current) + 1);
                        queue.add(neighbor);
                    }
                }
            }

            distances.put(v, dist);
        }

        return distances;
    }

    @FXML
    protected void onSaveGraph() {
        if (vertices.isEmpty()) {
            showAlert("Ошибка", "Граф пуст.");
            return;
        }

        // Создаем матрицу смежности текущего графа
        int n = vertices.size();
        int[][] adjMatrix = new int[n][n];
        List<String> vertexLabels = new ArrayList<>();
        Map<String, Integer> vertexIndex = new HashMap<>();

        for (int i = 0; i < n; i++) {
            vertexLabels.add(vertices.get(i).getLabel());
            vertexIndex.put(vertices.get(i).getLabel(), i);
        }

        for (Edge edge : edges) {
            int i = vertexIndex.get(edge.getStart().getLabel());
            int j = vertexIndex.get(edge.getEnd().getLabel());
            adjMatrix[i][j] = 1;
            adjMatrix[j][i] = 1;
        }

        if (!isFirstGraphSaved) {
            // Сохраняем первый граф
            firstGraphMatrix = adjMatrix;
            firstGraphVertices = vertexLabels;
            isFirstGraphSaved = true;
            statusLabel.setText("Первый граф сохранен");
            
            // Очищаем текущий граф
            clearCurrentGraph();
        } else {
            // Сохраняем второй граф
            secondGraphMatrix = adjMatrix;
            secondGraphVertices = vertexLabels;
            statusLabel.setText("Второй граф сохранен");
            
            // Активируем кнопки произведений
            // (это нужно реализовать в FXML)
        }
    }

    private void clearCurrentGraph() {
        graphPane.getChildren().clear();
        vertices.clear();
        edges.clear();
    }
}