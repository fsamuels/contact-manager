/**
 * Client-side validation for the person create/edit form.
 *
 * Mirrors the server-side Bean Validation rules; the server remains the
 * authority, so submissions that slip past this script are still rejected.
 */
(function ($) {
    'use strict';

    /**
     * Validation rules keyed by the data-validate attribute of each input.
     * Each rule returns an error message for an invalid value, or null when
     * the value is valid.
     */
    var RULES = {
        name: function (value, label) {
            return requiredMax(value, label, 30);
        },
        email: function (value, label) {
            return requiredMax(value, label, 30);
        },
        street: function (value, label) {
            return requiredMax(value, label, 60);
        },
        city: function (value, label) {
            return requiredMax(value, label, 30);
        },
        state: function (value, label) {
            if (value === '') {
                return label + ' is required.';
            }
            if (!/^[A-Za-z]{2}$/.test(value)) {
                return label + ' must be exactly 2 letters.';
            }
            return null;
        },
        zip: function (value, label) {
            if (value === '') {
                return label + ' is required.';
            }
            if (!/^\d{5}$/.test(value)) {
                return label + ' must be exactly 5 digits.';
            }
            return null;
        }
    };

    function requiredMax(value, label, max) {
        if (value === '') {
            return label + ' is required.';
        }
        if (value.length > max) {
            return label + ' must be at most ' + max + ' characters.';
        }
        return null;
    }

    /**
     * Validates a single input, rendering or clearing its error message.
     * Returns true when the input is valid.
     */
    function validateInput($input) {
        var rule = RULES[$input.data('validate')];
        if (!rule) {
            return true;
        }
        var label = $input.closest('.field').find('label').text();
        var message = rule($.trim($input.val()), label);

        $input.closest('.field').find('.client-error').remove();
        $input.toggleClass('input-invalid', message !== null);
        if (message !== null) {
            $('<span class="field-error client-error"></span>').text(message).insertAfter($input);
            return false;
        }
        return true;
    }

    $(function () {
        var $form = $('.person-form');
        if ($form.length === 0) {
            return;
        }

        // Validate on blur for immediate feedback.
        $form.on('blur', 'input[data-validate]', function () {
            validateInput($(this));
        });

        // Block submission while any field is invalid.
        $form.on('submit', function (event) {
            var valid = true;
            $form.find('input[data-validate]').each(function () {
                if (!validateInput($(this))) {
                    valid = false;
                }
            });
            if (!valid) {
                event.preventDefault();
                $form.find('input.input-invalid').first().trigger('focus');
            }
        });
    });
}(jQuery));
