package com.example.demo2048.tool;

import javafx.scene.control.Alert;

public class EasyAlert {
    public EasyAlert(String content){
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(alert.getAlertType().toString());
        alert.setContentText(content);
        alert.show();
    }
}
