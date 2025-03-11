package by.astakhau.examresults;

import by.astakhau.examresults.model.entity.Exam;
import by.astakhau.examresults.model.entity.Student;
import by.astakhau.examresults.model.servise.StudentRepository;
import by.astakhau.examresults.util.JPAUtil;

import java.util.ArrayList;

//public class HelloApplication extends Application {
//    @Override
//    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
//        stage.setTitle("Hello!");
//        stage.setScene(scene);
//        stage.show();
//    }
class Main {

    public static void main(String[] args) {
//        launch();
//        Student student = new Student();
//        student.setFullName("Astakhau Artsiom");
//        student.setStudentsGroup("321701");
//        ArrayList<Exam> exams = new ArrayList<>();
//        Exam exam = new Exam();
//        exam.setScore(10);
//        exam.setSubjectName("Math");
//        exam.setStudent(student);
//        exams.add(exam);
//        student.setExams(exams);
//        StudentRepository sr = new StudentRepository();
//        sr.addStudent(student);
//        JPAUtil.close();

        StudentRepository studentRepository = new StudentRepository();
        Student student = new Student();
        ArrayList<Student> students = (ArrayList<Student>)studentRepository.getAllStudents();
        ArrayList<Exam> exams = new ArrayList<>(students.get(0).getExams());
        studentRepository.deleteStudent(students.get(0).getId());
        JPAUtil.close();

    }
}