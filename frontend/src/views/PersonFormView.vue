<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { createPerson, fetchPerson, updatePerson } from '../api/persons';
import ClassicUiLink from '../components/ClassicUiLink.vue';
import PersonForm from '../components/PersonForm.vue';
import { ApiError } from '../types/api';
import type { PersonDto } from '../types/person';
import {
  emptyPersonForm,
  trimPersonForm,
  validatePersonForm,
  type PersonFormData,
} from '../utils/personValidation';

const route = useRoute();
const router = useRouter();

const personId = computed(() =>
  typeof route.params.id === 'string' ? route.params.id : undefined,
);
const isEdit = computed(() => personId.value !== undefined);
const formTitle = computed(() => (isEdit.value ? 'Edit Person' : 'Create Person'));

const form = ref<PersonFormData>(emptyPersonForm());
const fieldErrors = ref<Record<string, string>>({});
const loading = ref(isEdit.value);
const loadError = ref<string | null>(null);
const submitError = ref<string | null>(null);
const submitting = ref(false);

function personToForm(person: PersonDto): PersonFormData {
  return {
    firstName: person.firstName,
    lastName: person.lastName,
    emailAddress: person.emailAddress,
    streetAddress: person.streetAddress,
    city: person.city,
    state: person.state,
    zipCode: person.zipCode,
  };
}

async function loadPerson(id: string) {
  loading.value = true;
  loadError.value = null;
  try {
    form.value = personToForm(await fetchPerson(id));
  } catch (e) {
    loadError.value = e instanceof Error ? e.message : String(e);
  } finally {
    loading.value = false;
  }
}

function redirectToList(message: string) {
  router.push({ name: 'person-list', state: { flashSuccess: message } });
}

async function onSubmit() {
  submitError.value = null;
  const clientErrors = validatePersonForm(form.value);
  fieldErrors.value = clientErrors;
  if (Object.keys(clientErrors).length > 0) {
    return;
  }

  submitting.value = true;
  try {
    const payload = trimPersonForm(form.value);
    if (isEdit.value && personId.value) {
      const updated = await updatePerson(personId.value, payload);
      redirectToList(`Updated person: ${updated.firstName} ${updated.lastName}`);
    } else {
      const created = await createPerson(payload);
      redirectToList(`Created person: ${created.firstName} ${created.lastName}`);
    }
  } catch (e) {
    if (e instanceof ApiError && e.fieldErrors) {
      fieldErrors.value = e.fieldErrors;
    } else {
      submitError.value = e instanceof Error ? e.message : String(e);
    }
  } finally {
    submitting.value = false;
  }
}

watch(
  personId,
  (id) => {
    fieldErrors.value = {};
    submitError.value = null;
    if (id) {
      loadPerson(id);
    } else {
      form.value = emptyPersonForm();
      loading.value = false;
      loadError.value = null;
    }
  },
  { immediate: true },
);
</script>

<template>
  <div>
    <h2>{{ formTitle }}</h2>

    <p class="classic-ui-link">
      <ClassicUiLink />
    </p>

    <p v-if="loadError" class="flash flash-error">{{ loadError }}</p>
    <p v-if="submitError" class="flash flash-error">{{ submitError }}</p>

    <p v-if="loading" class="loading">Loading&hellip;</p>

    <PersonForm
      v-else-if="!loadError"
      v-model="form"
      :field-errors="fieldErrors"
      :submitting="submitting"
      @submit="onSubmit"
    />
  </div>
</template>

<style scoped>
.loading {
  color: var(--text-muted);
  font-style: italic;
}
</style>
