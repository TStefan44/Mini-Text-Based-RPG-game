import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class GraphicalUserInterface extends JFrame implements UserInterface{
    private Scanner in = null;
    private CharacterPanel panel;
    private JLabel imageChara;
    protected StatsManaHealth statsBars;

    private static GraphicalUserInterface singleton = null;

    private GraphicalUserInterface() {
        //construct initial panel
        super("World of Marcel");
        setSize(600,600);
        setResizable(false);
        setLayout(null);
        setLocationRelativeTo(null);

        JPanel home = new HomeScreen();
        add(home);
        home.setBounds(0,0,600, 600);
        setVisible(true);
    }

    private GraphicalUserInterface(Scanner in) {
        this();
        this.in = in;
    }

    public static GraphicalUserInterface getSingleton() {
        if (singleton == null)
            singleton = new GraphicalUserInterface();
        return singleton;
    }

    public static GraphicalUserInterface getSingleton(Scanner in) {
        if (singleton == null)
            singleton = new GraphicalUserInterface(in);
        return singleton;
    }

    @Override
    public Account login() {
        return null;
    }

    @Override
    public Character chooseCharacter(Account account) {
        return null;
    }

    @Override
    public boolean run() {
        return false;
    }

    /*
        Graphical interface for finish event. Delete old panel,
        add finis panel
     */
    @Override
    public void finish() {
        //remove old panels
        GraphicalUserInterface gui = GraphicalUserInterface.getSingleton();
        gui.remove(Map.getSingleton());
        gui.remove(gui.statsBars);
        gui.remove(gui.imageChara);
        gui.remove(gui.panel);
        gui.setLayout(null);
        gui.setPreferredSize(new Dimension(600,600));

        //add finish panel
        JPanel finish = new FinishInterface(Grid.getSingleton().getCurrentCharacter(), imageChara);
        gui.add(finish);
        finish.setBounds(0,200,600,600);

        gui.revalidate();
        gui.repaint();
    }

    /*
        Open graphical interface for given shop visited by character
     */
    @Override
    public void shop(Shop shop, Character currentCharacter) {
        //hide map
        GraphicalUserInterface gui = GraphicalUserInterface.getSingleton();
        JTable table = Map.getSingleton();
        table.setFocusable(false);
        table.setVisible(false);

        //add shop
        ShopInterface shopInterface = new ShopInterface(shop, currentCharacter);
        gui.add(shopInterface);
        shopInterface.setBounds(0,0,400,400);

        gui.revalidate();
        gui.repaint();
    }

    @Override
    public void showStory(String story) {
        //TO BE DONE WHEN NOT SLEEP DEPRIVED
    }

    //Open graphical interface for enemy encounter by character
    @Override
    public boolean enemy(Enemy enemy, Character currentCharacter) {
        //hide map
        GraphicalUserInterface gui = GraphicalUserInterface.getSingleton();
        JTable table = Map.getSingleton();
        table.setFocusable(false);
        table.setVisible(false);

        //add enemy interface
        EnemyInterface enemyInt = new EnemyInterface(currentCharacter, enemy);
        gui.add(enemyInt);
        enemyInt.setBounds(0,0, 400, 400);

        return true;
    }

    /*
        Change interface to a login screen. Delete old panel
     */
    public void toLoginScreen(Component c) {
        GraphicalUserInterface gui = GraphicalUserInterface.getSingleton();
        gui.remove(c);  //remove old panel/component
        gui.setLayout(null);

        //add login screen
        LoginScreen login = new LoginScreen();
        gui.add(login);
        Dimension size = login.getPreferredSize();
        login.setBounds(300 - size.width/2, 300 - size.width/2, size.width, size.height);

        gui.revalidate();
        gui.repaint();
    }

    /*
        Change interface to home screen. Delete old panel
     */
    public void toHomeScreen(Component c) {
        GraphicalUserInterface gui = GraphicalUserInterface.getSingleton();
        gui.remove(c); //remove old panel/component

        //add home screen
        gui.setLayout(new FlowLayout());
        JPanel home = new HomeScreen();
        gui.add(home);
        home.setBounds(0,0,600, 600);

        gui.revalidate();
        gui.repaint();
    }

    /*
        Add character selection screen for given account.
        Remove old component
     */
    public void toCharacterSelectionScreen(Component c, Account account) {
        GraphicalUserInterface gui = GraphicalUserInterface.getSingleton();
        gui.remove(c); //remove old panel/component
        gui.setLayout(new FlowLayout());

        //add character selection panel
        CharacterSelectionScreen.setAccount(account);
        gui.add(new CharacterSelectionScreen());

        gui.revalidate();
        gui.repaint();
    }

    /*
        Add map panel, character info panel, stats bar panel
     */
    public void toGame(Component c, Character character, int width, int height, JPanel panel, JLabel image) {
        //Generate map
        Grid.getSingleton(width, height).setCurrentCharacter(character);
        //Grid.generateMap(width, height);
        Grid.makeTestMap();

        GraphicalUserInterface gui = GraphicalUserInterface.getSingleton();
        gui.imageChara = image;
        gui.panel = (CharacterPanel) panel;
        gui.statsBars = new StatsManaHealth(character);
        JTable map = Map.getSingleton();
        gui.remove(c);
        gui.setLayout(null);

        //create and add map
        gui.add(panel);
        map.requestFocusInWindow();
        gui.add(map);
        map.setBounds(0,0,400,400);

        //add stats bar
        gui.add(statsBars);
        statsBars.setBounds(400,400,200,100);

        //add character image
        gui.add(image);
        image.setBounds(400,450,200,200);

        gui.revalidate();
        gui.repaint();
    }

    /*
        Used to exit shop ar enemy interface. Re-add map on frame
     */
    public void exit(Component c) {
        GraphicalUserInterface gui = GraphicalUserInterface.getSingleton();
        gui.remove(c); //remove old component

        //re-add map
        JTable map = Map.getSingleton();
        map.setFocusable(true);
        map.setVisible(true);
        map.requestFocusInWindow();

        gui.revalidate();
        gui.repaint();
    }

    public CharacterPanel getPanel() {
        return panel;
    }
}

