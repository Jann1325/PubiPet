<template>
  <q-page class="flex flex-center">
    <q-card class="q-pa-lg" style="width: 500px;">
      <q-card-section class="text-h6 text-center">註冊</q-card-section>
      <q-card-section class="q-px-xl q-py-md">
        <!-- 輸入框 -->
        <q-input v-model="email" type="email" disable class="q-mb-none" />
        <q-input v-model="userName" placeholder="用戶名" lazy-rules :rules="[val => val && val.length > 0 || '請輸入用戶名']"
          clearable clear icon=" close" dense class="q-mt-md" />
        <q-select v-model="gender" :options="genderOptions" option-value="value" option-label="label" emit-value
          map-options dense label="性別（選填）" class="q-mb-md" />
        <div class="row q-gutter-x-md q-my-md items-center">
          <!-- 年 -->
          <q-select v-model="birthYear" :options="yearOptions" label="年" dense class="col q-pb-none"
            :rules="[val => !!val || '請選擇年份']" lazy-rules />
          <!-- 月 -->
          <q-select v-model="birthMonth" :options="monthOptions" label="月" dense class="col q-pb-none"
            :rules="[val => !!val || '請選擇月份']" lazy-rules />
          <!-- 日 -->
          <q-select v-model="birthDay" :options="dayOptions" label="日" :rules="[val => !!val || '請選擇日期']" lazy-rules
            :disable="!birthYear || !birthMonth" dense class="col q-pb-none" />
          <div class="text-caption text-grey q-mt-none col-12">
            <span v-show="!birthYear || !birthMonth">選擇日期前請先選擇年份與月份</span>
          </div>
          <div class="text-caption text-negative q-mt-xs q-my-none col-12">
            <span v-show="isUnderAge">必須年滿 7 歲才能註冊帳號</span>
          </div>
        </div>
        <div class="q-my-md">
          <q-checkbox size="26px" v-model="accept" dense checked-icon="task_alt" unchecked-icon="radio_button_unchecked"
            label="我同意網站服務條款及隱私權政策" style="font-size: 12px; color: gray;" color="gray" />
        </div>
        <!-- 註冊按鈕 -->
        <q-btn label="註冊" style="background-color: #f1a300; color: white;" class="full-width" @click="handleRegister"
          :disabled="isUnderAge || !birthYear || !birthMonth || !birthDay || !userName || !accept" />
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router'
import { useLookup } from 'src/composables/useLookup'
import { api } from 'src/boot/axios'

const route = useRoute()
const router = useRouter()
const email = ref('');
const userName = ref('');
const gender = ref('')
const genderOptions = ref([])
// 年份選項
const currentYear = new Date().getFullYear()
const yearOptions = Array.from({ length: currentYear - 1900 + 1 }, (_, i) => currentYear - i)
// 月份選項
const monthOptions = Array.from({ length: 12 }, (_, i) => i + 1)
// 出生日期選項（依月份動態調整天數）
const birthYear = ref(null)
const birthMonth = ref(null)
const birthDay = ref(null)
// 同意條款
const accept = ref(false);
const isUnderAge = computed(() => !isAtLeast7YearsOld())
const { fetchLookup, getOptionsFromLookup } = useLookup()


onMounted(async () => {
  await fetchLookup('gender') // 取得性別下拉選單
  genderOptions.value = getOptionsFromLookup('gender')
  if (route.query.email) { // 取得從登入頁面獲得的電子信箱
    email.value = route.query.email
  }
})

// 日期選項（根據用戶選擇的年月顯示）
const dayOptions = computed(() => {
  if (birthYear.value && birthMonth.value) {
    const daysInMonth = new Date(birthYear.value, birthMonth.value, 0).getDate()
    return Array.from({ length: daysInMonth }, (_, i) => i + 1)
  }
  return []
})

// 檢查年齡至少七歲
const isAtLeast7YearsOld = () => {
  if (!birthYear.value || !birthMonth.value || !birthDay.value) return false
  const birthDate = new Date(birthYear.value, birthMonth.value - 1, birthDay.value)
  const today = new Date()
  const age = today.getFullYear() - birthDate.getFullYear()
  const hasHadBirthdayThisYear =
    today.getMonth() > birthDate.getMonth() ||
    (today.getMonth() === birthDate.getMonth() && today.getDate() >= birthDate.getDate())

  return hasHadBirthdayThisYear ? age >= 7 : age - 1 >= 7
}

// 註冊
const handleRegister = async () => {
  if (!isAtLeast7YearsOld() || !birthYear.value || !birthMonth.value || !birthDay.value || !userName.value) {
    return
  }
  try {
    let birth = ''
    if (birthYear.value && birthMonth.value && birthDay.value) {
      const pad = (n) => String(n).padStart(2, '0')
      birth = `${birthYear.value}-${pad(birthMonth.value)}-${pad(birthDay.value)}`
    }

    const response = await api.post('/api/auth/register', {
      email: email.value,
      userName: userName.value,
      gender: gender.value,
      birth
    });

    if (response.code === 200) {
      // 註冊成功後跳轉到登入頁
      router.push('/login');
    }
  } catch (error) {
    console.error('註冊失敗:', error);
  }
};

</script>