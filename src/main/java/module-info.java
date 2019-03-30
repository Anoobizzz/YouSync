module YouSync {
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.media;
    requires javafx.web;
    requires java.ws.rs;
    requires java.sql;

    requires YouGet;
    requires spring.core;
    requires spring.context;
    requires spring.beans;
    requires spring.expression;
    requires org.apache.commons.lang3;
    requires slf4j.api;
    requires mp3agic;
    requires jersey.client;
    requires jersey.common;
    requires jersey.media.json.jackson;
    requires com.fasterxml.jackson.annotation;

    exports yousync to javafx.graphics;
    exports yousync.domain.youtube to com.fasterxml.jackson.databind;
    opens yousync.domain to javafx.base;
    opens yousync.domain.youtube to com.fasterxml.jackson.databind;
    opens yousync.config to spring.core, spring.beans, spring.context;
    opens yousync.ui to spring.beans, spring.core, javafx.fxml;
    opens yousync.service to spring.beans, spring.core;
    opens yousync.sources to spring.beans, spring.core;
}