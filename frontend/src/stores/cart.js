import { defineStore } from 'pinia';
import { api } from 'boot/axios';

export const useCartStore = defineStore('cart', {
  state: () => ({
    itemCount: 0,
    selectedItems: []
  }),
  actions: {
    async fetchCount(token) { // 計算要顯示在 header 的購物車數量
      if (!token) {
        // 未登入狀態，從 localStorage 計算
        const localCart = JSON.parse(localStorage.getItem('pubi-cart') || '[]');
        this.itemCount = localCart.reduce((sum, item) => sum + item.quantity, 0);
        return;
      }
      try {
        const res = await api.get(`/api/cart/count`, { params: { token } });
        this.itemCount = res.data.data;
      } catch (error) {
        console.error('取得購物車數量失敗', error);
      }
    },
    clear() {
      this.itemCount = 0;
    },
    async syncCart() { // 用於同步購物車到後端
      const localCart = JSON.parse(localStorage.getItem('pubi-cart') || '[]');

      if (localCart.length > 0) {
        try {
          await api.post('/api/cart/sync', localCart );
          localStorage.removeItem('pubi-cart'); 
        } catch (error) {
          console.error('購物車同步失敗:', error);
        }
      }
    },
    setSelectedItems(items) { // 設置購物車中用戶選取要結帳的商品項目，以便成立訂單
      this.selectedItems = items;
    }
  },
});
