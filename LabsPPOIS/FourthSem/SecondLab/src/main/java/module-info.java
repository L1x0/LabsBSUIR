module by.astakhau.examresults {
    requires java.persistence;
    requires java.sql;
    requires org.hibernate.orm.core;
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;

    opens by.astakhau.examresults.model.entity to org.hibernate.orm.core;

    exports by.astakhau.examresults;
}