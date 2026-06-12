<script setup lang="ts">
import { RouterView } from 'vue-router';
import ThemePicker from './components/ThemePicker.vue';
</script>

<template>
  <header class="site-header">
    <h1><a href="/app/">Contact Manager</a></h1>
    <ThemePicker />
  </header>
  <main class="content">
    <RouterView />
  </main>
</template>
