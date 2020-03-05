/*--------------------------------------------------
 기능   : jQuery 및 plugin 추가적인 Script
 HISTORY:
 - ver1.0.0 : jQuery 의 serializeObject function 추가
 - ver1.0.1 :
 - bootStrap의 alert 창 확장 plugin 추가
 - 로그아웃 함수 추가
 - idle.Timeout 타이머 작동 추가
 - url의 Entity 수집

 RETURN : null
 ----------------------------------------------------*/

/*global jQuery, define */
(function( factory ) {
    "use strict";
    if ( typeof define === "function" && define.amd ) {
        // AMD. Register as an anonymous module.
        define([
            "jquery",
            "bootstrap",
            //"idleTimeout"
        ], factory );
    } else {
        // Browser globals
        //factory( jQuery, bootstrap, idleTimeout );
        factory(jQuery, bootstrap);
    }
    //}(function( $, bootstrap, idleTimeout ) {
}(function ($, bootstrap) {

    'use strict';
    // ROOT 경로
    $.fn.preUrl = '/slm';
    $.fn.sysUrl = '/slm';

    // 객체에 포함되어진 name을 가진 인자들 JSON 타입 변환
    $.fn.serializeObject = function () {
        var o = {};
        var a = this.serializeArray();
        $.each(a, function () {
            if (o[this.name]) {
                if (!o[this.name].push) {
                    o[this.name] = [o[this.name]];
                }
                o[this.name].push(this.value || '');
            } else {
                o[this.name] = this.value || '';
            }
        });

        return o;
    };

    $.fn.loadingStart = function () {
        // 로딩 완료 후 활성 화
        $.when($('#content-page-loader').removeClass('hide')).done(function () {
            $('#content').removeClass('in');
        });
    }

    $.fn.loadingComplete = function () {
        // 로딩 완료 후 활성 화
        $.when($('#content-page-loader').addClass('hide')).done(function () {
            $('#content').addClass('in');
        });
    }

    /*--------------------------------------------------
     기능   : bootstrap.notify
     INPUT  :
     - element : 타겟
     - options : 옵션 정보
     RETURN : null
     ----------------------------------------------------*/
    var Notification = function (element, options) {
        // Element collection
        this.$element = $(element);
        this.options = $.extend(true, {}, $.fn.notify.defaults, options);
        this.$note = (this.options.tabID ? element : $('<div class="alert"></div>'));

        // Setup from options
        if (this.options.transition) {
            if (this.options.transition == 'fade') {
                this.$note.addClass('in').addClass(this.options.transition);
            } else {
                this.$note.addClass(this.options.transition);
            }
        } else {
            this.$note.addClass('fade').addClass('in');
        }

        if (this.options.type) {
            this.$note.addClass('alert-' + this.options.type);
        } else {
            this.$note.addClass('alert-success');
        }

        if (!this.options.message && this.$element.data("message") !== '') {
            // dom text
            this.$note.html(this.$element.data("message"));
        } else {
            if (typeof this.options.message === 'object') {
                if (this.options.message.html) {
                    this.$note.html(this.options.message.html);
                } else if (this.options.message.text) {
                    this.$note.text(this.options.message.text);
                } else {
                    this.$note.html(this.options.message);
                }
            }
        }

        if (this.options.closable) {
            var link = $('<a class="close pull-right" href="#">&times;</a>');

            $(link).on('click', $.proxy(onClose, this));

            this.$note.prepend(link);
        }

        return this;
    };

    var onClose = function () {
        this.options.onClose();
        $(this.$note).remove();
        this.options.onClosed();
    };

    Notification.prototype.show = function () {
        if (this.options.fadeOut.enabled) {
            this.$note.delay(this.options.fadeOut.delay || 3000).fadeOut('slow', $.proxy(onClose, this));
        }

        if (!this.options.tabID) {
            this.$element.append(this.$note);
        }

        this.$note.alert();
    };

    Notification.prototype.hide = function () {
        if (this.options.fadeOut.enabled) {
            this.$note.delay(this.options.fadeOut.delay || 3000).fadeOut('slow', $.proxy(onClose, this));
        } else {
            onClose.call(this);
        }
    };

    $.fn.notify = function (options) {
        return new Notification(this, options);
    };

    $.fn.notify.defaults = {
        type: 'success',
        tabID: null,
        closable: true,
        transition: 'fade',
        fadeOut: {
            enabled: true,
            delay: 3000
        },
        message: null,
        onClose: function () { },
        onClosed: function () { }
    };

    // 로그아웃 기능
    $.fn.logOut = function(token) {

        // TODO 개발 시 클러스터링 지원을 위해 사용
        if ( token === undefined) {
            token = localStorage["token"];
        }

        $.ajax({
            url: $.fn.sysUrl + '/login/invalidate',
            type: "GET",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function (data, status) {

                if (data.invalidated === true) {

                    localStorage.clear();
                }

                location.href = $.fn.preUrl;
            },
            error: function (request, status) {
                console.log("ajax - error");
            }
        });
    }

    // idle.timeout-1.2 타이머 작동
    //$.fn.enableTimeout = function() {
    //    $(function () {
    //        $.idleTimeout('#idletimeout', '#idletimeout a', {
    //            idleAfter: parseInt(localStorage["expireIn"] ? localStorage["expireIn"] : 10, 10), // 60 : 1 minutes
    //            pollingInterval: 30, // 30 sec
    //            // TODO 개발 시 클러스터링 지원을 위해 사용
    //            keepAliveURL: '/auth/session/get/' + localStorage["token"],
    //            // TODO JEUS Session 클러스터링 지원 시
    //            //keepAliveURL: $.fn.sysUrl + '/login/live/' + localStorage["token"],
    //            serverResponseEquals: false,
    //            warningLength: 60, // give the user 60 seconds to respond
    //            onTimeout: function () {
    //                // redirect to login if the user takes no action
    //                $(this).slideUp();
    //                $.fn.logOut();
    //            },
    //            onIdle: function () {
    //                $(this).slideDown(); // show the warning bar
    //            },
    //            onCountdown: function (counter) {
    //                $(this).find("span").html(counter); // update the counter
    //            },
    //            onResume: function () {
    //                $(this).slideUp(); // hide the warning bar
    //            },
    //            onAbort: function () {
    //                $.fn.logOut();
    //            }
    //        });
    //    });
    //}

    /*-----------------------------------------------------------------------------
     기능 : URL Element 추출
     INPUT  :
     - url : 해당 URL
     예제 :
     var myURL = parseURL('http://abc.com:8080/dir/index.html?id=255&m=hello#top');

     myURL.source   = 'http://abc.com:8080/dir/index.html?id=255&m=hello#top'
     myURL.protocol = 'http'
     myURL.host     = 'abc.com'
     myURL.port     = '8080'
     myURL.query    = '?id=255&m=hello'
     myURL.params   = Object = { id: 255, m: hello }
     myURL.file     = 'index.html'
     myURL.hash     = 'top'
     myURL.path     = '/dir/index.html'
     myURL.relative = '/dir/index.html?id=255&m=hello#top'
     myURL.segments = ['dir', 'index.html']
     -----------------------------------------------------------------------------*/
    $.fn.parseURL = function(url) {

        if ( url === undefined ) {
            url = document.referrer; // 이전 URL 정보
        }

        if ( url === '' ) {
            url = document.location.href;
        }

        var a =  document.createElement('a');

        a.href = url;

        return {
            source: url,
            protocol: a.protocol.replace(':',''),
            host: a.hostname,
            port: a.port,
            query: a.search,
            params: (function(){
                var ret = {},
                    seg = a.search.replace(/^\?/,'').split('&'),
                    len = seg.length, i = 0, s;
                for (;i<len;i++) {
                    if (!seg[i]) { continue; }
                    s = seg[i].split('=');
                    ret[s[0]] = s[1];
                }
                return ret;
            })(),
            file: (a.pathname.match(/\/([^\/?#]+)$/i) || [,''])[1],
            hash: a.hash.replace('#',''),
            path: a.pathname.replace(/^([^\/])/,'/$1'),
            relative: (a.href.match(/tps?:\/\/[^\/]+(.+)/) || [,''])[1],
            segments: a.pathname.replace(/^\//,'').split('/')
        };
    }

    /*--------------------------------------------------
     기능   : 접근 URL로 로그인 페이지 유무 판단하여 페이지 강제 변경
     OUTPUT : JSON
     - isState : 로그인 페이지 Check 유/무
     - retURL : 로그인 페이지 접근 시 변경 되는 접근 URL
     RETURN : null
     ----------------------------------------------------*/
    $.fn.accessURL = function() {
        var isState = false;
        var arrURL = [$.fn.sysUrl + '/login/view', $.fn.sysUrl + '/login'];
        var retURL = $.fn.parseURL().relative;

        for ( var i = 0; i < arrURL.length; i++ ) {
            if ( retURL === arrURL[i] ) {
                isState = true;
                retURL = $.fn.preUrl;
                break;
            }
        }

        return {isState : isState, retURL : retURL};
    }

    /*--------------------------------------------------
     기능   : 버튼 스타일 적용
     INPUT  :
     - obj : {ele :버튼 객체, isShow : visible 상태, txt : 버튼 명칭}...
     예제 :
     $.fn.setBtnStyle([
     { ele : jQuery('#btnReg'), isShow : true, txt : '등록'},
     { ele : jQuery('#btnCancle'), isShow : true, txt : '취소'}
     ]);
     RETURN : null
     ----------------------------------------------------*/
    $.fn.setBtnStyle = function(obj) {

        if ( obj !== null ) {

            $.each(obj, function (idx, data) {

                var element = data.ele;

                if ( data.isShow === undefined ) {
                    data.isShow = true;
                }
                if ( data.txt !== undefined ) {
                    element.text(data.txt);
                }

                if ( data.isShow === true ) {
                    element.show();
                } else {
                    element.hide();
                }

            });
        }
    }


    /*--------------------------------------------------
     기능   : bootstrap-modal 버그 수정(backdrop : none으로 처리했을 경우 hide 시 문제 발생)
     ----------------------------------------------------*/
    //$.fn.modal.Constructor.prototype.removeBackdrop = function () {

    //    if (this.$backdrop !== undefined) {
    //        this.$backdrop.remove();
    //        this.$backdrop = null;
    //    }
    //}

    /*--------------------------------------------------
     기능   : 해당하는 파일 리스트 가져오기
     INPUT  :
     - obj : {ele : fileUpload 객체, thumbEle : 프리뷰 객체, tbKey : 테이블 정보, tbSeq : 테이블 실 고유번호, gKey : 구분 코드}...
     예제 :
     $.fn.setFileList({ ele : jQuery('#fileupload'), thumbEle : jQuery("#filesPreview"), tbKey : 'COM_MEM_INFO', tbSeq : '1', gKey : 'A'});
     RETURN : 표시 여부(true / false)
     ----------------------------------------------------*/
    $.fn.setFileList = function(obj) {

        if ( obj !== null ) {

            $.fn.clearFileList(obj.ele, obj.thumbEle); // 초기화

            obj.ele.addClass('fileupload-processing');
            $.ajax({

                url: $.fn.sysUrl + '/files/getData/' + $.fn.sysUrl + '/' + obj.tbKey + '/' + obj.tbSeq + '/' + obj.gKey,
                dataType: "json",
                beforeSend: function() {

                    obj.ele.addClass('fileupload-processing');
                },
                context: obj.ele
            }).always(function () {

                $(this).removeClass('fileupload-processing');
            }).done(function (result) {

                $(this).fileupload('option', 'done').call(this, $.Event('done'), { result: result });
            });

            return true;
        }

        return false;
    }

    /*--------------------------------------------------
     기능   : 해당하는 파일 액션 Init
     INPUT  :
     - obj : {
        ele : fileUpload 객체,
        thumbEle : 프리뷰 객체,
        extKey : 확장자 구분,
        dropObj : 드롭할수 있는 객체,
        systemPath : 시스템 구분자,
        folPath : 폴더 경로,
        tableKey : 테이블 정보,
        seq : 테이블 실 고유번호,
        gubunKey : 구분 코드,
        compressFl : 압축 여부(1, 0),
        maxCnt : 최대 파일 갯수
     }...
     예제 :
     $.fn.initFileAction({ ele : jQuery('#fileupload'), thumbEle : jQuery("#filesPreview"), extKey : 'images', dropObj : jQuery('#dropzone'), systemPath : 'slm', folPath : 'config', tableKey: 'COM_MEM_INFO', seq: 'admin', gubunKey: 'A', compressFl: 0, maxCnt: 1 });
     RETURN : fileupload 객체
     ----------------------------------------------------*/
    $.fn.initFileAction= function(obj) {

        if ( obj !== null ) {

            var extPattern = '@';

            if ( obj.extKey !== undefined ) {

                switch(obj.extKey) {

                    case "images" :

                        extPattern = /(\.|\/)(gif|jpe?g|png)$/i;
                        break;

                    case "documents" :

                        extPattern = /(\.|\/)(xls|docx|pds|xlsx|doc|txt|hwp|pdf)$/i;
                        break;

                    default :

                        extPattern = '@';
                        break;
                }
            }

            if ( obj.dropObj === undefined ) {

                obj.dropObj = $(document);
            }

            if ( obj.compressFl === undefined ) {

                obj.compressFl = 0;
            }

            if (obj.maxCnt === undefined) {

                obj.maxCnt = 1;
            }

            // 첨부파일 Init
            obj.ele.fileupload(
                'option',
                'redirect',
                $.fn.sysUrl + '/res/assets/plugins/jquery-file-upload/cors/result.html?%s'
            );

            var objFiles = obj.ele.fileupload({

                url: $.fn.sysUrl + '/files/upload',
                dataType: 'json',
                acceptFileTypes: extPattern,
                disableImageResize: /Android(?!.*Chrome)|Opera/.test(window.navigator.userAgent),
                maxFileSize: 5000000,
                dropZone: obj.dropObj,
                autoUpload: false,
                limitConcurrentUploads: obj.maxCnt,
                maxNumberOfFiles: obj.maxCnt,
                formData: {
                    rootPath: $.fn.sysUrl, systemPath: obj.systemPath, folPath: obj.folPath,
                    tableKey: obj.tableKey, seq: obj.seq, gubunKey: obj.gubunKey
                }
                /*,
                add: function (e, data) {
                    $('body').append('<p class="upl">Uploading...</p>')
                    data.submit();
                },
                done: function (e, data) {
                    $('.upl').remove();

                    console.log(data.files);
//                    $.each(data.files, function (index, file) {
//                    });
                } */
                //filesContainer: $('#xxx')
            });

//            if ($.support.cors) {
//
//                $.ajax({
//                    url: $.fn.sysUrl + 'files/upload',
//                    type: 'HEAD'
//                }).fail(function () {
//                    $('<div class="alert alert-danger"/>')
//                        .text('Upload server currently unavailable - ' +
//                            new Date())
//                        .appendTo('#' + obj.ele.attr('id'));
//                });
//            }

            $.fn.setRealSeq(obj.ele, {seq : obj.seq, gubunKey : obj.gubunKey});

            if ( obj.thumbEle !== undefined ) {

                objFiles.bind('fileuploaddone', function (e, data) {
                    // Thumbnail 이미지 처리 하기 위한 이벤트 Catch(파일 업로드 완료 후)
                    var that = $(this).data('blueimp-fileupload') || $(this).data('fileupload'),
                        getFilesFromResponse = data.getFilesFromResponse || that.options.getFilesFromResponse,
                        files = getFilesFromResponse(data);

                    if (data.context) {

                        var resObj = $("#filesPreview").removeClass("hide").find(".file-preview-thumbnails");

                        data.context.each(function (index) {

                            if (!$(data.context[index]).hasClass(that.options.uploadTemplateId)) { return true; } // THIS LINE WAS ADDED

                            var file = files[index] || {error: 'Empty file upload result'};

                            if ( file.files_seq !== undefined ) {

                                if ( file.thumbnailUrl ) {

                                    resObj.append("\n<div id='fileKey" + file.files_seq + "' class='file-preview-frame'><a href='" + file.url + "' title='" + file.name + "' alt='" + file.name + "' download='" + file.name + "' data-gallery='#blueimp-gallery'><img src='" + file.thumbnailUrl + "' class='file-preview-image'></a></div>");
                                } else {

                                    resObj.append("\n<div id='fileKey" + file.files_seq + "' class='file-preview-frame'><div class='file-preview-other'><a href='" + file.url + "' title='" + file.name + "' alt='" + file.name + "'><h2><i class='glyphicon glyphicon-file'></i></h2>" + file.name + "</a></div></div>");
                                }
                            }
                        });
                    }
                }).bind('fileuploaddestroy', function (e, data) {
                    // 파일 삭제 전 이벤트 Catch
                    var filename = data.url.substring(data.url.indexOf("=") + 1).split("/");

                    $("#fileKey" + filename[filename.length - 1]).remove();
                });
            }

            if (obj.compressFl === 1) {

                $.fn.compress(objFiles, obj);
            }

            return objFiles;
        }
    }

    /*--------------------------------------------------
     기능   : 해당하는 파일 압축
     INPUT  :
     - objFiles : fileUpload 객체
     - obj : form Object
     예제 :
     $.fn.compress(jQuery('#fileupload'), {tableKey: 'COM_MEM_INFO', gubunKey: 'A'});
     RETURN : NULL
     ----------------------------------------------------*/
    $.fn.compress = function(objFiles, obj) {

        objFiles.bind('fileuploadstopped', function (e) {

            $.ajax({
                url: $.fn.sysUrl + '/files/compress',
                type: "POST",
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify({real_tb: obj.tableKey, real_seq: objFiles.fileupload('option', 'formData').seq, gubun_cd: obj.gubunKey}),
                success: function (data) {
                    //통신 성공시 처리
                    setOSXModal('압축 문서 생성에 ' + (data === 1 ? '성공' : '실패') + '하였습니다');
                },
                error: function (jqXhr, textStatus, errorThrown) {
                    //통신 에러 발생시 처리
                    console.log("Error '" + jqXhr.status + "' (textStatus: '" + textStatus + "', errorThrown: '" + errorThrown + "')");

                    setOSXModal('압축 문서 생성에 실패하였습니다.');
                }
            });
        });
    }

    /*--------------------------------------------------
     기능   : 해당하는 파일 SEQ 셋팅 후 폼 Enable / Disable
     INPUT  :
     - obj : fileUpload 객체
     - data : Setting 객체
     - thumObj : 프리뷰 객체
     예제 :
     $.fn.setRealSeq(jQuery('#fileupload'), {seq : 'admin', gubunKey : 'A'});
     RETURN : NULL
     ----------------------------------------------------*/
    $.fn.setRealSeq = function(obj, data, thumObj) {

        var optFormData = obj.fileupload(
            'option',
            'formData'
        );

        if (data === undefined) {

            data = {
                tableKey: undefined,
                seq: undefined,
                gubunKey: undefined
            };
        }

        $.each(data, function(k, v) {

            optFormData[k] = v;
        });

        obj.fileupload(
            'option',
            'formData',
            optFormData
        );

        if ( optFormData.seq === undefined ) {

            $.fn.clearFileList(obj, thumObj);
        }

        obj.fileupload(( optFormData.seq !== undefined ? 'enable' : 'disable'));
    }

    /*--------------------------------------------------
     기능   : 파일 폼 초기화
     INPUT  :
     - obj : fileUpload 객체
     - thumObj : 프리뷰 객체
     예제 :
     $.fn.clearFileList(jQuery('#fileupload'), jQuery("#filesPreview"));
     RETURN : NULL
     ----------------------------------------------------*/
    $.fn.clearFileList = function(obj, thumObj) {

        if ( obj !== null ) {
            // 초기화
            obj.find(".files").empty(); // 파일 리스트

            if ( thumObj !== undefined ) {

                thumObj.find(".file-preview-thumbnails").empty(); // 파일 Preview
            }
        }
    }

    /*--------------------------------------------------
     기능   : 해당하는 파일 일괄 삭제
     INPUT  :
     - obj : form Object
     예제 :
     $.fn.deleteAll({real_tb: 'COM_MEM_INFO', real_seq: 'admin', gubun_cd: 'A'});
     RETURN : NULL
     ----------------------------------------------------*/
    $.fn.deleteAll = function(obj) {

        $.ajax({
            url: $.fn.sysUrl + '/files/deleteAll',
            type: "POST",
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(obj),
            success: function (data) {
                //통신 성공시 처리
                setOSXModal('파일 삭제를 ' + (data === 1 ? '성공' : '실패') + '하였습니다');
            },
            error: function (jqXhr, textStatus, errorThrown) {
                //통신 에러 발생시 처리
                console.log("Error '" + jqXhr.status + "' (textStatus: '" + textStatus + "', errorThrown: '" + errorThrown + "')");

                setOSXModal('파일 삭제를 실패하였습니다.');
            }
        });
    }

    /*--------------------------------------------------
     기능   : Checkbox 선택 시 타겟 Input 객체 Readonly, Disabled 상태 변경
     INPUT  :
     - obj : [
     oObj : 체크 객체
     tObj : Target 객체,
     sty : readonly / disabled,
     isCompare : oObj의 체크 값과 비교하는 상태 값
     ]
     예제 :
     $.fn.setInputLockStyle([
        {oObj : jQuery('#wOutCnfLvFl'), tObj : jQuery('#wOutCnfLv'), sty : 'readonly', isCompare : true },
        {oObj : jQuery('#wOutLimitFl'), tObj : jQuery('#wOutLimitVlMin, #wOutLimitVlMax'), sty : 'readonly', isCompare : true }
     ]);
     RETURN : NULL
     ----------------------------------------------------*/
    $.fn.setInputLockStyle = function(obj) {

        if ( obj !== undefined ) {

            jQuery(obj).each(function (idx, data) {

                data.oObj.on('change', function() {

                    if ( data.sty === undefined ) {

                        data.sty = 'readonly'; // disabled
                    }

                    data.tObj.attr(data.sty, jQuery(this)[0].checked === data.isCompare ? false : true);
                });
            });
        }
    }

    /*--------------------------------------------------
     기능   : 파일 업로드 시 이미지 미리보기
     참고   : http://opoloo.github.io/jquery_upload_preview/
     RETURN : NULL
     ----------------------------------------------------*/
    $.fn.uploadPreview = function (options) {

        // Options + Defaults
        var settings = $.extend({
            input_field: ".image-input",
            preview_box: ".image-preview",
            label_field: ".image-label",
            label_default: "Choose File",
            label_selected: "Change File",
            no_label: false
        }, options);

        // Check if FileReader is available
        if (window.File && window.FileList && window.FileReader) {
            if (typeof($(settings.input_field)) !== 'undefined' && $(settings.input_field) !== null) {
                $(settings.input_field).change(function() {
                    var files = event.target.files;

                    if (files.length > 0) {
                        var file = files[0];
                        var reader = new FileReader();

                        // Load file
                        reader.addEventListener("load",function(event) {
                            var loadedFile = event.target;

                            // preview box 안에 초기화
                            $(settings.preview_box).empty();

                            // Check format
                            if (file.type.match('image')) {
                                // Image
                                $(settings.preview_box).css("background-image", "url("+loadedFile.result+")");
                                $(settings.preview_box).css("background-size", "cover");
                                $(settings.preview_box).css("background-position", "center center");
                            } else if (file.type.match('audio')) {
                                // Audio
                                $(settings.preview_box).html("<audio controls><source src='" + loadedFile.result + "' type='" + file.type + "' />Your browser does not support the audio element.</audio>");
                            } else {
                                alert("This file type is not supported yet.");
                            }
                        });

                        if (settings.no_label == false) {
                            // Change label
                            $(settings.label_field).html(settings.label_selected);
                        }

                        // Read the file
                        reader.readAsDataURL(file);
                    } else {
                        if (settings.no_label == false) {
                            // Change label
                            $(settings.label_field).html(settings.label_default);
                        }

                        // Clear background
                        $(settings.preview_box).css("background-image", "none");

                        // Remove Audio
                        $(settings.preview_box + " audio").remove();
                    }
                });
            }
        } else {
            alert("You need a browser with file reader support, to use this form properly.");
            return false;
        }
    }

    return {
        version : "1.0.2",
        author : "darkhand",
        jQuery : $
    };
}));