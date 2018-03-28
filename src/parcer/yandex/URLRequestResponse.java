/**
 * Created by webmaster on 13.01.16.
 */
package parcer.yandex;

import java.io.BufferedReader;
import java.io.FileWriter;
// import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

public class URLRequestResponse {

    HttpURLConnection myConnection = null;

    public String getResponsed(String urlResp) throws IOException {
        try {
            System.out.println("  Получаем текст страницы");
            URL myURL = new URL(urlResp);
            myConnection = (HttpURLConnection) myURL.openConnection();
            myConnection.setRequestMethod("GET");
            myConnection.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = myConnection.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    myConnection.getInputStream())); // ,
            // Charset.forName("utf-8")
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            String responseString = response.toString();
            // System.out.println(responseString);
            System.out.println("....готово");
            return responseString;
        } catch (IOException e) {
            return "exception";

        }
        // ������� ��������� � ����
		/*
		 * FileWriter writeResponse = new FileWriter("response.txt");
		 * writeResponse.write(response.toString()); writeResponse.close();
		 */
    }

}
