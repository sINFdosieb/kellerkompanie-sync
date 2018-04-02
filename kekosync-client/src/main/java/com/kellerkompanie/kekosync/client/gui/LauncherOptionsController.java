package com.kellerkompanie.kekosync.client.gui;

import com.kellerkompanie.kekosync.client.arma.ArmAParameter;
import com.kellerkompanie.kekosync.client.settings.Settings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LauncherOptionsController implements Initializable {

    @FXML
    private VBox parameterVBox;

    @FXML
    private TextArea parameterTextArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createLauncherOptions();
    }

    private void createLauncherOptions() {
        List<ArmAParameter> parameterList = Settings.getInstance().getLaunchParams();

        for (ArmAParameter param : parameterList) {
            HBox row = new HBox();
            CheckBox checkBox = new CheckBox();
            checkBox.setText(param.getDescription());
            checkBox.setSelected(param.isEnabled());
            checkBox.setOnAction(this::handleCheckBoxStateChanged);
            row.getChildren().add(checkBox);

            if (param.getType() == ArmAParameter.ParameterType.COMBO) {
                ObservableList<String> options =
                        FXCollections.observableArrayList(
                                param.getValues()
                        );
                ComboBox<String> comboBox = new ComboBox<String>(options);
                comboBox.getSelectionModel().select(param.getValue());
                comboBox.setOnAction(this::handleComboBoxStateChanged);
                row.getChildren().add(comboBox);
            }

            parameterVBox.getChildren().add(row);
        }

        updateTextArea();
    }

    private void handleCheckBoxStateChanged(ActionEvent event) {
        CheckBox chk = (CheckBox) event.getSource();
        System.out.println("Action performed on checkbox " + chk.getText());
        updateTextArea();
    }

    private void handleComboBoxStateChanged(ActionEvent event) {
        ComboBox cb = (ComboBox) event.getSource();
        System.out.println("Action performed on checkbox " + cb.getSelectionModel().getSelectedItem());
        updateTextArea();
    }

    private void updateTextArea() {
        List<ArmAParameter> params = Settings.getInstance().getLaunchParams();

        parameterTextArea.clear();

        StringBuilder sb = new StringBuilder();
        for(ArmAParameter armAParameter : params) {
            if(armAParameter.isEnabled()) {
                sb.append(armAParameter.getArgument());
                sb.append("\n");
            }
        }

        parameterTextArea.setText(sb.toString());
    }
}
