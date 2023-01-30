import java.util.ArrayList;
import java.util.Random;

abstract class Entity {
    protected ArrayList<Spell> spells;
    protected int currentLife;
    protected int maxLife;
    protected int currentMana;
    protected int maxMana;
    protected boolean fireProtection;
    protected boolean iceProtection;
    protected boolean earthProtection;

    public Entity(int maxLife, int maxMana) {
        this.maxLife = maxLife;
        this.maxMana = maxMana;
        currentMana = maxMana;
        currentLife = maxLife;
        generateSpells(2 + new Random().nextInt(3));
    }

    public Entity() {
        this(100, 100);
    }

    /*
        Generate randomly spells for entity.
     */
    protected void generateSpells(int number) {
        spells = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < number; i++) {
            switch (rand.nextInt(3)) {
                case 0: spells.add(new Ice()); break;
                case 1: spells.add(new Fire()); break;
                case 2: spells.add(new Earth());
            }
        }
    }

    protected void lifeRecovery(int value) {
        if (currentLife + value <= maxLife)
            currentLife += value;
        else currentLife = maxLife;
    }

    protected void manaRecovery(int value) {
        if (currentMana + value <= maxMana)
            currentMana += value;
        else currentMana = maxMana;
    }

    /*
        Use spell on enemy and reduce mana
     */
    protected boolean useSpell(Spell spell, Entity enemy) {
        if (currentMana - spell.getManaCost() >= 0) {
            enemy.accept(spell);
            currentMana -= spell.getManaCost();
            return true;
        }
        return false;
    }

    public boolean isDead() {
        if (currentLife < 0) return true;
        return false;
    }

    public abstract void accept(Spell v);
    public abstract boolean receiveDamage(int value);
    public abstract int getDamage();
}

abstract class Spell {
    protected int damage;
    protected int manaCost;

    public Spell() {
        damage = new Random().nextInt(40) + 10;
        manaCost = (int) (1.75 * damage);
    }

    public int getDamage() {
        return damage;
    }

    public int getManaCost() {
        return manaCost;
    }

    abstract public void visit(Character c);
    abstract public void visit(Enemy e);
}

class Ice extends Spell{

    @Override
    public void visit(Character c) {
        if (c.iceProtection && new Random().nextBoolean()){
            c.receiveDamage(damage/2);
        } else {
            c.receiveDamage(damage);
        }
    }

    @Override
    public void visit(Enemy e) {
        if (e.iceProtection && new Random().nextBoolean()){
            e.receiveDamage(damage/2);
        } else {
            e.receiveDamage(damage);
        }
    }

    @Override
    public String toString() {
        return "Ice | damage " + damage + " | mana cost = " + manaCost ;
    }
}

class Fire extends Spell{
    @Override
    public void visit(Character c) {
        if (c.fireProtection && new Random().nextBoolean()){
            c.receiveDamage(damage/2);
        } else {
            c.receiveDamage(damage);
        }
    }

    @Override
    public void visit(Enemy e) {
        if (e.fireProtection && new Random().nextBoolean()){
            e.receiveDamage(damage/2);
        } else {
            e.receiveDamage(damage);
        }
    }

    @Override
    public String toString() {
        return "Fire | damage " + damage + " | mana cost = " + manaCost ;
    }
}

class Earth extends Spell{
    @Override
    public void visit(Character c) {
        if (c.earthProtection && new Random().nextBoolean()){
            c.receiveDamage(damage/2);
        } else {
            c.receiveDamage(damage);
        }
    }

    @Override
    public void visit(Enemy e) {
        if (e.earthProtection && new Random().nextBoolean()){
            e.receiveDamage(damage/2);
        } else {
            e.receiveDamage(damage);
        }
    }

    @Override
    public String toString() {
        return "Earth | damage " + damage + " | mana cost = " + manaCost ;
    }
}