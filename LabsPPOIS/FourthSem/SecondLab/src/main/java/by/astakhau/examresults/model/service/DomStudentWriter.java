package by.astakhau.examresults.model.service;

import by.astakhau.examresults.model.entity.Exam;
import by.astakhau.examresults.model.entity.Student;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class DomStudentWriter {

    /**
     * Записывает список студентов в XML-файл, перезаписывая исходный файл.
     * @param students Список студентов для записи.
     * @param sourceFile Исходный файл, который будет перезаписан.
     * @throws Exception Если произошла ошибка при записи.
     */
    public void writeStudentsToSourceFile(List<Student> students, File sourceFile) throws Exception {
        // Проверка существования файла
        if (!sourceFile.exists()) {
            throw new IllegalArgumentException("Файл не существует: " + sourceFile.getPath());
        }

        // Создаем временный файл для безопасной записи
        File tempFile = File.createTempFile("temp_", ".xml", sourceFile.getParentFile());

        try {
            // Создаем DOM-документ
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            // Корневой элемент
            Element rootElement = doc.createElement("students");
            doc.appendChild(rootElement);

            // Заполняем структуру
            for (Student student : students) {
                Element studentElement = createStudentElement(doc, student);
                rootElement.appendChild(studentElement);
            }

            // Записываем во временный файл
            writeDocumentToFile(doc, tempFile);

            // Заменяем исходный файл временным
            if (!sourceFile.delete() || !tempFile.renameTo(sourceFile)) {
                throw new IOException("Не удалось перезаписать исходный файл");
            }

        } finally {
            // Удаляем временный файл в случае ошибки
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    private Element createStudentElement(Document doc, Student student) {
        Element studentElement = doc.createElement("student");

        Element fullName = doc.createElement("fullName");
        fullName.setTextContent(student.getFullName());
        studentElement.appendChild(fullName);

        Element group = doc.createElement("studentsGroup");
        group.setTextContent(student.getStudentsGroup());
        studentElement.appendChild(group);

        Element exams = doc.createElement("exams");
        for (Exam exam : student.getExams()) {
            Element examElement = createExamElement(doc, exam);
            exams.appendChild(examElement);
        }
        studentElement.appendChild(exams);

        return studentElement;
    }

    private Element createExamElement(Document doc, Exam exam) {
        Element examElement = doc.createElement("exam");

        Element subject = doc.createElement("subjectName");
        subject.setTextContent(exam.getSubjectName());
        examElement.appendChild(subject);

        Element score = doc.createElement("score");
        score.setTextContent(String.valueOf(exam.getScore()));
        examElement.appendChild(score);

        return examElement;
    }

    private void writeDocumentToFile(Document doc, File file) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }
}