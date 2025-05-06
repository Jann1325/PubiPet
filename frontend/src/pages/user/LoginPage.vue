<template>
  <q-page class="flex flex-center">
    <q-card class="q-pa-lg" style="width: 500px;">
      <q-card-section class="text-h6 text-center">登入</q-card-section>
      <q-card-section class="q-pa-xl">
        <!-- 輸入框與錯誤顯示 -->
        <q-input v-model="email" type="email" placeholder="請輸入Email" clearable clear-icon="close" dense
          class="q-mb-none" @update:modelValue="checkEmailFormat" @keydown.enter="handleLogin" />
        <!-- 只有在錯誤訊息存在時顯示 -->
        <div class="text-caption text-grey q-mt-none col-12" style="height: 20px;">
          <span v-show="emailError" class="text-negative">{{ emailError }}</span>
        </div>
        <!-- 登入按鈕 -->
        <q-btn label="登入/註冊" style="background-color: #f1a300; color: white;" class="full-width q-mt-md"
          @click="handleLogin" :disabled="isButtonDisabled" />
      </q-card-section>
    </q-card>
    <q-dialog v-model="showVerifyEmailDialog" persistent>
      <q-card class="q-pa-md" style="min-width: 300px; position: relative;">
        <!-- 右上角的叉叉按鈕 -->
        <q-btn icon="close" flat round dense v-close-popup class="absolute-top-right q-mt-sm q-mr-sm" />
        <q-card-section class="text-h6 text-center q-mt-md">驗證信已寄出</q-card-section>
        <q-card-section class="text-center">
          已將登入連結寄至 <b>{{ email }}</b><br />
          請至信箱點擊完成登入。
        </q-card-section>
        <q-card-actions class="justify-center">
          <q-btn :disable="resendCountdown > 0" @click="handleLogin"
            :label="resendCountdown > 0 ? `請稍後 ${resendCountdown} 秒` : '重新寄送驗證信'" flat color="orange" />
        </q-card-actions>
      </q-card>
    </q-dialog>

  </q-page>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { api } from 'src/boot/axios'

const router = useRouter()
const email = ref('')
const emailError = ref('') // 儲存 email 的錯誤訊息
const isButtonDisabled = ref(true) // 控制登入按鈕是否禁用

onMounted(() => {
  if (localStorage.getItem("token")) {  // 檢查 localStorage 是否有 token（已登入）
    router.push("/");
  }
  // 監聽 localStorage 變化
  window.addEventListener("storage", onStorageChange);
});

// 當 storage 變化時跳轉
function onStorageChange(event) {
  if (event.key === "token" && event.newValue) {
    window.location.replace('/')
  }
}

onBeforeUnmount(() => {// 清除事件監聽器
  window.removeEventListener("storage", onStorageChange);
});

// 檢查 email 格式
function checkEmailFormat() {
  if (!email.value) {
    isButtonDisabled.value = true
    return
  }
  if (!isValidEmail(email.value)) {
    isButtonDisabled.value = true
  } else {
    isButtonDisabled.value = false
  }
}

// 驗證 email 格式
function isValidEmail(email) {
  const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
  return emailRegex.test(email)
}

// 登入/註冊按鈕
import { useQuasar } from 'quasar';
import { Loading } from 'quasar';
const $q = useQuasar();

async function handleLogin() {
  if (isButtonDisabled.value) {
    emailError.value = '請輸入有效的Email地址'
    return
  }
  emailError.value = ''

  Loading.show({
    message: '處理中，請稍候...'
  });
  try {
    const response = await api.post('/api/auth/login', { email: email.value });
    const res = response.data;
    if (res.message === 'NEED_REGISTER') {
      router.push({ path: '/signup', query: { email: email.value } })
    } else if (res.code === 1008) {
      $q.notify({ type: 'negative', message: res.message });
    } else {
      // 顯示提示：驗證信已寄出
      showVerifyEmailDialog.value = true
      // 倒數一分鐘
      startResendCountdown()
    }
  } catch (error) {
    console.error('登入錯誤：', error)
    alert('登入過程中發生錯誤')
  } finally {
    Loading.hide();
  }
}

const showVerifyEmailDialog = ref(false)
const resendCountdown = ref(60)
let countdownTimer = null

// 倒數一分鐘
function startResendCountdown() {
  resendCountdown.value = 60
  countdownTimer = setInterval(() => {
    resendCountdown.value--
    if (resendCountdown.value <= 0) {
      clearInterval(countdownTimer)
    }
  }, 1001)
}
</script>
