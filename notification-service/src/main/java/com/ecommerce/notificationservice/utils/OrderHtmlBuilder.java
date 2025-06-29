package com.ecommerce.notificationservice.utils;

import com.ecommerce.notificationservice.kafka.event.OrderCancelledEvent;
import com.ecommerce.notificationservice.kafka.event.OrderCreatedEvent;

import java.math.BigDecimal;

public class OrderHtmlBuilder {

    private OrderHtmlBuilder() {}

    public static String buildOrderCreatedHtmlContent(OrderCreatedEvent event) {
        StringBuilder html = new StringBuilder();

        html
                .append("<html><body style=\"font-family:Arial, sans-serif; line-height:1.6;\">")
                .append("<h2 style=\"color:#2d3748\">Thank you for your order!</h2>")
                .append("<p>Order <strong>#").append(event.getOrderId()).append("</strong> has been confirmed.</p>")
                .append("<p><strong>Total price:</strong> ").append(event.getTotalAmount()).append(" $</p>");

        if (event.getItems() != null && !event.getItems().isEmpty()) {
            html
                    .append("<h3>Order details:</h3>")
                    .append("<table border=\"1\" cellpadding=\"8\" cellspacing=\"0\" style=\"border-collapse: collapse; width:100%;\">")
                    .append("<thead style=\"background-color:#f2f2f2;\">")
                    .append("<tr>")
                    .append("<th style=\"text-align:left;\">Product Name</th>")
                    .append("<th style=\"text-align:right;\">Quantity</th>")
                    .append("<th style=\"text-align:right;\">Unit Price</th>")
                    .append("<th style=\"text-align:right;\">Total Price</th>")
                    .append("</tr>")
                    .append("</thead>")
                    .append("<tbody>");

            for (OrderCreatedEvent.OrderItem item : event.getItems()) {
                html
                        .append("<tr>")
                        .append("<td>").append(item.getName()).append("</td>")
                        .append("<td style=\"text-align:right;\">").append(item.getQuantity()).append("</td>")
                        .append("<td style=\"text-align:right;\">").append(item.getPrice()).append(" $</td>")
                        .append("<td style=\"text-align:right;\">").append(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))).append("$</td>")
                        .append("</tr>");
            }

            html.append("</tbody>").append("</table>");
        }

        html
                .append("<p style=\"color:gray; font-size:12px;\">&copy; 2025 E-Commerce App</p>")
                .append("</body></html>");

        return html.toString();
    }

    public static String buildOrderCancelledHtmlContent(OrderCancelledEvent event) {
        StringBuilder html = new StringBuilder();

        html
                .append("<html><body>")
                .append("<h2>We're sorry! Your order has been cancelled.</h2>")
                .append("<p><strong>Order #").append(event.getOrderId()).append("</p>")
                .append("<p><strong>Reason: </p>").append(event.getReason()).append("</p>")
                .append("</body></html>");

        return html.toString();
    }
}
