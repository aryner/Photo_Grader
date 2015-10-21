/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controller;

import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpSession;

import model.Study;
import model.User;
import model.Photo;

import metaData.MetaData;
import metaData.ManualMetaData;

import SQL.Query;
import SQL.Helper;

import utilities.Constants;
import utilities.FileIO;

/**
 *
 * @author aryner
 */
@WebServlet(name = "Controller.Patient_controller", urlPatterns = {
								"/defineAssignment","/define_assignment","/upload","/upload_pictures",
								"/upload_table_data","/img","/assign_manual_meta","/manually_assign_meta-data",
								"/setManualMetaData","/set_view_group","/view_groups","/selectGrouping","/view_group",
								"/removePhoto"
								})
public class Patient_controller extends HttpServlet {
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
		Study study = (Study)session.getAttribute("study");
		User user = (User)session.getAttribute("user");

		//The user is not logged in so is redirected to the index/login page
		if(user == null) {
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
		else if(userPath.equals("/set_view_group")) {
			ArrayList<String> columns = Photo.getMetaDataKeys(study.getPhoto_attribute_table_name());
			Helper.unprocess(columns);
			request.setAttribute("columns", columns);
		}
		else if(userPath.equals("/view_groups")) {
			request.setAttribute("groups",session.getAttribute("viewGroups"));
			request.setAttribute("groupOptions",session.getAttribute("viewGroupOptions"));
		}
		else if(userPath.equals("/view_group")) {
			ArrayList<Photo> groups = (ArrayList)session.getAttribute("viewGroups");
			ArrayList<String> groupOptions = (ArrayList)session.getAttribute("viewGroupOptions");
			int index = Integer.parseInt(request.getParameter("index"));
			request.setAttribute("index",Integer.parseInt(request.getParameter("index")));
			request.setAttribute("groupsSize",groups.size());
			request.setAttribute("photo_table_num",study.getPhoto_attribute_table_name());
			request.setAttribute("photos",groups.get(index).getGroup(groupOptions,study.getPhoto_attribute_table_name()));
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
		Study study = (Study)session.getAttribute("study");

		if(userPath.equals("/setManualMetaData")) {
			ArrayList<ManualMetaData> manualMetaData = (ArrayList)Query.getModel("SELECT * FROM photo_data_by_manual WHERE study_id="+study.getId(),new ManualMetaData());
			String redirect = ManualMetaData.assignManualMeta(request,study.getPhoto_attribute_table_name(),manualMetaData);

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
		else if(userPath.equals("/selectGrouping")) {
			//viewGroup contains an array list of photos, each of which represents a different group
			ArrayList<String> viewOptions = Photo.getViewGroupOptions(request,study);
			session.setAttribute("viewGroupOptions",viewOptions);
			session.setAttribute("viewGroups",Photo.getViewGroups(viewOptions,study));
			response.sendRedirect("/Photo_Grader/view_groups");
			return;
		}
		else if(userPath.equals("/removePhoto")) {
			int index = Photo.deletePhoto(request,study);
			response.sendRedirect("/Photo_Grader/view_group?index="+index);
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
