import java.util.*;
import java.util.concurrent.*;
public class bookingprocess11 {

    static class Venue {
        String name;
        boolean available;
        Venue(String n, boolean a){ name=n; available=a; }
    }

    public static void main(String[] args) throws Exception {
        Venue hall = new Venue("Grand Hall", true);
        String client = "Saule";

        if(!hall.available) {
            System.out.println("Площадка недоступна. Выберите другую дату.");
            return;
        }

        System.out.println("Площадка доступна. Стоимость отправлена клиенту.");
        System.out.println("Клиент подтверждает бронирование.");

        boolean payment = pay();
        if(!payment) {
            System.out.println("Платеж отклонён. Повторите оплату.");
            return;
        }

        System.out.println("Платеж успешен. Бронирование подтверждено.");
        System.out.println("Уведомление подрядчикам отправлено.");

        ExecutorService ex = Executors.newFixedThreadPool(3);
        List<String> contractors = List.of("Decor", "Catering", "Audio");

        List<Callable<String>> tasks = new ArrayList<>();
        for(String c : contractors)
            tasks.add(() -> notifyContractor(c));

        for(Future<String> f : ex.invokeAll(tasks))
            System.out.println(f.get());
        ex.shutdown();

        System.out.println("После мероприятия — отзыв клиента.");
        System.out.println("Отчет отправлен менеджеру.");
    }

    static boolean pay(){
        return Math.random() > 0.2;
    }

    static String notifyContractor(String name) throws Exception {
        Thread.sleep((int)(Math.random()*400));
        return "Подрядчик " + name + " подтвердил выполнение.";
    }
}
