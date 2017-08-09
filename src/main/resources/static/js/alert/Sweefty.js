(function($){
    
    var _SW_equalizeDivs = false,
    _SW_devices = {
        1 : 'screen',
        2 : 'screen',
        3 : 'tablet',
        4 : 'mobile',
        5 : 'mobile',
        types : {
            1 : 'Normal Screen',
            2 : 'Large Screen',
            3 : 'Tablet',
            4 : 'Mobile Landscape',
            5 : 'Mobile Portrait'
        }
    },_SW,
    _SW_cache = {};
    
    //module manager
    var SW_SELF_URL = (function() {
        var script_tags = document.getElementsByTagName('script');
        var path = script_tags[script_tags.length-1].src;
        path = path.match(/(.*\/).*\.js/);
        return path[1];
    })();
    
    var modules = {};
    var parent_module;
    var require = function(id,callback){
    	//if callback passed we expect this to be async require
    	var async = typeof callback === 'function' ? true : false;
    	if ($.isArray(id)){
    	    var ret = {};
    	    $(id).each(function(i,ix){
    		var ex = require(ix,callback);
    		ret = $.extend({}, ret, ex);
    	    });
    	    return ret;
    	}
    	
    	//resolve id
    	var url = SW_SELF_URL + id;
    	if ( modules[id] && modules[id].loaded === true ){
    	    console.log( id + ' loaded from cache');
    	    return modules[id].exports;
    	}
    	
    	modules[id] = {
    	    id : id,
    	    parent : parent_module,
    	    exports : {},
    	    loaded : false
    	};
    	
    	parent_module = modules[id];
    	window.exports = modules[id].exports;
    	var loadScript = function (url, cb) {
    	    cb = async ? cb : function(){}
    	    if (!async)
    		modules[id].loaded = true;
    	    jQuery.ajax({
				url: url,
				dataType: 'script',
				cache: false,
				success: function(){
					if (async) {
					   modules[id].loaded = true;
					}
					
					//switch require back to the parent
					if (modules[id].parent){
					   window.exports = modules[id].parent.exports;
					}
					
					parent_module = modules[id].parent;
					cb(modules[id].exports);
				},
				async: async
    	    });
    	}
    	
    	loadScript(url,callback);
    	return modules[id].exports;
    };
    
    window.require = require;
//    setTimeout(function(){
//        require('test.js',function(ex){
//	    console.log(modules);
//        });
//        //console.log(t);
//        var t = require('test.js');
//        //var t2 = require('test.js');
//        //t.google();
//        //t2.google();
//        //console.log(t);
//    },1000);
    
    //resize event handler with delay response
    var _resizeTimer;
    $(window).resize(function() {
        clearTimeout(_resizeTimer);
        _resizeTimer = setTimeout(methods.fireOnResize, 250);
    });

    //inatiate after document load
    $(document).ready(function() {
        //initiate
        if (!_SW){
            _SW = Sweefty();
        }
        
	   //quick test if our browser support floating point margins
        var div = $('<div id="SW_hidden_element" style="margin-left:50.6%"></div>');
        $('body').append(div);
        var num = parseFloat(div.css('margin-left'));
        if (num % 1 == 0){
            _SW_equalizeDivs = true;
        }
        
        div.hide();
        
        //run resize event
        setTimeout(methods.fireOnResize,250);
        //$('.row .column').wrapInner('<div style="margin:10px 15px; 0"></div>');
        
        //tabs
        $('.tabs').each(function(){
            var tabs = $(this);
            var divs = $('> div',tabs);
            var lis = $('> ul li',tabs);
            divs.hide();
            
            lis.each(function(i){
                var $this = $(this);
                if ($this.hasClass('active')){
                    divs.eq(i).show();
                }
                
                $this.click(function(){
                    
                    if ($(this).hasClass('active')){
                        return false;
                    }
                    
                    lis.removeClass('active');
                    $(this).addClass('active');
                    divs.hide();
                    divs.eq(i).fadeIn('fast');
                    return true;
                });
            });
        });
        
        //nav menu fix for IE 6 & 7
        $("ul.nav li").hover( function() {
            $(this).addClass("iehover");
        },function() {
            $(this).removeClass("iehover");
        });
	
        $('ul.nav > li').each(function(){
            var $this = $(this);
            var As = $this.children('a');
            
            if (As.next('ul').length){
                As.append('<span class="dir"></span>');
            } 
        });
        
        //delay processing of data-trigger attributes
        //making sure all elements in place
        setTimeout(function(){
            $('[data-trigger]').each(function(){
                var $this = $(this);
                var options = $this.data('trigger').split('[and]');
                $(options).each(function(i){
                    methods.parse($this,options[i]);
                });
            });
        },100);
    });
    
    //internal methods
    var methods = {
        parse : function(ele,options){
            var $this = this;
            if (typeof options == 'object'){
                
            } else {
                var obj = {};
                var action = '';
                
                options = options.split(';');
                for (var x = 0; x < options.length; x++){
                    var values = options[x].split(':');
                    if (values && values.length == 2){
                        var key = values[0].replace(/\s+/,'');
                        var value = values[1];
                        
                        if (x == 0){
                            action = key;
                        }
                        
                        obj[key] = value;
                    }
                }
                
                if (typeof $this.trigger[action] === 'function'){
                    $this.trigger[action](ele,obj);
                }
            }
        },
        
        trigger : {
            //default - predefined triggers
            //other triggers can be found in plugins folder as seperate plugins
            toggle : function(ele,obj){
                var target = obj.toggle;
                var on = obj.on || 'click';
                var type = obj.type || 'fadeToggle';
                
                if (target == 'this'){
                    target = ele;
                } else if (target == 'next') {
                    target = ele.next();
                }else if (target == 'previous') {
                    target = ele.prev();
                }
                
                target = $(target);
                
                if (obj.target == 'hide'){
                    target.hide();
                }
                
                ele.on(on,function(){
                    target[type]();
                });
            },
            
            menu : function(ele,obj) {
                
                var type = obj.menu;
                var getList = function(element,count){
                    var counter;
                    !count ? counter = 0 : counter = count;
                    
                    var value = '';
                    var style = '';
                    var padding = '';
                    var indent = '';
                    if (counter > 0){
                        indent = new Array( counter+1 ).join( '\u00a0\u00a0' );
                    }
                    
                    element.children('li').each(function(){
                        var $this = $(this);
                        var A = $this.children('a');
                        var text = A.text();
                        var link = A.attr('href');
                        
                        //get current link
                        //if link is hash or nothing then disable
                        //get associated event
                        value +='<option style="'+style+'" value="'+link+'">'+indent+text+'</option>';
                        
                        if (A.next('ul').length){
                            value += getList(A.next('ul'),counter+1);
                        }
                    });
                    
                    return value;
                }
                
                var select = '<div class="selectContainer show-phone prettySelect">';
                select += '<select class="selectMenu">';
                select += getList(ele);
                select +='</select>';
                select += '</div>';
                
                var newSelect = $(select);
                
                if (type == 'pretty'){
                var selectMenu = newSelect.find('select');
                    selectMenu.each(function(){
                        var title = $(this).attr('title');
                        if( $('option:selected', this).val() != ''  ) title = $('option:selected',this).text();
                        $(this)
                        .css({'z-index':10,'opacity':0,'-khtml-appearance':'none'})
                        .after('<div class="prettyMenu"><span class="prettyTitle">' + title + '</span><span class="pointer"></span></div>')
                        .change(function(){
                            var $this = $(this);
                            val = $('option:selected',$this).text();
                            $('span.prettyTitle',newSelect).text(val);
                            
                            //get value
                            var value = $this.val();
                            
                            if (value == '#'){
                                var index = $('option:selected',$this).index();
                                //get original element and trigger click event
                                ele.find('a').eq(index).trigger('click');    
                            } else {
                                window.location = value;
                            }
                        });
                    });
                }
                
                ele.addClass('hide-phone');
                if (obj.appendTo){
                    $(obj.appendTo).append(newSelect);
                } else {
                    ele.after(newSelect);
                }
            }
        },

        //array of events that will run on screen resize event
        events : [function(){
            //default resize acions
            //run fittext function on resize
            $('.fittext').fittext();
        }],
        
        fireOnResize : function(){
            //get current screen resolution
            _SW.on.lastDevice = _SW.on._ini();
            
            for (var i= 0; i < methods.events.length;i++){
                var fun = methods.events[i];
                fun();
            }
            
            _SW.on.previousDevice = _SW.on.lastDevice;
        },
        
        setEvent: function(func) {
            if (typeof func !== 'function'){
		throw 'Events must be of a function type';
            } else {
                methods.events.push(func);
            }
        }
    };
    
    var SW = function(){
        _SW = this;
    };
    
    //set plugin triggers
    SW.prototype.trigger = function(name,fn){
        methods.trigger[name] = fn;
        return fn;
    };
    
    SW.prototype.require = require;
    SW.prototype.on = {
        _ini : function(){
            var id = parseInt($('#SW_hidden_element').css('width'));
            var screenType = _SW_devices[id];
            return screenType;
        },
        resize : function(func){
            methods.setEvent(func);
        },
        device : function(dev,func,func2){
            var deviceType,Runfunc1,Runfunc2;
            if (func2 && typeof func2 == 'function'){
                Runfunc2 = func2;
            } if (func && typeof func == 'function'){
                Runfunc1 = func;
                deviceType = dev;
            } else {
                Runfunc1 = dev;
            }
            
            methods.setEvent(function(){
                if (_SW.on.previousDevice !== _SW.on.lastDevice){
                    if (deviceType){
                        if (deviceType == _SW.on.lastDevice) {
                            Runfunc1(_SW.on.lastDevice);
                        } else if (Runfunc2 && deviceType !== _SW.on.lastDevice
			    && _SW.on.previousDevice == deviceType) {
                            Runfunc2(_SW.on.lastDevice);
                        }
                    } else {
                        Runfunc1(_SW.on.lastDevice);
                    }
                }
            });
        },
        mobile : function(func,func2){
            _SW.on.device('mobile',func,func2);
        },
        tablet : function(func,func2){
            _SW.on.device('tablet',func,func2);
        },
        screen : function(func,func2){
            _SW.on.device('screen',func,func2);
        },
        lastDevice : '',
        previousDevice : ''
    };
    
    function path() {
        this.path = window.location.pathname;
    }
    
    //path detector
    SW.prototype.path = function(){
	   return new path();
    };
    
    $.fn.fittext = function(customOptions){
        return this.each(function() {
            var $this = $(this);
            
            //get data-compression
            var font = $this.data('font') || '1';
            font = font.toString().split(';');
            
            var compression = parseFloat(font[0]) || 1;
            var minimum = parseInt(font[1]) || undefined;
            var maximun = parseInt(font[2]) || undefined;
            
            var text = $this.text();
            var charsnum = text.split('').length;
            
            //get width
            var width = $this.width();
            var fontSize = parseInt((width/charsnum)*(2*compression));
            
            if (minimum && fontSize < minimum){
                fontSize = minimum;
            } else if (maximun && fontSize > maximun){
                fontSize = maximun;
            }
            
            $this.css('fontSize',fontSize+'px');
        });
    };
    
    window.Sweefty = function (args) {
        return _SW || new SW(args);
    }
    
}(jQuery));
    
    

    Sweefty().on.mobile(function(){
        
        var height = $('.row.scroll > .column:first').outerHeight();
        var sLeft = $(document).width();
        
        $('.row.scroll').css({
            //overflow : 'hidden',
            //height : height,
            position : 'relative'
        });
        
        $('.row.scroll > .column').on('swipeLeft',function(e){
            var $this = $(this);
            var next = $this.next('.column');
            
            if (next.length < 1){
                //
                $this.stop().animate({
                    left: -(sLeft/2)
                    },{
                    duration : 200,
                    complete : function(){
                        $this.animate({
                        left : 0
                        },{
                        duration : 250
                        });
                    }
                });
                
            }
            else {
                next.css({left:sLeft});
                $this.stop().animate({
                    left: -sLeft
                    },{
                    duration : 250,
                    complete : function(){
                        $this.hide();
                        next.show();
                        next.stop().animate({
                            left : 0
                        },{
                            duration : 250
                        });
                    }
                });
            }
        }).bind('swipeRight',function(e){
            
            var $this = $(this);
            var prev = $this.prev('.column');
            
            if (prev.length < 1){
                $this.stop().animate({
                    left: sLeft/2
                    },{
                    duration : 250,
                    complete : function(){
                        $this.animate({
                            left : 0
                        },{
                            duration : 250
                        });
                    }
                });
            }
            else {
                prev.css({left:-sLeft});
                $this.stop().animate({
                    left: sLeft
                    },{
                    duration : 250,
                    complete : function(){
                        $this.hide();
                        prev.show();
                        prev.stop().animate({
                            left : 0
                        },{
                            duration : 250
                        });
                    }
                });
            }
        });
        
        //other devices
        },function(){
            $('.row.scroll').css({
                height : 'auto',
                overflow : 'none'
            });
            
            $('.row.scroll .column').css({display:'block'});
            $('.row.scroll > .column').unbind('swipeLeft').unbind('swipeRight');
        }
    );

