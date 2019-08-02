# RoundedImageView
Android RoundedImageView widget

> RoundedImageView未上传到Maven仓库,需要使用请clone项目

#### 示例
![image](https://github.com/xiexiang89/RoundedImageView/blob/master/screenshots/screenshots1.png?raw=true=200*300)

#### XML属性

属性名 | 作用
---|---
supportRounded | 是否支持圆角特性,flase为不支持,使用父类的绘制图像
borderColor | 边框颜色
borderSize | 边框大小
borderOverlay | 边框是否覆盖图片, 此功能仅支持改变边框大小可用
isOval | 是否为圆形, 设置圆形时, radius属性无效
roundRadius | 设置全部圆角
roundTopLeftRadius | 设置左上的圆角
roundTopRightRadius | 设置右上的圆角
roundBottomLeftRadius | 设置左下的圆角
roundBottomRightRadius | 设置右下的圆角
maskColor | 设置遮罩颜色, 支持ColorStateList

#### Java方法

方法名 | 作用
--- | --- 
setMaskColor(int color) | 设置遮罩颜色
setMaskColor(ColorStateList color) | 设置遮罩颜色, 多状态颜色
setBorderColor(int color) | 设置边框颜色
setBorderSize(int borderSize) | 设置边框大小
setOval(bool isOval) | 设置是否显示圆形图像
setBorderOverlay(bool borderOverlay) | 设置是否边框覆盖
setCornerRadii(float topLeft, float topRight, float bottomRight, float bottomLeft) | 设置所有圆角
setTopRightRadii(float radii) | 设置左上的圆角
setTopRightRadii(float radii) | 设置右上的圆角
setBottomLeftRadii(float radii) | 设置左下的圆角
setBottomRightRadii(float radii) | 设置右下的圆角