<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { fetchPersons } from '../api/persons';
import {
  DEFAULT_PAGE_SIZE,
  PAGE_SIZE_OPTIONS,
  type PageDto,
  type PersonDto,
} from '../types/person';

const route = useRoute();
const router = useRouter();

const page = computed(() => parsePositiveInt(route.query.page, 1));
const size = computed(() => normalizePageSize(route.query.size));

const people = ref<PersonDto[]>([]);
const personPage = ref<PageDto<PersonDto> | null>(null);
const loading = ref(true);
const error = ref<string | null>(null);

function parsePositiveInt(value: unknown, fallback: number): number {
  const parsed = Number.parseInt(String(value ?? ''), 10);
  return Number.isFinite(parsed) && parsed >= 1 ? parsed : fallback;
}

function normalizePageSize(value: unknown): number {
  const parsed = Number.parseInt(String(value ?? ''), 10);
  return PAGE_SIZE_OPTIONS.includes(parsed as (typeof PAGE_SIZE_OPTIONS)[number])
    ? parsed
    : DEFAULT_PAGE_SIZE;
}

function personName(person: PersonDto): string {
  return `${person.firstName} ${person.lastName}`;
}

async function loadPeople() {
  loading.value = true;
  error.value = null;
  try {
    const pageData = await fetchPersons(page.value, size.value);
    people.value = pageData.items;
    personPage.value = pageData;
  } catch (e) {
    people.value = [];
    personPage.value = null;
    error.value = e instanceof Error ? e.message : String(e);
  } finally {
    loading.value = false;
  }
}

function goToPage(nextPage: number) {
  router.push({ query: { page: String(nextPage), size: String(size.value) } });
}

function onPageSizeChange(event: Event) {
  const nextSize = normalizePageSize((event.target as HTMLSelectElement).value);
  router.push({ query: { page: '1', size: String(nextSize) } });
}

watch([page, size], loadPeople, { immediate: true });
</script>

<template>
  <div>
    <h2>People</h2>

    <p class="classic-ui-link">
      <a href="/persons">
        <i class="fa-solid fa-hourglass-half" aria-hidden="true"></i>
        Return to the old ways
      </a>
    </p>

    <p v-if="error" class="flash flash-error">Could not load people: {{ error }}</p>

    <p>
      <a class="button" href="/persons/new">Create Person</a>
    </p>

    <p v-if="loading" class="loading">Loading&hellip;</p>

    <template v-else-if="personPage && people.length === 0">
      <p class="no-results">No results found</p>
    </template>

    <template v-else-if="personPage">
      <form class="page-size-form" @submit.prevent>
        <label for="page-size">Per page:</label>
        <select id="page-size" :value="personPage.pageSize" @change="onPageSizeChange">
          <option v-for="option in PAGE_SIZE_OPTIONS" :key="option" :value="option">
            {{ option }}
          </option>
        </select>
      </form>

      <table class="person-table">
        <thead>
          <tr>
            <th scope="col">First name</th>
            <th scope="col">Last name</th>
            <th scope="col">Email address</th>
            <th scope="col">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="person in people" :key="person.id">
            <td>{{ person.firstName }}</td>
            <td>{{ person.lastName }}</td>
            <td>{{ person.emailAddress }}</td>
            <td class="actions">
              <a
                class="icon-link"
                :href="`/persons/${person.id}/notes`"
                :title="`Notes for ${personName(person)} (${person.noteCount})`"
                :aria-label="`Notes for ${personName(person)} (${person.noteCount})`"
              >
                <i class="fa-solid fa-note-sticky" aria-hidden="true"></i>
              </a>
              <a
                class="icon-link"
                :href="`/persons/${person.id}/edit`"
                :title="`Edit ${personName(person)}`"
                :aria-label="`Edit ${personName(person)}`"
              >
                <i class="fa-solid fa-pen-to-square" aria-hidden="true"></i>
              </a>
              <a
                class="icon-link"
                :href="`/persons/${person.id}/delete`"
                :title="`Delete ${personName(person)}`"
                :aria-label="`Delete ${personName(person)}`"
              >
                <i class="fa-solid fa-trash" aria-hidden="true"></i>
              </a>
            </td>
          </tr>
        </tbody>
      </table>

      <nav
        v-if="personPage.totalPages > 1"
        class="pagination"
        aria-label="Page navigation"
      >
        <button
          v-if="personPage.pageNumber <= 1"
          type="button"
          class="page-arrow page-disabled"
          disabled
          aria-hidden="true"
        >
          &larr;
        </button>
        <button
          v-else
          type="button"
          class="page-arrow"
          aria-label="Previous page"
          @click="goToPage(personPage.pageNumber - 1)"
        >
          &larr;
        </button>

        <template v-for="pageNumber in personPage.totalPages" :key="pageNumber">
          <span
            v-if="pageNumber === personPage.pageNumber"
            class="page-link page-current"
            aria-current="page"
          >
            {{ pageNumber }}
          </span>
          <button
            v-else
            type="button"
            class="page-link"
            :aria-label="`Page ${pageNumber}`"
            @click="goToPage(pageNumber)"
          >
            {{ pageNumber }}
          </button>
        </template>

        <button
          v-if="personPage.pageNumber >= personPage.totalPages"
          type="button"
          class="page-arrow page-disabled"
          disabled
          aria-hidden="true"
        >
          &rarr;
        </button>
        <button
          v-else
          type="button"
          class="page-arrow"
          aria-label="Next page"
          @click="goToPage(personPage.pageNumber + 1)"
        >
          &rarr;
        </button>
      </nav>
    </template>
  </div>
</template>

<style scoped>
.loading {
  color: var(--text-muted);
  font-style: italic;
}

.pagination .page-link,
.pagination .page-arrow {
  font-family: var(--font-body);
  font-size: inherit;
  cursor: pointer;
}

.pagination .page-disabled {
  cursor: default;
}
</style>
