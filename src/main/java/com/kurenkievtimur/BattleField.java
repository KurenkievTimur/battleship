package com.kurenkievtimur;

import java.util.Arrays;
import java.util.Scanner;

public class BattleField {
    private final char[][] field = new char[10][10];
    private final Ship[] ships = new Ship[5];

    public BattleField() {
        for (char[] chars : field) {
            Arrays.fill(chars, '~');
        }

        ships[0] = new Ship("Aircraft Carrier", 5);
        ships[1] = new Ship("Battleship", 4);
        ships[2] = new Ship("Submarine", 3);
        ships[3] = new Ship("Cruiser", 3);
        ships[4] = new Ship("Destroyer", 2);
    }

    public static void play(BattleField field1, BattleField field2) {
        System.out.println("Player 1, place your ships on the game field\n");
        field1.printBattle();
        for (Ship ship : field1.ships) {
            field1.enterCoordinates(ship);
            field1.printBattle();
        }
        System.out.println();
        changeMove();

        System.out.println("Player 2, place your ships to the game field\n");
        field2.printBattle();
        for (Ship ship : field2.ships) {
            field2.enterCoordinates(ship);
            field2.printBattle();
        }
        System.out.println();
        changeMove();

        do {
            field2.printFogBattle();
            System.out.println("---------------------");
            field1.printBattle();

            System.out.println("\nPlayer 1, it's your turn:\n");
            field2.takeShot();

            if (field2.checkIsLose())
                break;

            field1.printFogBattle();
            System.out.println("---------------------");
            field2.printBattle();

            System.out.println("\nPlayer 2, it's your turn:\n");
            field1.takeShot();
        } while (!field1.checkIsLose() && !field2.checkIsLose());

        System.out.println("You sank the last ship. You won. Congratulations!");
    }

    private void printBattle() {
        System.out.println("  1 2 3 4 5 6 7 8 9 10");
        for (int i = 0; i < field.length; i++) {
            System.out.printf("%s ", Character.toString(65 + i));
            for (int j = 0; j < field[i].length; j++) {
                System.out.printf("%s ", field[i][j]);
            }
            System.out.println();
        }
    }

    private void printFogBattle() {
        System.out.println("  1 2 3 4 5 6 7 8 9 10");
        for (int i = 0; i < field.length; i++) {
            System.out.printf("%s ", Character.toString(65 + i));
            for (int j = 0; j < field[i].length; j++) {
                if (field[i][j] == 'O') {
                    System.out.printf("%s ", "~");
                    continue;
                }
                System.out.printf("%s ", field[i][j]);
            }
            System.out.println();
        }
    }

    private void enterCoordinates(Ship ship) {
        Scanner scanner = new Scanner(System.in);
        System.out.printf("\nEnter the coordinates of the %s (%d cells):\n\n", ship.getName(), ship.getLength());

        int[] coordinates = new int[4];
        boolean isValid = false;
        do {
            String x = scanner.next();
            String y = scanner.next();

            try {
                coordinates[0] = x.charAt(0) - 65;
                coordinates[1] = Integer.parseInt(x.substring(1)) - 1;
                coordinates[2] = y.charAt(0) - 65;
                coordinates[3] = Integer.parseInt(y.substring(1)) - 1;

                isValid = isValidCoordinates(coordinates, ship);
            } catch (NumberFormatException e) {
                System.out.print("\nError! Wrong ship location! Try again:\n\n");
            } catch (IllegalArgumentException e) {
                System.out.printf("\n%s\n\n", e.getMessage());
            }
        } while (!isValid);

        fillBattle(coordinates);
        ship.setCoordinates(coordinates);
        System.out.println();
    }

    private boolean isValidCoordinates(int[] coordinates, Ship ship) throws IllegalArgumentException {
        if ((coordinates[0] == coordinates[2]) == (coordinates[1] == coordinates[3])) {
            throw new IllegalArgumentException("Error! Wrong ship location! Try again:");
        }
        if (Math.min(coordinates[0], coordinates[2]) < 0 || Math.min(coordinates[1], coordinates[3]) < 0) {
            throw new IllegalArgumentException("Error! Wrong ship location! Try again:");
        }
        if (Math.max(coordinates[0], coordinates[2]) > 9 || Math.max(coordinates[1], coordinates[3]) > 9) {
            throw new IllegalArgumentException("Error! Wrong ship location! Try again:");
        }
        if (Math.abs((coordinates[0] - coordinates[2]) + Math.abs(coordinates[1] - coordinates[3])) + 1 != ship.getLength()) {
            throw new IllegalArgumentException("Error! Wrong length of the %s! Try again:".formatted(ship.getName()));
        }

        return checkNeighbors(coordinates);
    }

