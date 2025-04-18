package com.newbit.newbitfeatureservice.product.command.application.service;

import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.product.command.application.dto.request.ProductCreateRequest;
import com.newbit.newbitfeatureservice.product.command.application.dto.request.ProductUpdateRequest;
import com.newbit.newbitfeatureservice.product.command.application.dto.response.ProductResponse;
import com.newbit.newbitfeatureservice.product.command.domain.aggregate.Product;
import com.newbit.newbitfeatureservice.product.command.domain.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    
    private static final Logger log = LoggerFactory.getLogger(ProductServiceTest.class);

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = createProduct(1L, "다이아몬드 100개", new BigDecimal("10000"), 100);
        product2 = createProduct(2L, "다이아몬드 500개", new BigDecimal("45000"), 500);
    }

    @Test
    @DisplayName("모든 상품 조회")
    void getAllProducts() {
        // given
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        // when
        List<ProductResponse> result = productService.getAllProducts();

        // then
        assertEquals(2, result.size());
        assertEquals("다이아몬드 100개", result.get(0).getName());
        assertEquals("다이아몬드 500개", result.get(1).getName());
        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("ID로 상품 조회 성공")
    void getProductById_success() {
        // given
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));

        // when
        ProductResponse result = productService.getProductById(productId);

        // then
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals("다이아몬드 100개", result.getName());
        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 상품 조회 실패")
    void getProductById_notFound() {
        // given
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> productService.getProductById(productId));
        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, exception.getErrorCode());
        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("상품 생성 성공")
    void createProduct_success() throws Exception {
        // given
        ProductCreateRequest request = new ProductCreateRequest();
        setField(request, "name", "다이아몬드 1000개");
        setField(request, "price", new BigDecimal("85000"));
        setField(request, "diamondAmount", 1000);
        setField(request, "description", "대량 다이아몬드 패키지");

        Product newProduct = createProduct(3L, "다이아몬드 1000개", new BigDecimal("85000"), 1000);
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));
        when(productRepository.save(any(Product.class))).thenReturn(newProduct);

        // when
        ProductResponse result = productService.createProduct(request);

        // then
        assertNotNull(result);
        assertEquals(3L, result.getProductId());
        assertEquals("다이아몬드 1000개", result.getName());
        assertEquals(new BigDecimal("85000"), result.getPrice());
        assertEquals(1000, result.getDiamondAmount());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("중복된 상품명으로 생성 실패")
    void createProduct_duplicateName() throws Exception {
        // given
        ProductCreateRequest request = new ProductCreateRequest();
        setField(request, "name", "다이아몬드 100개");
        setField(request, "price", new BigDecimal("10000"));
        setField(request, "diamondAmount", 100);
        setField(request, "description", "기본 다이아몬드 패키지");

        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> productService.createProduct(request));
        assertEquals(ErrorCode.PRODUCT_NAME_DUPLICATE, exception.getErrorCode());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("잘못된 다이아몬드 수량으로 생성 실패")
    void createProduct_invalidDiamondAmount() throws Exception {
        // given
        ProductCreateRequest request = new ProductCreateRequest();
        setField(request, "name", "다이아몬드 0개");
        setField(request, "price", new BigDecimal("0"));
        setField(request, "diamondAmount", 0);
        setField(request, "description", "잘못된 패키지");

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> productService.createProduct(request));
        assertEquals(ErrorCode.PRODUCT_INVALID_DIAMOND_AMOUNT, exception.getErrorCode());
        verify(productRepository, never()).save(any(Product.class));
        verify(productRepository, never()).findAll();
    }

    @Test
    @DisplayName("잘못된 가격으로 생성 실패")
    void createProduct_invalidPrice() throws Exception {
        // given
        ProductCreateRequest request = new ProductCreateRequest();
        setField(request, "name", "다이아몬드 100개");
        setField(request, "price", new BigDecimal("-1000"));
        setField(request, "diamondAmount", 100);
        setField(request, "description", "잘못된 가격");

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> productService.createProduct(request));
        assertEquals(ErrorCode.PRODUCT_PRICE_MISMATCH, exception.getErrorCode());
        verify(productRepository, never()).save(any(Product.class));
        verify(productRepository, never()).findAll();
    }

    @Test
    @DisplayName("상품 정보 수정 성공")
    void updateProduct_success() throws Exception {
        // given
        Long productId = 1L;
        ProductUpdateRequest request = new ProductUpdateRequest();
        setField(request, "name", "다이아몬드 150개 특가");
        setField(request, "price", new BigDecimal("14000"));
        setField(request, "diamondAmount", 150);
        setField(request, "description", "특가 다이아몬드 패키지");

        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        // when
        ProductResponse result = productService.updateProduct(productId, request);

        // then
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals("다이아몬드 150개 특가", product1.getName());
        assertEquals(new BigDecimal("14000"), product1.getPrice());
        assertEquals(150, product1.getDiamondAmount());
        verify(productRepository).save(product1);
    }

    @Test
    @DisplayName("존재하지 않는 상품 수정 실패")
    void updateProduct_notFound() throws Exception {
        // given
        Long productId = 999L;
        ProductUpdateRequest request = new ProductUpdateRequest();
        setField(request, "name", "다이아몬드 150개 특가");
        setField(request, "price", new BigDecimal("14000"));
        setField(request, "diamondAmount", 150);
        setField(request, "description", "특가 다이아몬드 패키지");

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> productService.updateProduct(productId, request));
        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, exception.getErrorCode());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("상품 활성화 성공")
    void toggleProductActivation_activate() {
        // given
        Long productId = 1L;
        product1.deactivate(); // 먼저 비활성화
        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        // when
        ProductResponse result = productService.toggleProductActivation(productId, true);

        // then
        assertTrue(result.isActive());
        verify(productRepository).save(product1);
    }

    @Test
    @DisplayName("상품 비활성화 성공")
    void toggleProductActivation_deactivate() {
        // given
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        // when
        ProductResponse result = productService.toggleProductActivation(productId, false);

        // then
        assertFalse(result.isActive());
        verify(productRepository).save(product1);
    }

    @Test
    @DisplayName("상품 삭제 성공")
    void deleteProduct_success() {
        // given
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));
        doNothing().when(productRepository).deleteById(productId);

        // when
        productService.deleteProduct(productId);

        // then
        verify(productRepository).deleteById(productId);
    }

    @Test
    @DisplayName("존재하지 않는 상품 삭제 실패")
    void deleteProduct_notFound() {
        // given
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> productService.deleteProduct(productId));
        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, exception.getErrorCode());
        verify(productRepository, never()).deleteById(any());
    }

    private Product createProduct(Long id, String name, BigDecimal price, Integer diamondAmount) {
        Product product = Product.createDiamondProduct(name, price, diamondAmount, "테스트 상품 설명");
        try {
            Field field = Product.class.getDeclaredField("productId");
            field.setAccessible(true);
            field.set(product, id);
        } catch (Exception e) {
            log.error("상품 ID 설정 실패", e);
        }
        return product;
    }
    
    private <T> void setField(T object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }
} 