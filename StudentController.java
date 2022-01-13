package edu.smart.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import edu.smart.bo.SmartTestBoImpl;
import edu.smart.dao.CourseDetailsDaoImpl;
import edu.smart.dao.ManagementDaoImpl;
import edu.smart.dao.StudentDetailsDaoImpl;
import edu.smart.management.InstructorManagement;
import edu.smart.management.StudentManagement;
import edu.smart.model.CourseDetailsModel;
import edu.smart.model.InstructorModel;
import edu.smart.model.ProjectManagementModel;
import edu.smart.model.StudentDashboardModel;
import edu.smart.model.StudentManagementModel;
import edu.smart.model.UserDetailsModel;
import edu.smart.pojo.AssignmentDetails;
import edu.smart.pojo.ClassDetails;
import edu.smart.pojo.ClassStudentDetails;
import edu.smart.pojo.CourseDetails;
import edu.smart.pojo.ErrorDetails;
import edu.smart.pojo.MailDetails;
import edu.smart.pojo.ProjectDetails;
import edu.smart.pojo.ReferenceModel;
import edu.smart.pojo.UserDetails;
import edu.smart.util.AppendUtil;
import edu.smart.util.DisplayGraph;
import edu.smart.util.SendEmail;
import edu.smart.util.UserRegistration;
import edu.smart.util.AppendUtil;

@Controller
public class StudentController {
    static Logger log = Logger.getLogger(CourseDetailsController.class.getName());

    @Autowired
    SmartTestBoImpl smartTestBoImpl;

    CourseDetailsModel courseDetailsModel = new CourseDetailsModel();

    @Autowired
    DisplayGraph displayGraph;

    @Autowired
    CourseDetailsDaoImpl courseDetailsDaoImpl;

    @Autowired
    ManagementDaoImpl managementDaoImpl;

    @Autowired
    ClassDetails classDetails;

    @Autowired
    ClassStudentDetails classStudentDetails;

    @Autowired
    AssignmentDetails assignmentDetails;

    @Autowired
    ProjectDetails projectDetails;

    @Autowired
    UserDetailsModel userDetailsModel;

    @Autowired
    UserDetails userDetails;

    @Autowired
    StudentDetailsDaoImpl studentdetailsdaoimpl;

    @Autowired
    StudentDashboardModel studentdashboardmodel;

    StudentManagementModel studentmodel = new StudentManagementModel();

    ProjectManagementModel projectmanagement = new ProjectManagementModel();

    UserRegistration userRegistration = new UserRegistration();

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView displayHome() {

        ModelAndView mav = new ModelAndView("Course/Home");
        return mav;
    }

    @RequestMapping(value = "/login")
    public ModelAndView displaylogin() {
        System.out.println("StudentController.java: login() method");
        ModelAndView mav = new ModelAndView("Course/Login");
        return mav;
    }

    @RequestMapping(value = "/uiexp-nocue")
    public ModelAndView goTo1() {

        ModelAndView mav = new ModelAndView("Course/smart1Forward");
        return mav;
    }

    @RequestMapping(value = "/uiexp-noks")
    public ModelAndView goTo2() {
        ModelAndView mav = new ModelAndView("Course/smart2Forward");
        return mav;
    }

    @RequestMapping(value = "/uiexp-nosignal")
    public ModelAndView goTo3() {
        ModelAndView mav = new ModelAndView("Course/smart3Forward");
        return mav;
    }

    @RequestMapping(value = "/uiexp-noexp")
    public ModelAndView goTo4() {
        ModelAndView mav = new ModelAndView("Course/smart4Forward");
        return mav;
    }

    @RequestMapping(value = "/altui01")
    public ModelAndView goTo5() {
        ModelAndView mav = new ModelAndView("Course/smart5Forward");
        return mav;
    }

    @RequestMapping(value = "/altui02")
    public ModelAndView goTo6() {
        ModelAndView mav = new ModelAndView("Course/smart6Forward");
        return mav;
    }

    @RequestMapping(value = "/TermsOfService")
    public ModelAndView TermsOfService() {

        ModelAndView mav = new ModelAndView("Course/TermsOfService");
        return mav;
    }

    @RequestMapping(value = "/Contact")
    public ModelAndView Contact() {

        ModelAndView mav = new ModelAndView("Course/Contact");
        return mav;
    }

    @RequestMapping(value = "/PrivacyPolicy")
    public ModelAndView PrivacyPolicy() {

        ModelAndView mav = new ModelAndView("Course/PrivacyPolicy");
        return mav;
    }

    @RequestMapping(value = "/signout", method = RequestMethod.GET)
    public ModelAndView logout(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        int userid = (int) session.getAttribute("userid");
        log.info("Logged out: " + userid);

        session.invalidate();
        ModelAndView mav = new ModelAndView("Course/Loggedout");
        return mav;
    }

    @RequestMapping(value = "/addingclasstomydashboad", method = RequestMethod.POST)
    public ModelAndView addingclasstomydashboad(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("studentdashboardmodel") StudentDashboardModel studentdashboardmodel) {

        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userid");

        studentdashboardmodel.setStudentid(userId);
        int classid = studentdashboardmodel.getInputclassID();

        int val = studentdetailsdaoimpl.saveaddedclasses(studentdashboardmodel);
        if (val == 2) {
            studentdashboardmodel.setEmptymsg("class exists");
        }

        ModelAndView mav = new ModelAndView("redirect:StudentDashboard", "model", studentdashboardmodel);

        return mav;
    }

    @RequestMapping(value = "/removeclassfromdashboad", method = RequestMethod.POST)
    public ModelAndView removeclassfromdashboad(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("studentdashboardmodel") StudentDashboardModel studentdashboardmodel) {
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userid");

        studentdashboardmodel.setStudentid(userId);
        int classid = studentdashboardmodel.getInputclassID();
        studentdetailsdaoimpl.deleteclass(studentdashboardmodel);
        ModelAndView mav = new ModelAndView("redirect:StudentDashboard", "model", studentdashboardmodel);

        return mav;
    }

    @RequestMapping(value = "/StudentDashboard")
    public ModelAndView StudentDashboard(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav;
        HttpSession session = request.getSession();
        if (session.getAttribute("usertype") == null) {
            mav = new ModelAndView("redirect:login");
            return mav;
        }
        int userId = (int) session.getAttribute("userid");

        Map<ClassDetails, ArrayList<AssignmentDetails>> g;

        g = studentdetailsdaoimpl.getclasses(userId);
        studentdashboardmodel = new StudentDashboardModel();
        studentdashboardmodel.setRecentassignments(studentdetailsdaoimpl.getrecentsubmittedassignments(userId));
        studentdashboardmodel.setTotalclasses(g.size());
        int totalassign = 0;
        for (Map.Entry<ClassDetails, ArrayList<AssignmentDetails>> entry : g.entrySet()) {
            ArrayList<AssignmentDetails> ad = entry.getValue();
            totalassign += ad.size();
        }
        studentdashboardmodel.setTotalassignments(totalassign);

        if (g.isEmpty()) {
            studentdashboardmodel.setEmptymsg("yes");
            mav = new ModelAndView("Course/StudentDashboard", "model", studentdashboardmodel);
        } else {
            studentdashboardmodel.setAssignmentdetails(g);
            mav = new ModelAndView("Course/StudentDashboard", "model", studentdashboardmodel);
        }

        return mav;
    }

    @RequestMapping(value = "/displayAssignment/{assignid}")
    public ModelAndView displayAssignment(@PathVariable(value = "assignid") int id, HttpServletRequest request,
            HttpServletResponse response) {
        ModelAndView mav;
        HttpSession session = request.getSession();
        if (session.getAttribute("userid") == null) {
            mav = new ModelAndView("redirect:login");
            return mav;
        }
        session.setAttribute("assignid", id);
        int userId = (int) session.getAttribute("userid");

        AssignmentDetails assign = studentdetailsdaoimpl.getdisplayingassignment(id);
        studentdashboardmodel.setAllresponses(studentdetailsdaoimpl.getstudentresponses(id, userId));
        ArrayList<CourseDetailsModel> studentResponses = studentdashboardmodel.getAllresponses();
        studentdashboardmodel.setDisplayedassignment(assign);

        if (!studentdashboardmodel.getDisplayedassignment().getType().equals("default")) {
            mav = new ModelAndView("Course/DisplayAssignment", "model", studentdashboardmodel);
            return mav;
        }

        if (studentResponses.size() == 0) {
            Map<String, Object> model = new HashMap<String, Object>();
            String firstName = (String) session.getAttribute("firstname");
            String assignmentTitle = courseDetailsDaoImpl.getassignmentdetails(id).getTitle();

            model.put("studentDashboardModel", studentdashboardmodel);
            model.put("firstName", firstName);
            model.put("assignmentTitle", assignmentTitle);

            mav = new ModelAndView("Course/DisplayAssignment2", "modelname", model);
            return mav;
        } else {
            int latestResponseIndex = studentResponses.size() - 1;
            String responseTitle = studentResponses.get(latestResponseIndex).getTitle();
            RedirectView redirectView = new RedirectView("/SMART/displayAssignment/displayresponse/" + responseTitle);
            redirectView.setExposeModelAttributes(false);
            redirectView.setExposePathVariables(false);
            CourseDetailsModel courseDetails = studentResponses.get(latestResponseIndex);
            mav = new ModelAndView(redirectView);
            mav.addObject("courseDetails", courseDetails);
            return mav;
        }

    }

