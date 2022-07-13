package com.example.demo2048.node;

import com.example.demo2048.tool.EasyAlert;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;

// 节点类——2048游戏菜单栏
public class GameMenuBar extends MenuBar {
    private final Callbacks mCallbacks;
    private final Menu scoreMenu;

    // 回调接口
    public interface Callbacks {
        void afterRestart();

        void afterResetGridSize(int cols, int rows);

        void afterGetMoreScoreInfo();
    }

    public GameMenuBar(Application application) {
        mCallbacks = (Callbacks) application;// application供回调方法使用

        // Game菜单
        var gameMenu = new Menu("游戏");
        var restartMenuItem = new MenuItem("重新开始");
        restartMenuItem.setOnAction(e -> mCallbacks.afterRestart());
        var exitMenuItem = new MenuItem("退出");
        exitMenuItem.setOnAction(e -> Platform.exit());
        gameMenu.getItems().addAll(restartMenuItem, exitMenuItem);

        // Setting菜单
        var settingMenu = new Menu("设置");
        var tg = new ToggleGroup();

        var r44MenuItem = new RadioMenuItem("尺寸:4x4");
        r44MenuItem.setOnAction(e -> mCallbacks.afterResetGridSize(4, 4));
        var r55MenuItem = new RadioMenuItem("尺寸:5x5");
        r55MenuItem.setOnAction(e -> mCallbacks.afterResetGridSize(5, 5));
        var r66MenuItem = new RadioMenuItem("尺寸:6x6");
        r66MenuItem.setOnAction(e -> mCallbacks.afterResetGridSize(6, 6));

        r44MenuItem.setToggleGroup(tg);
        r55MenuItem.setToggleGroup(tg);
        r66MenuItem.setToggleGroup(tg);
        settingMenu.getItems().addAll(r44MenuItem, r55MenuItem, r66MenuItem);
        r44MenuItem.setSelected(true);// 默认选中4x4

        // Info菜单
        var helpMenuItem = new MenuItem("帮助");
        var content = """
                操作方式:
                向上滑动:方向键↑或键W
                向下滑动:方向键↓或键S
                向左滑动:方向键←或键A
                向右滑动:方向键→或键D

                游戏规则:
                相同数字的卡片在靠拢、相撞时会合并
                在操作中合并的卡片会以红色边框凸显
                尽可能获得更大的数字!""";
        helpMenuItem.setOnAction(e -> {
            new EasyAlert(content);
        });

        var aboutUsMenuItem = new MenuItem("关于我们");
        aboutUsMenuItem.setOnAction(e -> {
            new EasyAlert("游戏作者:邦邦拒绝魔抗\n他的邮箱:842748156@qq.com\n\n感谢你的游玩!");
        });

        var infoMenu = new Menu("信息");
        infoMenu.getItems().addAll(helpMenuItem, aboutUsMenuItem);

        // Record菜单
        var recordMenu = new Menu("记录");
        var historyScoreMenuItem = new MenuItem("历史分数");
        historyScoreMenuItem.setOnAction(e -> {
            new EasyAlert("还没有制作喵");
        });
        recordMenu.getItems().addAll(historyScoreMenuItem);

        // Score菜单
        scoreMenu = new Menu("分数");
        var moreScoreInfo = new MenuItem("更多分数信息");
        moreScoreInfo.setOnAction(e -> mCallbacks.afterGetMoreScoreInfo());
        scoreMenu.getItems().addAll(moreScoreInfo);

        getMenus().addAll(gameMenu, settingMenu, infoMenu, recordMenu, scoreMenu);
    }

    public Menu getScoreMenu() {
        return scoreMenu;
    }
}