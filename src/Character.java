import java.util.ArrayList;
import java.util.Random;

abstract class Character extends Entity {
    protected String name;
    protected int Ox;
    protected int Oy;
    protected Inventory inventory;
    protected int exp;
    protected int level;
    protected int strength;
    protected int charisma;
    protected int dexterity;

    protected int cumulatedExp = 0;
    protected int enemyKills = 0;
    protected int cumulatedMoney = 0;

    public Character(String name, int exp, int level, int strength, int charisma, int dexterity,
                     boolean fireProtection, boolean earthProtection, boolean iceProtection) {
        this(strength, charisma, dexterity,
                fireProtection, earthProtection, iceProtection);
        this.name = name;
        this.exp = exp;
        this.level = level;
    }

    public Character(int strength, int charisma, int dexterity,
                     boolean fireProtection, boolean earthProtection,
                     boolean iceProtection) {
        this.strength = strength;
        this.charisma = charisma;
        this.dexterity = dexterity;
        this.earthProtection = earthProtection;
        this.fireProtection = fireProtection;
        this.iceProtection = iceProtection;
    }

    /*
        Add potion in character's inventory.
        Return false on failure (inventory is null or the is not enough gold)
     */
    protected boolean buy(Potion potion) {
        if (inventory == null) return false;
        if (inventory.getGold() - potion.price() >= 0)
            return inventory.addPotion(potion);
        return false;
    }

    /*
        Rise stats when character level up
     */
    protected abstract void riseStats();
    public abstract String getVocation();

    public void accept(Spell v) {
        v.visit(this);
    }

    /*
        Level up character when the value of exp is high enough.
        The surplus exp is kept and the stats are risen.
        Return true on level up.
     */
    public boolean levelUp() {
        int cap = (int) (Math.pow(level - 1, 2) * 10 + 100);
        if (exp >= cap) {
            level++;
            exp = cap - exp;
            riseStats();
            return true;
        }
        return false;
    }

    public void addExp(int value) {
        cumulatedExp += value;
        exp += value;
        levelUp();
    }

    public void addMoney(int value) {
        cumulatedMoney += value;
        inventory.addGold(value);
    }

    public void addEnemyKills() {
        enemyKills++;
    }

    public String getName() {
        return name;
    }

    public int getOx() {
        return Ox;
    }

