import { createRouter, createWebHistory } from 'vue-router';
import PersonDeleteView from '../views/PersonDeleteView.vue';
import PersonFormView from '../views/PersonFormView.vue';
import PersonListView from '../views/PersonListView.vue';

const router = createRouter({
  history: createWebHistory('/app/'),
  routes: [
    {
      path: '/',
      name: 'person-list',
      component: PersonListView,
    },
    {
      path: '/persons/new',
      name: 'person-create',
      component: PersonFormView,
    },
    {
      path: '/persons/:id/edit',
      name: 'person-edit',
      component: PersonFormView,
    },
    {
      path: '/persons/:id/delete',
      name: 'person-delete',
      component: PersonDeleteView,
    },
  ],
});

export default router;
