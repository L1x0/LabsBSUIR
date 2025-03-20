package by.astakhau.examresults;

import by.astakhau.examresults.model.entity.Exam;
import by.astakhau.examresults.model.entity.Student;
import by.astakhau.examresults.model.service.StudentRepository;
import by.astakhau.examresults.util.JPAUtil;
import com.github.javafaker.Faker;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dataManager.fxml"));
        Parent root = loader.load();

        stage.setTitle("Student Manager");
        stage.setScene(new Scene(root, 1230, 400));
        stage.show();
    }


    public static void main(String[] args) {
        launch();
//        Faker faker = new Faker(new Locale("ru"));
//        Random random = new Random();
//        StudentRepository studentRepository = new StudentRepository();
//        for (int i = 0; i < 200; i++) {
//
//            Student student = new Student();
//            student.setStudentsGroup(String.valueOf(random.nextInt(10)));
//            student.setFullName(faker.name().fullName());
//
//            for (int j = 0; j < random.nextInt(8); j++) {
//                Exam exam = new Exam();
//                exam.setSubjectName(String.valueOf(random.nextInt(5)));
//                exam.setScore(random.nextInt(10));
//                student.getExams().add(exam);
//                exam.setStudent(student);
//            }
//            studentRepository.addStudent(student);
//        }
//
//
//        JPAUtil.close();
//        StudentRepository sr = new StudentRepository();
//        sr.findByScoreAndSubject(9, 10, "3").forEach(e -> System.out.println(e.getFullName()));
//        System.out.println(sr.deleteByScoreAndSubject(9, 10, "4"));
//        System.out.println("_____________");
//        sr.findByScoreAndSubject(9, 10, "3").forEach(e -> System.out.println(e.getFullName()));
    }
}