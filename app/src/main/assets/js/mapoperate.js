idList = [];
idplaceholder = 0;
var mapCoord = {
  x: 0,
  y: 0
};
map.on('loadComplete', parkingCar());

function parkingCar() {
  map.on('mapClickNode', function(event) {
    console.log(event);
    if (event.nodeType == esmap.ESNodeType.NONE || event.nodeType == esmap.ESNodeType.FLOOR) {
      changeColor();
      return;
    }
    var model = event;
    //if(!startPick) return;
    if (event.nodeType != esmap.ESNodeType.MODEL) {
      model.o3d_.flash({
        scale: 1.3,
        time: 0.3
      });
    }
    if (event.nodeType == esmap.ESNodeType.MODEL) {
      map.storeSelect(model);
      var result = confirm("您希望在此停车吗？");
      if (result) {
        var flag = pushId(model.ID);
        if (flag) {
          mapCoord.x = model.hitCoord.x || 0;
          mapCoord.y = model.hitCoord.y || 0;
          setPosIcon(mapCoord.x, mapCoord.y);
        }
        //console.log(global_.idList);
      } else {

      }
      changeColor();
    }
  });
}

function changeColor() {
  for (var roomid in idList) {
    map.changeModelColor({
      id: idList[roomid],
      color: '#FF0000'
    });
  }
}

function pushId(id) {
  for (var roomid in idList) {
    if (id == idList[roomid]) {
      alert("该车位已被占用!");
      return;
    }
  }
  idList.push(id);
  idplaceholder = id;
  return true;
}

function getidplaceholder() {
  return idplaceholder;
}

function setidList(id) {
  for (var roomid in idList) {
    if (id == idList[roomid]) {
      return;
    }
  }
  idList.push(id);
  changeColor();
}

function setPosIconById(id) {
  id = parseInt(id);
  var room = map.findRoomById(id);
  setPosIcon(room.mapCoord.x, room.mapCoord.y, '../image/car.png','caricon','car');
  return "success";
}

function setPosIcon(x_coor, y_coor, imgurl,layername,markname) {
  im = new esmap.ESImageMarker({
    x: x_coor,
    y: y_coor, //如果不添加x和y，则默认坐标在地图中心。
    url: imgurl, //图片标注的图片地址
    size: 64, //图片大小 或者 size:{w:32,h:64},
    spritify: true, //跟随地图缩放变化大小，默认为true，可选参数
    height: 0.5, //距离地面高度
    showLevel: 20, //地图缩放等级达到多少时隐藏,可选参数
    seeThrough: true, //是否可以穿透楼层一直显示,可选参数
    //angle:30,  	//如果设置了就是固定marker角度，与地图一起旋转。(size需要重新设置)
    id: 2017, //id，可自定义
    name: markname //name可自定义
  });
  var floorLayer = map.getFloor(1); //获取第一层的楼层对象
  var layer=floorLayer.getOrCreateLayerByName(layername,esmap.ESLayerType.IMAGE_MARKER);
  layer.addMarker(im); //将imageMarker添加到图层
  //floorLayer.addLayer(layer); //将图层添加到楼层对象
}

function queryPosIconById(id) {
  id = parseInt(id);
  var room = map.findRoomById(id);
  setPosIcon(room.mapCoord.x, room.mapCoord.y, '../image/location.png','posicon','location');
  return "success";
}
