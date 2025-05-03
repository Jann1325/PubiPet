<template>
  <!-- 資料載入中 -->
  <div v-if="isLoading" class="q-pa-md flex flex-center">
    <q-spinner color="orange" size="40px" />
  </div>
  <div v-else-if="product" class="q-my-md full-width">
    <q-card flat class="q-pa-md">
      <q-card-section class="row items-start q-col-gutter-md">
        <!-- 左側圖片區塊 -->
        <div class="col-5">
          <q-carousel v-if="product.images && product.images.length" v-model="slide" arrows animated
            transition-prev="slide-right" transition-next="slide-left" navigation height="400px"
            class="bg-grey-1 rounded-borders">
            <q-carousel-slide v-for="(img, index) in product.images" :key="index" :name="index" :img-src="img" />
          </q-carousel>
          <q-img v-else :src="imgFailedSrc" :alt="product.prodName" class="rounded-borders" />
        </div>
        <!-- 右側商品資訊 -->
        <div class="col-7 text-left">
          <!-- 商品名稱與描述 -->
          <div class="q-mb-md">
            <h2 class="text-h5 text-weight-bold q-mb-xs">{{ product.prodName }}</h2>
            <div v-if="product.features">
              <ul class="q-pl-none">
                <li v-for="(feature, index) in featureList" :key="index" class="row items-center q-my-xs">
                  <q-icon name="pets" class="q-mr-sm text-orange" />
                  <span>{{ feature }}</span>
                </li>
              </ul>
            </div>
          </div>
          <!-- 價格區塊 -->
          <div class="q-mb-md">
            <div v-if="product.discountPrice" class="text-negative">
              <span class="text-h6">NT$</span>
              <span class="text-h4 text-weight-bold q-mr-sm">{{ product.discountPrice.toLocaleString() }}</span>
              <span class="text-grey text-strike text-body1">NT${{ product.price.toLocaleString() }}</span>
            </div>
            <div v-else class="text-negative">
              <span class="text-h6">NT$</span>
              <span class="text-h4 text-weight-bold">{{ product.price.toLocaleString() }}</span>
            </div>
          </div>
          <!-- 商品資料卡片 -->
          <q-card flat bordered class="bg-grey-1 q-pa-md q-mb-md">
            <div class="text-subtitle1 text-weight-medium q-mb-sm">商品資料</div>
            <div class="q-gutter-y-sm">
              <div><q-badge color="grey-3" text-color="black" class="q-mr-sm">產地</q-badge>{{ getLookupName('country',
                product.origin) }}</div>
              <div v-if="product.material"><q-badge color="grey-3" text-color="black" class="q-mr-sm">材質</q-badge>{{
                product.material }}</div>
              <div v-if="product.weight"><q-badge color="grey-3" text-color="black" class="q-mr-sm">重量</q-badge>{{
                product.weight }} g</div>
              <div v-if="product.volume"><q-badge color="grey-3" text-color="black" class="q-mr-sm">容量</q-badge>{{
                product.volume }} ml</div>
              <div v-if="product.size"><q-badge color="grey-3" text-color="black" class="q-mr-sm">尺寸</q-badge>{{
                product.size }}</div>
              <div v-if="product.suitableFor"><q-badge color="grey-3" text-color="black"
                  class="q-mr-sm">適合對象</q-badge>{{ product.suitableFor }}</div>
              <div v-if="product.shelfLife"><q-badge color="grey-3" text-color="black" class="q-mr-sm">保存期限</q-badge>{{
                product.shelfLife }}</div>
            </div>
          </q-card>
          <!-- 數量與購物車按鈕 -->
          <div class="row items-center q-gutter-sm">
            <!-- 數量與按鈕 -->
            <div class="row items-center">
              <div class="q-gutter-none row items-center" color="gray"
                style="border-radius: 5px; border: 1px solid #ccc;height: 38px;">
                <q-btn square color="gray" outline flat icon="remove" @click="decreaseQuantity"
                  :disabled="quantity <= 1" size="sm" style="width: 36px;" />
                <input v-model="quantity" type="text" oninput="value=value.replace(/[^\d]/g,'')" style="width: 60px;
                  height: 36px; border: none; outline: none; text-align: center;" />
                <q-btn flat icon="add" @click="increaseQuantity" :disabled="quantity >= 99" size="sm"
                  style="width: 36px;" />
              </div>
            </div>
            <!-- 加入購物車 -->
            <q-btn label="加入購物車" color="orange" @click="addToCart" />
          </div>
        </div>
      </q-card-section>
    </q-card>
    <!-- 下方頁籤 -->
    <q-card class="q-pa-md">
      <q-tabs v-model="tab" dense class="text-black q-mt-lg" active-color="orange" indicator-color="orange">
        <q-tab name="intro" label="產品介紹" />
        <q-tab name="shipping" label="配送 & 售後說明" />
        <q-tab name="reviews" label="評論" />
      </q-tabs>
      <q-tab-panels v-model="tab">
        <q-tab-panel name="intro">
          <div class="q-mt-md text-center">
            {{ product.prodDescription || '暫無介紹內容' }}
          </div>
        </q-tab-panel>

        <q-tab-panel name="shipping">
          <div class="q-mt-md">
            <ul>
              <li>出貨時間：下單後 3 個工作天內出貨</li>
              <li>配送方式：宅配 / 超商取貨</li>
              <li>退換貨：七日內可申請退貨（需保持商品完整）</li>
            </ul>
          </div>
        </q-tab-panel>

        <q-tab-panel name="reviews">
          <div class="q-mt-md text-center">
            <div class="text-h5 text-grey">暫無評論內容</div>
          </div>
        </q-tab-panel>
      </q-tab-panels>
    </q-card>
  </div>
  <!-- 商品不存在 -->
  <div v-else class="q-pa-md">
    <p class="text-center text-h5 text-weight-light">未找到商品資料。</p>
  </div>
