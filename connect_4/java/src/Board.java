//todo add comments
public class Board {
    protected final char[][] gameboard = new char[6][7];
    private int last_r;
    private int last_c;

    public Board() {
        for (int r=0; r <= 5; r++) {
            for (int c=0; c <= 6; c++) {
                gameboard[r][c] = '_';
            }
        }
    }

    private Board(Board copy) {
        for (int r=0; r < 6; r++) {
            System.arraycopy(copy.gameboard[r], 0, gameboard[r], 0, 8);
        }
        last_c = copy.last_c;
        last_r = copy.last_r;
    }

    @Override
    public String toString() {
        StringBuilder rtnstr = new StringBuilder("  0 1 2 3 4 5 6\n");
        for (int r=0; r <= 5; r++) {
            rtnstr.append("| ");
            for (int c=0; c <= 6; c++) {
                rtnstr.append(gameboard[r][c]);
                rtnstr.append(' ');
            }
            rtnstr.append("|\n");
        }
        return  rtnstr.toString();
    }

    public boolean drop(int col, char player) {
        if (0 <= col && col <= 6) {
            for(int r=5; r>=0; r--){
                if (gameboard[r][col] == '_') {  //check for empty space
                    gameboard[r][col] = player;
                    last_r = r;
                    last_c = col;
                    return true;
                }
            }
        }
        return false;
    }

    public char win_check() {
        int count;

        //Check horizontally
        count = 1;
        count += count_equal(last_r, last_c, -1, 0); //Check left
        count += count_equal(last_r, last_c, 1, 0); //Check right
        if (count >= 4) {
            return gameboard[last_r][last_c];
        }

        //Check vertically
        count = 1;
        count += count_equal(last_r, last_c, 0, 1); //Check up
        count += count_equal(last_r, last_c, 0, -1); //Check down
        if (count >= 4) {
            return gameboard[last_r][last_c];
        }

        //Check diagonally /
        count = 1;
        count += count_equal(last_r, last_c, 1, 1); //Check up and right
        count += count_equal(last_r, last_c, -1, -1); //Check down and left
        if (count >= 4) {
            return gameboard[last_r][last_c];
        }

        //Check diagonally \
        count = 1;
        count += count_equal(last_r, last_c, -1, 1); //Check up and left
        count += count_equal(last_r, last_c, 1, -1); //Check down and right
        if (count >= 4) {
            return gameboard[last_r][last_c];
        }

        return '\0';  //return null char if none are found
    }

    private int count_equal(int start_r, int start_c, int delta_r, int delta_c) {
        char player = gameboard[start_r][start_c];
        int count = 0;
        int row = start_r;
        int col = start_c;

        while(true) {
            row += start_r;
            col += start_c;
            try {
                if (gameboard[row][col] == player) {
                    count += 1;
                } else {
                    break;
                }
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }

        return count;
    }
}
