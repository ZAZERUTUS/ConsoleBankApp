package org.example.statement_files;

import java.io.*;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FileWriterPdfTxt {

    String dirName;
    String content;
    String fileName;

    public void saveStatementPDF() {
        File folder = createFolder(dirName);
        String pathForSave = folder.getPath() + File.separatorChar + fileName + ".pdf";
        try {
            Reader inputString = new StringReader(content);
            BufferedReader reader = new BufferedReader(inputString);
            BufferedReader input = new BufferedReader (reader);
            Document output = new Document(PageSize.A4);
            PdfWriter.getInstance(output, new FileOutputStream(pathForSave));

            output.open();
            output.addAuthor("ConsoleApp");
            BaseFont unicode = BaseFont.createFont(
                    "./robo.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font = new Font(unicode, 12);

            String line = "";
            while(null != (line = input.readLine())) {
                Paragraph p = new Paragraph(line, font);
                output.add(p);
            }
            output.close();
            input.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveCheckFileTXT() {
        File folder = createFolder(dirName);
        File checkFile = new File(folder.getPath() + File.separatorChar + fileName + ".txt");
        try (FileWriter fileWriter = new FileWriter(checkFile)){
            fileWriter.append(content).flush();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private File createFolder(String dirName) {
        File folder = new File(System.getProperty("user.dir") + File.separatorChar + dirName);
        if (!folder.exists()) {
            folder.mkdir();
        }
        return folder;
    }
}
