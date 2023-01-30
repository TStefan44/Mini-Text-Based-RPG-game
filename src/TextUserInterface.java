import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

interface UserInterface {

    Account login();
    Character chooseCharacter(Account account);
    boolean run();
    void finish();
    void shop(Shop shop, Character currentCharacter);
    void showStory(String story);
    boolean enemy(Enemy enemy, Character currentCharacter);
}

class TextUserInterface implements UserInterface{
    private Scanner in = null;

    private static TextUserInterface singleton = null;

    private TextUserInterface() {}

    private TextUserInterface(Scanner in) {
        this.in = in;
    }

    public static TextUserInterface getSingleton() {
        if (singleton == null)
            singleton = new TextUserInterface();
        return singleton;
    }

    public static TextUserInterface getSingleton(Scanner in) {
        if (singleton == null)
            singleton = new TextUserInterface(in);
        return singleton;
    }

    /*
        Login for text user interface.
        The account is searched by credentials given by user.
     */
    public Account login() {
        String email;
        String password;
        Account account;

        in.nextLine();
        System.out.println("Login stage");
        while (true) {
            //read email and password
            System.out.print("Please insert email : ");
            email = in.nextLine();
            System.out.print("Please insert password : ");
            password = in.nextLine();

            if (email.equals("exit")) {
                return null;
            }

            //Search account by credentials
            account = Game.getSingleton().verify(email, password);
            if (account != null) {
                System.out.println("Login Success\n");
                return account;
            }

            //Account not found
            System.out.println("\nPassword or email incorrect");
            System.out.println("Please retry\n");
        }
    }

    /*
        Text interface for choosing character given an account
     */
    public Character chooseCharacter(Account account) {
        int option;
        while(true) {
            System.out.println("\n1 - to show Account");
            System.out.println("2 - to choose Character");
            System.out.println("3 - to exit\n");
            System.out.print("Option = ");
            option = in.nextInt();
            switch (option) {
                case 1:
                    //Print account
                    System.out.println(account);
                    break;
                case 2:
                    //Choose character
                    System.out.println("Choose by number in show in list");
                    System.out.print("Character = ");
                    int number = in.nextInt();
                    return account.getCharacters().get(number);
                case 3:
                    //Exit without choosing
                    return  null;
                default:
                    //Wrong input, retry
                    System.out.println("Wrong input\n");
            }
        }
    }

    /*
        login + choose character + generate map
        Return true on success
     */
    public boolean run() {
        //choose account
        Account account = login();
        if (account == null) {
            System.out.println("Login failed\n");
            return false;
        }

        //choose character
        Character character = chooseCharacter(account);
        if (character == null) {
            System.out.println("Character selection failed\n");
            return false;
        }

        //Generate map
        System.out.println(character);
        System.out.print("\nPlease insert size of map\nOx = ");
        int height = in.nextInt();
        System.out.print("Oy = ");
        int width = in.nextInt();
        Grid.getSingleton(width, height).setCurrentCharacter(character);
        //Grid.generateMap(width, height);
        Grid.makeTestMap();
        return true;
    }

    /*
        Finish text interface
     */
    public void finish() {
        System.out.println("Good job! You've reached the end somehow!");
        in.close();
    }

    /*
        Shop text interface.
     */
    @Override
    public void shop(Shop shop, Character currentCharacter) {
        while(true) {
            System.out.println("You can buy potion");
            System.out.println("0 to leave shop");
            System.out.println("1 to buy potion");
            System.out.println("2 to show inventory\n");
            System.out.print(shop + "\n");
            System.out.print("Choice = ");
            int choice = in.nextInt();
            switch (choice) {
                //exit shop
                case 0: return;
                //try to buy potion
                case 1:
                    System.out.print("Choose potion = ");
                    choice = in.nextInt();
                    Potion potion = shop.buyPotion(choice);
                    if (!currentCharacter.buy(potion)) {
                        System.out.println("Not enough gold!");
                        shop.getPotions().add(potion);
                    }
                    break;
                    //print inventory
                case 2:
                    System.out.println(currentCharacter.getInventory() + "\n");
            }
        }
    }

    @Override
    public void showStory(String story) {
        System.out.println(story);
    }

