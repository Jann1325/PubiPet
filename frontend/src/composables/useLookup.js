import { ref } from 'vue'
import axios from 'axios'

const lookupMap = ref({})

// 一般 lookup 查詢：返回key-value鍵值對
const fetchLookup = async (lookupName) => {
  if (lookupMap.value[lookupName]) return

  const response = await axios.get('/api/lookup/getByLookupName', {
    params: { lookupName }
  })
  const res = response.data;
  if (res.code === 200) {

    // 將結果轉成 key-value 鍵值對
    lookupMap.value[lookupName] = res.data.reduce((map, item) => {
      map[item.lookupKey] = item.lookupValue
      return map
    }, {})
  }
}

// 商品分類特殊處理（有子母類別）
const fetchProductCategories = async () => {
  const response = await axios.get('/api/lookup/getProdCategory');
  const res = response.data;
  if (res.code === 200) {
    const categories = res.data;
    lookupMap.value['product_category'] = categories;
  }
};

// 查找分類的中文名稱
const getLookupName = (key, subKey = null) => {
  // 如果是商品分類
  if (key === 'product_category') {
    // 查找大分類
    const category = lookupMap.value[key]?.find(item => item.lookupKey === subKey);
    // 如果找到對應的大分類，返回其名稱
    if (category) {
      return category.lookupValue;
    }
    // 如果沒找到大分類，則繼續查找子分類
    for (const category of lookupMap.value[key] || []) {
      const subCategory = category.subCategories.find(sub => sub.lookupKey === subKey);
      if (subCategory) {
        return subCategory.lookupValue;
      }
    }
  }

  // 其他一般的分類處理：直接返回值
  if (subKey) {
    return lookupMap.value[key]?.[subKey] || subKey;
  } else {
    return lookupMap.value[key] || key;
  }
}


// 轉換成下拉選單需要的格式
const getOptionsFromLookup = (lookupName) => {
  if (!lookupMap.value[lookupName]) return []

  return Object.entries(lookupMap.value[lookupName]).map(([key, value]) => ({
    label: value,  // 顯示在選單上的文字
    value: key     // 選擇的值
  }))
}

export function useLookup() {
  return {
    fetchLookup,
    fetchProductCategories,
    getLookupName,
    getOptionsFromLookup,
    lookupMap
  }
}
