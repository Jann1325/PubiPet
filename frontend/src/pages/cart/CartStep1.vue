<template>
  <div class="q-pa-lg q-mx-auto" style="max-width: 960px;">
    <div v-if="isLoading" class="q-pa-md flex flex-center">
      <q-spinner color="orange" size="40px" class="q-my-xl flex flex-center" />
    </div>
    <!-- 空購物車 -->
    <div v-else-if="cart.length === 0" class="q-my-xl text-center">
      <div style="display: flex; justify-content: center;">
        <q-icon name="shopping_cart_off" size="64px" color="grey-5" />
      </div>
      <div class="text-subtitle1 text-grey-6 q-mt-sm">您的購物車中沒有商品</div>
      <q-btn label="去購物" color="orange" class="q-mt-md" unelevated @click="goShopping" />
    </div>
    <!-- 有商品 -->
    <div v-else>
      <!-- 全選列 -->
      <q-card class="row items-center justify-between q-px-md q-mb-md" flat bordered>
        <q-checkbox v-model="selectAll" label="全選" color="orange" @update:model-value="toggleSelectAll" />
        <q-btn flat color="red" label="刪除選取商品" @click="deleteSelectedItems" :disable="!selectedItems.length" />
      </q-card>
      <!-- 購物車列表 -->
      <q-card v-for="product in cart" :key="product.id" flat bordered class="q-pa-md no-border-radius">
        <q-btn icon="close" color="grey-6" flat dense class="absolute-top-right q-ma-sm"
          @click="() => deleteItem(product.id)" />
        <div class="row q-gutter-md items-center">
          <q-checkbox v-model="selectedItems" :val="product.id" color="orange" />
          <q-img :src="product.imageUrl" style="width: 120px; height: 120px;" />
          <div class="col row">
            <div class="col-5 row items-center">
              <span class="text-body1 text-weight-medium" @click="$router.push(`/product/${product.id}`)"
                style="cursor: pointer; color: black;">
                {{ product.prodName }}
              </span>
            </div>
            <div class="col-3 row justify-end items-center">
              <div class="q-gutter-none row" style="border-radius: 5px; border: 1px solid #ccc; height: 38px;">
                <q-btn square color="gray" outline flat icon="remove" @click="decreaseQuantity(product)"
                  :disabled="product.quantity <= 1" size="sm" style="width: 36px;" />
                <input v-model="product.quantity" type="text" @change="updateQuantity(product)"
                  oninput="value=value.replace(/[^\d]/g,'')"
                  style="width: 60px; height: 36px; border: none; outline: none; text-align: center;" />
                <q-btn flat icon="add" @click="increaseQuantity(product)" :disabled="product.quantity >= 99" size="sm"
                  style="width: 36px;" />
              </div>
            </div>
            <div class="col-3 row justify-end items-center">
              <div class="column items-end">
                <template v-if="product.discountPrice">
                  <div class="text-body1">單價：NT${{ product.discountPrice.toLocaleString() }}</div>
                  <div class="text-grey text-strike">NT${{ product.price.toLocaleString() }}</div>
                </template>
                <template v-else>
                  <div class="text-body1">單價：NT${{ product.price.toLocaleString() }}</div>
                </template>
                <div class="text-body1 text-grey-7 q-mt-md">
                  小計：NT${{ ((product.discountPrice || product.price) * product.quantity).toLocaleString() }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </q-card>
      <!-- 金額總計與下一步 -->
      <div class="q-mt-lg q-py-md justify-end row"
        style="position: sticky; bottom: 0; background: white; border-top: 1px solid #eee;">
        <q-separator />
        <div class="text-h6">
          <span>總計：</span>
          <span class="text-weight-bold">NT${{ totalPrice.toLocaleString() }}</span>
        </div>
      </div>
      <div class="justify-end row q-mb-md">
        <q-btn label="下一步" color="orange" unelevated @click="GoToCheckOut" :disable="!selectedItems.length" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useQuasar } from 'quasar';
import { useUserStore } from 'src/stores/user';
import { useCartStore } from 'src/stores/cart';
import { api } from 'boot/axios';
import qs from 'qs';
import { defineEmits } from 'vue'

const emit = defineEmits(['next'])
const router = useRouter();
const $q = useQuasar();
const userStore = useUserStore();
const cartStore = useCartStore();

const cart = ref([]);
const selectedItems = ref([]);
const selectAll = ref(true);

onMounted(async () => {
  await initCart();
  // 預設全選
  selectAll.value = true;
  toggleSelectAll(true);
});

// 取得購物車資料
const initCart = async () => {
  let mergedCartItems = [];
  if (userStore.token) { // 有登入，從後端取得購物車資料
    const res = (await api.get('/api/cart/getCartItem')).data;
    if (res.code === 200) {
      mergedCartItems = res.data;
    } else {
      $q.notify({ type: 'negative', message: `取得購物車資料失敗：${res.message}` });
      return;
    }
  } else { // 未登入，從 localStorage 取得購物車資料
    const localCart = JSON.parse(localStorage.getItem('pubi-cart')) || [];
    mergedCartItems = localCart;
  }

  await fetchProductDetails(mergedCartItems);
};

const isLoading = ref(false)

