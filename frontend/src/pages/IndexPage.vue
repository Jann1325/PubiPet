<template>
  <div class="q-pa-md">
    <div class="q-mb-md" style="text-align: center;">
      <div class="q-pa-md flex flex-center">
        <q-carousel animated v-model="slide" arrows navigation infinite height="600px" style="width: 1300px">
          <q-carousel-slide fit="contain" :name="1" img-src="../assets/carousel1.png" />
          <q-carousel-slide fit="contain" :name="2" img-src="../assets/carousel2.png" />
          <q-carousel-slide fit="contain" :name="3" img-src="../assets/carousel3.png" />
        </q-carousel>
      </div>
    </div>
    <div class="q-mb-md">
      <div class="text-h6 q-mb-sm" style="text-align: center;">熱門商品</div>
      <div class="q-gutter-md row items-center justify-evenly">
        <q-card square flat v-for="product in products" :key="product.id" style="width: 300px;">
          <q-img :src="product.imageUrl" alt="Product" style="height: 300px;" />
          <div class="q-pa-sm">
            <div class="text-body1 text-center">{{ product.prodName }}</div>
          </div>
        </q-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { api } from 'src/boot/axios'
import { useQuasar } from 'quasar'

const $q = useQuasar()
const slide = ref(1)
const products = ref([])

onMounted(async () => {
  fetchProducts()
})

// 取得商品資訊
const fetchProducts = async () => {
  try {
    const params = {
      categoryId: undefined,
      keyword: undefined,
      page: 1,
      limit: 3,
      sort: undefined,
    }
    const response = await api.get('/api/product/getProducts', { params });
    const res = response.data;
    if (res.code === 200) {
      products.value = res.data.products
    } else {
      $q.notify({ type: 'negative', message: `取得商品失敗：${res.message}` });
    }
  } catch (error) {
    console.error('取得商品失敗:', error)
  }
}
</script>

<style scoped></style>
