<template>
  <q-page class="flex flex-center" style="width:100%;">
    <q-card class="q-pa-lg" style="width:100%;">
      <!-- 頁籤 -->
      <q-tabs v-model="tab" dense class="text-grey" active-color="orange" indicator-color="orange" aria-label="會員中心頁籤">
        <q-tab name="personal" label="個人資訊" />
        <q-tab name="orders" label="訂單" />
      </q-tabs>
      <q-tab-panels v-model="tab">
        <q-tab-panel name="personal">
          <PersonalTab />
        </q-tab-panel>
        <q-tab-panel name="orders">
          <OrdersTab />
        </q-tab-panel>
      </q-tab-panels>
    </q-card>
  </q-page>
</template>

<script setup>
import { ref } from 'vue'
const tab = ref('personal')

import PersonalTab from './memberTabs/PersonalTab.vue'
import OrdersTab from './memberTabs/OrdersTab.vue'
</script>

<style scoped></style>