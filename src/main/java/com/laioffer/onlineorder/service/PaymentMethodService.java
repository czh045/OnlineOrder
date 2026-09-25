package com.laioffer.onlineorder.service;

import com.laioffer.onlineorder.entity.PaymentMethodEntity;
import com.laioffer.onlineorder.model.AddPaymentMethodBody;
import com.laioffer.onlineorder.model.PaymentMethodDto;
import com.laioffer.onlineorder.repository.PaymentMethodRepository;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

@Service
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    public PaymentMethodService(PaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }

    public List<PaymentMethodDto> getPaymentMethods(long customerId) {
        return paymentMethodRepository.findByCustomerId(customerId).stream()
                .map(PaymentMethodDto::new)
                .toList();
    }

    public PaymentMethodDto addPaymentMethod(long customerId, AddPaymentMethodBody body) {
        if (body == null || isBlank(body.cardHolder()) || isBlank(body.cardNumber())) {
            throw new IllegalArgumentException("Card holder and card number are required");
        }
        if (body.expiryMonth() == null || body.expiryYear() == null
                || body.expiryMonth() < 1 || body.expiryMonth() > 12) {
            throw new IllegalArgumentException("A valid expiry month and year are required");
        }
        if (YearMonth.of(body.expiryYear(), body.expiryMonth()).isBefore(YearMonth.now())) {
            throw new IllegalArgumentException("This payment card has expired");
        }

        String digitsOnly = body.cardNumber().replaceAll("[\\s-]", "");
        if (!digitsOnly.matches("\\d{12,19}")) {
            throw new IllegalArgumentException("Invalid card number");
        }

        PaymentMethodEntity saved = paymentMethodRepository.save(new PaymentMethodEntity(
                null,
                customerId,
                body.cardHolder().trim(),
                detectBrand(digitsOnly),
                digitsOnly.substring(digitsOnly.length() - 4),
                body.expiryMonth(),
                body.expiryYear()
        ));
        return new PaymentMethodDto(saved);
    }

    private String detectBrand(String digitsOnly) {
        if (digitsOnly.startsWith("4")) {
            return "Visa";
        }
        if (digitsOnly.startsWith("34") || digitsOnly.startsWith("37")) {
            return "Amex";
        }
        if (digitsOnly.startsWith("6")) {
            return "Discover";
        }
        int prefix = Integer.parseInt(digitsOnly.substring(0, 2));
        if (prefix >= 51 && prefix <= 55) {
            return "Mastercard";
        }
        return "Card";
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
