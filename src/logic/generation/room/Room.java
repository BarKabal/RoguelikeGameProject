package logic.generation.room;

public class Room {
    public double height, width, xPos, yPos, size;
    public Vector vector;

    public Room(double height, double width, double xPos, double yPos) {
        this.height = height;
        this.width = width;
        this.xPos = xPos;
        this.yPos = yPos;
        this.size = xPos*yPos;
        this.vector = new Vector(0,0);
    }

    public boolean checkIfRoomOverlaps (Room room) {
        if ((room.width/2 + this.width/2) > Math.abs(this.xPos - room.xPos))
            return true;
        else if ((room.height/2 + this.height/2) > Math.abs(this.yPos - room.yPos))
            return true;
        else
            return false;
    }

    public void changeXPos(int xPos) {
        this.xPos = xPos;
    }

    public void changeYPos(int yPos) {
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
}
