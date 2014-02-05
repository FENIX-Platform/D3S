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
        accordion_url   :   'http://faostat3.fao.org/wds/rest/groupsanddomains',
        data_url        :   'http://fenixapps.fao.org/d3sp/service/msd/dm',
        uid_prefix      :   'FAOSTAT_',
        data            :   null
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

    };

    function buildFieldsArea(domainCode) {

        /* Fetch data from DB.*/
        $.ajax({

            type        :   'GET',
            dataType    :   'json',
            url         :   D3SC.CONFIG.data_url + '/' + D3SC.CONFIG.uid_prefix + domainCode,

            success : function(response) {

                /* Convert the response in JSON, if needed */
                var json = response;
                if (typeof json == 'string')
                    json = $.parseJSON(response);

                /* Store the result. */
                D3SC.CONFIG.data = json;

                /* Build tabs. */
                buildTabs();

            },

            error : function(err, b, c) {

            }

        });

    };

    function buildTabs() {

        /* Append tab structure. */
        $('#fields_area').append($(D3SC.CONFIG.snippets).filter('#tab_structure').html());

        /* Create tab headers. */
        $.each(D3SC.CONFIG.msd, function(k, v) {
            var s = '';
            s += '<li><a href="#' + k + '" data-toggle="tab">' + v[D3SC.CONFIG.lang + '_LABEL'] + '</a></li>';
            $('#tab').append(s);
        });

        /* Initiate tab contents. */
        $('#tab').after('<div class="tab-content" id="tab_content"></div>');
        $.each(D3SC.CONFIG.msd, function(k, v) {
            $('#tab_content').append('<div class="tab-pane fade active" id="' + k + '">' + k + '</div>');
        });


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
                $('#radio_id_' + v[i]).bind('click', function(e) {
                    var id = e.target.id.substring('radio_id_'.length);
                    D3SC.buildFieldsArea(id);
                });
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
        init            :   init,
        buildFieldsArea :   buildFieldsArea,
        CONFIG          :   CONFIG
    };

})();