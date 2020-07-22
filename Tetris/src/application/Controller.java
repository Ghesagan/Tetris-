package application;


import javafx.scene.shape.Rectangle;

public class Controller {
    // Getting the numbers and the MESH from Tetris
    public static final int MOVE = Tetris.MOVE;
    public static final int SIZE = Tetris.SIZE;
    public static int XMAX = Tetris.XMAX;
    public static int YMAX = Tetris.YMAX;
    public static int[][] MESH = Tetris.MESH;

    public static void MoveRight(Form form) {
        if (form.a.getX() + MOVE <= XMAX - SIZE && form.b.getX() + MOVE <= XMAX - SIZE
                && form.c.getX() + MOVE <= XMAX - SIZE && form.d.getX() + MOVE <= XMAX - SIZE) {
            int movea = MESH[((int) form.a.getX() / SIZE) + 1][((int) form.a.getY() / SIZE)];
            int moveb = MESH[((int) form.b.getX() / SIZE) + 1][((int) form.b.getY() / SIZE)];
            int movec = MESH[((int) form.c.getX() / SIZE) + 1][((int) form.c.getY() / SIZE)];
            int moved = MESH[((int) form.d.getX() / SIZE) + 1][((int) form.d.getY() / SIZE)];
            if (movea == 0 && movea == moveb && moveb == movec && movec == moved) {
                form.a.setX(form.a.getX() + MOVE);
                form.b.setX(form.b.getX() + MOVE);
                form.c.setX(form.c.getX() + MOVE);
                form.d.setX(form.d.getX() + MOVE);
            }
        }
    }

    public static void MoveLeft(Form form) {
        if (form.a.getX() - MOVE >= 0 && form.b.getX() - MOVE >= 0 && form.c.getX() - MOVE >= 0
                && form.d.getX() - MOVE >= 0) {
            int movea = MESH[((int) form.a.getX() / SIZE) - 1][((int) form.a.getY() / SIZE)];
            int moveb = MESH[((int) form.b.getX() / SIZE) - 1][((int) form.b.getY() / SIZE)];
            int movec = MESH[((int) form.c.getX() / SIZE) - 1][((int) form.c.getY() / SIZE)];
            int moved = MESH[((int) form.d.getX() / SIZE) - 1][((int) form.d.getY() / SIZE)];
            if (movea == 0 && movea == moveb && moveb == movec && movec == moved) {
                form.a.setX(form.a.getX() - MOVE);
                form.b.setX(form.b.getX() - MOVE);
                form.c.setX(form.c.getX() - MOVE);
                form.d.setX(form.d.getX() - MOVE);
            }
        }
    }
    
    
    public static Form CopyForm(Form form) {
    	Rectangle a = new Rectangle(SIZE-1, SIZE-1), b = new Rectangle(SIZE-1, SIZE-1), c = new Rectangle(SIZE-1, SIZE-1),
                d = new Rectangle(SIZE-1, SIZE-1);
    	a.setX(form.a.getX());
    	b.setX(form.b.getX());
    	c.setX(form.c.getX());
    	d.setX(form.d.getX());
    	a.setY(form.a.getY());
    	b.setY(form.b.getY());
    	c.setY(form.c.getY());
    	d.setY(form.d.getY());
    	
		Form copy = new Form(a,b,c,d,form.getName());
		return copy;
	}
    