    public int getOy() {
        return Oy;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getExp() {
        return exp;
    }

    public int getLevel() {
        return level;
    }

    public int getStrength() {
        return strength;
    }

    public int getCharisma() {
        return charisma;
    }

    public int getDexterity() {
        return dexterity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOx(int ox) {
        Ox = ox;
    }

    public void setOy(int oy) {
        Oy = oy;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}

class Inventory {
    private ArrayList<Potion> potions;
    private int maximInventory;
    private int gold;

    public Inventory(ArrayList<Potion> potions, int maximInventory, int gold) {
        this.potions = potions;
        this.maximInventory = maximInventory;
        this.gold = gold;
    }

    public Inventory(int maximInventory, int gold) {
        this(new ArrayList<Potion>(), maximInventory, gold);
    }

    public Inventory(int maximInventory) {
        this(maximInventory, 100);
    }

    /*
        Add potion in list if there is enough space in inventory.
        Return true on success.
     */
    protected boolean addPotion(Potion potion) {
        if (potions == null || remainingInventory() <= potion.size())
            return false;
        gold -= potion.price();
        potions.add(potion);
        return true;
    }

    /*
        Remove potion from inventory.
     */
    public boolean removePotion(Potion potion) {
        if (potions == null) return false;
        return potions.remove(potion);
    }

    /*
        Calculate remaining space(size) in inventory.
     */
    protected int remainingInventory() {
        //check if inventory is empty
        if(potions == null) return maximInventory;

        //calculate sum of all potions size
        int currentSize = 0;
        for (int i = 0; i < potions.size(); i++) {
            currentSize += potions.get(i).size();
        }
        return maximInventory - currentSize;
    }

    public void increaseInventory(int value) {
        if (value < 0) return;
        maximInventory += value;
    }

    public void addGold(int value) {
        gold += value;
    }

    public ArrayList<Potion> getPotions() {
        return potions;
    }

    public int getGold() {
        return gold;
    }

    @Override
    public String toString() {
        String string = new String("Inventory :\n");
        for (int i = 0; i < potions.size(); i++) {
            string =string.concat("\tPotion " + i + potions.get(i) + "\n");
        }
        string =string.concat("\tGold " + gold + "\n");
        string = string.concat("\tMaximum inventory " + maximInventory);
        return  string;
    }
}

class Warrior extends Character {

    public Warrior(String name, int lv, int exp) {
        super(name, exp, lv,
                25 + 2 * lv, 10 + lv, 10 + lv,
                true, false, false);
        inventory = new Inventory(100);
    }

    protected void riseStats() {
        strength += 5;
        charisma += 1;
        dexterity += 1;
        maxLife += 10;
        maxMana += 5;
        inventory.increaseInventory(10);
    }

    @Override
    public boolean receiveDamage(int value) {
        currentLife -= value;
        if (currentLife < 0) return true;
        return false;
    }

    @Override
    public int getDamage() {
        int damage = (int) (10 + strength + level * (charisma + dexterity) * 0.1);
        if (new Random().nextInt(100) < strength)
            damage *= 2;
        return damage;
    }

    @Override
    public String toString() {
        return "Class Warrior | Name: " + name +
                " | Level: " + level + " | Exp: " + exp;
    }

    @Override
    public String getVocation() {
        return "Warrior";
    }
}

class Mage extends Character {

    public Mage(String name, int lv, int exp) {
        super(name, exp, lv,
                10 + lv, 25 + 2 * lv, 10 + lv,
                false, false, true);
        inventory = new Inventory(50);
    }

    protected void riseStats() {
        strength += 1;
        charisma += 5;
        dexterity += 1;
        maxLife += 5;
        maxMana += 10;
        inventory.increaseInventory(5);
    }


    @Override
    public boolean receiveDamage(int value) {
        currentLife -= value;
        if (currentLife < 0) return true;
        return false;
    }

    @Override
    public int getDamage() {
        int damage = (int) (10 + charisma + level * (strength + dexterity) * 0.1);
        if (new Random().nextInt(100) < charisma)
            damage *= 2;
        return damage;
    }

    @Override
    public String toString() {
        return "Class Mage | Name: " + name +
                " | Level: " + level + " | Exp: " + exp;
    }

    @Override
    public String getVocation() {
        return "Mage";
    }
}

class Rogue extends Character {

    public Rogue(String name, int lv, int exp) {
        super(name, exp, lv,
                10 + lv, 10 + lv, 25 + 2 * lv,
                false, true, false);
        inventory = new Inventory(75);
    }

    protected void riseStats() {
        strength += 1;
        charisma += 1;
        dexterity += 5;
        maxLife += 7;
        maxMana += 7;
        inventory.increaseInventory(7);
    }


    @Override
    public boolean receiveDamage(int value) {
        currentLife -= value;
        if (currentLife < 0) return true;
        return false;
    }

    @Override
    public int getDamage() {
        int damage = (int) (10 + dexterity + level * (charisma + strength) * 0.1);
        if (new Random().nextInt(100) < dexterity)
            damage *= 2;
        return damage;
    }

    @Override
    public String toString() {
        return "Class Rogue | Name: " + name +
                " | Level: " + level + " | Exp: " + exp;
    }

    @Override
    public String getVocation() {
        return "Rogue";
    }
}

class CharacterFactory {
    private static CharacterFactory factory = null;

    private  CharacterFactory(){}

    public static CharacterFactory getFactory() {
        if (factory == null)
            factory = new CharacterFactory();
        return factory;
    }

    public Character factory(CharacterEnum characterClass, int lv, int exp, String name) {
        switch (characterClass) {
            case WARRIOR: return new Warrior(name, lv, exp);
            case MAGE: return new Mage(name, lv, exp);
            case ROGUE: return new Rogue(name, lv, exp);
            default: return null;
        }
    }
}

enum CharacterEnum {WARRIOR, MAGE, ROGUE}