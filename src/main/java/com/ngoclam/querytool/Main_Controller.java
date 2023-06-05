package com.ngoclam.querytool;

import com.ngoclam.querytool.dao.PostgreSQL_Driver;
import com.ngoclam.querytool.dao.SQLite_Driver;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main_Controller {
    @FXML private TextArea SQLText;
    @FXML private Button Run;
    @FXML private TableView<ObservableList> ResultSQL;
    @FXML private Label textResult;
    @FXML
    public void initialize(){
        ResultSQL.setPlaceholder(new Label("Không có dữ liệu!"));
        ResultSQL.getSelectionModel().setCellSelectionEnabled(true);
        ResultSQL.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ResultSQL.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.C && e.isControlDown()) {
                StringBuilder clipboardString = new StringBuilder();

                ObservableList<TablePosition> positionList = ResultSQL.getSelectionModel().getSelectedCells();

                int prevRow = -1;

                for (TablePosition position : positionList) {

                    int row = position.getRow();
                    int col = position.getColumn();

                    Object cell = (Object) ResultSQL.getColumns().get(col).getCellData(row);

                    // null-check: provide empty string for nulls
                    if (cell == null) {
                        cell = "";
                    }

                    // determine whether we advance in a row (tab) or a column
                    // (newline).
                    if (prevRow == row) {

                        clipboardString.append('\t');

                    } else if (prevRow != -1) {

                        clipboardString.append('\n');

                    }

                    // create string from cell
                    String text = cell.toString();

                    // add new item to clipboard
                    clipboardString.append(text);

                    // remember previous
                    prevRow = row;
                }

                // create clipboard content
                final ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.putString(clipboardString.toString());

                // set clipboard content
                Clipboard.getSystemClipboard().setContent(clipboardContent);
            }

        });
    }

    public void OnClickClear(){
        ResultSQL.getItems().clear();
        ResultSQL.getColumns().clear();
        ResultSQL.refresh();
        SQLText.setText("");
        textResult.setText("SQL COMMAND");
        SQLText.requestFocus();
        Notifications.create().title("Đã xóa mọi trạng thái!").showInformation();
    }

    public void OnClickSave(){
        if(!SQLText.getText().trim().isEmpty()){
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);
            Stage stage = (Stage) Run.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                saveTextToFile(SQLText.getText(), file);
            }
        }
    }

    public void OnClickExecute() throws SQLException {
        ResultSQL.getItems().clear();
        ResultSQL.getColumns().clear();
        ResultSQL.refresh();
        Run.setDisable(true);
        String select_query = "SELECT * FROM Configure WHERE is_selected = 'yes';";
        String host=null,port=null,databasename=null,user=null,password=null;
        try (Connection connection = SQLite_Driver.connect()){
            assert connection != null;
            PreparedStatement statement = connection.prepareStatement(select_query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                host = rs.getString("host");
                port = rs.getString("port");
                databasename = rs.getString("name");
                user = rs.getString("user");
                password = rs.getString("password");
            }
            PostgreSQL_Driver.ExecuteSQL(SQLText.getText(),host,port,databasename,user,password,ResultSQL,SQLText,Run,textResult);
        }
    }

    public void OnClickExit(){
        Alert alert =
                new Alert(Alert.AlertType.CONFIRMATION,
                        "Chắc chưa bạn?",
                        ButtonType.OK,
                        ButtonType.CANCEL);
        alert.setHeaderText("Lưu câu lệnh và xuất file excel nếu cần");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Platform.exit();
        }
    }

    public void OnClickExport(){
        if(!SQLText.getText().trim().isEmpty()){
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
            fileChooser.getExtensionFilters().add(extFilter);
            Stage stage = (Stage) Run.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);
            System.out.println(file);
            if (file != null) {
                sqlToCSV(SQLText.getText(), String.valueOf(file));
            }
        }
    }

    public void OnClickSetting(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main_Controller.class.getResource("/com/ngoclam/querytool/setting.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(Objects.requireNonNull(Main_Controller.class.getResource("/com/ngoclam/querytool/setting.css")).toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("Configure");
            String icon = Objects.requireNonNull(this.getClass().getResource("/icons/icon_setting.png")).toExternalForm();
            stage.getIcons().add(new Image(icon));
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }
        catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }
    }

    public void sqlToCSV (String query, String filename){
        try {
            FileWriter fw = new FileWriter(filename + ".csv");
            String select_query = "SELECT * FROM Configure WHERE is_selected = 'yes';";
            String host=null,port=null,databasename=null,user=null,password=null;
            try (Connection connection = SQLite_Driver.connect()){
                PreparedStatement statement = connection.prepareStatement(select_query);
                ResultSet rs = statement.executeQuery();

                while (rs.next()) {
                    host = rs.getString("host");
                    port = rs.getString("port");
                    databasename = rs.getString("name");
                    user = rs.getString("user");
                    password = rs.getString("password");
                }
            }

            String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, databasename);
            Connection con = DriverManager.getConnection(url, user, password);
            ResultSet rs = con.createStatement().executeQuery(query);
            int cols = rs.getMetaData().getColumnCount();
            for(int i = 1; i <= cols; i ++){
                fw.append(rs.getMetaData().getColumnLabel(i));
                if(i < cols) fw.append(',');
                else fw.append('\n');
            }
            while (rs.next()) {
                for(int i = 1; i <= cols; i ++){
                    fw.append(rs.getString(i));
                    if(i < cols) fw.append(',');
                }
                fw.append('\n');
            }
            fw.flush();
            fw.close();
            Notifications.create().title("Xuất file thành công")
                    .text(String.format("File đã được lưu ở đường dẫn %s.csv",filename))
                    .showInformation();
        }
        catch (Exception e) {
            File file = new File(String.format("%s.csv",filename));
            file.delete();
            Notifications.create().title("Xuất file thành công")
                    .text("Xuất file không thành công!\n Kiểm tra lại câu lệnh SQL và mạng LAN của bạn!")
                    .showError();
            SQLText.requestFocus();
        }
    }

    private void saveTextToFile(String content, File file) {
        try {
            PrintWriter writer;
            writer = new PrintWriter(file);
            writer.println(content);
            writer.close();
            Notifications.create().title("Lưu thành công")
                    .text(String.format("File đã được lưu ở đường dẫn %s.txt",file))
                    .showInformation();
        } catch (IOException ex) {
            Notifications.create().title("Lưu thành công")
                    .text(String.format("Lỗi: %s",ex))
                    .showError();
        }
    }

    public void OnClickCopy(){
        if(!SQLText.getText().trim().isEmpty())
        {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(SQLText.getText());
            clipboard.setContent(content);
            Notifications.create()
                    .title("Đã sao chép câu lệnh SQL vào Clipboard thành công!")
                    .text(String.format("Nội dung: %s",SQLText.getText()))
                    .showInformation();
        }
    }

    public void OnClickPaste(){
        Clipboard clipboard = Clipboard.getSystemClipboard();
        String stringContents = clipboard.getString();
        if (stringContents != null) {
            SQLText.setText(stringContents);
        }
    }
}