/*
 * jQuery Easing v1.3 - http://gsgd.co.uk/sandbox/jquery/easing/
 *
 * Uses the built in easing capabilities added In jQuery 1.1
 * to offer multiple easing options
 *
 * TERMS OF USE - jQuery Easing
 * 
 * Open source under the BSD License. 
 * 
 * Copyright © 2008 George McGinley Smith
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of 
 * conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list 
 * of conditions and the following disclaimer in the documentation and/or other materials 
 * provided with the distribution.
 * 
 * Neither the name of the author nor the names of contributors may be used to endorse 
 * or promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 *  COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 *  GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED 
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 * OF THE POSSIBILITY OF SUCH DAMAGE. 
 *
*/

// t: current time, b: begInnIng value, c: change In value, d: duration
jQuery.easing['jswing'] = jQuery.easing['swing'];

jQuery.extend( jQuery.easing,
{
	def: 'easeOutQuad',
	swing: function (x, t, b, c, d) {
		//alert(jQuery.easing.default);
		return jQuery.easing[jQuery.easing.def](x, t, b, c, d);
	},
	easeInQuad: function (x, t, b, c, d) {
		return c*(t/=d)*t + b;
	},
	easeOutQuad: function (x, t, b, c, d) {
		return -c *(t/=d)*(t-2) + b;
	},
	easeInOutQuad: function (x, t, b, c, d) {
		if ((t/=d/2) < 1) return c/2*t*t + b;
		return -c/2 * ((--t)*(t-2) - 1) + b;
	},
	easeInCubic: function (x, t, b, c, d) {
		return c*(t/=d)*t*t + b;
	},
	easeOutCubic: function (x, t, b, c, d) {
		return c*((t=t/d-1)*t*t + 1) + b;
	},
	easeInOutCubic: function (x, t, b, c, d) {
		if ((t/=d/2) < 1) return c/2*t*t*t + b;
		return c/2*((t-=2)*t*t + 2) + b;
	},
	easeInQuart: function (x, t, b, c, d) {
		return c*(t/=d)*t*t*t + b;
	},
	easeOutQuart: function (x, t, b, c, d) {
		return -c * ((t=t/d-1)*t*t*t - 1) + b;
	},
	easeInOutQuart: function (x, t, b, c, d) {
		if ((t/=d/2) < 1) return c/2*t*t*t*t + b;
		return -c/2 * ((t-=2)*t*t*t - 2) + b;
	},
	easeInQuint: function (x, t, b, c, d) {
		return c*(t/=d)*t*t*t*t + b;
	},
	easeOutQuint: function (x, t, b, c, d) {
		return c*((t=t/d-1)*t*t*t*t + 1) + b;
	},
	easeInOutQuint: function (x, t, b, c, d) {
		if ((t/=d/2) < 1) return c/2*t*t*t*t*t + b;
		return c/2*((t-=2)*t*t*t*t + 2) + b;
	},
	easeInSine: function (x, t, b, c, d) {
		return -c * Math.cos(t/d * (Math.PI/2)) + c + b;
	},
	easeOutSine: function (x, t, b, c, d) {
		return c * Math.sin(t/d * (Math.PI/2)) + b;
	},
	easeInOutSine: function (x, t, b, c, d) {
		return -c/2 * (Math.cos(Math.PI*t/d) - 1) + b;
	},
	easeInExpo: function (x, t, b, c, d) {
		return (t==0) ? b : c * Math.pow(2, 10 * (t/d - 1)) + b;
	},
	easeOutExpo: function (x, t, b, c, d) {
		return (t==d) ? b+c : c * (-Math.pow(2, -10 * t/d) + 1) + b;
	},
	easeInOutExpo: function (x, t, b, c, d) {
		if (t==0) return b;
		if (t==d) return b+c;
		if ((t/=d/2) < 1) return c/2 * Math.pow(2, 10 * (t - 1)) + b;
		return c/2 * (-Math.pow(2, -10 * --t) + 2) + b;
	},
	easeInCirc: function (x, t, b, c, d) {
		return -c * (Math.sqrt(1 - (t/=d)*t) - 1) + b;
	},
	easeOutCirc: function (x, t, b, c, d) {
		return c * Math.sqrt(1 - (t=t/d-1)*t) + b;
	},
	easeInOutCirc: function (x, t, b, c, d) {
		if ((t/=d/2) < 1) return -c/2 * (Math.sqrt(1 - t*t) - 1) + b;
		return c/2 * (Math.sqrt(1 - (t-=2)*t) + 1) + b;
	},
	easeInElastic: function (x, t, b, c, d) {
		var s=1.70158;var p=0;var a=c;
		if (t==0) return b;  if ((t/=d)==1) return b+c;  if (!p) p=d*.3;
		if (a < Math.abs(c)) { a=c; var s=p/4; }
		else var s = p/(2*Math.PI) * Math.asin (c/a);
		return -(a*Math.pow(2,10*(t-=1)) * Math.sin( (t*d-s)*(2*Math.PI)/p )) + b;
	},
	easeOutElastic: function (x, t, b, c, d) {
		var s=1.70158;var p=0;var a=c;
		if (t==0) return b;  if ((t/=d)==1) return b+c;  if (!p) p=d*.3;
		if (a < Math.abs(c)) { a=c; var s=p/4; }
		else var s = p/(2*Math.PI) * Math.asin (c/a);
		return a*Math.pow(2,-10*t) * Math.sin( (t*d-s)*(2*Math.PI)/p ) + c + b;
	},
	easeInOutElastic: function (x, t, b, c, d) {
		var s=1.70158;var p=0;var a=c;
		if (t==0) return b;  if ((t/=d/2)==2) return b+c;  if (!p) p=d*(.3*1.5);
		if (a < Math.abs(c)) { a=c; var s=p/4; }
		else var s = p/(2*Math.PI) * Math.asin (c/a);
		if (t < 1) return -.5*(a*Math.pow(2,10*(t-=1)) * Math.sin( (t*d-s)*(2*Math.PI)/p )) + b;
		return a*Math.pow(2,-10*(t-=1)) * Math.sin( (t*d-s)*(2*Math.PI)/p )*.5 + c + b;
	},
	easeInBack: function (x, t, b, c, d, s) {
		if (s == undefined) s = 1.70158;
		return c*(t/=d)*t*((s+1)*t - s) + b;
	},
	easeOutBack: function (x, t, b, c, d, s) {
		if (s == undefined) s = 1.70158;
		return c*((t=t/d-1)*t*((s+1)*t + s) + 1) + b;
	},
	easeInOutBack: function (x, t, b, c, d, s) {
		if (s == undefined) s = 1.70158; 
		if ((t/=d/2) < 1) return c/2*(t*t*(((s*=(1.525))+1)*t - s)) + b;
		return c/2*((t-=2)*t*(((s*=(1.525))+1)*t + s) + 2) + b;
	},
	easeInBounce: function (x, t, b, c, d) {
		return c - jQuery.easing.easeOutBounce (x, d-t, 0, c, d) + b;
	},
	easeOutBounce: function (x, t, b, c, d) {
		if ((t/=d) < (1/2.75)) {
			return c*(7.5625*t*t) + b;
		} else if (t < (2/2.75)) {
			return c*(7.5625*(t-=(1.5/2.75))*t + .75) + b;
		} else if (t < (2.5/2.75)) {
			return c*(7.5625*(t-=(2.25/2.75))*t + .9375) + b;
		} else {
			return c*(7.5625*(t-=(2.625/2.75))*t + .984375) + b;
		}
	},
	easeInOutBounce: function (x, t, b, c, d) {
		if (t < d/2) return jQuery.easing.easeInBounce (x, t*2, 0, c, d) * .5 + b;
		return jQuery.easing.easeOutBounce (x, t*2-d, 0, c, d) * .5 + c*.5 + b;
	}
});
