import socket


class Board:
    """
    An object to represent a game board
    The client doesn't need the Board object as all the logic is done in the server
    Anytime there is a variable r or c, they refer to rows or columns
    """
    def __init__(self, copy=None):
        """
        Constructor. By default boards are 6 rows x 7 columns
        """
        # creates a 6x7 board indexed by self.board[row][column]
        self._board = [['_' for _ in range(7)] for _ in range(6)]
        if copy is not None:
            for r in range(6):
                for c in range(7):
                    self._board[r][c] = copy._board[r][c]
        self._last = (-1, -1)  # used later to track the (r, c) of the last dropped token

    def __str__(self):
        """
        Method to turn the object into a string.
        :return: String version of the board
        """
        rtn_str = '  0 1 2 3 4 5 6\n'
        for r in range(6):
            rtn_str += '| '
            for c in range(7):
                rtn_str += f'{self._board[r][c]} '
            rtn_str += '|\n'
        return rtn_str

    def drop(self, column, player):
        """
        The method to drop a token at a particular column
        :param column: int of the column to drop into
        :param player: string of the player
        :return: True if successful, False if invalid
        """
        if 0 <= column <= 6 and len(player) == 1:  # Check for out of bounds or invalid player
            for r in range(5, -1, -1):
                if self._board[r][column] == '_':  # empty space, start looking from the bottom up
                    self._board[r][column] = player
                    self._last = (r, column)
                    return True
        return False

    def win_check(self, check_all=False):
        """
        The method to check for a win.
        By default, it only checks the most recent token dropped
        :param check_all: the option to check the whole board
        :return:
        """
        if check_all:
            pass
        else:
            # Check horizontally
            count = 1
            count += self._count_equal(self._last, (-1, 0))  # check left
            count += self._count_equal(self._last, (1, 0))  # check right
            if count >= 4:
                r, c = self._last
                return self._board[r][c]

            # Check vertically
            count = 1
            count += self._count_equal(self._last, (0, 1))  # check up
            count += self._count_equal(self._last, (0, -1))  # check down
            if count >= 4:
                r, c = self._last
                return self._board[r][c]

            # Check diagonally
            count = 1
            count += self._count_equal(self._last, (1, 1))  # check up and right
            count += self._count_equal(self._last, (-1, -1))  # check down and left
            if count >= 4:
                r, c = self._last
                return self._board[r][c]

            # Check vertically
            count = 1
            count += self._count_equal(self._last, (-1, 1))  # check up and left
            count += self._count_equal(self._last, (1, -1))  # check down and right
            if count >= 4:
                r, c = self._last
                return self._board[r][c]
            return None

    def _count_equal(self, start, deltas):
        """
        A private method to count moving in one direction how many tokens match
        the token at start
        :param start: a tuple of (r, c); where to start
        :param deltas: a tuple of (d_r, d_c) where d_r and d_c are the deltas for rows and columns
        :return: int count of how many matching tokens exist in that direction
        """
        r, c = start
        d_r, d_c = deltas
        player = self._board[r][c]
        count = 0
        while True:
            r += d_r
            c += d_c
            try:
                if self._board[r][c] == player:
                    count += 1
                else:
                    break
            except IndexError:
                break
        return count


def main():
    turn = 0
    players = 'XO'
    gameboard = Board()
    while True:
        turn += 1
        print(f"Turn {turn}: Player {players[turn%2]}'s turn:")
        print(gameboard)
        try:
            column = int(input('Drop a token at: '))
            if not (gameboard.drop(column, players[turn%2])):
                raise ValueError
        except ValueError:
            turn -= 1
            print('Please select a valid column')
        winner = gameboard.win_check()
        if winner is not None:
            print(f'The winner is {winner}!\n{gameboard}')
            break


if __name__ == '__main__':
    main()
