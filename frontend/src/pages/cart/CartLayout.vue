<template>
  <div class="q-pa-md">
    <!-- 流程步驟 -->
    <div class="q-mb-xl flex justify-around items-center text-center text-sm text-grey-7">
      <div :class="['step', step >= 1 ? 'active' : '']">1. 確認購物車</div>
      <div :class="['step', step >= 2 ? 'active' : '']">2. 填寫資料</div>
      <div :class="['step', step === 3 ? 'active' : '']">3. 完成訂購</div>
    </div>

    <component :is="currentComponent" @next="handleNext" @back="step--" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useQuasar } from 'quasar';
import { useUserStore } from 'src/stores/user';
import CartStep1 from './CartStep1.vue';
import CartStep2 from './CartStep2.vue';
import CartStep3 from './CartStep3.vue';

const step = ref(1)

onMounted(() => {
  step.value = 1
})

const currentComponent = computed(() => {
  switch (step.value) {
    case 1: return CartStep1
    case 2: return CartStep2
    case 3: return CartStep3
    default: return CartStep1
  }
})


const $q = useQuasar();
const userStore = useUserStore();
// 點選下一步，除了第一頁需要檢查是否已登入
function handleNext() {
  const requiresLogin = [2, 3];
  if (requiresLogin.includes(step.value + 1) && !userStore.isLoggedIn) {
    $q.notify({ type: 'warning', message: '請先登入才能進行下一步' });
    return;
  }
  step.value++;
}
</script>

<style scoped>
.step {
  padding: 8px 12px;
  border-bottom: 2px solid transparent;
}

.step.active {
  font-weight: bold;
  color: #ff9800;
  border-bottom: 2px solid #ff9800;
}
</style>