package com.in28minutes.learnspringframework;

import com.in28minutes.learnspringframework.game.GameRunner;
import com.in28minutes.learnspringframework.game.GamingConsole;
import com.in28minutes.learnspringframework.game.PacManGame;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App03GamingSpringBeans {

    public static void main(String[] args) {

        try (var context = new AnnotationConfigApplicationContext(GameConfiguration.class)) {
            context.getBean(GamingConsole.class).up();
    
            //러너빈을 가져오고 실행함
            context.getBean(GameRunner.class).run();
        }
    }
}