/*
    JPanel which implement a home screen.
    Contains a button which send user to a login screen
 */
class HomeScreen extends JPanel implements ActionListener {
    private JButton startButton;

    public HomeScreen() {
        super(new BorderLayout());
        setLayout(null);
        setPreferredSize(new Dimension(600,600));

        startButton = new JButton("Start");
        Dimension size = startButton.getPreferredSize();
        startButton.setBounds(300 - size.width/2, 400 - size.width/2, size.width, size.height);
        startButton.addActionListener(this);

        add(startButton);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 600);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        GraphicalUserInterface gui = GraphicalUserInterface.getSingleton();
        gui.toLoginScreen(this);
    }
}

/*
    JPanel which implements a login screen.
    User need to give a valid password and email. If credentials are correct
    ,user is sent to a character choose screen
 */
class LoginScreen extends JPanel implements ActionListener{
    private JButton login;
    private JButton exit;
    private JTextField username;
    private JTextField password;
    private JLabel inc;
    private JLabel nameLabel;
    private JLabel passLabel;

    public LoginScreen() {
        setLayout(new GridLayout(3,1,0 , 10));

        JPanel loginPanel = new JPanel(new GridLayout(2,2, 0 ,5));
        nameLabel = new JLabel("Email");
        username = new JTextField(20);
        passLabel = new JLabel("Password");
        password = new JTextField(20);
        loginPanel.add(nameLabel); loginPanel.add(username);
        loginPanel.add(passLabel); loginPanel.add(password);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        login = new JButton("Login");
        exit = new JButton("Exit");
        login.setActionCommand("login");
        login.addActionListener(this);
        exit.setActionCommand("exit");
        exit.addActionListener(this);
        buttonPanel.add(login);
        buttonPanel.add(exit);

        inc = new JLabel("Wrong Credentials. Please Retry");
        inc.setForeground(Color.RED);
        inc.setVisible(false);

        add(loginPanel, BorderLayout.CENTER);
        add(inc, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        GraphicalUserInterface gui = GraphicalUserInterface.getSingleton();
        //try login
        if (e.getActionCommand().equals("login")) {
            String email = username.getText();
            String pass = password.getText();
            Account account = Game.getSingleton().verify(email, pass);
            if (account == null) //incorrect credentials
                inc.setVisible(true);
            else { //correct credentials, change screen
                gui.toCharacterSelectionScreen(this, account);
            }
            //exit login screen
        } else if (e.getActionCommand().equals("exit")) {
            gui.toHomeScreen(this);
        }
    }
}

/*
    Character selection screen. Contains account info, a slider for
    character image, map dimension selection and a character info panel.
 */
class CharacterSelectionScreen extends JPanel implements ActionListener {
    private static Account account = null;
    private static ArrayList<Character> characters;

