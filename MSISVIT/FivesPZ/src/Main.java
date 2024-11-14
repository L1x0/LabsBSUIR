import java.util.ArrayList;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        ArrayList<Integer> list = generateRandomList();
        ArrayList<Integer> NegativeList = getNegativeValuesFromList(list);

        for (Integer integer : NegativeList) {
            System.out.println(integer);
        }
    }

    public static ArrayList<Integer> generateRandomList() {
        ArrayList<Integer> list = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 30; i++) {
            int randomValue = random.nextInt(201) - 100; // Диапазон от -100 до 100
            list.add(randomValue);
        }

        return list;
    }

    public static ArrayList<Integer> getNegativeValuesFromList(ArrayList<Integer> list) {
        ArrayList<Integer> negativeList = new ArrayList<>();
        for (Integer integer : list) {
            if (integer < 0) {
                negativeList.add(integer);
            }
        }

        while (negativeList.size() < 30) {
            negativeList.add(negativeList.size(), 0);
        }

        return negativeList;
    }

}