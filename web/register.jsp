<%-- 
    Document   : register
    Created on : Nov 14, 2017, 12:37:57 PM
    Author     : kevin
--%>

<%@page import="com.kevin.DBConnect"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Register</title>
    </head>  
     <%
            String email = request.getParameter("email");
            String pw = request.getParameter("pwd");
            DBConnect dbConnect = new DBConnect();  
            dbConnect.register(email,pw);
            String result = dbConnect.login(email,pw);
            if(result.equals("invalid"))
            {
                session.setAttribute("logged", "index");
                response.sendRedirect("index.jsp");
            }
            else
            {
                session.setAttribute("logged", email);
                response.sendRedirect("index.jsp");
            }
        %>
      

    <body>
        
    </body>
</html>
