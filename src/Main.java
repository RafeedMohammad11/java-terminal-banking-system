import controller.BankController;
import service.BankService;
import service.BankServiceImpl;

public class Main {
    public static void main(String[] args) {
        BankService bankService = new BankServiceImpl();
        BankController bankController = new BankController(bankService);
        bankController.start();
    }
}