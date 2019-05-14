package com.kevin;
import java.sql.*;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
public class DBConnect {
  private final String driver = "com.mysql.jdbc.Driver";
  private final String url = "jdbc:mysql://localhost:3306/csc3050";
  private final String user = "mahadev";
  private final String pwd = "mahadev";
  
  private Connection connection = null;
  private Statement statement = null;
  private PreparedStatement preparedStatement = null;
  private ResultSet resultSet = null;
  private ResultSetMetaData resultSetMetaData = null;
          
  private Boolean open()
  {
      try{
          Class.forName(driver);
          connection = DriverManager.getConnection(url, user, pwd);
          statement = connection.createStatement();
          return true;
      }
      catch(ClassNotFoundException | SQLException e){
          e.printStackTrace();
          return false;
      }
  }  
  private Boolean close()
  {
      try{
          statement.close();
          connection.close();
          return true;
      }
      catch(SQLException e){
          e.printStackTrace();
          return false;
      }    
  }
  
  public void updateHistory(String name, java.util.Date date, String price)
  {
      if(open())
      {
          try{
              
           // java.util.Date utilDate = new SimpleDateFormat("MM-dd-yyyy").parse(Date);
            //because PreparedStatement#setDate(..) expects a java.sql.Date argument
             java.sql.Date sqlDate = new java.sql.Date(date.getTime()); 
             
            preparedStatement = connection.prepareStatement("select assetID from assets where name = ?");
            preparedStatement.setString(1,name);
            resultSet = preparedStatement.executeQuery(); 
            if(resultSet.next())
            {   
                int assetID = resultSet.getInt(1);
                
                preparedStatement = connection.prepareStatement("select date from history where date = ? and assetID = ?");
                preparedStatement.setInt(2,assetID);
                preparedStatement.setDate(1, sqlDate);
                resultSet = preparedStatement.executeQuery(); 
                if(!resultSet.next()) //Only insert if it does not already exist
                {
                 preparedStatement = connection.prepareStatement("insert into history(date, assetID, price) values(?,?,?)");
                 preparedStatement.setInt(2, assetID);
                 preparedStatement.setDate(1, sqlDate);
                 preparedStatement.setFloat(3,Float.parseFloat(price));
                 preparedStatement.execute();
                }
            }
          }catch(Exception e){System.out.println(e.getMessage()); close();}
       
          close();
      }
      
      
  }
  
  public void updateAsset(JSONArray assets)
  {
      if(open()){    
          try{
           for(int i = 0; i < assets.length(); i++)
            {
                JSONObject asset = assets.getJSONObject(i);
                
                String symbol = asset.getString("symbol");
                String name = asset.getString("name");
                float priceUSD = asset.getFloat("price_usd");
                float priceBTC = asset.getFloat("price_btc");
                float percent_change_24h = asset.getFloat("percent_change_24h");
                preparedStatement = connection.prepareStatement("select name from assets where name = ?");
                preparedStatement.setString(1,name);
                resultSet = preparedStatement.executeQuery(); 

                if(resultSet.next() && resultSet.getString(1).equals(name))
                {   
                    //Asset already exists. Update values
                    preparedStatement = connection.prepareStatement("update assets set currentPriceUSD = ?, currentPriceBTC = ?, symbol = ?, percent_change_24h = ? where name = ?");
                    preparedStatement.setFloat(1,priceUSD);
                    preparedStatement.setFloat(2,priceBTC);
                    preparedStatement.setString(3, symbol); 
                    preparedStatement.setString(5, name); 
                    preparedStatement.setFloat(4,percent_change_24h);
                    preparedStatement.execute();   
                    System.out.println("Updating: " + name);
                }
                else
                {
                    //Asset does not exist. Add it
                    preparedStatement = connection.prepareStatement("insert into assets (assetID, Name, currentPriceBTC, currentPriceUSD, symbol) values (0,?,?,?, ?)");
                    preparedStatement.setFloat(3,priceUSD);
                    preparedStatement.setFloat(2,priceBTC);
                    preparedStatement.setString(1, name);
                    preparedStatement.setString(4, symbol);
                    preparedStatement.execute();    

                    System.out.println("Inserting: " + name);
                }

            }
          }catch(Exception e){System.out.println(e.getMessage());close();}
          close();
      }
  }