    private JLabel image;
    private JLabel name;
    private JLabel country;
    private JLabel gamesCompleted;
    private JList favouriteGames;

    private JTextField Ox;
    private JTextField Oy;
    private JLabel width;
    private JLabel height;

    private JButton left;
    private JButton right;
    private JButton choose;
    private JButton logout;

    private JPanel charaPanel;
    private ArrayList<JPanel> charaPanels;
    private int index = 0;

    public static void setAccount(Account account) {
        CharacterSelectionScreen.account = account;
        characters = account.getCharacters();
    }

    public CharacterSelectionScreen() {
        setSize(600, 600);
        setLayout(null);

        JPanel dataBox = accountInfo();
        JPanel chara = changeChara();
        JPanel map = mapSize();

        setPreferredSize(new Dimension(600, 600));
        Dimension dim;

        //add account info
        add(dataBox);
        dim = dataBox.getPreferredSize();
        dataBox.setBounds(0, 0, dim.width, dim.height);

        //add character slider
        add(chara);
        dim = chara.getPreferredSize();
        chara.setBounds(225, 0, dim.width, dim.height);

        //map dimension selection panel
        add(map);
        map.setBounds(225, 300, dim.width, 75);

        setPanels();
        charaPanel = charaPanels.get(index);
        charaPanel.setVisible(true);

        left.addActionListener(this);
        left.setActionCommand("left");
        right.addActionListener(this);
        right.setActionCommand("right");
        choose.addActionListener(this);
        choose.setActionCommand("choose");
        logout.addActionListener(this);
        logout.setActionCommand("logout");
    }

    private void setPanels() {
        charaPanels = new ArrayList<>();
        for (int i = 0; i < characters.size(); i++) {
            Dimension dim;
            JPanel charaPanel = new CharacterPanel(characters.get(i));
            add(charaPanel);
            dim = charaPanel.getPreferredSize();
            charaPanel.setBounds(400, 0, 200, 400);
            charaPanel.setVisible(false);
            charaPanels.add(charaPanel);
        }
    }

    private JPanel accountInfo() {
        Account.Information inf = account.getInf();

        JPanel dataBox = new JPanel();
        dataBox.setLayout(new BoxLayout(dataBox, BoxLayout.Y_AXIS));
        JPanel dataGrid = new JPanel(new GridLayout(3,1));

        JLabel text = new JLabel("Account");
        name = new JLabel("Name: " + inf.getName());
        country = new JLabel("Country: " + inf.getCountry());
        gamesCompleted = new JLabel("Games Completed: " + account.gamesCompleted);
        JLabel image = MakeImage.makeImage("images/profile1.png");
        favouriteGames = new JList(inf.getFavoriteGames().toArray(new String[0]));
        JScrollPane scroll = new JScrollPane(favouriteGames);

        logout = new JButton("Logout");

        //Account info
        dataGrid.add(name);
        dataGrid.add(country);
        dataGrid.add(gamesCompleted);

        //Account Info + favourite games
        dataBox.add(text);
        dataBox.add(image);
        dataBox.add(dataGrid);
        dataBox.add(scroll);
        dataBox.add(logout);

        return dataBox;
    }

