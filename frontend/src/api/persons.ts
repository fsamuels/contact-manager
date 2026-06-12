import type { PageDto, PersonDto, PersonRequest } from '../types/person';
import { parseJsonResponse } from './http';

export async function fetchPersons(page: number, size: number): Promise<PageDto<PersonDto>> {
  const params = new URLSearchParams({
    page: String(page),
    size: String(size),
  });
  const response = await fetch(`/api/persons?${params}`);
  return parseJsonResponse(response);
}

export async function fetchPerson(id: string): Promise<PersonDto> {
  const response = await fetch(`/api/persons/${id}`);
  return parseJsonResponse(response);
}

export async function createPerson(request: PersonRequest): Promise<PersonDto> {
  const response = await fetch('/api/persons', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  });
  return parseJsonResponse(response);
}

export async function updatePerson(id: string, request: PersonRequest): Promise<PersonDto> {
  const response = await fetch(`/api/persons/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  });
  return parseJsonResponse(response);
}

export async function deletePerson(id: string): Promise<void> {
  const response = await fetch(`/api/persons/${id}`, { method: 'DELETE' });
  return parseJsonResponse(response);
}
