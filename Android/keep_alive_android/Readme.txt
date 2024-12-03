配置开发环境：

    需要使用 Android Studio，并且配置安装 Android SDK
    还有 java 环境

    如果本地有配置 gradle 的全局代理环境，则无需进行任何改动
    
    如果本地有代理，则在 全局 C:\Users\{当前电脑的用户}\.gradle 目录下修改 gradle.properties 文件
    添加如下代码：
    systemProp.http.proxyHost=代理服务的ip
    systemProp.http.proxyPort=代理服务的端口号
    systemProp.https.proxyHost=代理服务的ip
    systemProp.https.proxyPort=代理服务的端口号
    或者尝试在 当前项目所在目录下的 gradle.properties 文件中增加上述代码
    
    如果本地没有开启代理服务，则需要在 当前项目所在目录的 build.gradle 文件中屏蔽如下代码：
    mavenCentral()
    maven { url 'https://maven.google.com' }
    两处代码都需屏蔽，无需提交该文件的改动，防止在其他人那边环境不对

    Keep_Alive 项目的相关依赖配置文件为 Keep_Alive 项目目录下的 build.gradle，而非根目录下的 build.gradle，如需添加依赖请在这个文件下添加

打包 Keep_Alive.aar 文件

    1. 只用设置一次(必做)
    Android Studio -> File -> Setting -> Experimental 中设置：
        勾选 Configure all Gradle tasks during Gradle Sync (this can make Gradle Sync slower)
        取消勾选 Enable parallel Gradle Sync

    2. 然后 点击 Sync Project with Gradle Files 按钮
    3. 然后打开 Gradle 面板，点击 Keep_Alive 项目 build -> assemble
    4. 然后打开 项目面板 的 Keep_Alive 项目下的 build -> outputs -> aar 文件夹

    打包 .aar 文件流程可参考 https://blog.csdn.net/qq_33210042/article/details/105863339
    Keep_Alive 项目的 AndroidManifest.xml 文件 和 build.gradle 文件是已经配置好的无需进行修改

相关文件作用：
    .\Keep_Alive\src\main\java\com\example\keep\keepAlive.java   Uniapp 原生插件的实现类，用以启动保活任务，通知栏显示

    .\Keep_Alive\src\main\java\com\example\keep\service\ForegroundService.java   通知栏显示，以及点击返回软件
    .\Keep_Alive\src\main\java\com\example\keep\service\KeepAliveJobService.java  保活服务的实现类

    .\Keep_Alive\src\main\java\com\example\keep\activity\OnePxActivity.java   一像素大小的页面

    .\Keep_Alive\src\main\java\com\example\keep\constant\KeepAlive.java  提供一个常量，在安卓系统大于等于 21 小于 26 时，启动通知栏通知使用

    .\Keep_Alive\src\main\java\com\example\keep\receiver\OnePxReceiver.java   开启或关闭一像素屏幕

    .\Keep_Alive\src\main\java\com\example\keep\utils\util.java  提供判断保活服务是否启动的方法

    .\Keep_Alive\src\main\java\com\example\keep\worker\KeepLiveWork.java  启动保活服务的任务