    private JPanel changeChara() {
        JPanel chara = new JPanel(new BorderLayout());
        left = new JButton("Left");
        right = new JButton("Right");
        choose = new JButton(("Choose"));
        image = MakeImage.makeImage("images/character" + index + ".png");

        JPanel panel = new JPanel();
        panel.add(left);
        panel.add(right);

        chara.add(image, BorderLayout.NORTH);
        chara.add(panel, BorderLayout.CENTER);
        chara.add(choose, BorderLayout.SOUTH);

        return chara;
    }

    private JPanel mapSize() {
        JPanel mapSize = new JPanel(new GridLayout(2,2));
        Ox = new JTextField("5",2);
        Oy = new JTextField("5", 2);
        width = new JLabel("Width");
        height = new JLabel("Height");
        mapSize.add(width);
        mapSize.add(Ox);
        mapSize.add(height);
        mapSize.add(Oy);

        JPanel map = new JPanel(new BorderLayout());
        JLabel text = new JLabel("Map size");
        map.add(text, BorderLayout.NORTH);
        map.add(mapSize, BorderLayout.CENTER);

        return map;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("left")) {
            index--;
            charaPanel.setVisible(false);
            if (index < 0)
                index = charaPanels.size() - 1;
            charaPanel = charaPanels.get(index);
            charaPanel.setVisible(true);
            changeImage();
        } else if (e.getActionCommand().equals("right")) {
            index++;
            charaPanel.setVisible(false);
            if (index >= charaPanels.size())
                index = 0;
            charaPanel = charaPanels.get(index);
            charaPanel.setVisible(true);
            changeImage();
        } else if (e.getActionCommand().equals("logout")) {
            GraphicalUserInterface.getSingleton().toLoginScreen(this);
        } else if (e.getActionCommand().equals("choose")) {
            int width = Integer.parseInt(Ox.getText());
            int height = Integer.parseInt(Oy.getText());
            GraphicalUserInterface.getSingleton().toGame(this, characters.get(index),
                    width, height, charaPanel, image);
        }
    }

    private void changeImage(){
        JPanel panel = (JPanel) getComponent(1);
        image = (JLabel) panel.getComponent(0);
        image.setIcon(MakeImage.makeImage("images/character" + index + ".png").getIcon());
    }
}

/*
    Info panel for given character.
 */
class CharacterPanel extends JPanel {
    private Character character;
    private Inventory inv;

    private JLabel level;
    private JLabel exp;
    private JTree attributeTree;
    private JTree invTree;

    public CharacterPanel(Character character) {
        this.character = character;
        inv = character.getInventory();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel text = new JLabel("Current Character");
        JLabel charaName = new JLabel("Name: " + character.getName());
        level = new JLabel("Level: " + character.getLevel());
        exp = new JLabel("Experience: " + character.getExp());

        add(text);
        add(charaName);
        add(level);
        add(exp);

        attributeTree = attributes(character);
        invTree = inventory(inv);

        JScrollPane attributesScroll = new JScrollPane(attributeTree);
        add(attributesScroll);
        attributesScroll.setMaximumSize(new Dimension(350, 200));

        JScrollPane inventoryScroll = new JScrollPane(invTree);
        add(inventoryScroll);
        inventoryScroll.setMaximumSize(new Dimension(350, 150));

    }

    public void updateInventory() {
        invTree.setModel(inventory(inv).getModel());
    }

    public void updateAttributes() {
        attributeTree.setModel(attributes(character).getModel());
    }

    public static JTree attributes(Character character) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Atributes");

        DefaultMutableTreeNode principal = new DefaultMutableTreeNode("Principal");
        principal.add(new DefaultMutableTreeNode("Strength: " + character.getStrength()));
        principal.add(new DefaultMutableTreeNode("Charisma: " + character.getCharisma()));
        principal.add(new DefaultMutableTreeNode("Dexterity: " + character.getDexterity()));

