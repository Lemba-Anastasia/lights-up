import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    private static int depth;

    public static void main(String[] args) throws IOException {
        System.out.println("Choose file from list: 1,2,3,4,5,6,7,8,9,10 and put enter");
        Scanner scanNumberOfFile = new Scanner(System.in);
        int numberOfFile = scanNumberOfFile.nextInt();
        FileReader fr = new FileReader(String.format("%d.txt", numberOfFile));
        Scanner scan = new Scanner(fr);
        depth = Integer.parseInt(scan.nextLine());

        String[] rowsInInputMatrix = scan.nextLine().split(",");
        //init input matrix
        int inputMatrix[][] = new int[rowsInInputMatrix.length][rowsInInputMatrix[0].length()];
        for (int i = 0; i < rowsInInputMatrix.length; i++) {
            for (int j = 0; j < rowsInInputMatrix[0].length(); j++) {
                inputMatrix[i][j] = Integer.parseInt(rowsInInputMatrix[i].split("")[j]);
            }
        }

        String[] switchesMatrixString = scan.nextLine().split(" ");
        System.out.println("switchesMatrixString: " + "");
        //init switches matrix
        int countOfSwitches = switchesMatrixString.length;
        List<int[][]> switches = new ArrayList<>();
        for (int i = 0; i < countOfSwitches; i++) {
            int rangRowOfSwitchesMatrix = switchesMatrixString[i].split(",").length;
            System.out.println("rangRowOfSwitchesMatrix" + rangRowOfSwitchesMatrix);
            int rangColumnOfSwitchesMatrix = switchesMatrixString[i].split(",")[0].split("").length;
            System.out.println("rangColumnOfSwitchesMatrix" + rangColumnOfSwitchesMatrix);
            int[][] matrixButton = new int[rangRowOfSwitchesMatrix][rangColumnOfSwitchesMatrix];
            char[][][] switchesChar = new char[countOfSwitches][rangRowOfSwitchesMatrix][rangColumnOfSwitchesMatrix];
            //
            for (int j = 0; j < rangRowOfSwitchesMatrix; j++) {
                for (int z = 0; z < rangColumnOfSwitchesMatrix; z++) {
                    switchesChar[i][j][z] = switchesMatrixString[i].split(",")[j].charAt(z);
                    System.out.print(switchesChar[i][j][z]);
                    if (switchesChar[i][j][z] == 'X') {
                        matrixButton[j][z] = 1;
                    } else {
                        matrixButton[j][z] = 0;
                    }
                }
            }
            switches.add(matrixButton);
            System.out.print("\n");
        }


        List<String> solve = solvePuzzle(inputMatrix, switches);
        Stream.of(solve).forEach(System.out::println);
        fr.close();
    }

    private static List<String> solvePuzzle(int[][] inputMatrix, List<int[][]> switches) {
        for (List<Solution> listOfPotentialSolution : getListOfPotentialSolutions(inputMatrix, switches)) {
            int[][] zeroMatrix = new int[inputMatrix.length][inputMatrix[0].length];
            for (Solution potentialSolution : listOfPotentialSolution) {
                for (int i = 0; i < inputMatrix.length; i++) {
                    for (int j = 0; j < inputMatrix[0].length; j++) {
                        zeroMatrix[i][j] += potentialSolution.getMatrixButton()[i][j];
                    }
                }
                if (isZeroMatrix(zeroMatrix) ) {
                    List<String> s = new ArrayList<>();
                    return listOfPotentialSolution.stream().
                            map((a) -> (a.getX() + "," + a.getY())).collect(Collectors.toList());
                }
            }

        }
        return Collections.singletonList("no solution");
    }

    static List<List<Solution>> getListOfPotentialSolutions(int[][] initialMatrix, List<int[][]> buttons) {
        List<List<Solution>> potentialSolutions = new ArrayList<>();
        HashMap<Integer, Integer> headOfVariationIndex = new HashMap();
        int variationIndex = 0;

        List<List<Solution>> solutions = new ArrayList(buttons.size());
        for (int iButton = 0; iButton < buttons.size(); iButton++) {
            solutions.add(variationsOfButton(buttons.get(iButton), initialMatrix, iButton));
            headOfVariationIndex.put(iButton, variationIndex);
        }

        int sumOfFirstElement = initialMatrix[0][0];
        Solution potentialSolution;
        List<Solution> currentPotentialSolutions = new ArrayList<>();

        for (int buttonIndex = 0; buttonIndex <= buttons.size(); ) {
            if (buttonIndex == buttons.size()) {
                if (headOfVariationIndex.get(buttonIndex - 2) == solutions.get(buttonIndex - 2).size() - 1)
                    break;
            } else if (headOfVariationIndex.get(buttonIndex) >= solutions.get(buttonIndex).size() - 1) {
                headOfVariationIndex.put(buttonIndex, 0);
                buttonIndex++;
            }
            for (int headOfButton = 0; ; headOfButton++) {
                variationIndex = headOfVariationIndex.get(headOfButton);
                if (buttonIndex == headOfButton && headOfVariationIndex.get(headOfButton) < solutions.get(headOfButton).size() - 1) {
                    if (headOfButton == 0)
                        headOfVariationIndex.put(headOfButton, variationIndex + 1);
                    else if (headOfVariationIndex.get(headOfButton - 1) == solutions.get(headOfButton - 1).size() - 1) {
                        headOfVariationIndex.put(headOfButton, variationIndex + 1);
                        clearAllPreviousHeadersOfVariation(headOfVariationIndex, headOfButton);
                    }
                }
                if (buttonIndex > headOfButton && headOfVariationIndex.get(headOfButton) < solutions.get(headOfButton).size() - 1) {
                    headOfVariationIndex.put(headOfButton, variationIndex + 1);
                }
                potentialSolution = ((Solution) solutions.get(headOfButton).get(variationIndex));
                currentPotentialSolutions.add(potentialSolution);
                sumOfFirstElement += potentialSolution.getMatrixButton()[0][0];
                if (headOfButton == solutions.size() - 1) {
                    if (sumOfFirstElement == depth || sumOfFirstElement == 0) {
                        potentialSolutions.add(currentPotentialSolutions);
                    }
                    currentPotentialSolutions = new ArrayList<>();

                    sumOfFirstElement = initialMatrix[0][0];
                    break;
                }
            }
        }
        System.out.print("First potential solutions\n");
        return potentialSolutions;
    }

    static List<Solution> variationsOfButton(int[][] smallButton, int[][] initialMatrix, int numberOfButton) {
        //counting combinations
        int countVariationsPerRow = getCountOfCombinationsInOneSide(initialMatrix.length, smallButton.length);
        int countVariationsPerColumn = getCountOfCombinationsInOneSide(initialMatrix[0].length, smallButton[0].length);
        List<Solution> listOfVariationsOfButton = new ArrayList<>();

        ///=mapping variation of small buttons-matrices to big-initial
        for (int rowHead = 0; rowHead < countVariationsPerRow; rowHead++) {
            for (int columnHead = 0; columnHead < countVariationsPerColumn; columnHead++) {
                int[][] mappedMatrix = new int[initialMatrix.length][initialMatrix[0].length];
                int currentI = rowHead;
                for (int iSmall = 0; iSmall < smallButton.length; iSmall++) {
                    int currentG = columnHead;
                    for (int jSmall = 0; jSmall < smallButton[0].length; jSmall++) {
                        mappedMatrix[currentI][currentG] = smallButton[iSmall][jSmall];
                        currentG++;
                    }
                    currentI++;
                }
                listOfVariationsOfButton.add(new Solution(rowHead, columnHead, mappedMatrix, numberOfButton));
            }
        }
        return listOfVariationsOfButton;
    }

    private static HashMap<Integer, Integer> clearAllPreviousHeadersOfVariation(HashMap<Integer, Integer> headOfVariationIndex, int headOfButton) {
        for (int i = 0; i < headOfButton; i++) {
            headOfVariationIndex.put(i, 0);
        }
        return headOfVariationIndex;
    }

    private static boolean isZeroMatrix(int[][] matrix) {
        for (int[] array : matrix) {
            for (int i : array) {
                if (i == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private static int getCountOfCombinationsInOneSide(int longSide, int shortSide) {
        int combinations = 0;
        for (int i = 0; i < longSide; i++) {
            if (shortSide + i <= longSide) {
                combinations++;
            } else {
                break;
            }
        }
        return combinations;
    }
}
