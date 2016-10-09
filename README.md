# CircleMenu



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
    设计来源于 https://dribbble.com/shots/2534780-Circle-Menu-Swift-Open-Source

#Licence

MIT
 