        DefaultMutableTreeNode secondary = new DefaultMutableTreeNode("Secondary");
        secondary.add(new DefaultMutableTreeNode("Fire Protection: " + character.fireProtection));
        secondary.add(new DefaultMutableTreeNode("Ice protection: " + character.iceProtection));
        secondary.add(new DefaultMutableTreeNode("Earth Protection " + character.earthProtection));

        DefaultMutableTreeNode vocation = new DefaultMutableTreeNode("Vocations");
        vocation.add(new DefaultMutableTreeNode(character.getVocation()));

        root.add(principal);
        root.add(secondary);
        root.add(vocation);

        JTree tree = new JTree(root);
        return tree;
    }

    public static JTree inventory(Inventory inv) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Inventory");
        DefaultMutableTreeNode potions = new DefaultMutableTreeNode("Potions");

        root.add(new DefaultMutableTreeNode("Gold: " + inv.getGold()));
        for (int i = 0; i < inv.getPotions().size(); i++) {
            potions.add(new DefaultMutableTreeNode(inv.getPotions().get(i).toString()));
        }
        root.add(potions);

        JTree tree = new JTree(root);
        return tree;
    }

    public JLabel getLevel() {
        return level;
    }

    public JLabel getExp() {
        return exp;
    }

    public JTree getAttributeTree() {
        return attributeTree;
    }

    public JTree getInvTree() {
        return invTree;
    }
}

class MakeImage {
    public static JLabel makeImage(String path) {
        BufferedImage myPicture = null;
        try {
            myPicture = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        JLabel image = new JLabel(new ImageIcon(myPicture));
        return image;
    }
}

class Map extends JTable implements KeyListener {
    private static Map singleton;

    private Grid grid;

    public static Map getSingleton() {
        if (singleton == null)
            singleton = new Map();
        return singleton;
    }

    private Map() {
        grid = Grid.getSingleton();
        setModel(new DataModel());
        setDefaultRenderer(JLabel.class, new Render());
        setRowHeight(400/grid.getWidth());
        addKeyListener(this);
        setFocusable(true);
    }

    private class DataModel extends AbstractTableModel {
        private int[][] data;
        Object enemy;
        Object player;
        Object shop;
        Object finish;
        Object empty;

        public DataModel() {
            setData();
            enemy = MakeImage.makeImage("images/enemy.png");
            player = MakeImage.makeImage("images/player.png");
            shop = MakeImage.makeImage("images/shop.png");
            finish = MakeImage.makeImage("images/finish.png");
            empty = MakeImage.makeImage("images/empty.png");
            ((JLabel)empty).setMaximumSize(new Dimension(40, 40));
        }

        private void setData() {
            int width = grid.getWidth();
            int height = grid.getHeight();
            data = new int[width][height];

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    switch (grid.get(i).get(j).type) {
                        case SHOP:
                            data[i][j] = 1;
                            break;
                        case ENEMY:
                            data[i][j] = -1;
                            break;
                        case FINISH:
                            data[i][j] = 2;
                            break;
                        case EMPTY:
                            data[i][j] = 0;
                    }
                }
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            fireTableCellUpdated(rowIndex, columnIndex);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return JLabel.class;
        }

        @Override
        public int getRowCount() {
            return grid.getWidth();
        }

        @Override
        public int getColumnCount() {
            return grid.getHeight();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (grid.getCurrentCharacter().getOy() == columnIndex &&
                    grid.getCurrentCharacter().getOx() == rowIndex)
                return player;
            else
                switch (data[rowIndex][columnIndex]) {
                case -1: return enemy;
                case  0: return empty;
                case  1: return shop;
                case  2: return finish;
            }
            return empty;
        }
    }

