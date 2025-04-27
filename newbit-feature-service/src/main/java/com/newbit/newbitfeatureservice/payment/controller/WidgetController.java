package com.newbit.newbitfeatureservice.payment.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.Payment;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.PaymentMethod;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.PaymentStatus;
import com.newbit.newbitfeatureservice.payment.command.application.service.PaymentCommandService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
public class WidgetController {

    private final PaymentCommandService paymentCommandService;
    
    @Value("${payment.toss.secret-key}")
    private String secretKey;
    
    @Value("${payment.toss.success-url}")
    private String successUrl;
    
    @Value("${payment.toss.fail-url}")
    private String failUrl;
    
    @GetMapping("/widget")
    public String widgetPage(Model model, 
                         @RequestParam String orderId,
                         @RequestParam BigDecimal amount,
                         @RequestParam String orderName,
                         @RequestParam Long userId) {
        model.addAttribute("clientKey", "test_ck_D5GePWvyJnrK0W0k6q8gLzN97Eoq");
        model.addAttribute("customerKey", "CUSTOMER-" + userId);
        model.addAttribute("orderId", orderId);
        model.addAttribute("amount", amount);
        model.addAttribute("orderName", orderName);
        model.addAttribute("successUrl", successUrl);
        model.addAttribute("failUrl", failUrl);
        
        return "payment/widget";
    }

    @PostMapping("/confirm")
    @ResponseBody
    public ResponseEntity<JSONObject> confirmPayment(@RequestBody String jsonBody) throws Exception {
        JSONParser parser = new JSONParser();
        String orderId;
        String amount;
        String paymentKey;
        
        try {
            // 클라이언트에서 받은 JSON 요청 바디입니다.
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

        // 토스페이먼츠 API는 시크릿 키를 사용자 ID로 사용하고, 비밀번호는 사용하지 않습니다.
        // 비밀번호가 없다는 것을 알리기 위해 시크릿 키 뒤에 콜론을 추가합니다.
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        // 결제를 승인하면 결제수단에서 금액이 차감돼요.
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

        // 결제 응답 데이터 처리
        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        JSONObject responseJson = (JSONObject) parser.parse(reader);
        responseStream.close();
        
        // 결제 성공시 DB에 저장
        if (isSuccess) {
            try {
                // 수신된 결제 데이터 가져오기
                String confirmedPaymentKey = (String) responseJson.get("paymentKey");
                String confirmedOrderId = (String) responseJson.get("orderId");
                String orderName = (String) responseJson.get("orderName");
                BigDecimal confirmedAmount = new BigDecimal(responseJson.get("totalAmount").toString());
                String receiptUrl = (String) responseJson.get("receipt").toString();
                PaymentMethod paymentMethod = PaymentMethod.valueOf(
                    ((String) responseJson.get("method")).toUpperCase()
                );
                Long userId = Long.parseLong(
                    ((String) responseJson.get("customerKey")).replace("CUSTOMER-", "")
                );
                
                // DB에 결제 정보 저장 또는 업데이트
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
   

    @GetMapping("/success")
    public String paymentSuccess(
            @RequestParam(required = false) String paymentKey,
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String amount,
            Model model) {
            
        model.addAttribute("paymentKey", paymentKey);
        model.addAttribute("orderId", orderId);
        model.addAttribute("amount", amount);
        
        return "payment/success";
    }
    
    @GetMapping("/fail")
    public String paymentFail(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) String orderId,
            Model model) {
            
        model.addAttribute("code", code);
        model.addAttribute("message", message);
        model.addAttribute("orderId", orderId);
        
        return "payment/fail";
    }
}