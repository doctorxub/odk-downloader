package com.utils;

import com.ODKDownloader;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

/**
 *
 * @author Alexi Akl
 *
 */
public class UIUtils {
  public static TextArea logArea;
  public static TextField sheetId;
  public static TextField sheetName;
  public static TextField fromRow;
  public static TextField toRow;
  public static TextField columns;
  public static TextField directory1;
  public static TextField directory2;
  public static TextField directory3;
  public static TextField directory4;
  public static TextField directory5;
  public static TextField filename1;
  public static TextField filename2;
  public static TextField filename3;
  public static TextField filename4;
  public static TextField filename5;
  public static TextField imageColumns;
  public static ImageView imageView;
  public static Text imageTitle;
  public static Button buttonSave;
  public static Button buttonSettings;
  public static Button buttonStart;
  public static Button buttonStop;
  public static CheckBox processAllCheckbox;
  public static TextField columnNameFilterKey;
  public static TextField columnNameFilterValue;
  public static DatePicker fromDatePicker;
  public static DatePicker toDatePicker;
  public static Stage settingsStage;
  public static VBox filtervbox;
  private static StringConverter<LocalDate> dateConverter;

  public static Scene buildUI() {
    dateConverter = new StringConverter<LocalDate>() {
      private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

      @Override
      public String toString(LocalDate localDate) {
        if (localDate == null) {
          return "";
        }
        return dateTimeFormatter.format(localDate);
      }

      @Override
      public LocalDate fromString(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
          return null;
        }
        return LocalDate.parse(dateString, dateTimeFormatter);
      }
    };

    createSettingsStage();

    BorderPane root = new BorderPane();
    HBox hbox = addHBox();
    root.setTop(hbox);
    VBox vbox = addVBox();
    root.setLeft(vbox);
    VBox imagevbox = addImageVBox();
    root.setCenter(imagevbox);

