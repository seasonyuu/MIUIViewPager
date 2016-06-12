MIUIViewPager
==============
[English](./README.markdown)

在页面滑动时实现页面中 Item 动画的 ViewPager。

灵感来自 MIUI 系统中部分系统应用的页面切换效果。

Sample
========
在MIUI系统中的效果

![](./miui_sample.gif)

我的lib实现的效果

![](./library_sample.gif)

引用
=======
在 build.gradle 中加入
```groovy
    compile 'me.seasonyuu.android:miui-viewpager:1.0.0'
```

使用
=====
1. 像添加普通的 ViewPager 一样在 xml 中添加 MIUIViewPager

    ```xml
    <me.seasonyuu.miuiviewpager.MIUIViewPager
        android:id="@+id/view_pager"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
    ```

2.  在 `onCreate` 方法 (或者 `fragment` 的 `onCreateView` 方法), 把想要进行内容动画的 `ViewGroup` 放置到 `SparseArray` 数组中，通过 `setPagerAttach` 连接到 `MIUIViewPager`。

    ```java
    miuiViewPager.setPagerAttach(new MIUIPagerAttachable() {
        @Override
        public SparseArray<ViewGroup> attachViewGroup() {
                ViewGroup[] viewGroups = adapter.getViewGroups(); // Just get your ViewGroup in ViewPager
                SparseArray<ViewGroup> attach = new SparseArray<>();
                for (int i = 0; i < viewGroups.length; i++) {
                        attach.put(i, viewGroups[i]);
                    }
                    return attach;
                }
    });
    ```

自定义
====

| Xml | 方法 | 意义 |
| :----: | :----: | :----: |
| app:type | setType | `type_in`:只做滑入页的动画（默认值）。<br/>`type_out`:只做滑出页的动画。<br/>`type_both`:对所有的页面动画 |
| app:item_offset | setItemOffset | 不同相邻 item 之间的位移差 |
| app:item_offsetFraction | setItemOffsetFraction | 不同相邻 item 之间的位移差相对于页面宽度的百分比。 `ItemOffsetFraction * Width` 则等于itemOffset (默认值为 1/6)。 |

安装 repo 中的 sample 应用可以查看这几种效果分别是如何运作的。

注意
======
* 不推荐使用 GridView 。尽管我对 GridView 做了支持，但是在实际情况下，由于要布局的 item 太多，会出现可能比较严重的性能问题，请尽量使用其他的实现。
* 只做了 RecyclerView 的 LinearLayoutManager 的支持。如果你使用了其他的 LayoutManager，请不要尝试 attach 到 MIUIViewPager，我没办法保证会出现什么状况。

License
====
```
MIUIViewPager library for Android
Copyright (c) 2016 Season Yuu.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