    class Render extends JLabel implements TableCellRenderer {
        private JLabel unknown = MakeImage.makeImage("images/unknown.png");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Cell currentCell = grid.get(row).get(column);
            if (currentCell.seen || currentCell == grid.getCurrentCell()) {
                return (JLabel) value;
            }
            return unknown;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        Game game = Game.getSingleton();
        Cell currentCell = grid.getCurrentCell();
        if (keyCode == 38 || keyCode == 87) { //up
            game.move('w');
        } else if (keyCode == 40 || keyCode == 83) { //down
            game.move('s');
        } else if (keyCode == 37 || keyCode == 65) { //left
            game.move('a');
        } else if (keyCode == 39 || keyCode == 68) { //right
            game.move('d');
        }

        //update old cell
        setValueAt(null, currentCell.Ox, currentCell.Oy);
        currentCell = grid.getCurrentCell();
        //update new cell
        setValueAt(null, currentCell.Ox, currentCell.Oy);
        Game.getSingleton().event(currentCell, grid);
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}

class ShopInterface extends JPanel implements ListSelectionListener, ActionListener{
    private Shop shop;
    private Character currentCharacter;
    private JButton buy;
    private JButton exit;

    private Model<Potion> listModel;
    private JList<Potion> listPotion;
    private Potion selectedPotion;

    public ShopInterface(Shop shop, Character currentCharacter) {
        this.shop = shop;
        this.currentCharacter = currentCharacter;

        selectedPotion = null;

        exit = new JButton("Exit");
        exit.setActionCommand("exit");
        exit.addActionListener(this);

        buy = new JButton("Buy");
        buy.setActionCommand("buy");
        buy.addActionListener(this);

        listModel = new Model<Potion>(shop.getPotions());
        listPotion = new JList<>(listModel);
        listPotion.addListSelectionListener(this);
        listPotion.setCellRenderer(new Renderer());
        JScrollPane sp = new JScrollPane(listPotion);

        sp.setMaximumSize(new Dimension(400, 400));

        add(sp);
        add(buy);
        add(exit);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        GraphicalUserInterface gui = GraphicalUserInterface.getSingleton();
        if(e.getActionCommand().equals("buy")) {
            shop.getPotions().remove(selectedPotion);
            if (!currentCharacter.buy(selectedPotion)) {
                shop.getPotions().add(selectedPotion);
                return;
            }
            this.repaint();
            gui.getPanel().updateInventory();
        } else if (e.getActionCommand().equals("exit")) {
            gui.exit(this);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            selectedPotion = listPotion.getSelectedValue();
        }
    }
}

class Model<E> extends AbstractListModel<E> {
    private ArrayList<E> list;

    public Model(ArrayList<E> potionList) {
        this.list = potionList;
    }

    @Override
    public int getSize() {
        return list.size();
    }

    @Override
    public E getElementAt(int index) {
        return list.get(index);
    }
}

class Renderer<E> extends JLabel implements ListCellRenderer<E> {
    public Renderer() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends E> list, E value, int index, boolean isSelected,
                                                  boolean cellHasFocus) {

        setText(value.toString());

        Color background;
        Color foreground;

            // check if this cell is selected
         if (isSelected) {
            background = Color.RED;
            foreground = Color.WHITE;
            // unselected
        } else {
            background = Color.WHITE;
            foreground = Color.BLACK;
        };

        setBackground(background);
        setForeground(foreground);

        return this;
    }
}

abstract class StatRange extends JProgressBar {
    protected Entity entity;

    public StatRange(Entity entity ,int maxim) {
        super(0, maxim);
        this.entity = entity;
        setValue(maxim);
        setStringPainted(true);
    }

    abstract public void update();
}

class HealthBar extends StatRange {
    public HealthBar(Entity entity) {
        super(entity, entity.maxLife);
    }

    @Override
    public void update() {
        setValue(entity.currentLife);
        setMaximum(entity.maxLife);
    }
}

class ManaBar extends StatRange {
    public ManaBar(Entity entity) {
        super(entity, entity.maxMana);
    }

    @Override
    public void update() {
        setValue(entity.currentMana);
        setMaximum(entity.maxMana);
    }
}

class StatsManaHealth extends JPanel {
    private StatRange healthBar;
    private StatRange manaBar;

