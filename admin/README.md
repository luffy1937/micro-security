# Angular SpringBoot 集成
####　1. plugin
导入并build，会下载并解压
```xml
<plugin>
                <groupId>com.github.eirslett</groupId>
                <artifactId>frontend-maven-plugin</artifactId>
                <version>1.8.0</version>
                <configuration>
                    <!--<workingDirectory>v10.16.3</workingDirectory>-->
                    <nodeVersion>v14.7.0</nodeVersion>
                </configuration>
                <executions>
                    <execution>
                        <!--<phase>prepare-package</phase>-->
                        <id>install node and npm</id>
                        <goals>
                            <goal>install-node-and-npm</goal>
                        </goals>
                        <!--<configuration>
                            <nodeVersion>v8.12.0</nodeVersion>
                            <npmVersion>6.4.1</npmVersion>
                        </configuration>-->
                    </execution>
                    <execution>
                       <!-- <phase>prepare-package</phase>-->
                        <id>npm install</id>
                        <goals>
                            <goal>npm</goal>
                        </goals>
                        <!--<configuration>
                            <arguments>install</arguments>
                        </configuration>-->
                    </execution>
                    <execution>
                        <!-- <phase>prepare-package</phase>-->
                        <id>npm-build</id>
                        <goals>
                            <goal>npm</goal>
                        </goals>
                        <configuration>
                            <arguments>run-script build</arguments>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
```
#### 2. npm
src同级目录npm软连接
```bash
#!/bin/sh
cd $(dirname $0)
PATH="$PWD/node/":$PATH
node "node/node_modules/npm/bin/npm-cli.js" "$@"
```
安装angular cli
`./npm install @angular/cli`

### 3. ng
src同级目录ng软连接
```
#!/bin/sh
cd $(dirname $0)
PATH="$PWD/node/":$PATH
node_modules/@angular/cli/bin/ng  "$@"
```
查看ng版本
`./ng -version`
ng创建angular项目
`./ng new angularProjectName`
将augularProjectName目录下的所有文件放放到根目录，可以删掉angularProjectName目录
#### 4. bootstrap 样式
`./npm install bootstrap@3 jquery --save`
src/styles.css文件中添加
`@import "~bootstrap/dist/css/bootstrap.css";`
####5. 主要文件
`src/app/app.module.ts` 主要的import

`src/app/app.component.html` 模板

`src/app/app.compoment.ts` 动态