    return new Scene(root, 820, 600);
  }

  public static void appendLog(final String string) {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    final String append = sdf.format(cal.getTime()) + " " + string + "\n";
    if (Platform.isFxApplicationThread()) {
      logArea.setText(append + logArea.getText());
    } else {
      Platform.runLater(new Runnable() {
        @Override
        public void run() {
          logArea.setText(append + logArea.getText());
        }
      });
    }
  }

  public static void toggleEditing(boolean enable) {
    filtervbox.setDisable(!enable);
    processAllCheckbox.setDisable(!enable);
    buttonSettings.setDisable(!enable);
  }

  public static VBox addImageVBox() {
    VBox vbox = new VBox();
    vbox.setPadding(new Insets(10));
    vbox.setSpacing(8);

    imageTitle = new Text("Images");
    imageTitle.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
    vbox.getChildren().add(imageTitle);

    imageView = new ImageView();
    imageView.setFitHeight(350);
    imageView.setFitWidth(350);
    imageView.setPreserveRatio(true);

    vbox.getChildren().add(imageView);

    return vbox;
  }

  private static Node getProcessAllCheckbox() {
    processAllCheckbox = new CheckBox("Process all records in table");
    processAllCheckbox.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
    processAllCheckbox.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        filtervbox.setVisible(!processAllCheckbox.isSelected());
      }
    });
    return processAllCheckbox;
  }

  public static VBox addVBox() {
    VBox vbox = new VBox();
    vbox.setPadding(new Insets(10));
    vbox.setSpacing(8);

    Text title = new Text("Logs");
    title.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
    vbox.getChildren().add(title);

    logArea = new TextArea();
    logArea.setPrefWidth(400);
    logArea.setEditable(false);
    logArea.setFont(Font.font("Arial", FontWeight.EXTRA_LIGHT, 10));
    vbox.getChildren().add(logArea);

    filtervbox = new VBox();
    filtervbox.setPadding(new Insets(10));
    filtervbox.setSpacing(8);

    Text fromRowToRowLabel = new Text("Row Range");
    fromRowToRowLabel.setWrappingWidth(80);
    fromRowToRowLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));

    fromRow = new TextField("2");
    fromRow.setPromptText("From Row");
    fromRow.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

    toRow = new TextField("50000");
    toRow.setPromptText("To Row");
    toRow.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

    HBox fromtohbox = new HBox();
    fromtohbox.setPadding(new Insets(0, 0, 0, 0));
    fromtohbox.setSpacing(2);
    fromtohbox.getChildren().add(fromRowToRowLabel);
    fromtohbox.getChildren().add(fromRow);
    fromtohbox.getChildren().add(toRow);

    HBox columnhbox = new HBox();
    columnhbox.setPadding(new Insets(0, 0, 0, 0));
    columnhbox.setSpacing(2);

    Text columnLabel = new Text("Col Value");
    columnLabel.setWrappingWidth(80);
    columnLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));

    columnNameFilterKey = new TextField("");
    columnNameFilterKey.setPromptText("Column Name");
    columnNameFilterKey.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

    columnNameFilterValue = new TextField("");
    columnNameFilterValue.setPromptText("Column Value");
    columnNameFilterValue.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

    columnhbox.getChildren().add(columnLabel);
    columnhbox.getChildren().add(columnNameFilterKey);
    columnhbox.getChildren().add(columnNameFilterValue);

    HBox datehbox = new HBox();
    datehbox.setPadding(new Insets(0, 0, 0, 0));
    datehbox.setSpacing(2);

    Text dateLabel = new Text("Date Range");
    dateLabel.setWrappingWidth(80);
    dateLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));

    fromDatePicker = new DatePicker();
    fromDatePicker.setConverter(dateConverter);
    toDatePicker = new DatePicker();
    toDatePicker.setConverter(dateConverter);
    datehbox.getChildren().add(dateLabel);
    datehbox.getChildren().add(fromDatePicker);
    datehbox.getChildren().add(toDatePicker);

    filtervbox.getChildren().add(fromtohbox);
    filtervbox.getChildren().add(columnhbox);
    filtervbox.getChildren().add(datehbox);

    vbox.getChildren().add(getProcessAllCheckbox());
    vbox.getChildren().add(filtervbox);
    return vbox;
  }

  public static HBox addHBox() {
    HBox hbox = new HBox();
    hbox.setPadding(new Insets(15, 12, 15, 12));
    hbox.setSpacing(10);
    hbox.setStyle("-fx-background-color: #336699;");

    buttonSettings = new Button("Settings");
    buttonSettings.setPrefSize(100, 20);
    buttonSettings.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        openSettings(event);
      }
    });

    buttonStart = new Button("Start");
    buttonStart.setPrefSize(100, 20);

    buttonStop = new Button("Stop");
    buttonStop.setPrefSize(100, 20);
    buttonStop.setDisable(true);

    Button buttonClear = new Button("Clear logs");
    buttonClear.setPrefSize(100, 20);
    buttonClear.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        logArea.setText("");
      }
    });

    Button buttonRefreshToken = new Button("Re-Auth");
    buttonRefreshToken.setPrefSize(100, 20);
    buttonRefreshToken.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        String[] entries = ODKDownloader.TOKENS_DIRECTORY_PATH.list();
        for (String s : entries) {
          File currentFile = new File(ODKDownloader.TOKENS_DIRECTORY_PATH.getPath(), s);
          currentFile.delete();
        }
        ODKDownloader.initFusionAPI();
      }
    });
    hbox.getChildren().addAll(buttonSettings, buttonStart, buttonStop, buttonClear, buttonRefreshToken);

    return hbox;
  }

  static void createSettingsStage() {
    settingsStage = new Stage();

    VBox vbox = new VBox();
    vbox.setPadding(new Insets(10));
    vbox.setSpacing(8);

    Text sheetIdLabel = new Text("Sheet ID");
    sheetIdLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

    sheetId = new TextField(ODKDownloader.sheetId);
    sheetId.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

    Text sheetNameLabel = new Text("Sheet Name");
    sheetNameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

    sheetName = new TextField(ODKDownloader.sheetName);
    sheetName.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

    Text directoryStructureLabel = new Text("Directory Structure");
    directoryStructureLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

    directory1 = new TextField(ODKDownloader.directory1);
    directory1.setPromptText("column name");
    directory1.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
    directory2 = new TextField(ODKDownloader.directory2);
    directory2.setPromptText("column name");
    directory2.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
    directory3 = new TextField(ODKDownloader.directory3);
    directory3.setPromptText("column name");
    directory3.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
    directory4 = new TextField(ODKDownloader.directory4);
    directory4.setPromptText("column name");
    directory4.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
    directory5 = new TextField(ODKDownloader.directory5);
    directory5.setPromptText("column name");
    directory5.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

    Text separator1 = new Text(File.separator);
    separator1.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
    Text separator2 = new Text(File.separator);
    separator2.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
    Text separator3 = new Text(File.separator);
    separator3.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
    Text separator4 = new Text(File.separator);
    separator4.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

    HBox directorieshbox = new HBox();
    directorieshbox.setPadding(new Insets(0, 0, 0, 0));
    directorieshbox.setSpacing(2);
    directorieshbox.getChildren().add(directory1);
    directorieshbox.getChildren().add(separator1);
    directorieshbox.getChildren().add(directory2);
    directorieshbox.getChildren().add(separator2);
    directorieshbox.getChildren().add(directory3);
    directorieshbox.getChildren().add(separator3);
    directorieshbox.getChildren().add(directory4);
    directorieshbox.getChildren().add(separator4);
    directorieshbox.getChildren().add(directory5);

    Text filenameStructureLabel = new Text("Filename Structure");
    filenameStructureLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

    filename1 = new TextField(ODKDownloader.filename1);
    filename1.setPromptText("column name");
    filename1.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
    filename2 = new TextField(ODKDownloader.filename2);
    filename2.setPromptText("column name");
    filename2.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
    filename3 = new TextField(ODKDownloader.filename3);
    filename3.setPromptText("column name");
    filename3.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
    filename4 = new TextField(ODKDownloader.filename4);
    filename4.setPromptText("column name");
    filename4.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
    filename5 = new TextField(ODKDownloader.filename5);
    filename5.setPromptText("column name");
    filename5.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

    Text fseparator1 = new Text("-");
    fseparator1.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
    Text fseparator2 = new Text("-");
    fseparator2.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
    Text fseparator3 = new Text("-");
    fseparator3.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
    Text fseparator4 = new Text("-");
    fseparator4.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

    HBox filenamehbox = new HBox();
    filenamehbox.setPadding(new Insets(0, 0, 0, 0));
    filenamehbox.setSpacing(2);
    filenamehbox.getChildren().add(filename1);
    filenamehbox.getChildren().add(fseparator1);
    filenamehbox.getChildren().add(filename2);
    filenamehbox.getChildren().add(fseparator2);
    filenamehbox.getChildren().add(filename3);
    filenamehbox.getChildren().add(fseparator3);
    filenamehbox.getChildren().add(filename4);
    filenamehbox.getChildren().add(fseparator4);
    filenamehbox.getChildren().add(filename5);

    Text imagesColumnsLabel = new Text("Image columns to download - comma separated");
    imagesColumnsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

    imageColumns = new TextField(ODKDownloader.imageColumns);
    imageColumns.setPromptText("column name");
    imageColumns.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

    vbox.getChildren().add(sheetIdLabel);
    vbox.getChildren().add(sheetId);
    vbox.getChildren().add(sheetNameLabel);
    vbox.getChildren().add(sheetName);
    vbox.getChildren().add(directoryStructureLabel);
    vbox.getChildren().add(directorieshbox);
    vbox.getChildren().add(filenameStructureLabel);
    vbox.getChildren().add(filenamehbox);
    vbox.getChildren().add(imagesColumnsLabel);
    vbox.getChildren().add(imageColumns);

    HBox buttonsBox = new HBox();
    buttonsBox.setSpacing(8);

    buttonSave = new Button("Save");
    buttonSave.setPrefSize(100, 20);
    buttonsBox.getChildren().add(buttonSave);

    Button buttonClose = new Button("Cancel");
    buttonClose.setPrefSize(100, 20);
    buttonsBox.getChildren().add(buttonClose);
    buttonClose.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        settingsStage.hide();
      }
    });

    vbox.getChildren().add(buttonsBox);

    Scene settingsScene = new Scene(vbox, 680, 400);

    settingsStage.setScene(settingsScene);
    settingsStage.setTitle("Settings");
    settingsStage.initModality(Modality.WINDOW_MODAL);
  }

  static void openSettings(ActionEvent event) {
    sheetId.setText(ODKDownloader.sheetId);
    sheetName.setText(ODKDownloader.sheetName);
    if (settingsStage.getOwner() == null) {
      settingsStage.initOwner(((Node) event.getSource()).getScene().getWindow());
    }
    settingsStage.show();
  }
}
