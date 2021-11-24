package qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.selenide.*;
import com.codeborne.selenide.selector.ByText;
import com.codeborne.xlstest.XLS;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.*;

import java.io.*;

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
        File file = $(byText("Download sample xls file"),0).download();

        XLS parsedXls = new XLS(file);
        boolean checkPassed = parsedXls.excel
                .getSheetAt(0)
                .getRow(1)
                .getCell(1)
                .getStringCellValue()
                .contains(XLSTEXT);
        assertTrue(checkPassed);
    }

}