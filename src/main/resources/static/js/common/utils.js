var Main = {};
Main.winSize = function () {
    var winW, winH;
    if (window.innerHeight) {// all except IE
        winW = window.innerWidth;
        winH = window.innerHeight;
    } else if (document.documentElement && document.documentElement.clientHeight) {// IE 6 Strict Mode
        winW = document.documentElement.clientWidth;
        winH = document.documentElement.clientHeight;
    } else if (document.body) { // other
        winW = document.body.clientWidth;
        winH = document.body.clientHeight;
    }  // for small pages with total size less then the viewport
    return {WinW: winW, WinH: winH};
}

if (!window.console) {
    window.console = {};
}
if (!window.console.log) {
    window.console.log = function (msg) {
    };
}
var layer;

layui.use('layer', function () {
    layer = layui.layer;
});

Main.abc = function () {
    alert("123");
};

/**
 * post请求
 * @param url 请求路径
 * @param data 参数{key:value,...}
 * @param callback 回调函数
 */
Main.post = function (url, data, callback) {
    $.post(rootPath + url + "?etc=" + new Date().getTime(), data, callback);
};

/**
 * getJSON请求
 * @param url 请求路径
 * @param data 参数{key:value,...}
 * @param callback 回调函数
 */
Main.getJson = function (url, data, callback) {
    $.getJSON(rootPath + url + "?etc=" + new Date().getTime(), data, callback);
}


Main.showErrorMsg = function (formId, errorJson) {
    // 系统错误
    if (errorJson != null && !errorJson.match("^\{(.+:.+,*){1,}\}$")) {
        layer.alert("操作失败" + errorJson);
    } else {// 校验错误
        var errorResult = $.parseJSON(errorJson);
        if (errorResult.field) {
            var field = errorResult.field;
            var message = errorResult.message;
            var fidldElm = $("#" + formId + " [name='" + field + "']");
            fidldElm.after("<label class='error' for='" + field
                + "' generated='true'>" + message + "</label>");
            fidldElm.keydown(function () {
                $(this).parent().find("label[for='" + field + "']").remove();
            });
        }
    }
}

$.fn.serializeObject = function () {
    var o = {};
    var a = this.serializeArray();
    $.each(a, function () {
        if (o[this.name] !== undefined) {
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

if ($.jgrid != null && $.jgrid != undefined) {
    $.jgrid.extend({//扩展$.fn.jgrid对象
        //添加--表格自适应到容器的方法
        fixToContainer: function () {
            $(this).each(function () {
                var obj = $(this)[0];
                var captionHeight = (obj.p.caption == "") ? 0 : 25;
                var pagerHeight = (obj.p.pager == "") ? 0 : 25;
                var toolbarHeight = (obj.p.toolbar[0] == false) ? 0 : 25;
                toolbarHeight += (obj.p.toolbar[1] == "both") * toolbarHeight;
                var borderAndTableHeight = 26;
                var searchHeight = $(obj).parents("body").find(".search_bar").height();
                // console.log(searchHeight, captionHeight, pagerHeight, toolbarHeight, borderAndTableHeight, Main.winSize().WinH);
                var containerHeight = Main.winSize().WinH - captionHeight - pagerHeight - toolbarHeight - borderAndTableHeight - searchHeight - 20;
                // console.log(containerHeight);
                $("#" + obj.id).jqGrid("setGridHeight", containerHeight);
            });
        }
    });
}

;(function ($) {
    // .gridUtil() interface
    $.fn.gridUtil = function (jq) {
        if (this instanceof $.fn.gridUtil) {
            this.$ = jq;
            return this;
        }
        return new $.fn.gridUtil(this);
    };
    // $.gridUtil(selector) interface
    $.gridUtil = function (selector) {
        return new $.fn.gridUtil($(selector));
    };

    $.fn.gridUtil.prototype = $.fn.gridUtil.fn = {
        defaults: {
            datatype: "json",
            mtype: "POST",
            width: "100%",
            autowidth: true,
            sortable: true,
            altRows: true,
            altclass: 'tr-even',
            rowNum: 15,
            rowList: [15, 20, 50, 100],
            multiselect: true,
            multiselectWidth: 20,
            multiboxonly: true,  //多选时设置选择checkbox才生效
            viewrecords: true,
            rownumbers: true,
            scrollrows: true,
            autoencode: true,
            loadError: function (xhr, st, err) {
                //TODO
            },
            loadComplete: function (data) {
                if (data.success == "false" || data.success == false) {
                    // console.log(data.data);
                    layer.msg(data.data);
                } else if (data.success) {
                    if (data.rows.length == 0) {
                        layer.msg("没有查询到数据");
                    }
                }
            },
            jsonReader: {
                root: "rows",
                page: "page",
                total: "total",
                records: "records",
                repeatitems: false,
                subgrid: {
                    root: "rows",
                    page: "page",
                    total: "total",
                    records: "records",
                    repeatitems: false
                }
            }
        },
        simpleGrid: function (options) {
            var settings = $.extend({}, this.defaults, options);
            // console.log(options.pager);
            var jq = this.$.jqGrid(settings);
            // if ($.browser.msie) {
            //     jq.closest(".ui-jqgrid-bdiv").css({'overflow-x': 'scroll'});
            // }

            jq.jqGrid('navGrid', options.pager, {edit: false, add: false, del: false, search: false});


            $(window).bind('resize', function () {
                jq.jqGrid("setGridWidth", Main.winSize().WinW - 15);
                jq.jqGrid("fixToContainer");
            }).trigger('resize');

            return jq;
        },
        searchGrid: function (form) {
            var objs = form.serializeObject();
            for (var p in objs) {
                this.$.setPostDataItem(p, objs[p]);
            }
            this.$.jqGrid("setGridParam", {page: 1}).trigger("reloadGrid");
            return false;
        }

    };
})(jQuery);

;(function ($) {
    $(document).ready(function () {
    }).keydown(function (e) {
        if (e.which === 27) {
            /** ESC键按下时关闭弹出窗口！ */
            layer.closeAll();
            top.layer.closeAll();
        }
    });
})(jQuery);