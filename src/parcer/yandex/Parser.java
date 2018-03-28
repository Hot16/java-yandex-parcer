/**
 * Created by webmaster on 13.01.16.
 */
package parcer.yandex;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Parser {
    public static void main(String[] args) throws IOException {

        ArrayList<String> urlList = new ArrayList<>();
        ArrayList<String> titleList = new ArrayList<>();
        ArrayList<String> resultList = new ArrayList<>();
        ArrayList<String> resultListMch = new ArrayList<>();

		/*
		 *
		 */
		/*
		 * if (args.length != 3 && args.length != 4) { System.out.println(
		 * "Program arguments should be in format: query username password " +
		 * "[--nosave|directory]\n" +
		 * "(login and password from Yandex.XML service)"); }
		 */

        Scanner inp = new Scanner(System.in);

        String query = inp.nextLine();

        String username = ""; // log in yandex
        String password = ""; // your key
        // API
        String lr = ""; // region
        String l10n = ""; // language
        String sortBy = "rlv"; // sorting parameter
        String filter = "none"; // filter
        String groupBy = "attr%3Dd.mode%3Ddeep.groups-on-page%3D300.docs-in-group%3D1";
        String page = "";

        String pathToSave = "/home/serg/IdeaProject/YaParcer/parserResult/"; //path to save result files

        for (int i = 1; i <= 1; i++) {

            page = Integer.toString(i);
            YandexParser parser = new YandexParser(username, password, lr, filter, groupBy, page);


            try {
                parser.query(query);
            } catch (XMLQueryResultsException e) {
                System.err.println(e.getErrorString());
                System.exit(1);
            } catch (IOException e) {
                System.err
                        .println("IO error. The possible reason is aborted internet connection");
                System.exit(2);
            }
            List<Result> results = parser.getResults();
            for (Result result : results) {
                // System.out.println(result.getTitle());
                titleList.add(result.getTitle());
                // System.out.println(result.getUrl());
                urlList.add(result.getUrl());
                // System.out.println(result.getAnnotation());
                // System.out.println(result.getGreenLine());
                // System.out.println();
            }
        }
        boolean save = false; // args.length == 3 ||
        // !args[3].equals("--nosave");
/*        if (save) {
            String dirName = "yandex_results";
            if (args.length == 4) {
                dirName = args[3];
            }
            File theDir = new File(dirName);
            if (!theDir.exists()) {
                System.out.println("Creating directory: " + dirName);
                boolean result = theDir.mkdir();
                if (result) {
                    System.out.println("DIR created");
                }
            }
            System.out.println("Saving results in " + dirName);
            int maxSize = 2 << 24; // max file size in bytes
            int index = 0;
            for (Result result : results) {
                URL url = new URL(result.getUrl());
                try {
                    ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                    FileOutputStream fos = new FileOutputStream(dirName + "/"
                            + index);
                    fos.getChannel().transferFrom(rbc, 0, maxSize);
                    fos.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                ++index;

            }

        }*/

        // urlList.add("modern-it.ru");
        // urlList.add("1-shirt.ru");
        // urlList.add("shatki.info/index.php/phoneofficial");

        for (int i = 0; i < urlList.size(); i++) {  //urlList.size()
            System.out.println(i + " URL : " + urlList.get(i));
            String resultString;
            String resultStringMch;
            URLRequestResponse newRequest = new URLRequestResponse();
            String URLtext = newRequest.getResponsed(urlList.get(i));

            System.out.println("  Убираем лишние теги");
            URLtext = URLtext.trim().replace("<b>", "").replace("</b>", "").replace("<span>", "").replace("</span>", "").replace("<i>", "").replace("</i>", "").replace(","," ").replace(";"," ").replace(":"," ").replaceAll("<[.]*>", "< >");
            System.out.println("....готово");
            resultStringMch = "";

            // System.out.println("URL http://" + urlList.get(i));
            // System.out.println(URLtext);
            if (URLtext != "exception") {
                TextFilter tf = new TextFilter();
                // String urlTitle = tf.ancorExtract(URLtext, "<title>",
                // "</title>").get(0);

                String mailString = tf.textExtract(URLtext, "mail");

                resultString = urlList.get(i) + ";" + titleList.get(i) + ";"
                        + tf.textExtract(URLtext, "phone") + ";" + mailString;

                if (mailString!="" && !resultListMch.contains(mailString) && !mailString.contains("{")) {
                    resultStringMch = mailString + "," + titleList.get(i);
                }

/*                String fileName = urlList.get(i);
                fileName = fileName.replace("http://", "");
                int iO = fileName.indexOf("/");
                fileName = fileName.substring(0, iO);
                FileWriter fw = new FileWriter(
                        "/home/webmaster/IdeaProjects/YaParcer/parserResult/response_" + i + "_"
                                + fileName + ".txt");
                fw.write(URLtext);
                fw.close();  */

            } else {
                resultString = urlList.get(i)
                        + ";Page not found or other collision";

            }

            resultList.add(resultString);
            resultListMch.add(resultStringMch);
        }

        FileWriter fw = new FileWriter(pathToSave + query + "_"
                + "_" + lr + "_" + new SimpleDateFormat("ddMMyyyy_hhmm").format(new Date())
                + ".csv");
        for (int i = 0; i < resultList.size(); i++) {

            fw.write(resultList.get(i) + "\n");
        }
        fw.close();

        FileWriter fwMch = new FileWriter(pathToSave + "mCh_" + query + "_"
                + "_" + lr + "_" + new SimpleDateFormat("ddMMyyyy_hhmm").format(new Date())
                + ".csv");
        for (int i = 0; i < resultListMch.size(); i++) {
            if (resultListMch.get(i)!= "") {
                fwMch.write(resultListMch.get(i) + "\n");
            }
        }
        fwMch.close();

    }
}
