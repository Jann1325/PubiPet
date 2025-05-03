<template>
  <div class="row q-pa-md"
    style="height: 500px; overflow-y: auto; display: flex; justify-content: center; align-items: flex-start;">
    <!-- 側邊欄 -->
    <div class="col-2">
      <q-list>
        <q-item clickable @click="onSidebarClick('profile')" :active="currentSection === 'profile'"
          active-class="bg-orange text-white">
          <q-item-section>個人資料</q-item-section>
        </q-item>
        <q-item clickable @click="onSidebarClick('delivery')" :active="currentSection === 'delivery'"
          active-class="bg-orange text-white">
          <q-item-section>送貨地址</q-item-section>
        </q-item>
      </q-list>
    </div>
    <!-- 主內容 -->
    <div class="col-10">
      <!-- 個人資料 -->
      <q-card v-if="currentSection === 'profile'" class="q-pa-md q-mb-lg" style="height: 480px; overflow-y: auto;">
        <q-form @submit.prevent="saveProfile" class="q-gutter-md">
          <div class="row q-pt-md">
            <div class="q-pa-md col-3">電子信箱</div>
            <div class="col-8 q-my-md items-center">
              {{ profile.email }}
            </div>
          </div>
          <div class="row q-pb-md">
            <div class="q-pa-md col-3">姓名</div>
            <q-input class="col-8" v-model="profile.userName" placeholder="請輸入姓名" />
          </div>
          <div class="row">
            <div class="q-pa-md col-3">手機號碼</div>
            <q-input class="col-8" v-model="profile.phone" placeholder="請輸入手機號碼"
              oninput="value=value.replace(/[^\d]/g,'')" :rules="[
                val => !!val || '請輸入手機號碼',
                val => /^09\d{8}$/.test(val) || '手機號碼格式錯誤（需為09開頭，共10碼）'
              ]" />
          </div>
          <div class="row q-pb-md">
            <div class="q-pa-md col-3">性別</div>
            <q-select class="col-8" v-model="profile.gender" :options="genderOptions" option-value="value"
              option-label="label" emit-value map-options label="性別（選填）" />
          </div>
          <div class="row">
            <div class="q-pa-md col-3">出生日期</div>
            <div class="col-8 q-my-md items-center">
              {{ birth }}
            </div>
          </div>
          <div class="row items-center justify-between">
            <q-space />
            <q-btn label="儲存變更" type="submit" color="orange" />
          </div>
        </q-form>
      </q-card>

      <!-- 送貨地址 -->
      <q-card v-if="currentSection === 'delivery'" class="q-pa-md q-mb-lg" style="height: 480px; overflow-y: auto;">
        <div v-if="deliveryEditing != null">
          <q-card-section class="row items-center justify-between">
            <span class="text-h6">{{ deliveryEditing != null ? '編輯送貨地址' : '新增送貨地址' }}</span>
            <q-space />
          </q-card-section>
          <q-form @submit.prevent="saveDelivery" class="q-gutter-md">
            <div class="row q-pb-md">
              <div class="q-pa-md col-2">收件人姓名</div>
              <q-input class="col-8" v-model="deliveryForm.recipient" placeholder="請輸入收件人姓名" />
            </div>
            <div class="row">
              <div class="q-pa-md col-2">手機號碼</div>
              <q-input class="col-8" v-model="deliveryForm.phone" placeholder="請輸入手機號碼"
                oninput="value=value.replace(/[^\d]/g,'')" :rules="[
                  val => !!val || '請輸入手機號碼',
                  val => /^09\d{8}$/.test(val) || '手機號碼格式錯誤（需為09開頭，共10碼）'
                ]" />
            </div>
            <div class="row q-pb-md">
              <div class="q-pa-md col-2">縣市</div>
              <q-input class="col-8" v-model="deliveryForm.city" placeholder="請輸入縣市" />
            </div>
            <div class="row q-pb-md">
              <div class="q-pa-md col-2">行政區</div>
              <q-input class="col-8" v-model="deliveryForm.area" placeholder="請輸入行政區" />
            </div>
            <div class="row q-pb-md">
              <div class="q-pa-md col-2">詳細地址</div>
              <q-input class="col-8" v-model="deliveryForm.address" placeholder="請輸入詳細地址" />
            </div>
            <div class="row">
              <div class="q-pa-md col-2">預設地址</div>
              <q-toggle v-model="deliveryForm.isDefault" color="orange" />
            </div>
            <div class="row items-center justify-between">
              <q-space />
              <q-btn label="儲存地址" type="submit" color="orange" />
            </div>
          </q-form>
        </div>
        <!-- 顯示地址清單 -->
        <div v-if="deliveryEditing == null">
          <q-list separator>
            <q-card-section class="row items-center justify-between">
              <q-space />
              <q-btn icon="add" color="orange" flat label="新增收貨地址" @click="addDelivery" />
            </q-card-section>
            <q-item v-for="item in deliveryList" :key="item.id" class="q-mb-sm">
              <q-item-section>
                {{ item.recipient }}
                <br>
                {{ item.phone }}
                <br>
                {{ item.city }}{{ item.area }}{{ item.address }}
                <div class="q-mt-xs">
                  <q-badge rounded v-if="item.isDefault" color="orange" text-color="white" class="text-caption">
                    預設地址
                  </q-badge>
                </div>
              </q-item-section>
              <q-item-section side>
                <q-btn flat icon="edit" @click="editDelivery(item.id)" />
                <q-btn flat icon="delete" color="red" @click="confirmDelete(item.id)" />
              </q-item-section>
            </q-item>
          </q-list>
        </div>
      </q-card>
    </div>
    <!-- 刪除確認 -->
    <q-dialog v-model="confirmDialog.show">
      <q-card class="q-pa-md">
        <q-card-section>確定要刪除這筆地址嗎？</q-card-section>
        <q-card-actions class="justify-center">
          <q-btn flat label="取消" v-close-popup />
          <q-btn flat label="確定" color="red" @click="deleteDelivery" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useLookup } from 'src/composables/useLookup'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { api } from 'src/boot/axios'
