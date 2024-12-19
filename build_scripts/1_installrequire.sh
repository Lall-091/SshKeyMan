#!/bin/bash

#####################
# usage: ./thisscript.sh [repo_path]
# the repo path used for locate the `local.properties` file,
#  this script will append cmake.dir into it,
#  if not specify, will use $GITHUB_WORKSPACE,
#  if $GITHUB_WORKSPACE is empty, will use `/home/runner/work/PuppyGit/PuppyGit`, it is the actually value of $GITHUB_WORKSPACE
#####################


# the 'a82p' just for avoid name conflict with exist file
export build_root=$HOME/app_workspace_a82p
mkdir -p $build_root
cd $build_root

echo "Downloading: Android SDK"
# ANDROID_HOME is android sdk root, is sdk root, not ndk root
export TOOLS=$build_root/tools
export ANDROID_CMD_TOOLS=$TOOLS/android-cmdline-tools
export ANDROID_HOME=$build_root/android-sdk
export CMAKE_VERSION=3.31.1
export NDK_VERSION=26.3.11579264
# 创建目标目录以及依赖目录，虽然后面会删一下cmd tools目录，但这个创建依然是有意义的，意义在于创建依赖目录，所以即使后面会删除cmd tools目录，这条命令也依然应该保留
mkdir -p $ANDROID_CMD_TOOLS
mkdir -p $ANDROID_HOME
curl -L -o cmdline-tools.zip https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
# 删一下，确保mv后目录结构不变
rm -rf $ANDROID_CMD_TOOLS
# 解压然后移动，确保先删后解压移动，以免当解压后的目录和移动的目标目录同名时误删
unzip -q cmdline-tools.zip -d $TOOLS/
mv $TOOLS/cmdline-tools $ANDROID_CMD_TOOLS
export ANDROID_SDKMANAGER=$ANDROID_CMD_TOOLS/bin/sdkmanager
chmod +x $ANDROID_SDKMANAGER

# 随便装个东西，不然后面gradle会接受协议失败
yes | $ANDROID_SDKMANAGER --install "cmdline-tools;17.0" --sdk_root=$ANDROID_HOME
ls -R $ANDROID_HOME
# ANDROID_SDKMANAGER=$ANDROID_HOME/cmdline-tools/17.0/sdkmanager
# chmod +x $ANDROID_SDKMANAGER
# echo "Accept all android sdk licenses to avoid failed by license not accepted"
# yes | $ANDROID_SDKMANAGER --licenses

echo "set ‘sdk.dir’ to ‘local.properties’ for gradle"
# 设置 sdk.dir 以使 github workflow 使用gradle构建apk的时候能找到我们指定版本的cmake和ndk
# -e是为了能输出换行符\n
# 由于izzydroid 用的gitlab，里面没有$GITHUB_WORKSPACE这个变量，所以用实际变量值替换了，不然路径找不到，会有bug
# echo -e "\ncmake.dir=$CMAKE_DIR" >> $GITHUB_WORKSPACE/local.properties
# if specified repo path, use it, else try use $GITHUB_WORKSPACE, if it doesn't exist, will use a literal path
REPO_PATH=${1:-$GITHUB_WORKSPACE}
REPO_PATH=${REPO_PATH:-/home/runner/work/SshKeyMan/SshKeyMan}
LOCAL_PROPERTIES_PATH=$REPO_PATH/local.properties
echo -e "\nsdk.dir=$ANDROID_HOME" >> $LOCAL_PROPERTIES_PATH
echo "local.properties at: $LOCAL_PROPERTIES_PATH"
echo "cat local.properties:"
cat $LOCAL_PROPERTIES_PATH


echo "Installation complete"
