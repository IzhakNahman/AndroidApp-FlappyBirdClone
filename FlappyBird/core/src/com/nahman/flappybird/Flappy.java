package com.nahman.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Flappy extends ApplicationAdapter {
    SpriteBatch batch;

    GameManager game;


    @Override
    public void create() {
        game = GameManager.getInstance();
        batch = new SpriteBatch();

    }

    @Override
    public void render() {

        //game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        batch.begin();
        batch.draw(game.background, 0, 0, Gdx.graphics.getWidth(), game.screenHeight);

        if (game.gameState == GameManager.GameState.GAME_OPENED){
            batch.draw(game.flappyBird,game.screenWidth/2 - game.flappyBird.getWidth()/2, game.screenHeight/2 + game.flappyBird.getHeight()/2);
            batch.draw(game.play,game.screenWidth/2 - game.play.getWidth()/2, (float) (game.screenHeight/2 - game.play.getHeight()*1.5));
            batch.draw(game.birds[game.flapState], game.screenWidth/2 - game.birds[0].getWidth()/2, game.birdY);

            if (Gdx.input.justTouched()) {
                if(game.checkIfPlayPressed()){
                    game.clickSound.play();
                    if (game.flagGameOpened == 0){
                        game.themeSound.setLooping(true);
                        game.themeSound.play();
                        game.flagGameOpened = 1;
                    }
                }
            }

        }else {

            if (game.gameState == GameManager.GameState.GAME_STARTED) {

                if(game.updateScore()){
                    game.scoreSound.play();
                }

                if (Gdx.input.justTouched()) {
                    game.birdVelocity = -game.birds[0].getHeight() / 4;
                    game.flySound.play();

                }

                for (int i = 0; i < game.numberOfTubes; i++) {
                    if (game.tubesXArray[i] < -game.topTube.getWidth()) {
                        game.tubesXArray[i] += game.numberOfTubes * game.distanceBetweenTubes;
                        game.tubeOfSet[i] = game.getRandomTubeOffset();
                        game.updateTubeVelocityAndCounter();
                    } else {
                        game.tubesXArray[i] -= game.tubeVelocity;
                    }
                    batch.draw(game.topTube, game.tubesXArray[i], game.screenHeight / 2 + game.gap / 2 + game.tubeOfSet[i]);
                    batch.draw(game.bottomTube, game.tubesXArray[i], game.screenHeight / 2 - game.gap / 2 - game.bottomTube.getHeight() + game.tubeOfSet[i]);

                    game.bottomTubesRectangles[i] = new Rectangle(game.tubesXArray[i], game.screenHeight / 2 - game.gap / 2 - game.bottomTube.getHeight() + game.tubeOfSet[i], game.topTube.getWidth(), game.topTube.getHeight());
                    game.topTubesRectangles[i] = new Rectangle(game.tubesXArray[i], game.screenHeight / 2 + game.gap / 2 + game.tubeOfSet[i], game.topTube.getWidth(), game.topTube.getHeight());

                }


                if (game.birdY > 0) {
                    game.updateBirdVelocity();
                } else {
                    game.gameState = GameManager.GameState.GAME_FINISHED;
                }

            } else if (game.gameState == GameManager.GameState.GAME_NOT_STARTED) {

                batch.draw(game.tapanywhere,game.screenWidth/2 - game.tapanywhere.getWidth() /2,game.screenHeight - game.screenHeight/10);
                if (Gdx.input.justTouched()) {
                    game.gameState = GameManager.GameState.GAME_STARTED;
                }
            }


            batch.draw(game.birds[game.flapState], game.birdX, game.birdY);

            if (game.gameState == GameManager.GameState.GAME_FINISHED) {
                game.themeSound.stop();
                game.saveScore();
                for (int i = 0; i < game.numberOfTubes; i++) {
                    batch.draw(game.topTube, game.tubesXArray[i], game.screenHeight / 2 + game.gap / 2 + game.tubeOfSet[i]);
                    batch.draw(game.bottomTube, game.tubesXArray[i], game.screenHeight / 2 - game.gap / 2 - game.bottomTube.getHeight() + game.tubeOfSet[i]);
                }
                int gameOverWidth = game.screenWidth * 5 / 6;
                int gameOverHeight = game.screenHeight *2/5;
                batch.draw(game.gameover, Gdx.graphics.getWidth() / 2 - gameOverWidth / 2, game.screenHeight / 2 - gameOverHeight/4, gameOverWidth, gameOverHeight);
                batch.draw(game.replay, Gdx.graphics.getWidth() / 2 - game.replay.getWidth() / 2, game.screenHeight / 2 - gameOverHeight/2, game.replay.getWidth(), game.replay.getHeight());

                game.gravity = 2;
                if (game.birdY > 0) {
                    game.updateBirdVelocity();
                }



                game.gameOverFont.draw(batch, String.valueOf(game.score), game.screenWidth *3/4, game.screenHeight / 2 + gameOverHeight/5 );
                game.gameOverFont.draw(batch, String.valueOf(game.prefs.getInteger("bestScore")), game.screenWidth *3/4, game.screenHeight / 2 - gameOverHeight/20 );

                Integer medal = game.checkIfMedal();
                if (medal!=null) {
                    batch.draw(game.getMedal(medal),  (game.screenWidth / 2 - gameOverWidth/2.5f), game.screenHeight / 2 - gameOverHeight/8 , gameOverWidth/4.5f, gameOverHeight/3.5f);
                }

                if (Gdx.input.justTouched()) {
                    if (game.checkIfReplayPressed()==true){
                        game.clickSound.play();
                    }
                }

            }else {
                game.font.draw(batch, String.valueOf(game.score), 100, 200);
                if (game.checkBirdCollision()){
                    game.crashSound.play();
                }
                game.animateBird();
            }
        }

        batch.end();
       //game.shapeRenderer.end();
    }



    @Override
    public void dispose() {
        game.flySound.dispose();
        game.clickSound.dispose();
        game.crashSound.dispose();
        game.scoreSound.dispose();
        game.themeSound.dispose();
        super.dispose();
        game.resetGameManager();
    }
}


