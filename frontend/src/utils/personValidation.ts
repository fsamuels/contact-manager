import type { PersonRequest } from '../types/person';

export type PersonFormData = PersonRequest;

export const PERSON_FIELDS: (keyof PersonFormData)[] = [
  'firstName',
  'lastName',
  'emailAddress',
  'streetAddress',
  'city',
  'state',
  'zipCode',
];

export const PERSON_FIELD_LABELS: Record<keyof PersonFormData, string> = {
  firstName: 'First name',
  lastName: 'Last name',
  emailAddress: 'Email address',
  streetAddress: 'Street address',
  city: 'City',
  state: 'State',
  zipCode: 'Zip code',
};

export function emptyPersonForm(): PersonFormData {
  return {
    firstName: '',
    lastName: '',
    emailAddress: '',
    streetAddress: '',
    city: '',
    state: '',
    zipCode: '',
  };
}

function requiredMax(value: string, label: string, max: number): string | null {
  if (value === '') {
    return `${label} is required.`;
  }
  if (value.length > max) {
    return `${label} must be at most ${max} characters.`;
  }
  return null;
}

export function validatePersonField(field: keyof PersonFormData, rawValue: string): string | null {
  const value = rawValue.trim();
  const label = PERSON_FIELD_LABELS[field];

  switch (field) {
    case 'firstName':
    case 'lastName':
      return requiredMax(value, label, 30);
    case 'emailAddress':
      return requiredMax(value, label, 30);
    case 'streetAddress':
      return requiredMax(value, label, 60);
    case 'city':
      return requiredMax(value, label, 30);
    case 'state':
      if (value === '') {
        return `${label} is required.`;
      }
      if (!/^[A-Za-z]{2}$/.test(value)) {
        return `${label} must be exactly 2 letters.`;
      }
      return null;
    case 'zipCode':
      if (value === '') {
        return `${label} is required.`;
      }
      if (!/^\d{5}$/.test(value)) {
        return `${label} must be exactly 5 digits.`;
      }
      return null;
    default:
      return null;
  }
}

export function validatePersonForm(data: PersonFormData): Record<string, string> {
  const errors: Record<string, string> = {};
  for (const field of PERSON_FIELDS) {
    const message = validatePersonField(field, data[field]);
    if (message) {
      errors[field] = message;
    }
  }
  return errors;
}

export function trimPersonForm(data: PersonFormData): PersonRequest {
  return {
    firstName: data.firstName.trim(),
    lastName: data.lastName.trim(),
    emailAddress: data.emailAddress.trim(),
    streetAddress: data.streetAddress.trim(),
    city: data.city.trim(),
    state: data.state.trim(),
    zipCode: data.zipCode.trim(),
  };
}
