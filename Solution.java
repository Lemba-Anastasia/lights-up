public class Solution {
    private final int x;
    private final int y;
    private int buttonIndex;
    private final int[][] matrixButton;

    public Solution(int x, int y, int[][] matrixButton, int buttonIndex) {
        this.x = x;
        this.y = y;
        this.matrixButton = matrixButton;
        this.buttonIndex = 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getButtonIndex() {
        return buttonIndex;
    }

    public int[][] getMatrixButton() {
        return matrixButton;
    }
}
