package grabit;

public class Product {
    String name;
    double price;
    double stock;
    int cat;

    public Product(String name, double price,double stock,int cat) {
        this.name = name;
        this.price = price;
        this.stock=stock;
        this.cat=cat;
    }
    @Override
    public String toString() {
        return "Product: " + name + ", Price: " + price +", Stock: "+stock + ",CatID: "+cat;
    }
}