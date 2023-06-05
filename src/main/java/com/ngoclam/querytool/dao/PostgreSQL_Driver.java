package com.ngoclam.querytool.dao;
import com.ngoclam.querytool.util.CustomAlert;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import org.controlsfx.control.Notifications;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgreSQL_Driver {
    public static void ExecuteSQL(String SQL, String host, String port, String databasename, String username, String password, TableView ResultSQL,TextArea SQLText, Button Run, Label textResult)
    {
        String url = String.format("jdbc:postgresql://%s:%s/%s", host,port,databasename);
        ObservableList<ObservableList> data;
        data = FXCollections.observableArrayList();
        if(checkDrivers()==true){
            try (
                    Connection con = DriverManager.getConnection(url, username, password);)
            {
                long start = System.currentTimeMillis();
                ResultSet rs = con.createStatement().executeQuery(SQL);
                for (int i = 0; i < rs.getMetaData().getColumnCount(); i++)
                {
                    final int j = i;
                    TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                    col.setCellValueFactory((Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
                    ResultSQL.getColumns().addAll(col);
                }
                long count = 0;
                while (rs.next())
                {
                    //Iterate Row
                    count++;
                    ObservableList<String> row = FXCollections.observableArrayList();
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++)
                    {
                        //Iterate Column
                        if(rs.getString(i)==null) row.add("");
                        else row.add(rs.getString(i));
                    }
                    data.add(row);
                }
                long end = System.currentTimeMillis();
                ResultSQL.setItems(data);
                ResultSQL.refresh();

                if(count>1){
                    Notifications.create().title("Kết quả chạy câu lệnh SQL")
                            .text(String.format("Trả về %s records. Truy vấn mất %s milliseconds", count, end-start))
                            .showInformation();
                    textResult.setText(String.format("TRẢ VỀ %s RECORDS. TRUY VẤN MẤT %s milliseconds",count,end-start));
                }
                else if(count==1){
                    Notifications.create().title("Kết quả chạy câu lệnh SQL")
                            .text(String.format("Trả về 1 record. Truy vấn mất %s milliseconds", end-start))
                            .showInformation();
                    textResult.setText(String.format("TRẢ VỀ 1 RECORD. TRUY VẤN MẤT %s milliseconds",end-start));
                }
                else{
                    Notifications.create().title("Kết quả chạy câu lệnh SQL")
                            .text(String.format("Trả về 0 record. Truy vấn mất %s milliseconds", end-start))
                            .showInformation();
                    textResult.setText(String.format("TRẢ VỀ 0 RECORD. TRUY VẤN MẤT %s milliseconds",end-start));
                }
            }
            catch (SQLException ex)
            {
                CustomAlert.error("Kết quả chạy câu lệnh SQL","Câu lệnh SQL sai!",String.valueOf(ex)).showAndWait();
                SQLText.requestFocus();
                Logger lgr = Logger.getLogger(PostgreSQL_Driver.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }
            Run.setDisable(false);
        }
    }
    public static boolean checkDrivers() {
        try {
            Class.forName("org.postgresql.Driver");
            DriverManager.registerDriver(new org.sqlite.JDBC());
            return true;
        } catch (ClassNotFoundException | SQLException classNotFoundException) {
            Logger.getAnonymousLogger().log(Level.SEVERE, LocalDateTime.now() + ": Could not start SQLite Drivers");
            return false;
        }
    }


}