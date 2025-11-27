public class dom11 {
    public static abstract class User {
        protected int id;
        protected String name;
        protected String email;
        protected String address;
        protected String phone;
        protected String role;

        public void register() {}
        public void login() {}
        public void updateData() {}
    }

    public static class Client extends User {
        private int loyaltyPoints;

        public void addPoints(int points) {
            loyaltyPoints += points;
        }
    }

    public static class Admin extends User {
        public void logAction(String action) {
            System.out.println("Admin log: " + action);
        }
    }


    public static class Category {
        int id;
        String name;
    }

    public static class Product {
        int id;
        String title;
        String description;
        double price;
        int stock;
        Category category;

        public void create() {}
        public void update() {}
        public void delete() {}
    }


    public static class Order {
        int id;
        String createdAt;
        String status;
        Client client;
        java.util.List<Product> products;
        double totalAmount;

        public void place() {}
        public void cancel() {}
        public void pay() {}
    }

    public static class Cart {
        java.util.List<Product> items = new java.util.ArrayList<>();
        PromoCode promoCode;

        public void addItem(Product p) {
            items.add(p);
        }

        public void removeItem(Product p) {
            items.remove(p);
        }

        public void applyPromoCode(PromoCode code) {
            this.promoCode = code;
        }
    }

    public static class PromoCode {
        String code;
        double discount;

        public boolean validate() { return true; }
    }


    public static class Payment {
        int id;
        String type;
        double amount;
        String status;
        String date;

        public void process() {}
        public void refund() {}
    }


    public static class Delivery {
        int id;
        String address;
        String status;
        String courier;

        public void send() {}
        public void track() {}
        public void complete() {}
    }


    public static class Review {
        int id;
        String text;
        int rating;
        String date;

        public void submit() {}
    }



    public static abstract class Discount {
        protected double value;
        public abstract double apply(double amount);
    }

    public static class ProductDiscount extends Discount {
        @Override
        public double apply(double amount) {
            return amount - value;
        }
    }

    public static class PromoCodeDiscount extends Discount {
        @Override
        public double apply(double amount) {
            return amount * (1 - value);
        }
    }



    public static void main(String[] args) {
        Client c = new Client();
        c.name = "Test User";
        c.addPoints(10);
        System.out.println("Client OK");
    }
}
