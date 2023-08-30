package org.example.statement_files;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TxtFileWriter {

    String dirName;
    String content;
    String fileName;


    public void saveCheckFile() {
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
