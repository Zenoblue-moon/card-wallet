# 卡包 · CardWallet

一款基于 Material 3 的安卓卡片收纳应用，把银行卡、会员卡和通行证（`.pkpass`）都收进同一个钱包。

## 功能

- **卡片收纳**：上传卡面，保存卡号、有效期、CVV 等敏感信息
- **卡券支持**：导入 `.pkpass`，展示条码 / 二维码等关键信息
- **感应式付款演示**：模拟靠近读卡器付款，短按音量下键触发
- **外观定制**：多套三色主题、动态取色、页面过渡动画、震动反馈
- **备份与恢复**：一键导出 / 导入 ZIP
- **多语言**：简体中文、繁体中文、English，跟随系统语言

## 技术栈

- Kotlin + Jetpack Compose（Material 3）
- MVVM + ViewModel + Kotlin Flow
- Room（卡片 / 卡券本地存储）
- DataStore Preferences（应用设置）
- Navigation Compose（页面导航与过渡动画）
- ZXing（二维码 / 条码解析）
- Coil（图片加载）

## 构建

需要 JDK 17 与 Android SDK 35。

```bash
./gradlew assembleDebug     # 调试包
./gradlew assembleRelease   # Release 包（需自行配置签名）
```

## 界面预览

主页采用卡片平铺布局，点击卡片即可查看卡面，卡片下方统一收纳卡券。

## License

暂未指定。