  //Login a user, check for valid PW.
  public String login(String email, String pw)
  {
         if(open()){
           try{ 
            preparedStatement = connection.prepareStatement("select email,pw from users where email = ? and pw = ?");
            preparedStatement.setString(1,email);
            preparedStatement.setString(2, pw);
            resultSet = preparedStatement.executeQuery(); 
            if(resultSet.next())
            {   
                close();
                return email;
            }
            else
            {
               close();
               return "invalid";
            }  
          }catch(SQLException e){ close();return "sql error: " + e.getMessage();}
  
         } else { return "Error accessing DB"; }
  }
  
  //Register a user and check if their email exists already
  public String register(String email, String pw)
  {
      //Check if already registered with email
         if(open()){
           try{ 
            preparedStatement = connection.prepareStatement("select email from users where email = ?");
            preparedStatement.setString(1,email);
            resultSet = preparedStatement.executeQuery(); 
            if(resultSet.next() && resultSet.getString(1).equals(email))
            {   
                close();
                return "exists";
            }
            else
            {
               close();
               tableInsert("users", "userID,email,pw", "" + 0,email,pw);
               return "registered";
            }  
          }catch(SQLException e){ close();return "sql error: " + e.getMessage();}
  
         } else { return "Error accessing DB"; }
  }
 
  
  //Gets userID from email
  public int getUserID(String email) throws Exception
  {
    preparedStatement = connection.prepareStatement("select userID from users where email = ?");
    preparedStatement.setString(1,email);
    resultSet = preparedStatement.executeQuery(); 
    if(resultSet.next() && resultSet.getInt(1) > 0)
    {
        return resultSet.getInt(1);
    }
    return -1;
  }
  
