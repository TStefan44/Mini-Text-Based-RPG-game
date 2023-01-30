import java.util.ArrayList;
import java.util.Random;

class Grid extends ArrayList<ArrayList<Cell>> {
    private final int width;
    private final int height;
    private int minimumShops = 2;
    private int minimumEnemies = 4;
    private int minimumFinishes = 1;
    private Cell currentCell = null;
    private Character currentCharacter = null;
    private static Grid singleton = null;

    private Grid(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static Grid getSingleton(int width, int height) {
        if (singleton == null)
            singleton = new Grid(width, height);
        return singleton;
    }

    public static Grid getSingleton() {
        return singleton;
    }

    /*
        Create grid. Width - Oy and Height - Ox
        Hardcoded: minimum cells, chances of shops and enemies
     */
    public static Grid generateMap(int width, int height) {
        if (singleton != null) return getSingleton();

        if (width * height < 6) {
            System.out.println("Insufficient cell, minimum requirement 6");
            return null;
        }

        //create grid with all cells empty
       Grid grid = getSingleton(width, height);
       for(int i = 0; i < width; i++) {
           ArrayList<Cell> column = new ArrayList<>();
           for (int j = 0; j < height; j++) {
                column.add(new Cell(i,j));
           }
           grid.add(column);
       }

       //randomizer
       grid.putPlayer();
       grid.randomize(0, grid.minimumFinishes, CellEnum.FINISH);
       grid.randomize(5, grid.minimumShops, CellEnum.SHOP);
       grid.randomize(25, grid.minimumEnemies, CellEnum.ENEMY);

       return grid;
    }

    /*
        Add player on a random cell in the grid. Cell need to be empty.
        Chosen cell become current cell, and it's coordinates become
        character's initial coordinates
     */
    private void putPlayer() {
        //Verify if player/grid exist
        if (singleton == null || singleton.currentCharacter == null) return;
        Random rand = new Random();
        int Ox, Oy;

        //Choose randomly cell
        while(true) {
            Oy = rand.nextInt(singleton.width);
            Ox = rand.nextInt(singleton.height);
            singleton.currentCell = singleton.get(Oy).get(Ox);
            if (currentCell.type == CellEnum.EMPTY) {
                singleton.currentCharacter.setOx(singleton.currentCell.Ox);
                singleton.currentCharacter.setOy(singleton.currentCell.Oy);
                return;
            }
        }
    }

    /*
        Randomize cells in grid depending on type given, chance and minimum
        apparitions.
     */
    private void randomize(int chance, int minimum, CellEnum type) {
        if (singleton == null) return;
        Random rand = new Random();
        int maximum = singleton.width * singleton.height;

        //create cell and replace it in grid
        int i = 0;
        while (i < maximum) {
            if (i < minimum || rand.nextInt(100) < chance) {
                int Oy = rand.nextInt(singleton.width);
                int Ox = rand.nextInt(singleton.height);
                Cell cell = singleton.get(Oy).get(Ox);

                //skipp cell if not empty or starting cell
                if (cell.type != CellEnum.EMPTY
                        || cell == singleton.currentCell) {
                    continue;
                }
                cell.type = type;
                cell.entity = createNewType(type);
            }
            i++;
        }
    }

    /*
        Create entity for cells. Similar to factory pattern.
     */
    private CellElement createNewType(CellEnum type) {
        CellElement entity = null;
        switch (type) {
            case SHOP:
                entity = new Shop();
                break;
            case ENEMY:
                entity = new Enemy();
                break;
            case FINISH:
                entity = new Finish();
        }
        return entity;
    }

    /*
        Print grid in terminal
     */
    public static void printMap() {
        if (singleton == null) {
            System.out.println("Can't print Grid, because it doesn't exist");
            return;
        }
        for (int i = 0; i < singleton.width; i++) {
            ArrayList<Cell> cellList = singleton.get(i);
            for (int j = 0; j < singleton.height; j++) {
                Cell cell = cellList.get(j);
                //print cell with character
                if(cell == singleton.currentCell) {
                    if(cell.type == CellEnum.EMPTY || cell.type == CellEnum.FINISH)
                        //empty cell + character
                        System.out.format("%3c", 'P');
                    else
                        //shop or enemy cell + character
                        System.out.format("%3s", "P" + cell.entity.toCharacter());
                } else if (cell.seen == false) {
                    //unvisited cells
                    System.out.format("%3c", '?');
                } else {
                    //shop or enemy cell
                    System.out.format("%3c", cell.entity.toCharacter());
                }
            }
            System.out.println();
        }
    }

    public boolean goNorth() {
        if (singleton == null) {
            System.out.println("Grid doesn't exist");
            return false;
        }
        Cell currentCell = singleton.currentCell;
        if (currentCell.Ox == 0) {
            System.out.println("Can't go north");
            return false;
        }
        currentCell = singleton.get(currentCell.Ox - 1).get(currentCell.Oy);
        singleton.currentCharacter.setOx(currentCell.Ox);
        singleton.currentCell = currentCell;
        return true;
    }

    public boolean goSouth() {
        if (singleton == null) {
            System.out.println("Grid doesn't exist");
            return false;
        }
        Cell currentCell = singleton.currentCell;
        if (currentCell.Ox == singleton.width - 1) {
            System.out.println("Can't go south");
            return false;
        }
        currentCell = singleton.get(currentCell.Ox + 1).get(currentCell.Oy);
        singleton.currentCharacter.setOx(currentCell.Ox);
        singleton.currentCell = currentCell;
        return true;
    }

    public boolean goWest() {
        if (singleton == null) {
            System.out.println("Grid doesn't exist");
            return false;
        }
        Cell currentCell = singleton.currentCell;
        if (currentCell.Oy == 0) {
            System.out.println("Can't go west");
            return false;
        }
        currentCell = singleton.get(currentCell.Ox).get(currentCell.Oy - 1);
        singleton.currentCharacter.setOy(currentCell.Oy);
        singleton.currentCell = currentCell;
        return true;
    }

    public boolean goEast() {
        if (singleton == null) {
            System.out.println("Grid doesn't exist");
            return false;
        }
        Cell currentCell = singleton.currentCell;
        if (currentCell.Oy == singleton.height - 1) {
            System.out.println("Can't go east");
            return false;
        }
        currentCell = singleton.get(currentCell.Ox).get(currentCell.Oy + 1);
        singleton.currentCharacter.setOy(currentCell.Oy);
        singleton.currentCell = currentCell;
        return true;
    }

    /*
        Make hardcoded map for test purpose
     */
    public static Grid makeTestMap() {
        Grid grid = Grid.getSingleton(5, 5);

        for(int i = 0; i < 5; i++) {
            ArrayList<Cell> column = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                column.add(new Cell(i,j));
            }
            grid.add(column);
        }

        grid.putPlayer(0, 0, grid);
        grid.putEntity(0, 3, grid, CellEnum.SHOP);
        grid.putEntity(1, 3, grid, CellEnum.SHOP);
        grid.putEntity(2, 0, grid, CellEnum.SHOP);
        grid.putEntity(3, 4, grid, CellEnum.ENEMY);
        grid.putEntity(4, 4, grid, CellEnum.FINISH);

        ((Shop)grid.get(0).get(3).getEntity()).addPotion(new ManaPotion());

        return grid;
    }