    public static void SetUpRelativeXY(Form copy, String name) {
    	if(name == "j") {
    		copy.a.setX(XMAX / 2 - SIZE);
    		copy.b.setX(XMAX / 2 - SIZE);
    		copy.b.setY(SIZE);
    		copy.c.setX(XMAX / 2);
    		copy.c.setY(SIZE);
    		copy.d.setX(XMAX / 2 + SIZE);
    		copy.d.setY(SIZE);
//    		a.setX(XMAX / 2 - SIZE);
//            b.setX(XMAX / 2 - SIZE);
//            b.setY(SIZE);
//            c.setX(XMAX / 2);
//            c.setY(SIZE);
//            d.setX(XMAX / 2 + SIZE);
//            d.setY(SIZE);
    	}
    	if(name == "l") {
    		copy.a.setX(XMAX / 2 + SIZE);
    		copy.b.setX(XMAX / 2 - SIZE);
    		copy.b.setY(SIZE);
    		copy.c.setX(XMAX / 2);
    		copy.c.setY(SIZE);
    		copy.d.setX(XMAX / 2 + SIZE);
    		copy.d.setY(SIZE);
    	}
    	if(name == "o") {
    		copy.a.setX(XMAX / 2 - SIZE);
    		copy.b.setX(XMAX / 2);
    		copy.c.setX(XMAX / 2 - SIZE);
    		copy.c.setY(SIZE);
    		copy.d.setX(XMAX / 2);
    		copy.d.setY(SIZE);

    	}
    	if(name == "s") {
    		copy.a.setX(XMAX / 2 + SIZE);
    		copy.b.setX(XMAX / 2);
    		copy.c.setX(XMAX / 2);
    		copy.c.setY(SIZE);
    		copy.d.setX(XMAX / 2 - SIZE);
    		copy.d.setY(SIZE);
    	}		
    	if(name == "t") {
    		copy.a.setX(XMAX / 2 - SIZE);
    		copy.b.setX(XMAX / 2);
            copy.c.setX(XMAX / 2);
            copy.c.setY(SIZE);
            copy.d.setX(XMAX / 2 + SIZE);

    	}
    	if(name == "z") {
    		copy.a.setX(XMAX / 2);
    		copy.b.setX(XMAX / 2 - SIZE);
    		copy.c.setX(XMAX / 2);
    		copy.c.setY(SIZE);
    		copy.d.setX(XMAX / 2 + SIZE);
    		copy.d.setY(SIZE);

    	}
    	if(name == "i") {
    		copy.a.setX(XMAX / 2 - SIZE - SIZE);
    		copy.b.setX(XMAX / 2 - SIZE);
    		copy.c.setX(XMAX / 2);
    		copy.d.setX(XMAX / 2 + SIZE);

    	}
    }
    
    public static Form makeRect() {
        int block = (int) (Math.random() * 100);
        String name;
        Rectangle a = new Rectangle(SIZE-1, SIZE-1), b = new Rectangle(SIZE-1, SIZE-1), c = new Rectangle(SIZE-1, SIZE-1),
                d = new Rectangle(SIZE-1, SIZE-1);
        if (block < 15) {
            a.setX(XMAX / 2 - SIZE);
            b.setX(XMAX / 2 - SIZE);
            b.setY(SIZE);
            c.setX(XMAX / 2);
            c.setY(SIZE);
            d.setX(XMAX / 2 + SIZE);
            d.setY(SIZE);
            name = "j";
        } else if (block < 30) {
            a.setX(XMAX / 2 + SIZE);
            b.setX(XMAX / 2 - SIZE);
            b.setY(SIZE);
            c.setX(XMAX / 2);
            c.setY(SIZE);
            d.setX(XMAX / 2 + SIZE);
            d.setY(SIZE);
            name = "l";
        } else if (block < 45) {
            a.setX(XMAX / 2 - SIZE);
            b.setX(XMAX / 2);
            c.setX(XMAX / 2 - SIZE);
            c.setY(SIZE);
            d.setX(XMAX / 2);
            d.setY(SIZE);
            name = "o";
        } else if (block < 60) {
            a.setX(XMAX / 2 + SIZE);
            b.setX(XMAX / 2);
            c.setX(XMAX / 2);
            c.setY(SIZE);
            d.setX(XMAX / 2 - SIZE);
            d.setY(SIZE);
            name = "s";
        } else if (block < 75) {
            a.setX(XMAX / 2 - SIZE);
            b.setX(XMAX / 2);
            c.setX(XMAX / 2);
            c.setY(SIZE);
            d.setX(XMAX / 2 + SIZE);
            name = "t";
        } else if (block < 90) {
            a.setX(XMAX / 2);
            b.setX(XMAX / 2 - SIZE);
            c.setX(XMAX / 2);
            c.setY(SIZE);
            d.setX(XMAX / 2 + SIZE);
            d.setY(SIZE);
            name = "z";
        } else {
            a.setX(XMAX / 2 - SIZE - SIZE);
            b.setX(XMAX / 2 - SIZE);
            c.setX(XMAX / 2);
            d.setX(XMAX / 2 + SIZE);
            name = "i";
        }
        return new Form(a, b, c, d, name);
    }

	
}