import { createRouter, createWebHistory } from 'vue-router';
import PersonListView from '../views/PersonListView.vue';

const router = createRouter({
  history: createWebHistory('/app/'),
  routes: [
    {
      path: '/',
      name: 'person-list',
      component: PersonListView,
    },
  ],
});

export default router;
