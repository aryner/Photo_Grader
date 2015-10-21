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
import model.Photo;
import model.Rank;
import model.Rank.Pair;

import metaData.grade.GradeGroup;

import SQL.Helper;

/**
 *
 * @author aryner
 */
@WebServlet(name = "Controller.Rank_controller", urlPatterns = {
								"/define_ranking","/defineRanking","/select_rank_category",
								"/rank","/startRanking","/submitRank"
								})
public class Rank_controller extends HttpServlet {

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
		else if (userPath.equals("/define_ranking")) {
			//TODO
			ArrayList<String> columns = Photo.getMetaDataKeys(study.getPhoto_attribute_table_name());
			ArrayList<String> usedNames = GradeGroup.getUsedNames(study.getId(), GradeGroup.RANK);
			Helper.unprocess(columns);
			request.setAttribute("columns", columns);
			request.setAttribute("usedNames", usedNames);
		}
		else if (userPath.equals("/select_rank_category")) {
			request.setAttribute("categories",study.getRankCategoryNames());
		}
		else if(userPath.equals("/rank")) {
			int photoCount = Photo.getPhotoCount(study.getPhoto_attribute_table_name());
			if(photoCount == 0) {
				ArrayList<String> errors = new ArrayList<String>();
				errors.add("There are no photos here to rank");
				session.setAttribute("errors",errors);
				response.sendRedirect("/Photo_Grader/home");
				return;
			} else if (photoCount == Photo.getUnassignedCount(study.getPhoto_attribute_table_name())) {
				ArrayList<String> errors = new ArrayList<String>();
				errors.add("Photos must have meta data assigned before they can be ranked");
				session.setAttribute("errors",errors);
				response.sendRedirect("/Photo_Grader/home");
				return;
			}
			GradeGroup group = (GradeGroup)session.getAttribute("rank_group");
			Pair pair = Rank.getPairToRank(group.getId(), user.getId(), study.getPhoto_attribute_table_name(), request);
			pair.setPhotos(study.getPhoto_attribute_table_name(),group);
			request.setAttribute("rank_pair",pair);
			request.setAttribute("photo_table",study.getPhoto_attribute_table_name());
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
		}
		if (userPath.equals("/defineRanking")) {
			session.setAttribute("errors",GradeGroup.createGradeGroup(request,study,GradeGroup.RANK));
			response.sendRedirect("/Photo_Grader/home");
			return;
		}
		else if (userPath.equals("/startRanking")) {
			int rank_group_id = study.getRankGroupId(request.getParameter("category"));
			session.setAttribute("rank_group", new GradeGroup(rank_group_id));
			response.sendRedirect("/Photo_Grader/rank");
			return;
		}
		else if (userPath.equals("/submitRank")) {
			GradeGroup group = (GradeGroup) session.getAttribute("rank_group");
			Rank.processRanking(request,group,user);
			response.sendRedirect("/Photo_Grader/rank");
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
