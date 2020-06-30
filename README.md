# MapGIS Desktop Based on JavaFX

`MapGIS 跨平台桌面`主要为`GIS`专业用户提供空间数据管理和处理、地图制图、空间数据分析等功能。同时也为各种行业应用提供了方便、灵活的二次开发框架，以满足专业GIS桌面应用系统的搭建。

工程包含基于`MapGIS Objects Java`的`MapGIS`跨平台桌面代码。

工程通过`Maven`进行编译，通过`IntelliJ IDEA`进行编辑。

## 目录

- [MapGIS Desktop Based On JavaFX](#mapgis-desktop-based-on-javafx)
    - [目录](#目录)
    - [工程目录结构](#工程目录结构)
    - [开始](#开始)
    - [运行](#运行)
    - [模块](#模块)

## 工程目录结构

```text
|-- mapgis_javafx
   |-- bin                          -- MapGIS Objects Java 和核心模块输出目录及启动脚本
      |-- libs                      -- 模块依赖开源库
      |-- plugin                    -- 插件模块输出目录
   |-- mapgis_dataconvert           -- 数据转换插件
   |-- mapgis_dockfx                -- 框架布局控件库
   |-- mapgis_gdbmanager_plugin     -- 地理数据库管理插件
   |-- mapgis_mapeditor_plugin      -- 地图编辑插件
   |-- mapgis_pluginengine          -- 插件引擎
   |-- mapgis_rastereditor_plugin   -- 栅格编辑插件
   |-- mapgis_ribbonapploader       -- 插件框架宿主(mainClass)
   |-- mapgis_ribbonfx              -- Ribbon功能区控件库
   |-- mapgis_ui_controls           -- MapGIS UI 控件库
   |-- mapgis_workspace_plugin      -- 工作空间插件
|-- .gitignore
|-- pom.xml                         -- 主 pom 文件
|-- README.md
```

## 地址

- **主库**: https://github.com/MapGIS/MapGIS-Desktop-Java
- **码云**: https://gitee.com/osmapgis/MapGIS-Desktop-Java

## 开始

1. 下载安装 `IntelliJ IDEA` [最新版本](https://www.jetbrains.com/idea/download/)
2. 下载安装 `JDK 1.8` [最新版本](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)。
3. 启动`IntelliJ IDEA`，点击`Open or Import`。
4. 选择主工程目录下的`pom.xml`，选择`Open As Project`。
5. 设置工程JDK: `File - Project Structure... `，在 `Project Settings - Project - Project SDK` 下拉选择 `1.8`
6. 编译: 
    - 在右侧 Maven 视窗中，双击 `desktop - Lifecycle - package` 即可编译整个工程。

**注：在编译过过程中，可能需要下载编译插件及工程依赖，请保持机器联网。**

## 运行

- **Runtime Version**
    - MapGIS 运行时版本为 10.3.7.13。

- **Windows**
    1. 将`MapGIS`的运行时目录`Program`配置到系统环境变量中(可能需要重新启动以使得环境变量生效)。
    2. 右键`desktop.bat`以管理员启动。
    
- **Linux**
    1. 将`MapGIS`的运行时目录`Program`配置到环境变量中。
    2. 使用`desktop.sh`启动。

## 模块
1. **框架模块**
    - `mapgis_dockfx`: 框架布局控件库，提供内容视图和停靠窗口的停靠和浮动。
    - `mapgis_ribbonfx`: Ribbon功能区控件库，统筹和管理各类工具按钮。
    - `mapgis_ui_controls`: UI 控件库提供了丰富的`MapGIS 控件`，为二次开发提供便利与指导。
    - `mapgis_pluginengine`: 插件引擎负责解析插件`jar`包，提取`jar`包中的插件类型信息，并提交给宿主程序生成对应的界面对象。插件引擎提供一种通讯契约，即标准插件接口。插件程序集只要实现了这些接口，就能被插件引擎认可为插件。插件引擎提供一个插件容器(`PluginContainer`)，负责管理插件的加载，卸载等状态控制。插件引擎提供一个运行框架(`Application`)，负责管理插件引擎运行状态和与插件程序集间的交互。
    - `mapgis_ribbonapploader`: 宿主程序是框架运行的入口，它通过插件引擎加载插件对象，并将插件对象以 UI 的形式来展示，并负责协调这些插件对象与界面控件间的交互。
    
2. **插件模块**
    - `mapgis_workspace_plugin`: 基础插件。如果要对地图进行编辑、处理或者分析，则此插件将是必备插件。
        - 插件特色: 
            - 以目录树的形式组织地图，提供地图的新建、保存及编辑等基础管理功能；
            - 提供地图数据的二维及三维图形显示窗口，能够无级放大缩小、全方位漫游；
        - 包含的主要控件
            - 工作空间视图: 地图管理的基础视图及操作区，包含了地图的新建、保存及编辑功能；地图下的图层数据的基础信息、属性表格结构及其内容的查阅功能。
            - 地图数据视图: 地图数据的图形显示区及交互操作区，显示当前工作空间视图内图层的图像。分为两种模式：数据视图模式（二维、三维场景）。
            - 文件菜单: 文件菜单提供地图的新建、打开、保存、关闭功能等功能；Ribbon功能区用于操作数据视图。            
    
    - `mapgis_gdbmanager_plugin`:  基础插件。所有对数据库文件的操作（如，创建简单要素类）都将依赖于此插件。该插件提供组织和管理各类地理信息的目录窗口、图形展示窗口。
        - 数据管理插件可以组织和管理的信息类型包括: 
            - 地理数据库
            - 要素数据集
            - 矢量数据
            - 栅格数据
        - 包含的主要控件: 
            - Catalog目录窗口: 以树视图组织各类数据，树节点的右键菜单提供各种编辑、管理功能。
            - 数据属性信息窗口: 显示单个数据的详细信息。
    
    - `mapgis_dataconvert_plugin`: 基础数据转换插件实现了矢量和栅格数据升级、数据迁移、数据交换等强大的数据操作功能。
        - 插件特色: 
            - 支持不同`MapGIS`数据源之间的数据迁移，包括`MapGISLocal`、`ArcGIS`常用格式的数据迁移、复制和转换。
            - 兼容`MapGIS 67`数据，提供`MapGIS 67`数据的升级，以及`K10数据`转换为`67数据`。（Windows）
            - 提供数据转换时的错误检查和自动改错功能，自动消除不符合规范的命名错误；可记录详细转换日志，并提供出错提示。
            - 此外，提供统改数据名称、目的数据类型、目的数据目录的功能，极大的方便了用户操作。
        - 包含主要控件: 
            - Catalog目录树的空间数据节点右键菜单，提供各种数据的导入、导出功能。
            - 要素数据集以及各要素类节点右键菜单，提供导入、导出功能。
            - 栅格数据集和栅格目录节点右键菜单，提供栅格数据的导入、导出功能。
    
    - `mapgis_mapeditor_plugin`: 地图编辑插件适用于制作精美地图，无专业分析需求的用户。它包含了部分编辑矢量数据的功能或工具。
        - 插件特色: 
            - 对地图中的地理实体对象进行增加、删除、移位等；
            - 属性查询、条件查询；
            - 地图投影变换，不同坐标参照的空间数据的处理；
        - 包含的主要控件: 
            - 点/线/区菜单及工具条：点/线/区要素的编辑功能都集中在这三个菜单中。例如，添加、移动、删除、修改等；
            - 通用编辑菜单及工具条：除了集成部分编辑功能外，还包含叠加分析、缓冲区分析、裁剪、查询等功能；
    
    - `mapgis_rastereditor_plugin`: 栅格编辑插件涵盖了栅格信息查询、栅格显示、栅格预处理、栅格分析的基础功能，适用于无专业遥感分析需求的用户。

3. **bin**: 作为依赖包的存放目录及各模块的输出目录，组织各功能模块及其依赖的组织结构。
    - `根目录`: 代码仓库本身包含`MapGIS Objects Java`的最新版本包，同时也是核心模块的打包输出目录，并包含可执行启动脚本。
    - `libs`: 包含各模块中对开源库的直接依赖和间接依赖的拷贝。
    - `plugin`: 插件模块的打包输出目录。




  
TODO:  
- [X] 简介
- [X] 工程目录 
- [X] 地址
- [X] 开始
- [X] 模块
- [X] 启动
- [ ] 架构
- [ ] 性能
- [ ] 环境服务器
- [ ] 文档
- [ ] 帮助
- [ ] 协议
- [ ] 贡献 