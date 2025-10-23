import java.util.*;
interface Command {
    void execute();
    void undo();
    String getName();
}

class Light {
    private String room;
    private boolean isOn = false;
    public Light(String room) { this.room = room; }
    public void on() { isOn = true; System.out.println(" " + room + " шамы қосылды."); }
    public void off() { isOn = false; System.out.println(" " + room + " шамы өшірілді."); }
}

class Door {
    private String name;
    private boolean open = false;
    public Door(String name) { this.name = name; }
    public void open() { open = true; System.out.println(" " + name + " есігі ашылды."); }
    public void close() { open = false; System.out.println(" " + name + " есігі жабылды."); }
}

class Thermostat {
    private int temperature = 22;
    public void increase() { temperature++; System.out.println("🌡 Температура артты: " + temperature + ""); }
    public void decrease() { temperature--; System.out.println("🌡 Температура азайды: " + temperature + ""); }
}
class LightOnCommand implements Command {
    private Light light;
    public LightOnCommand(Light light) { this.light = light; }
    public void execute() { light.on(); }
    public void undo() { light.off(); }
    public String getName() { return "LightOn"; }
}

class LightOffCommand implements Command {
    private Light light;
    public LightOffCommand(Light light) { this.light = light; }
    public void execute() { light.off(); }
    public void undo() { light.on(); }
    public String getName() { return "LightOff"; }
}

class DoorOpenCommand implements Command {
    private Door door;
    public DoorOpenCommand(Door door) { this.door = door; }
    public void execute() { door.open(); }
    public void undo() { door.close(); }
    public String getName() { return "DoorOpen"; }
}

class DoorCloseCommand implements Command {
    private Door door;
    public DoorCloseCommand(Door door) { this.door = door; }
    public void execute() { door.close(); }
    public void undo() { door.open(); }
    public String getName() { return "DoorClose"; }
}

class TempUpCommand implements Command {
    private Thermostat thermostat;
    public TempUpCommand(Thermostat thermostat) { this.thermostat = thermostat; }
    public void execute() { thermostat.increase(); }
    public void undo() { thermostat.decrease(); }
    public String getName() { return "TempUp"; }
}

class TempDownCommand implements Command {
    private Thermostat thermostat;
    public TempDownCommand(Thermostat thermostat) { this.thermostat = thermostat; }
    public void execute() { thermostat.decrease(); }
    public void undo() { thermostat.increase(); }
    public String getName() { return "TempDown"; }
}

class SmartHomeController {
    private Stack<Command> history = new Stack<>();
    public void executeCommand(Command command) {
        command.execute();
        history.push(command);
    }
    public void undoLastCommand() {
        if (!history.isEmpty()) {
            Command last = history.pop();
            System.out.println(" Болдырмау: " + last.getName());
            last.undo();
        } else {
            System.out.println("Болдырылатын команда жоқ!");
        }
    }
}

abstract class Beverage {
    public final void prepareRecipe() {
        boilWater();
        brew();
        pourInCup();
        if (customerWantsCondiments()) {
            addCondiments();
        }
        System.out.println(" " + getName() + " дайын!\n");
    }

    void boilWater() { System.out.println("Су қайнатылды."); }
    abstract void brew();
    void pourInCup() { System.out.println("Кесе дайын."); }
    abstract void addCondiments();
    boolean customerWantsCondiments() { return true; }
    abstract String getName();
}

class Tea extends Beverage {
    void brew() { System.out.println("Шай демделуде..."); }
    void addCondiments() { System.out.println("Қант пен лимон қосылды."); }
    String getName() { return "Шай"; }
}

class Coffee extends Beverage {
    private Scanner scanner = new Scanner(System.in);
    void brew() { System.out.println("Кофе дайындалуда..."); }
    void addCondiments() { System.out.println("Сүт пен қант қосылды."); }
    boolean customerWantsCondiments() {
        System.out.print("Қоспалар керек пе (иә/жоқ)? ");
        String answer = scanner.nextLine().trim().toLowerCase();
        return answer.equals("иә") || answer.equals("иа") || answer.equals("yes");
    }
    String getName() { return "Кофе"; }
}

class HotChocolate extends Beverage {
    void brew() { System.out.println("Шоколад ұнтағы араластырылуда..."); }
    void addCondiments() { System.out.println("Крем мен маршмеллоу қосылды."); }
    String getName() { return "Ыстық шоколад"; }
}

interface Mediator {
    void sendMessage(String message, User sender);
    void addUser(User user);
}

class ChatRoom implements Mediator {
    private List<User> users = new ArrayList<>();
    public void addUser(User user) {
        users.add(user);
        System.out.println( user.getName() + " чатқа қосылды.");
    }
    public void sendMessage(String message, User sender) {
        for (User u : users) {
            if (u != sender) {
                u.receive(message, sender);
            }
        }
    }
}

abstract class User {
    protected Mediator mediator;
    protected String name;
    public User(Mediator mediator, String name) {
        this.mediator = mediator;
        this.name = name;
    }
    public String getName() { return name; }
    public abstract void send(String message);
    public abstract void receive(String message, User sender);
}

class ChatUser extends User {
    public ChatUser(Mediator mediator, String name) {
        super(mediator, name);
    }
    public void send(String message) {
        System.out.println(" " + name + ": " + message);
        mediator.sendMessage(message, this);
    }
    public void receive(String message, User sender) {
        System.out.println(" " + name + " хабар алды: \"" + message + "\" (жіберген: " + sender.getName() + ")");
    }
}

public class PatternsDemo {
    public static void main(String[] args) {
        System.out.println("\n Command Pattern ");
        SmartHomeController controller = new SmartHomeController();
        Light light = new Light("Ас үй");
        Door door = new Door("Негізгі");
        Thermostat thermostat = new Thermostat();

        controller.executeCommand(new LightOnCommand(light));
        controller.executeCommand(new DoorOpenCommand(door));
        controller.executeCommand(new TempUpCommand(thermostat));
        controller.undoLastCommand();
        controller.undoLastCommand();

        System.out.println("\n Template Method Pattern ");
        Beverage tea = new Tea();
        Beverage coffee = new Coffee();
        Beverage choco = new HotChocolate();

        tea.prepareRecipe();
        coffee.prepareRecipe();
        choco.prepareRecipe();

        System.out.println("\n Mediator Pattern ");
        ChatRoom chat = new ChatRoom();
        User user1 = new ChatUser(chat, "Айдана");
        User user2 = new ChatUser(chat, "Данияр");
        User user3 = new ChatUser(chat, "Алия");

        chat.addUser(user1);
        chat.addUser(user2);
        chat.addUser(user3);

        user1.send("Сәлем, бәріне!");
        user2.send("Сәлем, Айдана!");
        user3.send("Қалың қалай?");
    }
}
