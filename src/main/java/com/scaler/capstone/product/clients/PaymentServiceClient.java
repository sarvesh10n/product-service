package com.scaler.capstone.product.clients;


import com.scaler.capstone.product.dto.PaymentClientDTO;
import com.scaler.capstone.product.exceptions.PaymentClientException;

public interface PaymentServiceClient {
    PaymentClientDTO createPaymentOrder(String invoiceNumber, String currency, Double amount) throws PaymentClientException;
    PaymentClientDTO getPaymentStatus(String paymentOrderId) throws PaymentClientException;
    PaymentClientDTO processRefund(String paymentOrderId) throws PaymentClientException;
}
