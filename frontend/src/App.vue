<script setup lang="ts">
import { onMounted, ref } from 'vue';

const totalPeople = ref<number | null>(null);
const error = ref<string | null>(null);

onMounted(async () => {
  try {
    const response = await fetch('/api/persons?size=10');
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`);
    }
    const page = await response.json();
    totalPeople.value = page.totalItems;
  } catch (e) {
    error.value = e instanceof Error ? e.message : String(e);
  }
});
</script>

<template>
  <main class="hello">
    <h1>Hello from Vue 3</h1>
    <p>This page is built by Vite and served by Spring Boot.</p>
    <p v-if="totalPeople !== null">
      The REST API reports <strong>{{ totalPeople }}</strong> people in the contact manager.
    </p>
    <p v-else-if="error">Could not reach the API: {{ error }}</p>
    <p v-else>Asking the REST API how many people exist&hellip;</p>
    <p><a href="/persons">Back to the classic UI</a></p>
  </main>
</template>

<style>
body {
  font-family: -apple-system, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
  background: #fafafa;
  color: #222;
  margin: 0;
}

.hello {
  max-width: 48rem;
  margin: 4rem auto;
  padding: 0 1.5rem;
}
</style>
