<template>
  <div>
    <q-layout>
      <!-- header -->
      <q-header class="bg-white text-black header q-px-xl">
        <router-link to="/" class="logo">
          <img src="../assets/logo.png" alt="Logo" style="cursor: pointer;" />
        </router-link>
        <q-toolbar class="q-px-xl row items-center q-gutter-x-xl">
          <div class="row items-center q-gutter-x-md">
            <router-link v-for="(category, index) in productCategories" :key="index"
              :to="`/products/${category.lookupKey}`" class="nav-link">
              {{ category.lookupValue }}
            </router-link>
            <router-link to="/products/all" class="nav-link">所有商品</router-link>
          </div>
          <q-space />
          <!-- 頁首按鈕區 -->
          <div class="row items-center q-gutter-x-sm header-right">
            <q-btn flat round icon="search" title="搜尋" class="icon-button" ref="searchBtn" @click.stop="toggleSearch" />
            <!-- 搜尋框 -->
            <q-menu v-model="showSearch" v-if="searchAnchorEl" self="top middle" :offset="[124, -51]"
              @hide="searchAnchorEl = null">
              <q-card class="q-pa-sm">
                <div class="row items-center no-wrap">
                  <q-input v-model="searchQuery" autofocus clearable clear-icon="close" outlined dense class="col"
                    style="min-width: 300px; padding-right: 30px" @keyup.enter="onSearch" />
                  <q-btn flat round dense icon="search" @click="onSearch" class="q-ml-sm" />
                </div>
                <div v-if="recentSearches.length" class="q-pt-sm">
                  <div class="text-subtitle2 q-mb-xs">最近搜尋</div>
                  <q-list class="q-pt-sm" dense separator>
                    <q-item v-for="(item, index) in recentSearches" :key="index" clickable
                      @click="searchQuery = item; onSearch()">
                      <q-item-section>{{ item }}</q-item-section>
                    </q-item>
                  </q-list>
                </div>
              </q-card>
            </q-menu>
            <q-btn flat round icon="shopping_cart" title="購物車" class="icon-button" to="/cart">
              <q-badge color="orange" rounded floating>{{ cartStore.itemCount }}</q-badge>
            </q-btn>
            <!-- 會員圖示 -->
            <div v-if="userStore.isLoggedIn" class="flex items-center">
              <q-btn flat icon="person" ref="memberBtn" class="icon-button" @click.stop="toggleMember">
                <span class="ml-2">會員中心</span>
              </q-btn>
              <!-- 會員下拉選單 -->
              <q-menu v-model="showMenu" v-if="memberAnchorEl" @hide="memberAnchorEl = null" self="top middle"
                :offset="[-50, 0]">
                <q-list>
                  <q-item clickable style="text-decoration: none; color: black;" to="/user/member">
                    <q-item-section>會員中心</q-item-section>
                  </q-item>
                  <q-item clickable @click="logout">
                    <q-item-section>登出</q-item-section>
                  </q-item>
                </q-list>
              </q-menu>
            </div>
            <!-- 未登入，則進入登入頁面 -->
            <div v-else>
              <q-btn flat round icon="person" title="登入" class="icon-button" to="/login" />
            </div>
          </div>
        </q-toolbar>
      </q-header>
      <div class="row q-px-md q-my-xl"
        style="margin-top:125px;padding-bottom:50px !important; display: flex; justify-content: center; align-items: flex-start;">
        <!-- 側邊欄區 -->
        <div class="col-2" style="width: 170px;" v-if="showSidebar">
          <q-list>
            <!-- 商品大分類 -->
            <q-expansion-item dense-toggle dense v-for="category in productCategories" :key="category.id"
              :label="category.lookupValue" expand-separator class="q-mb-sm text-subtitle2 text-weight-light">
              <!-- 商品子分類 -->
              <q-list dense class="q-pl-lg text-caption text-weight-light">
                <q-item v-for="subCategory in category.subCategories" :key="subCategory.id" clickable
                  @click="selectCategory(subCategory)">
                  <q-item-section>{{ subCategory.lookupValue }}</q-item-section>
                </q-item>
              </q-list>
            </q-expansion-item>
          </q-list>

        </div>
        <!-- 主內容區 -->
        <q-page-container class="col-9" style="padding:0px !important;">
          <router-view :key="$route.fullPath" />
        </q-page-container>
      </div>
      <!-- footer -->
      <q-footer class="text-center">
        <p>Copyright Ⓒ 2025 PubiPet</p>
      </q-footer>
    </q-layout>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCartStore } from 'src/stores/cart'
import { useLookup } from '../composables/useLookup'
import { useUserStore } from 'src/stores/user'
import { useQuasar } from 'quasar'
import { api } from 'boot/axios'

const cartStore = useCartStore()
const userStore = useUserStore()
const { fetchProductCategories, lookupMap } = useLookup()
const router = useRouter()
const route = useRoute()
const $q = useQuasar()

// 定義反應式變數
const searchQuery = ref('')
const recentSearches = ref([])
const showSearch = ref(false)
const showMenu = ref(false)
const productCategories = ref([])
const searchBtn = ref(null)
const memberBtn = ref(null)
const searchAnchorEl = ref(null)
const memberAnchorEl = ref(null)
const showSidebar = computed(() => route.meta.hasSidebar === true)

