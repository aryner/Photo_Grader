/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controller;

import java.util.ArrayList;
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
import model.Grade.GradeCounts;
import model.Photo;
import SQL.Helper;
import metaData.grade.GradeGroup;

/**
 *
 * @author aryner
 */
@WebServlet(name = "Controller.Grade_controller", urlPatterns = {
								"/select_grade_category","/grade","/startGrading","/submitGrade",
								"/define_grading_questions","/defineGradingQuestions","/removeGradeCategory"
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

		//The user is not logged in so is redirected to the index/login page
		if(user == null) {
			response.sendRedirect("/Photo_Grader/index.jsp");
			return;
		}
		else if(userPath.equals("/select_grade_category")) {
			if(!user.isGrader()) {
				response.sendRedirect("/home");
				return;
			}
			request.setAttribute("categories",study.getGradeCategoryNames());
		}
		else if(userPath.equals("/grade")) {
			GradeGroup group = (GradeGroup)session.getAttribute("grade_group");
			request.setAttribute("gradeCounts",new GradeCounts(study.getPhoto_attribute_table_name(),user.getName(),group));
			request.setAttribute("photoGroup",Photo.getUngradedGroup(group, study.getPhoto_attribute_table_name(), user.getName()));
			request.setAttribute("photoNumber", study.getPhotoNumber());
		}
		else if(userPath.equals("/define_grading_questions")) {
			if(!user.isStudy_coordinator()) {
				response.sendRedirect("/home");
				return;
			}
			ArrayList<String> columns = Photo.getMetaDataKeys(study.getPhoto_attribute_table_name());
			ArrayList<String> usedNames = GradeGroup.getUsedNames(study.getId(), GradeGroup.GRADE);
			Helper.unprocess(columns);
			request.setAttribute("columns", columns);
			request.setAttribute("usedNames", usedNames);
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
		User user = (User)session.getAttribute("user");

		if(user == null) {
			response.sendRedirect("/home");
			return;
		}

		if(userPath.equals("/submitGrade")) {
			GradeGroup group = (GradeGroup)session.getAttribute("grade_group");
			Grade.grade(request, study, group, user);
			response.sendRedirect("/Photo_Grader/grade");
			return;
		}
		else if(userPath.equals("/defineGradingQuestions")) {
			session.setAttribute("errors",GradeGroup.createGradeGroup(request,study,GradeGroup.GRADE));
			response.sendRedirect("/Photo_Grader/home");
			return;
		}
		else if(userPath.equals("/startGrading")) {
			int grade_group_id = study.getGradeGroupId(request.getParameter("category"));
			session.setAttribute("grade_group", new GradeGroup(grade_group_id));
			response.sendRedirect("/Photo_Grader/grade");
			return;
		}
		else if(userPath.equals("/removeGradeCategory")) {
			Grade.removeCategory(request,study);
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
