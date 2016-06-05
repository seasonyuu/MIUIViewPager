MIUI ViewPager
==============
A ViewPager performs item animations while scrolling.

Idea from MIUI Rom.

Sample
========
MIUI sample

![](./miui_sample.gif)

My Library sample

![](./library_sample.gif)

Usage
=====
1. Add this MIUIViewPager as normal ViewPager

	```xml
<me.seasonyuu.miuiviewpager.MIUIViewPager
	android:id="@+id/view_pager"
	android:layout_width="match_parent"
	android:layout_height="wrap_content"/>
```

2.  In your `onCreate` method (or `onCreateView` for a fragment), attach the
     ViewGroup you want to animate to the `ViewPager` by using a `SparseArray`.

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

Custom
====

| Xml | Method | Value and meaning |
| --- |
| app:type | setType | `type_in`:Animate the page which will be in.<br/>`type_out`:Animate the page which will be in.<br/>`type_both`:Animate both the in and out page. |
| app:item_offset | setItemOffset | Dimension. Offset of different items. |
| app:item_offsetFraction | setItemOffsetFraction | Fraction. Fraction to calculate offset of different items. `Fraction * Width` will be the item offset.|

To see how these attrs work , please install the sample apk.

TODO
====
* add GridView support

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
