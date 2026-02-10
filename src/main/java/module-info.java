module com.amitron.quoteapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.json;
    requires org.xerial.sqlitejdbc;

    opens com.amitron.quoteapp to javafx.fxml;
    opens com.amitron.quoteapp.controller to javafx.fxml;
    opens com.amitron.quoteapp.model to javafx.base;

    exports com.amitron.quoteapp;
}
