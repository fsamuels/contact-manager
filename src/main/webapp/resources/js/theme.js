/*
 * Theme switching via the top-bar Settings menu.
 *
 * The chosen theme is applied as a data-theme attribute on <html> (the CSS
 * custom properties in styles.css do the rest) and persisted per browser in
 * localStorage. An inline script in header.jspf re-applies the saved theme
 * before first paint, so this file only has to handle the menu itself.
 */
(function ($) {
    'use strict';

    var STORAGE_KEY = 'contactManagerTheme';
    var DEFAULT_THEME = 'light';

    function currentTheme() {
        return document.documentElement.getAttribute('data-theme') || DEFAULT_THEME;
    }

    function applyTheme(theme) {
        document.documentElement.setAttribute('data-theme', theme);
        try {
            window.localStorage.setItem(STORAGE_KEY, theme);
        } catch (ignored) {
            // Storage unavailable (e.g. private browsing): the theme still
            // applies to this page, it just will not persist.
        }
    }

    $(function () {
        var $menu = $('.config-menu');
        if (!$menu.length) {
            return;
        }
        var $button = $menu.find('.config-menu-button');
        var $panel = $menu.find('.config-menu-panel');

        function markActiveOption() {
            var theme = currentTheme();
            $menu.find('.theme-option').each(function () {
                var $option = $(this);
                $option.toggleClass('is-active', $option.data('theme-option') === theme);
            });
        }

        function setOpen(open) {
            $button.attr('aria-expanded', String(open));
            $panel.prop('hidden', !open);
        }

        $button.on('click', function (event) {
            event.stopPropagation();
            setOpen($panel.prop('hidden'));
        });

        $menu.on('click', '.theme-option', function () {
            applyTheme($(this).data('theme-option'));
            markActiveOption();
            setOpen(false);
        });

        // Close the menu when clicking elsewhere or pressing Escape.
        $(document).on('click', function (event) {
            if (!$panel.prop('hidden') && !$.contains($menu[0], event.target)) {
                setOpen(false);
            }
        });
        $(document).on('keydown', function (event) {
            if (event.key === 'Escape') {
                setOpen(false);
            }
        });

        markActiveOption();
    });
})(jQuery);
