package com;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.utils.UIUtils;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;

/**
 *
 * @author Alexi Akl
 *
 */
public class ODKDownloader extends Application {

  private static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS);
  public static final File TOKENS_DIRECTORY_PATH = new java.io.File(System.getProperty("user.home"),
      ".store/sheet_tables");
  private static final String APPLICATION_NAME = "ODK-Downloader/1.0";
  public static String sheetId;
  public static String sheetName;
  public static Integer fromRowVal;
  public static Integer toRowVal;
  public static String directory1;
  public static String directory2;
  public static String directory3;
  public static String directory4;
  public static String directory5;
  public static String filename1;
  public static String filename2;
  public static String filename3;
  public static String filename4;
  public static String filename5;
  public static String imageColumns;
  public static HashMap<String, Integer> columnsMap = new HashMap<String, Integer>();

  static boolean processShouldBeRunning = false;
  static Thread processThread;
  private Preferences prefs;
  private static Sheets service;

  private static NetHttpTransport HTTP_TRANSPORT;

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    prefs = Preferences.userRoot().node(this.getClass().getName());
    sheetId = prefs.get("sheetId", "1ZyRnBgtr-GIYpa6Hi6LLh9gwt16Ev8mG1bEuUaQU1EY");
    sheetName = prefs.get("sheetName", "UB Doctor X ICBA");
    directory1 = prefs.get("directory1", "");
    directory2 = prefs.get("directory2", "");
    directory3 = prefs.get("directory3", "");
    directory4 = prefs.get("directory4", "");
    directory5 = prefs.get("directory5", "");
    filename1 = prefs.get("filename1", "metainstanceid");
    filename2 = prefs.get("filename2", "");
    filename3 = prefs.get("filename3", "");
    filename4 = prefs.get("filename4", "");
    filename5 = prefs.get("filename5", "");
    imageColumns = prefs.get("imageColumns", "image1,image2,image3,image4,image5,image6");
    Scene scene = UIUtils.buildUI();
    primaryStage.setTitle("ODK Downloader");
    primaryStage.setScene(scene);
    primaryStage.show();

    HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    initFusionAPI();
    initApplication();
  }

  private void initApplication() {
    UIUtils.buttonSave.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        savePressed();
      }
    });

    UIUtils.buttonStart.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        startPressed();
      }
    });

    UIUtils.buttonStop.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        stopPressed();
      }
    });
  }

  protected void savePressed() {
    if (UIUtils.buttonStart.isDisabled()) {
      return;
    }

    String tempSheetId = UIUtils.sheetId.getText();
    if (tempSheetId.compareTo(sheetId) != 0) {
      sheetId = tempSheetId;
      UIUtils.appendLog("Storing sheet ID");
      prefs.put("sheetId", tempSheetId);
    }

    String tempSheetName = UIUtils.sheetName.getText();
    if (tempSheetName.compareTo(sheetName) != 0) {
      sheetName = tempSheetName;
      UIUtils.appendLog("Storing sheet name");
      prefs.put("sheetName", tempSheetName);
    }

    boolean directoryChanged = false;
    String tempDirectory1 = UIUtils.directory1.getText();
    if (tempDirectory1.compareTo(directory1) != 0) {
      directory1 = tempDirectory1;
      prefs.put("directory1", tempDirectory1);
      directoryChanged = true;
    }

    String tempDirectory2 = UIUtils.directory2.getText();
    if (tempDirectory2.compareTo(directory2) != 0) {
      directory2 = tempDirectory2;
      prefs.put("directory2", tempDirectory2);
      directoryChanged = true;
    }

    String tempDirectory3 = UIUtils.directory3.getText();
    if (tempDirectory3.compareTo(directory3) != 0) {
      directory3 = tempDirectory3;
      prefs.put("directory3", tempDirectory3);
      directoryChanged = true;
    }

    String tempDirectory4 = UIUtils.directory4.getText();
    if (tempDirectory4.compareTo(directory4) != 0) {
      directory4 = tempDirectory4;
      prefs.put("directory4", tempDirectory4);
      directoryChanged = true;
    }

    String tempDirectory5 = UIUtils.directory5.getText();
    if (tempDirectory5.compareTo(directory5) != 0) {
      directory5 = tempDirectory5;
      prefs.put("directory5", tempDirectory5);
      directoryChanged = true;
    }

    if (directoryChanged) {
      UIUtils.appendLog("Storing new directory structure");
    }

    boolean filenameChanged = false;
    String tempFilename1 = UIUtils.filename1.getText();
    if (tempFilename1.compareTo(filename1) != 0) {
      filename1 = tempFilename1;
      prefs.put("filename1", tempFilename1);
      filenameChanged = true;
    }

    String tempFilename2 = UIUtils.filename2.getText();
    if (tempFilename2.compareTo(filename2) != 0) {
      filename2 = tempFilename2;
      prefs.put("filename2", tempFilename2);
      filenameChanged = true;
    }

    String tempFilename3 = UIUtils.filename3.getText();
    if (tempFilename3.compareTo(filename3) != 0) {
      filename3 = tempFilename3;
      prefs.put("filename3", tempFilename3);
      filenameChanged = true;
    }

    String tempFilename4 = UIUtils.filename4.getText();
    if (tempFilename4.compareTo(filename4) != 0) {
      filename4 = tempFilename4;
      prefs.put("filename4", tempFilename4);
      filenameChanged = true;
    }

    String tempFilename5 = UIUtils.filename5.getText();
    if (tempFilename5.compareTo(filename5) != 0) {
      filename5 = tempFilename5;
      prefs.put("filename5", tempFilename5);
      filenameChanged = true;
    }

    if (filenameChanged) {
      UIUtils.appendLog("Storing new filename structure");
    }

    String tempImageColumns = UIUtils.imageColumns.getText();
    if (tempImageColumns.compareTo(imageColumns) != 0) {
      imageColumns = tempImageColumns;
      prefs.put("imageColumns", tempImageColumns);
      UIUtils.appendLog("Storing new Image Columns");
    }

    try {
      prefs.sync();
      UIUtils.settingsStage.hide();
    } catch (BackingStoreException exception) {
      exception.printStackTrace();
    }
  }

  void startPressed() {
    UIUtils.imageView.setImage(null);
    String fromRow = UIUtils.fromRow.getText();
    String toRow = UIUtils.toRow.getText();

    try {
      fromRowVal = Integer.parseInt(fromRow);
      toRowVal = Integer.parseInt(toRow);
      if (fromRowVal > toRowVal || fromRowVal < 1) {
        UIUtils.appendLog("From Row and/or To Row are invalid");
        return;
      }
    } catch (Exception e) {
      UIUtils.appendLog("From Row and/or To Row are invalid");
      return;
    }

    UIUtils.buttonStart.setDisable(true);
    UIUtils.toggleEditing(false);
    UIUtils.appendLog("Starting process");
    UIUtils.appendLog("Sheet ID: " + sheetId);
    UIUtils.appendLog("Sheet Name: " + sheetName);
    if (UIUtils.processAllCheckbox.isSelected()) {
      UIUtils.appendLog("Processing all records in table");
    } else {
      UIUtils.appendLog("Rows: [" + fromRow + ", " + toRow + "]");
    }

    processThread = new Thread(new Runnable() {
      @Override
      public void run() {
        processRows();
      }
    });
    processThread.start();
  }

  void stopPressed() {
    processShouldBeRunning = false;
    UIUtils.appendLog("Process will halt once download of current image is complete");
    UIUtils.appendLog("Please wait");
    UIUtils.buttonStop.setDisable(true);
  }

  public static void initFusionAPI() {
    try {
      service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
          .setApplicationName(APPLICATION_NAME).build();
    } catch (Exception e) {
      UIUtils.appendLog("Sheets API failed to initialize: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Creates an authorized Credential object.
   *
   * @param HTTP_TRANSPORT The network HTTP Transport.
   * @return An authorized Credential object.
   * @throws IOException If the credentials.json file cannot be found.
   */
  private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
    InputStream in = ODKDownloader.class.getResourceAsStream("/client_secrets.json");
    if (in == null) {
      in = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/client_secrets.json");
    }
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    // Build flow and trigger user authorization request.
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
        clientSecrets, SCOPES).setDataStoreFactory(new FileDataStoreFactory(TOKENS_DIRECTORY_PATH))
            .setAccessType("offline").build();
    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
    return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
  }

  static void processRows() {
    try {
      processShouldBeRunning = true;
      UIUtils.buttonStop.setDisable(false);
      columnsMap.clear();

      final String[] imageCols = imageColumns.split(",");

      ValueRange columnsResponse = service.spreadsheets().values().get(sheetId, "!1:1").execute();
      List<List<Object>> columnValues = columnsResponse.getValues();
      if (columnValues == null || columnValues.isEmpty()) {
        UIUtils.appendLog("No columns found.");
        return;
      }
      for (List<?> columns : columnValues) {
        for (int i = 0; i < columns.size(); i++) {
          String columnName = columns.get(i).toString().toLowerCase();
          columnsMap.put(columnName, Integer.valueOf(i));
        }
      }

      StringBuilder range = new StringBuilder("!");
      range.append(fromRowVal).append(":").append(toRowVal);
      ValueRange response = service.spreadsheets().values().get(sheetId, range.toString()).execute();

      boolean processAll = UIUtils.processAllCheckbox.isSelected();
      Integer filterIndex = null, dateIndex = null;
      LocalDate filterFromDate = null, filterToDate = null;
      if (!processAll) {
        String filterKey = UIUtils.columnNameFilterKey.getText();
        filterIndex = columnsMap.get(filterKey);
        dateIndex = columnsMap.get("metasubmissiondate");
        filterFromDate = UIUtils.fromDatePicker.getValue();
        filterToDate = UIUtils.toDatePicker.getValue();
      }
      List<List<Object>> values = response.getValues();
      if (values == null || values.isEmpty()) {
        UIUtils.appendLog("No data found.");
      } else {
        UIUtils.appendLog("Records count: " + values.size());
        int counter = -1;
        HashMap<Integer, Boolean> shouldProcess = new HashMap<>();
        for (List<?> row : values) {
          ++counter;
          boolean process = false;
          process = processAll;
          if (!process) {
            boolean skip = false;

            boolean inRange = counter < toRowVal;
            if (!inRange) {
              process = false;
              skip = true;
            } else {
              process = true;
            }

            if (!skip && filterIndex != null && filterIndex > -1 && row.size() > filterIndex) {
              String filterValue = row.get(filterIndex).toString();
              if (UIUtils.columnNameFilterValue.getText().compareToIgnoreCase(filterValue) != 0) {
                process = false;
                skip = true;
              } else {
                process = true;
              }
            }

            boolean checkDate = dateIndex != null && dateIndex > -1 && filterFromDate != null && filterToDate != null
                && row.size() > dateIndex;
            if (!skip && checkDate) {
              String dateValue = row.get(dateIndex).toString();
              try {
                dateValue = dateValue.split(" ")[0];
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                LocalDate date = LocalDate.parse(dateValue, formatter);
                process = (date.isEqual(filterFromDate) || date.isAfter(filterFromDate))
                    && (date.isEqual(filterToDate) || date.isBefore(filterToDate));
              } catch (Exception e) {
              }
            }
          }

          if (process) {
            try {
              shouldProcess.put(Integer.valueOf(counter), true);
            } catch (Exception e) {
            }
          }
        }

        counter = -1;
        int processed = 0;
        UIUtils.appendLog("Records to process: " + shouldProcess.size());
        for (List<?> row : values) {
          ++counter;
          boolean atLeastOneValid = false;
          if (shouldProcess.containsKey(Integer.valueOf(counter))) {
            processed++;
            final int recordNumber = counter + fromRowVal;
            for (int i = 0; i < imageCols.length; i++) {
              final String imgCol = imageCols[i];
              if (!columnsMap.containsKey(imgCol)) {
                continue;
              }
              int imageColumnIndex = columnsMap.get(imgCol);
              String imageString = row.get(imageColumnIndex).toString();
              boolean validImage = imageString.startsWith("http");
              if (!validImage) {
                continue;
              }
              atLeastOneValid = true;
              URL imageurl = new URL(imageString);
              if (shouldHalt()) {
                return;
              }

              UIUtils.appendLog("Downloading - Row: " + recordNumber + ", Column: " + imgCol);
              final BufferedImage image = ImageIO.read(imageurl);

              String directoryName = getDirectory(row);
              String fileName = getFilename(row, imgCol);

              File directory = new File(directoryName);
              if (!directory.exists()) {
                if (!directory.mkdirs()) {
                  UIUtils.appendLog("Failed to create directory " + directory.getPath());
                  return;
                }
              }
              File file = new File(directory.getAbsolutePath() + File.separator + fileName);
              try {
                if (!ImageIO.write(image, "jpg", file)) {
                  UIUtils.appendLog("Write unsuccessful for " + file.getPath());
                }
              } catch (IOException e) {
                UIUtils.appendLog("Write error for " + file.getPath() + ": " + e.getMessage());
              }

              if (shouldHalt()) {
                return;
              }

              Platform.runLater(new Runnable() {
                @Override
                public void run() {
                  Image transimage = SwingFXUtils.toFXImage(image, null);
                  UIUtils.imageView.setImage(transimage);
                  UIUtils.imageTitle.setText("Row " + recordNumber + ", " + imgCol);
                }
              });
            }
            if (!atLeastOneValid) {
              UIUtils.appendLog("No image records in Row " + recordNumber);
            }
            UIUtils.appendLog("Progress: " + processed + " / " + shouldProcess.size());
          }
        }
        UIUtils.appendLog("Records processed: " + processed);
      }

      processShouldBeRunning = false;
      UIUtils.buttonStart.setDisable(false);
      UIUtils.toggleEditing(true);
      UIUtils.buttonStop.setDisable(true);
      new Timer().schedule(new java.util.TimerTask() {
        @Override
        public void run() {
          UIUtils.appendLog("Done");
        }
      }, 500);
    } catch (Exception e) {
      UIUtils.appendLog("Error while processing images: " + e.getMessage());
      UIUtils.appendLog("Process Halted");
      UIUtils.buttonStart.setDisable(false);
      UIUtils.toggleEditing(true);
      UIUtils.buttonStop.setDisable(true);
      e.printStackTrace();
    }
  }

  private static final String getSafeRowValue(List<?> row, String key) {
    if (!key.isEmpty()) {
      Integer index = columnsMap.get(key);
      if (index != null) {
        String value = row.get(index).toString();
        if (value != null && !value.isEmpty()) {
          return value.replaceAll("[^a-zA-Z0-9\\-\\._]+", "_");
        }
      }
    }
    return "";
  }

  private static final String getDirectory(List<?> row) {
    String directory = "downloads" + File.separator;
    String subDirectory1 = getSafeRowValue(row, directory1);
    String subDirectory2 = getSafeRowValue(row, directory2);
    String subDirectory3 = getSafeRowValue(row, directory3);
    String subDirectory4 = getSafeRowValue(row, directory4);
    String subDirectory5 = getSafeRowValue(row, directory5);

    if (!subDirectory1.isEmpty()) {
      directory += subDirectory1 + File.separator;
    }
    if (!subDirectory2.isEmpty()) {
      directory += subDirectory2 + File.separator;
    }
    if (!subDirectory3.isEmpty()) {
      directory += subDirectory3 + File.separator;
    }
    if (!subDirectory4.isEmpty()) {
      directory += subDirectory4 + File.separator;
    }
    if (!subDirectory5.isEmpty()) {
      directory += subDirectory5 + File.separator;
    }
    return directory;
  }

  private static final String getFilename(List<?> row, String col) {
    String filename = "";
    String subFilename1 = getSafeRowValue(row, filename1);
    String subFilename2 = getSafeRowValue(row, filename2);
    String subFilename3 = getSafeRowValue(row, filename3);
    String subFilename4 = getSafeRowValue(row, filename4);
    String subFilename5 = getSafeRowValue(row, filename5);

    if (!subFilename1.isEmpty()) {
      filename += subFilename1;
    }
    if (!subFilename2.isEmpty()) {
      if (!filename.isEmpty())
        filename += "-";
      filename += subFilename2;
    }
    if (!subFilename3.isEmpty()) {
      if (!filename.isEmpty())
        filename += "-";
      filename += subFilename3;
    }
    if (!subFilename4.isEmpty()) {
      if (!filename.isEmpty())
        filename += "-";
      filename += subFilename4;
    }
    if (!subFilename5.isEmpty()) {
      if (!filename.isEmpty())
        filename += "-";
      filename += subFilename5;
    }
    return filename + "-" + col + ".jpg";
  }

  private static boolean shouldHalt() {
    if (!processShouldBeRunning) {
      UIUtils.appendLog("Process Halted");
      UIUtils.buttonStart.setDisable(false);
      UIUtils.toggleEditing(true);
      return true;
    }
    return false;
  }
}
