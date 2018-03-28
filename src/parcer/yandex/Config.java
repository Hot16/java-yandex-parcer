package parcer.yandex;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Config {
//    ArrayList<String> configData = new ArrayList<>();
    Map<String, String> configData = new HashMap<String, String>();


    public Map<String, String> getConfig() throws Exception {

        FileReader configFile = null;
        try {
            configFile = new FileReader("configYP.txt");
            Scanner config = new Scanner(configFile);
            String line;
            while (config.hasNextLine()){
                line = config.nextLine();
                int delimiter = line.indexOf("=");
                String key = line.substring(0, delimiter).trim();
                String value = line.substring(delimiter+1);
                configData.put(key, value);

            }
            configFile.close();

        } catch (FileNotFoundException e) {
            createConfig();
            getConfig();
            //e.printStackTrace();
        }

        return configData;
    }

    public static void createConfig() throws Exception {

        FileWriter configFile = new FileWriter("configYP.txt");
        Scanner in = new Scanner(System.in);
        System.out.println("Input Yandex login");
        String login = in.nextLine();
        System.out.println("Input Yandex key");
        String key = in.nextLine();
        System.out.println("Input path to save data");
        String path = in.nextLine();

        configFile.write("name="+login+"\n");
        configFile.write("key="+key+"\n");
        configFile.write("path="+path+"\n");
        configFile.close();

    }
}
