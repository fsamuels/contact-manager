<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue';
import { THEMES } from '../constants/themes';
import { useTheme } from '../composables/useTheme';

const { currentTheme, applyTheme } = useTheme();
const open = ref(false);
const menuRef = ref<HTMLElement | null>(null);

function toggleMenu(event: MouseEvent) {
  event.stopPropagation();
  open.value = !open.value;
}

function closeMenu() {
  open.value = false;
}

function selectTheme(themeId: (typeof THEMES)[number]['id']) {
  applyTheme(themeId);
  closeMenu();
}

function onDocumentClick(event: MouseEvent) {
  if (open.value && menuRef.value && !menuRef.value.contains(event.target as Node)) {
    closeMenu();
  }
}

function onDocumentKeydown(event: KeyboardEvent) {
  if (event.key === 'Escape') {
    closeMenu();
  }
}

onMounted(() => {
  document.addEventListener('click', onDocumentClick);
  document.addEventListener('keydown', onDocumentKeydown);
});

onUnmounted(() => {
  document.removeEventListener('click', onDocumentClick);
  document.removeEventListener('keydown', onDocumentKeydown);
});
</script>

<template>
  <div ref="menuRef" class="config-menu">
    <button
      type="button"
      class="config-menu-button"
      aria-haspopup="true"
      :aria-expanded="open"
      @click="toggleMenu"
    >
      &#9881; Settings
    </button>
    <div class="config-menu-panel" :hidden="!open">
      <p class="config-menu-heading">Theme</p>
      <ul class="theme-list">
        <li v-for="theme in THEMES" :key="theme.id">
          <button
            type="button"
            class="theme-option"
            :class="{ 'is-active': currentTheme === theme.id }"
            @click="selectTheme(theme.id)"
          >
            {{ theme.label }}
          </button>
        </li>
      </ul>
    </div>
  </div>
</template>
