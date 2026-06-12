/*
 * Client-side validation for the Create / Edit Person form, implemented with jQuery.
 *
 * Mirrors the server-side Bean Validation rules so the user gets immediate feedback. The
 * server remains the authoritative validator; this layer is purely for user experience.
 */
(function ($) {
    "use strict";

    // Field definitions: each validator returns null when valid, or an error message when not.
    var rules = {
        firstName: function (v) {
            if (!v.trim()) { return "First name is required."; }
            if (v.length > 30) { return "First name must be at most 30 characters."; }
            return null;
        },
        lastName: function (v) {
            if (!v.trim()) { return "Last name is required."; }
            if (v.length > 30) { return "Last name must be at most 30 characters."; }
            return null;
        },
        emailAddress: function (v) {
            if (!v.trim()) { return "Email address is required."; }
            if (v.length > 30) { return "Email address must be at most 30 characters."; }
            return null;
        },
        streetAddress: function (v) {
            if (!v.trim()) { return "Street address is required."; }
            if (v.length > 60) { return "Street address must be at most 60 characters."; }
            return null;
        },
        city: function (v) {
            if (!v.trim()) { return "City is required."; }
            if (v.length > 30) { return "City must be at most 30 characters."; }
            return null;
        },
        state: function (v) {
            if (!v.trim()) { return "State is required."; }
            if (!/^[A-Za-z]{2}$/.test(v)) { return "State must be exactly 2 letters."; }
            return null;
        },
        zipCode: function (v) {
            if (!v.trim()) { return "Zip code is required."; }
            if (!/^\d{5}$/.test(v)) { return "Zip code must be exactly 5 digits."; }
            return null;
        }
    };

    /**
     * Validates a single field, updating its inline error message and styling.
     * @param {string} name the field name / input id
     * @returns {boolean} true when the field is valid
     */
    function validateField(name) {
        var $input = $("#" + name);
        if ($input.length === 0) { return true; }
        var message = rules[name]($input.val());
        var $error = $input.nextAll(".error").first();
        if (message) {
            if ($error.length === 0) {
                $error = $('<span class="error"></span>').insertAfter($input);
            }
            $error.text(message);
            $input.addClass("invalid");
            return false;
        }
        $error.text("");
        $input.removeClass("invalid");
        return true;
    }

    $(function () {
        var $form = $("#person-form");
        if ($form.length === 0) { return; }

        // Validate on blur and as the user corrects a previously-invalid field.
        $.each(rules, function (name) {
            $("#" + name).on("blur", function () {
                validateField(name);
            }).on("input", function () {
                if ($(this).hasClass("invalid")) {
                    validateField(name);
                }
            });
        });

        // Block submission if anything is invalid; focus the first offending field.
        $form.on("submit", function (e) {
            var firstInvalid = null;
            $.each(rules, function (name) {
                if (!validateField(name) && firstInvalid === null) {
                    firstInvalid = name;
                }
            });
            if (firstInvalid !== null) {
                e.preventDefault();
                $("#" + firstInvalid).focus();
            }
        });
    });
})(jQuery);