    @RequestMapping(value = "/referenceModel")
    public ModelAndView referenceModel(@ModelAttribute("referencemodel") CourseDetails referencemodel) {

        courseDetailsModel.setReferencedetails(referencemodel);
        ModelAndView mav = new ModelAndView("Course/ExpertForm", "model", courseDetailsModel);
        return mav;
    }

    @RequestMapping(value = "/dashboard", method = RequestMethod.POST)
    public ModelAndView dashboard(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("userDetails") UserDetails userDetails) {
        System.out.println("StudentController.java Dashboard() method");
        ErrorDetails errorDetails = new ErrorDetails();
        HttpSession session = request.getSession();
        ModelAndView mav;
        if (userDetails != null && userDetails.getUserName() != null && userDetails.getPassword() != null) {
            if (courseDetailsDaoImpl.checkValidResearcher(userDetails) == 0) {
                ModelAndView mav2;
                errorDetails.setBackLink("/SMART");
                errorDetails.setBackText("Go to Home");
                errorDetails.setHeading("Account Approval");
                errorDetails
                        .setMessage("Account pending approval. Please wait for confirmation email from administrator.");
                errorDetails.setTitle("Login");
                mav2 = new ModelAndView("Course/LoginError", "model", errorDetails);
                return mav2;
            }

            userDetails = courseDetailsDaoImpl.userlogin(userDetails);
            if (userDetails.getLoginStatus().equals("invalid")) {
                ModelAndView mav2;
                errorDetails.setBackLink("login");
                errorDetails.setBackText("Go to Login");
                errorDetails.setHeading("User Login");
                errorDetails.setMessage("This account is no longer valid. Please contact the help disk.");
                errorDetails.setTitle("Login");
                mav2 = new ModelAndView("Course/LoginError", "model", errorDetails);
                return mav2;
            }
            if (userDetails.getLoginStatus().equals("failed")) {
                ModelAndView mav2;
                errorDetails.setBackLink("login");
                errorDetails.setBackText("Go to Login");
                errorDetails.setHeading("User Login");
                errorDetails.setMessage("Username or Password is incorrect. Try again.");
                errorDetails.setTitle("Login");
                mav2 = new ModelAndView("Course/LoginError", "model", errorDetails);
                return mav2;
            }
            log.info("Logged in Userid: " + userDetails.getUserID());
            // set session variables for user details
            session.setAttribute("userid", userDetails.getUserID());
            session.setAttribute("username", userDetails.getUserName());
            session.setAttribute("firstname", userDetails.getFirstName());
            session.setAttribute("usertype", userDetails.getUserType());
            if (userDetails.getUserType().equals("admin")) {
                session.setAttribute("admin", 1);
                session.setAttribute("researcherid", userDetails.getUserID());
            } else {
                session.setAttribute("admin", 0);
            }

            if (userDetails.getUserType().toString().equals("researcher")) {
                session.setAttribute("researcherid", userDetails.getUserID());
            } else {
                session.setAttribute("researcherid", -1);
            }
        } else {
            userDetails.setLoginStatus("failed");
        }
        if (userDetails.getLoginStatus().equals("success")) {
            if (userDetails.getUserType().toString().equals("teacher")) {
                studentmodel.setInstructordetails(userDetails);
                mav = new ModelAndView("redirect:InstructorDashboard", "model", studentmodel);
            } else if (userDetails.getUserType().toString().equals("student")) {
                userDetailsModel.setUserDetails(userDetails);
                mav = new ModelAndView("redirect:StudentDashboard", "model", userDetailsModel);
            } else if (userDetails.getUserType().toString().equals("researcher")) {
                studentmodel.setInstructordetails(userDetails);
                mav = new ModelAndView("redirect:InstructorDashboard", "model", studentmodel);
            } else if (userDetails.getUserType().toString().equals("admin")) {
                mav = new ModelAndView("Course/ResearcherDashboard", "model", userDetails);
            } else {
                mav = new ModelAndView("Course/overview", "model", userDetails);
            }
        } else {
            mav = new ModelAndView("Course/Login", "model", userDetails);
        }
        userDetailsModel.setUserDetails(userDetails);
        return mav;
    }

    @RequestMapping(value = "/InstructorDashboard")
    public ModelAndView InstructorDashboard(HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav;
        HttpSession session = request.getSession();
        if (session.getAttribute("userid") == null) {
            mav = new ModelAndView("redirect:login");
            return mav;
        }
        int userId = (int) session.getAttribute("userid");

        userDetails = new UserDetails();
        InstructorModel instructorModel = new InstructorModel();
        // session.setAttribute("userType", "instructor");
        userDetails.setUserID(userId);
        userDetails.setUserName((String) session.getAttribute("username"));
        userDetails.setFirstName((String) session.getAttribute("firstname"));
        userDetails.setUserType((String) session.getAttribute("usertype"));
        instructorModel.setInstructordetails(userDetails);
        InstructorManagement instructorManagement = new InstructorManagement();
        instructorModel = instructorManagement.getAllStudentsOfInstructorByClass(
                instructorModel.getInstructordetails().getUserID(), managementDaoImpl, instructorModel);
        instructorModel = instructorManagement.setValuesForDashboardStats(instructorModel, courseDetailsDaoImpl);
        mav = new ModelAndView("Course/InstructorDashboard", "model", instructorModel);

        return mav;
    }

    @RequestMapping(value = "/myStudents")
    public ModelAndView mystudents(HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav;
        HttpSession session = request.getSession();
        if (session.getAttribute("userid") == null) {
            mav = new ModelAndView("redirect:login");
            return mav;
        }
        session.setAttribute("classIdSelected", 0);
        int userId = (int) session.getAttribute("userid");
        UserDetails instructorDetails = new UserDetails();
        userDetailsModel.getUserDetails().setUserID(userId);
        // session.setAttribute("userid", userId);

        studentmodel.getInstructordetails().setUserID(userDetailsModel.getUserDetails().getUserID());
        instructorDetails = courseDetailsDaoImpl.getUserDetailsbyId(userDetailsModel.getUserDetails().getUserID());
        studentmodel.setInstructordetails(instructorDetails);
        StudentManagement studentManagement = new StudentManagement();
        // studentmodel=studentManagement.getClassesOfTeacher(studentmodel.getInstructordetails(),courseDetailsDaoImpl);
        log.info("Instructor ID: " + userDetailsModel.getUserDetails().getUserID());
        studentmodel = studentManagement.getAllStudentsOfTeacherByClass(studentmodel.getInstructordetails().getUserID(),
                courseDetailsDaoImpl, studentmodel);
        studentmodel.setInstructordetails(instructorDetails);
        mav = new ModelAndView("Course/ClassManagement2", "model", studentmodel);
        return mav;
    }

    @RequestMapping(value = "/Classes.CSV")
    public void getClassCSV(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userid");
        // session.setAttribute("userid", userId);
        studentmodel.getInstructordetails().setUserID(userId);
        StudentManagement studentManagement = new StudentManagement();
        studentmodel = studentManagement.getAllStudentsOfTeacherByClass(studentmodel.getInstructordetails().getUserID(),
                courseDetailsDaoImpl, studentmodel);
        response.setContentType("application/csv");
        PrintWriter w = response.getWriter();
        w.println(studentManagement.generateCsvFile(studentmodel));
    }

