package com.ngoclam.querytool;

import com.ngoclam.querytool.dao.PostgreSQL_Driver;
import com.ngoclam.querytool.dao.SQLite_Driver;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Setting_Controller {
    @FXML private TextField databasename;
    @FXML private TextField hostname;
    @FXML private TextField port;
    @FXML private TextField username;
    @FXML private TextField password;
    @FXML private ChoiceBox selectdb;
    @FXML private TextField id_database;
    @FXML
    public void initialize() {
        String select_query = "SELECT * FROM Configure WHERE is_selected = 'yes';";
        String all_database_query = "SELECT display FROM Configure";
        try (Connection connection = SQLite_Driver.connect()) {
            assert connection != null;
            PreparedStatement statement = connection.prepareStatement(all_database_query);
            ResultSet rs = statement.executeQuery();
            ObservableList<String> data = FXCollections.observableArrayList();
            while (rs.next()){
                data.add(rs.getString("display"));
            }
            selectdb.setItems(data);

            statement = connection.prepareStatement(select_query);
            rs = statement.executeQuery();
            while (rs.next()) {
                selectdb.setValue(rs.getString("display"));
                databasename.setText(rs.getString("name"));
                hostname.setText(rs.getString("host"));
                port.setText(rs.getString("port"));
                username.setText(rs.getString("user"));
                password.setText(rs.getString("password"));
                id_database.setText(String.valueOf(rs.getInt("id")));
            }
        } catch (SQLException e) {
            Logger.getAnonymousLogger().log(
                    Level.SEVERE,
                    LocalDateTime.now() + String.valueOf(e));
        }

        selectdb.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldV, newV) ->
                        SwitchDatabase((String) newV)
                );
    }
    public void SwitchDatabase(String sw){
        String query = String.format("SELECT * FROM Configure WHERE display = '%s';", sw);
        try (Connection connection = SQLite_Driver.connect()){
            assert connection != null;
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                selectdb.setValue(rs.getString("display"));
                databasename.setText(rs.getString("name"));
                hostname.setText(rs.getString("host"));
                port.setText(rs.getString("port"));
                username.setText(rs.getString("user"));
                password.setText(rs.getString("password"));
                id_database.setText(String.valueOf(rs.getInt("id")));
            }
        }
        catch (SQLException e) {
            Logger.getAnonymousLogger().log(
                    Level.SEVERE,
                    LocalDateTime.now() + String.valueOf(e));
        }
    }
    public void OnClickTest() {
        String url = String.format("jdbc:postgresql://%s:%s/%s",hostname.getText(),port.getText(),databasename.getText());
        String user = username.getText();
        String pw = password.getText();
        if(PostgreSQL_Driver.checkDrivers()){
            try (Connection con = DriverManager.getConnection(url, user, pw)){
                Notifications.create()
                        .title("Kết quả test database")
                        .text(String.format("Cấu hình hợp lệ, kết nối thành công!\n %s", con))
                        .showInformation();
            }
            catch (SQLException ex) {
                Notifications.create()
                        .title("Kết quả test database")
                        .text(String.format("Cấu hình không hợp lệ!\n %s", ex))
                        .showError();
            }
        }
    }
    public void OnClickSave(){
        String sql_ResetAll = "UPDATE Configure SET is_selected = 'no';";
        String sql_SetSelect = String.format("UPDATE Configure SET is_selected = 'yes', name = '%s', host = '%s', port = '%s', user = '%s', password = '%s' WHERE id = '%s';", databasename.getText(),hostname.getText(),port.getText(),username.getText(),password.getText(),id_database.getText());
        if(PostgreSQL_Driver.checkDrivers()){
            try (Connection connection = SQLite_Driver.connect()){
                PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(sql_ResetAll);
                statement.executeUpdate();
                statement = connection.prepareStatement(sql_SetSelect);
                statement.executeUpdate();
                Stage stage = (Stage) selectdb.getScene().getWindow();
                stage.close();
                Notifications.create().title("Kết quả lưu cấu hình database")
                        .text("Đã lưu thành công!")
                        .showInformation();
            }
            catch (SQLException e) {
                Notifications.create()
                        .title("Kết quả lưu cấu hình database")
                        .text(String.format("Lưu thất bại!\n %s",e))
                        .showError();
            }
        }
    }

}