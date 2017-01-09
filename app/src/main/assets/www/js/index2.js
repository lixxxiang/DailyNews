
    var tdtImageProvider = new Cesium.WebMapTileServiceImageryProvider({
        url: 'http://t4.tianditu.com/img_w/wmts?',
        layer: 'img',
        style: 'default',
        format: 'tiles',
        tileMatrixSetID: 'w',
        maximumlevel: 3,
        alpha: 1.0,
        show: true,
    });

    Cesium.BingMapsApi.defaultKey = '5Vv41lJmAZXWbGIzEOr5~hRdyprq3YjnMm149t4PS5Q~AoyOxzxOxDEMLfugZcp_GC9JDW0nSH-gm4-xYM5erL0lkoMJTAM5hk6UBRoKrSWK';

    var viewer = new Cesium.Viewer('cesiumContainer',
            {
                imageryProvider: tdtImageProvider,
                animation: false,
                timeline: false,
                fullscreenButton: false,
                geocoder: false,
                selectionIndicator: false,
                navigationInstructionsInitiallyVisible: false,
                baseLayerPicker: false,
                homeButton: false,
                navigationHelpButton: false,
                sceneModePicker: false,
                infoBox: false,
            });
    var camera = viewer.scene.camera;
    camera.flyTo({
        destination: Cesium.Cartesian3.fromDegrees(108.9, 34.267, 20000000)
    });

    function setLocation(location) {
        var array = location.split(",");
        console.log("latitude" + array[0]);
        console.log("longtitude" + array[1]);
        camera.flyTo({
            destination: Cesium.Cartesian3.fromDegrees(array[1], array[0], 200000)
        });
    }
    function backHome(){
        camera.flyTo({
            destination: Cesium.Cartesian3.fromDegrees(108.9, 34.267, 20000000)
        });
    }
    //    var geoCoder = viewer.geocoder;
    //    geoCoder.searchText = "changchun";
    //    geoCoder.viewModel.search();

    //viewer.extend(Cesium.viewerCesiumNavigationMixin, {});

    /**
     * plus and minus start
     *
     * */
    //    var canvas = viewer.canvas;
    //    canvas.setAttribute('tabindex', '0');
    //    canvas.onclick = function () {
    //        canvas.focus();
    //    };
    //    var ellipsoid = viewer.scene.globe.ellipsoid;
    //    var flags = {
    //        looking: false,
    //        moveForward: false,
    //        moveBackward: false,
    //        moveUp: false,
    //        moveDown: false,
    //        moveLeft: false,
    //        moveRight: false
    //    };
    //
    //
    //    function plus() {
    //        document.getElementById("plus").click();
    //    }
    //
    //    function minus() {
    //        document.getElementById("minus").click();
    //    }
    //
    //    function getFlagForKeyCode(buttonType) {
    //        if (buttonType == "plus") {
    //            flags['moveForward'] = true;
    //            return 'moveForward';
    //        } else if (buttonType == "minus") {
    //            flags['moveBackward'] = true;
    //            return 'moveBackward';
    //
    //        }
    //    }
    //
    //    var temp = '';
    //    $(document).ready(function () {
    //        $("#plus").click(function () {
    //            getFlagForKeyCode('plus');
    //            temp = getFlagForKeyCode('plus');
    //            console.log('temp in :' + temp);
    //        });
    //        var success = function (message) {
    //            return 'moveForward';
    //        };
    //        var fail = function (message) {};
    //    });
    //
    //    $(document).ready(function () {
    //        $("#minus").click(function () {
    //            temp = getFlagForKeyCode('minus');
    //            getFlagForKeyCode('minus');
    //        });
    //        var success = function (message) {
    //            return 'moveBackward';
    //        };
    //        var fail = function (message) {};
    //    });
    //
    //    document.addEventListener('click', function (e) {
    //        var flagName = temp;
    //        console.log('flagName : ', flagName);
    //        if (typeof flagName !== 'undefined') {
    //            flags[flagName] = true;
    //            var camera = viewer.camera;
    //            var cameraHeight = ellipsoid.cartesianToCartographic(camera.position).height;
    //            var moveRate = cameraHeight / 100.0;
    //            if (flags.moveForward)
    //                camera.moveForward(moveRate * 5);
    //            if (flags.moveBackward)
    //                camera.moveBackward(moveRate * 5);
    //        }
    //    }, false);
    /*
     * plus and minus end
     *
     * */

    function compass(content) {
        console.log(content);
        var camera = viewer.scene.camera;
        var position = camera.position;
        console.log(camera.position);

        camera.flyTo({
//                destination: Cesium.Cartesian3.fromDegrees(108.9, 34.267, 10000000.34038727246224)
            destination: position
        });

//        var geoCoder = viewer.geocoder;
//        geoCoder.searchText = "changchun";
//        geoCoder.viewModel.search();
//        console.log(geoCoder.viewModel.search());
    }

    var imageryLayers = viewer.imageryLayers;
    var curWwwPath = "http://10.10.90.6:8089/";
    var initialurl = curWwwPath + 'GetInitialLayers.ashx';
    var data = {xmlname: "LayersCollection"};
    InitialZhuantiLayers(viewer, initialurl);
    var layers = new Array();
    function InitialZhuantiLayers(viewer, initialurl) {
        $.ajax({
            url: initialurl,
            async: true,
            data: data,
            success: function (result) {
                $.each(result.layers, function (index, layer) {
                    $.each(layer, function (key, value) {

                        var currenturl = curWwwPath + value.urlSource + ".ashx?params={z}/{x}/{reverseY}/";
                        console.log("-=----"+currenturl);
                        var region = value.region.split(",");
                        var rectangle = Cesium.Rectangle.fromDegrees(region[0], region[1], region[2], region[3]);
                        var currentProvider = new Cesium.UrlTemplateImageryProvider({
                            url: currenturl,
                            credit: value.credit,
                            tilingScheme: new Cesium.WebMercatorTilingScheme({
                                ellipsoid: Cesium.Ellipsoid.WGS84,
                                rectangleSouthwestInMeters: new Cesium.Cartesian2(-20037508.34, -20037508.34),
                                rectangleNortheastInMeters: new Cesium.Cartesian2(20037508.34, 20037508.34)
                            }),
                            maximumLevel: value.maxlevel,
                            rectangle: rectangle
                        });
                        layerTemp = addAdditionalLayerOption(value.name, currentProvider, 1);
                        layerTemp.show = false;
                        layers.push(layerTemp);
                    })
                });
                selectLayers('-1');
            },
            error: function (e) {
                alert("error:" + e);
            }
        });
    }
    /**
     * show and hide
     * @type {string}
     */

    Array.prototype.indexOf = function (val) {
        for (var i = 0; i < this.length; i++) {
            if (this[i] == val) return i;
        }
        return -1;
    };
    Array.prototype.remove = function (val) {
        var index = this.indexOf(val);
        if (index > -1) {
            this.splice(index, 1);
        }
    };

    var inputLayers = new Array();
    function selectLayers(content) {
        var temp = content;
        console.log('inputLayers.size' + inputLayers);
        var len = inputLayers.length;
        for (var i in inputLayers) {
            if (i == temp) {
                inputLayers.remove(i);
                hideLayer(layers[i]);
                console.log('layers:' + layers[i]);

            }
        }
        if (inputLayers.length == len) {
            inputLayers.push(temp);
            showLayer(layers[temp]);
        }
        console.log(inputLayers);
        //console.log('layers:'+ layers);
    }

    var layerid;

    function selectLayer(layerId) {
        console.log('layerId:' + layerId);
        layerid = layerId;
    }

    function seekBar(progress) {
        //       console.log('lay'+layers[0]);
        console.log('progress' + progress);
        layers[layerid].alpha = progress / 100;
        console.log('alpha' + layers[layerid].alpha);
    }
    function showLayer(layerToshow) {
        layerToshow.show = true;
    }
    function hideLayer(layerToshow) {
        layerToshow.show = false;
    }
    function addAdditionalLayerOption(name, imageryProvider, alpha, show) {
        var layer = imageryLayers.addImageryProvider(imageryProvider);
        layer.alpha = Cesium.defaultValue(alpha, 1);
        layer.show = Cesium.defaultValue(show, true);
        layer.name = name;
        return layer;
    }
    function getPic() {
        document.getElementById("getPic").click();
        console.log("aaaaaa");
    }
    $(document).ready(function () {
        $("#getPic").click(function () {
            cordova.exec(success, fail, "CallBack", "speak", ["url"]);
        });
            var success = function(message){
//                alert("success = "+message);
            };
            var fail = function(message){
//                alert("fail = "+message);
            };

    });
    var myMap = new Map();
    var picId;
    function getPicId(content){
        picId = content;
        console.log("aaaaaa"+picId);
        initPic();
    }
    function initPic() {
        $.ajax({
            url: curWwwPath + "GetEveryPicList.ashx",
            async: true,
            data: {},
            success: function (result) {
                var options = {
                    hash_bookmark: false,
                    scale_factor: 0.25,
                    timenav_height_percentage: 50,
                    slide_default_fade: "10%",
                    zoom_sequence: [, 0.01, 0.25, 0.5, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610],
                    timenav_height_min: 0
                }
                var key=picId;
                console.log("b"+picId);
                console.log("bbb"+key);


                var region = result.events[key].region.split(",");
                var x = (parseFloat(region[0]) + parseFloat(region[2])) / 2;
                var y = (parseFloat(region[1]) + parseFloat(region[3])) / 2;
                camera.flyTo({
                    destination: Cesium.Cartesian3.fromDegrees(x,y,20000)
                });

//                if (!myMap.containsKey(key)) {
                    var provider = new Cesium.UrlTemplateImageryProvider({
                        url: curWwwPath+"GetEveryPicTile.ashx?params={z}/{x}/{y}/"+key,
                        credit: key,
                        tilingScheme: new Cesium.WebMercatorTilingScheme({
                            ellipsoid: Cesium.Ellipsoid.WGS84,
                            rectangleSouthwestInMeters: new Cesium.Cartesian2(-20037508.34, -20037508.34),
                            rectangleNortheastInMeters: new Cesium.Cartesian2(20037508.34, 20037508.34)
                        }),
                        maximumLevel: 18,
                        rectangle: Cesium.Rectangle.fromDegrees(region[0],region[1],region[2],region[3])
                    });
                console.log('result' + key);
                    addAdditionalLayerOption(key, provider, 1);
//                }

            }
        });
    }


    InitialNews(viewer);
    function InitialNews(viewer) {
        var dataSourcePromise = Cesium.GeoJsonDataSource.load(curWwwPath + 'geojson/testGeojson.geojson');
        console.log('ddddd',dataSourcePromise);
        dataSourcePromise.then(function(dataSource) {
            viewer.dataSources.add(dataSource);
            var entities = dataSource.entities.values;
            entities.forEach(function(entity) {
                entity.billboard.image = curWwwPath + 'images/48.ico';
            });
            var pixelRange = 150;
            var minimumClusterSize = 2;
            var enabled = true;
            dataSource.clustering.enabled = enabled;
            dataSource.clustering.pixelRange = pixelRange;
            dataSource.clustering.minimumClusterSize = minimumClusterSize;
            var removeListener;
            var pinBuilder = new Cesium.PinBuilder();
            var pin50 = pinBuilder.fromText('50+', Cesium.Color.RED, 48).toDataURL();
            var pin40 = pinBuilder.fromText('40+', Cesium.Color.ORANGE, 48).toDataURL();
            var pin30 = pinBuilder.fromText('30+', Cesium.Color.CHARTREUSE, 48).toDataURL();
            var pin20 = pinBuilder.fromText('20+', Cesium.Color.GREEN, 48).toDataURL();
            var pin10 = pinBuilder.fromText('10+', Cesium.Color.AQUA, 48).toDataURL();
            var singleDigitPins = new Array(8);
            for (var i = 0; i < singleDigitPins.length; ++i) {
                singleDigitPins[i] = pinBuilder.fromText('' + (i + 2), Cesium.Color.VIOLET, 48).toDataURL();
            }
            customStyle();
            function customStyle() {
                if (Cesium.defined(removeListener)) {
                    removeListener();
                    removeListener = undefined;
                } else {
                    removeListener = dataSource.clustering.clusterEvent.addEventListener(function(clusteredEntities, cluster) {
                        cluster.label.show = false;
                        cluster.billboard.show = true;
                        cluster.billboard.verticalOrigin = Cesium.VerticalOrigin.BOTTOM;
                        if (clusteredEntities.length >= 50) {
                            cluster.billboard.image = pin50;
                        } else if (clusteredEntities.length >= 40) {
                            cluster.billboard.image = pin40;
                        } else if (clusteredEntities.length >= 30) {
                            cluster.billboard.image = pin30;
                        } else if (clusteredEntities.length >= 20) {
                            cluster.billboard.image = pin20;
                        } else if (clusteredEntities.length >= 10) {
                            cluster.billboard.image = pin10;
                        } else {
                            cluster.billboard.image = singleDigitPins[clusteredEntities.length - 2];
                        }
                    });
                }
            }


            var _isPop = false;
            var _isDown = false;
            var pickedLabel;
            var handler = new Cesium.ScreenSpaceEventHandler(viewer.scene.canvas);
            handler.setInputAction(function(movement) {
                var currentpickedLabel = viewer.scene.pick(movement.position);
                if (!Cesium.defined(currentpickedLabel)) {
                    return;
                }
                if (typeof (currentpickedLabel.id) == "undefined") {
                    return;
                }
                var windowposition = Cesium.SceneTransforms.wgs84ToWindowCoordinates(viewer.scene, currentpickedLabel.primitive.position);
                var left = windowposition.x + 20;
                var top = windowposition.y - 20;
                pickedLabel = currentpickedLabel;
                var title =pickedLabel.id._properties.title;
                var newsurl = pickedLabel.id._properties.url;
                console.log('newsurl' + newsurl);
                if (!document.getElementById('pop')) {
//                    var newsdivhtml = "<div id=\"pop\"><div/>";
//                    var newsdiv = $(newsdivhtml).css({ "height": "650px", "width": "500px", "background-color": "white", "left": left + "px", "top": top + "px", "z-index": 200, "position": "absolute" });
//                    newsdiv.appendTo($("#cesiumContainer"));
//                    var titledivhtml = "<h3 id=\"newstitle\">" + title + "</h3>";
//                    var titlediv = $(titledivhtml).css({ "color": "black", "font-size": 13, "display": "inline" }).appendTo($(newsdiv));
//                    var a = "<a>x</a>";
//                    $(a).css({ "float": "right", "margin-right": "5px" }).appendTo(newsdiv);
//                    $("#pop").on('click', "a", function() {
//                        console.log("asdf");
//                        newsdiv.remove();
//                    });
//
//                    var newsiframehtml = "<iframe id=\"newsframe\" src='" + newsurl+ "'></iframe>";
//                    var newsiframe = $(newsiframehtml).css({ "height": "630px", "width": "500px" }).appendTo($(newsdiv));
                    //alert('alert');
                    cordova.exec(success, fail, "CallBack", "speak", [newsurl]);


                } else {
                    $('#newstitle').html(title);
                    $('#newsframe').attr("src", newsurl);
                    $('#pop').css({ "left": left + "px", "top": top + "px" });
                }
                _isPop = true;
                _isDown = true;
            }, Cesium.ScreenSpaceEventType.LEFT_CLICK);


        });
    }
    var success = function(message){
//                alert("success = "+message);
    };
    var fail = function(message){
//                alert("fail = "+message);
    };