    /*
        Enemy text interface. Return true if character survive encounter
     */
    @Override
    public boolean enemy(Enemy enemy, Character currentCharacter) {
        while (true) {
            System.out.println("0 to flee enemy");
            System.out.println("1 to attack enemey");
            System.out.println("2 tot use spell");
            System.out.println("3 to use potion\n");
            System.out.println("Enemy health = " + enemy.currentLife);
            System.out.println("Your health = " + currentCharacter.currentLife + "\n");
            System.out.print("Action = ");
            int choice = in.nextInt();
            switch (choice) {
                case 0:
                    //flee
                    System.out.println("You've escaped the enemy");
                    return true;
                case 1:
                    //attack
                    if (attackEnemyText(enemy, currentCharacter))
                        return true;
                    break;
                case 2:
                    //use spell
                    if (spellEnemyText(enemy, currentCharacter))
                        return true;
                    break;
                case 3:
                    //use potion
                    potionUseText(currentCharacter);
                    break;
            }
            System.out.println();
            //enemy turn to attack character
            if (enemyAttackText(enemy, currentCharacter) || currentCharacter.isDead())
                return false;

        }
    }

    private boolean attackEnemyText(Enemy enemy, Character currentCharacter) {
        int damage = currentCharacter.getDamage();
        if (new Random().nextBoolean()) {
            System.out.println("You've dealt " + damage + " damage");
            if (enemy.receiveDamage(damage)) {
                //enemy killed give reward
                System.out.println("Enemy is dead");
                warReward(currentCharacter);
                return true;
            }
        } else {
            System.out.println("Enemy dodged your attack!\n");
        }
        return false;
    }

    private boolean spellEnemyText(Enemy enemy, Character currentCharacter) {
        System.out.println("Character mana " + currentCharacter.currentMana);
        //print spell
        System.out.println("Choose Spell :");
        for (int i = 0; i < currentCharacter.spells.size(); i++) {
            System.out.println("Spell " + i + " : " + currentCharacter.spells.get(i));
        }

        //choose spell
        System.out.print("\nSpell = ");
        int spell = in.nextInt();
        if (spell < 0 || spell >= currentCharacter.spells.size()) {
            System.out.println("Spell doesn't exist");
            return false;
        }

        //try to use spell on enemy
        if(!currentCharacter.useSpell(currentCharacter.spells.get(spell), enemy))
            System.out.println("Not enough mana!");
        if (enemy.isDead()) {
            //enemy killed give reward
            System.out.println("Enemy is dead");
            warReward(currentCharacter);
            return true;
        }
        return false;
    }

    /*
        Add reward to character (gold + exp) after killing enemy
     */
    private void warReward(Character currentCharacter) {
        Random rand = new Random();
        int wonExp = rand.nextInt(50) + 50;
        int wonGold = rand.nextInt(50) + 50;
        System.out.println("You've won " + wonExp + "exp and " + wonGold + "gold!");
        currentCharacter.addExp(wonExp);
        currentCharacter.addMoney(wonGold);
    }

    /*
        Test interface for potion. Return false if can't use potion
     */
    private boolean potionUseText(Character currentCharacter) {
        System.out.println("Choose potion :");
        Inventory inv = currentCharacter.getInventory();
        ArrayList<Potion> potionList = inv.getPotions();

        //print potions
        for (int i = 0; i < potionList.size(); i++) {
            System.out.println("\tPotion " + i + " : " + potionList.get(i));
        }
        System.out.println("\nPotion = ");

        //choose potion
        int potionN = in.nextInt();
        if (potionN < 0 || potionN >= potionList.size()) {
            System.out.println("Potion doesn't exist!");
            return false;
        }

        //use potion effect
        Potion potion = potionList.get(potionN);
        potion.effect(currentCharacter);
        inv.removePotion(potion);
        return true;
    }

    /*
        Text enemy interface. Return true if enemy killed character
     */
    public boolean enemyAttackText(Enemy enemy, Character currentCharacter) {
        Spell enemySpell = enemy.spells.get(new Random().nextInt(enemy.spells.size()));
        if(new Random().nextBoolean()) {    //chance to dodge
            //enemy try to use spell
            if (enemy.useSpell(enemySpell, currentCharacter)) {
                System.out.println("Enemy used " + enemySpell);
                return false;
            }

            //enemy's simple attack
            int damage = enemy.getDamage();
            if (new Random().nextBoolean()) //chance for double damage
                damage *= 2;
            if (currentCharacter.receiveDamage(damage)) {
                System.out.println("Enemy dealt " + damage + " damage!");
                System.out.println("You are dead");
                return true;
            }
        }
        System.out.println("You've dodged enemy attack!");
        return false;
    }
}
