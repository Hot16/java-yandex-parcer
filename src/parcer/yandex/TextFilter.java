/**
 * Created by webmaster on 13.01.16.
 */
package parcer.yandex;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextFilter {
    public ArrayList<String> ancorExtract(String URLText, String startTag, String endTag) {
        URLText = URLText.trim();
        ArrayList<String> ancor = new ArrayList<>();
        int startAncor = 0;
        while (startAncor <= URLText.lastIndexOf(startTag)) {
            startAncor = URLText.indexOf(startTag, startAncor);
            int finalAncor = URLText.indexOf(endTag, startAncor);
            ancor.add(URLText.substring(startAncor + startTag.length(),
                    finalAncor));
            startAncor += finalAncor;
        }
        return ancor;
    }

    public String textExtract(String URLText, String choiseParam) {
        // StringBuffer bufferURLText = new StringBuffer(URLText);

        System.out.println("  Ищем ценную инфу");

        //URLText = URLText.trim().replace(","," ").replace(";"," ").replace(":"," ");

        String phoneString = "";
        String mailString = "";
        int startText = 0;
        int finalURLText = URLText.lastIndexOf("<");
        while (startText < finalURLText) {
//            System.out.println(startText + " -- " + finalURLText);
            startText = URLText.indexOf(">", startText);
            if (startText > 0) {
                int finalText = URLText.indexOf("<", startText);

                if (finalText > 0) {
                    if ((finalText - startText) > 4) {
                        // System.out.println(startText + " " + finalText + " "
                        // + finalURLText);
                        String text = URLText.substring(startText + 1,
                                finalText);

                        if (testPhone(text)) {
                            text = text.replaceAll("\\D", "");
                            if (!phoneString.contains(text)) {
                                phoneString = phoneString + text + " ";
                            }

                        }
                        if (testEmail(text) && !mailString.contains(text)) {
                            text = text.trim();
                            mailString = mailString + text + " ";
                        }
                    }
                    startText = finalText;
                } else {
                    // System.out.println("");
                    break;

                }
            }

        }
        // phoneString = phoneString.replace("-", " ").replace("(", "")
        // .replace(")", "");
        mailString = mailString.trim();

       if (choiseParam == "mail"){
           System.out.println("....готово mail");
           return mailString;
       }
        else {
           System.out.println("....готово");
           return phoneString;
       }



    }



    public static boolean testPhone(String testString) {
        Pattern p = Pattern
                .compile("^.+[1-9][0-9][0-9](\\s|\\))?.+[0-9][0-9][0-9].?[0-9][0-9].?[0-9][0-9].?$");
        Matcher m = p.matcher(testString);
        return m.matches();
    }

    public static boolean testEmail(String testString) {
        Pattern p = Pattern
                .compile("^.*([a-z]|[0-9])\\@.+\\.(ru|com|net|su).*$");
        Matcher m = p.matcher(testString);
        return m.matches();
    }
}