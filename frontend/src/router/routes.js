const routes = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '', component: () => import('pages/IndexPage.vue') },
      { path: '/products/:type', component: () => import('src/pages/product/ProductList.vue'), meta: { hasSidebar: true } },
      { path: '/product/:id', component: () => import('src/pages/product/ProductInfo.vue'), meta: { hasSidebar: true } },
      { path: '/login', component: () => import('pages/user/LoginPage.vue') },
      { path: '/signup', component: () => import('pages/user/SignUp.vue') },
      { path: '/user/member', component: () => import('pages/user/MemberCenter.vue'), meta: { requiresAuth: true } },
      { path: '/cart', component: () => import('pages/cart/CartLayout.vue') },
    ]
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue')
  }
  
]

export default routes
