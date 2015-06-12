/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controller;

import metaData.*;
import SQL.*;
import model.*;
import metaData.grade.*;
import utilities.*;

import java.util.*;
import java.io.*;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletOutputStream;
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
						"/grade","/startGrading","/img","/submitGrade","/select_CSVs","/present_CSV",
						"/printCSV","/assign_manual_meta","/manually_assign_meta-data","/setManualMetaData"
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
		GradeGroup group = (GradeGroup)session.getAttribute("grade_group");
		Study study = (Study)session.getAttribute("study");

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
		else if(userPath.equals("/assign_manual_meta")) {
			ArrayList<Photo> photos = (ArrayList)Query.getModel("SELECT * FROM "+study.getPhoto_attribute_table_name()+" ORDER BY id", new Photo());
			ArrayList<ManualMetaData> manualMetaData = (ArrayList)Query.getModel("SELECT * FROM photo_data_by_manual WHERE study_id="+study.getId(),new ManualMetaData());

			request.setAttribute("photos",photos);
			request.setAttribute("manualMetaData",manualMetaData);
		}
		else if(userPath.equals("/manually_assign_meta-data")) {
			int photoID = Integer.parseInt(request.getParameter("id"));
			Photo photo = (Photo)Query.getModel("SELECT * FROM "+study.getPhoto_attribute_table_name()+" WHERE id='"+photoID+"'",new Photo()).get(0);
			ArrayList<ManualMetaData> manualMetaData = (ArrayList)Query.getModel("SELECT * FROM photo_data_by_manual WHERE study_id="+study.getId(),new ManualMetaData());
			ArrayList<Photo> prevNext = photo.getPrevNext(study.getPhoto_attribute_table_name());

			request.setAttribute("prevNext",prevNext);
			request.setAttribute("photoNumber", study.getPhotoNumber());
			request.setAttribute("photo",photo);
			request.setAttribute("manualMetaData",manualMetaData);
		}
		else if(userPath.equals("/select_study")) {
			request.setAttribute("studyNames",Query.getField("study","name",null,null));
		}
		else if(userPath.equals("/define_grading_questions")) {
			ArrayList<String> columns = Photo.getMetaDataKeys(study.getPhoto_attribute_table_name());
			ArrayList<String> usedNames = GradeGroup.getUsedNames(study.getId());
			Helper.unprocess(columns);
			request.setAttribute("columns", columns);
			request.setAttribute("usedNames", usedNames);
		}
		else if(userPath.equals("/select_grade_category")) {
			request.setAttribute("categories",study.getGradeCategoryNames());
		}
		else if(userPath.equals("/grade")) {
			request.setAttribute("photoGroup",Photo.getUngradedGroup(group, study.getPhoto_attribute_table_name(), user!=null?user.getName():null));
			request.setAttribute("photoNumber", study.getPhotoNumber());
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
		else if(userPath.equals("/img")) {
			String name = request.getParameter("name");
			String number = request.getParameter("number");
			response.setContentType("image/jpeg");
			ServletOutputStream output = response.getOutputStream();
			FileInputStream imgStream = new FileInputStream(Constants.PIC_PATH+number+Constants.FILE_SEP+name);

			BufferedInputStream bufferedIn = new BufferedInputStream(imgStream);
			BufferedOutputStream bufferedOut = new BufferedOutputStream(output);

			int nextByte;
			while((nextByte = bufferedIn.read()) != -1) {
				bufferedOut.write(nextByte);
			}

			bufferedIn.close();
			imgStream.close();
			bufferedOut.close();
			output.close();
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
		GradeGroup group = (GradeGroup)session.getAttribute("grade_group");
		Study study = (Study)session.getAttribute("study");

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
			session.removeAttribute("grade_group");
			response.sendRedirect("/Photo_Grader/");
			return;
		}
		else if(userPath.equals("/setManualMetaData")) {
			ArrayList<ManualMetaData> manualMetaData = (ArrayList)Query.getModel("SELECT * FROM photo_data_by_manual WHERE study_id="+study.getId(),new ManualMetaData());
			String redirect = Photo.assignManualMeta(request,study.getPhoto_attribute_table_name(),manualMetaData);

			response.sendRedirect(redirect);
			return;
		}
		else if(userPath.equals("/defineAssignment")) {
			study = Study.createStudy(request);
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
			ArrayList<String> errors = FileIO.upload(request,FileIO.PHOTO,study);
			session.setAttribute("errors",errors);
			response.sendRedirect("/Photo_Grader/home");
			return;
		}
		else if(userPath.equals("/upload_table_data")) {
			ArrayList<String> errors = FileIO.upload(request,FileIO.TABLE,study);
			session.setAttribute("errors",errors);
			response.sendRedirect("/Photo_Grader/home");
			return;
		}
		else if(userPath.equals("/defineGradingQuestions")) {
			session.setAttribute("errors",study.createGradeGroup(request));
			response.sendRedirect("/Photo_Grader/home");
			return;
		}
		else if(userPath.equals("/startGrading")) {
			int grade_group_id = study.getGradeGroupId(request.getParameter("category"));
			session.setAttribute("grade_group", new GradeGroup(grade_group_id));
			response.sendRedirect("/Photo_Grader/grade");
			return;
		}
		else if(userPath.equals("/submitGrade")) {
			Grade.grade(request, study, group, user);
			response.sendRedirect("/Photo_Grader/grade");
			return;
		}
		else if(userPath.equals("/printCSV")) {
			String category = request.getParameter("category");
			int grade_group_id = study.getGradeGroupId(request.getParameter("category"));
			Tools.createCSV(Grade.getCSVLines(new GradeGroup(grade_group_id),study),category);
			
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
