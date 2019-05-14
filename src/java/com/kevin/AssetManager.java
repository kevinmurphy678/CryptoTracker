package com.kevin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author kevin
 */
public class AssetManager {
    //Load assets from coinmarketcap api into databases
    DBConnect dbConnect = new DBConnect();
    public void populateAssets()
    { 
        try{
            String assets = getHTML("https://api.coinmarketcap.com/v1/ticker/?limit=200");
            JSONArray json = new JSONArray(assets);
            dbConnect.updateAsset(json);
        }catch(Exception e){}
    }
    
    public void updateAsset(String name){//Update a specific asset based on name. Assets are updated every page refresh to always have latest price. Only update assets which are needed for performance
           try{
            System.out.println("Updating: " + name);
            String assets = getHTML("https://api.coinmarketcap.com/v1/ticker/" + name);
            JSONArray json = new JSONArray(assets);
            dbConnect.updateAsset(json);
        }catch(Exception e){}        
    }
    
    
    public void updateHistoricalData(String name){//Update the last 30 days of historical data
        try
        {
            String data = getHTML("https://coinmarketcap.com/currencies/" + name + "/historical-data/?start=20130428&end=2020121");
            
            String [] dataLines = data.replaceAll("\\r", "\\n")
            .replaceAll("\\n{2,}", "\\n")
            .split("\\n"); //Split into new lines
            
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, YYYY");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -30);
            for(int i = 0; i< 30; i++){
                cal.add(Calendar.DAY_OF_YEAR, 1);
                String date = (sdf.format(cal.getTime()));
                for(int j = 0; j < dataLines.length - 1; j++)
                {
                    if(dataLines[j].contains(date))
                    {
                        String priceData = dataLines[j+1];//get price for this date
                        String price = priceData.replace("<td>","").replace("</td>","").trim();
                        dbConnect.updateHistory(name, cal.getTime(), price);
                    }
                }
            }
        }catch(Exception e){}      
    }
    
    
     public static String getHTML(String urlToRead) throws Exception {
      StringBuilder result = new StringBuilder();
      URL url = new URL(urlToRead);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String line;
      while ((line = rd.readLine()) != null) {
         result.append(line + "\n");
      }
      rd.close();
      return result.toString();
   }
}
