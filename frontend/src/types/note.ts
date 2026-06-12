export interface NoteDto {
  id: string;
  personId: string;
  noteText: string;
  createdAt: string;
}

export interface NoteRequest {
  noteText: string;
}

export const NOTE_MAX_LENGTH = 1000;
