import java.util.Random;

class Enemy extends Entity implements CellElement{
    /*
        Randomly choose enemy fields (mana, life and secondary attributes
     */
    public Enemy() {
        super(new Random().nextInt(51) + 50,
                new Random().nextInt(51) + 50);
        Random rand = new Random();
        fireProtection = rand.nextBoolean();
        iceProtection = rand.nextBoolean();
        earthProtection = rand.nextBoolean();
    }

    @Override
    public char toCharacter() {
        return 'E';
    }

    @Override
    public boolean receiveDamage(int value) {
        currentLife -= value;
        return currentLife < 0;
    }

    @Override
    public int getDamage() {
        Random rand = new Random();
        int damage = 10 + rand.nextInt(10);
        if (rand.nextBoolean()) return damage*2;
        return damage;
    }

    @Override
    public void accept(Spell v) {
        v.visit(this);
    }
}
