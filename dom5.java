import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;


class ConfigurationManager {
    private static ConfigurationManager instance;
    private static final ReentrantLock lock = new ReentrantLock();
    private final Map<String, String> settings;

    private ConfigurationManager() {
        settings = new HashMap<>();
    }

    public static ConfigurationManager getInstance() {
        if (instance == null) {
            lock.lock();
            try {
                if (instance == null) {
                    instance = new ConfigurationManager();
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }

    public void loadFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            settings.clear();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    settings.put(parts[0].trim(), parts[1].trim());
                }
            }
            System.out.println("Настройки успешно загружены из файла.");
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке настроек: " + e.getMessage());
        }
    }

    public void saveToFile(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Map.Entry<String, String> entry : settings.entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue());
                writer.newLine();
            }
            System.out.println("Настройки успешно сохранены в файл.");
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении настроек: " + e.getMessage());
        }
    }

    public void setSetting(String key, String value) {
        settings.put(key, value);
    }

    public String getSetting(String key) {
        if (!settings.containsKey(key)) {
            throw new IllegalArgumentException("Настройка не найдена: " + key);
        }
        return settings.get(key);
    }

    public void printSettings() {
        System.out.println("Текущие настройки:");
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }
}


class Report {
    private String header;
    private String content;
    private String footer;

    public void setHeader(String header) { this.header = header; }
    public void setContent(String content) { this.content = content; }
    public void setFooter(String footer) { this.footer = footer; }

    public void display() {
        System.out.println("----- Отчет -----");
        if (header != null) System.out.println(header);
        if (content != null) System.out.println(content);
        if (footer != null) System.out.println(footer);
        System.out.println("-----------------");
    }

    public String toHtml() {
        return "<html><body>" +
                (header != null ? "<h1>" + header + "</h1>" : "") +
                (content != null ? "<p>" + content + "</p>" : "") +
                (footer != null ? "<footer>" + footer + "</footer>" : "") +
                "</body></html>";
    }
}

interface IReportBuilder {
    void setHeader(String header);
    void setContent(String content);
    void setFooter(String footer);
    Report getReport();
}

class TextReportBuilder implements IReportBuilder {
    private final Report report = new Report();

    public void setHeader(String header) { report.setHeader("HEADER: " + header); }
    public void setContent(String content) { report.setContent("CONTENT: " + content); }
    public void setFooter(String footer) { report.setFooter("FOOTER: " + footer); }
    public Report getReport() { return report; }
}

class HtmlReportBuilder implements IReportBuilder {
    private final Report report = new Report();

    public void setHeader(String header) { report.setHeader("<h1>" + header + "</h1>"); }
    public void setContent(String content) { report.setContent("<p>" + content + "</p>"); }
    public void setFooter(String footer) { report.setFooter("<footer>" + footer + "</footer>"); }
    public Report getReport() { return report; }
}

class ReportDirector {
    public void constructReport(IReportBuilder builder, String header, String content, String footer) {
        builder.setHeader(header);
        builder.setContent(content);
        builder.setFooter(footer);
    }
}


public class dom5 {
    public static void main(String[] args) {
        // ==== Тест Singleton ====
        System.out.println("=== Тест Singleton ===");

        ConfigurationManager config1 = ConfigurationManager.getInstance();
        ConfigurationManager config2 = ConfigurationManager.getInstance();

        config1.setSetting("AppName", "My Java App");
        config1.setSetting("Version", "1.0.0");

        System.out.println("config1 == config2 ? " + (config1 == config2));
        config2.printSettings();

        config1.saveToFile("config.txt");

        config2.loadFromFile("config.txt");
        System.out.println("AppName из файла: " + config2.getSetting("AppName"));

        System.out.println("\n=== Тест Builder ===");

        ReportDirector director = new ReportDirector();

        IReportBuilder textBuilder = new TextReportBuilder();
        director.constructReport(textBuilder, "Отчет о продажах", "Продажи выросли на 20%", "Конец отчета");
        Report textReport = textBuilder.getReport();
        textReport.display();

        IReportBuilder htmlBuilder = new HtmlReportBuilder();
        director.constructReport(htmlBuilder, "Sales Report", "Revenue increased by 20%", "End of report");
        Report htmlReport = htmlBuilder.getReport();
        System.out.println("\nHTML отчет:");
        System.out.println(htmlReport.toHtml());


        System.out.println("\n=== Тест многопоточности Singleton ===");

        Runnable task = () -> {
            ConfigurationManager cfg = ConfigurationManager.getInstance();
            System.out.println(Thread.currentThread().getName() + " получил экземпляр: " + cfg.hashCode());
        };

        Thread t1 = new Thread(task, "Поток 1");
        Thread t2 = new Thread(task, "Поток 2");
        Thread t3 = new Thread(task, "Поток 3");

        t1.start();
        t2.start();
        t3.start();
    }
}