import { useUserStore } from 'src/stores/user'

const router = useRouter()
const $q = useQuasar()
const userStore = useUserStore()
const currentSection = ref('profile')
const genderOptions = ref([])
const { fetchLookup, getOptionsFromLookup } = useLookup()
const profile = ref({
  userName: '',
  email: '',
  phone: '',
  gender: '',
  birth: ''
})
const birth = ref('')

onMounted(async () => {
  await fetchLookup('gender')
  fetchProfiles()
  genderOptions.value = getOptionsFromLookup('gender')
})

// 切換側邊欄選項
function onSidebarClick(section) {
  currentSection.value = section
  if (section == 'profile') {
    fetchProfiles()
  } else {
    fetchAddresses()
  }
  cancelDeliveryEdit()
}

// 取得用戶基本資料
const fetchProfiles = async () => {
  if (!userStore.isLoggedIn) {
    $q.notify({ type: 'negative', message: '請先登入' })
    router.push('/login')
    return
  }
  try {
    const res = await api.get('/api/user/getUserProfile')
    if (res.data.code === 200) {
      profile.value = res.data.data
      birth.value = new Intl.DateTimeFormat('zh-TW', { year: 'numeric', month: 'long', day: 'numeric' }).format(new Date(res.data.data.birth))
    } else {
      $q.notify({ type: 'negative', message: `取得會員資訊失敗：${res.data.message}` })
    }
  } catch (error) {
    $q.notify({ type: 'negative', message: error })
  }
}

// 儲存用戶基本資料
async function saveProfile() {
  try {
    const res = await api.post('/api/user/updateUserProfile', profile.value)
    if (res.data.code === 200) {
      $q.notify({ type: 'positive', message: '更新成功' })
      fetchProfiles()
    } else {
      $q.notify({ type: 'negative', message: `更新失敗：${res.data.message}` })
    }
  } catch (error) {
    $q.notify({ type: 'negative', message: error })
  }
}

const deliveryList = ref([])
const deliveryForm = ref({
  id: null,
  userId: null,
  recipient: '',
  phone: '',
  city: '',
  area: '',
  address: '',
  isDefault: false
})
const deliveryEditing = ref(null)

// 新增地址資料
function addDelivery() {
  deliveryEditing.value = true
  deliveryForm.value = {
    id: null,
    userId: null,
    recipient: '',
    phone: '',
    city: '',
    area: '',
    address: '',
    isDefault: false
  }
}

// 編輯已存在地址，帶入編輯畫面
function editDelivery(id) {
  const selectedItem = deliveryList.value.find(item => item.id === id)
  if (selectedItem) {
    deliveryEditing.value = id
    deliveryForm.value = { ...selectedItem }
  }
}

// 取得用戶儲存之地址資料
const fetchAddresses = async () => {
  if (!userStore.isLoggedIn) {
    $q.notify({ type: 'negative', message: '請先登入' })
    router.push('/login')
    return
  }
  try {
    const res = await api.get('/api/user/getUserAddress')
    if (res.data.code === 200) {
      deliveryList.value = res.data.data
    } else {
      $q.notify({ type: 'negative', message: `取得會員地址資訊失敗：${res.data.message}` })
    }
  } catch (error) {
    $q.notify({ type: 'negative', message: error })
  }
}

// 儲存用戶地址資料
async function saveDelivery() {
  try {
    const res = await api.post('/api/user/updateUserAddress', deliveryForm.value)
    if (res.data.code === 200) {
      $q.notify({ type: 'positive', message: '更新成功' })
      fetchAddresses()
    } else {
      $q.notify({ type: 'negative', message: `更新失敗：${res.data.message}` })
    }
  } catch (error) {
    $q.notify({ type: 'negative', message: error })
  }
  cancelDeliveryEdit()

}

// 取消編輯地址
function cancelDeliveryEdit() {
  deliveryEditing.value = null
  deliveryForm.value = {
    id: null,
    userId: null,
    recipient: '',
    phone: '',
    city: '',
    area: '',
    address: '',
    isDefault: false
  }
}

const confirmDialog = ref({ show: false })

// 刪除地址資料確認框
function confirmDelete(id) {
  deliveryForm.value = deliveryList.value.find(item => item.id === id)
  confirmDialog.value = { show: true }
}

// 刪除地址資料
async function deleteDelivery() {
  try {
    const res = await api.post('/api/user/deleteUserAddress', { addressId: deliveryForm.value.id })
    if (res.data.code === 200) {
      $q.notify({ type: 'positive', message: '刪除成功' })
      fetchAddresses()
    } else {
      $q.notify({ type: 'negative', message: `刪除失敗：${res.data.message}` })
    }
  } catch (error) {
    $q.notify({ type: 'negative', message: error })
  }
  confirmDialog.value.show = false
}
</script>
