package com.example.demo2048.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.example.demo2048.tool.EasyAlert;
import javafx.application.Application;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

// 节点类——卡片矩阵
// 若继承自Pane类,缺少需要的setAlignment()方法
public class CardMatrixPane extends StackPane {
    private final Callbacks mCallbacks;
    private final int cols;// 卡片矩阵列数
    private final int rows;// 卡片矩阵行数
    private GridPane gridPane;// 卡片矩阵容器
    private CardPane[][] cps;// 卡片矩阵
    private long score = 0;// 分数
    private final Random rand = new Random();
    private int[] mcQuantities = new int[15];// 合并过的卡片数字数量,包括4,8,16,...

    // 回调接口
    public interface Callbacks {
        void afterScoreChange();
    }

    public CardMatrixPane(Application application) {
        this(4, 4, application);
    }

    public CardMatrixPane(int cols, int rows, Application application) {
        mCallbacks = (Callbacks) application;// application供回调方法使用
        this.cols = cols;
        this.rows = rows;
        initGridPane();
        createRandomNumber();
        createRandomNumber();
        getChildren().add(gridPane);
    }

    public long getScore() {
        return score;
    }

    public int[] getMcQuantities() {
        return mcQuantities;
    }

    // 初始化GridPane
    private void initGridPane() {
        gridPane = new GridPane();

        // 对this尺寸监听
        widthProperty().addListener(ov -> setGridSizeAndCardFont());// 宽度变化,更新边长和字号
        heightProperty().addListener(ov -> setGridSizeAndCardFont());// 高度变化,更新边长和字号
        // 单元格间隙
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        // 绘制每个单元格
        cps = new CardPane[cols][rows];
        for (int i = 0; i < cols; i++) {// 遍历卡片矩阵的列
            for (int j = 0; j < rows; j++) {// 遍历卡片矩阵的行
                var card = new CardPane(0);
                gridPane.add(card, i, j);
                cps[i][j] = card;
            }
        }
    }

    // 设置GridPane的边长,其内部单元格的尺寸和CardPane的字号
    private void setGridSizeAndCardFont() {
        double minSide = Math.min(widthProperty().get(), heightProperty().get());
        gridPane.setMaxWidth(minSide);
        gridPane.setMaxHeight(minSide);
        for (var row : cps) {
            for (var card : row) {
                card.getLabel().setFont(new Font((minSide / 14) / cols * 4));// 设置显示数字的尺寸
                // 由于下面两行代码主动设置了每个单元格内cardPane的尺寸,gridPane不需要自动扩张
                card.setPrefWidth(minSide - 5 * (cols - 1));// 设置单元格内cardPane的宽度,否则它会随其内容变化,进而影响单元格宽度
                card.setPrefHeight(minSide - 5 * (rows - 1));// 设置单元格内cardPane的高度,否则它会随其内容变化,进而影响单元格高度
            }
        }
    }

    // 添加键盘监听
    public void createKeyListener() {
        setOnKeyPressed(e -> {
            CardPane maxCard = getMaxCard();// 最大卡片
            if (maxCard.getType() == 16) {// 出现最大数字
                var content = String.format("恭喜你,游戏的最大数字为%d,可在菜单栏选择重新开始\n" +
                        "事实上,我们还尚未准备比%d更大的数字卡片,终点已至", maxCard.getNumber(), maxCard.getNumber());
                new EasyAlert(content);
                return;
            }
            switch (e.getCode()) {
                case UP, W:
                    goUp();
                    break;
                case DOWN, S:
                    goDown();
                    break;
                case LEFT, A:
                    goLeft();
                    break;
                case RIGHT, D:
                    goRight();
                    break;
                default:
                    return;
            }
            redrawAllCardsAndResetIsMergeAndSetScore();// 重绘所有的卡片,并重设合并记录,更新分数
            boolean isFull = !createRandomNumber();
            boolean canMove = testUp() || testLeft();
            if (isFull && !canMove) {
                var content = String.format("游戏结束,本次最大数字为%d,可在菜单栏选择重新开始\n", maxCard.getNumber());
                new EasyAlert(content);
            }
        });
    }

