import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class Game {
    public ArrayList<Account> accountsList;
    private EnumMap <CellEnum, ArrayList<String>> stories;
    private static Game singleton = null;
    private Scanner in = new Scanner(System.in);
    private UserInterface ui = null;

    private Game(){
        accountsList = new ArrayList<>();
        stories = new EnumMap<>(CellEnum.class);
    }

    public static Game getSingleton() {
        if (singleton == null)
            singleton = new Game();
        return singleton;
    }

    /*
        Choose interface type.
     */
    public void run() {
        readStories();
        readAccount();
        int interfaceOption = -1;

        while (true) {
            System.out.println("Welcome to World of Marcel");
            System.out.println("Please for terminal interface press - 1");
            System.out.println("Please for graphical interface press - 2");
            System.out.println("If you wish to exit, please press - 0\n");
            System.out.print("Input = ");
            try {
                interfaceOption = in.nextInt();
            } catch (InputMismatchException e) {
                throw new InvalidCommandException("Input gresit");
            }
            switch (interfaceOption) {
                case 0: return;
                case 1:
                    ui = TextUserInterface.getSingleton(in);
                    System.out.println("Terminal mode chosen, please wait warmly");
                    if (ui.run()) {
                        options();
                        return;
                    }
                    break;
                case 2:
                    in.close();
                    System.out.println("Graphical mode chosen, please wait warmly");
                    ui = GraphicalUserInterface.getSingleton();
                    return;
                default:
                    System.out.println("Wrong input\n");
            }
        }
    }

    /*
        Verify if given email and password are valid credentials.
        Return corresponding account or null.
     */
    public Account verify(String email, String password) {
        Account account = null;
        Iterator<Account> itr = accountsList.iterator();
        while (itr.hasNext()) {
            account = itr.next();
            if (account.getInf().getCredentials().getEmail().equals(email)) {
                if (account.getInf().getCredentials().getPassword().equals(password)) {
                    return account;
                }
            }
        }
        return null;
    }

    private void readAccount() {
        try {
            //extract accounts
            Object obj = new JSONParser().parse(new FileReader("accounts.json"));
            JSONObject jo = (JSONObject) obj;
            JSONArray ja = (JSONArray) jo.get("accounts");

            //iterate throw accounts
            for (JSONObject jsonObject : (Iterable<JSONObject>) ja) {
                ArrayList<String> favoriteGamesList = new ArrayList<>();
                Account account;

                //extract credential, favorite game list
                // characters list, name, country and maps completed
                JSONObject credentials = (JSONObject) jsonObject.get("credentials");
                String email = (String) credentials.get("email");
                String password = (String) credentials.get("password");
                String name = (String) jsonObject.get("name");
                String country = (String) jsonObject.get("country");
                int mapsCompleted = Integer.parseInt((String)jsonObject.get("maps_completed"));

                //extract list of favorite games
                JSONArray favoriteGames = (JSONArray) jsonObject.get("favorite_games");
                for (String favoriteGame : (Iterable<String>) favoriteGames) {
                    favoriteGamesList.add((String) favoriteGame);
                }
                Collections.sort(favoriteGamesList);

                //Iterate through an account's characters and make the character list
                ArrayList<Character> charactersList = new ArrayList<>();
                JSONArray characters = (JSONArray) jsonObject.get("characters");
                for (JSONObject jsonObject1 : (Iterable<JSONObject>) characters) {
                    //extract name, level, experience, profession
                    String nameC = (String) jsonObject1.get("name");
                    String profession = (String) jsonObject1.get("profession");
                    CharacterEnum professionEnum = CharacterEnum.valueOf(profession.toUpperCase());
                    String level = (String) jsonObject1.get("level");
                    long experience = (long) jsonObject1.get("experience");

                    //create character based on profession with given exp, lv and name using factory and seters
                    Character character = CharacterFactory.getFactory().factory(professionEnum,
                            Integer.parseInt(level), (int) experience, nameC);
                    charactersList.add(character);
                }

                Account.Information inf = new Account.Information.InformationBuilder(email, password)
                        .country(country)
                        .favoriteGames(favoriteGamesList)
                        .name(name)
                        .build();
                account = new Account(inf, charactersList, mapsCompleted);
                accountsList.add(account);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void readStories() {
        try {
            //extract stories
            Object obj = new JSONParser().parse(new FileReader("stories.json"));
            JSONObject jo = (JSONObject) obj;
            JSONArray ja = (JSONArray) jo.get("stories");

            //iterate throw stories
            for (JSONObject jsonObject : (Iterable<JSONObject>) ja) {
                ArrayList<String> valueList;

                //extract type and value
                String type = (String) jsonObject.get("type");
                CellEnum typeEnum = CellEnum.valueOf(type.toUpperCase());
                String value = (String) jsonObject.get("value");

                //add type and value in Map
                if (!stories.containsKey(typeEnum)) {
                    valueList = new ArrayList<>();
                    stories.put(typeEnum, valueList);
                } else {
                    valueList = stories.get(typeEnum);
                }
                valueList.add(value);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    /*
        Used by text interface. Determine event by current cell
        in grid and call corresponding functions.
     */
    public void options() {
        Grid grid = Grid.getSingleton(0,0);
        Cell currentCell;
        ui.showStory(showStory(grid.getCurrentCell().type));
        while (true) {
            Grid.printMap();
            currentCell = grid.getCurrentCell();

            char c;
            do {
                System.out.println("Press 'p' to continue\n");
                c = in.next().charAt(0);
            } while (c != 'p');

            switch (currentCell.type) {
                case SHOP: ui.shop((Shop) currentCell.getEntity(), grid.getCurrentCharacter()); break;
                case ENEMY: if (!currentCell.seen) {
                    if (!ui.enemy((Enemy) currentCell.getEntity(), grid.getCurrentCharacter())) {
                        ui.finish();
                        return;
                    }
                    currentCell.seen = true;
                } break;
                case FINISH: ui.finish(); return;
            }

            move();
            System.out.println();
        }
    }

    /*
        Used by graphical interface
     */
    public void event(Cell currentCell, Grid grid) {
        switch (currentCell.type) {
            case SHOP: ui.shop((Shop) currentCell.getEntity(), grid.getCurrentCharacter()); break;
            case ENEMY:
                if (!currentCell.seen) {
                    if (!ui.enemy((Enemy) currentCell.getEntity(), grid.getCurrentCharacter())) {
                        ui.finish();
                        return;
                    }
                    currentCell.seen = true;
            } break;
            case FINISH: ui.finish();
        }
    }

    /*
        Move current character. Called by gui using key listener
     */
    protected void move(char input) {
        //move character
        Grid grid = Grid.getSingleton();
        switch (input) {
            case 'w': grid.goNorth(); break;
            case 's': grid.goSouth(); break;
            case 'a': grid.goWest(); break;
            case 'd': grid.goEast(); break;
            case 'l': in.close(); return;
        }

        //mark new cell as seen
        Cell currentCell = grid.getCurrentCell();
        if (!currentCell.seen) {
            ui.showStory(showStory(currentCell.type));
            if(currentCell.type != CellEnum.ENEMY) currentCell.seen = true;
        }

        //chance to get coin
        Random rand = new Random();
        if(rand.nextInt(100) <= 20) {
            grid.getCurrentCharacter().addMoney(rand.nextInt(25) + 25);
            GraphicalUserInterface.getSingleton().getPanel().updateInventory();
        }
    }

    protected void move() {
        Grid grid = Grid.getSingleton(0,0);
        System.out.println("w - up | s - down |\na - left | d - right\nOption = ");

        char input = in.next().charAt(0);
        switch (input) {
            case 'w': grid.goNorth(); break;
            case 's': grid.goSouth(); break;
            case 'a': grid.goWest(); break;
            case 'd': grid.goEast(); break;
            case 'l': in.close(); return;
        }

        Cell currentCell = grid.getCurrentCell();
        if (currentCell.seen == false) {
            ui.showStory(showStory(currentCell.type));
            if(currentCell.type != CellEnum.ENEMY) currentCell.seen = true;
        }

        Random rand = new Random();
        if(rand.nextInt(100) <= 20) {
            grid.getCurrentCharacter().addMoney(rand.nextInt(25) + 25);
        }

        System.out.println();
    }

    private String showStory(CellEnum type) {
        ArrayList<String> storiesList = stories.get(type);
        return storiesList.get(new Random().nextInt(storiesList.size()));
    }

}

class InvalidCommandException extends InputMismatchException {
    public InvalidCommandException(String s) {
        super(s);
    }
}

enum CellEnum {EMPTY, ENEMY, SHOP, FINISH}