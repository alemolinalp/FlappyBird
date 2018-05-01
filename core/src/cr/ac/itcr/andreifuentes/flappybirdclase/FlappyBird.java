package cr.ac.itcr.andreifuentes.flappybirdclase;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;

	Texture background;
	Texture topTube;
	Texture bottomTube;
	Texture[] birds;
	Texture gameOver;
	int birdState;
	float gap;
	float birdY;
	float velocity;
	float gravity;
	int numberOfPipes = 4;
	float pipeX[] = new float[numberOfPipes];
	float pipeYOffset[] = new float[numberOfPipes];
	float distance;
	float pipeVelocity = 5;
	Random random;
	float maxLine;
	float minLine;
	int score;
	int pipeActivo;
	BitmapFont font;
	int game_state;

	Circle birdCircle;
	Rectangle[] topPipes;
	Rectangle[] bottomPipes;

	String easy = "Easy";
	String hard = "Hard";

	Sound jump;
	Sound hit;
	Sound onlyjump;

	@Override
	public void create () {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		background = new Texture("bg.png");
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");
		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		gameOver = new Texture("gameOverOriginal.png");

		birdCircle = new Circle();
		topPipes = new Rectangle[numberOfPipes];
		bottomPipes = new Rectangle[numberOfPipes];

		birdState = 0;
		game_state = 0;
		gap = 500;
		velocity = 0;
		gravity = 0.5f;
		random = new Random();
		distance = Gdx.graphics.getWidth() * 3/5;
		maxLine = Gdx.graphics.getHeight()* 3/4;
		minLine = Gdx.graphics.getHeight()* 1/4;
		score = 0;
		pipeActivo = 0;

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		jump = Gdx.audio.newSound(Gdx.files.internal("jump.mp3"));
		hit =  Gdx.audio.newSound(Gdx.files.internal("hit.mp3"));
		onlyjump = Gdx.audio.newSound(Gdx.files.internal("onlyjump.mp3"));

		startGame();
	}

	public void startGame(){
		birdY = Gdx.graphics.getHeight()/2 - birds[birdState].getHeight()/2;
		for (int i = 0; i<numberOfPipes; i++){
			pipeYOffset[i] = (random.nextFloat()*(maxLine-minLine)+minLine);
			pipeX[i] = Gdx.graphics.getWidth()/2 - topTube.getWidth() + Gdx.graphics.getWidth() + distance*i;

			// inicializamos cada uno de los Shapes
			topPipes[i] = new Rectangle();
			bottomPipes[i] = new Rectangle();
		}
	}

	@Override
	public void render () {

		batch.begin();
		batch.draw(background, 0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// no iniciado
		if (game_state == 0){
			dificultad();
		}
		// jugando
		else if (game_state == 1){
			if (pipeX[pipeActivo] < Gdx.graphics.getWidth()/2 - topTube.getWidth()){
				jump.play(1.0f);
				score++;

				if (pipeActivo < numberOfPipes - 1){
					pipeActivo++;
				}
				else {
					pipeActivo = 0;
				}

				Gdx.app.log("score", Integer.toString(score));
			}


			birdCircle.set(Gdx.graphics.getWidth()/2, birdY + birds[birdState].getHeight()/2, birds[birdState].getWidth()/2);

//			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//			shapeRenderer.setColor(Color.MAGENTA);
//			shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
//

			// Posicionamiento de los pipes
			for (int i = 0; i<numberOfPipes; i++) {

				if (pipeX[i] < -topTube.getWidth()){
					pipeYOffset[i] = (random.nextFloat()*(maxLine-minLine)+minLine);
					pipeX[i] += distance*(numberOfPipes);
				}
				else {
					pipeX[i] = pipeX[i] - pipeVelocity;
				}

				batch.draw(topTube,
						pipeX[i],
						pipeYOffset[i]+gap/2,
						topTube.getWidth(),
						topTube.getHeight());
				batch.draw(bottomTube,
						pipeX[i],
						pipeYOffset[i]-bottomTube.getHeight()-gap/2,
						bottomTube.getWidth(),
						bottomTube.getHeight());

				topPipes[i] = new Rectangle(pipeX[i],
						pipeYOffset[i]+gap/2,
						topTube.getWidth(),
						topTube.getHeight());
				bottomPipes[i] = new Rectangle(pipeX[i],
						pipeYOffset[i]-bottomTube.getHeight()-gap/2,
						bottomTube.getWidth(),
						bottomTube.getHeight());

//				shapeRenderer.rect(topPipes[i].x, topPipes[i].y, topTube.getWidth(),
//						topTube.getHeight());
//				shapeRenderer.rect(bottomPipes[i].x, bottomPipes[i].y, bottomTube.getWidth(),
//						bottomTube.getHeight());

				if (Intersector.overlaps(birdCircle, topPipes[i])){
					Gdx.app.log("Intersector", "top pipe overlap");
					hit.play(1.0f);
					game_state = 2;
				}
				else if (Intersector.overlaps(birdCircle, bottomPipes[i])){
					Gdx.app.log("Intersector", "bottom pipe overlap");
					hit.play(1.0f);
					game_state = 2;
				}
			}

			if (Gdx.input.justTouched()){
				velocity = velocity - 30;
				onlyjump.play(1.0f);
			}

			birdState = birdState == 0 ? 1 : 0;


			velocity = velocity + gravity;


			if (birdY < 0){
				game_state = 2;
			}
			else {
				birdY = birdY - velocity;
			}

//			shapeRenderer.end();


		}
		// game over
		else if (game_state == 2){
			batch.draw(gameOver, Gdx.graphics.getWidth()/2 - gameOver.getWidth()/2, Gdx.graphics.getHeight()/2 - gameOver.getHeight()/2);
			dificultad();
		}

		batch.draw(birds[birdState], Gdx.graphics.getWidth() / 2 - birds[birdState].getWidth()/2,  birdY,  birds[birdState].getWidth(), birds[birdState].getHeight());
		font.draw(batch, Integer.toString(score), Gdx.graphics.getWidth()*1/8, Gdx.graphics.getHeight()*9/10);
		batch.end();

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}

	public void dificultad(){
		font.draw(batch, easy, Gdx.graphics.getWidth()*1/14, Gdx.graphics.getHeight()*9/14);
		font.draw(batch, hard, Gdx.graphics.getWidth()*3/5, Gdx.graphics.getHeight()*9/14);
		if (Gdx.input.justTouched()){
			if(Gdx.input.getX() > Gdx.graphics.getWidth()/2){
				gap = 400;
				distance = Gdx.graphics.getWidth()*2/5;
			}
			else{
				gap = 500;
				distance = Gdx.graphics.getWidth()*3/5;
			}
			if(game_state == 0) {
				game_state = 1;
				startGame();
			}
			else if(game_state == 2){
				game_state = 1;
				score = 0;
				pipeActivo = 0;
				velocity = 0;
				startGame();
			}
		}
	}
}
