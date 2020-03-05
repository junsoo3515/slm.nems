/**
 * 기본 requireJS config
 * jquery, bootstrap, darkhand, common, formcheck, mustache : 기본 사용
 * idleTimeout, idleTimer : 세션 Live 상태 체크 Plugin
 * jqGrid : jqGrid 플러그인 사용
 */
//requireJS 기본 설정 부분
var inDevelopment = true, version = "1.0.0", resBaseUrl = document.querySelector('script[data-root]').getAttribute('data-root');

requirejs.config({
    baseUrl: resBaseUrl + "/plugins/",
    map: {
        '*': {
            'css': 'require-css/css.min'
        }
    },
    paths: {
        // javascript 로딩 시 필요한 경로를 지정한다.
        'jquery': 'jquery-2.1.3/jquery-2.1.3.min',

        'bootstrap' : resBaseUrl + '/assets/plugins/bootstrap/js/bootstrap.min',

        'bootstrap-switchery': resBaseUrl + '/assets/plugins/switchery/switchery.min',

        'lightbox': resBaseUrl + '/assets/plugins/lightbox/js/lightbox-2.6.min',

        'bootstrap-datepicker': resBaseUrl + '/assets/plugins/bootstrap-datepicker/js/bootstrap-datepicker',
        'bootstrap-datepicker.lang': resBaseUrl + '/assets/plugins/bootstrap-datepicker/js/locales/bootstrap-datepicker.kr',
        'bootstrap-timepicker': resBaseUrl + '/assets/plugins/bootstrap-timepicker/js/bootstrap-timepicker.min',

        'moment' : 'moment/moment-with-locales.min',
        'bootstrap-datetimepicker' : resBaseUrl + '/assets/plugins/bootstrap-eonasdan-datetimepicker/src/js/bootstrap-datetimepicker',

        'darkhand' : 'darkhand-0.1.2/js/darkhand-1.0.2',
        'common' : 'darkhand-0.1.2/js/common-1.0.2',
        'formcheck' : 'darkhand-0.1.2/js/formcheck-1.0.4',

        'jqGrid.locale' : 'jqGrid-5.0.0/js/i18n/grid.locale-kr',
        'jqGrid.setting' : 'darkhand-0.1.2/js/jqGrid.setting-1.0.0',
        'jqGrid': 'jqGrid-5.0.0/js/jquery.jqGrid',

        'jquery-ui' : 'jquery-ui-1.11.4/js/jquery-ui.min',
        'jquery.form': 'jquery-2.1.3/plugin/jquery.form.3.40.0',

        'jquery.gritter': resBaseUrl + '/assets/plugins/gritter/js/jquery.gritter.min',

        'select2': resBaseUrl + '/assets/plugins/select2/dist/js/select2.min',
        'select2.lang': resBaseUrl + '/assets/plugins/select2/dist/js/i18n/ko',

        'masked-input': resBaseUrl + '/assets/plugins/masked-input/masked-input.min',

        'highcharts-module' : 'Highcharts-4.1.4/js/highcharts',
        'highcharts' : 'Highcharts-4.1.4/js/highcharts-more',

        'jquery.ui.widget' : 'jquery-file-upload-v9.9.3/js/vendor/jquery.ui.widget',
        'tmpl' : 'jquery-file-upload-v9.9.3/js/etc/tmpl.min',
        'load-image' : 'jquery-file-upload-v9.9.3/js/etc/load-image',
        'load-image-meta':'jquery-file-upload-v9.9.3/js/etc/load-image-meta',
        'load-image-ios':'jquery-file-upload-v9.9.3/js/etc/load-image-ios',
        'load-image-exif':'jquery-file-upload-v9.9.3/js/etc/load-image-exif',
        'canvas-to-blob' : 'jquery-file-upload-v9.9.3/js/etc/canvas-to-blob.min',
        'jquery.blueimp-gallery' : 'blueimp-gallery-2.15.2/js/jquery.blueimp-gallery',
        'jquery.fileupload' : 'jquery-file-upload-v9.9.3/js/jquery.fileupload',
        'jquery.iframe-transport' : 'darkhand-0.1.2/js/jquery.iframe-transport.cmlenz',
        'jquery.fileupload-ui' : 'jquery-file-upload-v9.9.3/js/jquery.fileupload-ui',
        'jquery.fileupload-process' : 'jquery-file-upload-v9.9.3/js/jquery.fileupload-process',
        'jquery.fileupload-image' : 'jquery-file-upload-v9.9.3/js/jquery.fileupload-image',
        'jquery.fileupload-audio' : 'jquery-file-upload-v9.9.3/js/jquery.fileupload-audio',
        'jquery.fileupload-video' : 'jquery-file-upload-v9.9.3/js/jquery.fileupload-video',
        'jquery.fileupload-validate' : 'jquery-file-upload-v9.9.3/js/jquery.fileupload-validate'
    },
    shim: {
        'bootstrap' : {
            deps : ['jquery'],
            exports: 'jQuery'
        },
        "bootstrap-datepicker": {
            deps: ["bootstrap", "jquery"],
            exports: "$.fn.datetimepicker"
        },
        'bootstrap-datepicker.lang': {
            deps: ['jquery', 'bootstrap-datepicker'],
            exports: "$.fn.datepicker.dates.kr"
        },
        "bootstrap-timepicker": {
            deps: ["jquery"],
            exports: "$.fn.timepicker"
        },
        'moment': {
            deps: ['jquery'],
            exports: 'moment'
        },
        'bootstrap-datetimepicker' : {
            deps: ['moment', 'jquery', 'bootstrap'],
            exports: 'datetimepicker'
        },
        'bootstrap-switchery' : {
            deps: [ 'jquery'],
            exports: 'Switchery'
        },
        'jqGrid' : {
            deps : ['jquery', 'jqGrid.locale', 'jqGrid.setting'],
            exports: 'jQuery',
            init: function (jQuery) {

                jQuery.jgrid.no_legacy_api = true;
                jQuery.jgrid.useJSON = true;

                return jQuery;
            }
        },
        'jquery.form' : {
            deps : ['jquery'],
            exports: 'jQuery'
        },
        'jquery.gritter': {
            deps: ['jquery'],
            exports: 'jQuery'
        },
        'select2': {
            deps: ['jquery'],
            exports: 'jQuery'
        },
        'select2.lang': {
            deps: ['jquery', 'select2'],
            exports: "$.fn.select2",
            init: function (jQuery) {

                jQuery.fn.select2.defaults.set('language', 'ko');

                return jQuery.fn.select2;
            }
        },
        'masked-input': {
            deps: ['jquery'],
            exports: 'jQuery'
        },
        'highcharts-module' : {
            'deps': ['jquery'],
            'exports': 'Highcharts'
        },
        'highcharts': {
            'deps': ['highcharts-module'],
            'exports': 'Highcharts',
            init: function(Highcharts) {

                Highcharts.setOptions({
                    credits: {
                        enabled: false,
                        text: '',
                        href: ''
                    },
                    lang: {
                        numericSymbols: null, // 숫자 단위에 따라서 ['k', 'M', 'G', 'T', 'P', 'E'] axis Label 표시 할 떄 사용하는 단위 조절 옵션 사용 안함
                        thousandsSep: ','
                    }
                });

                return Highcharts;
            }
        }
    },
    urlArgs: "v=" + ((inDevelopment) ? (new Date()).getTime() : version) // Cache
});