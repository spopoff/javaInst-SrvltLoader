/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spopoff.agentloader;

import com.spopoff.oursinstrument.ApplicationOne;
import com.sun.tools.attach.VirtualMachine;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mrwc1264
 */
public class AgentLoadServlet extends HttpServlet {

    static final Logger logger = LoggerFactory.getLogger(AgentLoadServlet.class);
    private String jarFilePath;
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String qry = request.getQueryString();
        if(qry==null){
            logger.info("dynamically loading javaagent");
            String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
            int p = nameOfRunningVM.indexOf('@');
            String pid = nameOfRunningVM.substring(0, p);
            jarFilePath = this.getInitParameter("AgentPath");
            logger.info("path to javaagent="+jarFilePath);
            try {
                VirtualMachine vm = VirtualMachine.attach(pid);
                vm.loadAgent(jarFilePath, "");
                vm.detach();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else{
            logger.info("query="+qry);
            ApplicationOne une = ApplicationOne.getInstance();
            String[] nv = qry.split("=");
            if(nv[0].equals("class")){
                une.instrumentClass(nv[1]);
            }
        }
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet AgentLoadServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AgentLoadServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
