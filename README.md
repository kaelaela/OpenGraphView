OpenGraphView
===

[![Download](https://api.bintray.com/packages/kaelaela/maven/OpenGraphView/images/download.svg) ](https://bintray.com/kaelaela/maven/OpenGraphView/_latestVersion)
[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

Rich url link view for Android.

![default](art/ogview.gif)![default](art/ogview2.gif)

## Usage

```groovy
compile 'me.kaelaela:OpenGraphView:1.1.0@aar'
```

```java
OpenGraphView ogView = new OpenGraphView();
ogView.loadFrom(url);
```

## Attributes

```xml
<me.kaelaela.opengraphview.OpenGraphView
            android:id="@+id/og_view"
            android:layout_width="match_parent"
            android:layout_height="96dp"
            og:bgColor="@android:color/white"
            og:cornerRadius="4dp"
            og:descTextColor="@color/text_black"
            og:faviconPlaceHolder="@color/light_gray"
            og:imagePlaceHolder="@color/light_gray"
            og:separateImage="true"
            og:strokeColor="@color/light_gray"
            og:titleColor="@color/text_black"
            og:urlTextColor="@color/base_gray"
            og:imagePosition="left|right"
            />
```

| Attribute | Description |
|:---|:---|
| bgColor | Set background color |
| cornerRadius | Set view corner radius |
| imagePlaceHolder | Set image or drawable to image place holder |
| faviconPlaceHolder | Set image or drawable to favicon place holder |
| separateImage | Set image separator visiblity |
| strokeColor | Set stroke color |
| strokeWidth | Set stroke width |
| titleColor | Set og title text color |
| descTextColor | Set og description text color |
| urlTextColor | Set og url text color |
| imagePosition | Set image position to left or right |

## Milestone

- [ ] Custom layout
 - [x] View color
 - [ ] View size
 - [x] Text color
 - [x] Image position
 - [x] Rounded view
- [x] Image cache
- [x] Enable load twitter
- [ ] Load more metadata
- [ ] custom parser

## Me

Yuichi Maekawa(Kaelaela)
* Twitter [@kaelaela31](https://twitter.com/kaelaela31)
* kaelaela.31@gmail.com

##### Inspired

[szk-atmosphere/URLEmbeddedView](https://github.com/szk-atmosphere/URLEmbeddedView)

License
-------
    Copyright (C) 2016 kaelaela
    Copyright (C) 2013 The Android Open Source Project

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
