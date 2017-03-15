## 前言
该控件是Google 官方BottomNavigationView的加强版，在此基础上添加了其他一些动画效果，遵循Material Design.
如果你想开发一款带底部导航栏+Fragment/ViewPager的App,通过配置相关信息，你甚至都不用在函数中写一行代码就可以实现炫丽的切换动画。该控件内部实现了切换的逻辑，让开发者们专注于Fragment页面的开发，提高开发效率。

## minSdkVersion 14

## 部分效果
![show.gif](https://github.com/WakeHao/BottomNavigationBar/blob/master/image/test3.gif)



## 基本使用

使用这个控件，只需要简单的几部

- 引入该控件到你的项目中
```
compile 'com.chen.wakehao.library:bottom-navigation-bar:1.0.0'
```

- res/meun/demo_menu.xml：

```
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item
  android:title="首页"
  android:icon="@drawable/ic_home"
  ></item>
    <item
  android:title="发现"
  android:icon="@drawable/ic_t4"
  ></item>
    <item
  android:title="社交"
  android:icon="@drawable/ic_t5"
  ></item>
</menu>
```

- 在你的layout文件中将这个menu引入

```
app:menu="@menu/demo_menu"
```

- 如果想自动集成Fragment/ViewPager切换逻辑

**item**:
```
   <item
  ...
  fragment="com.wakehao.demo.fragment.WeChatHomeFragment"
  ></item>
  <item
  ...
  fragment="Fragment完整包名"
  >
  .
  .
  .
  
``` 
  
**layout**:
```
 <FrameLayout
        android:id="@+id/fragment_container"
        android:layout_width="match_parent"
        android:layout_height="match_parent"></FrameLayout>
<com.wakehao.bar.BottomNavigationBar
    ...
    app:fragmentContainerId="@id/fragment_container"
    ...
    >        

```



用法和BottomNavigationView差不多，但是在此基础上添加了许多其他功能效果

## 自定义设置

### BottomNavigationBar设置

```
<com.wakehao.bar.BottomNavigationBar
  android:id="@+id/bar"
  app:switchMode="shift"
  app:isSlide="true"
  app:menu="@menu/demo_menu_2"
  app:selectedColor="#ffffff"
  app:unSelectedColor="#bbbbbb"
  app:fragmentContainerId="@id/fragment_container"
  app:viewpagerId="@id/viewpager"
  android:layout_alignParentBottom="true"
  android:layout_width="match_parent"
  android:layout_height="wrap_content">
</com.wakehao.bar.BottomNavigationBar>
```




#### app:switchMode
- **still** 点击item不产生动画效果

![still.gif](https://github.com/WakeHao/BottomNavigationBar/blob/master/image/still_icon2.gif)
- **scale** 点击item有缩放效果,默认是**scale**

![scale.gif](https://github.com/WakeHao/BottomNavigationBar/blob/master/image/scale_icon2.gif)
- **shift** 点击item有偏移效果

![shfit.gif](https://github.com/WakeHao/BottomNavigationBar/blob/master/image/shift_color.gif)



#### app:isSlide

默认**false**，设置为true表示可以随着viewPager的滑动item有渐变效果。所以设置为true需要提供viewPagerId以及icon2

- isSlide=true:随着viewPager的滑动而不断改变图片文字背景色等...

![diff_slide.gif](https://github.com/WakeHao/BottomNavigationBar/blob/master/image/diff_slide.gif)

- isSlide=false:

![diff_notslide.gif](https://github.com/WakeHao/BottomNavigationBar/blob/master/image/diff_notslide.gif)



#### app:menu
将res/meun目录下的xml文件中的item填充到该视图中

#### app:selectedColor
被选中的item图片和文字的颜色，默认是colorPrimary

#### app:unSelectedColor
未被选中item图片和文字的颜色，默认是Color.GRAY

#### app:fragmentContainerId
指定存放fragment的容器，配合item里的设置可以自动实现bar点击切换相应的fragment

#### app:viewpagerId
指定存放viewpager的容器，配合item里的设置可以自动实现bar和viewpager之间的联动


## item设置
```
<item
  android:id="@+id/test_1"
  android:title="@string/home"
  android:icon="@mipmap/home_normal"
  icon2="@mipmap/home_selected"
  shiftedColor="#258555"
 fragment="com.wakehao.demo.fragment.WeChatHomeFragment"
  ></item>
```

#### android:title
必须设置，显示的文字
#### android:icon
必须设置，显示的图片
#### icon2
可选设置，如果设置了点击显示**icon2**，未选中显示**android:icon**;如果未设置，选中和未选中都是**android:icon**,变化的只是图片的颜色
#### shiftedColor
可选设置，表示点击背景蔓延的水纹效果的颜色
![scale_color.gif](https://github.com/WakeHao/BottomNavigationBar/blob/master/image/scale_color.gif)

#### fragment
可选设置，指定点击该item时显示的fragment，需要提供完整包名。且需要指定**app:fragmentContainerId**或**app:viewpagerId**
- app:fragmentContainerId
实现bar点击切换相应的fragment
- app:viewpagerId
实现bar和viewpager之间的联动

## 代码设置

####  bar.showNum(position,num);
- num<0
显示小红点，不可拖拽
- 99>=num>0
显示1到99的数字，可拖拽
- num>99
显示99+，可拖拽

```
bar.showNum(0,80);
bar.showNum(1,100);
bar.showNum(2,-2);
```
![](https://github.com/WakeHao/BottomNavigationBar/blob/master/image/6BEE85BE6A2B4C769692F4629BDEBEFF.jpg)


#### bar.disMissNum(position)
指定位置的小红点消失

#### bar.setOnNavigationItemSelectedListener()
监听点击事件
```
bar.setOnNavigationItemSelectedListener(new BottomNavigationBar.OnNavigationItemSelectedListener() {
    @Override
  public boolean onNavigationItemSelected(@NonNull BottomNavigationItem item, int selectedPosition) {
        if(selectedPosition==2){
            //返回值为false表示不可点击
  return false;
        }
        return true;
    }
    @Override
  public void onNavigationItemSelectedAgain(@NonNull BottomNavigationItem item, int reSelectedPosition) {
        //reSelectedPosition 表示已选中之后再次点击该位置
  Toast.makeText(MainActivity.this,"you click it again on item :"+reSelectedPosition,Toast.LENGTH_SHORT).show();
    }
});
```

#### bar.setTitle(position,title)

动态设置position位置的item的标题

#### bar.setItemSelected(position,true);
通过代码设置position位置item选中,true表示执行动画(shfit,scale),false不执行.



#### bar.showBar()/bar.hideBar()
Slide(滑动) 显示/隐藏bar

#### bar.showBar(1)/bar.hideBar(1)
Fade(渐隐) 显示/隐藏bar


#### bar.getFragment(position)
获取相应位置的Fragment实例


#### bar.getViewPager()
获取ViewPager实例
