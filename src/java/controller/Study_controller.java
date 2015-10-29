/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import SQL.Query;

import model.Study;
import model.User;

/**
 *
 * @author aryner
 */
@WebServlet(name = "Controller.Study_controller", urlPatterns = {
								"/select_study","/setStudy","/createStudy","/create_study",
								"/remove_category"
								})
public class Study_controller extends HttpServlet {
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
		if(user == null) {
			response.sendRedirect("/Photo_Grader/index.jsp");
			return;
		}
		else if(userPath.equals("/select_study")) {
			if(!user.isStudy_coordinator() && !user.isGrader()) {
				response.sendRedirect("/Photo_Grader/home");
				return;
			}
			request.setAttribute("user",user);
			request.setAttribute("studyNames",Query.getField("study","name",null,null));
		}
		else if(userPath.equals("/remove_category")) {
			if(!user.isStudy_coordinator()) {
				response.sendRedirect("/Photo_Grader/home");
				return;
			}
			Study study = (Study)session.getAttribute("study");
			request.setAttribute("grades",study.getGradeCategoryNames());
			request.setAttribute("ranks",study.getRankCategoryNames());
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
		User user = (User)session.getAttribute("user");

		if(user == null) {
			response.sendRedirect("/Photo_Grader/home");
			return;
		}
		else if(userPath.equals("/setStudy")) {
			session.setAttribute("study",Study.getStudyByName(request.getParameter("name")));
			response.sendRedirect("/Photo_Grader/home");
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
