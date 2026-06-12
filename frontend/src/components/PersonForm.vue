<script setup lang="ts">
import { ref } from 'vue';
import { RouterLink } from 'vue-router';
import {
  PERSON_FIELD_LABELS,
  type PersonFormData,
  validatePersonField,
} from '../utils/personValidation';

const model = defineModel<PersonFormData>({ required: true });

const props = defineProps<{
  fieldErrors: Record<string, string>;
  submitting?: boolean;
}>();

const emit = defineEmits<{
  submit: [];
}>();

const invalidFields = ref<Set<keyof PersonFormData>>(new Set());

function onBlur(field: keyof PersonFormData) {
  const message = validatePersonField(field, model.value[field]);
  if (message) {
    invalidFields.value.add(field);
  } else {
    invalidFields.value.delete(field);
  }
}

function isInvalid(field: keyof PersonFormData): boolean {
  return invalidFields.value.has(field) || field in props.fieldErrors;
}
</script>

<template>
  <form class="person-form" novalidate @submit.prevent="emit('submit')">
    <div class="field">
      <label for="firstName">{{ PERSON_FIELD_LABELS.firstName }}</label>
      <input
        id="firstName"
        v-model="model.firstName"
        type="text"
        maxlength="30"
        autocomplete="given-name"
        :class="{ 'input-invalid': isInvalid('firstName') }"
        @blur="onBlur('firstName')"
      />
      <span v-if="fieldErrors.firstName" class="field-error">{{ fieldErrors.firstName }}</span>
    </div>

    <div class="field">
      <label for="lastName">{{ PERSON_FIELD_LABELS.lastName }}</label>
      <input
        id="lastName"
        v-model="model.lastName"
        type="text"
        maxlength="30"
        autocomplete="family-name"
        :class="{ 'input-invalid': isInvalid('lastName') }"
        @blur="onBlur('lastName')"
      />
      <span v-if="fieldErrors.lastName" class="field-error">{{ fieldErrors.lastName }}</span>
    </div>

    <div class="field">
      <label for="emailAddress">{{ PERSON_FIELD_LABELS.emailAddress }}</label>
      <input
        id="emailAddress"
        v-model="model.emailAddress"
        type="email"
        maxlength="30"
        autocomplete="email"
        :class="{ 'input-invalid': isInvalid('emailAddress') }"
        @blur="onBlur('emailAddress')"
      />
      <span v-if="fieldErrors.emailAddress" class="field-error">{{ fieldErrors.emailAddress }}</span>
    </div>

    <div class="field">
      <label for="streetAddress">{{ PERSON_FIELD_LABELS.streetAddress }}</label>
      <input
        id="streetAddress"
        v-model="model.streetAddress"
        type="text"
        maxlength="60"
        autocomplete="street-address"
        :class="{ 'input-invalid': isInvalid('streetAddress') }"
        @blur="onBlur('streetAddress')"
      />
      <span v-if="fieldErrors.streetAddress" class="field-error">{{ fieldErrors.streetAddress }}</span>
    </div>

    <div class="field">
      <label for="city">{{ PERSON_FIELD_LABELS.city }}</label>
      <input
        id="city"
        v-model="model.city"
        type="text"
        maxlength="30"
        autocomplete="address-level2"
        :class="{ 'input-invalid': isInvalid('city') }"
        @blur="onBlur('city')"
      />
      <span v-if="fieldErrors.city" class="field-error">{{ fieldErrors.city }}</span>
    </div>

    <div class="field">
      <label for="state">{{ PERSON_FIELD_LABELS.state }}</label>
      <input
        id="state"
        v-model="model.state"
        type="text"
        maxlength="2"
        size="2"
        autocomplete="address-level1"
        :class="{ 'input-invalid': isInvalid('state') }"
        @blur="onBlur('state')"
      />
      <span v-if="fieldErrors.state" class="field-error">{{ fieldErrors.state }}</span>
    </div>

    <div class="field">
      <label for="zipCode">{{ PERSON_FIELD_LABELS.zipCode }}</label>
      <input
        id="zipCode"
        v-model="model.zipCode"
        type="text"
        maxlength="5"
        size="5"
        inputmode="numeric"
        autocomplete="postal-code"
        :class="{ 'input-invalid': isInvalid('zipCode') }"
        @blur="onBlur('zipCode')"
      />
      <span v-if="fieldErrors.zipCode" class="field-error">{{ fieldErrors.zipCode }}</span>
    </div>

    <div class="form-actions">
      <button type="submit" class="button" :disabled="submitting">
        {{ submitting ? 'Saving…' : 'Save' }}
      </button>
      <RouterLink :to="{ name: 'person-list' }">Cancel</RouterLink>
    </div>
  </form>
</template>
