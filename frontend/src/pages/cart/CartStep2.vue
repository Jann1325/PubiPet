<template>
  <div class="q-gutter-md">
    <q-card class="q-pa-md" flat bordered>
      <div class="text-h6">付款與運送方式</div>
      <q-option-group v-model="payment" :options="[
        { label: '宅配信用卡付款', value: 'credit' },
        { label: '宅配貨到付款', value: 'home_cod' },
        { label: '超商貨到付款', value: 'store_cod' }
      ]" type="radio" color="orange" />
    </q-card>
    <!-- 收件人欄位 -->
    <div>
      <q-card class="q-pa-md" flat bordered>
        <div class="row items-center justify-between">
          <div class="text-h6">填寫收件人資料</div>
          <q-btn v-if="payment !== 'store'" label="選擇收件人資料" dense flat icon="location_on" color="orange"
            @click="addressDialog = true" />
        </div>
        <div class="q-gutter-sm q-mt-md">
          <q-input v-model="form.name" label="姓名" outlined />
          <q-input v-model="form.phone" label="電話" outlined />
          <q-input v-if="payment !== 'store'" v-model="form.address" label="地址" outlined
            :disable="payment == 'store'" />
        </div>
      </q-card>
    </div>
    <!-- 信用卡欄位 -->
    <div v-if="payment === 'credit'" class="rounded-borders">
      <q-card class="q-px-md q-pa-md" flat bordered>
        <div class="text-h6">信用卡資訊</div>
        <div class="q-col-gutter-sm">
          <q-input v-model="form.cardNumber" label="信用卡號" outlined @update:model-value="onCardNumberInput"
            oninput="value=value.replace(/[^\d]/g,'')" maxlength="19" />
          <div class="row q-col-gutter-sm">
            <div class="col-6">
              <q-input v-model="form.expiry" label="到期日 (MM/YY)" outlined oninput="value=value.replace(/[^\d]/g,'')"
                @update:model-value="onExpiryInput" maxlength="5" />
            </div>
            <div class="col-6">
              <q-input v-model="form.cvv" label="安全碼 (CVV)" outlined maxlength="3"
                oninput="value=value.replace(/[^\d]/g,'')" @update:model-value="val => form.cvv = val.slice(0, 3)" />
            </div>
          </div>
        </div>
      </q-card>
    </div>
    <!-- 超商選擇欄位 -->
    <div v-if="payment === 'store'" class="rounded-borders">
      <q-card class="q-pa-md" flat bordered>
        <div class="text-h6 q-mb-md">取貨門市 </div>
        <q-btn label="選擇門市" color="orange" @click="storeDialog = true" />
        <div v-if="form.store" class="q-mt-sm text-body1 text-grey-7">選擇門市：{{ form.store }}</div>
      </q-card>
    </div>
    <div class="row justify-end q-mt-xl q-gutter-sm">
      <q-btn label="上一步" flat @click="$emit('back')" />
      <q-btn label="下一步：完成訂購" color="orange" unelevated :disable="isNextStepDisabled" @click="submitOrder" />
    </div>
    <!-- 門市 Dialog -->
    <q-dialog v-model="storeDialog">
      <q-card class="q-pa-md" style="min-width: 300px">
        <div class="text-h6">選擇超商門市</div>
        <q-list bordered separator class="q-mt-md">
          <q-item clickable v-for="store in storeList" :key="store" @click="selectStore(store)">
            <q-item-section>{{ store }}</q-item-section>
          </q-item>
        </q-list>
      </q-card>
    </q-dialog>
    <!-- 地址選擇 Dialog -->
    <q-dialog v-model="addressDialog">
      <q-card class="q-pa-md" style="min-width: 350px">
        <div class="text-h6">選擇收件人地址</div>
        <q-list bordered separator class="q-mt-md" style="max-height: 300px; overflow-y: auto">
          <q-item clickable v-for="addr in savedAddresses" :key="addr.id" @click="selectAddress(addr)">
            <q-item-section>
              <div>{{ addr.recipient }}（{{ addr.phone }}）</div>
              <div class="text-caption text-grey-7">{{ addr.city }}{{ addr.area }}{{ addr.address }}</div>
            </q-item-section>
            <q-item-section side v-if="addr.isDefault">
              <q-badge color="orange" text-color="white">預設</q-badge>
            </q-item-section>
          </q-item>
        </q-list>
      </q-card>
    </q-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from 'src/stores/user'
import { useCartStore } from 'src/stores/cart'
import { useQuasar } from 'quasar'
import { api } from 'src/boot/axios'
import { defineEmits } from 'vue'

const emit = defineEmits(['next'])
const userStore = useUserStore()
const cartStore = useCartStore();
const $q = useQuasar()
const router = useRouter()

