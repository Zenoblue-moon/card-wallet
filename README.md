<div align="center">

# 卡包 · CardWallet

一款基于 **Material 3** 的安卓卡片收纳应用，把银行卡、会员卡与通行证统一收进一个数字钱包。

[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Compose-Material%203-4285F4?style=flat&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![minSdk 26](https://img.shields.io/badge/minSdk-26-34A853?style=flat&logo=android&logoColor=white)](https://developer.android.com)
[![Release](https://img.shields.io/github/v/release/Zenoblue-moon/card-wallet?style=flat)](https://github.com/Zenoblue-moon/card-wallet/releases)
[![下载 APK](https://img.shields.io/badge/APK-Download-007EC6?style=flat)](https://github.com/Zenoblue-moon/card-wallet/releases/latest)

</div>

---

## 功能特性

| 分类 | 说明 |
| --- | --- |
| 卡片收纳 | 上传卡面，保存卡号、有效期、CVV 等敏感信息 |
| 卡券支持 | 导入 `.pkpass`，展示条码 / 二维码等关键信息 |
| 感应式付款演示 | 模拟靠近读卡器付款，短按音量下键触发 |
| 外观定制 | 多套三色主题、动态取色、页面过渡动画、震动反馈 |
| 数据管理 | 卡片手动排序、一键备份 / 恢复 ZIP |
| 多语言 | 简体中文、繁体中文、English，跟随系统语言 |

## 技术栈

- **语言**：Kotlin
- **UI**：Jetpack Compose + Material 3
- **架构**：MVVM + ViewModel + Kotlin Flow
- **存储**：Room（卡片 / 卡券）、DataStore Preferences（设置）
- **导航**：Navigation Compose + 过渡动画
- **其他**：ZXing（条码 / 二维码）、Coil（图片加载）

## 构建

需要 JDK 17 与 Android SDK 35。

```bash
git clone https://github.com/Zenoblue-moon/card-wallet.git
cd card-wallet

./gradlew assembleDebug      # 调试包
./gradlew assembleRelease    # Release 包（需自行配置签名）
```

## 项目结构

```
app/src/main/java/com/example/cardwallet/
├── data/          # Room 数据库、仓库层
├── ui/
│   ├── about/     # 关于页
│   ├── components/# 通用组件（卡面等）
│   ├── detail/    # 卡片详情
│   ├── edit/      # 新增 / 编辑卡片
│   ├── home/      # 主页
│   ├── navigation/# 导航与过渡动画
│   ├── pass/      # 卡券详情
│   ├── payment/   # 感应式付款演示
│   ├── settings/  # 设置
│   ├── sort/      # 卡片排序
│   └── theme/     # 主题与配色
└── util/          # 工具类
```

## 下载

最新安装包见 [Releases](https://github.com/Zenoblue-moon/card-wallet/releases)。

## License

暂未指定。