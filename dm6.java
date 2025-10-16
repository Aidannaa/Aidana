import java.util.*;
interface PaymentStrategy {
    void pay(double amount);
}

class CreditCardPayment implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("Оплата " + amount + " тг банковской картой успешно выполнена");
    }
}
class PayPalPayment implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("Оплата " + amount + " тг через PayPal успешно выполнена");
    }
}

class CryptoPayment implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("Оплата " + amount + " тг через криптовалюту подтверждена.");
    }
}
class PaymentContext {
    private PaymentStrategy strategy;

    public void setStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public void executePayment(double amount) {
        if (strategy == null) {
            System.out.println("Стратегия оплаты не выбрана");
        } else {
            strategy.pay(amount);
        }
    }
}


interface Observer {
    void update(String currency, double rate);
}
interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers(String currency, double rate);
}

class CurrencyExchange implements Subject {
    private List<Observer> observers = new ArrayList<>();
    private Map<String, Double> rates = new HashMap<>();

    @Override
    public void attach(Observer observer) {
        observers.add(observer);
        System.out.println(observer.getClass().getSimpleName() + " подписан на обновления валют");
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
        System.out.println(observer.getClass().getSimpleName() + " отписан от уведомлений");
    }

    @Override
    public void notifyObservers(String currency, double rate) {
        for (Observer o : observers) {
            o.update(currency, rate);
        }
    }
    public void setRate(String currency, double newRate) {
        rates.put(currency, newRate);
        System.out.println("\n Новый курс: " + currency + " = " + newRate);
        notifyObservers(currency, newRate);
    }
}
class Trader implements Observer {
    @Override
    public void update(String currency, double rate) {
        System.out.println(" Трейдер получил обновление: " + currency + " = " + rate);
    }
}

class MobileApp implements Observer {
    @Override
    public void update(String currency, double rate) {
        System.out.println(" Мобильное приложение обновлено: " + currency + " = " + rate);
    }
}

class AutoTradingBot implements Observer {
    @Override
    public void update(String currency, double rate) {
        if (rate > 500) {
            System.out.println("Бот: " + currency + " слишком высок — продаю 4");
        } else {
            System.out.println("Бот: " + currency + " низкий курс — покупаю ");
        }
    }
}
public class dm6 {
    public static void main(String[] args) {
        System.out.println(" СИСТЕМА ОПЛАТЫ (Strategy) ");

        PaymentContext payment = new PaymentContext();
        Scanner sc = new Scanner(System.in);

        System.out.print("Введите сумму для оплаты: ");
        double amount = sc.nextDouble();

        System.out.println("\nВыберите способ оплаты:");
        System.out.println("1 - Банковская карта");
        System.out.println("2 - PayPal");
        System.out.println("3 - Криптовалюта");
        int choice = sc.nextInt();

        switch (choice) {
            case 1 -> payment.setStrategy(new CreditCardPayment());
            case 2 -> payment.setStrategy(new PayPalPayment());
            case 3 -> payment.setStrategy(new CryptoPayment());
            default -> System.out.println(" Неверный выбор!");
        }

        payment.executePayment(amount);
        System.out.println("\n СИСТЕМА ОБНОВЛЕНИЯ ВАЛЮТ (Observer) ");

        CurrencyExchange exchange = new CurrencyExchange();

        Trader trader = new Trader();
        MobileApp app = new MobileApp();
        AutoTradingBot bot = new AutoTradingBot();

        exchange.attach(trader);
        exchange.attach(app);
        exchange.attach(bot);
        exchange.setRate("USD", 475.5);
        exchange.setRate("EUR", 510.3);
        exchange.detach(app);
        exchange.setRate("USD", 490.0);
        sc.close();
    }
}