    private boolean checkNeighbors(int[] coordinates) throws IllegalArgumentException {
        int minRow = Math.min(coordinates[0], coordinates[2]);
        int maxRow = Math.max(coordinates[0], coordinates[2]);
        int minColumn = Math.min(coordinates[1], coordinates[3]);
        int maxColumn = Math.max(coordinates[1], coordinates[3]);

        minRow = (minRow == 0 ? minRow : minRow - 1);
        maxRow = (maxRow == 9 ? maxRow : maxRow + 1);
        minColumn = (minColumn == 0 ? minColumn : minColumn - 1);
        maxColumn = (maxColumn == 9 ? maxColumn : maxColumn + 1);


        for (int i = minRow; i <= maxRow; i++) {
            for (int j = minColumn; j <= maxColumn; j++) {
                if (field[i][j] == 'O')
                    throw new IllegalArgumentException("Error! You places it too close to another one. Try again:");
            }
        }

        return true;
    }

    private void fillBattle(int[] coordinates) {
        int minRow = Math.min(coordinates[0], coordinates[2]);
        int maxRow = Math.max(coordinates[0], coordinates[2]);
        int minColumn = Math.min(coordinates[1], coordinates[3]);
        int maxColumn = Math.max(coordinates[1], coordinates[3]);

        for (int i = minRow; i <= maxRow; i++) {
            for (int j = minColumn; j <= maxColumn; j++) {
                field[i][j] = 'O';
            }
        }
    }

    private void takeShot() {
        Scanner scanner = new Scanner(System.in);

        int[] coordinates = new int[2];
        boolean isValid = false;
        do {
            try {
                String shot = scanner.next();
                coordinates[0] = shot.charAt(0) - 65;
                coordinates[1] = Integer.parseInt(shot.substring(1)) - 1;

                isValid = isValidCoordinates(coordinates);
            } catch (NumberFormatException e) {
                System.out.print("\nError! You entered the wrong coordinates! Try again:\n\n");
            } catch (IllegalArgumentException e) {
                System.out.printf("\n%s\n\n", e.getMessage());
            }
        } while (!isValid);

        System.out.println();

        if (hitShot(coordinates))
            printHitInfo(coordinates);
        else if (!checkIsLose()) {
            System.out.println("You missed. Try again:");
            changeMove();
        }
    }

    private boolean isValidCoordinates(int[] coordinates) throws IllegalArgumentException {
        if ((coordinates[0] < 0 || coordinates[1] < 0) || (coordinates[0] > 9 || coordinates[1] > 9)) {
            throw new IllegalArgumentException("Error! You entered the wrong coordinates! Try again:");
        }

        return true;
    }

    private boolean hitShot(int[] coordinates) {
        if (field[coordinates[0]][coordinates[1]] == 'O' || field[coordinates[0]][coordinates[1]] == 'X') {
            field[coordinates[0]][coordinates[1]] = 'X';
            return true;
        }

        field[coordinates[0]][coordinates[1]] = 'M';
        return false;
    }

    private void printHitInfo(int[] coordinates) {
        int row = coordinates[0];
        int column = coordinates[1];

        boolean left = checkLeftShipHealth(row, column == 0 ? column : column - 1);
        boolean right = checkRightShipHealth(row, column == 9 ? column : column + 1);
        boolean top = checkTopShipHealth(row == 0 ? row : row - 1, column);
        boolean bottom = checkBottomShipHealth(row == 9 ? row : row + 1, column);

        if ((left || right || top || bottom) && !checkIsLose()) {
            System.out.println("You hit a ship! Try again:");
            changeMove();
        } else if (!checkIsLose()) {
            System.out.println("You sank a ship! Specify a new target:");
            changeMove();
        }
    }

    private boolean checkLeftShipHealth(int row, int column) {
        while (column > 0 && field[row][column] != '~' && field[row][column] != 'M') {
            if (field[row][column--] == 'O') {
                return true;
            }
        }

        return false;
    }

    private boolean checkRightShipHealth(int row, int column) {
        while (column < 10 && field[row][column] != '~' && field[row][column] != 'M') {
            if (field[row][column++] == 'O') {
                return true;
            }
        }

        return false;
    }

    private boolean checkTopShipHealth(int row, int column) {
        while (row > 0 && field[row][column] != '~' && field[row][column] != 'M') {
            if (field[row--][column] == 'O') {
                return true;
            }
        }

        return false;
    }

    private boolean checkBottomShipHealth(int row, int column) {
        while (row < 10 && field[row][column] != '~' && field[row][column] != 'M') {
            if (field[row++][column] == 'O') {
                return true;
            }
        }

        return false;
    }

    private static void changeMove() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Press Enter and pass the move to another player");
        scanner.nextLine();
    }

    private boolean checkIsLose() {
        for (Ship ship : ships) {
            int[] coordinates = ship.getCoordinates();
            for (int i = Math.min(coordinates[0], coordinates[2]); i <= Math.max(coordinates[0], coordinates[2]); i++) {
                for (int j = Math.min(coordinates[1], coordinates[3]); j <= Math.max(coordinates[1], coordinates[3]); j++) {
                    if (field[i][j] == 'O') {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}