onMounted(async () => {
  setupStorageListener()
  await checkLoginStatus()
  await initRecentSearches()
  await initCartCount()
  await initProductCategories()
})

onUnmounted(() => {
  window.removeEventListener('storage', onStorageChange)
})

function setupStorageListener() {
  window.addEventListener('storage', onStorageChange)
}

function onStorageChange(event) {
  if (event.key === 'token') { // 監聽 token 的變動
    const newToken = event.newValue
    if (newToken) {
      // 如果有新的 token，則同步登入狀態
      userStore.setUser({ token: newToken })
    } else {
      // 如果 token 被清除，則登出
      userStore.logout()
    }
  }
}

// 檢查登入狀態：
// 1. 檢查是否透過郵件驗證登入（第一次登入），會透過網址帶入token
// 2. 非透過郵件登入：檢查localStorage是否有token(JWT)
// 3. 皆無：未登入
async function checkLoginStatus() {
  const emailToken = route.query.token
  // 透過郵件登入
  if (emailToken) {
    try {
      const res = await api.get(`/api/auth/verifyEmail`, { params: { emailToken } })
      const jwt = res.data.data
      userStore.setUser({ token: jwt })
      await cartStore.syncCart(); // 登入後將 localStorage 中的購物車資料同步到後端
      $q.notify({ type: 'positive', message: '登入成功' });
      router.replace('/');
    } catch (err) {
      const res = err.response?.data
      const msg = res?.message || '驗證失敗，請重新登入'
      $q.notify({ type: 'negative', message: msg })
      router.replace('/')
    }
    // 一般進入頁面，檢查 localStorage 是否有 token
  } else if (localStorage.getItem('token')) {
    try {
      const res = await api.post('/api/auth/validateToken')
      if (res.data.code !== 200) {
        logout()
        $q.notify({ type: 'negative', message: '登入過期，請重新登入' })
      }
    } catch {
      logout() // 如果有錯誤，登出
      $q.notify({ type: 'negative', message: '登入驗證失敗' })
    }
  }
}

// 初始化最近搜尋紀錄，從 localStorage 讀取
function initRecentSearches() {
  const saved = localStorage.getItem('recentSearches')
  if (saved) {
    recentSearches.value = JSON.parse(saved)
  }
}

async function initCartCount() {
  const token = userStore.token;
  if (token) {
    // 已登入，從後端獲取購物車數量
    cartStore.fetchCount(token);
  } else {
    // 未登入，從 localStorage 計算數量
    cartStore.fetchCount();
  }
}

// 初始化商品分類
async function initProductCategories() {
  await fetchProductCategories()
  productCategories.value = lookupMap.value['product_category'] || []
}

// 切換搜尋框顯示狀態
function toggleSearch() {
  nextTick(() => {
    if (searchBtn.value?.$el) {
      searchAnchorEl.value = searchBtn.value.$el
      showSearch.value = !showSearch.value
    }
  })
}

// 切換會員選單顯示狀態
function toggleMember() {
  nextTick(() => {
    if (memberBtn.value?.$el) {
      memberAnchorEl.value = memberBtn.value.$el
      showMenu.value = !showMenu.value
    }
  })
}

// 處理搜尋行為
function onSearch() {
  const keyword = searchQuery.value.trim()
  if (!keyword) return

  const index = recentSearches.value.indexOf(keyword)
  if (index !== -1) {
    recentSearches.value.splice(index, 1)
  }
  recentSearches.value.unshift(keyword)
  recentSearches.value = recentSearches.value.slice(0, 5)
  localStorage.setItem('recentSearches', JSON.stringify(recentSearches.value))
  // 跳轉到搜尋頁面並傳遞關鍵字
  router.push({ path: '/products/search', query: { keyword } })
  searchQuery.value = ''
  showSearch.value = false
}

// 選擇商品分類並跳轉
function selectCategory(subCategory) {
  router.push(`/products/${subCategory.lookupKey}`)
}

// 登出功能
function logout() {
  userStore.logout();
  localStorage.removeItem('pubi-cart'); // 清除 localStorage 中的購物車資料
  cartStore.clear(); // 清除購物車數量
  router.replace('/');
  $q.notify({ type: 'positive', message: '登出成功' });
}
</script>



<style scoped>
.header {
  display: flex;
  position: fixed;
  align-items: center;
  justify-content: space-between;
  box-sizing: border-box;
  padding: 15px 80px;
}

.logo img {
  height: 80px;
  margin-right: 10px;
  object-fit: contain;
}

.nav-link {
  padding: 10px;
  text-decoration: none;
  color: black;
  font-size: 15px;
}

.nav-link:hover {
  color: #f1a300;
}

.header-right {
  display: flex;
  align-items: center;
  margin-left: auto;
}

.icon-button {
  background: none;
  padding: 8px;
  color: gray;
  font-size: 15px;
  cursor: pointer;
}

.icon-button:hover {
  color: black;
}

.q-toolbar {
  display: flex;
  justify-content: flex-start;
}

.q-footer {
  background-color: white;
  color: black;
  padding-top: 30px;
}
</style>
