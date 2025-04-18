package com.newbit.newbitfeatureservice.product.command.controller;

import com.newbit.newbitfeatureservice.product.command.application.dto.request.ProductCreateRequest;
import com.newbit.newbitfeatureservice.product.command.application.dto.response.ProductResponse;
import com.newbit.newbitfeatureservice.product.command.application.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    @DisplayName("전체 상품 목록 조회")
    void getAllProducts() throws Exception {
        // given
        ProductResponse product1 = createProduct(1L, "다이아몬드 100개", new BigDecimal("10000"), 100);
        ProductResponse product2 = createProduct(2L, "다이아몬드 500개", new BigDecimal("45000"), 500);
        List<ProductResponse> products = Arrays.asList(product1, product2);

        given(productService.getAllProducts()).willReturn(products);

        // when & then
        mockMvc.perform(get("/api/v1/products")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].productId").value(1L))
                .andExpect(jsonPath("$.data[0].name").value("다이아몬드 100개"))
                .andExpect(jsonPath("$.data[1].productId").value(2L))
                .andExpect(jsonPath("$.data[1].name").value("다이아몬드 500개"));
    }

    @Test
    @DisplayName("상품 단일 조회")
    void getProductById() throws Exception {
        // given
        Long productId = 1L;
        ProductResponse product = createProduct(productId, "다이아몬드 100개", new BigDecimal("10000"), 100);

        given(productService.getProductById(productId)).willReturn(product);

        // when & then
        mockMvc.perform(get("/api/v1/products/{id}", productId)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.productId").value(productId))
                .andExpect(jsonPath("$.data.name").value("다이아몬드 100개"))
                .andExpect(jsonPath("$.data.price").value(10000))
                .andExpect(jsonPath("$.data.diamondAmount").value(100));
    }

    @Test
    @DisplayName("상품 생성 테스트")
    void createProduct() throws Exception {
        // given
        String requestJson = "{\"name\":\"다이아몬드 200개\",\"price\":20000,\"diamondAmount\":200,\"description\":\"중간 크기 다이아몬드 패키지\"}";
        
        ProductResponse createdProduct = createProduct(1L, "다이아몬드 200개", new BigDecimal("20000"), 200);
        given(productService.createProduct(any(ProductCreateRequest.class))).willReturn(createdProduct);

        // when & then
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.productId").value(1L))
                .andExpect(jsonPath("$.data.name").value("다이아몬드 200개"));
    }

    private ProductResponse createProduct(Long id, String name, BigDecimal price, Integer diamondAmount) {
        return ProductResponse.builder()
                .productId(id)
                .name(name)
                .price(price)
                .diamondAmount(diamondAmount)
                .description("기본 다이아몬드 패키지")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
} 