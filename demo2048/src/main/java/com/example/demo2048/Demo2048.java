package com.example.demo2048;

import com.example.demo2048.node.CardMatrixPane;
import com.example.demo2048.node.GameMenuBar;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

// 2048运行类
public class Demo2048 extends Application implements GameMenuBar.Callbacks, CardMatrixPane.Callbacks {
    private BorderPane borderPane;
    private GameMenuBar menuBar;
    private CardMatrixPane cardMatrixPane;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        borderPane = new BorderPane();
        var scene = new Scene(borderPane, 1000, 600);

        // Top菜单栏
        menuBar = new GameMenuBar(this);
        borderPane.setTop(menuBar);

        // Center2048卡片矩阵
        cardMatrixPane = new CardMatrixPane(this);
        cardMatrixPane.setPadding(new Insets(5));
        borderPane.setCenter(cardMatrixPane);

        primaryStage.setTitle("2048");
        primaryStage.setScene(scene);
        primaryStage.show();

        startGame();
    }

    private void startGame() {
        cardMatrixPane.requestFocus();      // 添加焦点
        cardMatrixPane.createKeyListener(); // 添加键盘监听
        afterScoreChange();
    }

    @Override
    public void afterRestart() {
        cardMatrixPane.restartMatrix();
    }

    @Override
    public void afterResetGridSize(int cols, int rows) {
        cardMatrixPane = new CardMatrixPane(cols, rows, this);
        cardMatrixPane.setPadding(new Insets(5));
        borderPane.setCenter(cardMatrixPane);
        startGame();
    }

    @Override
    public void afterScoreChange() {
        menuBar.getScoreMenu().setText("分数: " + cardMatrixPane.getScore());
    }

    @Override
    public void afterGetMoreScoreInfo() {
        List<Counter> lc = new ArrayList();
        var statistics = cardMatrixPane.getMcQuantities();
        int number = 2;
        for (int value : statistics) {
            number *= 2;
            lc.add(new Counter(number, value));
        }
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("合并次数统计");
        alert.setHeaderText("合并次数统计");
        var table = new TableView();
        var numberCol = new TableColumn("数字");
        numberCol.setCellValueFactory(new PropertyValueFactory<>("number"));
        var timeCol = new TableColumn("次数");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        table.getColumns().addAll(numberCol, timeCol);
        alert.getDialogPane().setExpandableContent(table);
        alert.getDialogPane().setExpanded(true);
        ObservableList<Counter> data = FXCollections.observableList(lc);
        table.setItems(data);
        alert.show();
    }

    public static class Counter {
        private final SimpleIntegerProperty number;
        private final SimpleIntegerProperty time;

        private Counter(int number, int time) {
            this.number = new SimpleIntegerProperty(number);
            this.time = new SimpleIntegerProperty(time);
        }

        public int getNumber() {
            return number.get();
        }

        public void setNumber(int t) {
            number.set(t);
        }

        public int getTime() {
            return time.get();
        }

        public void setTime(int t) {
            time.set(t);
        }
    }
}
