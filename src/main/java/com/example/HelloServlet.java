package com.example;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;

public class HelloServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");
        response.getWriter().println(
            "<h1>Hello from Jenkins CI/CD Pipeline!</h1>"
        );
    }
}
