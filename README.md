# CircleMenu

小记：<br/>
    在 dribbble 上看到的一个比较有感觉的设计，并不炫酷，但是值得推敲的细节比较多。因为没有比较标准的原型图。在还原设计的时候，来来回回修改好多次。到最后也不知道跟原设计有多少差距。不过自己看着还算满意。

   该库在实现的时候碰到了许多困难。比如选中子菜单项绘制圆环轨迹路径的时候，使用 PathMeasure 发现 getSegment 方法并不是可以截取任何两个位置之间的 Path（因为要从选中的子菜单按钮的位置开始绘制圆环轨迹路径），思考良久后，使用旋转画布的方法巧妙解决。

   又例如，在圆环绘制完成后，圆环会逐渐放大扩散变透明，然后消失的动画。如果这个动画针对的是一个 View对象，我想使用 ObjectAnimator 可以很快解决。但是现在是在 onDraw 中绘制这一动画效果。最困难的是绘制圆环扩散变透明直至消失这一动画效果。（ps:期间还问过我 QQ 中所有技术群。都说很简单，就是没人说具体，群中绝大部分都是各种灌水，到最后也得不到答案。那时候的感觉很糟糕，感觉再也不相信技术群了，伤心···）最后实现很简单，也是无意中发现 ColorUtils 这个类， ColorUtils 是 Support.v4 中提供的，封装了对 Color 的各种操作。我使用了 ColorUtils.setAlphaComponent(color, alpha) 来操作圆环的颜色的透明度，从而达到一个圆环逐渐消失的效果。

   最后谢谢 [Aige](http://blog.csdn.net/aigestudio?viewmode=contents) 和 [GcsSloop](http://www.gcssloop.com/#blog) 两位大神无私奉献。让我能深入学习自定义 View 范畴的知识。让我有能力去完成这个项目。


# Preview

<img src="preview/circle_menu.gif"/>


# Usage

导入 cmlibrary module, 或者直接拷贝 com.hitomi.cmlibrary 包下所有 java 文件到您的项目中

布局文件中：

    <com.hitomi.cmlibrary.CircleMenu
        android:id="@+id/circle_menu"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />

Activity 中：

    circleMenu = (CircleMenu) findViewById(R.id.circle_menu);
    // 设置打开/关闭菜单图标
    circleMenu.setMainIconResource(R.mipmap.icon_menu, R.mipmap.icon_cancel);
    // 设置一组 Resource 格式的子菜单项图
    circleMenu.setSubIconResources(iconResArray);
    // 绑定菜单选择监听器
    circleMenu.setOnMenuSelectedListener(new OnMenuSelectedListener() {
        @Override
        public void onMenuSelected(int index) {
        }
    });
    // 绑定菜单状态改变监听器
    circleMenu.setOnMenuStatusChangeListener(new OnMenuStatusChangeListener() {
        @Override
        public void onMenuOpened() {
        }

        @Override
        public void onMenuClosed() {
        }
    });

# Thanks

   原型设计来源于 [dribbble](https://dribbble.com/shots/2534780-Circle-Menu-Swift-Open-Source)

#Licence

MIT
 


