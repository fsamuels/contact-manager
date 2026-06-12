import type { NoteDto } from '../types/note';
import { parseJsonResponse } from './http';

export async function fetchNotes(personId: string): Promise<NoteDto[]> {
  const response = await fetch(`/api/persons/${personId}/notes`);
  return parseJsonResponse(response);
}

export async function addNote(personId: string, noteText: string): Promise<NoteDto> {
  const response = await fetch(`/api/persons/${personId}/notes`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ noteText }),
  });
  return parseJsonResponse(response);
}

export async function deleteNote(personId: string, noteId: string): Promise<void> {
  const response = await fetch(`/api/persons/${personId}/notes/${noteId}`, {
    method: 'DELETE',
  });
  return parseJsonResponse(response);
}
