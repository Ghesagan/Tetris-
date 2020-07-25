package application;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Tetris extends Application {
	// The variables
	public static final int MOVE = 25;
	public static final int SIZE = 25;
	public static int XMAX = SIZE * 12;
	public static int YMAX = SIZE * 24;
	public static int[][] MESH = new int[XMAX / SIZE][YMAX / SIZE];
	private static Pane group = new Pane();
	private static Form object;
	private static Scene scene = new Scene(group, XMAX + 150, YMAX, Color.BLACK);
	public static int score = 0;
	private static int top = 0;
	private static boolean game = true;
	private static Form nextObj = Controller.makeRect();
	private static int linesNo = 0;
	private static int linesNoSince = 0;
	private static Form[] heldShape = new Form[1];
	private static Form showNext = null;
	private static Form heldBlock = null;
	private static int meshWipeCount = 2;
	private static Text meshWipe;

	public static void main(String[] args) {
		launch(args);
	}
	//Two things to do, one add file to root project directory, two make a double thread in main should it give errors 
	MediaPlayer mediaPlayer;
	public void music() {
		String s = "Tetris 99 - Main Theme";
		//"C:\Users\Clauz\Music\Tetris 99 - Main Theme";
		Media h = new Media(Paths.get(s).toUri().toString());
		mediaPlayer = new MediaPlayer(h);
		mediaPlayer.play();;
	}

	@Override
	public void start(Stage stage) throws Exception {
		for (int[] a : MESH) {
			Arrays.fill(a, 0);
		}
		//music();
		Line line = new Line(XMAX, 0, XMAX, YMAX);
		line.setStroke(Color.WHITE);

		// To make the grid lines
		for(int i = 0; i < 12; i++) {
			Line gridline = new Line(SIZE*i+SIZE, 0, SIZE*i +SIZE, YMAX);
			gridline.setStroke(Color.WHITE);
			group.getChildren().add(gridline);
		}
		for(int i = 0; i < 24; i++) {
			Line gridline = new Line(0, SIZE*i+SIZE, XMAX, SIZE*i+SIZE);
			gridline.setStroke(Color.WHITE);
			group.getChildren().add(gridline);
		}

		Text scoretext = new Text("Score: ");
		scoretext.setStyle("-fx-font: 20 arial;");
		scoretext.setY(50);
		scoretext.setX(XMAX + 5);
		scoretext.setFill(Color.WHITE);
		Text level = new Text("Lines: ");
		level.setStyle("-fx-font: 20 arial;");
		level.setY(100);
		level.setX(XMAX + 5);
		level.setFill(Color.GREEN);
		Text nextblock = new Text("Next Block: ");
		nextblock.setStyle("-fx-font: 20 arial;");
		nextblock.setY(150);
		nextblock.setX(XMAX + 5);
		nextblock.setFill(Color.WHITE);
		Text held = new Text("Held Block: ");
		held.setStyle("-fx-font: 20 arial;");
		held.setY(275);
		held.setX(XMAX + 5);
		held.setFill(Color.WHITE);
		meshWipe = new Text(Integer.toString(meshWipeCount) + " Grid Wipes left!");
		meshWipe.setStyle("-fx-font: 15 arial;");
		meshWipe.setY(YMAX-25);
		meshWipe.setX(XMAX + 5);
		meshWipe.setFill(Color.WHITE);

		group.getChildren().addAll(scoretext, line, level, nextblock, held, meshWipe);



		Form a = nextObj;
		group.getChildren().addAll(a.a, a.b, a.c, a.d);
		moveOnKeyPress(a);
		object = a;
		nextObj = Controller.makeRect();


		//first time we show the next playable block
		showNext = Controller.CopyForm(nextObj);
		showNext.a.setX(showNext.a.getX()+ 9*SIZE);
		showNext.b.setX(showNext.b.getX()+ 9*SIZE);
		showNext.c.setX(showNext.c.getX()+ 9*SIZE);
		showNext.d.setX(showNext.d.getX()+ 9*SIZE);
		showNext.a.setY(showNext.a.getY()+7*SIZE);
		showNext.b.setY(showNext.b.getY()+7*SIZE);
		showNext.c.setY(showNext.c.getY()+7*SIZE);
		showNext.d.setY(showNext.d.getY()+7*SIZE);

		group.getChildren().addAll(showNext.a,showNext.b,showNext.c,showNext.d);


		stage.setScene(scene);
		stage.setTitle("T E T R I S");
		stage.show();


		Timer fall = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				Platform.runLater(new Runnable() {
					public void run() {
						if (object.a.getY() == 0 || object.b.getY() == 0 || object.c.getY() == 0
								|| object.d.getY() == 0)
							top++;
						else
							top = 0;

						if (top == 2) {
							// GAME OVER
							Text over = new Text("GAME OVER");
							over.setFill(Color.RED);
							over.setStyle("-fx-font: 70 arial;");
							over.setY(250);
							over.setX(10);
							group.getChildren().add(over);
							game = false;
						}
						// Exit
						if (top == 15) {
							System.exit(0);
						}

						if (game) {
							
							MoveDown(object);
							score++;
							scoretext.setText("Score: " + Integer.toString(score));
							level.setText("Lines: " + Integer.toString(linesNo));
						}
					}
				});
			}
		};
		fall.schedule(task, 0, 300);
		
		Timer lines = new Timer();
		TimerTask checkTimeSinceLastLine = new TimerTask() {
			public void run() {
				Platform.runLater(new Runnable() {
					public void run() {
						if(linesNo<=linesNoSince) {
							AddRow(group, object);
							
						}else {
							linesNoSince = linesNo;
						}
					}
				});
			}
		};
		lines.schedule(checkTimeSinceLastLine, 9000, 9000);//this is really fast!
		
	}


	private void moveOnKeyPress(Form form) {
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				switch (event.getCode()) {
				case RIGHT:
					Controller.MoveRight(form);
					break;
				case DOWN:
					MoveDown(form);
					score++;
					break;
				case LEFT:
					Controller.MoveLeft(form);
					break;
				case UP:
					MoveTurn(form);
					break;
				case CONTROL:
					HardDrop(form);
					break;
				case B:
					if(meshWipeCount > 0) {
						MeshWipe(group);
						meshWipeCount--;
						meshWipe.setText(Integer.toString(meshWipeCount) + " Grid Wipes left!");
					}
					break;
				case H:
					HoldBlock(form, group);
					break;
				}
			}
		});
	}
	//has the same bug as the rest if the object in play is moving then its rectangles become invisable note, only happens when I call teh pushUp method 
	private void AddRow(Pane group, Form obj) {
		//we are going to make a auxillary funcion that pushes all the exsiting blocks up 
		PushUpBlocks(group);
		//just to make sure we don't generate the new row on top of the object
		if(obj.a.getY()==23*SIZE || obj.b.getY()==23*SIZE || obj.c.getY()==23*SIZE || obj.d.getY()==23*SIZE) {
			obj.a.setY(obj.a.getY()-SIZE);
			obj.b.setY(obj.b.getY()-SIZE);
			obj.c.setY(obj.c.getY()-SIZE);
			obj.d.setY(obj.d.getY()-SIZE);	
		}
		//we have a empty row at the bottom of the screen that needs to be almost filled
		Random rand = new Random();
		int leaveEmpty = rand.nextInt(XMAX/SIZE); //yes?
		for(int i =0; i<XMAX/SIZE; i++) {
			if(i != leaveEmpty) {
				MESH[i][YMAX/SIZE-1] = 1;
				Rectangle r = new Rectangle(SIZE-1, SIZE-1);// just to make the blocks look more like the other blocks
				r.setFill(Color.WHITESMOKE);
				r.setX(i*SIZE);
				r.setY(23*SIZE);
				group.getChildren().add(r);
			}
		}
		
	}
	private void PushUpBlocks(Pane pane) {
		ArrayList<Node> rects = new ArrayList<Node>();
		for (Node node : pane.getChildren()) {
			if (node instanceof Rectangle)
				if((((Rectangle) node).getX()<=XMAX && ((Rectangle) node).getX()<=YMAX)){ 
					if((((Rectangle) node).getX() == object.a.getX() && ((Rectangle) node).getY() == object.a.getY())
					|| (((Rectangle) node).getX() == object.b.getX() && ((Rectangle) node).getY() == object.b.getY())
					|| (((Rectangle) node).getX() == object.c.getX() && ((Rectangle) node).getY() == object.c.getY())
					|| ((Rectangle) node).getX() == object.d.getX() && ((Rectangle) node).getY() == object.d.getY()) {
						//do nothing because we want to keep the object in play on the screen
					}
					else {
						rects.add(node);
					}
				}				
		}

		for (Node n : rects) {
			Rectangle a = (Rectangle) n;
			MESH[(int) a.getX() / SIZE][(int) a.getY() / SIZE] = 0;
			pane.getChildren().remove(a);
		}
		for (Node n : rects) {
			Rectangle a = (Rectangle) n;
			MESH[(int) a.getX() / SIZE][(int) a.getY() / SIZE - 1] = 1;
			a.setY(a.getY()-SIZE);//draw the rectangle on square up
			pane.getChildren().add(a);
		}
		rects.clear();
		
	}
	private void HoldBlock(Form form, Pane group) {
		if(heldShape[0] == null) {
			//erase form after making a copy
			heldBlock = Controller.CopyForm(form);
			group.getChildren().removeAll(form.a,form.b,form.c,form.d);

			//creating the new nextObj and setting object equal to the old nextObj
			Form a = nextObj;
			nextObj = Controller.makeRect();
			object = a;
			group.getChildren().addAll(a.a, a.b, a.c, a.d);
			moveOnKeyPress(a);
			
			//updating the showNext block 
			if(showNext != null) {
				group.getChildren().removeAll(showNext.a,showNext.b,showNext.c,showNext.d);
			}
			showNext = Controller.CopyForm(nextObj);
			showNext.a.setX(showNext.a.getX() + 9*SIZE);
			showNext.b.setX(showNext.b.getX() + 9*SIZE);
			showNext.c.setX(showNext.c.getX() + 9*SIZE);
			showNext.d.setX(showNext.d.getX() + 9*SIZE);
			showNext.a.setY(showNext.a.getY() + 7*SIZE);
			showNext.b.setY(showNext.b.getY() + 7*SIZE);
			showNext.c.setY(showNext.c.getY() + 7*SIZE);
			showNext.d.setY(showNext.d.getY() + 7*SIZE);
			group.getChildren().addAll(showNext.a,showNext.b,showNext.c,showNext.d);

			heldShape[0]=heldBlock;
		
			Controller.ResetPosition(heldBlock, heldBlock.getName());
			heldBlock.a.setX(heldBlock.a.getX() + 9*SIZE);
			heldBlock.b.setX(heldBlock.b.getX() + 9*SIZE);
			heldBlock.c.setX(heldBlock.c.getX() + 9*SIZE);
			heldBlock.d.setX(heldBlock.d.getX() + 9*SIZE);
			
			heldBlock.a.setY(heldBlock.a.getY() + 12*SIZE);
			heldBlock.b.setY(heldBlock.b.getY() + 12*SIZE);
			heldBlock.c.setY(heldBlock.c.getY() + 12*SIZE);
			heldBlock.d.setY(heldBlock.d.getY() + 12*SIZE);
			group.getChildren().addAll(heldBlock.a,heldBlock.b,heldBlock.c,heldBlock.d); 
		}
		else {
			//we make a copy of the held block then remove held block from the game
			Form copy = Controller.CopyForm(heldBlock);
			if(heldBlock!= null) {
				group.getChildren().removeAll(heldBlock.a,heldBlock.b,heldBlock.c,heldBlock.d);
			}
			//we make a copy of the current moving block in the game, delete the old moving block 
			heldBlock = Controller.CopyForm(form);
			group.getChildren().removeAll(form.a,form.b,form.c,form.d);
			//we take the copy of the old block and place it on the side of the screen
			Controller.ResetPosition(heldBlock, heldBlock.getName());
			heldBlock.a.setX(heldBlock.a.getX() + 9*SIZE);
			heldBlock.b.setX(heldBlock.b.getX() + 9*SIZE);
			heldBlock.c.setX(heldBlock.c.getX() + 9*SIZE);
			heldBlock.d.setX(heldBlock.d.getX() + 9*SIZE);
			heldBlock.a.setY(heldBlock.a.getY() + 12*SIZE);
			heldBlock.b.setY(heldBlock.b.getY() + 12*SIZE);
			heldBlock.c.setY(heldBlock.c.getY() + 12*SIZE);
			heldBlock.d.setY(heldBlock.d.getY() + 12*SIZE);
			group.getChildren().addAll(heldBlock.a, heldBlock.b, heldBlock.c, heldBlock.d);
			//we take the copy of the old held block and place it onto the mesh as well as make it the new playable moving block
			Controller.ResetPosition(copy, copy.getName());
			group.getChildren().addAll(copy.a,copy.b,copy.c,copy.d);
			heldShape[0] = heldBlock;
			object = copy;
			moveOnKeyPress(copy);
			

		}
	}
	
	//current issue, it also erases the new shape on the screen but not in the mesh
	private void MeshWipe(Pane group) {
		ArrayList<Rectangle> rects = new ArrayList<Rectangle>();
		for (Node node : group.getChildren()) {
			if (node instanceof Rectangle) {
				Rectangle r = (Rectangle) node;
				if((r.getX()<=XMAX && r.getX()<=YMAX)){
					if((r.getX() == object.a.getX() &&  r.getY() == object.a.getY())
					|| (r.getX() == object.b.getX() && r.getY() == object.b.getY())
					|| (r.getX() == object.c.getX() && r.getY() == object.c.getY())
					|| (r.getX() == object.d.getX() && r.getY() == object.d.getY())) {
						//do nothing because we want to keep the shape that is in play.
				}else {
					rects.add(r);
				}
				}

			}

		}
		for (Rectangle r : rects) { 
			MESH[(int) r.getX() / SIZE][(int) r.getY() / SIZE] = 0;
			group.getChildren().remove(r);
		}
		rects.clear();
	}
	private void DeleteTop4(Pane group) {
		//idea: someday.. if Chandler feels like doing he can.
	}


	// drop the block all the way to the bottom
	private void HardDrop(Form form) {
		int AYPeak = (int) form.a.getY()/SIZE;
		int BYPeak = (int) form.b.getY()/SIZE;
		int CYPeak = (int) form.c.getY()/SIZE;
		int DYPeak = (int) form.d.getY()/SIZE;
		// find where the first collision occur or if the first collision is the bottom of the MESH
		for (int i = 0; i < 23; i++) {
			if (AYPeak + i == YMAX/SIZE - 1 || MESH[(int) form.a.getX()/SIZE][AYPeak + i + 1] == 1) {
				form.a.setY((AYPeak + i)*SIZE);
				form.b.setY((BYPeak + i)*SIZE);
				form.c.setY((CYPeak + i)*SIZE);
				form.d.setY((DYPeak + i)*SIZE);
				score += i;
				break;
			} else if (BYPeak + i == YMAX/SIZE - 1 || MESH[(int) form.b.getX()/SIZE][BYPeak + i + 1] == 1){
				form.a.setY((AYPeak + i)*SIZE);
				form.b.setY((BYPeak + i)*SIZE);
				form.c.setY((CYPeak + i)*SIZE);
				form.d.setY((DYPeak + i)*SIZE);
				score += i;
				break;
			} else if (CYPeak + i == YMAX/SIZE - 1 || MESH[(int) form.c.getX()/SIZE][CYPeak + i + 1] == 1) {
				form.a.setY((AYPeak + i)*SIZE);
				form.b.setY((BYPeak + i)*SIZE);
				form.c.setY((CYPeak + i)*SIZE);
				form.d.setY((DYPeak + i)*SIZE);
				score += i;
				break;
			} else if (DYPeak + i == YMAX/SIZE - 1 || MESH[(int) form.d.getX()/SIZE][DYPeak + i + 1] == 1) {
				form.a.setY((AYPeak + i)*SIZE);
				form.b.setY((BYPeak + i)*SIZE);
				form.c.setY((CYPeak + i)*SIZE);
				form.d.setY((DYPeak + i)*SIZE);
				score += i;
				break;
			}
		}
		MESH[(int) form.a.getX() / SIZE][(int) form.a.getY() / SIZE] = 1;
		MESH[(int) form.b.getX() / SIZE][(int) form.b.getY() / SIZE] = 1;
		MESH[(int) form.c.getX() / SIZE][(int) form.c.getY() / SIZE] = 1;
		MESH[(int) form.d.getX() / SIZE][(int) form.d.getY() / SIZE] = 1;
	}

	private void MoveTurn(Form form) {
		int f = form.form;
		Rectangle a = form.a;
		Rectangle b = form.b;
		Rectangle c = form.c;
		Rectangle d = form.d;
		switch (form.getName()) {
		case "j":
			if (f == 1 && cB(a, 1, -1) && cB(c, -1, -1) && cB(d, -2, -2)) {
				MoveRight(form.a);
				MoveDown(form.a);
				MoveDown(form.c);
				MoveLeft(form.c);
				MoveDown(form.d);
				MoveDown(form.d);
				MoveLeft(form.d);
				MoveLeft(form.d);
				form.changeForm();
				break;
			}
			if (f == 2 && cB(a, -1, -1) && cB(c, -1, 1) && cB(d, -2, 2)) {
				MoveDown(form.a);
				MoveLeft(form.a);
				MoveLeft(form.c);
				MoveUp(form.c);
				MoveLeft(form.d);
				MoveLeft(form.d);
				MoveUp(form.d);
				MoveUp(form.d);
				form.changeForm();
				break;
			}
			if (f == 3 && cB(a, -1, 1) && cB(c, 1, 1) && cB(d, 2, 2)) {
				MoveLeft(form.a);
				MoveUp(form.a);
				MoveUp(form.c);
				MoveRight(form.c);
				MoveUp(form.d);
				MoveUp(form.d);
				MoveRight(form.d);
				MoveRight(form.d);
				form.changeForm();
				break;
			}
			if (f == 4 && cB(a, 1, 1) && cB(c, 1, -1) && cB(d, 2, -2)) {
				MoveUp(form.a);
				MoveRight(form.a);
				MoveRight(form.c);
				MoveDown(form.c);
				MoveRight(form.d);
				MoveRight(form.d);
				MoveDown(form.d);
				MoveDown(form.d);
				form.changeForm();
				break;
			}
			break;
		case "l":
			if (f == 1 && cB(a, 1, -1) && cB(c, 1, 1) && cB(b, 2, 2)) {
				MoveRight(form.a);
				MoveDown(form.a);
				MoveUp(form.c);
				MoveRight(form.c);
				MoveUp(form.b);
				MoveUp(form.b);
				MoveRight(form.b);
				MoveRight(form.b);
				form.changeForm();
				break;
			}
			if (f == 2 && cB(a, -1, -1) && cB(b, 2, -2) && cB(c, 1, -1)) {
				MoveDown(form.a);
				MoveLeft(form.a);
				MoveRight(form.b);
				MoveRight(form.b);
				MoveDown(form.b);
				MoveDown(form.b);
				MoveRight(form.c);
				MoveDown(form.c);
				form.changeForm();
				break;
			}
			if (f == 3 && cB(a, -1, 1) && cB(c, -1, -1) && cB(b, -2, -2)) {
				MoveLeft(form.a);
				MoveUp(form.a);
				MoveDown(form.c);
				MoveLeft(form.c);
				MoveDown(form.b);
				MoveDown(form.b);
				MoveLeft(form.b);
				MoveLeft(form.b);
				form.changeForm();
				break;
			}
			if (f == 4 && cB(a, 1, 1) && cB(b, -2, 2) && cB(c, -1, 1)) {
				MoveUp(form.a);
				MoveRight(form.a);
				MoveLeft(form.b);
				MoveLeft(form.b);
				MoveUp(form.b);
				MoveUp(form.b);
				MoveLeft(form.c);
				MoveUp(form.c);
				form.changeForm();
				break;
			}
			break;
		case "o":
			break;
		case "s":
			if (f == 1 && cB(a, -1, -1) && cB(c, -1, 1) && cB(d, 0, 2)) {
				MoveDown(form.a);
				MoveLeft(form.a);
				MoveLeft(form.c);
				MoveUp(form.c);
				MoveUp(form.d);
				MoveUp(form.d);
				form.changeForm();
				break;
			}
			if (f == 2 && cB(a, 1, 1) && cB(c, 1, -1) && cB(d, 0, -2)) {
				MoveUp(form.a);
				MoveRight(form.a);
				MoveRight(form.c);
				MoveDown(form.c);
				MoveDown(form.d);
				MoveDown(form.d);
				form.changeForm();
				break;
			}
			if (f == 3 && cB(a, -1, -1) && cB(c, -1, 1) && cB(d, 0, 2)) {
				MoveDown(form.a);
				MoveLeft(form.a);
				MoveLeft(form.c);
				MoveUp(form.c);
				MoveUp(form.d);
				MoveUp(form.d);
				form.changeForm();
				break;
			}
			if (f == 4 && cB(a, 1, 1) && cB(c, 1, -1) && cB(d, 0, -2)) {
				MoveUp(form.a);
				MoveRight(form.a);
				MoveRight(form.c);
				MoveDown(form.c);
				MoveDown(form.d);
				MoveDown(form.d);
				form.changeForm();
				break;
			}
			break;
		case "t":
			if (f == 1 && cB(a, 1, 1) && cB(d, -1, -1) && cB(c, -1, 1)) {
				MoveUp(form.a);
				MoveRight(form.a);
				MoveDown(form.d);
				MoveLeft(form.d);
				MoveLeft(form.c);
				MoveUp(form.c);
				form.changeForm();
				break;
			}
			if (f == 2 && cB(a, 1, -1) && cB(d, -1, 1) && cB(c, 1, 1)) {
				MoveRight(form.a);
				MoveDown(form.a);
				MoveLeft(form.d);
				MoveUp(form.d);
				MoveUp(form.c);
				MoveRight(form.c);
				form.changeForm();
				break;
			}
			if (f == 3 && cB(a, -1, -1) && cB(d, 1, 1) && cB(c, 1, -1)) {
				MoveDown(form.a);
				MoveLeft(form.a);
				MoveUp(form.d);
				MoveRight(form.d);
				MoveRight(form.c);
				MoveDown(form.c);
				form.changeForm();
				break;
			}
			if (f == 4 && cB(a, -1, 1) && cB(d, 1, -1) && cB(c, -1, -1)) {
				MoveLeft(form.a);
				MoveUp(form.a);
				MoveRight(form.d);
				MoveDown(form.d);
				MoveDown(form.c);
				MoveLeft(form.c);
				form.changeForm();
				break;
			}
			break;
		case "z":
			if (f == 1 && cB(b, 1, 1) && cB(c, -1, 1) && cB(d, -2, 0)) {
				MoveUp(form.b);
				MoveRight(form.b);
				MoveLeft(form.c);
				MoveUp(form.c);
				MoveLeft(form.d);
				MoveLeft(form.d);
				form.changeForm();
				break;
			}
			if (f == 2 && cB(b, -1, -1) && cB(c, 1, -1) && cB(d, 2, 0)) {
				MoveDown(form.b);
				MoveLeft(form.b);
				MoveRight(form.c);
				MoveDown(form.c);
				MoveRight(form.d);
				MoveRight(form.d);
				form.changeForm();
				break;
			}
			if (f == 3 && cB(b, 1, 1) && cB(c, -1, 1) && cB(d, -2, 0)) {
				MoveUp(form.b);
				MoveRight(form.b);
				MoveLeft(form.c);
				MoveUp(form.c);
				MoveLeft(form.d);
				MoveLeft(form.d);
				form.changeForm();
				break;
			}
			if (f == 4 && cB(b, -1, -1) && cB(c, 1, -1) && cB(d, 2, 0)) {
				MoveDown(form.b);
				MoveLeft(form.b);
				MoveRight(form.c);
				MoveDown(form.c);
				MoveRight(form.d);
				MoveRight(form.d);
				form.changeForm();
				break;
			}
			break;
		case "i":
			if (f == 1 && cB(a, 2, 2) && cB(b, 1, 1) && cB(d, -1, -1)) {
				MoveUp(form.a);
				MoveUp(form.a);
				MoveRight(form.a);
				MoveRight(form.a);
				MoveUp(form.b);
				MoveRight(form.b);
				MoveDown(form.d);
				MoveLeft(form.d);
				form.changeForm();
				break;
			}
			if (f == 2 && cB(a, -2, -2) && cB(b, -1, -1) && cB(d, 1, 1)) {
				MoveDown(form.a);
				MoveDown(form.a);
				MoveLeft(form.a);
				MoveLeft(form.a);
				MoveDown(form.b);
				MoveLeft(form.b);
				MoveUp(form.d);
				MoveRight(form.d);
				form.changeForm();
				break;
			}
			if (f == 3 && cB(a, 2, 2) && cB(b, 1, 1) && cB(d, -1, -1)) {
				MoveUp(form.a);
				MoveUp(form.a);
				MoveRight(form.a);
				MoveRight(form.a);
				MoveUp(form.b);
				MoveRight(form.b);
				MoveDown(form.d);
				MoveLeft(form.d);
				form.changeForm();
				break;
			}
			if (f == 4 && cB(a, -2, -2) && cB(b, -1, -1) && cB(d, 1, 1)) {
				MoveDown(form.a);
				MoveDown(form.a);
				MoveLeft(form.a);
				MoveLeft(form.a);
				MoveDown(form.b);
				MoveLeft(form.b);
				MoveUp(form.d);
				MoveRight(form.d);
				form.changeForm();
				break;
			}
			break;
		}
	}

	private void RemoveRows(Pane pane) {
		ArrayList<Node> rects = new ArrayList<Node>();
		ArrayList<Integer> lines = new ArrayList<Integer>();
		ArrayList<Node> newrects = new ArrayList<Node>();
		int full = 0;
		for (int i = 0; i < MESH[0].length; i++) {
			for (int j = 0; j < MESH.length; j++) {
				if (MESH[j][i] == 1)
					full++;
			}
			if (full == MESH.length)
				lines.add(i); //add the x cord of the line to the list
			full = 0;
		}
		if (lines.size() > 0)
			do {
				for (Node node : pane.getChildren()) {
					if (node instanceof Rectangle)
						if(((Rectangle) node).getX()<=XMAX && ((Rectangle) node).getX()<=YMAX) //FIXED IT!!
							rects.add(node);
				}
				score += 50;
				linesNo++;

				for (Node node : rects) {
					Rectangle a = (Rectangle) node;
					if (a.getY() == lines.get(0) * SIZE) {
						MESH[(int) a.getX() / SIZE][(int) a.getY() / SIZE] = 0;
						pane.getChildren().remove(node);
					} else
						newrects.add(node);
				}

				for (Node node : newrects) {
					Rectangle a = (Rectangle) node;
					if (a.getY() < lines.get(0) * SIZE && a.getX() < XMAX) {
						MESH[(int) a.getX() / SIZE][(int) a.getY() / SIZE] = 0;
						a.setY(a.getY() + SIZE);
					}
				}
				lines.remove(0);
				rects.clear();
				newrects.clear();
				for (Node node : pane.getChildren()) {
					if (node instanceof Rectangle)
						rects.add(node);
				}
				for (Node node : rects) {
					Rectangle a = (Rectangle) node;
					try {
						MESH[(int) a.getX() / SIZE][(int) a.getY() / SIZE] = 1;
					} catch (ArrayIndexOutOfBoundsException e) {
					}
				}
				rects.clear();
			} while (lines.size() > 0);
	}

	private void MoveDown(Rectangle rect) {
		if (rect.getY() + MOVE < YMAX)
			rect.setY(rect.getY() + MOVE);

	}

	private void MoveRight(Rectangle rect) {
		if (rect.getX() + MOVE <= XMAX - SIZE)
			rect.setX(rect.getX() + MOVE);
	}

	private void MoveLeft(Rectangle rect) {
		if (rect.getX() - MOVE >= 0)
			rect.setX(rect.getX() - MOVE);
	}

	private void MoveUp(Rectangle rect) {
		if (rect.getY() - MOVE > 0)
			rect.setY(rect.getY() - MOVE);
	}

	private void MoveDown(Form form) {
		//moving down if down is full
		if (form.a.getY() == YMAX - SIZE || form.b.getY() == YMAX - SIZE || form.c.getY() == YMAX - SIZE
				|| form.d.getY() == YMAX - SIZE || moveA(form) || moveB(form) || moveC(form) || moveD(form)) {
			MESH[(int) form.a.getX() / SIZE][(int) form.a.getY() / SIZE] = 1;
			MESH[(int) form.b.getX() / SIZE][(int) form.b.getY() / SIZE] = 1;
			MESH[(int) form.c.getX() / SIZE][(int) form.c.getY() / SIZE] = 1;
			MESH[(int) form.d.getX() / SIZE][(int) form.d.getY() / SIZE] = 1;
			RemoveRows(group);

			Form a = nextObj;
			nextObj = Controller.makeRect();

			//remove previous block then show the next playable block
			if(showNext != null) {
				group.getChildren().removeAll(showNext.a,showNext.b,showNext.c,showNext.d);
			}
			showNext = Controller.CopyForm(nextObj);
			showNext.a.setX(showNext.a.getX()+ 9*SIZE);
			showNext.b.setX(showNext.b.getX()+ 9*SIZE);
			showNext.c.setX(showNext.c.getX()+ 9*SIZE);
			showNext.d.setX(showNext.d.getX()+ 9*SIZE);
			showNext.a.setY(showNext.a.getY()+7*SIZE);
			showNext.b.setY(showNext.b.getY()+7*SIZE);
			showNext.c.setY(showNext.c.getY()+7*SIZE);
			showNext.d.setY(showNext.d.getY()+7*SIZE);
			group.getChildren().addAll(showNext.a,showNext.b,showNext.c,showNext.d);



			object = a;
			group.getChildren().addAll(a.a, a.b, a.c, a.d);
			moveOnKeyPress(a);
		}
		//moving one block down if down is not full
		if (form.a.getY() + MOVE < YMAX && form.b.getY() + MOVE < YMAX && form.c.getY() + MOVE < YMAX
				&& form.d.getY() + MOVE < YMAX) {
			int movea = MESH[(int) form.a.getX() / SIZE][((int) form.a.getY() / SIZE) + 1];
			int moveb = MESH[(int) form.b.getX() / SIZE][((int) form.b.getY() / SIZE) + 1];
			int movec = MESH[(int) form.c.getX() / SIZE][((int) form.c.getY() / SIZE) + 1];
			int moved = MESH[(int) form.d.getX() / SIZE][((int) form.d.getY() / SIZE) + 1];
			if (movea == 0 && movea == moveb && moveb == movec && movec == moved) {
				form.a.setY(form.a.getY() + MOVE);
				form.b.setY(form.b.getY() + MOVE);
				form.c.setY(form.c.getY() + MOVE);
				form.d.setY(form.d.getY() + MOVE);
			}
		}
	}

	private boolean moveA(Form form) {
		return (MESH[(int) form.a.getX() / SIZE][((int) form.a.getY() / SIZE) + 1] == 1);
	}

	private boolean moveB(Form form) {
		return (MESH[(int) form.b.getX() / SIZE][((int) form.b.getY() / SIZE) + 1] == 1);
	}

	private boolean moveC(Form form) {
		return (MESH[(int) form.c.getX() / SIZE][((int) form.c.getY() / SIZE) + 1] == 1);
	}

	private boolean moveD(Form form) {
		return (MESH[(int) form.d.getX() / SIZE][((int) form.d.getY() / SIZE) + 1] == 1);
	}

	private boolean cB(Rectangle rect, int x, int y) {
		boolean xb = false;
		boolean yb = false;
		if (x >= 0)
			xb = rect.getX() + x * MOVE <= XMAX - SIZE;
		if (x < 0)
			xb = rect.getX() + x * MOVE >= 0;
		if (y >= 0)
			yb = rect.getY() - y * MOVE > 0;
		if (y < 0)
			yb = rect.getY() + y * MOVE < YMAX;
		return xb && yb && MESH[((int) rect.getX() / SIZE) + x][((int) rect.getY() / SIZE) - y] == 0;
	}

}