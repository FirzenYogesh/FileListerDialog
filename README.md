# FileListerDialog

FileListerDialog helps you to list and pick file/directory. Library is built for Android

## Getting Started

### Installing

To use this library simply import it by placing the following line under dependencies in your app module's build.gradle file

This library is posted in jCenter

```
   dependencies {
      implementation 'yogesh.firzen:FilesLister:1.0.1.f'
   }
```
If any problem occured while importing please add this line to your app module's build. gradle file

```
   repositories {
      maven {url "https://dl.bintray.com/firzenyogesh/maven"}
   }
```

This library has dependencies

```
   dependencies {
      implementation 'com.android.support:appcompat-v7:26.0.0-beta2'
      implementation 'com.android.support:design:26.0.0-beta2'
      implementation 'com.android.support:recyclerview-v7:26.0.0-beta2'
      implementation 'yogesh.firzen:MukkiyaSevaigal:1.0.6'
   }
```

## Usage

After importing the library you can use FileListerDialog to list the files and pick one among them. Simply follow the steps

1. Create an instance of FileListerDialog by using the static method createFileListerDialog()
   
   Default Instance:
   ```   
      FileListerDialog fileListerDialog = FileListerDialog.createFileListerDialog(context);
   ```   
   Instance with a theme for Dialog:
   ```   
      FileListerDialog fileListerDialog = FileListerDialog.createFileListerDialog(context, themeId);
   ```   
2. Set OnFileSelectedListener so that you get what file/ directory has been selected
   ```   
      filelister.setOnFileSelectedListener(new OnFileSelectedListener() {
            @Override
            public void onFileSelected(File file, String path) {
                  //your code here
            }
      });
   ```
3. Set the default directory to load when showing the dialog:
   
   Using a file
   
   ```
      fileListerDialog.setDefaultDir(file);
   ```
   
   Using a file path
   
   ```
      fileListerDialog.setDefaultDir(path);
   ```
4. Set the File Filter type to filter the type of files to be listed:

   ```
      fileListerDialog.setFileFilter(FileListerDialog.FILE_FILTER.ALL_FILES);
   ```
   
   Possible values are:
   
   ```
      FileListerDialog.FILE_FILTER.ALL_FILES
      FileListerDialog.FILE_FILTER.DIRECTORY_ONLY
      FileListerDialog.FILE_FILTER.IMAGE_ONLY
      FileListerDialog.FILE_FILTER.VIDEO_ONLY
      FileListerDialog.FILE_FILTER.AUDIO_ONLY
   ```  


## Screenshots

#### FileListerDialog
![](raw/Screenshot_1500726143.png?raw=true)
![](raw/Screenshot_1500726169.png?raw=true)
#### Create a new folder
![](raw/Screenshot_1500726176.png?raw=true)
![](raw/Screenshot_1500726185.png?raw=true)

#### When a Directory is picked
![](raw/Screenshot_1500726269.png?raw=true)

#### When a file is picked
![](raw/Screenshot_1500726291.png?raw=true)


## Authors

* **Yogesh Sundaresan** -  [GitHub](https://github.com/FirzenYogesh)


## License

```
Copyright 2017 Yogesh Sundaresan

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
