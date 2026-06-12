import { ApiError, type ProblemDetail } from '../types/api';

export async function parseJsonResponse<T>(response: Response): Promise<T> {
  if (response.ok) {
    if (response.status === 204) {
      return undefined as T;
    }
    return response.json() as Promise<T>;
  }

  const contentType = response.headers.get('Content-Type') ?? '';
  if (contentType.includes('application/problem+json')) {
    const problem = (await response.json()) as ProblemDetail;
    throw new ApiError(
      problem.detail ?? problem.title ?? `HTTP ${response.status}`,
      response.status,
      problem.title,
      problem.errors,
    );
  }

  throw new ApiError(`HTTP ${response.status}`, response.status);
}
