import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class M3USeparator {

    private File file;
    private HashMap<String, StringBuilder> groupTitleFileContentMap = new HashMap<>();

    public M3USeparator(File file) {
        this.file = file;
        generateSeparateContent();
        generateSeparateFiles();
    }

    private void generateSeparateContent() {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();

            while ((line = br.readLine()) != null) {
                updateContentMap(line, br);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateContentMap(String line, BufferedReader br) {
        final String M3U_INITIAL_LINE = "#EXTM3U\n";

        String groupTitle = groupTitleExtractor(line);
        StringBuilder stringBuilder = groupTitleFileContentMap.getOrDefault(groupTitle, new StringBuilder(M3U_INITIAL_LINE));
        stringBuilder.append(line + "\n");

        try {
            line = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stringBuilder.append(line + "\n");
        groupTitleFileContentMap.put(groupTitle, stringBuilder);
    }

    private void generateSeparateFiles() {
        System.out.println(groupTitleFileContentMap.keySet().size());
        String pwd = System.getProperty("user.dir");
        final String groupTitleFilesDirectory = pwd + "\\groupTitleFiles";
        File groupTitleFilesDirectoryFile = new File(groupTitleFilesDirectory);
        groupTitleFilesDirectoryFile.mkdirs();

        for(String key : groupTitleFileContentMap.keySet()) {
            System.out.println("key = " + key);
            final String fileName = key.replace(" ", "_").replace("(", "_").replace(")", "_").replace("\\\\", "_").replace("/", "_").concat(".m3u");
            StringBuilder stringBuilder = groupTitleFileContentMap.get(key);
            File newFile = new File(groupTitleFilesDirectory + "\\" + fileName);
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(newFile));
                writer.append(stringBuilder);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private String groupTitleExtractor(String line) {
        Pattern pattern = Pattern.compile("group-title=\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String groupTitle = matcher.group(1);
            //System.out.println(groupTitle);
            return groupTitle;
        }
        else {
            return null;
        }
    }
}
