/*
 * Progressive enhancement for the person listing page: submits the page-size
 * form as soon as the selection changes. The Apply button is hidden because
 * it is only needed when JavaScript is unavailable.
 */
(function ($) {
    'use strict';

    $(function () {
        var $form = $('.page-size-form');
        if (!$form.length) {
            return;
        }
        $form.find('.page-size-apply').hide();
        $form.find('#page-size').on('change', function () {
            $form.trigger('submit');
        });
    });
})(jQuery);
