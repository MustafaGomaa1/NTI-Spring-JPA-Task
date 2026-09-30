package com.nti;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.nti.config.AppConfig;
import com.nti.dto.OrderSummeryDTO;
import com.nti.model.Address;
import com.nti.model.Customer;
import com.nti.model.Method;
import com.nti.model.OrderItem;
import com.nti.model.Payment;
import com.nti.model.Product;
import com.nti.service.CustomerService;
import com.nti.service.OrderService;
import com.nti.service.ProductService;

public class Main {
    private final Scanner scanner;
    private final CustomerService customerService;
    private final ProductService productService;
    private final OrderService orderService;

    private Main(Scanner scanner, CustomerService customerService, ProductService productService,
            OrderService orderService) {
        this.scanner = scanner;
        this.customerService = customerService;
        this.productService = productService;
        this.orderService = orderService;
    }

    public static void main(String[] args) {
        try (var context = new AnnotationConfigApplicationContext(AppConfig.class);
                var scanner = new Scanner(System.in)) {
            Main app = new Main(
                    scanner,
                    context.getBean(CustomerService.class),
                    context.getBean(ProductService.class),
                    context.getBean(OrderService.class));
            app.run();
        } catch (RuntimeException exception) {
            System.err.println("Unable to start the shop application: " + exception.getMessage());
            System.exit(1);
        }
    }

    private void run() {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Choose an option: ");
            try {
                switch (choice) {
                    case 2 -> registerCustomer();
                    case 3 -> addProduct();
                    case 4 -> restockProduct();
                    case 5 -> changeProductPrice();
                    case 6 -> placeOrder();
                    case 7 -> payForOrder();
                    case 8 -> shipOrder();
                    case 9 -> cancelOrder();
                    case 10 -> showOrderSummary();
                    case 0 -> running = false;
                    default -> System.out.println("Choose an option from 0 to 10.");
                }
            } catch (RuntimeException exception) {
                System.out.println("Operation failed: " + exception.getMessage());
            }
            System.out.println();
        }
        System.out.println("Goodbye.");
    }

    private void printMenu() {
        System.out.println("""
                === Shop Management ===
                1. List products
                2. Register customer
                3. Add product
                4. Restock product
                5. Change product price
                6. Place order
                7. Pay for order
                8. Ship order
                9. Cancel order
                10. Show order summary
                0. Exit
                """);
    }

    private void registerCustomer() {
        Customer customer = new Customer();
        customer.setEmail(readText("Email: "));
        Address address = new Address();
        address.setStreet(readText("Street: "));
        address.setCity(readText("City: "));
        address.setCountry(readText("Country: "));
        customer.setShippingAddress(address);
        customerService.register(customer);
        System.out.println("Customer registered with ID " + customer.getId() + ".");
    }

    private void addProduct() {
        Product product = new Product();
        product.setSku(readText("SKU: "));
        product.setName(readText("Name: "));
        product.setPrice(readPositiveDecimal("Price: "));
        product.setStock(readNonNegativeInt("Starting stock: "));
        productService.addProduct(product);
        System.out.println("Product added with ID " + product.getId() + ".");
    }

    private void restockProduct() {
        int productId = readPositiveInt("Product ID: ");
        int quantity = readPositiveInt("Quantity to add: ");
        productService.restock(productId, quantity);
        System.out.println("Product restocked.");
    }

    private void changeProductPrice() {
        int productId = readPositiveInt("Product ID: ");
        BigDecimal price = readPositiveDecimal("New price: ");
        productService.changePrice(productId, price);
        System.out.println("Product price updated.");
    }

    private void placeOrder() {
        int customerId = readPositiveInt("Customer ID: ");
        Map<Integer, Integer> quantities = new LinkedHashMap<>();
        while (true) {
            int productId = readPositiveInt("Product ID (0 to finish): ", true);
            if (productId == 0) {
                break;
            }
            int quantity = readPositiveInt("Quantity: ");
            quantities.merge(productId, quantity, Integer::sum);
        }
        int orderId = orderService.placeOrder(customerId, quantities);
        System.out.println("Order placed with ID " + orderId + ".");
    }

    private void payForOrder() {
        int orderId = readPositiveInt("Order ID: ");
        BigDecimal amount = readPositiveDecimal("Payment amount: ");
        Method method = readEnum("Payment method", Method.class);
        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setMethod(method);
        orderService.pay(orderId, payment);
        System.out.println("Payment recorded.");
    }

    private void shipOrder() {
        orderService.ship(readPositiveInt("Order ID: "));
        System.out.println("Order shipped.");
    }

    private void cancelOrder() {
        orderService.cancel(readPositiveInt("Order ID: "));
        System.out.println("Order cancelled and stock restored.");
    }

    private void showOrderSummary() {
        OrderSummeryDTO summary = orderService.getOrderSummery(readPositiveInt("Order ID: "));
        System.out.printf("Order %d | customer %s | status %s | total %s%n",
                summary.getOrder().getId(),
                summary.getCustomer().getEmail(),
                summary.getOrder().getStatus(),
                summary.getTotalPrice());
        for (OrderItem item : summary.getOrderItem()) {
            System.out.printf("  %s x %d @ %s%n",
                    item.getProduct().getName(), item.getQuantity(), item.getUnitPrice());
        }
    }

    private String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("A value is required.");
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException exception) {
                System.out.println("Enter a whole number.");
            }
        }
    }

    private int readPositiveInt(String prompt) {
        return readPositiveInt(prompt, false);
    }

    private int readPositiveInt(String prompt, boolean allowZero) {
        while (true) {
            int value = readInt(prompt);
            if (value > 0 || (allowZero && value == 0)) {
                return value;
            }
            System.out.println(allowZero ? "Enter zero or a positive number." : "Enter a positive number.");
        }
    }

    private int readNonNegativeInt(String prompt) {
        while (true) {
            int value = readInt(prompt);
            if (value >= 0) {
                return value;
            }
            System.out.println("Enter zero or a positive number.");
        }
    }

    private BigDecimal readPositiveDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                BigDecimal value = new BigDecimal(scanner.nextLine().trim());
                if (value.signum() > 0) {
                    return value;
                }
            } catch (NumberFormatException exception) {
                System.out.println("Enter a valid positive amount.");
                continue;
            }
            System.out.println("Enter a valid positive amount.");
        }
    }

    private <T extends Enum<T>> T readEnum(String label, Class<T> enumType) {
        while (true) {
            System.out.print(label + " (" + String.join(", ",
                    java.util.Arrays.stream(enumType.getEnumConstants()).map(Enum::name).toList()) + "): ");
            try {
                return Enum.valueOf(enumType, scanner.nextLine().trim().toUpperCase());
            } catch (IllegalArgumentException exception) {
                System.out.println("Choose one of the listed values.");
            }
        }
    }
}
