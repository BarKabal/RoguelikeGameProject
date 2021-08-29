package logic.gameData;

public class FOVCalculator {
    GameData gameData;

    public FOVCalculator(GameData gameData) {
        this.gameData = gameData;
    }

    public void CalculateFOV() {
        int posX = gameData.getCharacterData().getPositionX();
        int posY = gameData.getCharacterData().getPositionY();
        for (int i = -5; i <=5; i++) {
            BresenhamAlgorithm(posX, posY, posX + i, posY + 5);
            BresenhamAlgorithm(posX, posY, posX + i, posY - 5);
            if (i  != -5 && i != 5) {
                BresenhamAlgorithm(posX, posY, posX + 5, posY + i);
                BresenhamAlgorithm(posX, posY, posX - 5, posY + i);
            }
        }

    }

    private void BresenhamAlgorithm(int x1, int y1, int x2, int y2) {
        int x = x1;
        int y = y1;
        int dx = Math.abs(x2-x1);
        int dy = Math.abs(y2-y1);
        int s1 = sign(x2-x1);
        int s2 = sign(y2-y1);
        boolean interchange;
        boolean metWall = false;
        if (dy > dx) { //dx zawsze większe od dy (jeśli zamienione to interchange)
            int tmp = dx;
            dx = dy;
            dy = tmp;
            interchange = true;
        } else {
            interchange = false;
        }
        int e = 2*dy - dx; //pk, fraction*2
        int a = 2*dy;
        int b = 2*dy - 2*dx;
        for (int i =0; i < dx; i++) {
            if (e < 0) {
                if (interchange) {
                    y = y + s2;
                } else {
                    x = x + s1;
                }
                e = e + a;
            } else {
                y = y + s2;
                x = x + s1;
                e = e + b;
            }
            if (!metWall) {
                metWall = gameData.getMapData().getDungeonFloor(gameData.getCharacterData().getCurrentFloor()).getTileFromMap(x, y).markAsVisible();
            }
        }
    }

    private int sign(int num) {
        if (num < 0) {
            return -1;
        } else if (num > 0) {
            return 1;
        } else {
            return 0;
        }
    }
}
