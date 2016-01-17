package gr.planetz;

public class Schema {

    public static final String CONTEXT = "/broker";

    public static class V1_0 {
        public static final String VERSION = "1.0";

        public static final String GAME_PATH = CONTEXT + '/' + VERSION + "/messages";

        public static final String JOIN_PATH = CONTEXT + '/' + VERSION + "/join";

        public static final String PLAYERS_PATH = CONTEXT + '/' + VERSION + "/players";
    }
}
