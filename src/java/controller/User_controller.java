/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.User;
import utilities.Constants;
import javax.servlet.annotation.WebServlet;

/**
 *
 * @author aryner
 */

@WebServlet(name = "Controller.User_controller", urlPatterns = {
								"/createUser","/login","/logout","/register"
								})
public class User_controller extends HttpServlet {

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
		String userPath = request.getServletPath(); 

		String url = "/WEB-INF/view" + userPath + ".jsp";

		try {
			request.getRequestDispatcher(url).forward(request, response);
		} catch (IOException ex){
			ex.printStackTrace(System.err);
		}
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
		String userPath = request.getServletPath(); 
		HttpSession session = request.getSession(); 

		if(userPath.equals("/createUser")) {
			String name = request.getParameter("userName");
			String password = request.getParameter("password");
			String rePassword = request.getParameter("rePassword");
			String type = request.getParameter("graderType");

			if(password.equals(rePassword)){
				User user = User.register(name, password);

				if(user == null) {
					session.setAttribute("error", Constants.TAKEN_USERNAME);
					response.sendRedirect("/Photo_Grader/register");
					return;
				}
				else { 
					session.setAttribute("user", user); 
					response.sendRedirect("/Photo_Grader/select_study"); 
					return;
				} 
			}
			else {
				session.setAttribute("error", Constants.PASSWORDS_DONT_MATCH);
				response.sendRedirect("/Photo_Grader/register"); 
				return;
			}
		}
		else if(userPath.equals("/login")) {
			String name = request.getParameter("userName");
			String password = request.getParameter("password");
			User user = User.login(name, password);

			if(user == null) {
				session.setAttribute("error", Constants.INCORRECT_NAME_PASS);
				response.sendRedirect("/Photo_Grader/"); 
				return;
			}

			session.setAttribute("user",user);
			response.sendRedirect("/Photo_Grader/select_study"); 
			return;
		}
		else if(userPath.equals("/logout")) {
			session.removeAttribute("user");
			session.removeAttribute("study");
			session.removeAttribute("grade_group");
			response.sendRedirect("/Photo_Grader/");
			return;
		}

		String url = "/WEB-INF/view" + userPath + ".jsp";

		try {
			request.getRequestDispatcher(url).forward(request, response);
		} catch (IOException ex){
			ex.printStackTrace(System.err);
		}

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
