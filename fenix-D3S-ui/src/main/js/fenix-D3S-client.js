var D3SC = (function() {

    var CONFIG = {
        placeholderID   :   null,
        lang            :   null,
        lang_ISO2       :   null,
        theme           :   'faostat',
        datasource      :   'faostat2',
        I18N_prefix     :   '',
        msd             :   null,
        msd_url         :   'config/msd.json',
        snippets        :   null,
        snippets_url    :   'config/snippets.html',
        accordion_url   :   'http://faostat3.fao.org/wds/rest/groupsanddomains'
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

        /* Add structure. */
        $('#' + D3SC.CONFIG.placeholderID).append($(D3SC.CONFIG.snippets).filter('#structure').html());
        document.getElementById('submit_button').innerHTML = $.i18n.prop('_submit_changes');

        /* Build accordion. */
        buildAccordion();

//        for (var i = 1 ; i < 15 ; i++) {
//            var tmp = $(D3SC.CONFIG.snippets).filter('#radio_element').html();
//            tmp = tmp.replace(new RegExp('radio_id_', 'gm'), ('radio_id_' + i));
//            tmp = tmp.replace(new RegExp('radio_title_', 'gm'), ('radio_title_' + i));
//            $('#radio_buttons_1').append(tmp);
//        }
//
//        for (var i = 1 ; i < 15 ; i++)
//            document.getElementById('radio_title_' + i).innerHTML = 'Option ' + i;

    };

    function buildAccordion() {

        /* Add accordion. */
        $('#accordion_area').append($(D3SC.CONFIG.snippets).filter('#accordion_structure').html());

        /* Fetch groups and domains from the DB. */
        $.ajax({

            type        :   'GET',
            dataType    :   'json',
            url         :   D3SC.CONFIG.accordion_url + '/' + D3SC.CONFIG.datasource + '/' + D3SC.CONFIG.lang,

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
                var tmp = $(D3SC.CONFIG.snippets).filter('#radio_element').html();
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
            }
        });

    };

    function addAccordionElement(code, label) {
        var s = $(D3SC.CONFIG.snippets).filter('#accordion_element_structure').html();
        s = s.replace(new RegExp('collapse_', 'gm'), ('collapse_' + code));
        s = s.replace(new RegExp('accordion_element_title_', 'gm'), ('accordion_element_title_' + code));
        s = s.replace(new RegExp('radio_buttons_', 'gm'), ('radio_buttons_' + code));
        $('#accordion').append(s);
        document.getElementById('accordion_element_title_' + code).innerHTML = label;
    }

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

            error : function(err, b, c) {

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