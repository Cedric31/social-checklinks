/*=========================================================================

    Copyright © 2013 BIREME/PAHO/WHO

    This file is part of Social Check Links.

    Social Check Links is free software: you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public License as
    published by the Free Software Foundation, either version 2.1 of
    the License, or (at your option) any later version.

    Social Check Links is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with Social Check Links. If not, see
    <http://www.gnu.org/licenses/>.

=========================================================================*/

package br.bireme.web;

import br.bireme.scl.EncDecUrl;
import br.bireme.scl.IdUrl;
import br.bireme.scl.MongoOperations;
import com.mongodb.DBCollection;
import java.io.IOException;
import java.util.Set;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Heitor Barbieri
 * date: 20130806
 */
public class CheckManyLinksServlet extends HttpServlet {
    private static final String CODEC = "UTF-8";

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(final HttpServletRequest request,
                                  final HttpServletResponse response)
                                         throws ServletException, IOException {
        request.setCharacterEncoding(CODEC);
        
        final ServletContext context = getServletContext();
        final DBCollection coll =
                               (DBCollection)context.getAttribute("collection");
        final DBCollection hcoll =
                              (DBCollection)context.getAttribute("historycoll");
        final HttpSession session = request.getSession();
        final String user = (String)session.getAttribute("user");
        final Set<String> centerIds = (Set<String>)request.getSession()
                                                     .getAttribute("centerIds");         
        final String brokenUrl = request.getParameter("url");        
        final String brokenUrl_E = EncDecUrl.encodeUrl(brokenUrl, CODEC, true);        
        final String brokenUrl_D = EncDecUrl.decodeUrl(brokenUrl);
        final String fixedUrl = request.getParameter("furl");
        final String fixedUrl_E = EncDecUrl.encodeUrl(fixedUrl, CODEC, true);
        final String fixedUrl_D = EncDecUrl.decodeUrl(fixedUrl);
        final String lang = request.getParameter("lang");
        final String group = request.getParameter("group");
        final String id = request.getParameter("id");
        final String scollCenterFilter = request.getParameter("collCenterFilter");
        final String collCenterFilter = "null".equals(scollCenterFilter) ? null 
                                                            : scollCenterFilter;            
        final String sorder = request.getParameter("order");
        final String order = "null".equals(sorder) ? "descending" : sorder;
        final String sdbFilter = request.getParameter("dbFilter");
        final String dbFilter = "null".equals(sdbFilter) ? null : sdbFilter;
        final boolean force = "force".equals(request.getParameter("force"));
        
        final Set<IdUrl> fixed_E = MongoOperations.fixRelatedUrls(coll, hcoll,
                user, centerIds, collCenterFilter, brokenUrl_D, fixedUrl_D, id, 
                                                                         force);
        
        final RequestDispatcher dispatcher = context.getRequestDispatcher(
                "/showFixedUrls.jsp?group=0&lgroup=" + group 
            + "&dbFilter=" + dbFilter + "&lang=" + lang 
            + "&id=" + id + "&brokenUrl=" + brokenUrl_E + "&url=" + fixedUrl_E
            + "&collCenterFilter=" + collCenterFilter + "&order=" + order);

        session.setAttribute("IdUrls", fixed_E);
        dispatcher.forward(request, response);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(final HttpServletRequest request,
                          final HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(final HttpServletRequest request,
                           final HttpServletResponse response)
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
