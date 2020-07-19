// source code https://www.youtube.com/watch?v=boAJUSN8fOU
package application;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Form {
	Rectangle a;
	Rectangle b;
	Rectangle c;
	Rectangle d;
	Color color;
	private String name;
	public int form = 1;
	
	public Form (Rectangle a, Rectangle b, Rectangle c, Rectangle d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
	
	public Form (Rectangle a, Rectangle b, Rectangle c, Rectangle d, String name) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.name = name;
	
	// set the color of the stones (colors can be changed
	switch(name) {
		case "j":
			color = Color.LIME;
			break;
		case "l":
			color = Color.DEEPPINK;
			break;
		case "o":
			color = Color.CYAN;
			break;
		case "s":
			color = Color.BLUE;
			break;
		case "t":
			color = Color.RED;
			break;
		case "z":
			color = Color.DARKVIOLET;
			break;
		case "i":
			color = Color.ORANGERED;
			break;
	
	}
	
	this.a.setFill(color);
	this.b.setFill(color);
	this.c.setFill(color);
	this.d.setFill(color);
	}

	// get method
	public String getName() {
		return name;
	}
	
	
	// changing stone forms
	public void changeForm() {
		if (form != 4) {
			form++;
		} else {
			form = 1;
		}
	}
	

}
