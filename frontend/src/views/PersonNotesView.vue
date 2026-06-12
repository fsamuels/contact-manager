<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { RouterLink, useRoute } from 'vue-router';
import { addNote, deleteNote, fetchNotes } from '../api/notes';
import { fetchPerson } from '../api/persons';
import ClassicUiLink from '../components/ClassicUiLink.vue';
import { ApiError } from '../types/api';
import { NOTE_MAX_LENGTH, type NoteDto } from '../types/note';
import type { PersonDto } from '../types/person';

const route = useRoute();

const personId = computed(() =>
  typeof route.params.id === 'string' ? route.params.id : undefined,
);

const person = ref<PersonDto | null>(null);
const notes = ref<NoteDto[]>([]);
const loading = ref(true);
const loadError = ref<string | null>(null);

const noteText = ref('');
const fieldError = ref<string | null>(null);
const submitError = ref<string | null>(null);
const successMessage = ref<string | null>(null);
const submitting = ref(false);

const personName = computed(() =>
  person.value ? `${person.value.firstName} ${person.value.lastName}` : '',
);

async function load(id: string) {
  loading.value = true;
  loadError.value = null;
  try {
    const [personData, noteData] = await Promise.all([fetchPerson(id), fetchNotes(id)]);
    person.value = personData;
    notes.value = noteData;
  } catch (e) {
    person.value = null;
    notes.value = [];
    loadError.value = e instanceof Error ? e.message : String(e);
  } finally {
    loading.value = false;
  }
}

async function onAddNote() {
  if (!personId.value) {
    return;
  }
  successMessage.value = null;
  submitError.value = null;

  const trimmed = noteText.value.trim();
  if (trimmed === '') {
    fieldError.value = 'Note text is required.';
    return;
  }
  fieldError.value = null;

  submitting.value = true;
  try {
    await addNote(personId.value, trimmed);
    noteText.value = '';
    notes.value = await fetchNotes(personId.value);
    successMessage.value = 'Added note.';
  } catch (e) {
    if (e instanceof ApiError && e.fieldErrors?.noteText) {
      fieldError.value = e.fieldErrors.noteText;
    } else {
      submitError.value = e instanceof Error ? e.message : String(e);
    }
  } finally {
    submitting.value = false;
  }
}

async function onDeleteNote(noteId: string) {
  if (!personId.value) {
    return;
  }
  successMessage.value = null;
  submitError.value = null;
  try {
    await deleteNote(personId.value, noteId);
    notes.value = await fetchNotes(personId.value);
    successMessage.value = 'Deleted note.';
  } catch (e) {
    submitError.value = e instanceof Error ? e.message : String(e);
  }
}

function noteDate(note: NoteDto): string {
  // createdAt is an ISO local date-time string; the first 10 characters
  // are the date, matching the JSP UI's display.
  return note.createdAt.slice(0, 10);
}

watch(
  personId,
  (id) => {
    fieldError.value = null;
    submitError.value = null;
    successMessage.value = null;
    if (id) {
      load(id);
    } else {
      person.value = null;
      notes.value = [];
      loading.value = false;
    }
  },
  { immediate: true },
);
</script>

<template>
  <div>
    <h2>Notes<template v-if="person"> for {{ personName }}</template></h2>

    <p class="classic-ui-link">
      <ClassicUiLink />
    </p>

    <p v-if="successMessage" class="flash flash-success">{{ successMessage }}</p>
    <p v-if="loadError" class="flash flash-error">{{ loadError }}</p>
    <p v-if="submitError" class="flash flash-error">{{ submitError }}</p>

    <p v-if="loading" class="loading">Loading&hellip;</p>

    <template v-else-if="person">
      <form class="note-form" novalidate @submit.prevent="onAddNote">
        <div class="field">
          <label for="noteText">New note</label>
          <textarea
            id="noteText"
            v-model="noteText"
            rows="3"
            :maxlength="NOTE_MAX_LENGTH"
            :class="{ 'input-invalid': fieldError !== null }"
          ></textarea>
          <span v-if="fieldError" class="field-error">{{ fieldError }}</span>
        </div>
        <button type="submit" class="button" :disabled="submitting">
          {{ submitting ? 'Adding…' : 'Add Note' }}
        </button>
      </form>

      <p v-if="notes.length === 0" class="no-results">No notes found</p>

      <ul v-else class="note-list">
        <li v-for="note in notes" :key="note.id" class="note">
          <p class="note-text">{{ note.noteText }}</p>
          <div class="note-meta">
            <span>Added {{ noteDate(note) }}</span>
            <button
              type="button"
              class="icon-button"
              title="Delete note"
              aria-label="Delete note"
              @click="onDeleteNote(note.id)"
            >
              <i class="fa-solid fa-trash" aria-hidden="true"></i>
            </button>
          </div>
        </li>
      </ul>
    </template>

    <p class="back-link">
      <RouterLink :to="{ name: 'person-list' }">&larr; Back to people</RouterLink>
    </p>
  </div>
</template>

<style scoped>
.loading {
  color: var(--text-muted);
  font-style: italic;
}
</style>