  public void addTrade(String user, String assetID, String dateString, String amount, String price, String buySell)
  {
      if(user.matches("index")) return;
      System.out.println("Adding Trade");
      //Get userid
      int userID = -1;
      if(open()){
          try{
              userID = getUserID(user);
          }catch(Exception e){System.out.println(e.getMessage());}
      if(userID <= 0) return;
      System.out.println("USER: " + userID);
      try{
      int assetID_int = Integer.parseInt(assetID);
      
      java.util.Date utilDate = new SimpleDateFormat("MM-dd-yyyy").parse(dateString);
      //because PreparedStatement#setDate(..) expects a java.sql.Date argument
      java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime()); 
      float amount_float = Float.parseFloat(amount);
      float price_float = Float.parseFloat(price);
      
      
      preparedStatement = connection.prepareStatement("insert into trades(id, date, assetID, amount, priceBTC, userID, buySell) values(0,?,?,?,?,?,?)");
      preparedStatement.setDate(1, sqlDate);
      preparedStatement.setInt(2, assetID_int);
      preparedStatement.setFloat(3, amount_float);
      preparedStatement.setFloat(4, price_float);
      preparedStatement.setInt(5, userID);
      preparedStatement.setString(6, buySell);
      preparedStatement.execute();
      
      }catch(Exception e){System.out.println(e.getMessage());}
      
      close();
      
      }
      
  }

  
  public float calculateValueAtDate(int assetID, java.sql.Date date)//Calculate price of an asset at a given date
  {
      if(open())
      {
          try{
           preparedStatement = connection.prepareStatement("select price from history where date = ? and assetID = ?");
           preparedStatement.setDate(1,date);
           preparedStatement.setInt(2,assetID);
           resultSet = preparedStatement.executeQuery(); 
           if(resultSet.next())   
               return resultSet.getFloat(1);        
          }catch(Exception e){System.out.println(e.getMessage());close();}
          close();
      }

      return 0;
  }
  
  
  public float calculateHoldingsAtDate(String user, int assetID, java.sql.Date date)//Calculate your holdings of an asset at a given date..
  {
           try{
                int userID = getUserID(user); 
           preparedStatement = connection.prepareStatement("select date, assetID, amount, priceBTC, userID, buySell from trades where userID = ? and assetID = ?");
           preparedStatement.setInt(1,userID);
           preparedStatement.setInt(2,assetID);
           resultSet = preparedStatement.executeQuery(); 
           
           float holdings = 0;
           
            while(resultSet.next())
            {
                java.sql.Date tradeDate = resultSet.getDate(1);
                if(tradeDate.getTime() <= date.getTime())
                    
                if(resultSet.getString(6).matches("Buy"))
                    holdings += resultSet.getFloat(3);//Buy. Add amount
                else
                    holdings -= resultSet.getFloat(3);//Sell. Remove amount

            }
            return holdings;
      
          }catch(Exception e){System.out.println(e.getMessage());close();}
          close();
      
          return 0;
      
  }
  
  
  public String portfolio(String user)
  {
      AssetManager manager = new AssetManager();
      String result = "<h3> Your current holdings </h3> " ; 
      result += " <div class=\"container\">\n" +
        "  <div class=\"row\">\n" +
        "    <div class=\"col-sm-6\">\n" +
        "    <div  id=\"piechart\"></div> \n" +
        "    </div>\n" +
        "    <div class=\"col-sm-6\">\n" +
        "     <div  id=\"linechart\"></div> \n" +
        "    </div>\n" +
        "  </div>\n" +
        "</div> ";

      if(open())
      {
       try{
           int userID = getUserID(user); 
           preparedStatement = connection.prepareStatement("select date, assetID, amount, priceBTC, userID, buySell from trades where userID = ? order by assetID");
           preparedStatement.setInt(1,userID);
           resultSet = preparedStatement.executeQuery(); 
            int trades=0;

            HashMap<Integer, Float> netTotal = new HashMap<Integer, Float>(); //ASSET ID, NET_TOTAL
            HashMap<String, Float> values = new HashMap<String, Float>(); //NAME, CURRENT_PRICE
              
            while(resultSet.next())
            {
                int assetID = resultSet.getInt(2);
                if(!netTotal.containsKey(assetID))
                {
                    netTotal.put(assetID, 0f);
                }
                float currentNet = (float)netTotal.get(assetID);
                if(resultSet.getString(6).matches("Buy"))
                    netTotal.replace(assetID, currentNet + resultSet.getFloat(3));//Buy. Add net
                else
                   netTotal.replace(assetID, currentNet - resultSet.getFloat(3));//Sell. Subtract net
                
                trades++;
            }
            float totalUSD = 0;
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
           
            for (Map.Entry<Integer, Float> entry : netTotal.entrySet()) {
                Integer key = entry.getKey();
                float value = entry.getValue();
                preparedStatement = connection.prepareStatement("select name, symbol, currentPriceUSD, percent_change_24h  from assets where assetID = ?"); //Get display name
                preparedStatement.setInt(1,key);
                resultSet = preparedStatement.executeQuery(); 
                if(resultSet.next())
                {
                   
                    String displayName = resultSet.getString(1);
                    
                    manager.updateAsset(displayName); //Get latest price
                    manager.updateHistoricalData(displayName); //Update historical data
                    
                    String symbol = resultSet.getString(2);
                    float usdValue = resultSet.getFloat(3);
                    float change = resultSet.getFloat(4);
                    float ownedValue = usdValue * value;
                    String ownedValueString = formatter.format(ownedValue);    
                    totalUSD += ownedValue;
                    if(resultSet.next())
                    usdValue = resultSet.getFloat(1);
                    if(!values.containsKey(displayName))
                    values.put(displayName, ownedValue);
                    
                    String textColor = (change >= 0) ? "text-success" : "text-danger";
                    if(ownedValue>0)
                    result += "<div class=\"well well-lg\"> <h3 class=\""+textColor+"\">" + displayName + " :" + formatter.format(usdValue) + " ( " + change + " % )  </h3> <br>Amount: " + value + "<br>Total: " + ownedValueString +  " </div> \n";     
                }
            }          

            //Pie chart of assets
            String totalUSDString = formatter.format(totalUSD); 
            result += "<h2> Total Value: " + totalUSDString + "<h2>";
            result += 
                " <script type=\"text/javascript\">\n" +
                "// Load google charts\n" +
                "google.charts.load('current', {'packages':['corechart']});\n" +
                "google.charts.setOnLoadCallback(drawChart);\n" +
                "\n" +
                "// Draw the chart and set the chart values\n" +
                "function drawChart() {\n" +
                "  var data = google.visualization.arrayToDataTable([\n" +
                "  ['Asset', 'Price'],\n";
                
              int valueCount = 0;
              for (Map.Entry<String, Float> entry : values.entrySet()) { //Add values to pie charts
                String symbol = entry.getKey();
                float value = entry.getValue();
                
                result+=
                "[' " + symbol + " ', " + value + " ]" ; if(valueCount<values.size()) result+= ",\n"; else result += "\n";
                 valueCount++;
              }
              
                result+=
                "]);\n" +
                "\n" +
                "  // Optional; add a title and set the width and height of the chart\n" +
                "  var options = {'width':600, 'height':400};\n" +
                "\n" +
                "  // Display the chart inside the <div> element with id=\"piechart\"\n" +
                "  var chart = new google.visualization.PieChart(document.getElementById('piechart'));\n" +
                "  chart.draw(data, options);\n" +
                "}\n" +
                "</script>";

                //Line chart. Value over time
                result += "  <script type=\"text/javascript\">\n" +
            "      google.charts.load('current', {'packages':['corechart']});\n" +
            "      google.charts.setOnLoadCallback(drawChart);\n" +
            "      function drawChart() {\n" +
            "        var data = google.visualization.arrayToDataTable([\n";
                 result+=   "   ['Date', 'Price'],\n";     
                   
                          SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, YYYY");
                            Calendar cal = Calendar.getInstance();
                            cal.add(Calendar.DAY_OF_YEAR, -21);
                            for(int i = 0; i < 20; i++)
                            {
                                cal.add(Calendar.DAY_OF_YEAR, 1);
                                String dateString = (sdf.format(cal.getTime()));
                                float valueDate = 0.0f;
                                //get portfolio value at current date
                                 for (Map.Entry<Integer, Float> entry : netTotal.entrySet()) {
                                       Integer assetID = entry.getKey();
                                       java.sql.Date date =    new java.sql.Date(cal.getTime().getTime());
                                       float holdings = calculateHoldingsAtDate(user, assetID, date);
                                       valueDate += calculateValueAtDate(assetID,date) * holdings;
                                 }
                                result+= "['" + dateString + "'," + valueDate + "]";
                                if(i<19) result+=",\n";
                            }
    

           result+= "        ]);\n" +
            "\n" +
            "        var options = {\n" +
            "          title: 'Total Value (USD)',\n" +
            "          curveType: 'none', width: 600,height:400,   \n" +
            "          legend: { position: 'bottom' },  hAxis: { textPosition: 'none' }\n" +
            "        };\n" +
            "\n" +
            "        var chart = new google.visualization.LineChart(document.getElementById('linechart'));\n" +
            "\n" +
            "        chart.draw(data, options);\n" +
            "      }\n" +
            "    </script>";
                
                
   
            if(trades==0){
                 result = "<div class=\"well well-lg\">No trades entered yet... Go to Trades -> Add Trade to enter a trade</div>";
            }
                
       }catch(Exception e){}
       close();   
      }
 
      
      return result;
  }
  
  public String editTrades(String user)
  {
      if(open())
      {
      int userID = -1;
      try {
          userID = getUserID(user);
      } catch (Exception ex) {ex.printStackTrace();}
      
      close();
      
      return htmlTable_Trades("select trades.id, trades.date, assets.Name, trades.amount, trades.buySell from trades,assets where trades.assetID=assets.assetID and trades.userID="+userID, userID);

      } else return "";
  }
  
  
  public void tableInsert(String table, String params, String... values)
  {
      String sql = "insert into " + table + "(" + params + ")" + "values (";
      for(int i = 0; i < values.length; i++)
      {
          sql += "'" + values[i] + "'";
          if(i < values.length - 1) sql += ",";
      }
      sql += ")";
      executeSQL(sql);
  }
 
  
  
  public void removeTrade(int tradeID, String user)
  {
      if(open())
      {
          try{
            int userID = getUserID(user);
             preparedStatement = connection.prepareStatement("delete from trades where id = ? and userID = ?"); //Get display name
             preparedStatement.setInt(1,tradeID);
             preparedStatement.setInt(2,userID);
             preparedStatement.execute(); 
            
            
          }catch(Exception e){e.printStackTrace();}
          
      }
      
  }
  
  //Table for editing trades
     public String htmlTable_Trades(String sql, int userID)
    {
        System.out.println(sql);
        String result = "<div class = 'well'><table class = 'table'>\n";
        if(open())
        {
          try {
            resultSet = statement.executeQuery(sql);
            resultSetMetaData = resultSet.getMetaData();
            int count = resultSetMetaData.getColumnCount();
            // create column headings
            result += "<thead><tr>\n";
            for (int i = 1; i < count; i++) {
              result += "<th>" + resultSetMetaData.getColumnName(i + 1) + "</th>\n";
            }
            result+="<th></th>";
            result += "</tr></thead>\n";
            // create data rows
            result += "<tbody>\n";
            while (resultSet.next()) {
                String tradeID=resultSet.getString(1);
              result += "<tr>\n";
              for (int i = 1; i < count; i++) {
                result += "<td>" + resultSet.getString(i + 1) + "</td>  \n";
              }
              result+= "<td> <a href=\"deleteTrade.jsp?tradeID=" + tradeID + "&userID=" + userID + "\" class=\"btn btn-danger btn-xs\" role=\"button\">Delete</a> </td>";
              result += "</tr>\n";
            }
            result += "</tbody></table></div>\n";
            close();
            return result;
          } catch (Exception e) {
            close();
            return e.getMessage();
          }
        }      
        return "Error accessing DB";
    }
  
  
    public String htmlTable(String sql)
    {
        System.out.println(sql);
        String result = "<div class = 'well'><table class = 'table'>\n";
        if(open())
        {
          try {
            resultSet = statement.executeQuery(sql);
            resultSetMetaData = resultSet.getMetaData();
            int count = resultSetMetaData.getColumnCount();
            // create column headings
            result += "<thead><tr>\n";
            for (int i = 0; i < count; i++) {
              result += "<th>" + resultSetMetaData.getColumnName(i + 1) + "</th>\n";
            }
            result += "</tr></thead>\n";
            // create data rows
            result += "<tbody>\n";
            while (resultSet.next()) {
              result += "<tr>\n";
              for (int i = 0; i < count; i++) {
                result += "<td>" + resultSet.getString(i + 1) + "</td>\n";
              }
              result += "</tr>\n";
            }
            result += "</tbody></table></div>\n";
            close();
            return result;
          } catch (Exception e) {
            close();
            return e.getMessage();
          }
        }      
        return "Error accessing DB";
    }
  public void executeSQL(String sql)
  {
      System.out.println("Executing: " + sql);
      if(open())
      {
          try{
              statement.executeUpdate(sql);
              close();
          }
          catch(Exception e)
          {
              e.printStackTrace();
              close();
          }
      }
  }
  
    public String dropdown(String sql)
    {
        String result = "<option disabled selected>Select one ...</option>\n";
        if(open())
        {
          try {
            resultSet = statement.executeQuery(sql);
            resultSetMetaData = resultSet.getMetaData();
            int count = resultSetMetaData.getColumnCount();
            while (resultSet.next()) {
              result += "<option value='" + resultSet.getInt(1) + "'>";
              for (int i = 2; i <= count; i++) {
                result += resultSet.getString(i) + " ";
              }
              result += "</option>\n";
            }
            return result;
          } catch (Exception e) {
            return e.getMessage();
          }
        }
        else
        {
            return null;
        }
    }
}
    
    

       
