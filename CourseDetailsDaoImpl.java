package edu.smart.dao;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import edu.smart.controller.CourseDetailsController;
import edu.smart.model.CourseDetailsModel;
import edu.smart.model.InstructorModel;
import edu.smart.model.ProjectManagementModel;
import edu.smart.model.UserDetailsModel;
import edu.smart.pojo.CourseDetails;
import edu.smart.pojo.ProjectDetails;
import edu.smart.pojo.UserDetails;
import edu.smart.util.AppendUtil;
import edu.smart.model.StudentManagementModel;
import edu.smart.pojo.AssignmentDetails;
import edu.smart.pojo.ClassDetails;
import edu.smart.pojo.ClassStudentDetails;

public class CourseDetailsDaoImpl {
    static Logger log = Logger.getLogger(CourseDetailsController.class.getName());
    JdbcTemplate template;

    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }

    public int addStudentToClass(UserDetails studentDetails, int classId) {
        int status = 0;
        String sql = "INSERT INTO class_student (classid, studentid) VALUES (?,?);";
        status = template.update(sql, new Object[] { classId, studentDetails.getUserID() });

        return status;
    }

    public int addStudent(UserDetails studentDetails) {

        int status = 0;
        int userid = 0;
        StringBuilder sqlAppend = new StringBuilder();
        String usertype;

        String query1 = "select role,userid from members where username=?";
        SqlRowSet sqlRowset = template.queryForRowSet(query1, new Object[] { studentDetails.getUserName() });

        if (sqlRowset.next()) {
            userid = sqlRowset.getInt("userid");
            usertype = sqlRowset.getString("role");
        } else {
            status = 2;
            return status;
        }
        if (!usertype.equals("student")) {
            status = 4;
            return status;
        }
        log.info(status);
        String query3 = "select * from class_student where classid =? and studentid=?";
        SqlRowSet sqlRowset2 = template.queryForRowSet(query3, new Object[] { studentDetails.getClassId(), userid });

        if (sqlRowset2.next()) {
            status = 3;
            return status;
        } else {
            String query2 = "INSERT INTO class_student (classid,studentid) values (?,?)";
            status = template.update(query2, new Object[] { studentDetails.getClassId(), userid });

            log.info(status);
            log.info(query2);
        }
        return status;
    }

    public int removeStudentFromClass(int classId, int studentId) {
        int status = 0;
        String query = "DELETE FROM class_student where classid=? and studentid=?";
        status = template.update(query, new Object[] { classId, studentId });

        return status;
    }

    public AssignmentDetails getassignmentdetails(int assignid) {
        AssignmentDetails assigmentdetails = new AssignmentDetails();

        String sql = "SELECT * FROM assignment WHERE assgntid=?;";
        SqlRowSet sqlRowset = template.queryForRowSet(sql, new Object[] { assignid });

        if (sqlRowset.next()) {
            assigmentdetails.setAssgnmntId(assignid);
            assigmentdetails.setTitle(sqlRowset.getString("title"));
            assigmentdetails.setDirections(sqlRowset.getString("directions").replace("`", "'"));
            assigmentdetails.setDescription(sqlRowset.getString("description").replace("`", "'"));
            assigmentdetails.setStatus(sqlRowset.getString("as_status"));
            assigmentdetails.setType(sqlRowset.getString("type"));
        }
        return assigmentdetails;
    }

    public int removeClassFromTeacher(int teacherId, int classId) {
        int status = 0;

        String query = "UPDATE class set teacherid=null where classid=?";
        status = template.update(query, new Object[] { classId });

        return status;
    }

    public StudentManagementModel getStudents(int classId) {

        StudentManagementModel studentManagementModel = new StudentManagementModel();
        ArrayList<ClassStudentDetails> studentList = new ArrayList<ClassStudentDetails>();
        ArrayList<UserDetails> studentDetailsList = new ArrayList<UserDetails>();
        UserDetails studentDetails = new UserDetails();

        ClassStudentDetails classStudentDetails = new ClassStudentDetails();
        String query;
        query = "Select * from class_student where classid=?";
        SqlRowSet sqlRowset = template.queryForRowSet(query, new Object[] { classId });

        while (sqlRowset.next()) {
            classStudentDetails.setStudentId(sqlRowset.getInt("studentid"));
            studentList.add(classStudentDetails);
        }

        for (int i = 0; i < studentList.size(); i++) {
            studentDetails = getUserDetailsbyId(studentList.get(i).getStudentId());
            studentDetailsList.add(studentDetails);
        }

        studentManagementModel.setStudentList(studentList);
        studentManagementModel.setStudentDetailsList(studentDetailsList);
        return studentManagementModel;
    }

    public StudentManagementModel getAllStudentsOfTeacherByClass(int teacherId) {
        StudentManagementModel studentManagementModel = new StudentManagementModel();
        ArrayList<Integer> classIdList = new ArrayList<Integer>();
        String query = "";
        query = "Select classid from class where teacherid=? order by classid desc";
        SqlRowSet sqlRowset = template.queryForRowSet(query, new Object[] { teacherId });

        while (sqlRowset.next()) {
            classIdList.add(sqlRowset.getInt("classid"));
        }
        query = "";
        for (int i = 0; i < classIdList.size(); i++) {
            ClassDetails classDetails = new ClassDetails();
            ArrayList<Integer> studentIdList = new ArrayList<Integer>();
            ArrayList<UserDetails> studentDetailsList = new ArrayList<UserDetails>();

            classDetails = getClassDetailsbyId(classIdList.get(i));

            String query2 = "select * from class_student where classid=?";
            SqlRowSet sqlRowset2 = template.queryForRowSet(query2, new Object[] { classIdList.get(i) });

            while (sqlRowset2.next()) {
                studentIdList.add(sqlRowset2.getInt("studentid"));
            }
            for (int j = 0; j < studentIdList.size(); j++) {
                UserDetails studentDetails = new UserDetails();
                studentDetails = getUserDetailsbyId(studentIdList.get(j));
                studentDetails.setClassStudentId(
                        Integer.toString(classIdList.get(i)) + Integer.toString(studentDetails.getUserID()));
                studentDetailsList.add(studentDetails);
            }
            log.info("classId: " + classIdList.get(i));
            System.out.println("classId: " + classIdList.get(i));
            studentManagementModel.getClassStudentList().put(classDetails, studentDetailsList);
        }

        return studentManagementModel;
    }

    public StudentManagementModel getClasses(int teacherId) {

        StudentManagementModel studentManagementModel = new StudentManagementModel();
        ArrayList<ClassDetails> classList = new ArrayList<ClassDetails>();
        System.out.println("teacher ID: " + teacherId);
        String query;

        query = "Select * from class where teacherid=?";
        SqlRowSet sqlRowset = template.queryForRowSet(query, new Object[] { teacherId });

        while (sqlRowset.next()) {
            ClassDetails classDetails = new ClassDetails();
            classDetails.setClassId(sqlRowset.getInt("classid"));
            classDetails.setClassName(sqlRowset.getString("classname"));
            classDetails.setDescription(sqlRowset.getString("description"));
            classDetails.setTeacherId(teacherId);
            classList.add(classDetails);
        }

        studentManagementModel.setClassDetailsList(classList);
        return studentManagementModel;
    }

    public ClassDetails getClassDetailsbyId(int classId) {
        ClassDetails classDetails = new ClassDetails();
        String query = "";
        query = "Select * from class where classid=?";
        SqlRowSet sqlRowset2 = template.queryForRowSet(query, new Object[] { classId });

        while (sqlRowset2.next()) {
            classDetails.setClassId(sqlRowset2.getInt("classid"));
            classDetails.setClassName(sqlRowset2.getString("classname"));
            classDetails.setDescription(sqlRowset2.getString("description"));
        }
        return classDetails;

    }

    public UserDetails getUserDetailsbyId(int userId) {
        UserDetails userDetails = new UserDetails();
        String query = "";

        query = "Select * from members where userid=?";
        SqlRowSet sqlRowset2 = template.queryForRowSet(query, new Object[] { userId });

        while (sqlRowset2.next()) {
            userDetails.setUserID(sqlRowset2.getInt("userid"));
            userDetails.setAnswer(sqlRowset2.getString("answer"));
            userDetails.setEmail(sqlRowset2.getString("email"));
            userDetails.setFirstName(sqlRowset2.getString("firstname"));
            userDetails.setPassword(sqlRowset2.getString("password"));
            userDetails.setSchoolName(sqlRowset2.getString("schoolname"));
            userDetails.setUserName(sqlRowset2.getString("username"));
            userDetails.setUserType(sqlRowset2.getString("role"));
        }
        return userDetails;

    }

    public int saveUser(UserDetails userDetails) {

        StringBuilder sqlAppend = new StringBuilder();
        String username = userDetails.getUserName().toLowerCase();

        String query = "select * from members where username=?";
        SqlRowSet sqlRowset = template.queryForRowSet(query, new Object[] { userDetails.getUserName() });
        if (sqlRowset.next()) {
            return 3;
        }
        String sqlInsert = "INSERT INTO members (role, schoolname, firstname, username, password, email, answer) VALUES (?,?,?,?,?,?,?)";

        int status = template.update(sqlInsert,
                new Object[] { userDetails.getUserType(), userDetails.getSchoolName(), userDetails.getFirstName(),
                        userDetails.getUserName(), userDetails.getPassword(), userDetails.getEmail(),
                        userDetails.getAnswer() });

        if (status != 0 && userDetails.getUserType().equals("researcher")) {

            String query2 = "select userid from members where username=?";
            SqlRowSet sqlRowset2 = template.queryForRowSet(query2, new Object[] { userDetails.getUserName() });

            while (sqlRowset2.next()) {
                String query1 = "insert into validresearcher values (?,?)";
                status = template.update(query1, new Object[] { sqlRowset2.getInt("userid"), 0 });
            }
        }
        if (status != 0 && userDetails.getUserType().equals("teacher")) {

            String query2 = "select userid from members where username=?";
            SqlRowSet sqlRowset2 = template.queryForRowSet(query2, new Object[] { userDetails.getUserName() });
            while (sqlRowset2.next()) {
                String query1 = "insert into validresearcher values (?,?)";
                status = template.update(query1, new Object[] { sqlRowset2.getInt("userid"), 0 });
            }
        }
        if (status != 0) {
            String query2 = "select userid from members where username=?";
            SqlRowSet sqlRowset2 = template.queryForRowSet(query2, new Object[] { userDetails.getUserName() });
            while (sqlRowset2.next()) {
                String query1 = "insert into validuser values (?,?)";
                status = template.update(query1, new Object[] { sqlRowset2.getInt("userid"), 1 });
            }
        }
        return status;
    }

    public int updateValidResearcherUpgrade(int userid) {
        int status = 0;

        String query2 = "select valid from validresearcher where userid=?";
        SqlRowSet sqlRowset = template.queryForRowSet(query2, new Object[] { userid });
        if (sqlRowset.next()) {
            String query = "update validresearcher set valid=2 where userid=?";
            status = template.update(query, new Object[] { userid });
        } else {
            String query = "insert into validresearcher values (?,?)";
            status = template.update(query, new Object[] { userid, 2 });
        }
        return status;

    }

    public int save(CourseDetailsModel course, String model) {
        course.setSm_Subgraph(99999.0);

        String sqlAppend = "INSERT INTO expert_model (problemID, title, text, concepts,keyconcepts, relations, avgdegree, density, diameter, noofconcepts, meandistance, betweeness, noofrelations, pagerank, closenessCentrality, eigenvector, Clustering, subgroups) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        int flag1 = template.update(sqlAppend.toString(),
                new Object[] { course.getAssgntID(), course.getTitle(), course.getText().replace('\'', '~'),
                        AppendUtil.listToString(course.getAllConceptList()),
                        AppendUtil.listToString(course.getKeyConcepts()), course.getRelation(), course.getAvgdegree(),
                        course.getDensity(), course.getDiameter(), course.getNoOfConcepts(), course.getMeandistance(),
                        course.getBetweenness(), course.getNoOfRelations(), course.getPageRank(),
                        course.getClosenessCentrality(), course.getEigenVectorCentrality(), course.getClusteringcoef(),
                        course.getSubgraph() });

        SqlRowSet sqlRowset = template.queryForRowSet(
                "select max(expertmodelid) from expert_model where title=? and text=?",
                new Object[] { course.getTitle(), course.getText().replace('\'', '~') });
        if (sqlRowset.next()) {
            course.setExpertID(sqlRowset.getInt("max(expertmodelid)"));
        }
        String sqlAppend2 = "INSERT INTO expert_model2 (expertmodelid, problemID, pathlist, concepthighlightpairs, adjacencyMatrix) VALUES (?,?,?,?,?)";

        int flag = template.update(sqlAppend2,
                new Object[] { course.getExpertID(), course.getAssgntID(),
                        AppendUtil.listToString(course.getPathList()), course.getConcepthighlightpairs(),
                        course.getDBAdjacencyMatrix() });

        return course.getExpertID();
    }

    public int updatereference(CourseDetailsModel course) {
        String sqlAppend2 = "UPDATE expert_model SET title=?, text=?, concepts=?, keyconcepts=?, relations=?, avgdegree=?, density=?, diameter=?, noofconcepts=?, meandistance=?, noofrelations=?, subgroups=? WHERE expertmodelid=?;";
        template.update(sqlAppend2,
                new Object[] { course.getTitle(), course.getText().replace('\'', '~'),
                        AppendUtil.listToString(course.getAllConceptList()),
                        AppendUtil.listToString(course.getKeyConcepts()), course.getRelation(), course.getAvgdegree(),
                        course.getDensity(), course.getDiameter(), course.getNoOfConcepts(), course.getMeandistance(),
                        course.getNoOfRelations(), course.getSubgraph(), course.getExpertID() });

        String sqlAppend1 = "UPDATE expert_model2 SET pathlist=?,adjacencyMatrix=? WHERE expertmodelid=?";

        template.update(sqlAppend1, new Object[] { AppendUtil.listToString(course.getPathList()),
                course.getDBAdjacencyMatrix(), course.getExpertID() });

        return 1;
    }

    public int savestudentesponse(CourseDetailsModel course) {
        course.setSm_Subgraph(99999.0);

        String sqlAppend = "INSERT INTO student_model (text, concepts, keyconcepts, relations, avgdegree, density, "
                + "diameter, noofconcepts, meandistance, expertmodelid, betweeness, noofrelations, pagerank, closenessCentrality,"
                + "eigenvector, missingconcepts, subgroups, SM_balanced,SM_conceptual,SM_density, SM_diameter, SM_meandistance, SM_noofconcepts,"
                + "SM_noofrelations, SM_relational, SM_subgraphs, SM_avgdegree) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

        int flag1 = template.update(sqlAppend,
                new Object[] { course.getText().replace('\'', '~'), AppendUtil.listToString(course.getAllConceptList()),
                        AppendUtil.listToString(course.getKeyConcepts()), course.getRelation(), course.getAvgdegree(),
                        course.getDensity(), course.getDiameter(), course.getNoOfConcepts(), course.getMeandistance(),
                        course.getExpertID(), course.getBetweenness(), course.getNoOfRelations(), course.getPageRank(),
                        course.getClosenessCentrality(), course.getEigenVectorCentrality(),
                        AppendUtil.listToString(course.getMissingConcepts()), course.getSubgraph(),
                        course.getSm_Balancedmatching(), course.getSm_conceptualmatching(), course.getSm_density(),
                        course.getSm_diameter(), course.getSm_meandistance(), course.getSm_NOofconcepts(),
                        course.getSm_NoOfRelations(), course.getSm_propositionalmatching(), course.getSm_Subgraph(),
                        course.getSm_avgdegree() });

        if (flag1 != 0) {
            SqlRowSet sqlRowset = template.queryForRowSet("select max(modelID) from student_model where text=?",
                    new Object[] { course.getText().replace('\'', '~') });
            if (sqlRowset.next()) {
                course.setStudentresponseid(sqlRowset.getInt("max(modelID)"));
            }
        }

        String sqlAppend2 = "INSERT INTO student_model2 (modelID, pathlist, recallC, recallP, missinglinks, adjacencyMatrix) VALUES (?,?,?,?,?,?)";
        int flag = template.update(sqlAppend2,
                new Object[] { course.getStudentresponseid(), AppendUtil.listToString(course.getPathList()),
                        course.getRecallkeyconcepts(), course.getRecallKeylinks(),
                        AppendUtil.listToString(course.getMissingLinks()), course.getDBAdjacencyMatrix() });

        if (flag1 != 0 && flag != 0) {

            String query = "INSERT INTO student_model_mapping (studentid, modelid) values (?,?)";
            template.update(query, new Object[] { course.getStudentId(), course.getStudentresponseid() });

            course.setModel(Integer.toString(course.getStudentId()));
        }

        return course.getStudentresponseid();

    }

    public int addresponseentry(int studentid, int assignid, int modelid) {

        String query = "INSERT INTO studentresponses (studentid, assignid, studentmodelid) values (?,?,?)";
        return template.update(query, new Object[] { studentid, assignid, modelid });
    }

    public int saveconcepts(int id, CourseDetailsModel courseDetailsModel) {
        int status = 0;

        String sqlAppend2 = "UPDATE expert_model SET keyconcepts=?, keyconceptsynonyms=?, expertmodelid=?";
        template.update(sqlAppend2, new Object[] { AppendUtil.listToString(courseDetailsModel.getKeyConcepts()),
                AppendUtil.mapToString(courseDetailsModel.getKeyConceptSynonyms()), id });

        String query = "UPDATE expert_model2 SET pathlist=? WHERE expertmodelid=?;";
        status = template.update(query, new Object[] { AppendUtil.listToString(courseDetailsModel.getPathList()), id });

        return status;
    }

    public ArrayList<String> getKeyConcepts(int id) {

        String sqlAppend = "SELECT keyconcepts FROM expert_model WHERE expertmodelid=?;";
        SqlRowSet sqlRowset = template.queryForRowSet(sqlAppend, new Object[] { id });

        CourseDetailsModel result = new CourseDetailsModel();
        while (sqlRowset.next()) {
            result.setKeyConcepts(AppendUtil.stringToList(sqlRowset.getString("keyconcepts")));
        }
        return result.getKeyConcepts();
    }

    public CourseDetailsModel ExpertModelValues(int id) {
        String sqlAppend = "SELECT * FROM expert_model WHERE expertmodelid=?;";
        SqlRowSet sqlRowset = template.queryForRowSet(sqlAppend, new Object[] { id });
        CourseDetailsModel result = new CourseDetailsModel();
        while (sqlRowset.next()) {
            result.setTitle(sqlRowset.getString("title"));
            result.setText(sqlRowset.getString("text").replace("~", "\'"));
            result.setAvgdegree(sqlRowset.getDouble("avgdegree"));
            result.setDensity(sqlRowset.getDouble("density"));
            result.setDiameter(sqlRowset.getDouble("diameter"));
            result.setMeandistance(sqlRowset.getDouble("meandistance"));
            result.setNoOfConcepts(sqlRowset.getInt("noofconcepts"));
            result.setNoOfRelations(sqlRowset.getInt("noofrelations"));
            result.setSubgraph(sqlRowset.getDouble("subgroups"));
            result.setKeyConcepts(AppendUtil.stringToList(sqlRowset.getString("keyconcepts")));
            result.setAllConceptList(AppendUtil.stringToList(sqlRowset.getString("concepts")));
            result.setRelation(sqlRowset.getString("relations"));
            result.setPageRank(sqlRowset.getString("pagerank"));
            result.setBetweenness(sqlRowset.getString("betweeness"));
            result.setClusteringcoef(sqlRowset.getString("Clustering"));
            result.setClosenessCentrality(sqlRowset.getString("closenessCentrality"));
            result.setEigenVectorCentrality(sqlRowset.getString("eigenvector"));
            result.setKeyConceptSynonyms(AppendUtil.stringToMap(sqlRowset.getString("keyconceptsynonyms")));
        }
        String sqlAppend2 = "SELECT * FROM expert_model2 WHERE expertmodelid=?;";
        SqlRowSet sqlRowset2 = template.queryForRowSet(sqlAppend2, new Object[] { id });

        while (sqlRowset2.next()) {

            result.setPathList(AppendUtil.stringToList(sqlRowset2.getString("pathlist")));
            result.setConcepthighlightpairs(sqlRowset2.getString("concepthighlightpairs"));
            result.setDBAdjacencyMatrix(sqlRowset2.getString("adjacencyMatrix"));
        }

        if (result.getDBAdjacencyMatrix() != null) {
            ArrayList<ArrayList<String>> adjacencyList = new ArrayList<ArrayList<String>>();
            String[] adjacencyArray1 = result.getDBAdjacencyMatrix().split("/");
            int i, j, size = 0;

            for (i = 0; i < adjacencyArray1.length; i++) {
                ArrayList<String> adjacencyList1 = new ArrayList<String>();
                String[] adjacencyArray2 = adjacencyArray1[i].split(",");
                if (i == 0) {
                    size = adjacencyArray2.length;
                }
                for (j = 0; j < adjacencyArray2.length; j++) {
                    adjacencyList1.add(adjacencyArray2[j]);
                }
                adjacencyList.add(adjacencyList1);
            }

            size = size + 1;
            int l = 0;
            double[][] adjacencyMatrix = new double[size][size];
            if (!adjacencyList.isEmpty()) {
                for (i = 0; i < size; i++) {
                    for (j = 0; j < size; j++) {
                        if (i == j || i > j) {
                            adjacencyMatrix[i][j] = 0.0;
                        } else {
                            if (adjacencyList.get(i).get(l) != null && adjacencyList.get(i).get(l) != "null") {
                                adjacencyMatrix[i][j] = Double.parseDouble(adjacencyList.get(i).get(l));
                                l++;
                            }
                        }
                    }
                    l = 0;
                }
            }
            result.setAdjacencyMatrix(adjacencyMatrix);
        }
        int found = 0;
        ArrayList<String> bConcepts = new ArrayList<String>();
        for (int m = 0; m < result.getAllConceptList().size(); m++) {
            for (int n = 0; n < result.getKeyConcepts().size(); n++) {
                if (result.getKeyConcepts().get(n).equals(result.getAllConceptList().get(m))) {
                    found = 1;
                    break;
                }
            }
            if (found == 0) {
                bConcepts.add(result.getAllConceptList().get(m));
            }
            found = 0;
        }

        result.setbConcepts(bConcepts);

        return result;
    }

    public int update(CourseDetailsModel course) {
        return 0;
    }

    public CourseDetailsModel getCourseDetailsById(int id) {
        return null;
    }

    public UserDetails userlogin(UserDetails userDetails) {

        String username = userDetails.getUserName();
        String pwd = userDetails.getPassword();

        SqlRowSet sqlRowset = template.queryForRowSet("select * from members where username=? and password=?",
                new Object[] { username, pwd });
        if (sqlRowset.next()) {
            userDetails.setUserType(sqlRowset.getString("role"));
            userDetails.setFirstName(sqlRowset.getString("firstname"));
            userDetails.setUserID(sqlRowset.getInt("userid"));
            userDetails.setSchoolName(sqlRowset.getString("schoolname"));
            userDetails.setEmail(sqlRowset.getString("email"));
            SqlRowSet sqlRowset2 = template.queryForRowSet("select valid from validuser where userid=?",
                    new Object[] { userDetails.getUserID() });
            if (sqlRowset2.next()) {
                if (sqlRowset2.getInt("valid") == 0) {
                    userDetails.setLoginStatus("invalid");
                    return userDetails;
                }
            }
            userDetails.setLoginStatus("success");
            return userDetails;
        } else {
            userDetails.setLoginStatus("failed");

            return userDetails;
        }

    }

    public int editClass(ClassDetails classDetails) {

        String query = "Update class set classname=?,description=? where classid=?";
        int status = template.update(query,
                new Object[] { classDetails.getClassName(), classDetails.getDescription(), classDetails.getClassId() });

        return status;
    }

    public int addClass(ClassDetails classDetails) {
        int status = 0;

        String sqlAppend = "INSERT INTO class (classname, description, teacherid) VALUES (?,?,?)";

        status = template.update(sqlAppend, new Object[] { classDetails.getClassName(), classDetails.getDescription(),
                classDetails.getTeacherId() });

        SqlRowSet sqlRowset = template.queryForRowSet(
                "select * from class where classname=? AND description=? AND teacherid=?", new Object[] {
                        classDetails.getClassName(), classDetails.getDescription(), classDetails.getTeacherId() });

        if (sqlRowset.next()) {
            classDetails.setClassId(sqlRowset.getInt("classid"));
        }

        return status;
    }

    public ClassDetails removeclass(ClassDetails classDetails) {
        String sqlAppend = "delete from class where classname=? AND description=? AND teacherid=?";

        template.update(sqlAppend, new Object[] { classDetails.getClassName(), classDetails.getDescription(),
                classDetails.getTeacherId() });
        return classDetails;
    }

    public ArrayList<ClassDetails> getclases(int userid) {

        ClassDetails classdetails;
        ArrayList<ClassDetails> classes = new ArrayList<ClassDetails>();
        SqlRowSet sqlRowset = template.queryForRowSet("select * from class where teacherid=?", new Object[] { userid });

        while (sqlRowset.next()) {
            classdetails = new ClassDetails();
            classdetails.setClassId(sqlRowset.getInt("classid"));
            classdetails.setClassName(sqlRowset.getString("classname"));
            classdetails.setDescription(sqlRowset.getString("description"));
            classes.add(classdetails);
        }
        return classes;
    }

    public int getstudentcountbyclassid(int classid) {
        int studentcount = 0;
        String query2 = "select * from class_student where classid=?";
        SqlRowSet sqlRowset = template.queryForRowSet(query2, new Object[] { classid });
        while (sqlRowset.next()) {
            studentcount++;
        }

        return studentcount;
    }

    public int countStudentResponsesinClass(int classid, int assignmentid) {
        int studentresponses = 0;
        String query2 = "select distinct studentid,assignid from studentresponses where assignid=? and studentid "
                + "in (select studentid from class_student where classid=?)";
        SqlRowSet sqlRowset = template.queryForRowSet(query2, new Object[] { assignmentid, classid });
        while (sqlRowset.next()) {
            studentresponses++;
        }
        return studentresponses;
    }

    public ArrayList<ClassDetails> getclassofassignment(int assignmentid) {
        ClassDetails classdetails;
        ArrayList<ClassDetails> classes = new ArrayList<ClassDetails>();
        SqlRowSet sqlRowset = template.queryForRowSet(
                "select * from class where classid IN (SELECT classid FROM classassignments where assgntid=?)",
                new Object[] { assignmentid });
        while (sqlRowset.next()) {
            classdetails = new ClassDetails();
            classdetails.setClassId(sqlRowset.getInt("classid"));
            classdetails.setStudentcount(getstudentcountbyclassid(classdetails.getClassId()));
            classdetails.setClassName(sqlRowset.getString("classname"));
            classdetails.setDescription(sqlRowset.getString("description"));
            classdetails.setStudentresponses(countStudentResponsesinClass(classdetails.getClassId(), assignmentid));

            classes.add(classdetails);
        }
        return classes;
    }

    public void updateassignment(AssignmentDetails assign) {

        template.update(
                "UPDATE assignment  SET title=?, directions=?, description=?, as_status=?, type=? WHERE assgntid=?",
                new Object[] { assign.getTitle(), assign.getDirections(), assign.getDescription(), assign.getStatus(),
                        assign.getType(), assign.getAssgnmntId() });
        int choosenmodel = assign.getChoosenmodelID();
        if (choosenmodel != 0)
            template.update("UPDATE choosenexpert SET choosenmodelid=? WHERE assignmentid=?",
                    new Object[] { assign.getChoosenmodelID(), assign.getAssgnmntId() });
    }

    public void choosemodel(CourseDetails CD) {
        template.update("UPDATE choosenexpert SET choosenmodelid=? WHERE assignmentid=?",
                new Object[] { CD.getModelID(), CD.getAssignmentID() });
    }

    public void deletereferencemodel(int modelid) {
        template.update("DELETE FROM expert_model2 where expertmodelid=?", new Object[] { modelid });
        template.update("DELETE FROM expert_model where expertmodelid=?", new Object[] { modelid });

    }

    public void deleteassignment(int assignmentid) {

        template.update("DELETE FROM classassignments where assgntid=?", new Object[] { assignmentid });
        template.update("DELETE FROM choosenexpert where assignmentid=?", new Object[] { assignmentid });
        template.update("DELETE FROM studentresponses where assignid=?", new Object[] { assignmentid });
        template.update("DELETE FROM expert_model2 where problemID=?", new Object[] { assignmentid });
        template.update("DELETE FROM expert_model where problemID=?", new Object[] { assignmentid });

        SqlRowSet sqlRowset = template.queryForRowSet("select * from studentresponses where assignid=?",
                new Object[] { assignmentid });

        while (sqlRowset.next()) {
            template.update("DELETE FROM student_model2 where modelID=?", new Object[] { modelid });
            template.update("DELETE FROM student_model where modelID=?", new Object[] { modelid });
        }

        template.update("DELETE FROM assignment where assgntid=?", new Object[] { assignmentid });
    }

    public void addclasstoassgn(int assignmentid, int classid) {
        String sqlAppend = "INSERT into classassignments (assgntid,classid) VALUES (?,?)";
        template.update(sqlAppend, new Object[] { assignmentid, classid });
    }

    public ArrayList<ProjectDetails> getprojects(int userid) {
        ProjectDetails projectdetails;
        ArrayList<ProjectDetails> projects = new ArrayList<ProjectDetails>();
        SqlRowSet sqlRowset = template.queryForRowSet(
                "select * from project where teacherid=? order by createdDate desc", new Object[] { userid });
        while (sqlRowset.next()) {
            projectdetails = new ProjectDetails();
            projectdetails.setProjectId(sqlRowset.getInt("projectid"));
            projectdetails.setProjectName(sqlRowset.getString("projectname"));
            projectdetails.setDescription(sqlRowset.getString("desciption"));
            projectdetails.setSubject(sqlRowset.getString("sub"));
            projectdetails.setDateCreated(sqlRowset.getString("createdDate").split(" ")[0]);
            projectdetails.setImagepath(sqlRowset.getString("imgpath"));

            projects.add(projectdetails);
        }
        return projects;
    }

    public ProjectDetails addproject(ProjectDetails projectDetails) {
        String sqlAppend = "INSERT INTO project (projectname, desciption, sub, imgpath, teacherid) VALUES (?,?,?,?,?)";
        template.update(sqlAppend,
                new Object[] { projectDetails.getProjectName(), projectDetails.getDescription(),
                        projectDetails.getSubject(), projectDetails.getImagepath().replace("\\", "/"),
                        projectDetails.getTeacherId() });

        SqlRowSet sqlRowset = template.queryForRowSet(
                "select * from project where projectname=? AND desciption=? AND sub=? AND teacherid=?",
                new Object[] { projectDetails.getProjectName(), projectDetails.getDescription(),
                        projectDetails.getSubject(), projectDetails.getTeacherId() });
        if (sqlRowset.next()) {

            projectDetails.setProjectId(sqlRowset.getInt("projectid"));
        }

        return projectDetails;
    }

    public void editproject(ProjectDetails projectDetails) {
        if (projectDetails.getImagepath() == null)
            template.update("UPDATE project  SET projectname=?, desciption=?, sub=? WHERE projectid=?",
                    new Object[] { projectDetails.getProjectName(), projectDetails.getDescription(),
                            projectDetails.getSubject(), projectDetails.getProjectId() });

        else
            template.update("UPDATE project  SET projectname=?, desciption=?, sub=?, imgpath=?  WHERE projectid=?",
                    new Object[] { projectDetails.getProjectName(), projectDetails.getDescription(),
                            projectDetails.getSubject(), projectDetails.getImagepath(),
                            projectDetails.getProjectId() });

    }

    public void removeproject(Integer[] ids) {
        for (int i = 0; i < ids.length; i++) {
            SqlRowSet sqlRowset = template.queryForRowSet("select * from assignment where projectid=?",
                    new Object[] { ids[i] });
            while (sqlRowset.next()) {
                int assignid = sqlRowset.getInt("assgntid");
                deleteassignment(assignid);
            }
            template.update("DELETE FROM project where projectid=?", new Object[] { ids[i] });

        }

    }

    public ArrayList<AssignmentDetails> getassignments(int userid) {
        AssignmentDetails assign;
        ArrayList<AssignmentDetails> assignments = new ArrayList<AssignmentDetails>();
        SqlRowSet sqlRowset = template.queryForRowSet("SELECT assignment.* FROM project\r\n" + "INNER JOIN\r\n"
                + "assignment\r\n" + "ON project.projectid=assignment.projectid\r\n" + "WHERE project.teacherid=?"
                + " order by createdDate desc", new Object[] { userid });
        while (sqlRowset.next()) {
            assign = new AssignmentDetails();
            assign.setAssgnmntId(sqlRowset.getInt("assgntid"));
            assign.setTitle(sqlRowset.getString("title"));
            assign.setDateCreated(sqlRowset.getString("createdDate").split(" ")[0]);
            assign.setDescription(sqlRowset.getString("description").replace("`", "'"));
            assign.setDirections(sqlRowset.getString("directions").replace("`", "'"));
            assign.setProjectId(sqlRowset.getInt("projectid"));
            assign.setStatus(sqlRowset.getString("as_status"));

            assignments.add(assign);
        }
        return assignments;
    }

    public AssignmentDetails addassignment(AssignmentDetails assign) {
        String sqlAppend = "INSERT INTO assignment (projectid, title, description,directions, as_status) VALUES (?,?,?,?,?)";

        template.update(sqlAppend,
                new Object[] { assign.getProjectId(), assign.getTitle(), assign.getDescription().replace("'", "`"),
                        assign.getDirections().replace("'", "`"), assign.getStatus() });
        SqlRowSet sqlRowset = template.queryForRowSet(
                "select * from assignment where projectid=? AND title=? AND description=? AND as_status=?",
                new Object[] { assign.getProjectId(), assign.getTitle(), assign.getDescription().replace("'", "`"),
                        assign.getStatus().replace("'", "`") });
        if (sqlRowset.next()) {
            assign.setAssgnmntId(sqlRowset.getInt("assgntid"));
        }
        template.update("INSERT INTO choosenexpert(assignmentid, choosenmodelid) VALUES (?,0)",
                new Object[] { assign.getAssgnmntId() });
        return assign;
    }

    public int editProfile(UserDetails userDetails) {
        String query = "Update members set schoolname=? ,firstname=? ,email=? ,username=?, answer=? where userid=?";
        int status = template.update(query, new Object[] { userDetails.getSchoolName(), userDetails.getFirstName(),
                userDetails.getEmail(), userDetails.getUserName(), userDetails.getAnswer(), userDetails.getUserID() });

        return status;
    }

    public ArrayList<CourseDetails> getallexpertmodels(int assignmentid) {
        CourseDetails coursedetails;
        ArrayList<CourseDetails> models = new ArrayList<CourseDetails>();
        SqlRowSet sqlRowset = template.queryForRowSet("select * from expert_model where problemID=?",
                new Object[] { assignmentid });
        while (sqlRowset.next()) {
            coursedetails = new CourseDetails();
            coursedetails.setModelID(sqlRowset.getInt("expertmodelid"));
            coursedetails.setTitle(sqlRowset.getString("title"));
            SqlRowSet sqlRowset2 = template.queryForRowSet("select * from expert_model2 where expertmodelid=?",
                    new Object[] { coursedetails.getModelID() });
            if (sqlRowset2.next())
                coursedetails.setCreatedon(sqlRowset2.getString("createddate").split(" ")[0]);

            models.add(coursedetails);
        }
        return models;
    }

    public int getchoosenexpertmodelid(int assignmentid) {
        SqlRowSet sqlRowset = template.queryForRowSet("select * from choosenexpert where assignmentid=?",
                new Object[] { assignmentid });
        if (sqlRowset.next()) {
            return sqlRowset.getInt("choosenmodelid");
        } else
            return 0;
    }

    public int removeclassfromassignment(int assignid, int classid) {

        template.update("DELETE FROM classassignments where assgntid=? AND classid=?",
                new Object[] { assignid, classid });

        return 0;
    }

    public CourseDetailsModel getstudentmodel(int studentresponseid) {
        CourseDetailsModel courseDetailsModel = new CourseDetailsModel();
        String query1 = "SELECT * FROM student_model WHERE modelID=?";
        String query2 = "SELECT * FROM student_model2 WHERE modelID=?";
        SqlRowSet sqlRowset = template.queryForRowSet(query1, new Object[] { studentresponseid });
        SqlRowSet sqlRowset2 = template.queryForRowSet(query2, new Object[] { studentresponseid });

        if (sqlRowset.next()) {
            courseDetailsModel.setExpertID(sqlRowset.getInt("expertmodelid"));
            courseDetailsModel.setText(sqlRowset.getString("text").replace("~", "\'"));
            courseDetailsModel.setRelation(sqlRowset.getString("relations"));
            courseDetailsModel.setAllConceptList(AppendUtil.stringToList(sqlRowset.getString("concepts")));
            courseDetailsModel.setKeyConcepts(AppendUtil.stringToList(sqlRowset.getString("keyconcepts")));
            courseDetailsModel.setMissingConcepts(AppendUtil.stringToList(sqlRowset.getString("missingconcepts")));
            courseDetailsModel.setNoOfRelations(sqlRowset.getInt("noofrelations"));
            courseDetailsModel.setNoOfConcepts(sqlRowset.getInt("noofconcepts"));
            courseDetailsModel.setAvgdegree(sqlRowset.getDouble("avgdegree"));
            courseDetailsModel.setDiameter(sqlRowset.getDouble("diameter"));
            courseDetailsModel.setDensity(sqlRowset.getDouble("density"));
            courseDetailsModel.setMeandistance(sqlRowset.getDouble("meandistance"));
            courseDetailsModel.setSm_avgdegree(sqlRowset.getDouble("SM_avgdegree"));
            courseDetailsModel.setSm_Balancedmatching(sqlRowset.getDouble("SM_balanced"));
            courseDetailsModel.setSm_conceptualmatching(sqlRowset.getDouble("SM_conceptual"));
            courseDetailsModel.setSm_density(sqlRowset.getDouble("SM_density"));
            courseDetailsModel.setSm_diameter(sqlRowset.getDouble("SM_diameter"));
            courseDetailsModel.setSm_meandistance(sqlRowset.getDouble("SM_meandistance"));
            courseDetailsModel.setSm_NOofconcepts(sqlRowset.getDouble("SM_noofconcepts"));
            courseDetailsModel.setSm_NoOfRelations(sqlRowset.getDouble("SM_noofrelations"));
            courseDetailsModel.setSm_propositionalmatching(sqlRowset.getDouble("SM_relational"));

        }
        if (sqlRowset2.next()) {
            courseDetailsModel.setDBAdjacencyMatrix(sqlRowset2.getString("adjacencyMatrix"));
            courseDetailsModel.setPathList(AppendUtil.stringToList(sqlRowset2.getString("pathlist")));
            courseDetailsModel.setRecallkeyconcepts(Double.parseDouble(sqlRowset2.getString("recallC")));
            courseDetailsModel.setRecallKeylinks(Double.parseDouble(sqlRowset2.getString("recallP")));
            courseDetailsModel.setMissingLinks(AppendUtil.stringToList(sqlRowset2.getString("missinglinks")));
        }

        return courseDetailsModel;
    }

    public CourseDetailsModel getStudentModelDetails(int studentresponseid, CourseDetailsModel courseDetailsModel) {
        // TODO Auto-generated method stub
        String query1 = "SELECT * FROM student_model WHERE modelID=?";
        String query2 = "SELECT * FROM student_model2 WHERE modelID=?";
        SqlRowSet sqlRowset = template.queryForRowSet(query1, new Object[] { studentresponseid });
        SqlRowSet sqlRowset2 = template.queryForRowSet(query2, new Object[] { studentresponseid });
        System.out.println();
        if (sqlRowset.next()) {
            courseDetailsModel.setExpertID(sqlRowset.getInt("expertmodelid"));
            courseDetailsModel.setText(sqlRowset.getString("text").replace("~", "\'"));
            courseDetailsModel.setAllConceptList(AppendUtil.stringToList(sqlRowset.getString("concepts")));
            courseDetailsModel.setKeyConcepts(AppendUtil.stringToList(sqlRowset.getString("keyconcepts")));
            courseDetailsModel.setMissingConcepts(AppendUtil.stringToList(sqlRowset.getString("missingconcepts")));
            courseDetailsModel.setNoOfRelations(sqlRowset.getInt("noofrelations"));
            courseDetailsModel.setNoOfConcepts(sqlRowset.getInt("noofconcepts"));
        }
        System.out
                .println("CourseDetailsDaoImpl.java getStudentModelDetails method " + courseDetailsModel.getExpertID());
        CourseDetailsModel expertParameters = ExpertModelValues(courseDetailsModel.getExpertID());
        courseDetailsModel.setExpert(expertParameters);
        if (sqlRowset2.next()) {
            courseDetailsModel.setDBAdjacencyMatrix(sqlRowset2.getString("adjacencyMatrix"));
            courseDetailsModel.setPathList(AppendUtil.stringToList(sqlRowset2.getString("pathlist")));
            courseDetailsModel.setRecallkeyconcepts(Double.parseDouble(sqlRowset2.getString("recallC")));
            courseDetailsModel.setRecallKeylinks(Double.parseDouble(sqlRowset2.getString("recallP")));
            courseDetailsModel.setMissingLinks(AppendUtil.stringToList(sqlRowset2.getString("missinglinks")));
        }
        if (courseDetailsModel.getDBAdjacencyMatrix() != null
                && !courseDetailsModel.getDBAdjacencyMatrix().equals("null")) {
            ArrayList<ArrayList<String>> adjacencyList = new ArrayList<ArrayList<String>>();
            String[] adjacencyArray1 = courseDetailsModel.getDBAdjacencyMatrix().split("/");
            int i, j, size = 0;

            for (i = 0; i < adjacencyArray1.length; i++) {
                ArrayList<String> adjacencyList1 = new ArrayList<String>();
                String[] adjacencyArray2 = adjacencyArray1[i].split(",");
                if (i == 0) {
                    size = adjacencyArray2.length;
                }
                for (j = 0; j < adjacencyArray2.length; j++) {
                    adjacencyList1.add(adjacencyArray2[j]);
                }
                adjacencyList.add(adjacencyList1);
            }

            size = size + 1;
            int l = 0;
            double[][] adjacencyMatrix = new double[size][size];
            if (!adjacencyList.isEmpty()) {
                for (i = 0; i < size; i++) {
                    for (j = 0; j < size; j++) {
                        if (i == j || i > j) {
                            adjacencyMatrix[i][j] = 0.0;
                        } else {
                            if (adjacencyList.get(i).get(l) != null && adjacencyList.get(i).get(l) != "null") {
                                adjacencyMatrix[i][j] = Double.parseDouble(adjacencyList.get(i).get(l));
                                l++;
                            }
                        }
                    }
                    l = 0;
                }
            }
            courseDetailsModel.setAdjacencyMatrix(adjacencyMatrix);
        }
        return courseDetailsModel;
    }

    public int checkValidResearcher(UserDetails userDetails) {
        String username = userDetails.getUserName();
        String pwd = userDetails.getPassword();
        int status = -1;

        SqlRowSet sqlRowset = template.queryForRowSet(
                "select valid from validresearcher where userid in (select userid from members where username=?)",
                new Object[] { username });

        if (sqlRowset.next()) {
            status = sqlRowset.getInt("valid");
        }
        return status;

    }
}