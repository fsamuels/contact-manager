import { ref } from 'vue';
import {
  DEFAULT_THEME,
  THEME_STORAGE_KEY,
  type ThemeId,
  isThemeId,
} from '../constants/themes';

function readStoredTheme(): ThemeId {
  try {
    const stored = window.localStorage.getItem(THEME_STORAGE_KEY);
    return isThemeId(stored) ? stored : DEFAULT_THEME;
  } catch {
    return DEFAULT_THEME;
  }
}

function readDocumentTheme(): ThemeId {
  const onDocument = document.documentElement.getAttribute('data-theme');
  return isThemeId(onDocument) ? onDocument : readStoredTheme();
}

const currentTheme = ref<ThemeId>(readDocumentTheme());

export function useTheme() {
  function applyTheme(theme: ThemeId) {
    document.documentElement.setAttribute('data-theme', theme);
    currentTheme.value = theme;
    try {
      window.localStorage.setItem(THEME_STORAGE_KEY, theme);
    } catch {
      // Storage unavailable; theme still applies for this page.
    }
  }

  return { currentTheme, applyTheme };
}