    public StatsManaHealth(Entity entity) {
        setMaximumSize(new Dimension(200, 200));
        setLayout(new GridLayout(4,1));
        healthBar = new HealthBar(entity);
        manaBar = new ManaBar(entity);
        add(new JLabel("Health"));
        add(healthBar);
        add(new JLabel("Mana"));
        add(manaBar);
    }

    public StatRange getHealthBar() {
        return healthBar;
    }

    public StatRange getManaBar() {
        return manaBar;
    }
}

class EnemyInterface extends JPanel implements ActionListener{
    private JLabel image;
    private Character character;
    private Enemy enemy;
    private StatsManaHealth statsBars;
    private GraphicalUserInterface gui;
    private Interface<Potion> potionInt;
    private Interface<Spell> spellInt;

    public EnemyInterface(Character character, Enemy enemy) {
        this.character = character;
        this.enemy = enemy;
        gui = GraphicalUserInterface.getSingleton();
        potionInt = new PotionInterface(character, enemy);
        spellInt = new SpellInterface(character, enemy);

        setLayout(null);
        setMaximumSize(new Dimension(400,400));

        image = MakeImage.makeImage("images/character" + new Random().nextInt(7) + ".png");
        add(image);
        image.setBounds(0,0, 120,200);

        statsBars = new StatsManaHealth(enemy);
        add(statsBars);
        statsBars.setBounds(0, 200, 120, 100);

        JPanel buttons = buttons();
        add(buttons);
        buttons.setBounds(0, 350, 400, 100);

        addInt(potionInt);
        addInt(spellInt);
    }

    private void addInt(Interface panel) {
        add(panel);
        panel.setBounds(130, 0, 240, 400);
        panel.setVisible(false);
    }

    private JPanel buttons() {
        JPanel group = new JPanel(new FlowLayout());

        JButton flee = new JButton("Flee");
        JButton attack = new JButton("Attack");
        JButton spell = new JButton("Use Spell");
        JButton potion = new JButton("Use Potion");

        flee.addActionListener(this);
        attack.addActionListener(this);
        spell.addActionListener(this);
        potion.addActionListener(this);

        flee.setActionCommand("flee");
        attack.setActionCommand("attack");
        spell.setActionCommand("spell");
        potion.setActionCommand("potion");

        group.add(flee);
        group.add(attack);
        group.add(spell);
        group.add(potion);

        return group;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("flee")) {
            gui.exit(this);
        } else if (e.getActionCommand().equals("attack")) {
            int damage = character.getDamage();
            if (new Random().nextBoolean()) {
                if (enemy.receiveDamage(damage)) {
                    gui.exit(this);
                }
            }
            if (TextUserInterface.getSingleton().enemyAttackText(enemy, character)) {
                gui.finish();
            }
        } else if (e.getActionCommand().equals("spell")) {
            potionInt.setVisible(false);
            spellInt.setVisible(true);
        } else if (e.getActionCommand().equals("potion")) {
            spellInt.setVisible(false);
            potionInt.setVisible(true);
        }

        statsBars.getHealthBar().update();
        statsBars.getManaBar().update();
        gui.statsBars.getHealthBar().update();
    }

    public StatsManaHealth getStatsBars() {
        return statsBars;
    }
}

abstract class Interface<E> extends JPanel implements ActionListener, ListSelectionListener {
    protected GraphicalUserInterface gui;
    protected Character character;
    protected JButton use;
    protected Model<E> listModel;
    protected JList<E> list;
    protected E selected;
    protected Enemy enemy;

    public Interface(Character character, Enemy enemy) {
        this.character = character;
        this.enemy = enemy;
        gui = GraphicalUserInterface.getSingleton();
        setLayout(new FlowLayout());
        selected = null;
        use = new JButton("Use");
        use.addActionListener(this);
        use.setActionCommand("use");
    }

    @Override
    abstract public void actionPerformed(ActionEvent e);

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            selected = list.getSelectedValue();
        }
    }
}

class PotionInterface extends Interface<Potion> {

