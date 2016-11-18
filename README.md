# CircleMenu

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-CircleMenu-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/4631)
[![](https://jitpack.io/v/Hitomis/CircleMenu.svg)](https://jitpack.io/#Hitomis/CircleMenu)

CircleMenu 是一个精美别致支持定制的圆形菜单，可以有 0 到 8 个子菜单按钮，按钮背景色，图标都可以修改。CircleMenu 比较适合放在屏幕中间，以得到完好的展现。

小记：<br/>

   CircleMenu 是在 dribbble 上看到的一个比较有感觉的设计，并不炫酷，但是 UI 很精致，值得推敲的细节比较多。因为没有比较标准的原型图。在还原设计的时候，来来回回修改好多次。到最后也不知道跟原设计有多少差距。不过自己看着还算满意。

   该库在实现的时候碰到了许多困难。比如选中子菜单项绘制圆环轨迹路径的时候，使用 PathMeasure 发现 getSegment 方法并不是可以截取任何两个位置之间的 Path（因为要从选中的子菜单按钮的位置开始绘制圆环轨迹路径），思考良久后，使用旋转画布的方法巧妙解决。

   又例如，在圆环绘制完成后，圆环会逐渐放大扩散变透明，然后消失的动画。如果这个动画针对的是一个 View 对象，我想使用 ObjectAnimator 可以很快解决。但是现在是在 onDraw 中绘制这一动画效果。最困难的是绘制圆环扩散变透明直至消失这一动画效果。（ps:期间还问过我 QQ 中所有技术群。都说很简单，就是没人说具体，群中绝大部分都是各种灌水，到最后也得不到答案。那时候的感觉很糟糕，感觉再也不相信技术群了，伤心···）最后实现很简单，也是无意中发现 ColorUtils 这个类， ColorUtils 是 Support.v4 中提供的，封装了对 Color 的各种操作。我使用了 ColorUtils.setAlphaComponent(color, alpha) 来操作圆环的颜色的透明度，从而达到一个圆环逐渐消失的效果。

   最后谢谢 [Aige](http://blog.csdn.net/aigestudio?viewmode=contents) 和 [GcsSloop](http://www.gcssloop.com/#blog) 两位大神无私奉献。让我能深入学习自定义 View 范畴的知识。让我有能力去完成这个项目。


# Preview

<img src="preview/circle_menu.gif"/>


# Import

### Gradle

Step 1. Add the JitPack repository to your build file

	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
   
Step 2. Add the dependency

	dependencies {
	        compile 'com.github.Hitomis:CircleMenu:v1.0.2'
	}
   
### Maven
   
Step 1. Add the JitPack repository to your build file

	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
   
Step 2. Add the dependency

	<dependency>
	    <groupId>com.github.Hitomis</groupId>
	    <artifactId>CircleMenu</artifactId>
	    <version>v1.0.2</version>
	</dependency>
   
# Usage

布局文件中：

    <com.hitomi.cmlibrary.CircleMenu
        android:id="@+id/circle_menu"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />

Activity 中：

    circleMenu = (CircleMenu) findViewById(R.id.circle_menu);

    circleMenu.setMainMenu(Color.parseColor("#CDCDCD"), R.mipmap.icon_menu, R.mipmap.icon_cancel)
            .addSubMenu(Color.parseColor("#258CFF"), R.mipmap.icon_home)
            .addSubMenu(Color.parseColor("#30A400"), R.mipmap.icon_search)
            .addSubMenu(Color.parseColor("#FF4B32"), R.mipmap.icon_notify)
            .addSubMenu(Color.parseColor("#8A39FF"), R.mipmap.icon_setting)
            .addSubMenu(Color.parseColor("#FF6A00"), R.mipmap.icon_gps)
            .setOnMenuSelectedListener(new OnMenuSelectedListener() {

                @Override
                public void onMenuSelected(int index) {}

            }).setOnMenuStatusChangeListener(new OnMenuStatusChangeListener() {

                @Override
                public void onMenuOpened() {}

                @Override
                public void onMenuClosed() {}

            });

# Method

| 方法 | 说明 |
| :--: | :--: |
| setMainMenu | 设置主按钮(打开/关闭)的背景色，以及打开/关闭的图标。图标支持 Resource、Bitmap、Drawable 形式 |
| addSubMenu | 添加一个子菜单项，包括子菜单的背景色以及图标 。图标支持 Resource、Bitmap、Drawable 形式|
| openMenu | 打开菜单 |
| closeMenu | 关闭菜单 |
| isOpened | 菜单是否打开，返回 boolean 值 |
| setOnMenuSelectedListener | 设置选中子菜单项的监听器，回调方法会传递当前点击子菜单项的下标值，从 0 开始计算 |
| setOnMenuStatusChangeListener | 设置 CircleMenu 行为状态监听器，onMenuOpened 为菜单打开后的回调方法，onMenuClosed 为菜单关闭后的回调方法 |


# Thanks

   原型设计来源于 [dribbble](https://dribbble.com/shots/2534780-Circle-Menu-Swift-Open-Source)<br/>
   IOS 版本 [Ramotion](https://github.com/Ramotion/circle-menu)

#Licence

      Copyright 2016 Hitomis, Inc.

      Licensed under the Apache License, Version 2.0 (the "License");
      you may not use this file except in compliance with the License.
      You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

      Unless required by applicable law or agreed to in writing, software
      distributed under the License is distributed on an "AS IS" BASIS,
      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
      See the License for the specific language governing permissions and
      limitations under the License.
 


