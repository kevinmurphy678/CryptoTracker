<%-- 
    Document   : enterTrade
    Created on : Oct 16, 2017, 9:21:25 PM
    Author     : kevin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" import = "com.kevin.DBConnect"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Trades Debug</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    </head>
    <body>
        <h1>Trades from DB</h1>
        
        
        
        
        <%
            
             String logged = (String)session.getAttribute("logged");
             
            if (logged!=null && !logged.matches("index")) {            
                response.sendRedirect("index.jsp");
            }
            
            String asset = request.getParameter("asset");
            String price = "1"; //Price not implemented or used ...request.getParameter("price");
            String amount = request.getParameter("amount");
            String date = request.getParameter("date");
            String user = (String)session.getAttribute("logged");
            String buySell = request.getParameter("buySell");
            
            DBConnect dbConnect = new DBConnect();
         
            dbConnect.addTrade(user, asset, date, amount, price, buySell);
           
           
        %>
      
  
        
    </body>
</html>
