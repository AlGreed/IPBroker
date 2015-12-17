package gr.planetz;

public class Schema {

    public static final String CONTEXT = "/broker";

    public static class V1_0 {
        public static final String VERSION = "1.0";

        public static final String PATH    = CONTEXT + '/' + VERSION + "/messages";
    }
}
