# 注意事项
## 1.使用idea的build进行构建
### pom文件已经引用本地依赖，使用idea的build进行构建要容易，但需要手动拷贝config.xml configuration.properties 到输出jar包相同目录的config文件夹内

## 2.使用maven直接构建
### 目录libs下的jar包没法一起打包，需要适当修改放入maven的repository中
1、先把待引入的jar包放在一个目录下，需要改一下包名，ojdbc6.jar修改成ojdbc6-1.0.jar，在命令行CD到libs目录，执行以下命令:

    mvn install:install-file -Dfile=beautyeye_Inf-1.0.jar -DgroupId=beautyeye_Inf -DartifactId=beautyeye_Inf -Dversion=1.0 -Dpackaging=jar  
    mvn install:install-file -Dfile=ojdbc6-1.0.jar -DgroupId=ojdbc6 -DartifactId=ojdbc6 -Dversion=1.0 -Dpackaging=jar  

2、修改项目pom文件加入包对应的依赖

##