</template>



<script setup>
import { ref, onMounted, watch, computed } from 'vue';
import { useRoute } from 'vue-router';
import imgFailedSrc from '../../assets/loadFailed.png'
import { api } from 'src/boot/axios'
import { useLookup } from '../../composables/useLookup'
import { useCartStore } from 'src/stores/cart';
import { useUserStore } from 'src/stores/user';
import { useQuasar } from 'quasar'

const product = ref(null);
const tab = ref('intro');
const route = useRoute();
const productId = ref('');
const slide = ref(0)
const quantity = ref(1);
const { fetchLookup, getLookupName } = useLookup()
const $q = useQuasar()
const cartStore = useCartStore();
const userStore = useUserStore();

onMounted(async () => {
  await fetchLookup('country')
  productId.value = route.params.id; // 設定 productId 為路徑中的 id
  fetchProductDetails(productId.value);
});

const isLoading = ref(false)

// 載入商品詳細資料
const fetchProductDetails = async (productId) => {
  isLoading.value = true
  try {
    const response = await api.get('/api/product/getProduct', {
      params: { productId }
    });
    const res = response.data;
    if (res.code === 200) {
      product.value = res.data;
    } else {
      $q.notify({ type: 'negative', message: `取得商品失敗：${res.message}` });
    }
  } catch (error) {
    console.error('取得商品失敗:', error);
  } finally {
    isLoading.value = false
  }
};

// 減少數量按鈕
const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value -= 1;
  }
};

// 增加數量按鈕
const increaseQuantity = () => {
  if (quantity.value < 99) {
    quantity.value += 1;
  }
};

// 監視 quantity 的變化
watch(quantity, (newQuantity) => {
  if (newQuantity >= 99) {
    quantity.value = 99;
  }
  if (newQuantity < 1) {
    quantity.value = 1;
  }
});

// 商品特色描述
const featureList = computed(() =>
  product.value?.features ? product.value.features.split('\n').filter(f => f.trim() !== '') : []
)

// 加入購物車
const addToCart = async () => {
  const token = userStore.token;
  const productIdValue = Number(productId.value);
  const quantityValue = quantity.value;
  const cartItem = { productId: productIdValue, quantity: quantityValue };
  if (token) { // 已登入
    try {
      const response = await api.post('/api/cart/addToCart', cartItem);
      const res = response.data;
      if (res.code === 200) {
        $q.notify({ type: 'positive', message: '已加入購物車' });
      } else {
        $q.notify({ type: 'negative', message: `加入失敗，請稍後再試：${res.message}` });
      }
    } catch (error) {
      console.error('加入購物車失敗:', error);
      $q.notify({ type: 'negative', message: '加入失敗，請稍後再試' });
    }
  } else { // 未登入，將商品暫存至 localStorage
    const cart = JSON.parse(localStorage.getItem('pubi-cart')) || [];
    const existingItem = cart.find(item => item.productId === productIdValue);  // 檢查是否已經有這個商品
    if (existingItem) {
      existingItem.quantity += quantityValue;
    } else {
      cart.push(cartItem);
    }
    // 儲存回 localStorage
    localStorage.setItem('pubi-cart', JSON.stringify(cart));
    $q.notify({ type: 'positive', message: '商品已加入購物車' });
  }
  cartStore.fetchCount(token);  // 更新購物車數量提示
};
</script>

<style scoped></style>
