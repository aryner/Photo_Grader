/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import SQL.Helper;

import model.Study;
import model.User;
import model.Photo;

import metaData.grade.GradeGroup;

/**
 *
 * @author aryner
 */
@WebServlet(name = "Compare_controller", urlPatterns = {
							"/Compare_controller","/define_compare","/select_compare_category",
							"/defineCompare","/startComparing","/compare"
							})
public class Compare_controller extends HttpServlet {

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
		if(userPath.equals("/define_compare")) {
			if(!user.isStudy_coordinator()) {
				response.sendRedirect("/home");
				return;
			}
			ArrayList<String> columns = Photo.getMetaDataKeys(study.getPhoto_attribute_table_name());
			ArrayList<String> usedNames = GradeGroup.getUsedNames(study.getId(), GradeGroup.COMPARE);
			Helper.unprocess(columns);
			request.setAttribute("columns", columns);
			request.setAttribute("usedNames", usedNames);
		}
		else if(userPath.equals("/select_compare_category")) {
			if(!user.isGrader()) {
				response.sendRedirect("/Photo_Grader/home");
				return;
			}
			request.setAttribute("categories",study.getCompareCategoryNames());
		}
		else if(userPath.equals("/compare")) {
			int photoCount = Photo.getPhotoCount(study.getPhoto_attribute_table_name());
			if(photoCount == 0) {
				ArrayList<String> errors = new ArrayList<String>();
				errors.add("There are no photos here to compare");
				session.setAttribute("errors",errors);
				response.sendRedirect("/Photo_Grader/home");
				return;
			} else if (photoCount == Photo.getUnassignedCount(study.getPhoto_attribute_table_name())) {
				ArrayList<String> errors = new ArrayList<String>();
				errors.add("Photos must have meta data assigned before they can be compared");
				session.setAttribute("errors",errors);
				response.sendRedirect("/Photo_Grader/home");
				return;
			}
			GradeGroup group = (GradeGroup)session.getAttribute("compare_group");
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

		String url = "/WEB-INF/view" + userPath + ".jsp";

		if(userPath.equals("/defineCompare")) {
			session.setAttribute("errors",GradeGroup.createGradeGroup(request,study,GradeGroup.COMPARE));
			response.sendRedirect("/Photo_Grader/home");
			return;
		}
		else if (userPath.equals("/startComparing")) {
			int compare_group_id = study.getCompareGroupId(request.getParameter("category"));
			session.setAttribute("compare_group", new GradeGroup(compare_group_id));
			response.sendRedirect("/Photo_Grader/compare");
			return;
		}

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
