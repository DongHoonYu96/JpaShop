package com.in28minutes.learnspringframework.game;

public class PacManGame implements GamingConsole{

    public void up(){
        System.out.println("pacman up");
    }

    public void down(){
        System.out.println("pacman d");
    }

    public void left(){
        System.out.println("pacman ll");
    }

    public void right(){
        System.out.println("pacman rr");
    }
}
