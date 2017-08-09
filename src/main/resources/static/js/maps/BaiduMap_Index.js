/**
 * Created by admin on 2017/7/25.
 */
checkhHtml5();

if(!isSupportCanvas()){
    alert('热力图目前只支持有canvas支持的浏览器,您所使用的浏览器不能使用热力图功能~')
}

var points = [];

var map = new BMap.Map("allmap");    // 创建Map实例

var carInterval = "";     //创建循环终止器

var tingyun = 0;

var kongche = 0;

var chongche = 0;

var dianzhao = 0;

var yuyue = 0;

var allCar = 0;

var option;

var app_count = 11;

var myChart = echarts.init(document.getElementById('car_main'));

map.centerAndZoom(new BMap.Point(113.52473516453851,22.256457269439903), 15);

map.addControl(new BMap.MapTypeControl());   //添加地图类型控件

map.setCurrentCity("珠海");          // 设置地图显示的城市 此项是必须设置的

map.enableScrollWheelZoom(true);     //开启鼠标滚轮缩放

var  mapStyle ={
    features: ["road", "building","water","land"],//隐藏地图上的poi
    style : "pink"  //设置地图风格为午夜黑
};

map.setMapStyle(mapStyle);

//详细的参数,可以查看heatmap.js的文档 https://github.com/pa7/heatmap.js/blob/master/README.md
//参数说明如下:
/* visible 热力图是否显示,默认为true
 * opacity 热力的透明度,1-100
 * radius 势力图的每个点的半径大小
 * gradient  {JSON} 热力图的渐变区间 . gradient如下所示
 *	{
 .2:'rgb(0, 255, 255)',
 .5:'rgb(0, 110, 255)',
 .8:'rgb(100, 0, 255)'
 }
 其中 key 表示插值的位置, 0~1.
 value 为颜色值.
 */
heatmapOverlay = new BMapLib.HeatmapOverlay({
    "radius":18,
    // "opacity":80,
    // "gradient":{
    //     .2:'rgb(0, 255, 255)',
    //     .5:'rgb(0, 110, 255)',
    //     .8:'rgb(100, 0, 255)'
    // }
    });

map.addOverlay(heatmapOverlay);

heatmapOverlay.setDataSet({data:points,max:80});

var d1 ;

var d2 ;

setOption();
/**
 * 第一次加载
 */
addStartOverlay(113.52473516453851, 22.256457269439903);

points = [];

if(carInterval !="") window.clearInterval(carInterval);

getDataOfGps(113.52473516453851,22.256457269439903);

carInterval= setInterval(function () {

    points = [];

    getDataOfGps(113.52473516453851,22.256457269439903);

}, 5000);
/**
 * 鼠标移动的加载
 */
map.addEventListener("moveend",function(e){

    var geoc = new BMap.Geocoder();

    geoc.getLocation(map.getCenter(), function(rs){

        var lng = rs.point.lng,

            lat = rs.point.lat;

        addStartOverlay(lng, lat);

        points = [];

        if(carInterval !="") window.clearInterval(carInterval);

        getDataOfGps(rs.point.lng,rs.point.lat);

        carInterval= setInterval(function () {

            points = [];

            getDataOfGps(rs.point.lng, rs.point.lat);

        }, 5000);

    });

});






/******************函数区**********************/
/*检查浏览器是否支持h5*/
function checkhHtml5() {

    if (typeof(Worker) === "undefined") {

        if(navigator.userAgent.indexOf("MSIE 9.0")<=0) {

            alert("定制个性地图示例：IE9以下不兼容，推荐使用百度浏览器、chrome、firefox、safari、IE10");

        }
    }
}

//是否显示热力图
function openHeatmap(){

    heatmapOverlay.show();

}

function closeHeatmap(){

    heatmapOverlay.hide();

}

function setGradient(){

    /*格式如下所示:
     {
     0:'rgb(102, 255, 0)',
     .5:'rgb(255, 170, 0)',
     1:'rgb(255, 0, 0)'
     }*/

    var gradient = {};

    var colors = document.querySelectorAll("input[type='color']");

    colors = [].slice.call(colors,0);

    colors.forEach(function(ele){

        gradient[ele.getAttribute("data-key")] = ele.value;

    });

    heatmapOverlay.setOptions({"gradient":gradient});

}

//判断浏览区是否支持canvas
function isSupportCanvas(){

    var elem = document.createElement('canvas');

    return !!(elem.getContext && elem.getContext('2d'));

}

//获取gps接口的数据
function getDataOfGps(lng,lat) {

    $.post("/showCarByGPS", {"lng": lng, "lat": lat}, function (result) {
        tingyun = 0;

        if (result.success) {

            tingyun = 0;

            kongche = 0;

            chongche = 0;

            dianzhao = 0;

            yuyue = 0;
            
            allCar = 0;

            $.each(result.driverList,function(index,object){


                points.push( {"lng":object.longitude,"lat":object.latitude,"count":object.speed} );

                if(object.zhuang_tai ==1) tingyun++;

                if(object.zhuang_tai ==2) kongche++;

                if(object.zhuang_tai ==3) chongche++;

                if(object.zhuang_tai ==4) dianzhao++;

                if(object.zhuang_tai ==5) yuyue++;

            });
            
            allCar = result.driverList.length;

            $('#tingyun').closest('.pie-chart-tiny').data('easyPieChart').update(tingyun/allCar*100) ;

            $('#kongche').closest('.pie-chart-tiny').data('easyPieChart').update(kongche/allCar*100) ;

            $('#chongche').closest('.pie-chart-tiny').data('easyPieChart').update(chongche/allCar*100) ;

            $('#dianzhao').closest('.pie-chart-tiny').data('easyPieChart').update(dianzhao/allCar*100) ;

            $('#yuyue').closest('.pie-chart-tiny').data('easyPieChart').update(yuyue/allCar*100) ;

            heatmapOverlay.setDataSet({data:points,max:80});

            $("#vehicles_span").html(allCar+"&nbsp;&nbsp;Car");

            $("#listview_OUTAGE").empty();
            $("#listview_EMPTY").empty();
            $("#listview_HEAVY_TRUCK").empty();
            $("#listview_CALL").empty();
            $("#listview_ORDER").empty();

            $("#listview_OUTAGE").append(tingyun);
            $("#listview_EMPTY").append(kongche);
            $("#listview_HEAVY_TRUCK").append(chongche);
            $("#listview_CALL").append(dianzhao);
            $("#listview_ORDER").append(yuyue);

            drawEcharts();
        }

    });
}

