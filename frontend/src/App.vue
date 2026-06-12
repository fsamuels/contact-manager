<script setup lang="ts">
import { RouterView } from 'vue-router';
</script>

<template>
  <header class="site-header">
    <h1><a href="/app/">Contact Manager</a></h1>
  </header>
  <main class="content">
    <RouterView />
  </main>
</template>
