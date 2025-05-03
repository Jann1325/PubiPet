<template>
  <div class="row q-pa-none">
    <div class="col">
      <div style="display: flex; justify-content: space-between; align-items: center;" class="q-mx-lg">
        <div class="text-h5" style="margin: 0;">
          {{ keyword ? `${keyword}` : (categoryType === 'all' ? '所有商品' :
            getLookupName('product_category', categoryType)) }}
        </div>
        <q-select outlined v-model="sortOption" emit-value map-options :options="sortOptions" label="排序"
          @update:modelValue="onSort" style="width: 200px;" />
      </div>
      <!-- 商品卡列表 -->
      <div class="row q-pt-md" style="display: flex; flex-wrap: wrap; justify-content: flex-start;"
        v-if="products && products.length > 0">
        <router-link v-for="product in (products || []).slice(0, 20)" :key="product.id" :to="`/product/${product.id}`"
          class="product-card-link">
          <q-card flat class="product-card q-my-md q-mx-sm">
            <q-card-section>
              <div style="text-align: center; margin-top: 10px;">
                <div>
                  <q-img :src="product.imageUrl" :fallback-src="imgFailedSrc" :placeholder-src="imgFailedSrc"
                    :alt="product.prodName" style="width: 200px; height: 200px;" />
                </div>
                <div style="font-size:14px; padding: 5px;">{{ product.prodName }}</div>
                <template v-if="product.discountPrice">
                  <span class="text-orange text-weight-medium text-body2">優惠價：NT${{
                    product.discountPrice.toLocaleString()
                  }}</span>
                  <span class="text-grey text-strike q-ml-md">NT${{ product.price.toLocaleString() }}</span>
                </template>
                <template v-else>
                  <div class="text-body2">NT${{ product.price.toLocaleString() }}</div>
                </template>
              </div>
            </q-card-section>
          </q-card>
        </router-link>
      </div>
      <div v-else-if="isLoading" class="q-pa-md flex flex-center">
        <q-spinner color="orange" size="40px" />
      </div>
      <!-- 沒有符合搜尋條件的商品 -->
      <div v-else class="q-pa-md">
        <p class="text-center text-h5 text-weight-light">找不到符合條件的商品</p>
      </div>
      <!-- 分頁 -->
      <div class="q-pa-lg" style="display: flex;">
        <q-pagination style="margin-left: auto;" v-model="currentPage" :max="totalPages"
          @update:model-value="fetchProducts" direction-links flat color="grey" active-color="orange" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { onBeforeRouteUpdate, useRoute } from 'vue-router'
import imgFailedSrc from '../../assets/loadFailed.png'
import { useLookup } from '../../composables/useLookup'
import { api } from 'src/boot/axios'
import { useQuasar } from 'quasar'
const $q = useQuasar()
const route = useRoute()
const { getLookupName, lookupMap } = useLookup()

// 分頁
const currentPage = ref(1)
const totalPages = ref(1)
const pageSize = 16
// 分類項目
const categoryType = ref(route.params.type)
// 商品資訊
const products = ref([])
// 排序選項
const sortOptions = ref([
  { label: '依名稱排序', value: 'name' },
  { label: '依價格排序', value: 'price' }
])
// 預設排序選項
const sortOption = ref('name')
// 關鍵字
const keyword = route.query.keyword || ''

// 初次加載
onMounted(async () => {
  await fetchProducts(currentPage.value)
  sortOption.value = sortOptions.value[0].value
})

// 用 lookupKey 找到對應 id
const categoryId = computed(() => {
  // 先找大分類
  const foundCategory = lookupMap.value['product_category']?.find(cat => cat.lookupKey === categoryType.value)
  if (foundCategory) {
    return foundCategory.id
  }
  // 如果大分類找不到，再去所有大分類裡找子分類
  for (const category of lookupMap.value['product_category'] || []) {
    const foundSubCategory = category.subCategories?.find(sub => sub.lookupKey === categoryType.value)
    if (foundSubCategory) {
      return foundSubCategory.id
    }
  }
  // 都找不到，回傳 null
  return null
})

const isLoading = ref(false)

// 取得商品資料
const fetchProducts = async (page = 1) => {
  isLoading.value = true
  try {
    const params = {
      categoryId: categoryId.value,
      keyword: route.query.keyword || undefined,
      page,
      limit: pageSize,
      sort: sortOption.value,
    }
    const response = await api.get('/api/product/getProducts', { params });
    const res = response.data;
    if (res.code === 200) {
      products.value = res.data.products
      totalPages.value = res.data.totalPages
    } else {
      $q.notify({ type: 'negative', message: `取得商品失敗：${res.message}` });
    }
  } catch (error) {
    console.error('取得商品失敗:', error)
  } finally {
    isLoading.value = false
  }
}

// 觸發搜尋
onBeforeRouteUpdate((to, from, next) => {
  if (to.query.keyword !== from.query.keyword) {
    fetchProducts()
  }
  next()
})

// 排序方法
function onSort(val) {
  sortOption.value = val
  fetchProducts(currentPage.value)
}
</script>



<style scoped>
.product-card-link {
  text-decoration: none;
  color: inherit;
}

.product-card {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 230px;
  height: 300px;
}

.product-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.product-image {
  width: 100%;
  height: auto;
}

.q-btn {
  width: 100%;
  margin-top: 10px;
}
</style>