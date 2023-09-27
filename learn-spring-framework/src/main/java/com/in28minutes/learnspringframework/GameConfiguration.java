package com.in28minutes.learnspringframework;

import com.in28minutes.learnspringframework.game.GameRunner;
import com.in28minutes.learnspringframework.game.PacManGame;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.in28minutes.learnspringframework.game.GamingConsole;

@Configuration
public class GameConfiguration {

    @Bean
    public GamingConsole game(){
        var game=new PacManGame();
        return game;
    }

    @Bean                        //위의 빈을 연결해줌!!
    public GameRunner gameRunner(GamingConsole game){
        var gameRunner=new GameRunner(game);
        return gameRunner;
    }
}
