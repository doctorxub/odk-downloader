# ODK Downloader

Quick description of the Application:

1. Set Sheet ID, Sheet name, Rows range, Directory and Filename structure in the Settings pane
2. Authorize application by clicking Re-Auth
3. Process records by clicking Start
4. Images will be stored in downloads folder according to the structure set in Settings

## Start Application

Make sure you have Java 11: https://adoptopenjdk.net/

On Windows, start the application by double clicking ODKDownloader.bat or running in the command line:

`java --module-path libs\win\javafx-sdk-14.0.2.1\lib\ --add-modules javafx.controls,javafx.fxml,javafx.swing -jar odk-downloader-1.0.0.jar`

On Mac (Make sure you have Mac javafx libraries downloaded):

`java --module-path libs/mac/javafx-sdk-14.0.2.1/lib/ --add-modules javafx.controls,javafx.fxml,javafx.swing -jar odk-downloader-1.0.0.jar`

Optional, You can use Maven (example for windows):

- install libraries: `mvn install -Pwin`
- package jar: `mvn package -Pwin`
- run application: `mvn javafx:run -Pwin`

To install Maven: http://www.baeldung.com/install-maven-on-windows-linux-mac

## Filtering

1. Process all records available in the Google Sheet by checking `Process all records in table`
2. Filter records to process by unchecking `Process all records in table` and picking your filter options:
   - Set `Column key` and `column value` to filter by column value, eg: `treatment = road`
   - Set `From date` and `To Date` to filter by `metasubmissiondate` date column

## Sheets API

To connect to the Sheets API you need client id and secret which are saved in `src/main/resources/client_secrets.json`

If you wish to use new credentials you need to:

1. create OAuth 2.0 client ID here: https://console.cloud.google.com/apis/credentials?project=universitatbarcelonadoctorxlai
2. Update the `client_secrets.json` file
3. Optional: Create a new runnable JAR file if you choose to run it using a JAR

# Authors

- Dr. Shawn Kefauver
  - Project Principal Investigator, University of Barcelona
- [Jose Armando Fernandez Gallego](https://integrativecropecophysiology.com/academic-staff/phd-students/fernandez-gallego-jose-armando/)
  - Algorithm Development, University of Barcelona
- Alexi Akl
  - Software Engineer, [Postlight](https://postlight.com)

# License

Copyright 2018 Shawn Carlisle Kefauver

Licensed under the General Public License version 3.0

- [http://www.gnu.org/licenses/gpl-3.0.en.html](http://www.gnu.org/licenses/gpl-3.0.en.html)
- [https://tldrlegal.com/license/gnu-general-public-license-v3-(gpl-3)](<https://tldrlegal.com/license/gnu-general-public-license-v3-(gpl-3)>)