// 取得購物車中商品的資料
const fetchProductDetails = async (cartItems) => {
  isLoading.value = true
  if (cartItems.length === 0) return
  const productIds = cartItems.map(item => item.productId);
  const res = (await api.get('/api/product/getCartProducts', {
    params: { productIds },
    // 將陣列轉成 ?productIds=1&productIds=2... 的格式
    paramsSerializer: p => qs.stringify(p, { arrayFormat: 'repeat' })
  })).data;
  if (res.code === 200) {
    cart.value = cartItems.map(item => {
      const product = res.data.find(p => p.id === item.productId);
      return { ...product, quantity: item.quantity };
    });
  } else {
    $q.notify({ type: 'negative', message: `取得商品資料失敗：${res.message}` });
  }
  isLoading.value = false
};

//監控商品選擇的狀態
watch(selectedItems, (val) => {
  selectAll.value = val.length === cart.value.length && cart.value.length > 0;
});

//監控全選框狀態
const toggleSelectAll = (val) => {
  selectedItems.value = val ? cart.value.map(p => p.id) : [];
};

// 批次刪除
const deleteSelectedItems = async () => {
  deleteItems(selectedItems.value);
};

// 單項刪除
const deleteItem = async (productId) => {
  deleteItems([productId]); // 轉為單項陣列來傳入
};

// 刪除購物車資料
const deleteItems = async (productIds) => {
  if (!productIds.length) return;

  if (userStore.token) { // 已登入，透過後端刪除資料庫的購物車資料
    const res = (await api.delete('/api/cart/deleteItems', { data: { productIds } })).data;
    if (res.code === 200) {
      // 更新前端資料，過濾掉已刪除的商品
      cart.value = cart.value.filter(p => !productIds.includes(p.id));
      $q.notify({ type: 'positive', message: '已刪除選取商品' });
    } else {
      $q.notify({ type: 'negative', message: `刪除失敗：${res.message}` });
    }
  } else { // 未登入，刪除 localStorage 中的購物車資料
    const localCart = JSON.parse(localStorage.getItem('pubi-cart')) || [];
    const updated = localCart.filter(item => !productIds.includes(item.productId));
    localStorage.setItem('pubi-cart', JSON.stringify(updated));
    cart.value = cart.value.filter(p => !productIds.includes(p.id));
  }

  selectedItems.value = []; // 清空選中的商品
  selectAll.value = false; // 取消全選
  cartStore.fetchCount(userStore.token); // 重新計算購物車數量
};


// 更新數量(修改輸入框數量)
const updateQuantity = async (product) => {
  // 數量限制
  if (product.quantity < 1) product.quantity = 1;
  if (product.quantity > 99) product.quantity = 99;

  if (userStore.token) {  //已登入，需透過後端更新資料庫的購物車資料
    try {
      await api.put('/api/cart/updateQuantities', [{ productId: product.id, quantity: product.quantity }]);
    } catch {
      $q.notify({ type: 'negative', message: '更新數量失敗，請稍後再試' });
    }
  } else {  // 未登入，更新 localStorage 中的購物車資料
    const localCart = JSON.parse(localStorage.getItem('pubi-cart')) || [];
    const item = localCart.find(i => i.productId === product.id);
    if (item) item.quantity = product.quantity;
    localStorage.setItem('pubi-cart', JSON.stringify(localCart));
  }
  cartStore.fetchCount(userStore.token);// 重新計算購物車數量
};

// 增加數量按鈕
const increaseQuantity = async (product) => {
  if (product.quantity >= 99) return;
  product.quantity++;
  if (userStore.token) { //已登入，需透過後端更新資料庫的購物車資料
    try {
      await api.put('/api/cart/updateQuantities', [{ productId: product.id, quantity: product.quantity }]);
    } catch {
      $q.notify({ type: 'negative', message: '更新數量失敗，請稍後再試' });
      product.quantity--;
    }
  } else {  // 未登入，更新 localStorage 中的購物車資料
    const localCart = JSON.parse(localStorage.getItem('pubi-cart')) || [];
    const item = localCart.find(i => i.productId === product.id);
    if (item) item.quantity = product.quantity;
    localStorage.setItem('pubi-cart', JSON.stringify(localCart));
  }
  cartStore.fetchCount(userStore.token);// 重新計算購物車數量
};

// 減少數量按鈕
const decreaseQuantity = async (product) => {
  if (product.quantity <= 1) return;
  product.quantity--;
  if (userStore.token) { //已登入，需透過後端更新資料庫的購物車資料
    try {
      await api.put('/api/cart/updateQuantities', [{ productId: product.id, quantity: product.quantity }]);
    } catch {
      $q.notify({ type: 'negative', message: '更新數量失敗，請稍後再試' });
      product.quantity++;
    }
  } else { // 未登入，更新 localStorage 中的購物車資料
    const localCart = JSON.parse(localStorage.getItem('pubi-cart')) || [];
    const item = localCart.find(i => i.productId === product.id);
    if (item) item.quantity = product.quantity;
    localStorage.setItem('pubi-cart', JSON.stringify(localCart));
  }
  cartStore.fetchCount(userStore.token);// 重新計算購物車數量
};

// 計算總金額
const totalPrice = computed(() => {
  return cart.value.reduce((sum, p) => {
    const price = p.discountPrice || p.price;
    return selectedItems.value.includes(p.id) ? sum + price * p.quantity : sum;
  }, 0);
});

// 去購物按鈕
const goShopping = () => {
  router.push('/');
};

// 下一頁
function GoToCheckOut() {
  const selectedCartItems = selectedItems.value.map(id => {
    const found = cart.value.find(item => item.id === id);
    return {
      productId: id,
      quantity: found?.quantity ?? 1,
      price: found?.discountPrice ?? found?.price ?? 0
    };
  });
  cartStore.setSelectedItems(selectedCartItems); // 將所勾選商品儲存至cartStore
  emit('next');
}
</script>

<style scoped></style>
