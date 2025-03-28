package by.astakhau.examresults.model.service;

import by.astakhau.examresults.model.entity.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;

public class StudentRepository {
    public void addStudent(Student student) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(student);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void addAllStudent(List<Student> students) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            for (Student s : students) {
                em.persist(s);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public List<Student> getAllStudents() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Student> query = em.createQuery("SELECT s FROM Student s", Student.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public void updateStudent(Student student) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(student);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void deleteStudent(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Student student = em.find(Student.class, id);
            if (student != null) {
                em.remove(student);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

    }

    public ObservableList<Student> findByAverageScoreAndSubject(double lower, double upper, String subject) {
        EntityManager em = JPAUtil.getEntityManager();
        ObservableList<Student> result = FXCollections.observableArrayList(em.createQuery(
                        "SELECT s FROM Student s WHERE " +
                                "(SELECT AVG(e.score) FROM s.exams e WHERE e.subjectName = :subject) " +
                                "BETWEEN :lower AND :upper", Student.class)
                .setParameter("lower", lower)
                .setParameter("upper", upper)
                .setParameter("subject", subject)
                .getResultList());

        return result;
    }

    public List<Student> findByStudentsGroup(String group) {
        EntityManager em = JPAUtil.getEntityManager();
        return em.createQuery(
                        "SELECT s FROM Student s WHERE s.studentsGroup = :group", Student.class)
                .setParameter("group", group)
                .getResultList();
    }

    public ObservableList<Student> findByScoreAndSubject(double lower, double upper, String subject) {
        EntityManager em = JPAUtil.getEntityManager();
        ObservableList<Student> result = FXCollections.observableArrayList(em.createQuery(
                        "SELECT DISTINCT s FROM Student s JOIN s.exams e " +
                                "WHERE e.subjectName = :subject AND e.score BETWEEN :lower AND :upper", Student.class)
                .setParameter("lower", lower)
                .setParameter("upper", upper)
                .setParameter("subject", subject)
                .getResultList());
        return result;
    }

    public List<String> findAllGroups() {
        EntityManager em = JPAUtil.getEntityManager();
        return em.createQuery(
                        "SELECT DISTINCT s.studentsGroup FROM Student s", String.class)
                .getResultList();
    }

    public List<String> findSubjectsByGroup(String group) {
        EntityManager em = JPAUtil.getEntityManager();
        return em.createQuery(
                        "SELECT DISTINCT e.subjectName FROM Exam e " +
                                "WHERE e.student.studentsGroup = :group", String.class)
                .setParameter("group", group)
                .getResultList();
    }

    public int deleteByAverageScoreAndSubject(double lower, double upper, String subject) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            int deletedCount = em.createQuery(
                            "DELETE FROM Student s WHERE " +
                                    "(SELECT AVG(e.score) FROM s.exams e WHERE e.subjectName = :subject) " +
                                    "BETWEEN :lower AND :upper")
                    .setParameter("lower", lower)
                    .setParameter("upper", upper)
                    .setParameter("subject", subject)
                    .executeUpdate();
            transaction.commit();
            return deletedCount;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Delete operation failed", e);
        }
    }

    public int deleteByGroup(String group) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            int deletedCount = em.createQuery("DELETE FROM Student s WHERE s.studentsGroup = :group")
                    .setParameter("group", group)
                    .executeUpdate();
            transaction.commit();
            return deletedCount;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Delete operation failed", e);
        }
    }

    public int deleteByScoreAndSubject(int lower, int upper, String subject) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            int deletedCount = em.createQuery(
                            "DELETE FROM Student s WHERE s IN (" +
                                    "SELECT DISTINCT st FROM Student st JOIN st.exams e " +
                                    "WHERE e.subjectName = :subject AND e.score BETWEEN :lower AND :upper)")
                    .setParameter("lower", lower)
                    .setParameter("upper", upper)
                    .setParameter("subject", subject)
                    .executeUpdate();
            transaction.commit();
            return deletedCount;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Delete operation failed", e);
        }
    }
}
