package com.nahman.flappybird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.Random;


public class GameManager {

    enum GameState{
        GAME_OPENED,
       GAME_NOT_STARTED,
        GAME_STARTED,
        GAME_FINISHED
    }

    Texture background;
    Texture[] birds;
    Texture topTube;
    Texture replay;
    Texture bottomTube;
    Texture gameover;
    Texture flappyBird;
    Texture medal;
    Texture play;
    BitmapFont font;
    BitmapFont gameOverFont;

    Preferences prefs;


    int score;
    int scoringTube;
    int flapState;
    float birdY;
    float birdVelocity;
    GameState gameState;
    float gravity;
    int getNumberOfTubesForVelocityUp;
    int tubesCounter;
    int timerBirdInt;
    int screenHeight;
    int screenWidth;
    float tubeVelocity;
    int numberOfTubes;
    float gap;
    int flagScoreUpdate;

    Circle birdCircle;
    Rectangle[] topTubesRectangles;
    Rectangle[] bottomTubesRectangles;

    Random randomGenerator;

    float[] tubesXArray;
    float[] tubeOfSet;
    float distanceBetweenTubes;

    public void resetGameManager(){
        game = null;
    }


    private GameManager() {
        prefs = Gdx.app.getPreferences("MyPreferences");
        screenHeight = Gdx.graphics.getHeight();
        screenWidth = Gdx.graphics.getWidth();
        flapState = 0;
        gameState = GameState.GAME_OPENED;
        getNumberOfTubesForVelocityUp = 8;
        timerBirdInt = 0;
        tubeVelocity = 4;
        numberOfTubes = 4;
        tubeOfSet = new float[numberOfTubes];
        tubesXArray = new float[numberOfTubes];

        birdCircle = new Circle();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().scale(10);
        gameOverFont = new BitmapFont();
        gameOverFont.setColor(Color.WHITE);
        gameOverFont.getData().scale(5);
        // shapeRenderer = new ShapeRenderer();
        background = new Texture("bg.png");
        gameover = new Texture("gameover.png");
        replay = new Texture("ok.png");
        birds = new Texture[]{new Texture("bird.png"), new Texture("bird2.png")};
        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");
        play = new Texture("play.png");
        flappyBird = new Texture("flappybird.png");
        medal = new Texture("medal1.png");


        topTubesRectangles = new Rectangle[numberOfTubes];
        bottomTubesRectangles = new Rectangle[numberOfTubes];
        gap = Float.valueOf(birds[flapState].getHeight() * 4);
        randomGenerator = new Random();
        distanceBetweenTubes = screenWidth * 5/6;

        setupGame();

    }

    private void setupGame() {
        flagScoreUpdate = 0;
        gravity = 2;
        birdVelocity = 0;
        score = 0;
        timerBirdInt = 0;
        tubesCounter = 0;
        scoringTube = 0;
        birdY = screenHeight / 2 - birds[0].getHeight() / 2;

        for (int i = 0; i < numberOfTubes; i++) {
            tubeOfSet[i] = (randomGenerator.nextFloat() - 0.5f) * (screenHeight - gap * 2);
            tubesXArray[i] = screenWidth / 2 - topTube.getWidth() / 2 + screenWidth + i * distanceBetweenTubes;
            topTubesRectangles[i] = new Rectangle();
            bottomTubesRectangles[i] = new Rectangle();
        }
    }

    private static GameManager game = null;

    public synchronized static GameManager getInstance() {
        if (game == null)
            game = new GameManager();
        return game;
    }
    
    public void updateScore(){
        if (tubesXArray[scoringTube] < Gdx.graphics.getWidth() / 2){
            score++;
            Gdx.app.log("Score",String.valueOf(score));

            if (scoringTube < numberOfTubes-1){
                scoringTube++;
            }else{
                scoringTube = 0;
            }
        }
    }

    public float getRandomTubeOffset(){
        return (game.randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap * 2);
    }

    public void updateTubeVelocityAndCounter(){
        game.tubesCounter += 1;
        if (tubesCounter % getNumberOfTubesForVelocityUp == 0) {
            tubeVelocity += 1;
        }
    }

    public void updateBirdVelocity(){
        birdVelocity += gravity;
        birdY -= birdVelocity;
    }

    public void animateBird(){
        if (timerBirdInt == 5) {
            flapState = 1;
            timerBirdInt = 0;
        } else {
            flapState = 0;
            timerBirdInt += 1;
        }
    }

    public void checkBirdCollision(){
        // shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // shapeRenderer.setColor(Color.RED);
        birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2, birds[flapState].getHeight()/2);
        //  shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
        for (int i = 0; i < numberOfTubes; i++) {
            // shapeRenderer.rect(bottomTubesRectangles[i].x, bottomTubesRectangles[i].y, bottomTubesRectangles[i].width, bottomTubesRectangles[i].height);
            // shapeRenderer.rect(topTubesRectangles[i].x, topTubesRectangles[i].y, topTubesRectangles[i].width, topTubesRectangles[i].height);

            if (Intersector.overlaps(birdCircle,bottomTubesRectangles[i]) || Intersector.overlaps(birdCircle,topTubesRectangles[i])){
                //Gdx.app.log("Collision","Yes!");
                gameState = GameState.GAME_FINISHED;
            }
        }
    }

    public void checkIfReplayPressed(){

        Rectangle textureBounds = new Rectangle(screenWidth/2- replay.getWidth()/2,screenHeight/2 + screenHeight *1/8  ,game.replay.getWidth(),game.replay.getHeight());

        Vector3 tmp = new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);
        if(textureBounds.contains(tmp.x,tmp.y)) {
            gameState = GameState.GAME_OPENED;
            setupGame();

        }
    }

    public void checkIfPlayPressed(){

        Rectangle textureBounds = new Rectangle((float)game.screenWidth/2 - game.play.getWidth()/2, (float) (game.screenHeight/2 + game.play.getHeight()*.5),game.play.getWidth(),game.play.getHeight());
        Vector3 tmp = new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);
        if(textureBounds.contains(tmp.x,tmp.y)) {
            gameState = GameState.GAME_NOT_STARTED;
        }
    }

    public void saveScore(){

        if (flagScoreUpdate == 1){
            return;
        }
        flagScoreUpdate = 1;

        if (prefs.getInteger("bestScore") < score){
            prefs.putInteger("bestScore",score);
            prefs.flush();
            gameover = new Texture("gameover-new.png");
            return;
        }
        gameover = new Texture("gameover.png");

    }

    public Texture getMedal(int num){
        medal = new Texture("medal"+num+".png");
        return medal;
    }

    public Integer checkIfMedal(){
        if (score > 10){
            return 1;
        }else if (score < 30 && score > 10){
            return 2;
        }else if (score > 30){
            return 3;
        }else{
            return null;
        }
    }


}