    @RequestMapping(value = "/downloadexpert.CSV/{expid}")
    public ModelAndView downloadexpertmodel(@PathVariable(value = "expid") int id, HttpServletResponse response)
            throws IOException {

        courseDetailsModel = courseDetailsDaoImpl.ExpertModelValues(id);
        courseDetailsModel.setExpertID(id);
        StudentManagement studentManagement = new StudentManagement();
        courseDetailsModel.setNoOfWords(studentManagement.countWords(courseDetailsModel.getText()));
        InstructorManagement instructormanagement = new InstructorManagement();

        response.setContentType("application/csv");
        response.setHeader("content-disposition", "attachment;filename=expertmetrices.csv");
        PrintWriter w = response.getWriter();
        w.println(instructormanagement.generatecsv(courseDetailsModel));
        w.flush();
        w.close();

        return null;
    }

    @RequestMapping(value = "/downloadStudent.CSV/{stuid}")
    public ModelAndView downloadstudentmodel(@PathVariable(value = "stuid") int id, HttpServletResponse response)
            throws IOException {

        courseDetailsModel = courseDetailsDaoImpl.getstudentmodel(id);
        // courseDetailsModel.setExpertID(id);
        StudentManagement studentManagement = new StudentManagement();
        courseDetailsModel.setNoOfWords(studentManagement.countWords(courseDetailsModel.getText()));
        InstructorManagement instructormanagement = new InstructorManagement();

        response.setContentType("application/csv");
        response.setHeader("content-disposition", "attachment;filename=Studentmetrices.csv");
        PrintWriter w = response.getWriter();
        w.println(instructormanagement.generatecsvstu(courseDetailsModel));
        w.flush();
        w.close();

        return null;
    }

    @RequestMapping(value = "/studentManagement")
    public ModelAndView studentManagement(@RequestParam(value = "id", required = true) int userId,
            @ModelAttribute("classDetails") ClassDetails classDetails) {
        studentmodel.setInstructordetails(userDetailsModel.getUserDetails());
        ModelAndView mav = new ModelAndView("Course/StudentManagement", "studentModel", studentmodel);
        return mav;
    }

    @RequestMapping(value = "/ProjectManagement")
    public ModelAndView displayprojects(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        ModelAndView mav;
        if (session.getAttribute("userid") == null) {
            mav = new ModelAndView("redirect:login");
            return mav;
        }
        userDetails = new UserDetails();
        int userId = (int) session.getAttribute("userid");

        userDetails.setUserID(userId);
        userDetails.setUserName((String) session.getAttribute("username"));
        userDetails.setFirstName((String) session.getAttribute("firstname"));
        userDetails.setUserType((String) session.getAttribute("usertype"));

        projectmanagement.setInstructorDetails(userDetails);
        projectmanagement.setExistingprojects(courseDetailsDaoImpl.getprojects(userDetails.getUserID()));
        projectmanagement.setExistingassignments(courseDetailsDaoImpl.getassignments(userDetails.getUserID()));
        mav = new ModelAndView("Course/ProjectManagement", "model", projectmanagement);
        return mav;
    }

    @RequestMapping(value = "/addproject", method = RequestMethod.POST)
    public ModelAndView addproject(@ModelAttribute("projectDetails") ProjectDetails projectDetails,
            @RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String username = session.getAttribute("username").toString();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(timestamp);
        String filename = username + "_" + sdf + ".jpg";
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();

                // Creating the directory to store file

                String path = this.getClass().getClassLoader().getResource("").getPath();
                String fullPath = URLDecoder.decode(path, "UTF-8");
                String pathArr[] = fullPath.split("/WEB-INF/classes/");
                fullPath = pathArr[0];
                File dir = new File(fullPath + File.separator + "tiles//");
                if (!dir.exists())
                    dir.mkdirs();

                // Create the file on server
                File serverFile = new File(dir.getAbsolutePath()
                        + File.separator + filename);
                BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream(serverFile));
                stream.write(bytes);
                stream.close();

                projectDetails.setImagepath(filename);

