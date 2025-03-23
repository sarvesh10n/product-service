package com.scaler.capstone.product.clients;

import com.scaler.capstone.product.dto.PaymentClientDTO;
import com.scaler.capstone.product.exceptions.PaymentClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service("razorpayPaymentClient")
public class RazorpayPaymentClient implements PaymentServiceClient{

    private RestTemplate restTemplate;
    private String paymentServiceBaseUrl;

    public RazorpayPaymentClient(RestTemplate restTemplate,
                                 @Value("${paymentservice.razorpay.api.url}") String paymentServiceApiUrl,
                                 @Value("${paymentservice.razorpay.api.paths.payments}") String paymentServicePaymentApiPath) {
        this.restTemplate = restTemplate;
        this.paymentServiceBaseUrl = paymentServiceApiUrl+paymentServicePaymentApiPath;
    }

    @Override
    public PaymentClientDTO createPaymentOrder(String invoiceNumber, String currency, Double amount) throws PaymentClientException {
        URI uri = UriComponentsBuilder.fromUriString(paymentServiceBaseUrl + "/create-paymentLink")
                .queryParam("amount", amount)
                .queryParam("currency", currency)
                .queryParam("invoiceNo", invoiceNumber)
                .build()
                .toUri();

        // Use an empty Map as the request body
        Map<String, Object> requestBody = new HashMap<>();

        ResponseEntity<PaymentClientDTO> response =
                restTemplate.postForEntity(uri,requestBody,PaymentClientDTO.class);
        if(response.getStatusCode()!= HttpStatus.CREATED)
        {
            throw new PaymentClientException("Payment link not created.");
        }
        System.out.println();
        return response.getBody();
    }

    @Override
    public PaymentClientDTO getPaymentStatus(String paymentOrderId) throws PaymentClientException {

        ResponseEntity<PaymentClientDTO> response =
                restTemplate.getForEntity(paymentServiceBaseUrl+"/details/{orderId}",PaymentClientDTO.class,paymentOrderId);

        if(response.getStatusCode()!= HttpStatus.OK)
        {
            throw new PaymentClientException("Could not get payment status.");
        }
        return response.getBody();
    }

    @Override
    public PaymentClientDTO processRefund(String paymentOrderId) throws PaymentClientException {
        // Use an empty Map as the request body
        Map<String, Object> requestBody = new HashMap<>();

        ResponseEntity<PaymentClientDTO> response =
                restTemplate.postForEntity(paymentServiceBaseUrl+"/create-refund/{orderId}",requestBody,PaymentClientDTO.class,paymentOrderId);

        if(response.getStatusCode()!= HttpStatus.CREATED)
        {
            throw new PaymentClientException("Could not create refund request.");
        }
        return response.getBody();
    }
}
