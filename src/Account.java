import java.util.ArrayList;

class Account {
    protected Information inf;
    protected ArrayList<Character> characters;
    protected int gamesCompleted;

    public Account(Information inf, ArrayList<Character> characters,
                   int gamesCompleted) {
        this.inf = inf;
        this.characters = characters;
        this.gamesCompleted = gamesCompleted;
    }

    public static class Information {
        private Credentials credentials;
        private ArrayList<String> favoriteGames;
        private String name;
        private String country;

        public Information(InformationBuilder builder) {
            this.credentials = builder.credentials;
            this.favoriteGames = builder.favoriteGames;
            this.name = builder.name;
            this.country = builder.country;
        }

        public static class Credentials {
            private String email;
            private String password;

            public Credentials(String email, String password) {
                this.email = email;
                this.password = password;
            }

            public String getEmail() {
                return email;
            }

            public String getPassword() {
                return password;
            }

            @Override
            public String toString() {
                return "Credentials | " +
                        "email = " + email + " | " +
                        "password = " + password;
            }
        }

        public static class InformationBuilder {
            private Credentials credentials;
            private ArrayList<String> favoriteGames;
            private String name;
            private String country;

            public InformationBuilder(String email, String password) {
                this.credentials = new Credentials(email, password);
            }

            public InformationBuilder favoriteGames(ArrayList<String> favoriteGames) {
                this.favoriteGames = favoriteGames;
                return this;
            }

            public InformationBuilder name(String name) {
                this.name = name;
                return this;
            }

            public InformationBuilder country(String country) {
                this.country = country;
                return this;
            }

            public Information build() {
                return new Information(this);
            }
        }

        public Credentials getCredentials() {
            return credentials;
        }

        public ArrayList<String> getFavoriteGames() {
            return favoriteGames;
        }

        public String getName() {
            return name;
        }

        public String getCountry() {
            return country;
        }

        @Override
        public String toString() {
            return "\t\tfavoriteGames: " + favoriteGames + "\n" +
                    "\t\tname: " + name + '\n' +
                    "\t\tcountry: " + country + '\n';
        }
    }

    public Information getInf() {
        return inf;
    }

    public ArrayList<Character> getCharacters() {
        return characters;
    }

    @Override
    public String toString() {
        String string = new String("Account :\n" + "\tInformations :\n" + inf + "\tcharacters :\n");
        for (int i = 0; i < characters.size(); i++) {
            string = string.concat("\t\tCharacter " + i + " " + characters.get(i) + "\n");
        }
        string = string.concat("\tgamesCompleted: " + gamesCompleted);
        return string;
    }
}
