package qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.selenide.*;
import com.codeborne.selenide.selector.ByText;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.List;
import java.util.zip.*;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.*;
import static qa.TestData.*;


public class FilesTest {


    @Test
    @DisplayName("Загрузить JPG файл по абсолютному пути (при наличии файла в запускаемой среде)")
    void uploadFileFromPC() {
        open(UPLOADURL);
        File exapleFile = new File(FILEPATH);
        $("#uploadFile").uploadFile(exapleFile);
        $("#uploadedFilePath").shouldHave(Condition.text("FileName.txt"));
    }

    @Test
    @DisplayName("Загрузить JPG файл по относительному пути")
    void uploadJpgFileTest() {
        open(UPLOADURL);
        $("#uploadFile").uploadFromClasspath(FILENAME);
        $("#uploadedFilePath").shouldHave(Condition.text(FILENAME));
    }

    @Test
    @DisplayName("Скачать txt файл и проверить его содержимое")
    void downloadTextFileTest() throws IOException {
        open(DOWNLOADURL);
        File download = $("#raw-url").download();
        String fileContent = IOUtils.toString(new FileReader(download));
        assertTrue(fileContent.contains("Selenide = UI Testing Framework powered by Selenium WebDriver"));
    }

    @Test
    @DisplayName("Скачать txt файл и проверить его содержимое (ссылка на download не содержит 'href')")
    void downloadTextFileTestWithNoHref() throws IOException {
        Configuration.proxyEnabled = true;
        Configuration.fileDownload = FileDownloadMode.PROXY;

        open(DOWNLOADURL);
        File download = $("#raw-url").download();
        String fileContent = IOUtils.toString(new FileReader(download));
        assertTrue(fileContent.contains("Selenide = UI Testing Framework powered by Selenium WebDriver"));
    }

    @Test
    @DisplayName("Скачать PDF файл и проверить содержимое")
    void pdfDownloadFileTest() throws IOException {
        open(PDFDOWNLOAD);
        File pdf = $(byText("Download sample pdf file")).download();
        PDF parsedPdf = new PDF(pdf);
        Assertions.assertEquals(4, parsedPdf.numberOfPages);
    }

    @Test
    @DisplayName("Скачать XLS файл и проверить содержимое")
    void xlsDownloadFileTest() throws IOException {
        open(XLSDOWNLOAD);
        $((".row"),2).scrollTo();
        File file = $(".download-button").download();

        XLS parsedXls = new XLS(file);
        boolean checkPassed = parsedXls.excel
                .getSheetAt(0)
                .getRow(1)
                .getCell(1)
                .getStringCellValue()
                .contains(XLSTEXT);
        assertTrue(checkPassed);
    }

    @Disabled
    @Test
    @DisplayName("Парсинг ZIP файлов")
    void parseZipFileTest() throws IOException, CsvException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("zip_2MB.zip");
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                System.out.println(entry.getName());
            }
        }
    }


    @Disabled
    @Test
    @DisplayName("Парсинг CSV файлов")
    void parseCsvFileTest() throws IOException, CsvException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("csv.csv");
             Reader reader = new InputStreamReader(is)) {
            CSVReader csvReader = new CSVReader(reader);

            List<String[]> strings = csvReader.readAll();
            assertEquals(3, strings.size());
        }
    }



}