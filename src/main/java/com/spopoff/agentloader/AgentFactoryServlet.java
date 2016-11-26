/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spopoff.agentloader;

import com.spopoff.dynamicagent.InstrumentationFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.instrument.Instrumentation;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mrwc1264
 */
public class AgentFactoryServlet extends HttpServlet {
    static final Logger logger = LoggerFactory.getLogger(AgentFactoryServlet.class);
    private Instrumentation inst = null;
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
        String msg ="";
        if(qry==null){
            try{
                logger.debug("Avant getInstrumentation");
                inst = InstrumentationFactory.getInstrumentation(logger);
                if(inst!=null){
                    msg = "Chargement Agent OK";
                }else{
                    msg = "Chargement Agent KO instance nulle";
                }
            }catch(Exception e){
                msg = e.toString();
                logger.error(msg);
            }
        }else{
            logger.info("query="+qry);
            String[] nv = qry.split("=");
            if(nv[0].equals("class") && inst!=null){
                try {
                    instrumentClass(nv[1]);
                    msg = "intrumentation OK";
                } catch (Exception ex) {
                    msg = ex.getLocalizedMessage();
                    logger.error(msg);
                }
            }
        }


        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet AgentFactoryServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AgentFactoryServlet at " + request.getContextPath() + "</h1>");
            out.println("<p>"+msg+"</p>");
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

    public void instrumentClass(String laClass) throws Exception{
        logger.info("traite class="+laClass);
        Class[] lesClass = new Class[1];
        Class uneClass = null;
        try {
            uneClass = Class.forName(laClass);
        } catch (ClassNotFoundException ex) {
            logger.error(ex.getLocalizedMessage());
            throw(ex);
        }
        if(uneClass==null){
            Exception ex = new Exception("classe vide pas dans loader ?");
            logger.error(ex.getLocalizedMessage());
            throw(ex);
        }
        logger.info("retransform class="+uneClass.toGenericString());
        lesClass[0] = uneClass;
        if(inst==null){
            Exception ex = new Exception("intrumentation vide");
            logger.error(ex.getLocalizedMessage());
            throw(ex);
        }
        try {
            inst.retransformClasses(lesClass);
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage());
            throw(ex);
        }
    }
}
