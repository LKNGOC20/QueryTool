package com.ngoclam.querytool;

public class Launcher {
    public static void main(String[] args) {
        System.setProperty("javafx.preloader", "com.ngoclam.querytool.SplashScreen");
        Main_Application.main(args);
    }
}
