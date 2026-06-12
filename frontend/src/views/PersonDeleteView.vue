<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { RouterLink, useRoute, useRouter } from 'vue-router';
import { deletePerson, fetchPerson } from '../api/persons';
import ClassicUiLink from '../components/ClassicUiLink.vue';
import type { PersonDto } from '../types/person';

const route = useRoute();
const router = useRouter();

const personId = computed(() =>
  typeof route.params.id === 'string' ? route.params.id : undefined,
);

const person = ref<PersonDto | null>(null);
const loading = ref(true);
const loadError = ref<string | null>(null);
const submitError = ref<string | null>(null);
const deleting = ref(false);

const personName = computed(() =>
  person.value ? `${person.value.firstName} ${person.value.lastName}` : '',
);

async function loadPerson(id: string) {
  loading.value = true;
  loadError.value = null;
  try {
    person.value = await fetchPerson(id);
  } catch (e) {
    person.value = null;
    loadError.value = e instanceof Error ? e.message : String(e);
  } finally {
    loading.value = false;
  }
}

async function onDelete() {
  if (!person.value || !personId.value) {
    return;
  }
  deleting.value = true;
  submitError.value = null;
  try {
    await deletePerson(personId.value);
    router.push({
      name: 'person-list',
      state: { flashSuccess: `Deleted person: ${personName.value}` },
    });
  } catch (e) {
    submitError.value = e instanceof Error ? e.message : String(e);
  } finally {
    deleting.value = false;
  }
}

watch(
  personId,
  (id) => {
    submitError.value = null;
    if (id) {
      loadPerson(id);
    } else {
      person.value = null;
      loading.value = false;
    }
  },
  { immediate: true },
);
</script>

<template>
  <div>
    <h2>Delete Person</h2>

    <p class="classic-ui-link">
      <ClassicUiLink />
    </p>

    <p v-if="loadError" class="flash flash-error">{{ loadError }}</p>
    <p v-if="submitError" class="flash flash-error">{{ submitError }}</p>

    <p v-if="loading" class="loading">Loading&hellip;</p>

    <template v-else-if="person">
      <p>
        You are about to delete the person:
        <strong>{{ personName }}</strong>, are you sure?
      </p>

      <form class="delete-form" @submit.prevent="onDelete">
        <button type="submit" class="button button-danger" :disabled="deleting">
          {{ deleting ? 'Deleting…' : 'Delete' }}
        </button>
        <RouterLink :to="{ name: 'person-list' }">Cancel</RouterLink>
      </form>
    </template>
  </div>
</template>

<style scoped>
.loading {
  color: var(--text-muted);
  font-style: italic;
}
</style>
