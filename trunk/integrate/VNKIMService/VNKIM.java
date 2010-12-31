/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.me.VNKIMService;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.*;
import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

/**
 *
 * @author Minh Dung
 */
@WebService()
public class VNKIM {

    /**
     * Web service operation
     */
    @Resource
    private WebServiceContext wsContext;
    @WebMethod(operationName = "parse")
    public String parse(@WebParam(name = "query")
    String query, @WebParam(name = "ambiguous")
    String ambiguous) {
        //TODO write your implementation code here:
        String output="";
        MessageContext context = wsContext.getMessageContext();
        ServletContext servletContext = (ServletContext) context.get(MessageContext.SERVLET_CONTEXT);
        QuerytoCG querytocg = new QuerytoCG();
        try{
            output = QuerytoCG.viewCG(servletContext, query.trim(), 0,ambiguous);
        }catch (Exception e) {
            output = e.toString();
        }
    return output;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "lucene")
    public String lucene(@WebParam(name = "triple")
    String triple) {
        //TODO write your implementation code here:
        String output="";
        MessageContext context = wsContext.getMessageContext();
        ServletContext servletContext = (ServletContext) context.get(MessageContext.SERVLET_CONTEXT);
        try{
            output = QuerytoCG.resultEntities(servletContext, triple.trim(), 0);
        }catch (Exception e) {
            output = e.toString();
        }
    return output;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "annoAmbiguous")
    public String annoAmbiguous(@WebParam(name = "query")
    String query, @WebParam(name = "ambiguous")
    String ambiguous) {
        String output="";
        MessageContext context = wsContext.getMessageContext();
        ServletContext servletContext = (ServletContext) context.get(MessageContext.SERVLET_CONTEXT);
        try{
            output = QuerytoCG.resultAmbiguous(servletContext, query.trim(), 0, ambiguous);
        }catch (Exception e) {
            output = e.toString();
        }
    return output;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getDocuments")
    public String getDocuments(@WebParam(name = "query")
    String query, @WebParam(name = "start")
    String start, @WebParam(name = "hitNumber")
    String hitNumber) {
        String output="";
        ConnectServers connect = new ConnectServers();
        MessageContext context = wsContext.getMessageContext();
        ServletContext servletContext = (ServletContext) context.get(MessageContext.SERVLET_CONTEXT);
        try{
            output = connect.performLucene(query, start, hitNumber);
        }catch (Exception e) {
            output = e.toString();
        }
        return output;
    }
}
