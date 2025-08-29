import grabit.AdminApp;
import grabit.Login;
import grabit.Product;
import grabit.Categorie;
import java.util.Scanner;

class Grabit
{
    static
    {
        System.out.println("=========================");
        System.out.println(" Welcome to Grab-It");
        System.out.println("=========================");
    }
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter who is from the following");
        System.out.println("1. User");
        System.out.println("2. Admin");
        System.out.println("3. Exit");
        int choice = sc.nextInt();

        switch(choice) {
            case 1:

                Login login = new Login();
                login.App();
                break;
            case 2:
                System.out.println("Hello Admin");
                AdminApp admin = new AdminApp();
                admin.App();
                break;
            case 3:
                System.out.println("Exit");
                break;
            default:
                System.out.println("Invalid choice");
        }
        sc.close();
    }
}