/**
 * 绘制echarts表
 */
function drawEcharts() {
    axisData = (new Date()).toLocaleTimeString().replace(/^\D*/,'');

    d1 = allCar;
    d2 = kongche;

    var data0 = option.series[0].data;
    var data1 = option.series[1].data;
    data0.shift();
    data0.push(d1);
    data1.shift();
    data1.push(d2);

    option.xAxis[0].data.shift();
    option.xAxis[0].data.push(axisData);
    option.xAxis[1].data.shift();
    option.xAxis[1].data.push(app_count++);

    myChart.setOption(option);
}


/**
 * 绘制开始位置标记物
 * @param lng,lat  y轴、x轴
 */
function addStartOverlay(lng,lat) {
    //移除之前的起点标记
    var $startP = $(".startPoint");

    if ($startP.length>0){

        $startP.remove();

    }
    function StartOverlay(point){

        this._point = point;

    }

    StartOverlay.prototype = new BMap.Overlay();

    StartOverlay.prototype.initialize = function(map){

        this._map = map;

        var div = this._div = document.createElement("div");

        div.style.position = "absolute";

        div.style.zIndex = "9999";

        div.style.background = 'url("img/start_location.png") no-repeat center';

        div.style.backgroundSize = '16px 35px';

        div.style.height = "35px";

        div.style.width = "16px";

        div.className = 'startPoint';

        map.getPanes().labelPane.appendChild(div);

        return div;

    };

    StartOverlay.prototype.draw = function(){

        var map = this._map;

        var pixel = map.pointToOverlayPixel(this._point);

        this._div.style.left = pixel.x - 8 + "px";

        this._div.style.top  = pixel.y - 35 + "px";

    };

    var sOverlay = new StartOverlay(new BMap.Point(lng, lat));

    map.addOverlay(sOverlay);
}


/**
 * 设置动态echarts表的属性
 */
function setOption() {
    option = {
        title: {
            text: '空车率',
            subtext: '空车的比值'
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'cross',
                label: {
                    backgroundColor: '#283b56'
                }
            }
        },
        grid:{
            bottom: 20
        },
        legend: {
            textStyle :{
                color : "#283b56"
            },
            data:['空车', '总车辆']
        },
        toolbox: {
            show: true,
            feature: {
                dataView: {readOnly: false},
                restore: {},
                saveAsImage: {}
            }
        },
        dataZoom: {
            show: false,
            start: 0,
            end: 100
        },
        xAxis: [
            {
                type: 'category',
                boundaryGap: true,
                nameTextStyle:{
                    color:"#283b56"
                },
                axisLine:{
                    lineStyle:{
                        color : "#283b56"
                    }
                },
                data: (function (){
                    var now = new Date();
                    var res = [];
                    var len = 10;
                    while (len--) {
                        res.unshift(now.toLocaleTimeString().replace(/^\D*/,''));
                        now = new Date(now - 5000);
                    }
                    return res;
                })()
            },
            {
                type: 'category',
                boundaryGap: true,
                nameTextStyle:{
                    color:"#283b56"
                },
                axisLine:{
                    lineStyle:{
                        color : "#283b56"
                    }
                },
                data: (function (){
                    var res = [];
                    var len = 10;
                    while (len--) {
                        res.push(len + 1);
                    }
                    return res;
                })()
            }
        ],
        yAxis: [
            {
                type: 'value',
                nameTextStyle:{
                    color:"#283b56"
                },
                axisLine:{
                    lineStyle:{
                        color : "#283b56"
                    }
                },
                scale: true,
                name: '空车',
                max: 600,
                min: 0,
                boundaryGap: [0.2, 0.2]
            },
            {
                type: 'value',
                nameTextStyle:{
                    color:"#283b56"
                },
                axisLine:{
                    lineStyle:{
                        color : "#283b56"
                    }
                },
                scale: true,
                name: '总车辆',
                max: 600,
                min: 0,
                boundaryGap: [0.2, 0.2]
            }
        ],
        series: [
            {
                name:'总车辆',
                type:'bar',
                xAxisIndex: 1,
                yAxisIndex: 1,
                data:(function (){
                    var res = [];
                    var len = 10;
                    while (len--) {
                        // res.push(Math.round(Math.random() * 1000));
                        res.push(d1);
                    }
                    return res;
                })()
            },
            {
                name:'空车',
                type:'line',
                data:(function (){
                    var res = [];
                    var len = 0;
                    while (len < 10) {
                        // res.push((Math.random()*10 + 5).toFixed(1) - 0);
                        res.push(d2);
                        len++;
                    }
                    return res;
                })()
            }
        ]
    };
}