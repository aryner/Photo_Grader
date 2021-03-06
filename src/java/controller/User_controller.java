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
import javax.servlet.annotation.WebServlet;

import utilities.Constants;

import model.User;

/**
 *
 * @author aryner
 */

@WebServlet(name = "Controller.User_controller", urlPatterns = {
								"/createUser","/login","/logout","/register",
								"/admin_page","/updatePrivileges"
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
		HttpSession session = request.getSession(); 
		User user = (User)session.getAttribute("user");

		//The user is not logged in so is redirected to the index/login page
		if(user == null && !userPath.equals("/register")) {
			response.sendRedirect("/Photo_Grader/index.jsp");
			return;
		}
		else if(userPath.equals("/admin_page")) {
			if(user!= null && !user.isAdmin()) {
				response.sendRedirect("/home");
				return;
			}
			session.removeAttribute("grade_group");
			session.removeAttribute("rank_group");
			session.removeAttribute("last_compared_rank");
			session.removeAttribute("high_rank");
			session.removeAttribute("low_rank");
			session.removeAttribute("right_rank");
			session.removeAttribute("left_rank");
			session.removeAttribute("viewGroups");
			session.removeAttribute("viewGroupOptions");
			request.setAttribute("users",User.getUsers());
			request.setAttribute("user",user);
		}

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

			response.sendRedirect(User.createUser(name, password, rePassword, type, session));

			return;
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
		else if(userPath.equals("/updatePrivileges")) {
			User user = (User)session.getAttribute("user");
			user.updatePrivileges(request);
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
