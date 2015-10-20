/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controller;

import model.Study;
import model.User;
import model.Grade;

import metaData.grade.GradeGroup;

import utilities.FileIO;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author aryner
 */
@WebServlet(name = "Controller", urlPatterns = {
						"/Controller","/home","/select_CSVs","/present_CSV","/printCSV"
						})
public class Controller extends HttpServlet {
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
		Study study = (Study)session.getAttribute("study");

		//The user is not logged in so is redirected to the index/login page
		if(user == null) {
			response.sendRedirect("/Photo_Grader/index.jsp");
			return;
		}
		else if(userPath.equals("/home")) {
			session.removeAttribute("grade_group");
			session.removeAttribute("rank_group");
			session.removeAttribute("last_compared_rank");
			session.removeAttribute("high_rank");
			session.removeAttribute("low_rank");
			session.removeAttribute("right_rank");
			session.removeAttribute("left_rank");
			session.removeAttribute("viewGroups");
			session.removeAttribute("viewGroupOptions");
		}
		else if(userPath.equals("/select_CSVs")) {
			request.setAttribute("categories",study.getGradeCategoryNames());
		}
		else if (userPath.equals("/present_CSV")) {
			String category = request.getParameter("category");
			int grade_group_id = study.getGradeGroupId(request.getParameter("category"));

			request.setAttribute("category",category);
			request.setAttribute("csvLines", Grade.getCSVLines(new GradeGroup(grade_group_id),study));
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
		Study study = (Study)session.getAttribute("study");

		if(userPath.equals("/printCSV")) {
			String category = request.getParameter("category");
			int grade_group_id = study.getGradeGroupId(request.getParameter("category"));
			FileIO.createCSV(Grade.getCSVLines(new GradeGroup(grade_group_id),study),category);
			
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
