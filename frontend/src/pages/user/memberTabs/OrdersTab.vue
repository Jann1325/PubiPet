<template>
  <div class="row q-pa-md"
    style="height: 500px; overflow-y: auto; display: flex; justify-content: center; align-items: flex-start;">
    <div v-if="orders.length === 0">
      <q-banner color="warning" class="q-mt-md">
        <span class="text-center text-h5 text-weight-light">您還沒有任何訂單</span>
      </q-banner>
    </div>
    <!-- 側邊欄 -->
    <div class="col-3" v-if="orders.length > 0">
      <q-list bordered separator>
        <q-item-label header class="text-h6">歷史訂單</q-item-label>
        <q-item v-for="(order, index) in orders" :key="index">
          <q-item-section>
            <div class="text-subtitle2">訂單號碼：{{ order.orderNumber }}</div>
            <div class="text-caption">總金額：{{ order.totalAmount }} 元</div>
          </q-item-section>
          <q-item-section side>
            <q-btn label="查看詳情" color="orange" unelevated @click="viewOrderDetails(order)" />
          </q-item-section>
        </q-item>
      </q-list>
    </div>
    <div class="col-9">
      <!-- 訂單資訊 -->
      <q-card v-if="selectedOrder" class="q-pa-md" style="width: 100%;">
        <div class="text-h6 q-mb-md">訂單詳情</div>
        <div class="q-mb-sm"><strong>訂單號碼：</strong>{{ selectedOrder.orderNumber }}</div>
        <div class="q-mb-sm"><strong>付款方式：</strong>{{ getLookupName('payment_category',
          selectedOrder.paymentMethod) }}</div>
        <div class="q-mb-sm"><strong>付款狀態：</strong>{{ getLookupName('payment_status',
          selectedOrder.paymentStatus) }}</div>
        <div class="q-mb-sm"><strong>訂單狀態：</strong>{{ getLookupName('order_status',
          selectedOrder.orderStatus) }}</div>
        <div class="q-mb-sm"><strong>總金額：</strong>{{ selectedOrder.totalAmount.toLocaleString() }} 元</div>
        <!-- 訂單商品項目 -->
        <div class="q-mt-md">
          <div class="text-subtitle2 q-mb-sm">商品明細：</div>
          <q-list bordered separator>
            <q-item v-for="(item, i) in selectedOrder.items" :key="i">
              <q-item-section>
                <span @click="$router.push(`/product/${item.productId}`)" style="cursor: pointer; color: black;">
                  {{ item.name }}
                </span>
                <div class="text-caption">數量：{{ item.quantity }}</div>
              </q-item-section>
              <q-item-section side>
                <div class="text-caption">購買單價：{{ item.price.toLocaleString() }} 元</div>
                <div class="text-caption">小計：{{ (item.price * item.quantity).toLocaleString() }} 元</div>
              </q-item-section>
            </q-item>
          </q-list>
        </div>
      </q-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { api } from 'boot/axios';
import { useLookup } from '../../../composables/useLookup'

const orders = ref([]);
const selectedOrder = ref(null);
const { fetchLookup, getLookupName } = useLookup()

onMounted(async () => {
  fetchOrders();
  await fetchLookup('payment_category')
  await fetchLookup('payment_status')
  await fetchLookup('order_status')
});

// 取得訂單資訊
const fetchOrders = async () => {
  try {
    const token = localStorage.getItem('token');
    const res = await api.get('/api/order/history', {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
    orders.value = res.data.data;
  } catch (error) {
    console.error('取得訂單失敗', error);
  }
};

// 切換訂單
const viewOrderDetails = (order) => {
  selectedOrder.value = order;
};

</script>

<style scoped></style>
