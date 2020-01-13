# 提供快速开发库，涵盖了基本的android开发组件    
### 工程目录结构
|--FastDevFramwork   
|---- app演示实例   
|---- audiolibrary 录音库，提供了组件和实例   
|---- databaseutilslibrary 数据库工具，提供了CRUD功能   
|---- ddpushlibrary 开源库，提供了推送消息功能   
|---- devutilslibrary 开发库，提供了常用的开发工具、常用功能封装   
|---- gpslibrary GPS库，提供了实时位置获取   
|---- maplibrary 地图库，提供了地图实例，完成天地图的二次开发   
|---- metrialdialoglibrary dialog库，提供了各类弹窗的封装   
|---- timepicklibrary 日期选择库，提供了各类日期的选择功能   
|---- webutilslibrary http库，提供了实现了http、https请求的封装
|---- wheelviewlibrary 仿ios滚轮选择库   
|---- zxinglibrary 扫一扫库，提供了扫描二维码和条形码功能

### 库地址采用阿里云maven   
```
maven { url 'http://maven.aliyun.com/nexus/content/groups/public/' }
```      
#### 库列表
```
audiolibrary
implementation 'com.hesc.android.library:AudioLibrary:1.0.4-SNAPSHOT'

databaseutilslibrary
implementation 'com.hesc.android.library:DatabaseLibrary:1.0.0-SNAPSHOT'

ddpushlibrary
implementation 'com.hesc.android.library:HescDDPushLibrary:3.0.3-SNAPSHOT'

devutilslibrary
implementation 'com.hesc.android.library:FastDevLibrary:4.1.8-SNAPSHOT'

gpslibrary
implementation 'com.hesc.android.library:HescLibrary_GpsStatus:1.0.10-SNAPSHOT'

maplibrary
implementation 'com.hesc.android.library:MapLibrary:2.3.2-SNAPSHOT'

metrialdialoglibrary
implementation 'com.hesc.android.library:MaterialDialogLibrary:1.0.7-SNAPSHOT'

timepicklibrary
implementation 'com.hesc.android.library:TimePickerDialog:1.0.0-SNAPSHOT'

webutilslibrary
implementation 'com.hesc.android.library:WebUtilsLibrary:1.8.9-SNAPSHOT'

wheelviewlibrary
implementation 'com.hesc.android.library:WheelViewLibrary:1.1.2-SNAPSHOT'

zxinglibrary
implementation 'com.hesc.android.library:QrcodeLibrary:1.0.0-SNAPSHOT'
```                    
   