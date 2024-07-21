import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PhotoRenamer {

    // ugly file - 20240713_171249.jpg
    // nice file - 2024-07-14 09.27.15.jpg

    private static final String ROOT_DIR = "./";

    private static final DateFormat UGLY_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private static final DateFormat NICE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");

    public static void main(String[] args) throws IOException, ParseException {
        List<Path> allFiles = Files.list(Paths.get(ROOT_DIR)).toList();
        List<Path> files = getPhotosMatchingNamingScheme(allFiles);
        // count files
        System.out.println("No. of matching files in current dir: " + files.size());
        System.out.println("Files to be changed: ");
        // list names
        files.forEach(file -> {
            System.out.println(file.getFileName());
        });
        // create a new dir
        Path outputPath = Files.createDirectory(Path.of(ROOT_DIR + "/Output"));
        // save renamed files in output dir
        saveRenamedFiles(files, outputPath);
    }

    private static List<Path> getPhotosMatchingNamingScheme(List<Path> allFiles) {
        return allFiles.stream()
                .filter(PhotoRenamer::isPhotoMatchingUglyFormat)
                .toList();
    }

    private static boolean isPhotoMatchingUglyFormat(Path path) {
        if (!path.toString().endsWith(".jpg")) { // gets rid of dirs, among other things
            return false;
        }
        String fileNameWithoutExtension = getFilenameWithoutExtension(path);
        try {
            UGLY_FORMAT.parse(fileNameWithoutExtension);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    private static void saveRenamedFiles(List<Path> files, Path outputPath) throws IOException, ParseException {
        for (Path file : files) {
            Date dateFromFilename = UGLY_FORMAT.parse(getFilenameWithoutExtension(file));
            String newFilename = NICE_FORMAT.format(dateFromFilename) + ".jpg";
            Files.copy(file, Path.of(outputPath + "/" + newFilename));
        }
    }

    private static String getFilenameWithoutExtension(Path path) {
        return path.getFileName()
                .toString()
                .substring(0, path.getFileName().toString().lastIndexOf("."));
    }
}