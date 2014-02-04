var D3SC = (function() {

    var CONFIG = {
        placeholderID   :   null,
        lang            :   null,
        lang_ISO2       :   null,
        theme           :   'faostat',
        datasource      :   'faostatproddiss',
        I18N_prefix     :   '',
        msd             :   null,
        msd_url         :   'config/msd.json',
        snippets        :   null,
        snippets_url    :   'config/snippets.html'
    };

    function init(config) {

        /* Store user preferences. */
        D3SC.CONFIG = $.extend(D3SC.CONFIG, config);

        /* Set ISO2 language code. */
        switch (D3SC.CONFIG.lang) {
            case 'F': D3SC.CONFIG.lang_ISO2 = 'FR'; break;
            case 'S': D3SC.CONFIG.lang_ISO2 = 'ES'; break;
            default : D3SC.CONFIG.lang_ISO2 = 'EN'; break;
        }

        /* Initiate multi-language. */
        $.i18n.properties({
            name        :   'I18N',
            mode        :   'both',
            path        :   D3SC.CONFIG.I18N_prefix + 'I18N/',
            language    :   D3SC.CONFIG.lang_ISO2
        });

        /* Load configuration files. */
        loadMSD();
        loadSnippets(buildUI);


    };

    function buildUI() {

        /* Add header. */
        $('#' + D3SC.CONFIG.placeholderID).append($(D3SC.CONFIG.snippets).filter('#header').html());

    };

    function loadMSD() {

        /* Load the configuration file. */
        $.ajax({

            type        :   'GET',
            dataType    :   'json',
            url         :   D3SC.CONFIG.msd_url,

            /* Load data from the DB */
            success : function(response) {

                /* Convert the response in JSON, if needed */
                var msd = response;
                if (typeof msd == 'string')
                    msd = $.parseJSON(response);
                D3SC.CONFIG.msd = msd;

            },

            /* Use test data */
            error : function(err, b, c) {
                console.log(err + ', ' + b + ', ' + c);
            }

        });

    };

    function loadSnippets(callback) {

        /* Load the configuration file. */
        $.ajax({

            type        :   'GET',
            dataType    :   'text',
            url         :   D3SC.CONFIG.snippets_url,

            /* Load data from the DB */
            success : function(response) {

                /* Convert the response in JSON, if needed */
                D3SC.CONFIG.snippets = response;
                callback();

            },

            /* Use test data */
            error : function(err, b, c) {
                console.log(err + ', ' + b + ', ' + c);
            }

        });

    };

    return {
        init    :   init,
        CONFIG  :   CONFIG
    };

})();