    private void putPlayer(int Ox, int Oy, Grid grid) {
        Cell cell = grid.get(Ox).get(Oy);
        cell.seen = true;
        grid.currentCell = cell;
        grid.currentCharacter.setOx(Ox);
        grid.currentCharacter.setOy(Oy);
    }

    private void putEntity(int Ox, int Oy, Grid grid, CellEnum type) {
        Cell cell = grid.get(Ox).get(Oy);
        cell.type = type;
        cell.entity = createNewType(type);
    }

    public Character getCurrentCharacter() {
        return currentCharacter;
    }

    public Cell getCurrentCell() {
        return currentCell;
    }

    public void setMinimumShops(int minimumShops) {
        this.minimumShops = minimumShops;
    }

    public void setMinimumEnemies(int minimumEnemies) {
        this.minimumEnemies = minimumEnemies;
    }

    public void setMinimumFinishes(int minimumFinishes) {
        this.minimumFinishes = minimumFinishes;
    }

    public void setCurrentCharacter(Character currentCharacter) {
        this.currentCharacter = currentCharacter;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setCurrentCell(Cell currentCell) {
        this.currentCell = currentCell;
    }
}

class Cell {
    protected int Ox;
    protected int Oy;
    protected CellEnum type;
    protected CellElement entity;
    protected boolean seen;

    public Cell(int Ox, int Oy, CellEnum type, CellElement entity) {
        this.Ox = Ox;
        this.Oy = Oy;
        this.type = type;
        this.entity = entity;
        seen = false;
    }

    public Cell(int Ox, int Oy) {
        this(Ox, Oy, CellEnum.EMPTY, new Empty());
    }

    public CellElement getEntity() {
        return entity;
    }
}