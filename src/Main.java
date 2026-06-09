import model.SavingsAccount;
import model.CurrentAccount;
import service.BankService;
import service.BankServiceImpl;
import exception.DuplicateAccountException;

public class Main {
    public static void main(String[] args) {
        BankService bankService = new BankServiceImpl();

        try {
            // Create accounts
            SavingsAccount savingsAccount1 = new SavingsAccount("10012-234112-2323", "Rafeed Mohammad", 10000);
            CurrentAccount currentAccount1 = new CurrentAccount("10012-234112-2324", "Ahmed Khan", "ahmed@bank.com", "01700000001", 50000, 10000);
            SavingsAccount savingsAccount2 = new SavingsAccount("10012-234112-2325", "Fatima Ali", 25000);

            // Add accounts to the service
            bankService.createAccount(savingsAccount1);
            bankService.createAccount(currentAccount1);
            bankService.createAccount(savingsAccount2);

            System.out.println("=== Created Accounts ===");
            for (var account : bankService.getAllAccounts()) {
                account.displayInfo();
            }

            // Save to file
            System.out.println("\n=== Saving to File ===");
            bankService.saveToFile();

            // Load from file
            System.out.println("\n=== Loading from File ===");
            BankService newService = new BankServiceImpl();
            newService.loadFromFile();

            System.out.println("\n=== Loaded Accounts ===");
            for (var account : newService.getAllAccounts()) {
                account.displayInfo();
            }

        } catch (DuplicateAccountException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}