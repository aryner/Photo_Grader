/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controller;

import metaData.MetaData;
import SQL.*;
import model.*;
import utilities.*;

import java.util.*;
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
						"/Controller","/register","/createUser","/select_study","/login",
						"/logout","/setStudy","/createStudy","/create_study","/defineAssignment",
						"/define_assignment","/home","/upload","/upload_pictures","/upload_table_data",
						"/define_grading_questions", "/defineGradingQuestions","/select_grade_category",
						"/grade","/startGrading"
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

		//The user is not logged in so is redirected to the index/login page
		if(user == null && !userPath.equals("/register")) {
			response.sendRedirect("/Photo_Grader/index.jsp");
			return;
		}
		else if(userPath.equals("/define_assignment")) {
			ArrayList<MetaData> metaData = new ArrayList<MetaData>();
			MetaData.makeLists(request, metaData);

			request.setAttribute("studyName",request.getParameter("studyName"));
			request.setAttribute("metaData",metaData);
		}
		else if(userPath.equals("/select_study")) {
			request.setAttribute("studyNames",Query.getField("study","name",null,null));
		}
		else if(userPath.equals("/define_grading_questions")) {
			ArrayList<String> columns = Photo.getMetaDataKeys(((Study)session.getAttribute("study")).getPhoto_attribute_table_name());
			Helper.unprocess(columns);
			request.setAttribute("columns", columns);
		}
		else if(userPath.equals("/select_grade_category")) {
			request.setAttribute("categories",((Study)session.getAttribute("study")).getGradeCategoryNames());
		}
		else if(userPath.equals("/grade")) {
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
		User user;

		if(userPath.equals("/createUser")) {
			String name = request.getParameter("userName");
			String password = request.getParameter("password");
			String rePassword = request.getParameter("rePassword");
			String type = request.getParameter("graderType");

			if(password.equals(rePassword)){
				user = User.register(name, password);

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
			user = User.login(name, password);

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
			response.sendRedirect("/Photo_Grader/");
			return;
		}
		else if(userPath.equals("/defineAssignment")) {
			Study study = Study.createStudy(request);
			MetaData.processDefinitions(study, request);

			session.setAttribute("study",study);
			response.sendRedirect("/Photo_Grader/home");
			return;
		}
		else if(userPath.equals("/setStudy")) {
			session.setAttribute("study",Study.getStudyByName(request.getParameter("name")));
			response.sendRedirect("/Photo_Grader/home");
			return;
		}
		else if(userPath.equals("/upload_pictures")) {
			ArrayList<String> errors = FileIO.upload(request,FileIO.PHOTO,(Study)session.getAttribute("study"));
			session.setAttribute("errors",errors);
			response.sendRedirect("/Photo_Grader/home");
			return;
		}
		else if(userPath.equals("/upload_table_data")) {
			ArrayList<String> errors = FileIO.upload(request,FileIO.TABLE,(Study)session.getAttribute("study"));
			session.setAttribute("errors",errors);
			response.sendRedirect("/Photo_Grader/home");
			return;
		}
		else if(userPath.equals("/defineGradingQuestions")) {
			session.setAttribute("errors",((Study)session.getAttribute("study")).createGradeGroup(request));
			response.sendRedirect("/Photo_Grader/home");
			return;
		}
		else if(userPath.equals("/startGrading")) {
			session.setAttribute("grade_group_id", ((Study)session.getAttribute("study")).getGradeGroupId(request.getParameter("category")));
			response.sendRedirect("/Photo_Grader/grade");
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
