package com.example.desent.desent.models;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by magnust on 07.08.2017.
 *
 */


public class CarRegNr {

    private static final String TAG = "DOWNLOAD_REG";
    private String regNr;
    private TextView txtResult;
    private String resultString = "Not a valid number";
    private URL url;

    public CarRegNr(String regNr) {
        Log.e(TAG, regNr);
        this.regNr = regNr;
    }

    public String fetchCO2() {
        if(!regNr.equals("Not a valid number")){
            try {
                url = new URL("https://www.vegvesen.no/Kjoretoy/Kjop+og+salg/Kj%C3%B8ret%C3%B8yopplysninger?registreringsnummer=" + regNr);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader r = new BufferedReader(new InputStreamReader(in));
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        if (line.contains("CO2")) {
                            total.append(line);
                            resultString = total.toString();
                            Log.i(TAG, resultString);
                            resultString = resultString.trim();
                            resultString = resultString.replaceFirst("</dd>", "");
                            resultString = resultString.replaceFirst("<dt>", "");
                            resultString = resultString.replaceFirst("</dt>","");
                            resultString = resultString.replaceFirst("<dd>", "");
                            resultString = resultString.replaceFirst("CO2-utslipp", "");
                            resultString = resultString.replaceFirst(" g/km", "");
                        }else if(line.contains("Du har tastet et ugyldig registreringsnummer")){
                            total.append(line);
                            resultString = "Not a valid number";
                        }else if(line.contains("Det oppstod en ukjent feil ved søk")){
                            total.append(line);
                            Log.i(TAG, "Det oppstod en ukjent feil ved søk");
                            resultString = "Not a valid number";
                        }
                        else if(line.contains("Vi finner ingen kjøretøy med dette nummeret")){
                            total.append(line);
                            Log.i(TAG, "Vi finner ingen kjøretøy med dette nummeret");
                            resultString = "Not a valid number";
                        }
                    }

                } finally {
                    urlConnection.disconnect();

                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return resultString;
    }
}
