# Git SSH 提交代码全流程指南

使用 SSH 协议提交代码到 GitHub 是最推荐的姿势。它不仅可以免去频繁输入账号密码的繁琐，更能有效绕过国内因为 HTTPS 导致的“连接重置（Connection was reset）”或“超时”等网络障碍。

以下是完整的从零开始配置并使用 SSH 提交代码的步骤：

---

## 步骤一：检查或生成本机的 SSH 密钥

在与 GitHub 通信前，你的电脑需要有一把“钥匙”。

1. 打开你的终端（如 PowerShell、Git Bash 或 CMD）。
2. 输入以下命令生成密钥。如果提示您输入保存路径或密码，**直接一路狂按回车键（Enter）**即可：
   ```bash
   ssh-keygen -t ed25519 -C "你的邮箱@example.com"
   ```
   *(如果你之前已经生成过，它会问你是否覆盖 `Overwrite (y/n)?`，输入 `n` 保留已有密钥即可。)*

---

## 步骤二：查看并复制你的公钥

密钥分为“私钥”（留在你电脑上保护安全）和“公钥”（我们要放到 GitHub 上）。我们需要复制公钥内容。

在终端中执行命令查看公钥：
* **Windows (PowerShell)**:
  ```powershell
  Get-Content ~/.ssh/id_ed25519.pub
  ```
* **Mac/Linux 或 Git Bash**:
  ```bash
  cat ~/.ssh/id_ed25519.pub
  ```

此时终端会输出一段以 `ssh-ed25519` 开头，以你的邮箱结尾的一长串文本。**把这段完整的字母和数字复制下来**。

---

## 步骤三：将公钥添加到 GitHub 账号

1. 登录你的 [GitHub 网页](https://github.com/)。
2. 点击右上角的个人头像，在下拉菜单中点击 **Settings (设置)**。
3. 在左侧菜单栏找到 **SSH and GPG keys** 并点击进去。
4. 点击右上角的绿色按钮 **New SSH key**。
5. 表单填写：
   * **Title**: 随便起个名字来标识这台电脑（比如 `My Windows PC` 或者 `公司电脑`）。
   * **Key type**: 保持默认的 `Authentication Key`。
   * **Key**: 将刚才**复制的长串公钥代码**，原封不动地粘贴在这个大输入框里。
6. 点击绿色的 **Add SSH key** 按钮保存。

---

## 步骤四：测试与 GitHub 的连接

回到你本地的终端，输入以下命令测试配对是否成功：
```bash
ssh -T git@github.com
```

* **注意**：如果是第一次连接，终端会弹出一长串询问类似 `Are you sure you want to continue connecting (yes/no/[fingerprint])?` 的提示。此阶段不要直接按回车，必须**手动打出完整的 `yes` 并敲回车**。
* 如果你看到类似：`Hi XXXXXX225! You've successfully authenticated, but GitHub does not provide shell access.` 的句子，恭喜你，你的钥匙认证彻底打通了！

---

## 步骤五：将本地项目的推送地址切换为 SSH 格式

虽然电脑已经和 GitHub 认识了，但你的项目可能还在用旧的 HTTPS 地址（`https://github.com/...`）尝试推送。我们需要让项目改走 SSH 通道。

在你的代码项目目录中（比如 `D:\cfp`），运行以下命令：
```bash
# 修改远程仓库源的地址，将 URL 替换为 git@github.com:用户名/仓库名.git
git remote set-url origin git@github.com:XXXXXX225/carbon-footprint-tracker.git
```

你可以随时用 `git remote -v` 命令检查，如果输出的结果是 `git@github.com:...` 就说明配置生效了。

---

## 步骤六：日常开发中的黄金提交三连击

通道修好后，以后你每一次写完代码需要保存到线上时，都只需执行经典的三步走：

```bash
# 1. 追踪所有更改过的代码文件 (点 代表当前目录下所有改动)
git add .

# 2. 将改动保存为一次提交（Commit），记得写一句带有意义的更新留言
git commit -m "更新说明：例如修改了 Home.vue 的 UI"

# 3. 将本地提交推送到远程主分支（main 或 master）
git push origin main
```

这样就能纵享丝滑，再也不怕代码无法提交啦！