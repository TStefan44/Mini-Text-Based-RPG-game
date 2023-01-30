import java.util.Random;

interface Potion {
    void effect(Entity entity);
    int price();
    int regeneretion();
    int size();
}

class HealthPotion implements Potion {
    private int price;
    private int size;
    private int regenerateValue;

    public HealthPotion() {
        Random rand = new Random();
        regenerateValue = 25 + rand.nextInt(50);
        size = (int) (1 + 0.05 * regenerateValue);
        price = (int) (10 + 0.5 * regenerateValue);
    }

    @Override
    public void effect(Entity entity) {
        entity.lifeRecovery(regenerateValue);
    }

    @Override
    public int price() {
        return price;
    }

    @Override
    public int regeneretion() {
        return regenerateValue;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String toString() {
        return "Health Potion | " +
                "price = " + price +
                " | size = " + size +
                " | regenerateValue = " + regenerateValue;
    }
}

class ManaPotion implements Potion {
    private int price;
    private int size;
    private int regenerateValue;

    public ManaPotion() {
        Random rand = new Random();
        regenerateValue = 20 + rand.nextInt(60);
        size = (int) (1 + 0.02 * regenerateValue);
        price = (int) (10 + 0.6 * regenerateValue);
    }

    @Override
    public void effect(Entity entity) {
        entity.manaRecovery(regenerateValue);
    }

    @Override
    public int price() {
        return price;
    }

    @Override
    public int regeneretion() {
        return regenerateValue;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String toString() {
        return "Mana Potion | " +
                "price = " + price +
                " | size = " + size +
                " | regenerateValue = " + regenerateValue;
    }
}