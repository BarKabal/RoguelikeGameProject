package logic.generation.room;

public class Room {
    public static int number = 0;
    public int id;
    public double height, width, xPos, yPos, size;
    public Vector vector;

    public Room(double height, double width, double xPos, double yPos) {
        this.id = number;
        this.height = height;
        this.width = width;
        this.xPos = xPos;
        this.yPos = yPos;
        this.size = Math.abs(xPos * yPos);
        this.vector = new Vector(0, 0);
        number++;
    }

    public void changeXPos(double xPos) {
        this.xPos = xPos;
    }

    public void changeYPos(double yPos) {
        this.yPos = yPos;
    }

    public double getxPos() {
        return xPos;
    }

    public double getyPos() {
        return yPos;
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public double getSize() {
        return size;
    }

    public int getId() {
        return id;
    }

    public void addVector(double X, double Y) {
        vector.setX(vector.getX() + X);
        vector.setY(vector.getY() + Y);
    }

    public void moveByVector() {
        xPos += vector.getX();
        yPos += vector.getY();
        vector.setX(0);
        vector.setY(0);
    }

    public boolean checkIfRoomOverlaps(Room room2) {
        double upper1, lower1, left1, right1, upper2, lower2, left2, right2;
        upper1 = this.getyPos() + this.getHeight()/2;
        lower1 = this.getyPos() - this.getHeight()/2;
        left1 = this.getxPos() - this.getWidth()/2;
        right1 = this.getxPos() + this.getWidth()/2;
        upper2 = room2.getyPos() + room2.getHeight()/2;
        lower2 = room2.getyPos() - room2.getHeight()/2;
        left2 = room2.getxPos() - room2.getWidth()/2;
        right2 = room2.getxPos() + room2.getWidth()/2;
        if (upper1 > upper2 && lower1 < upper2 || upper1 > lower2 && lower1 < lower2) { //corner of room2 in room1
            if (right1 > right2 && left1 < right2 || right1 > left2 && left1 < left2) {
                return true;
            }
        }
        if (upper2 > upper1 && lower2 < upper1 || upper2 > lower1 && lower2 < lower1) { //corner of room1 in room2
            if (right2 > right1 && left2 < right1 || right2 > left1 && left2 < left1) {
                return true;
            }
        }
        if (upper1 > upper2 && upper1 > lower2 && lower1 < upper2 && lower1 < lower2) { //rooms overlap in cross position |1 -2
            if (right1 < right2 && right1 > left2 && left1 < right2 && left1 > left2) {
                return true;
            }
        }
        if (upper2 > upper1 && upper2 > lower1 && lower2 < upper1 && lower2 < lower1) { //rooms overlap in cross position |2 -1
            if (right2 < right1 && right2 > left1 && left2 < right1 && left2 > left1) {
                return true;
            }
        }
        return false;
    }

    public boolean checkIfLineOverlaps(double x1, double y1, double x2, double y2) {
        double upper1 = this.getyPos() + this.getHeight() / 2;
        double lower1 = this.getyPos() - this.getHeight() / 2;
        double left1 = this.getxPos() - this.getWidth() / 2;
        double right1 = this.getxPos() + this.getWidth() / 2;
        double lineA = (y2-y1)/(x2-x1);
        double lineB = y2 - lineA*x2;
        double xLineUpper = (upper1-lineB)/lineA; //point at which the line intersects the upper point of rectangle
        double xLineLower = (lower1-lineB)/lineA;
        double yLineLeft = lineA*left1+lineB;
        double yLineRight = lineA*right1+lineB;
        if (y1 < yPos && yPos < y2 || y1 > yPos && yPos > y2) {
            if (x1 < xPos && xPos < x2 || x1 > xPos && xPos > x2) {
                if (xLineUpper > left1 && xLineUpper < right1) {
                    return true;
                }
                if (xLineLower > left1 && xLineLower < right1) {
                    return true;
                }
                if (yLineLeft > lower1 && yLineLeft < upper1) {
                    return true;
                }
                if (yLineRight > lower1 && yLineRight < upper1) {
                    return true;
                }
            }
        }
        return false;
    }
}