    // 向上操作
    private void goUp() {
        boolean mergeOrMoveExist;// 矩阵的这次操作的一次遍历中是否存在移动或合并
        do {
            mergeOrMoveExist = false;
            for (int i = 0; i < cols; i++) {
                for (int j = 1; j < rows; j++) {
                    CardPane card = cps[i][j];
                    CardPane preCard = cps[i][j - 1];
                    boolean isChanged = card.tryMergeOrMoveInto(preCard);// 记录两张卡片间是否进行了移动或合并
                    mergeOrMoveExist |= isChanged;// 只要有一次移动或合并记录,就记存在为true
                }
            }
        } while (mergeOrMoveExist);// 如果存在移动或合并,就可能需要再次遍历,继续移动或合并
    }

    // 向下操作
    private void goDown() {
        boolean mergeOrMoveExist;
        do {
            mergeOrMoveExist = false;
            for (int i = 0; i < cols; i++) {
                for (int j = rows - 2; j >= 0; j--) {
                    CardPane card = cps[i][j];
                    CardPane preCard = cps[i][j + 1];
                    boolean isChanged = card.tryMergeOrMoveInto(preCard);
                    mergeOrMoveExist |= isChanged;
                }
            }
        } while (mergeOrMoveExist);
    }

    // 向左操作
    private void goLeft() {
        boolean mergeOrMoveExist;
        do {
            mergeOrMoveExist = false;
            for (int i = 1; i < cols; i++) {
                for (int j = 0; j < rows; j++) {
                    var card = cps[i][j];
                    var preCard = cps[i - 1][j];
                    boolean isChanged = card.tryMergeOrMoveInto(preCard);
                    mergeOrMoveExist |= isChanged;
                }
            }
        } while (mergeOrMoveExist);
    }

    // 向右操作
    private void goRight() {
        boolean mergeOrMoveExist;
        do {
            mergeOrMoveExist = false;
            for (int i = cols - 2; i >= 0; i--) {
                for (int j = 0; j < rows; j++) {
                    var card = cps[i][j];
                    var preCard = cps[i + 1][j];
                    boolean isChanged = card.tryMergeOrMoveInto(preCard);
                    mergeOrMoveExist |= isChanged;
                }
            }
        } while (mergeOrMoveExist);
    }

    // 测试是否能向上操作
    private boolean testUp() {
        for (int i = 0; i < cols; i++) {
            for (int j = 1; j < rows; j++) {
                var card = cps[i][j];
                var preCard = cps[i][j - 1];
                if (card.canMergeOrMove(preCard)) return true;
            }
        }
        return false;
    }

    // 测试是否能向左操作
    private boolean testLeft() {
        for (int i = 1; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                var card = cps[i][j];
                var preCard = cps[i - 1][j];
                if (card.canMergeOrMove(preCard)) return true;
            }
        }
        return false;
    }

    // 重绘所有的卡片,并重设合并记录,并设置分数
    private void redrawAllCardsAndResetIsMergeAndSetScore() {
        for (var row : cps) {
            for (var card : row) {
                card.draw();
                if (card.isMerge()) {// 这张卡片合并过
                    score += card.getNumber();// 计入分数
                    mcQuantities[card.getType() - 2]++;// 相应的合并过的卡片数字数量+1
                    card.setMerge(false);
                }
            }
        }
        mCallbacks.afterScoreChange();
    }

    // 获取卡片矩阵中的最大卡片
    private CardPane getMaxCard() {
        var maxCard = new CardPane();// type=0的新卡片
        for (var row : cps) {
            for (var card : row) {
                if (card.getType() > maxCard.getType()) maxCard = card;
            }
        }
        return maxCard;
    }

    // 在随机的空卡片上生成新的数字,若矩阵已满,或生成数字后满,则返回false
    public boolean createRandomNumber() {
        List<CardPane> voidCards = new ArrayList<>();// 空卡片列表
        for (var row : cps) {
            for (var card : row) {
                if (card.getType() == 0) voidCards.add(card);
            }
        }
        int len = voidCards.size();
        if (len == 0) return false;
        var card = voidCards.get((int) (rand.nextDouble(1) * len));
        card.setType(rand.nextInt(5) != 0 ? 1 : 2);// 更新type,生成数字
        card.draw();
        return len != 1;
    }

    // 重启卡片矩阵,并在随机的空卡片上生成数字
    public void restartMatrix() {
        for (var row : cps) {
            for (var card : row) {
                card.setType(0);
                card.draw();
            }
        }
        score = 0;
        mcQuantities = new int[15];
        mCallbacks.afterScoreChange();
        createRandomNumber();
        createRandomNumber();
    }
}