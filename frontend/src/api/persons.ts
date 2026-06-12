import type { PageDto, PersonDto } from '../types/person';

export async function fetchPersons(page: number, size: number): Promise<PageDto<PersonDto>> {
  const params = new URLSearchParams({
    page: String(page),
    size: String(size),
  });
  const response = await fetch(`/api/persons?${params}`);
  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`);
  }
  return response.json();
}
