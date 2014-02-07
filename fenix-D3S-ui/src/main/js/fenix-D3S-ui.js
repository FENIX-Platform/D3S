var D3SC = (function() {

    var CONFIG = {
        placeholderID   :   null,
        domainCode      :   null,
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
        data            :   null,
        codelists_url   :   'http://fenixapps.fao.org/d3sp/service/msd/cl/system',
        contacts_url    :   'http://fenixapps.fao.org/d3sp/service/msd/cm/contact/byFields?context=FAOSTAT'
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
        loadSnippets(buildFieldsArea);

    };

    function buildFieldsArea() {

        /* Fetch data from DB.*/
        $.ajax({

            type        :   'GET',
            dataType    :   'json',
            url         :   D3SC.CONFIG.data_url + '/' + D3SC.CONFIG.uid_prefix + D3SC.CONFIG.domainCode,

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
        $('#' + D3SC.CONFIG.placeholderID).empty();
        $('#' + D3SC.CONFIG.placeholderID).append($(D3SC.CONFIG.snippets).filter('#tab_structure').html());

        /* Create tab headers. */
        $.each(D3SC.CONFIG.msd, function(k, v) {
            var s = '';
            s += '<li><a href="#' + k + '" data-toggle="tab">' + v[D3SC.CONFIG.lang + '_LABEL'] + '</a></li>';
            $('#tab').append(s);
        });

        /* Initiate tab contents. */
        $('#tab').after('<div class="tab-content" id="tab_content"></div>');
        $.each(D3SC.CONFIG.msd, function(k, v) {
            $('#tab_content').append('<div class="tab-pane fade active" id="' + k + '"></div>');
        });

        /* Build tabs. */
        $.each(D3SC.CONFIG.msd, function(k, v) {
            buildTab(k, v);
        });

        /* Show first tab. */
        $('#tab a:first').tab('show');

    };

    function buildTab(tabID, tabContent) {
        $.each(tabContent, function(k, v) {
            switch (v.TYPE) {
                case 'STRING'           :   buildString(tabID, k, v);           break;
                case 'SINGLECHOICE'     :   buildSingleChoice(tabID, k, v);     break;
                case 'MULTIPLECHOICE'   :   buildMultipleChoice(tabID, k, v);   break;
                case 'CONTACT'          :   buildContact(tabID, k, v);     break;
                case 'CONTACTLIST'      :   buildContactList(tabID, k, v);   break;
                case 'DATE'             :   buildDate(tabID, k, v);             break;
                case 'TEXTAREA'         :   buildTextArea(tabID, k, v);         break;
                case 'NESTED'           :   buildNested(tabID, k, v);           break;
            }
        });
    };

    function buildNested(tabID, id, definition) {
        buildNestedFieldBox(tabID, id, definition, 'nested_structure');
        CKEDITOR.replace(id + '_nested_description', {toolbar: 'FAOSTAT'});
        $('#' + id + '_nested_date').jqxDateTimeInput({height: '33px'});
    };

    function buildTextArea(tabID, id, definition) {
        buildFieldBox(tabID, id, definition, 'date_structure');
        CKEDITOR.replace(id + '_content', {toolbar: 'FAOSTAT'});
        CKEDITOR.timestamp = (new Date()).toString() ;
        CKEDITOR.instances[id + '_content'].setData(D3SC.CONFIG.data[id][D3SC.CONFIG.lang_ISO2]);
    };

    function buildDate(tabID, id, definition) {
        buildFieldBox(tabID, id, definition, 'date_structure');
        $('#' + id + '_content').jqxDateTimeInput({height: '33px'});
        if (id.indexOf('From') > -1) {
            $('#' + id + '_content').jqxDateTimeInput('setDate', new Date(D3SC.CONFIG.data[id.substring(0, id.indexOf('From'))]['from']));
        } else if (id.indexOf('To') > -1) {
            $('#' + id + '_content').jqxDateTimeInput('setDate', new Date(D3SC.CONFIG.data[id.substring(0, id.indexOf('To'))]['to']));
        } else {
            $('#' + id + '_content').jqxDateTimeInput('setDate', new Date(D3SC.CONFIG.data[id]));
        }
    };

    function buildSingleChoice(tabID, id, definition) {

        buildFieldBox(tabID, id, definition, 'singlechoice_structure');
        $('#' + id + '_content').chosen({disable_search_threshold: 10});

        $.ajax({

            type: 'GET',
            url: D3SC.CONFIG.codelists_url + '/' + D3SC.CONFIG.msd[tabID][id].REST,
            dataType: 'json',

            success : function(response) {

                /* Convert the response in JSON, if needed */
                var json = response;
                if (typeof json == 'string')
                    json = $.parseJSON(response);

                /* Add options to the list. */
                for (var i = 0 ; i < json.rootCodes.length ; i++) {
                    var s = '<option value="' + json.rootCodes[i].code + '">' + json.rootCodes[i].title[D3SC.CONFIG.lang_ISO2] + '</option>';
                    $('#' + id + '_content').append(s);
                }

                /* Update chosen. */
                $('#' + id + '_content').trigger('chosen:updated');

            },

            error : function(err, b, c) {

            }

        });

    };

    function buildMultipleChoice(tabID, id, definition) {

        buildFieldBox(tabID, id, definition, 'multiplechoice_structure');
        $('#' + id + '_content').chosen({disable_search_threshold: 10});

        $.ajax({

            type: 'GET',
            url: D3SC.CONFIG.codelists_url + '/' + D3SC.CONFIG.msd[tabID][id].REST,
            dataType: 'json',

            success : function(response) {

                /* Convert the response in JSON, if needed */
                var json = response;
                if (typeof json == 'string')
                    json = $.parseJSON(response);

                if (json.rootCodes != null) {

                    /* Add options to the list. */
                    for (var i = 0 ; i < json.rootCodes.length ; i++) {
                        var s = '<option value="' + json.rootCodes[i].code + '">' + json.rootCodes[i].title[D3SC.CONFIG.lang_ISO2] + '</option>';
                        $('#' + id + '_content').append(s);
                    }

                } else {

                    for (var i = 0 ; i < json.length ; i++) {
                        var s = '<option value="' + json[i].system + '">' + json[i].title[D3SC.CONFIG.lang_ISO2] + '</option>';
                        $('#' + id + '_content').append(s);
                    }

                }

                /* Update chosen. */
                $('#' + id + '_content').trigger('chosen:updated');

            },

            error : function(err, b, c) {

            }

        });

    };

    function buildContact(tabID, id, definition) {
        buildFieldBox(tabID, id, definition, 'singlechoice_structure');
        $('#' + id + '_content').chosen({disable_search_threshold: 10});
    };

    function buildContactList(tabID, id, definition) {
        buildFieldBox(tabID, id, definition, 'multiplechoice_structure');
        $('#' + id + '_content').chosen({disable_search_threshold: 10});
    };

    function buildString(tabID, id, definition) {
        buildFieldBox(tabID, id, definition, 'string_structure');
        $('#' + id + '_help').attr('title', definition[D3SC.CONFIG.lang + '_DESCRIPTION']);
        $('#' + id + '_content').val(D3SC.CONFIG.data[id][D3SC.CONFIG.lang_ISO2]);
    };

    function buildFieldBox(tabID, id, definition, snippetID) {
        var s = $(D3SC.CONFIG.snippets).filter('#' + snippetID).html();
        s = s.replace('_title', id + '_title');
        s = s.replace('_help', id + '_help');
        s = s.replace('_content', id + '_content');
        $('#' + tabID).append(s);
        document.getElementById(id + '_title').innerHTML = definition[D3SC.CONFIG.lang + '_LABEL'];
        $('#' + id + '_help').attr('title', definition[D3SC.CONFIG.lang + '_DESCRIPTION']);
    };

    function buildNestedFieldBox(tabID, id, definition, snippetID) {
        var s = $(D3SC.CONFIG.snippets).filter('#' + snippetID).html();
        s = s.replace('_container_0', id + '_container_0');
        s = s.replace('_title', id + '_title');
        s = s.replace('_nested_label_title', id + '_nested_label_title');
        s = s.replace('_nested_label_description', id + '_nested_label_description');
        s = s.replace('_nested_label_date', id + '_nested_label_date');
        s = s.replace('_nested_label_link', id + '_nested_label_link');
        s = s.replace('_nested_title', id + '_nested_title');
        s = s.replace('_nested_description', id + '_nested_description');
        s = s.replace('_nested_date', id + '_nested_date');
        s = s.replace('_nested_link', id + '_nested_link');
        s = s.replace('_help_title', id + '_help_title');
        s = s.replace('_help_description', id + '_help_description');
        s = s.replace('_help_date', id + '_help_date');
        s = s.replace('_help_link', id + '_help_link');
        $('#' + tabID).append(s);
        document.getElementById(id + '_title').innerHTML = definition[D3SC.CONFIG.lang + '_LABEL'];
        document.getElementById(id + '_nested_label_title').innerHTML = $.i18n.prop('_title');
        document.getElementById(id + '_nested_label_description').innerHTML = $.i18n.prop('_description');
        document.getElementById(id + '_nested_label_date').innerHTML = $.i18n.prop('_date');
        document.getElementById(id + '_nested_label_link').innerHTML = $.i18n.prop('_link');
        $('#' + id + '_help_title').attr('title', definition.FIELDS.title[D3SC.CONFIG.lang + '_DESCRIPTION']);
        if (definition.FIELDS.date != null)
            $('#' + id + '_help_date').attr('title', definition.FIELDS.date[D3SC.CONFIG.lang + '_DESCRIPTION']);
        if (definition.FIELDS.link != null)
            $('#' + id + '_help_link').attr('title', definition.FIELDS.link[D3SC.CONFIG.lang + '_DESCRIPTION']);
        if (definition.FIELDS.description != null)
            $('#' + id + '_help_description').attr('title', definition.FIELDS.description[D3SC.CONFIG.lang + '_DESCRIPTION']);
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
        CONFIG          :   CONFIG
    };

})();