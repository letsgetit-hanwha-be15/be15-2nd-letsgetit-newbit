<script setup>

const { mentor } = defineProps({
  mentor: {
    type: Object,
    required: true
  }
})
</script>

<template>
  <router-link
      class="item"
      :to="`/mentors/${mentor.mentorId}`">
    <img :src="mentor.profileImageUrl" :alt="mentor.profileImageUrl" class="mentor-image">
    <div class="mentor-details">
      <p class="mentor-name">상품명: {{ mentor.userName }}</p>
      <p class="product-price">상품 가격: {{ product.productPrice }}</p>
      <p class="product-description">상품 설명: {{ product.productDescription }}</p>
      <p class="product-category">상품 카테고리: {{ product.category.categoryName }}</p>
      <p class="product-stock">상품 수량: {{ product.productStock }}</p>
    </div>
  </router-link>
</template>

<style scoped>

</style>