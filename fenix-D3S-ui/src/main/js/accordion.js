var ACCORDION = (function() {

    var CONFIG = {
        placeholderID   :   null,
        lang            :   null,
        lang_ISO2       :   null,
        accordion_url   :   'http://faostat3.fao.org/wds/rest/groupsanddomains',
        datasource      :   'faostat2',
        I18N_prefix     :   '',
        snippets        :   null,
        snippets_url    :   'config/accordion_snippets.html'
    };

    function init(config) {

        /* Store user preferences. */
        ACCORDION.CONFIG = $.extend(ACCORDION.CONFIG, config);

        /* Set ISO2 language code. */
        switch (ACCORDION.CONFIG.lang) {
            case 'F': ACCORDION.CONFIG.lang_ISO2 = 'FR'; break;
            case 'S': ACCORDION.CONFIG.lang_ISO2 = 'ES'; break;
            default : ACCORDION.CONFIG.lang_ISO2 = 'EN'; break;
        }

        /* Initiate multi-language. */
        $.i18n.properties({
            name        :   'I18N',
            mode        :   'both',
            path        :   ACCORDION.CONFIG.I18N_prefix + 'I18N/',
            language    :   ACCORDION.CONFIG.lang_ISO2
        });

        /* Load configuration files. */
        loadSnippets(buildAccordion);

    };

    function loadSnippets(callback) {

        /* Load the configuration file. */
        $.ajax({

            type        :   'GET',
            dataType    :   'text',
            url         :   ACCORDION.CONFIG.snippets_url,

            /* Load data from the DB */
            success : function(response) {

                /* Convert the response in JSON, if needed */
                ACCORDION.CONFIG.snippets = response;
                callback();

            },

            /* Use test data */
            error : function(err, b, c) {
                console.log(err + ', ' + b + ', ' + c);
            }

        });

    };

    function buildAccordion() {

        /* Add structure. */
        $('#' + ACCORDION.CONFIG.placeholderID).append($(ACCORDION.CONFIG.snippets).filter('#structure').html());
        document.getElementById('submit_button').innerHTML = $.i18n.prop('_submit_changes');

        /* Add accordion. */
        $('#accordion_area').append($(ACCORDION.CONFIG.snippets).filter('#accordion_structure').html());

        /* Fetch groups and domains from the DB. */
        $.ajax({

            type        :   'GET',
            dataType    :   'json',
            url         :   ACCORDION.CONFIG.accordion_url + '/' + ACCORDION.CONFIG.datasource + '/' + ACCORDION.CONFIG.lang,

            success : function(response) {

                /* Convert the response in JSON, if needed */
                var json = response;
                if (typeof json == 'string')
                    json = $.parseJSON(response);

                /* Collect group codes and labels. */
                var groups = [];
                var labels = [];
                var tree = {};
                var treeLabels = {};
                for (var i = 0 ; i < json.length ; i++) {
                    if ($.inArray(json[i][0], groups) < 0) {
                        groups.push(json[i][0]);
                        labels.push(json[i][1]);
                        tree[json[i][0]] = [];
                        treeLabels[json[i][0]] = [];
                    }
                    tree[json[i][0]].push(json[i][2]);
                    treeLabels[json[i][0]].push(json[i][3]);
                }

                /* Populate the accordion. */
                for (var i = 0 ; i < groups.length ; i++)
                    addAccordionElement(groups[i], labels[i]);

                /* Populate the groups. */
                populateAccordionGroups(tree, treeLabels);

            },

            error : function(err, b, c) {

            }

        });

    };

    function populateAccordionGroups(tree, treeLabels) {

        /* Create the radio buttons. */
        $.each(tree, function(k, v) {
            for (var i = 0 ; i < v.length ; i++) {
                var tmp = $(ACCORDION.CONFIG.snippets).filter('#radio_element').html();
                tmp = tmp.replace(new RegExp('radio_id_', 'gm'), ('radio_id_' + v[i]));
                tmp = tmp.replace(new RegExp('radio_title_', 'gm'), ('radio_title_' + v[i]));
                $('#radio_buttons_' + k).append(tmp);
            }
        });

        /* Add labels to the radio buttons. */
        $.each(tree, function(k, v) {
            for (var i = 0 ; i < v.length ; i++) {
                document.getElementById('radio_title_' + v[i]).innerHTML = treeLabels[k][i];
                $('#radio_id_' + v[i]).attr('value', v[i]);
                $('#radio_id_' + v[i]).bind('click', function(e) {
                    var id = e.target.id.substring('radio_id_'.length);
                    D3SC.init({"placeholderID" : "fields_area", "lang" : "E", "domainCode" : id});
                });
            }
        });

    };

    function addAccordionElement(code, label) {
        var s = $(ACCORDION.CONFIG.snippets).filter('#accordion_element_structure').html();
        s = s.replace(new RegExp('collapse_', 'gm'), ('collapse_' + code));
        s = s.replace(new RegExp('accordion_element_title_', 'gm'), ('accordion_element_title_' + code));
        s = s.replace(new RegExp('radio_buttons_', 'gm'), ('radio_buttons_' + code));
        $('#accordion').append(s);
        document.getElementById('accordion_element_title_' + code).innerHTML = label;
    };

    return {
        CONFIG          :   CONFIG,
        init            :   init
    };

})();