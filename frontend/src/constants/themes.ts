export const THEME_STORAGE_KEY = 'contactManagerTheme';
export const DEFAULT_THEME = 'light';

export const THEMES = [
  { id: 'light', label: 'Light Mode' },
  { id: 'dark', label: 'Dark Mode' },
  { id: 'google', label: 'Google' },
  { id: 'claude', label: 'Claude' },
  { id: 'facebook', label: 'Facebook' },
  { id: 'alaska', label: 'Alaska Airlines' },
  { id: 'reddit', label: 'Reddit' },
  { id: 'yahoo', label: 'Yahoo' },
  { id: 'wikipedia', label: 'Wikipedia' },
  { id: 'amazon', label: 'Amazon' },
] as const;

export type ThemeId = (typeof THEMES)[number]['id'];

export function isThemeId(value: string | null): value is ThemeId {
  return THEMES.some((theme) => theme.id === value);
}