    public PotionInterface(Character character, Enemy enemy) {
        super(character, enemy);

        listModel = new Model<Potion>(character.inventory.getPotions());
        list = new JList<Potion>(listModel);
        list.addListSelectionListener(this);
        list.setCellRenderer(new Renderer<Potion>());
        JScrollPane sp = new JScrollPane(list);
        sp.setPreferredSize(new Dimension(200,200));

        add(sp);
        add(use);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("use")) {
            if (selected == null)
                return;
            selected.effect(character);
            character.getInventory().removePotion(selected);
            GraphicalUserInterface.getSingleton().getPanel().updateInventory();
            list.repaint();

            if (TextUserInterface.getSingleton().enemyAttackText(enemy, character)) {
                gui.finish();
            }

            gui.statsBars.getManaBar().update();
            gui.statsBars.getHealthBar().update();
            ((EnemyInterface)getParent()).getStatsBars().getManaBar().update();
        }
    }
}

class SpellInterface extends Interface<Spell> {

    public SpellInterface(Character character, Enemy enemy) {
        super(character, enemy);

        listModel = new Model<Spell>(character.spells);
        list = new JList<Spell>(listModel);
        list.addListSelectionListener(this);
        list.setCellRenderer(new Renderer<Spell>());
        JScrollPane sp = new JScrollPane(list);

        add(sp);
        add(use);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("use")) {
            if (selected == null)
                return;
            if(!character.useSpell(selected, enemy))
                System.out.println("Not enough mana!");
            gui.getPanel().updateInventory();
            if (TextUserInterface.getSingleton().enemyAttackText(enemy, character)) {
                gui.finish();
            }
            gui.statsBars.getManaBar().update();
            gui.statsBars.getHealthBar().update();
            ((EnemyInterface)getParent()).getStatsBars().getHealthBar().update();
            ((EnemyInterface)getParent()).getStatsBars().getManaBar().update();
            list.repaint();
            if (enemy.isDead()) {
                GraphicalUserInterface.getSingleton().exit(getParent());
            }
        }
    }
}

class FinishInterface extends JPanel implements ActionListener{
    private Character character;
    private JLabel image;

    public FinishInterface(Character character, JLabel image) {
        this.character = character;
        this.image = image;

        setPreferredSize(new Dimension(600,600));

        add(image);
        image.setBounds(0,0,120,200);

        JPanel stats = stats();
        add(stats);
        Dimension dim = stats.getPreferredSize();
        stats.setBounds(150, 40, dim.width, dim.height);

        JPanel buttons = buttons();
        add(buttons);
        dim = buttons.getPreferredSize();
        buttons.setBounds(300- dim.width/2,400, dim.width, dim.height);
    }

    private JPanel stats() {
        JPanel panel = new JPanel(new GridLayout(4,1));

        JLabel money = new JLabel("Money won " + character.cumulatedMoney);
        JLabel exp = new JLabel("Exp won " + character.cumulatedExp);
        JLabel enemy = new JLabel("Enemy killed "  + character.enemyKills);
        JLabel level = new JLabel("Final level " + character.getLevel());

        panel.add(level);
        panel.add(exp);
        panel.add(enemy);
        panel.add(money);

        return panel;

    }

    private JPanel buttons() {
        JPanel panel = new JPanel(new FlowLayout());

        JButton exit = new JButton("Exit");
        JButton toCharaSelection =  new JButton("To character selection");
        JButton logout = new JButton("Logout");

        exit.addActionListener(this);
        toCharaSelection.addActionListener(this);
        logout.addActionListener(this);

        exit.setActionCommand("exit");
        toCharaSelection.setActionCommand("chara");
        logout.setActionCommand("logout");

        panel.add(exit);
        panel.add(toCharaSelection);
        panel.add(logout);

        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        GraphicalUserInterface gui = GraphicalUserInterface.getSingleton();
        if (e.getActionCommand().equals("exit")) {
            gui.toHomeScreen(this);
        } else if (e.getActionCommand().equals("chara")) {

        } else if (e.getActionCommand().equals("logout")) {
            gui.toLoginScreen(this);
        }
    }
}