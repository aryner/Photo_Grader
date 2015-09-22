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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;

import model.User;
import model.Study;
import model.Grade;
import model.Photo;
import metaData.grade.GradeGroup;

/**
 *
 * @author aryner
 */
@WebServlet(name = "Controller.User_controller", urlPatterns = {
								"/select_grade_category","/grade","/startGrading","/submitGrade",
								})
public class Grade_controller extends HttpServlet {
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
		GradeGroup group = (GradeGroup)session.getAttribute("grade_group");

		if(userPath.equals("/select_grade_category")) {
			request.setAttribute("categories",study.getGradeCategoryNames());
		}
		else if(userPath.equals("/grade")) {
			request.setAttribute("photoGroup",Photo.getUngradedGroup(group, study.getPhoto_attribute_table_name(), user.getName()));
			request.setAttribute("photoNumber", study.getPhotoNumber());
		}
		else if(userPath.equals("/startGrading")) {
			int grade_group_id = study.getGradeGroupId(request.getParameter("category"));
			session.setAttribute("grade_group", new GradeGroup(grade_group_id));
			response.sendRedirect("/Photo_Grader/grade");
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
		Study study = (Study)session.getAttribute("study");
		GradeGroup group = (GradeGroup)session.getAttribute("grade_group");

		if(userPath.equals("/submitGrade")) {
			Grade.grade(request, study, group, user);
			response.sendRedirect("/Photo_Grader/grade");
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
