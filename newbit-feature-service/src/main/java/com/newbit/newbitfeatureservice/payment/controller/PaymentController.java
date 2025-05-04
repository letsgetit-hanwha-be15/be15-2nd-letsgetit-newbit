package com.newbit.newbitfeatureservice.payment.controller;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import com.newbit.newbitfeatureservice.payment.command.application.dto.request.PaymentCancelRequest;
import com.newbit.newbitfeatureservice.payment.command.application.dto.request.CreateOrderRequest;
import com.newbit.newbitfeatureservice.payment.command.application.dto.response.PaymentApproveResponse;
import com.newbit.newbitfeatureservice.payment.command.application.dto.response.PaymentRefundResponse;
import com.newbit.newbitfeatureservice.payment.command.application.service.PaymentCommandService;
import com.newbit.newbitfeatureservice.payment.command.application.service.RefundCommandService;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.PaymentMethod;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("payments")
@RequiredArgsConstructor
@Tag(name = "결제 API", description = "토스 결제 위젯 연동 API")
public class PaymentController extends AbstractApiController {

    private final PaymentCommandService paymentCommandService;
    private final RefundCommandService refundCommandService;
    
    @Value("${payment.toss.secret-key}")
    private String secretKey;
    
    @Operation(
        summary = "결제 승인", 
        description = "토스 결제 위젯에서 결제 완료 후 결제 승인 요청을 처리합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "결제 승인 성공", 
            content = @Content(schema = @Schema(implementation = PaymentApproveResponse.class)))
    })
    @PostMapping("/confirm")
    public ResponseEntity<JSONObject> confirmPayment(@RequestBody String jsonBody) throws Exception {
        JSONParser parser = new JSONParser();
        String orderId;
        String amount;
        String paymentKey;
        
        try {
            JSONObject requestData = (JSONObject) parser.parse(jsonBody);
            paymentKey = (String) requestData.get("paymentKey");
            orderId = (String) requestData.get("orderId");
            amount = (String) requestData.get("amount");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        
        JSONObject obj = new JSONObject();
        obj.put("orderId", orderId);
        obj.put("amount", amount);
        obj.put("paymentKey", paymentKey);

        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(obj.toString().getBytes(StandardCharsets.UTF_8));

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200;

        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        JSONObject responseJson = (JSONObject) parser.parse(reader);
        responseStream.close();
        
        if (isSuccess) {
            try {
                String confirmedPaymentKey = (String) responseJson.get("paymentKey");
                String confirmedOrderId = (String) responseJson.get("orderId");
                String orderName = (String) responseJson.get("orderName");
                BigDecimal confirmedAmount = new BigDecimal(responseJson.get("totalAmount").toString());
                String receiptUrl = (String) responseJson.get("receipt").toString();
                
                PaymentMethod paymentMethod;
                try {
                    paymentMethod = PaymentMethod.valueOf(
                        ((String) responseJson.get("method")).toUpperCase()
                    );
                } catch (IllegalArgumentException e) {
                    paymentMethod = PaymentMethod.CARD;
                }

                com.newbit.newbitfeatureservice.payment.command.domain.aggregate.Payment payment = paymentCommandService.findByOrderId(confirmedOrderId);
                Long userId = payment.getUserId();

                paymentCommandService.processPaymentSuccess(
                    confirmedPaymentKey, 
                    confirmedOrderId, 
                    orderName,
                    confirmedAmount, 
                    paymentMethod, 
                    userId,
                    receiptUrl
                );
                
                log.info("결제 승인 성공: paymentKey={}, orderId={}", confirmedPaymentKey, confirmedOrderId);
            } catch (Exception e) {
                log.error("결제 데이터 처리 중 오류: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseJson);
            }
        } else {
            log.error("결제 승인 실패: {}", responseJson.toJSONString());
        }

        return ResponseEntity.status(code).body(responseJson);
    }
    
    @Operation(
        summary = "결제 취소", 
        description = "결제를 취소합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "취소 성공", 
            content = @Content(schema = @Schema(implementation = PaymentRefundResponse.class)))
    })
    @PostMapping("/cancel")
    public ResponseEntity<ApiResponse<PaymentRefundResponse>> cancelPayment(
            @RequestBody PaymentCancelRequest request) {
        
        log.info("결제 취소 요청: paymentKey={}, reason={}", request.getPaymentKey(), request.getReason());
        
        PaymentRefundResponse response = refundCommandService.cancelPaymentByKey(
                request.getPaymentKey(), 
                request.getReason()
        );
        
        return successResponse(response);
    }
    
    @Operation(
        summary = "결제 상태 조회", 
        description = "주문번호로 결제 상태를 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", 
            content = @Content(schema = @Schema(implementation = PaymentApproveResponse.class)))
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<PaymentApproveResponse>> getPaymentStatus(
            @Parameter(description = "주문번호") @PathVariable String orderId) {
        
        PaymentApproveResponse response = paymentCommandService.getPaymentByOrderId(orderId);
        return successResponse(response);
    }

    @Operation(
        summary = "주문 생성",
        description = "결제 전에 주문 정보를 저장합니다."
    )
    @PostMapping("/order")
    public ResponseEntity<ApiResponse<String>> createOrder(@RequestBody CreateOrderRequest request) {
        paymentCommandService.createOrder(request);
        return successResponse(request.getOrderId());
    }
} 