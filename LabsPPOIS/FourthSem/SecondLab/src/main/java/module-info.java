module by.astakhau.examresults {
    requires java.persistence;
    requires java.sql;
    requires org.hibernate.orm.core;
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires javafaker;
    requires java.compiler;

    opens by.astakhau.examresults.model.entity to org.hibernate.orm.core;
    opens by.astakhau.examresults.controller to javafx.fxml;

    exports by.astakhau.examresults;
    exports by.astakhau.examresults.controller;
}