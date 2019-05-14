<%-- 
    Document   : deleteTrade.jsp
    Created on : Dec 19, 2017, 11:24:35 PM
    Author     : kevin
--%>

<%@page import="com.kevin.DBConnect"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
           <%
               
               
            String logged = (String)session.getAttribute("logged");
             
            if (logged!=null && !logged.matches("index")) {            
                response.sendRedirect("index.jsp");
            }
            
            String tradeID = request.getParameter("tradeID");
            DBConnect connect = new DBConnect();
            connect.removeTrade(Integer.parseInt(tradeID), logged);
            
           
            
            //response.sendRedirect("index.jsp");
        %>
    </body>
</html>