                log.info("Server File Location="
                        + serverFile.getAbsolutePath());

            } catch (Exception e) {
                log.info(e.getMessage());
            }
        } else {
            log.info("file is empty ");
            projectDetails.setImagepath("bookicon.jpg");
        }

        projectmanagement.setProjectadded(courseDetailsDaoImpl.addproject(projectDetails));

        ModelAndView mav = new ModelAndView("redirect:ProjectManagement", "model", projectmanagement);
        return mav;
    }

    @RequestMapping(value = "/removeprojects", method = RequestMethod.POST)
    public String removeprojects(@RequestParam(value = "myArray[]") Integer[] myArray) {

        courseDetailsDaoImpl.removeproject(myArray);
        return "hello";
    }

    @RequestMapping(value = "/updateproject", method = RequestMethod.POST)
    public ModelAndView updateproject(@ModelAttribute("projectDetails") ProjectDetails projectDetails,
            @RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String username = session.getAttribute("username").toString();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(timestamp);
        String filename = username + "_" + sdf + ".jpg";
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();

                // Creating the directory to store file

                String path = this.getClass().getClassLoader().getResource("").getPath();
                String fullPath = URLDecoder.decode(path, "UTF-8");
                String pathArr[] = fullPath.split("/WEB-INF/classes/");
                System.out.println(fullPath);
                System.out.println(pathArr[0]);
                fullPath = pathArr[0];
                File dir = new File(fullPath + File.separator + "tiles//");
                if (!dir.exists())
                    dir.mkdirs();

                // Create the file on server
                File serverFile = new File(dir.getAbsolutePath()
                        + File.separator + filename);
                BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream(serverFile));
                stream.write(bytes);
                stream.close();

                projectDetails.setImagepath(filename);

                log.info("Server File Location="
                        + serverFile.getAbsolutePath());

            } catch (Exception e) {
                log.info(e.getMessage());
            }
        } else {
            log.info("file is empty ");
        }
        courseDetailsDaoImpl.editproject(projectDetails);
        ModelAndView mav = new ModelAndView("redirect:ProjectManagement", "model", projectmanagement);
        return mav;
    }

    @RequestMapping(value = "/newassignmentform", method = RequestMethod.POST)
    public ModelAndView newassignmentform(@ModelAttribute("assignmentDetails") AssignmentDetails assignmentDetails) {
        ModelAndView mav = new ModelAndView("Course/NewAssignment", "model", assignmentDetails);
        return mav;
    }

    @RequestMapping(value = "/addassignment", method = RequestMethod.POST)
    public ModelAndView addassignment(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("assignmentDetails") AssignmentDetails assignmentDetails) {
        projectmanagement.setAssignmentadded(courseDetailsDaoImpl.addassignment(assignmentDetails));
        HttpSession session = request.getSession();
        session.setAttribute("assignid", projectmanagement.getAssignmentadded().getAssgnmntId());

        ModelAndView mav = new ModelAndView("redirect:0", "model", projectmanagement);

        return mav;
    }

    @RequestMapping(value = "/{assignid}")
    public ModelAndView assignment(@PathVariable(value = "assignid") int id, HttpServletRequest request,
            HttpServletResponse response) {

        HttpSession session = request.getSession();
        ModelAndView mav;
        if (session.getAttribute("userid") == null) {
            mav = new ModelAndView("redirect:login");
            return mav;
        }
        int userId = (int) session.getAttribute("userid");
        int assignid;
        if (id == 0) {
            assignid = (int) session.getAttribute("assignid");

            assignmentDetails = courseDetailsDaoImpl.getassignmentdetails(assignid);
        } else {
            session.setAttribute("assignid", id);

            assignmentDetails = courseDetailsDaoImpl.getassignmentdetails(id);
        }

        projectmanagement.getInstructorDetails().setUserID(userId);
        // get classes assigned to an assignment
        assignmentDetails.setClassdetails(courseDetailsDaoImpl.getclassofassignment(assignmentDetails.getAssgnmntId()));
        assignmentDetails
                .setChoosenmodelID(courseDetailsDaoImpl.getchoosenexpertmodelid(assignmentDetails.getAssgnmntId()));
        projectmanagement.setDisplayedAssignment(assignmentDetails);
        // get all classes added by the instructor
        projectmanagement
                .setClassdetails(courseDetailsDaoImpl.getclases(projectmanagement.getInstructorDetails().getUserID()));
        projectmanagement
                .setReferencemodels(courseDetailsDaoImpl.getallexpertmodels(assignmentDetails.getAssgnmntId()));

        mav = new ModelAndView("Course/Assignment", "model", projectmanagement);
        return mav;
    }

    @RequestMapping(value = "/editassignmentpage", method = RequestMethod.POST)
    public ModelAndView editassignmentpage(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("assignmentDetails") AssignmentDetails assignmentDetails) {

        assignmentDetails = courseDetailsDaoImpl.getassignmentdetails(assignmentDetails.getAssgnmntId());
        ModelAndView mav = new ModelAndView("Course/EditAssignment", "model", assignmentDetails);
        return mav;
    }

    @RequestMapping(value = "/updateassignment", method = RequestMethod.POST)
    public ModelAndView updateassignment(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("assignmentDetails") AssignmentDetails assignmentDetails) {
        System.out.println("StudentController.java updateassignment() assignmentDetails value: "
                + assignmentDetails.getDescription());
        courseDetailsDaoImpl.updateassignment(assignmentDetails);
        ModelAndView mav = new ModelAndView("redirect:0", "model", projectmanagement);
        return mav;
    }

    @RequestMapping(value = "/assignment", params = "delete", method = RequestMethod.POST)
    public ModelAndView removeassignment(@ModelAttribute("assignmentDetails") AssignmentDetails assignmentDetails) {
        courseDetailsDaoImpl.deleteassignment(assignmentDetails.getAssgnmntId());
        ModelAndView mav = new ModelAndView("redirect:ProjectManagement", "model", projectmanagement);
        return mav;
    }

    @RequestMapping(value = "/expertmodel", params = "select", method = RequestMethod.POST)
    public ModelAndView chooseExpertModel(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("referencemodel") CourseDetails referencemodel) {
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userid");
        int assignid = (int) session.getAttribute("assignid");

        courseDetailsDaoImpl.choosemodel(referencemodel);
        assignmentDetails.setAssgnmntId(assignid);
        ModelAndView mav = new ModelAndView("redirect:0", "model", projectmanagement);
        return mav;
    }

    @RequestMapping(value = "/expertmodel", params = "edit", method = RequestMethod.POST)
    public ModelAndView editExpertModel(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("referencemodel") CourseDetails referencemodel) {
        HttpSession session = request.getSession();
        ModelAndView mav;
        if (session.getAttribute("userid") == null) {
            mav = new ModelAndView("redirect:login");
            return mav;
        }
        courseDetailsModel = courseDetailsDaoImpl.ExpertModelValues(referencemodel.getModelID());
        String text = courseDetailsModel.getText().replaceAll("<b>", "");
        text = text.replaceAll("</b>", "");
        courseDetailsModel.setText(text);
        courseDetailsModel.setExpertID(referencemodel.getModelID());
        mav = new ModelAndView("Course/Editexpertmodel", "model", courseDetailsModel);
        return mav;
    }

    @RequestMapping(value = "/updateexpertmodel", method = RequestMethod.POST)
    public ModelAndView updateexpertmodel(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("courseDetails") CourseDetails courseDetails) {

        log.info("logged info successfully");
        courseDetailsModel.setExpertID(courseDetails.getModelID());
        ModelAndView mav;
        if (null != smartTestBoImpl) {
            courseDetailsModel = smartTestBoImpl.smartTestNplImpl(courseDetails, "expert");
        }

        if (null != courseDetailsModel.getExpert()) {
            courseDetailsModel = displayGraph.createJsonExpertBase(courseDetailsModel);
        } else {
            courseDetailsModel = displayGraph.createJsonExpert(courseDetailsModel);
        }

        courseDetailsDaoImpl.updatereference(courseDetailsModel);
        HttpSession session = request.getSession();
        session.setAttribute("expertid", courseDetailsModel.getExpertID());

        mav = new ModelAndView("redirect:concepts");
        return mav;
    }

    @RequestMapping(value = "/expertmodel", params = "remove", method = RequestMethod.POST)
    public ModelAndView deleteExpertModel(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("referencemodel") CourseDetails referencemodel) {

        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userid");
        int assignid = (int) session.getAttribute("assignid");
        assignmentDetails = new AssignmentDetails();
        assignmentDetails = courseDetailsDaoImpl.getassignmentdetails(assignid);
        assignmentDetails.setAssgnmntId(assignid);
        courseDetailsDaoImpl.deletereferencemodel(referencemodel.getModelID());
        assignment(assignmentDetails.getAssgnmntId(), request, response);
        ModelAndView mav = new ModelAndView("redirect:0", "model", projectmanagement);
        return mav;
    }

    @RequestMapping(value = "/addclasstoassignment", method = RequestMethod.POST)
    public ModelAndView addclasstoassignment(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("assignmentDetails") AssignmentDetails assignmentDetails) {
        courseDetailsDaoImpl.addclasstoassgn(assignmentDetails.getAssgnmntId(),
                assignmentDetails.getSelectedclassIDtoadd());
        ModelAndView mav = new ModelAndView("redirect:0", "model", projectmanagement);
        return mav;
    }

    @RequestMapping(value = "/registerAs", method = RequestMethod.GET)
    public ModelAndView registerAs() {

        ModelAndView mav = new ModelAndView("Course/RegisterAs");
        return mav;
    }

    @RequestMapping(value = "/removeclassesfromassignment", method = RequestMethod.POST)
    public String removeclassesfromassignment(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value = "classid") Integer classid) {
        HttpSession session = request.getSession();
        int assignid = (int) session.getAttribute("assignid");
        courseDetailsDaoImpl.removeclassfromassignment(assignid, classid);
        return "hello";
    }

    @RequestMapping(value = "/registerUser", method = RequestMethod.POST)
    public ModelAndView registerUser(@ModelAttribute("userDetails") UserDetails userDetails) {
        userDetailsModel = userRegistration.setUserType(userDetailsModel, userDetails.getUserType());
        ModelAndView mav;
        if (userDetails.getUserType().equals("student")) {
            mav = new ModelAndView("Course/RegistrationFormStudent", "userDetails", userDetailsModel);
        } else {
            mav = new ModelAndView("Course/RegistrationForm", "userDetails", userDetailsModel);
        }
        return mav;
    }

    @RequestMapping(value = "/registerlogin", method = RequestMethod.POST)
    public ModelAndView registerlogin(@ModelAttribute("userDetails") UserDetails userDetails,
            HttpServletRequest request, HttpServletResponse response) {
        userDetails.setUserType(userDetailsModel.getUserDetails().getUserType());
        userDetailsModel = userRegistration.setUserModel(userDetails, userDetailsModel);
        ErrorDetails errorDetails = new ErrorDetails();
        HttpSession session = request.getSession();
        session.setAttribute("usertype", userDetails.getUserType());

        System.out.println("User Type: " + userDetails.getUserType());
        ModelAndView mav;
        int flag = 0;
        if (userDetails != null) {
            flag = courseDetailsDaoImpl.saveUser(userDetails);
        }
        if (flag == 1) {
            if (userDetails.getUserType().equals("researcher") || userDetails.getUserType().equals("teacher")) {
                mav = new ModelAndView("Course/requestAccount", "userDetails", userDetails);
                return mav;
            } else {
                mav = new ModelAndView("Course/Login", "userDetails", userDetailsModel);
            }
        }

        else if (flag == 3) {
            ModelAndView mav2;
            errorDetails.setBackLink("registerAs");
            errorDetails.setBackText("Go to Register");
            errorDetails.setHeading("User Registration");
            errorDetails.setMessage("Username already exists! Choose a different Username for your account.");
            errorDetails.setTitle("User Registration");
            mav2 = new ModelAndView("Course/LoginError", "model", errorDetails);
            return mav2;
        } else {
            errorDetails.setBackLink("registerAs");
            errorDetails.setBackText("Go to Register");
            errorDetails.setHeading("User Registration");
            errorDetails.setMessage(
                    "Error occured while registering your account. Please try again. If the error persists, contact the help desk.");
            errorDetails.setTitle("User Registration");
            mav = new ModelAndView("Course/LoginError", "model", errorDetails);
            return mav;
        }
        return mav;
    }

    @RequestMapping(value = "/addclass", method = RequestMethod.POST)
    public ModelAndView addclass(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("classDetails") ClassDetails classDetails) {
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userid");
        userDetailsModel.getUserDetails().setUserID(userId);
        ErrorDetails errorDetails = new ErrorDetails();
        classDetails.setTeacherId(userDetailsModel.getUserDetails().getUserID());
        int status = 0, exist = 0;
        InstructorManagement instructorManagement = new InstructorManagement();
        ModelAndView mav;
        classDetails.setTeacherId(userDetailsModel.getUserDetails().getUserID());
        exist = instructorManagement.checkIfClassNameExists(classDetails, managementDaoImpl);
        UserDetails instructorDetails = new UserDetails();
        studentmodel.getInstructordetails().setUserID(userDetailsModel.getUserDetails().getUserID());
        instructorDetails = courseDetailsDaoImpl.getUserDetailsbyId(userDetailsModel.getUserDetails().getUserID());
        studentmodel.setInstructordetails(instructorDetails);
        StudentManagement studentManagement = new StudentManagement();
        log.info("Instructor ID: " + userDetailsModel.getUserDetails().getUserID());
        studentmodel = studentManagement.getAllStudentsOfTeacherByClass(studentmodel.getInstructordetails().getUserID(),
                courseDetailsDaoImpl, studentmodel);
        studentmodel.setInstructordetails(instructorDetails);
        studentmodel.setId(classDetails.getClassId());
        log.info("Exist value: " + exist);
        if (classDetails.getClassId() != -1) {
            log.info("Edit class!!!!");
            if (exist == 1) {
                errorDetails.setBackLink("myStudents");
                errorDetails.setBackText("Go back to My Classes");
                errorDetails.setHeading("Edit Class");
                errorDetails.setMessage("Class Name " + classDetails.getClassName()
                        + " already exists! Please choose a different name.");
                errorDetails.setTitle("Class Management");
                mav = new ModelAndView("Course/InstructorErrors", "model", errorDetails);
                return mav;
            }
            status = courseDetailsDaoImpl.editClass(classDetails);
            if (status == 0) {
                errorDetails.setBackLink("myStudents");
                errorDetails.setBackText("Go back to My Classes");
                errorDetails.setHeading("Edit Class");
                errorDetails.setMessage(
                        "Error editing class. Please try again later. If the error persists, contact the help desk.");
                errorDetails.setTitle("Class Management");
                mav = new ModelAndView("Course/InstructorErrors", "model", errorDetails);
            }
        } else {
            if (exist == 1) {
                errorDetails.setBackLink("myStudents");
                errorDetails.setBackText("Go back to My Classes");
                errorDetails.setHeading("Add Class ");
                errorDetails.setMessage("Class Name " + classDetails.getClassName()
                        + " already exists! Please choose a different name.");
                errorDetails.setTitle("Class Management");
                mav = new ModelAndView("Course/InstructorErrors", "model", errorDetails);
                return mav;
            }
            status = courseDetailsDaoImpl.addClass(classDetails);
            if (status == 0) {
                errorDetails.setBackLink("myStudents");
                errorDetails.setBackText("Go back to My Classes");
                errorDetails.setHeading("Add Class");
                errorDetails.setMessage(
                        "Error adding class. Please try again later. If the error persists, contact the help desk.");
                errorDetails.setTitle("Class Management");
                mav = new ModelAndView("Course/InstructorErrors", "model", errorDetails);
            }
        }
        studentmodel = studentManagement.getAllStudentsOfTeacherByClass(studentmodel.getInstructordetails().getUserID(),
                courseDetailsDaoImpl, studentmodel);
        studentmodel.setExist(exist);
        mav = new ModelAndView("Course/ClassManagement2", "model", studentmodel);
        return mav;
    }

    @RequestMapping(value = "/addstudent", method = RequestMethod.POST)

    public ModelAndView addstudent(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("userDetails") UserDetails studentDetails) {
        ErrorDetails errorDetails = new ErrorDetails();
        ModelAndView mav;
        int status = 0;
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userid");
        userDetailsModel.getUserDetails().setUserID(userId);
        studentDetails.setUserType("student");
        log.info("Class ID: " + studentDetails.getClassId());
        session.setAttribute("classIdSelected", Integer.parseInt(studentDetails.getClassId()));
        StudentManagement studentManagement = new StudentManagement();
        status = studentManagement.addStudent(studentDetails, courseDetailsDaoImpl);
        if (status == 2) {
            // username doesnt exist
            errorDetails.setBackLink("myStudents");
            errorDetails.setBackText("Go back to My Classes");
            errorDetails.setHeading("Add Student");
            errorDetails.setMessage("Username doesn't exist. If not yet registered, register and then add to class.");
            errorDetails.setTitle("Class Management");
            mav = new ModelAndView("Course/InstructorErrors", "model", errorDetails);
            return mav;
        } else if (status == 4) {
            // user not student
            errorDetails.setBackLink("myStudents");
            errorDetails.setBackText("Go back to My Classes");
            errorDetails.setHeading("Add Student");
            errorDetails.setMessage(
                    "User is not registered as a student. Only users registered as student can be added to class.");
            errorDetails.setTitle("Class Management");
            mav = new ModelAndView("Course/InstructorErrors", "model", errorDetails);
            return mav;
        } else if (status == 3) {
            // student already added to class
            errorDetails.setBackLink("myStudents");
            errorDetails.setBackText("Go back to My Classes");
            errorDetails.setHeading("Add Class");
            errorDetails.setMessage("Student is already added to class.");
            errorDetails.setTitle("Class Management");
            mav = new ModelAndView("Course/InstructorErrors", "model", errorDetails);
            return mav;
        } else if (status == 0) {
            // error
            errorDetails.setBackLink("myStudents");
            errorDetails.setBackText("Go back to My Classes");
            errorDetails.setHeading("Add Class");
            errorDetails.setMessage(
                    "Error adding student to class. Please try again later. If the error persists, contact the help desk.");
            errorDetails.setTitle("Class Management");
            mav = new ModelAndView("Course/InstructorErrors", "model", errorDetails);
            return mav;
        }

        UserDetails instructorDetails = new UserDetails();
        userDetailsModel.getUserDetails().setUserID(userId);
        studentmodel.getInstructordetails().setUserID(userDetailsModel.getUserDetails().getUserID());
        instructorDetails = courseDetailsDaoImpl.getUserDetailsbyId(userDetailsModel.getUserDetails().getUserID());
        studentmodel.setInstructordetails(instructorDetails);
        log.info("Instructor ID: " + userDetailsModel.getUserDetails().getUserID());
        studentmodel = studentManagement.getAllStudentsOfTeacherByClass(studentmodel.getInstructordetails().getUserID(),
                courseDetailsDaoImpl, studentmodel);
        studentmodel.setInstructordetails(instructorDetails);
        mav = new ModelAndView("Course/ClassManagement2", "model", studentmodel);
        return mav;

    }

    @RequestMapping(value = "/removeClasses", method = RequestMethod.POST)
    public ModelAndView removeClasses(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("classDetails") ClassDetails classDetails) {
        ModelAndView mav;
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userid");
        int status = 0;
        ErrorDetails errorDetails = new ErrorDetails();
        userDetailsModel.getUserDetails().setUserID(userId);
        classDetails.setTeacherId(userDetailsModel.getUserDetails().getUserID());
        StudentManagement studentManagement = new StudentManagement();
        log.info("Remove Classes");
        session.setAttribute("classIdSelected", 0);
        System.out.println("Remove Classes");
        if (classDetails.getStudentIds() != null) {
            classDetails.setClassId(classDetails.getClassIdSelected());

            status = studentManagement.removeStudentsFromClass(classDetails.getClassId(), classDetails.getStudentIds(),
                    courseDetailsDaoImpl);
            if (status == 0) {
                errorDetails.setBackLink("myStudents");
                errorDetails.setBackText("Go back to My Classes");
                errorDetails.setHeading("Remove Student(s)");
                errorDetails.setMessage(
                        "Error removing student(s) from class. Please try again later. If the error persists, contact the help desk.");
                errorDetails.setTitle("Class Management");
                mav = new ModelAndView("Course/InstructorErrors", "model", errorDetails);
                return mav;
            }
            session.setAttribute("classIdSelected", classDetails.getClassId());
        } else if (classDetails.getClassIds() != null) {
            status = studentManagement.removeClasses(classDetails.getTeacherId(), classDetails.getClassIds(),
                    courseDetailsDaoImpl);
            if (status == 0) {
                errorDetails.setBackLink("myStudents");
                errorDetails.setBackText("Go back to My Classes");
                errorDetails.setHeading("Remove Class(es)");
                errorDetails.setMessage(
                        "Error removing class(es) from class. Please try again later. If the error persists, contact the help desk.");
                errorDetails.setTitle("Class Management");
                mav = new ModelAndView("Course/InstructorErrors", "model", errorDetails);
                return mav;
            }
        }

        UserDetails instructorDetails = new UserDetails();
        userDetailsModel.getUserDetails().setUserID(userId);
        studentmodel.getInstructordetails().setUserID(userDetailsModel.getUserDetails().getUserID());
        instructorDetails = courseDetailsDaoImpl.getUserDetailsbyId(userDetailsModel.getUserDetails().getUserID());
        studentmodel.setInstructordetails(instructorDetails);
        log.info("Instructor ID: " + userDetailsModel.getUserDetails().getUserID());
        studentmodel = studentManagement.getAllStudentsOfTeacherByClass(studentmodel.getInstructordetails().getUserID(),
                courseDetailsDaoImpl, studentmodel);
        studentmodel.setInstructordetails(instructorDetails);
        mav = new ModelAndView("Course/ClassManagement2", "model", studentmodel);
        return mav;
    }

    @RequestMapping(value = "/removeStudents", method = RequestMethod.POST)
    public ModelAndView removeStudents(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("studentRemoveDetails") ClassDetails classDetails) {
        ModelAndView mav;
        int status = 0;
        ErrorDetails errorDetails = new ErrorDetails();
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userid");
        userDetailsModel.getUserDetails().setUserID(userId);

        if (classDetails.getStudentIds() == null) {
            mav = mystudents(request, response);
            return mav;
        }
        classDetails.setTeacherId(userDetailsModel.getUserDetails().getUserID());
        StudentManagement studentManagement = new StudentManagement();
        status = studentManagement.removeStudentsFromClass(classDetails.getClassId(), classDetails.getStudentIds(),
                courseDetailsDaoImpl);
        if (status == 0) {
            errorDetails.setBackLink("myStudents");
            errorDetails.setBackText("Go back to My Classes");
            errorDetails.setHeading("Remove Student(s)");
            errorDetails.setMessage(
                    "Error removing student(s) from class. Please try again later. If the error persists, contact the help desk.");
            errorDetails.setTitle("Class Management");
            mav = new ModelAndView("Course/InstructorErrors", "model", errorDetails);
            return mav;
        }
        mav = mystudents(request, response);
        return mav;
    }

    @RequestMapping(value = "/instructorProfile")
    public ModelAndView instructorProfile(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        ModelAndView mav;
        if (session.getAttribute("userid") == null) {
            mav = new ModelAndView("redirect:login");
            return mav;
        }

        int userId = (int) session.getAttribute("userid");
        userDetailsModel.getUserDetails().setUserID(userId);

        userDetailsModel
                .setUserDetails(courseDetailsDaoImpl.getUserDetailsbyId(userDetailsModel.getUserDetails().getUserID()));
        mav = new ModelAndView("Course/InstructorProfile", "model", userDetailsModel);
        return mav;
    }

    @RequestMapping(value = "/studentProfile")
    public ModelAndView studentProfile(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav;
        HttpSession session = request.getSession();
        if (session.getAttribute("userid") == null) {
            mav = new ModelAndView("redirect:login");
            return mav;
        }
        int userId = (int) session.getAttribute("userid");
        userDetailsModel.getUserDetails().setUserID(userId);

        userDetailsModel
                .setUserDetails(courseDetailsDaoImpl.getUserDetailsbyId(userDetailsModel.getUserDetails().getUserID()));
        mav = new ModelAndView("Course/StudentProfile", "model", userDetailsModel);
        return mav;
    }

    @RequestMapping(value = "/editProfile", method = RequestMethod.POST)
    public ModelAndView editProfile(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("userDetails") UserDetails userDetails) {
        ModelAndView mav;
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userid");
        String type = (String) session.getAttribute("usertype");
        userDetailsModel.getUserDetails().setUserID(userId);
        ErrorDetails errorDetails = new ErrorDetails();

        // log.info("Edit Profile!!!!");
        int status = courseDetailsDaoImpl.editProfile(userDetails);
        if (status == 0) {
            if (type.equals("teacher") || type.equals("researcher")) {
                errorDetails.setBackLink("instructorProfile");
                errorDetails.setBackText("Go back to My Profile");
                errorDetails.setHeading("Edit Profile");
                errorDetails.setMessage(
                        "Error editing profile. Please try again later. If the error persists, contact the help desk.");
                errorDetails.setTitle("My Profile");
                mav = new ModelAndView("Course/InstructorErrors", "model", errorDetails);
                return mav;
            } else if (type.equals("student")) {
                errorDetails.setBackLink("studentProfile");
                errorDetails.setBackText("Go back to My Profile");
                errorDetails.setHeading("Edit Profile");
                errorDetails.setMessage(
                        "Error editing profile. Please try again later. If the error persists, contact the help desk.");
                errorDetails.setTitle("My Profile");
                mav = new ModelAndView("Course/StudentErrors", "model", errorDetails);
                return mav;
            }
        } else {
            if (type.equals("teacher") || type.equals("researcher")) {
                mav = instructorProfile(request, response);
                return mav;
            }

            else if (type.equals("student")) {
                mav = studentProfile(request, response);
                return mav;
            }
        }
        mav = new ModelAndView("Course/Home");
        return mav;
    }

    @RequestMapping(value = "/instructorResults", method = RequestMethod.POST)
    public ModelAndView instructorResults(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("assgntDetails") AssignmentDetails assignmentDetails) {
        System.out.println("StudentController.java ModelAndView Method");

        InstructorModel instructorModel = new InstructorModel();
        HttpSession session = request.getSession();
        ModelAndView mav;
        if (session.getAttribute("userid") == null) {
            mav = new ModelAndView("redirect:login");
            return mav;
        }
        int userId = (int) session.getAttribute("userid");
        userDetailsModel.getUserDetails().setUserID(userId);
        int assgnmntId = (int) session.getAttribute("assignid");
        int download = 0;
        ErrorDetails errorDetails = new ErrorDetails();

        int classId = assignmentDetails.getClassId();
        assignmentDetails = managementDaoImpl.getAssignmentDetails(assgnmntId);
        assignmentDetails.setClassId(classId);
        System.out.println();
        InstructorManagement instructorManagement = new InstructorManagement();
        instructorModel.getInstructordetails().setUserID(userId);
        instructorModel.setAssignmentDetails(assignmentDetails);
        instructorModel.setClassDetails(courseDetailsDaoImpl.getClassDetailsbyId(classId));
        instructorModel = instructorManagement.getClassLevelResultOfAssgnt(assignmentDetails.getAssgnmntId(),
                managementDaoImpl, instructorModel, download);
        if (instructorModel.getStatus() == -2) {
            errorDetails.setBackLink(Integer.toString(assgnmntId));
            errorDetails.setBackText("Go back to Assignment");
            errorDetails.setHeading("Results");
            errorDetails.setMessage("No Student Results to display.");
            errorDetails.setTitle("Assignment Results");
            mav = new ModelAndView("Course/InstructorErrors", "model", errorDetails);
            return mav;
        }
        mav = new ModelAndView("Course/InstructorResults", "model", instructorModel);

        return mav;
    }

    @RequestMapping(value = "/Results.CSV", method = RequestMethod.POST)
    public void getResultsCSV(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("model") ClassDetails classDetails) {

        int download = 1;
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userid");
        userDetailsModel.getUserDetails().setUserID(userId);
        // session.setAttribute("userid", userId);
        System.out.println("Download........");
        log.info("Download........");
        try {
            InstructorModel instructorModel = new InstructorModel();
            InstructorManagement instructorManagement = new InstructorManagement();
            instructorModel.getInstructordetails().setUserID(userId);
            instructorModel
                    .setAssignmentDetails(managementDaoImpl.getAssignmentDetails(classDetails.getAssignmentId()));
            instructorModel.setClassDetails(courseDetailsDaoImpl.getClassDetailsbyId(classDetails.getClassId()));

            instructorModel = instructorManagement.getClassLevelResultOfAssgntForDownload(
                    assignmentDetails.getAssgnmntId(), managementDaoImpl, instructorModel, download);
            response.setContentType("application/csv");
            System.out.println("CSV file download data loaded");
            PrintWriter w;

            w = response.getWriter();
            w.println(instructorManagement.generateResultsCsvFile(instructorModel));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @RequestMapping(value = "/StudentModel.CSV", method = RequestMethod.POST)
    public void getstudentmodelCSV(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("model") ClassDetails classDetails) throws IOException {
        int download = 1;
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userid");
        userDetailsModel.getUserDetails().setUserID(userId);
        System.out.println("Download........");
        log.info("Download........");
        InstructorModel instructorModel = new InstructorModel();
        InstructorManagement instructorManagement = new InstructorManagement();
        instructorModel.getInstructordetails().setUserID(userId);
        instructorModel.setAssignmentDetails(managementDaoImpl.getAssignmentDetails(classDetails.getAssignmentId()));
        instructorModel.setClassDetails(courseDetailsDaoImpl.getClassDetailsbyId(classDetails.getClassId()));
        instructorModel = instructorManagement.getClassLevelResultOfAssgntForDownload(assignmentDetails.getAssgnmntId(),
                managementDaoImpl, instructorModel, download);
        response.setContentType("application/csv");
        PrintWriter w = response.getWriter();
        w.println(instructorManagement.generateResultsCsvFileforresearcher(instructorModel));
    }

    @RequestMapping(value = "/ResearcherDashboard")
    public ModelAndView ResearcherDashboard(HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav;
        HttpSession session = request.getSession();

        if (session.getAttribute("userid") == null) {
            mav = new ModelAndView("redirect:login");
            return mav;
        }
        int userId = (int) session.getAttribute("userid");

        userDetails = new UserDetails();
        InstructorModel instructorModel = new InstructorModel();
        userDetails.setUserID(userId);
        userDetails.setUserName((String) session.getAttribute("username"));
        userDetails.setFirstName((String) session.getAttribute("firstname"));
        userDetails.setUserType((String) session.getAttribute("usertype"));
        mav = new ModelAndView("Course/ResearcherDashboard");

        return mav;
    }

    @RequestMapping(value = "/searchUser", method = RequestMethod.POST)
    public ModelAndView searchUser(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("userDetails") UserDetails userDetails) {

        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userid");
        UserDetailsModel userDetailsModel = new UserDetailsModel();
        userDetailsModel.setUserDetails(userDetails);
        userDetailsModel = managementDaoImpl.searchUser(userDetailsModel);
        ModelAndView mav = new ModelAndView("Course/UserSearch", "model", userDetailsModel);

        return mav;
    }

    @RequestMapping(value = "/loginasuser", params = "login", method = RequestMethod.POST)
    public ModelAndView loginasuser(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("userDetails") UserDetails userDetails) {

        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userid");
        session.setAttribute("researcherid", userId);
        session.setAttribute("userid", userDetails.getUserID());
        userDetails = courseDetailsDaoImpl.getUserDetailsbyId(userDetails.getUserID());
        ModelAndView mav = fromadmindashboard(request, response, userDetails);
        return mav;
    }

    @RequestMapping(value = "/fromadmindashboard", method = RequestMethod.POST)
    public ModelAndView fromadmindashboard(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("userDetails") UserDetails userDetails) {
        ErrorDetails errorDetails = new ErrorDetails();
        HttpSession session = request.getSession();
        ModelAndView mav;
        log.info("Logged in Userid: " + userDetails.getUserID());
        // set session variables for user details
        session.setAttribute("userid", userDetails.getUserID());
        session.setAttribute("username", userDetails.getUserName());
        session.setAttribute("firstname", userDetails.getFirstName());
        session.setAttribute("usertype", userDetails.getUserType());

        if (userDetails.getUserType().toString().equals("teacher")) {
            studentmodel.setInstructordetails(userDetails);
            mav = new ModelAndView("redirect:InstructorDashboard", "model", studentmodel);
        } else if (userDetails.getUserType().toString().equals("student")) {
            userDetailsModel.setUserDetails(userDetails);
            mav = new ModelAndView("redirect:StudentDashboard", "model", userDetailsModel);
        } else if (userDetails.getUserType().toString().equals("researcher")) {
            studentmodel.setInstructordetails(userDetails);
            mav = new ModelAndView("redirect:InstructorDashboard", "model", studentmodel);
        } else if (userDetails.getUserType().toString().equals("admin")) {
            mav = new ModelAndView("Course/ResearcherDashboard", "model", userDetails);
        } else {
            mav = new ModelAndView("Course/overview", "model", userDetails);
        }

        userDetailsModel.setUserDetails(userDetails);
        return mav;
    }

    @RequestMapping(value = "/loginasuser", params = "delete", method = RequestMethod.POST)
    public ModelAndView deleteuser(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("userDetails") UserDetails userDetails) {

        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userid");
        UserDetailsModel userDetailsModel = new UserDetailsModel();
        int status = managementDaoImpl.deleteUser(userDetails.getUserID());
        ErrorDetails errorDetails = new ErrorDetails();
        ModelAndView mav;
        if (status == 0) {
            errorDetails.setBackLink("ResearcherDashboard");
            errorDetails.setBackText("Go back to Dashboard");
            errorDetails.setHeading("Delete User");
            errorDetails.setMessage("Error occurred during delete user. Contact help desk.");
            errorDetails.setTitle("Delete");
            mav = new ModelAndView("Course/ResearcherErrors", "model", errorDetails);
            return mav;
        }
        userDetailsModel = managementDaoImpl.searchUser(userDetailsModel);
        mav = new ModelAndView("Course/UserSearch", "model", userDetailsModel);
        return mav;
    }

    @RequestMapping(value = "/requestaccount", method = RequestMethod.POST)
    public ModelAndView requestaccount(@ModelAttribute("maildetails") MailDetails maildetails) {
        ErrorDetails errorDetails = new ErrorDetails();
        StudentManagement studentManagement = new StudentManagement();
        maildetails = studentManagement.composeMessage(maildetails);
        SendEmail sendEmail = new SendEmail();
        sendEmail.sendmail(maildetails);
        ModelAndView mav2;
        errorDetails.setBackLink("/SMART");
        errorDetails.setBackText("Go to Home");
        errorDetails.setHeading("New Account Request");
        errorDetails
                .setMessage("Account successfully requested. Please wait for confirmation email from administrator.");
        errorDetails.setTitle("Request");
        mav2 = new ModelAndView("Course/LoginError", "model", errorDetails);
        return mav2;
    }

    @RequestMapping(value = "/approveResearcher", method = RequestMethod.POST)
    public ModelAndView approveResearcher(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("userDetails") UserDetails userDetails) {

        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userid");
        ErrorDetails errorDetails = new ErrorDetails();
        int status = managementDaoImpl.approveResearcher(userDetails);
        ModelAndView mav2;
        errorDetails.setBackLink("ResearcherDashboard");
        errorDetails.setBackText("Go to Dashboard");
        errorDetails.setHeading("Account Approval");
        if (status != 1) {
            errorDetails.setMessage("Error occurred. Please make sure the username entered is correct.");
        } else {
            errorDetails.setMessage("Account successfully approved.");
        }
        errorDetails.setTitle("Approval");
        MailDetails maildetails = new MailDetails();
        StudentManagement studentManagement = new StudentManagement();
        maildetails.setEmail(managementDaoImpl.getEmail(userDetails).getEmail());
        maildetails = studentManagement.composeResearcherApprovalMessage(maildetails);
        SendEmail sendEmail = new SendEmail();
        sendEmail.sendmail(maildetails);
        mav2 = new ModelAndView("Course/ResearcherErrors", "model", errorDetails);
        return mav2;
    }

    @RequestMapping(value = "/forgotpassword")
    public ModelAndView forgotpassword() {

        ModelAndView mav = new ModelAndView("Course/ForgotPassword");
        return mav;
    }

    @RequestMapping(value = "/newpasswordrequest", method = RequestMethod.POST)
    public ModelAndView newpasswordrequest(@ModelAttribute("userdetails") UserDetails userDetails) {
        ModelAndView mav;
        int status = 0;
        int userdoesntexist = 0;
        StudentManagement studentManagement = new StudentManagement();
        if (userDetails.getUserType().equals("notstudent")) {
            if (managementDaoImpl.forgotPasswordValidUser(userDetails) == 1) {
                String tempPwd = "";
                MailDetails maildetails = new MailDetails();
                maildetails.setEmail(userDetails.getEmail());
                tempPwd = studentManagement.generateTempPassword();
                maildetails.setMessage(tempPwd);
                userDetails.setPassword(tempPwd);
                status = managementDaoImpl.updatePassword(userDetails);
                maildetails = studentManagement.composeTemporaryPasswordMessage(maildetails);
                if (status == 1) {
                    SendEmail sendEmail = new SendEmail();
                    sendEmail.sendmail(maildetails);
                }
            } else {
                userdoesntexist = 1;
            }
            if (status == 0) {
                ErrorDetails errorDetails = new ErrorDetails();
                errorDetails.setBackLink("forgotpassword");
                errorDetails.setBackText("Go back to Forgot Password");
                errorDetails.setHeading("Password Reset");
                if (userdoesntexist == 1) {
                    errorDetails.setMessage("Entered username and email don't match. Please try again.");
                } else {
                    errorDetails.setMessage(
                            "Error requesting temporary password. Please try again later. If error persists, contact the help desk.");
                }
                errorDetails.setTitle("Login");
                mav = new ModelAndView("Course/LoginError", "model", errorDetails);
                return mav;
            } else {
                mav = new ModelAndView("Course/TemporaryPassword");
                return mav;
            }
        } else {
            if (managementDaoImpl.forgotPasswordValidStudent(userDetails) == 1) {
                mav = new ModelAndView("Course/TemporaryPasswordStudent", "model", userDetails);
                return mav;
            } else {
                ErrorDetails errorDetails = new ErrorDetails();
                errorDetails.setBackLink("forgotpassword");
                errorDetails.setBackText("Go back to Forgot Password");
                errorDetails.setHeading("Password Reset");
                errorDetails.setMessage("Entered username and answer don't match. Please try again.");
                errorDetails.setTitle("Login");
                mav = new ModelAndView("Course/LoginError", "model", errorDetails);
                return mav;
            }

        }
    }

    @RequestMapping(value = "/temporaryPassword")
    public ModelAndView temporaryPassword() {

        ModelAndView mav = new ModelAndView("Course/TemporaryPassword");
        return mav;
    }

    @RequestMapping(value = "/changetemppassword", method = RequestMethod.POST)
    public ModelAndView changetemppassword(@ModelAttribute("userdetails") UserDetails userDetails) {
        ModelAndView mav;
        ErrorDetails errorDetails = new ErrorDetails();
        int status = 0;
        status = managementDaoImpl.changeTempPassword(userDetails);
        if (status == 0) {
            errorDetails.setBackLink("temporaryPassword");
            errorDetails.setBackText("Go back");
            errorDetails.setHeading("Reset Password");
            errorDetails.setMessage("Wrong Temporary Password. Try again.");
            errorDetails.setTitle("Login");
        } else {
            errorDetails.setBackLink("login");
            errorDetails.setBackText("Go to Login");
            errorDetails.setHeading("Reset Password");
            errorDetails.setMessage("Password reset successful.");
            errorDetails.setTitle("Login");
        }
        mav = new ModelAndView("Course/LoginError", "model", errorDetails);
        return mav;
    }

    @RequestMapping(value = "/changetemppasswordstudent", method = RequestMethod.POST)
    public ModelAndView changetemppasswordstudent(@ModelAttribute("userdetails") UserDetails userDetails) {
        ModelAndView mav;
        ErrorDetails errorDetails = new ErrorDetails();
        int status = 0;
        status = managementDaoImpl.changePasswordStudent(userDetails);
        if (status == 0) {
            errorDetails.setBackLink("temporaryPassword");
            errorDetails.setBackText("Go back");
            errorDetails.setHeading("Reset Password");
            errorDetails.setMessage("Error resetting password. Please make sure the entered username is valid.");
            errorDetails.setTitle("Login");
        } else {
            errorDetails.setBackLink("login");
            errorDetails.setBackText("Go to Login");
            errorDetails.setHeading("Reset Password");
            errorDetails.setMessage("Password reset successful.");
            errorDetails.setTitle("Login");
        }
        mav = new ModelAndView("Course/LoginError", "model", errorDetails);
        return mav;
    }

    @RequestMapping(value = "/validateUser", method = RequestMethod.POST)
    public ModelAndView validateUser(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("userDetails") UserDetails userDetails) {
        int userid = userDetails.getUserID();
        int status = managementDaoImpl.validateUser(userid);
        ErrorDetails errorDetails = new ErrorDetails();
        ModelAndView mav;
        if (status == 0) {
            errorDetails.setBackLink("ResearcherDashboard");
            errorDetails.setBackText("Go back to Dashboard");
            errorDetails.setHeading("Validate User");
            errorDetails.setMessage("Error occurred during validate user. Contact help desk.");
            errorDetails.setTitle("Validate");
            mav = new ModelAndView("Course/ResearcherErrors", "model", errorDetails);
            return mav;
        }
        errorDetails.setBackLink("ResearcherDashboard");
        errorDetails.setBackText("Go back to Dashboard");
        errorDetails.setHeading("Validate User");
        errorDetails.setMessage("Userid: " + userid + " successfully validated");
        errorDetails.setTitle("Validate");
        mav = new ModelAndView("Course/ResearcherErrors", "model", errorDetails);
        return mav;
    }

    @RequestMapping(value = "/upgradeToResearcher")
    public ModelAndView upgradeToResearcher() {
        ModelAndView mav;
        mav = new ModelAndView("Course/UpgradeToResearcherRequest");
        return mav;
    }

    @RequestMapping(value = "/requestResearcherAccount", method = RequestMethod.POST)
    public ModelAndView requestResearcherAccount(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, @ModelAttribute("maildetails") MailDetails maildetails) {
        ErrorDetails errorDetails = new ErrorDetails();
        StudentManagement studentManagement = new StudentManagement();
        int userid = (int) session.getAttribute("userid");
        UserDetails userDetails = courseDetailsDaoImpl.getUserDetailsbyId(userid);
        maildetails.setEmail(userDetails.getEmail());
        maildetails.setFirstName(userDetails.getFirstName());
        maildetails.setUserName(userDetails.getUserName());
        maildetails.setSchoolName(userDetails.getSchoolName());
        maildetails.setUserId(userid);
        maildetails = studentManagement.composeUpgradeToResearcherMessage(maildetails);
        SendEmail sendEmail = new SendEmail();
        sendEmail.sendmail(maildetails);
        courseDetailsDaoImpl.updateValidResearcherUpgrade(userid);
        ModelAndView mav2;
        errorDetails.setBackLink("instructorProfile");
        errorDetails.setBackText("Go back to Profile");
        errorDetails.setHeading("Account Request");
        errorDetails
                .setMessage("Account successfully requested. Please wait for confirmation email from administrator.");
        errorDetails.setTitle("Upgrade Request");
        mav2 = new ModelAndView("Course/InstructorErrors", "model", errorDetails);
        return mav2;
    }

    @RequestMapping(value = "/contactus", method = RequestMethod.POST)
    public ModelAndView contactus(HttpServletRequest request, HttpServletResponse response, HttpSession session,
            @ModelAttribute("maildetails") MailDetails maildetails) {
        ErrorDetails errorDetails = new ErrorDetails();
        StudentManagement studentManagement = new StudentManagement();

        maildetails = studentManagement.composecontactus(maildetails);
        SendEmail sendEmail = new SendEmail();
        sendEmail.sendmail(maildetails);
        ModelAndView mav2;
        errorDetails.setBackLink("login");
        errorDetails.setBackText("Home");
        errorDetails.setHeading("Email");
        errorDetails.setMessage("Email sent to the admin team successfully. You will get a response soon.");
        errorDetails.setTitle("Contact");
        mav2 = new ModelAndView("Course/InstructorErrors", "model", errorDetails);
        return mav2;
    }

    @RequestMapping(value = "/approveUpgradeRequest", method = RequestMethod.POST)
    public ModelAndView approveUpgradeRequest(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("userDetails") UserDetails userDetails) {

        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userid");
        ErrorDetails errorDetails = new ErrorDetails();
        int status = managementDaoImpl.approveResearcher(userDetails);
        status = managementDaoImpl.upgradeToResearcher(userDetails);
        ModelAndView mav2;
        errorDetails.setBackLink("ResearcherDashboard");
        errorDetails.setBackText("Go to Dashboard");
        errorDetails.setHeading("Account Approval");
        if (status != 1) {
            errorDetails.setMessage("Error occurred. Please make sure the username entered is correct.");
        } else {
            errorDetails.setMessage("Account successfully approved.");
        }
        errorDetails.setTitle("Approval");
        MailDetails maildetails = new MailDetails();
        StudentManagement studentManagement = new StudentManagement();
        maildetails.setEmail(managementDaoImpl.getEmail(userDetails).getEmail());
        maildetails = studentManagement.composeResearcherUpgradeApprovalMessage(maildetails);
        SendEmail sendEmail = new SendEmail();
        sendEmail.sendmail(maildetails);
        mav2 = new ModelAndView("Course/ResearcherErrors", "model", errorDetails);
        return mav2;
    }
}
