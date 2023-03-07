
# SSO流程备忘
### 1. 简述
SSO单点登录系统，方便各个系统统一登录。(部分功能不健全)

---
访问login接口，生成TGT，10分钟有效期，防止界面长时间停留的安全问题
然后输入用户名，开始接收验证码登录
登录通过后，会根据login接口传过来的回调地址回调源系统登录接口，此步骤返回生成的code,10分钟有效期（安全性）,并在本系统绑定用户与tgt的信息(2h)
源系统接收到code后，调用oss系统获取access_token ，此时oss系统会验证code的有效性、app、秘钥信息（安全性）
oss 在接收到code等一系列信息后，会根据code判断有效，然后有效的数据会被移除（有效性一次）
然后根据code获取tgt,根据tgt获取用户信息（id,name）
最后都符合要求后，生成access_token(1h)
### 2. 具体流程
1. 流程简述
系统打开初期，由当前系统判断是否有已登录用户session信息，再决定是否跳转SSO系统。前端localStorage是否存有user,sessionId信息。
 `1.` 
### 3. 后续升级
部分流程不完整，后续完整流程

### 4.涉及技术
1. Springboot
2. Guava
3. Jackson
4. Commons Collections
5. Mysql
6. Shiro
7. Maven
