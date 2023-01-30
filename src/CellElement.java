import java.util.ArrayList;
import java.util.Random;

interface CellElement {
    char toCharacter();
}

class Empty implements CellElement {
    @Override
    public char toCharacter() {
        return 'N';
    }
}

class Finish implements CellElement {
    @Override
    public char toCharacter() {
        return 'F';
    }
}

class Shop implements CellElement {
    private ArrayList<Potion> potions;

    public Shop() {
        //initialize potions list with 2-4 potions
        potions = new ArrayList<>();
        Random rand = new Random();
        int size = 2 + rand.nextInt(3);
        boolean type;

        for (int i = 0; i < size; i++) {
            //choose randomly potion type
            type = rand.nextBoolean();
            if (type) {
                potions.add(new HealthPotion());
            } else {
                potions.add((new ManaPotion()));
            }
        }
    }

    /*
        Return the potion found at the index position or null if the
        index is out of list size bounds or the list is uninitialized.
        If the potion does exist in the list, it is removed.
     */
    protected Potion buyPotion(int index) {
        int size = potions.size();
        if (index < 0 || index >= size) return null;
        return potions.remove(index);
    }

    @Override
    public String toString() {
        String string = "";
        for (int i = 0; i < potions.size(); i++) {
            string = string.concat(i + " : " + potions.get(i) + "\n");
        }
        return string;
    }

    public void addPotion(Potion potion) {
        potions.add(potion);
    }

    @Override
    public char toCharacter() {
        return 'S';
    }

    public ArrayList<Potion> getPotions() {
        return potions;
    }
}
