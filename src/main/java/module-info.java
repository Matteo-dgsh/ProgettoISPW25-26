module moscetta.dellagiovampaola {
    opens logic.graphiccontrollers;
    opens logic.controllers;
    opens logic.utils;
    opens logic.beans;
    opens logic.model;
    opens logic.view;

    requires java.base;
    requires java.net.http;
    requires java.desktop;
    requires java.logging;
    requires jdk.httpserver;
    requires transitive java.sql;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.web;
    requires transitive javafx.graphics;

    requires org.json;

    requires google.api.client;
    requires com.google.api.client;
    requires com.google.api.client.auth;
    requires com.google.api.client.extensions.java6.auth;
    requires com.google.api.client.extensions.jetty.auth;
    requires com.google.api.client.json.gson;
    requires com.google.api.client.json.jackson2;
    requires com.opencsv;

    exports logic.view;
    exports logic.graphiccontrollers;
    exports logic.controllers;
    exports logic.utils;
    exports logic.beans;
    exports logic.exceptions;
    exports logic.utils.enums;
    exports logic.model;

    opens logic.utils.enums;
    exports logic.controllers.factory;
    opens logic.controllers.factory;

}