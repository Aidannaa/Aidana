interface Beverage {
    String getDescription();
    double getCost();
}

class Americano implements Beverage {
    public String getDescription() { return "Американо"; }
    public double getCost() { return 650.00; }
}

class Latte implements Beverage {
    public String getDescription() { return "Латте"; }
    public double getCost() { return 1100.00; }
}

class Capuchino implements Beverage {
    public String getDescription() { return "Капучино"; }
    public double getCost() { return 980.00; }
}

class Tea implements Beverage {
    public String getDescription() { return "Чай"; }
    public double getCost() { return 600.00; }
}

abstract class BeverageDecorator implements Beverage {
    protected Beverage beverage;

    public BeverageDecorator(Beverage beverage) {
        this.beverage = beverage;
    }

    public String getDescription() {
        return beverage.getDescription();
    }

    public double getCost() {
        return beverage.getCost();
    }
}

class Milk extends BeverageDecorator {
    public Milk(Beverage beverage) { super(beverage); }

    public String getDescription() { return beverage.getDescription() + ", Молоко"; }
    public double getCost() { return beverage.getCost() + 200.00; }
}

class Syrup extends BeverageDecorator {
    public Syrup(Beverage beverage) { super(beverage); }

    public String getDescription() { return beverage.getDescription() + ", Сироп"; }
    public double getCost() { return beverage.getCost() + 300.00; }
}

class WhippedCream extends BeverageDecorator {
    public WhippedCream(Beverage beverage) { super(beverage); }

    public String getDescription() { return beverage.getDescription() + ", Взбитые сливки"; }
    public double getCost() { return beverage.getCost() + 400.00; }
}

class ExtraShot extends BeverageDecorator {
    public ExtraShot(Beverage beverage) { super(beverage); }

    public String getDescription() { return beverage.getDescription() + ", Доп. шот эспрессо"; }
    public double getCost() { return beverage.getCost() + 450.00; }
}

class Sugar extends BeverageDecorator {
    public Sugar(Beverage beverage) { super(beverage); }

    public String getDescription() { return beverage.getDescription() + ", Сахар"; }
    public double getCost() { return beverage.getCost() + 100.00; }
}

interface IPaymentProcessor {
    void processPayment(double amount);
}

class PayPalPaymentProcessor implements IPaymentProcessor {
    public void processPayment(double amount) {
        System.out.printf("  [PayPal]: Начало обработки платежа на сумму %.2f тенге. Стандартный интерфейс.\n", amount);
    }
}

class StripePaymentService {
    public void makeTransaction(double totalAmount) {
        System.out.printf("  [StripeService]: Инициирование MakeTransaction на сумму %.2f тенге. Сторонний интерфейс.\n", totalAmount);
    }
}

class SquarePaymentSystem {
    public boolean executeSale(double price) {
        System.out.printf("  [SquareSystem]: Выполнение executeSale на сумму %.2f тенге. Сторонний интерфейс.\n", price);
        return true;
    }
}

class StripePaymentAdapter implements IPaymentProcessor {
    private StripePaymentService stripeService;

    public StripePaymentAdapter(StripePaymentService stripeService) {
        this.stripeService = stripeService;
    }

    public void processPayment(double amount) {
        System.out.println("Адаптер Stripe: Адаптация вызова...");
        stripeService.makeTransaction(amount);
    }
}

class SquarePaymentAdapter implements IPaymentProcessor {
    private SquarePaymentSystem squareSystem;

    public SquarePaymentAdapter(SquarePaymentSystem squareSystem) {
        this.squareSystem = squareSystem;
    }

    public void processPayment(double amount) {
        System.out.println("Адаптер Square: Адаптация вызова");
        squareSystem.executeSale(amount);
    }
}

public class dom8 {

    public static void runDecoratorTest() {
        System.out.println("\n----------------------------------");

        System.out.println("Система Управления Заказами (Декоратор) \n");
        Beverage order1 = new Americano();
        order1 = new Milk(order1);
        order1 = new Sugar(order1);

        System.out.println("Заказ 1: (Американо + Молоко + Сахар)");
        System.out.println("  Описание: " + order1.getDescription());
        System.out.printf("  Стоимость: %.2f тг.\n", order1.getCost());


        Beverage order2 = new Capuchino ();
        order2 = new Syrup(order2);
        order2 = new WhippedCream(order2);
        order2 = new ExtraShot(order2);

        System.out.println("Заказ 2: (Капучино + Сироп + Сливки + Доп. шот)");
        System.out.println("  Описание: " + order2.getDescription());
        System.out.printf("  Стоимость: %.2f тг.\n", order2.getCost());


        Beverage order3 = new Latte();
        order3 = new Milk(order3);
        order3 = new Syrup(order3);

        System.out.println("Заказ 3: (Латте + Молоко + Сироп)");
        System.out.println("  Описание: " + order3.getDescription());
        System.out.printf("  Стоимость: %.2f тг.\n", order3.getCost());
        System.out.println("\n----------------------------------");
    }



    public static void runAdapterTest() {
        System.out.println("\n\nСистема Оплаты (Адаптер) ");

        IPaymentProcessor payPalProcessor = new PayPalPaymentProcessor();
        System.out.println("Клиент использует PayPal:");
        payPalProcessor.processPayment(15000.00);
        System.out.println("\n----------------------------------");


        StripePaymentService stripeService = new StripePaymentService();
        IPaymentProcessor stripeAdapter = new StripePaymentAdapter(stripeService);
        System.out.println("Клиент использует Stripe через адаптер:");
        stripeAdapter.processPayment(24000.50);
        System.out.println("\n----------------------------------");


        SquarePaymentSystem squareSystem = new SquarePaymentSystem();
        IPaymentProcessor squareAdapter = new SquarePaymentAdapter(squareSystem);
        System.out.println("Клиент использует Square через адаптер:");
        squareAdapter.processPayment(9999.00);
        System.out.println("\n----------------------------------");

        System.out.println("\nВсе платежи обработаны через единый интерфейс IPaymentProcessor ");
    }

    public static void main(String[] args) {
        runDecoratorTest();
        runAdapterTest();
    }
}