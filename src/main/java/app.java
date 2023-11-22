public class app {
    public static void main(String[] args) {
        String file = System.getProperty("user.dir");
        dbExcutor dbExcutor = new dbExcutor();
        dbExcutor.init();

    }
}
