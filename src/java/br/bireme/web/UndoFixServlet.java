/*=========================================================================

    Copyright © 2013 BIREME/PAHO/WHO

    This file is part of SocialCheckLinks.

    SocialCheckLinks is free software: you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public License as
    published by the Free Software Foundation, either version 2.1 of
    the License, or (at your option) any later version.

    SocialCheckLinks is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with SocialCheckLinks. If not, see
    <http://www.gnu.org/licenses/>.

=========================================================================*/

package br.bireme.web;

import br.bireme.scl.IdUrl;
import br.bireme.scl.MongoOperations;
import com.mongodb.DBCollection;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Heitor Barbieri
 * date 20130822
 */
public class UndoFixServlet extends HttpServlet {

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
        final ServletContext context = getServletContext();
        final DBCollection coll =
                               (DBCollection)context.getAttribute("collection");
        final DBCollection hcoll =
                              (DBCollection)context.getAttribute("historycoll");
        final HttpSession session = request.getSession();
        final Set<IdUrl> fixed = (Set<IdUrl>)session.getAttribute("IdUrls");
        final Set<IdUrl> nfixed = new HashSet<IdUrl>();
        final String undoUrl = request.getParameter("undoUrl");
        final String undoUrlF = undoUrl.replace("<<amp;>>", "&");
        final String lang = request.getParameter("lang");

        for (IdUrl iu : fixed) {
            if (iu.url.equals(undoUrlF)) {
               if (! MongoOperations.undoUpdateDocument(coll, hcoll, iu.id)) {
                   throw new IOException("Undo operation failed.");
               }
            } else {
                nfixed.add(iu);
            }
        }
        session.setAttribute("IdUrls", nfixed);
        response.sendRedirect(response.encodeRedirectURL(
                                     "showFixedUrls.jsp?group=0&lang=" + lang));
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