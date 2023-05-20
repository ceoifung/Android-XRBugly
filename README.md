# XRBugly[![](https://jitpack.io/v/ceoifung/xrbugly.svg)](https://jitpack.io/#ceoifung/xrbugly)
> 软件升级库

## 引用
- 添加依赖
```shell
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	
implementation 'com.github.ceoifung:xrbugly:1.0.1'
```
- AndroidManifest.xml中添加
```xml
<application>
    <provider
        android:name="androidx.core.content.FileProvider"
        android:authorities="${applicationId}.fileProvider"
        android:exported="false"
        android:grantUriPermissions="true">
        <meta-data
            android:name="android.support.FILE_PROVIDER_PATHS"
            android:resource="@xml/file_paths" />
    </provider>
    <activity android:name="com.xiaor.xrbugly.XRBetaActivity"
        android:configChanges="keyboardHidden|orientation|screenSize|locale"
        android:theme="@style/MyTranslucentTheme"/>
</application>
<uses-permission android:name="android.permission.INTERNET" />
<!--请求安装权限-->
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```
android 12以上，需要在application里面添加 `android:requestLegacyExternalStorage="true"`，需要动态申请权限读写权限

## 使用

在自己的application里面注册初始化

```java
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 申请的软件appId
        XRBugly.init(getApplicationContext(), "appid", true);
        // 检查升级的API地址，自动检查升级
        XRBugly.autoUpgrade(getApplicationContext(), "升级地址");
    }
}
```

## APIS

- 自动检查升级

```java
XRBugly.autoUpgrade(getApplicationContext(), "升级地址");
```

- 手动检查升级

```java
// OnDataCallBackListener 请求的回调
checkUpgrade(Context context, String upgradeUrl, OnDataCallBackListener<String> dataCallBackListener) 
```
## 更新日志
### 2023-5-20
- 修复检测到按键按下，重复下载的bug
