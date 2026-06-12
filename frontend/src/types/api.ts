export interface ProblemDetail {
  title?: string;
  detail?: string;
  status?: number;
  errors?: Record<string, string>;
}

export class ApiError extends Error {
  readonly status: number;
  readonly title?: string;
  readonly fieldErrors?: Record<string, string>;

  constructor(message: string, status: number, title?: string, fieldErrors?: Record<string, string>) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.title = title;
    this.fieldErrors = fieldErrors;
  }
}
