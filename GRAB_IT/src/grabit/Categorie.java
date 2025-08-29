package grabit;

public class Categorie {
    private int c_id;
    private String c_name;

    public Categorie(int c_id, String c_name) {
        this.c_id = c_id;
        this.c_name = c_name;
    }

    public Categorie(String c_name) {
        this.c_name = c_name;
    }

    public Categorie(int c_id){
        this.c_id = c_id;
    }

    public int getC_id() {
        return c_id;
    }
    public String getC_name() {
        return c_name;
    }
}