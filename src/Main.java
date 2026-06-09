import model.SavingsAccount;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        SavingsAccount s1 = new SavingsAccount("10012-234112-2323", "Rafeed Mohammad", 10000);
        s1.displayInfo();
        s1.deposit(20000);
        s1.displayInfo();
    }
}