// 狀態管理
const payment = ref('credit')
const form = ref({
  name: '',
  phone: '',
  address: '',
  cardNumber: '',
  expiry: '',
  cvv: '',
  store: ''
})

const storeDialog = ref(false)
const addressDialog = ref(false)

// 模擬超商清單
const storeList = ['7-11 台北中山店', '全家 民權店', '萊爾富 大安店', 'OK 超商 信義店']

// 用戶已儲存之地址資料
const savedAddresses = ref([])

onMounted(() => {
  fetchAddresses()
})

//取得用戶已儲存之地址資料
const fetchAddresses = async () => {
  if (!userStore.isLoggedIn) {
    $q.notify({ type: 'negative', message: '請先登入' });
    router.push('/login');
    return;
  }
  try {
    const res = await api.get('/api/user/getUserAddress');
    if (res.data.code === 200) {
      savedAddresses.value = res.data.data;
      // 預設填入設置為預設的地址資料
      const defaultAddr = savedAddresses.value.find(addr => addr.isDefault) || savedAddresses.value[0];
      if (defaultAddr) {
        form.value.name = defaultAddr.recipient;
        form.value.phone = defaultAddr.phone;
        form.value.address = `${defaultAddr.city}${defaultAddr.area}${defaultAddr.address}`;
      }
    } else {
      $q.notify({ type: 'negative', message: `取得會員地址資訊失敗：${res.data.message}` });
    }
  } catch {
    $q.notify({ type: 'negative', message: '無法載入地址資訊' });
  }
};

// 信用卡卡號檢查，每四個數字增加一個間隔
function onCardNumberInput(val) {
  const digitsOnly = val.slice(0, 16)
  const formatted = digitsOnly.replace(/(.{4})/g, '$1 ').trim()
  form.value.cardNumber = formatted
}

// 檢查到期日，月份不可超過12，超過12系統更改成01
function onExpiryInput(val) {
  const digitsOnly = val.slice(0, 4)
  let formatted = digitsOnly

  const month = parseInt(formatted.slice(0, 2), 10)
  if (month > 12 || month < 1) {
    formatted = `01/${digitsOnly.slice(2, 4)}`;
  }

  if (digitsOnly.length > 2) {
    formatted = `${digitsOnly.slice(0, 2)}/${digitsOnly.slice(2)}`
  }
  form.value.expiry = formatted
}

// 判斷是否可以進行下一步
const isNextStepDisabled = computed(() => {
  if (payment.value === 'credit') {
    // 檢查信用卡欄位
    return !form.value.cardNumber || !form.value.expiry || !form.value.cvv || form.value.cardNumber.length < 19 || form.value.expiry.length < 5 || form.value.cvv.length < 3;
  } else if (payment.value === 'store') {
    // 確認是否選擇超商
    return !form.value.store;
  }
  return false;
});

// 選擇超商列表中的門市，回填至畫面
function selectStore(store) {
  form.value.store = store
  storeDialog.value = false
}

// 選擇用戶已儲存之地址列表中的地址，回填至畫面
function selectAddress(addr) {
  form.value.name = addr.recipient
  form.value.phone = addr.phone
  form.value.address = `${addr.city}${addr.area}${addr.address}`
  addressDialog.value = false
}

// 建立訂單
const submitOrder = async () => {
  // 檢查必要的資料
  if (payment.value === 'credit') {
    // 信用卡付款，檢查信用卡資料
    if (!form.value.cardNumber || !form.value.expiry || !form.value.cvv) {
      $q.notify({ type: 'negative', message: '請填寫完整的信用卡資料' })
      return
    }

    // 檢查信用卡日期是否有效
    const [month] = form.value.expiry.split('/')
    if (parseInt(month) < 1 || parseInt(month) > 12) {
      $q.notify({ type: 'negative', message: '請輸入有效的到期日月份' })
      return
    }
    if (!form.value.cvv.match(/^\d{3}$/)) {
      $q.notify({ type: 'negative', message: '請輸入有效的 CVV' })
      return
    }
  } else if (payment.value === 'store') {
    // 超商貨到付款，檢查是否選擇門市
    if (!form.value.store) {
      $q.notify({ type: 'negative', message: '請選擇超商取貨門市' })
      return
    }
  }
  // 進行訂單提交
  try {
    const orderData = {
      cart: cartStore.selectedItems,
      payment: payment.value,
      formData: form.value
    }
    const response = await api.post('/api/order/submit', orderData)
    if (response.data.code === 200) {
      emit('next');
    } else {
      $q.notify({ type: 'negative', message: '訂單提交失敗，請稍後再試' })
    }
  } catch {
    $q.notify({ type: 'negative', message: '訂單提交出現錯誤，請稍後再試' })
  }
}

</script>
