<template>
  <el-container class="dashboard-container">
    <el-header height="60px" class="dashboard-header">
      <div class="header-left">
        <router-link to="/home" class="logo-link">
          <h1>碳足迹追踪平台</h1>
        </router-link>
      </div>
      <div class="header-right">
        <el-dropdown>
          <span class="user-info">
            {{ user.name }}
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="navigateToProfile">个人中心</el-dropdown-item>
              <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <el-container>
      <el-aside width="200px" class="dashboard-aside">
        <el-menu :default-active="activeMenu" class="dashboard-menu" @select="handleMenuSelect">
          <el-menu-item index="/dashboard">
            <el-icon><House /></el-icon>
            <span>仪表盘</span>
          </el-menu-item>
          <el-menu-item index="/transport">
            <el-icon><Van /></el-icon>
            <span>交通排放</span>
          </el-menu-item>
          <el-menu-item index="/diet">
            <el-icon><KnifeFork /></el-icon>
            <span>饮食排放</span>
          </el-menu-item>
          <el-menu-item index="/electricity">
            <el-icon><Lightning /></el-icon>
            <span>用电排放</span>
          </el-menu-item>
          <el-menu-item index="/report">
            <el-icon><DataLine /></el-icon>
            <span>报表展示</span>
          </el-menu-item>
          <el-menu-item index="/recommendations">
            <el-icon><Star /></el-icon>
            <span>减排建议</span>
          </el-menu-item>
          <el-menu-item index="/points">
            <el-icon><CollectionTag /></el-icon>
            <span>减碳积分</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="dashboard-main">
        <h2>个人中心</h2>
        
        <!-- 个人信息卡片 -->
        <el-card class="profile-card" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span>个人信息</span>
              <el-button type="primary" @click="toggleEditMode">
                {{ isEditMode ? '取消' : '编辑' }}
              </el-button>
            </div>
          </template>
          
          <el-form :model="userForm" :rules="rules" ref="userFormRef" label-width="120px">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="userForm.username" :disabled="!isEditMode" />
            </el-form-item>
            <el-form-item label="姓名" prop="name">
              <el-input v-model="userForm.name" :disabled="!isEditMode" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="userForm.email" type="email" :disabled="!isEditMode" />
            </el-form-item>
            <el-form-item v-if="isEditMode" class="form-actions">
              <el-button type="primary" @click="submitForm">保存修改</el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        
        <!-- 账户安全卡片 -->
        <el-card class="security-card" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span>账户安全</span>
            </div>
          </template>
          <div class="security-content">
            <div class="security-item">
              <span class="security-label">修改密码</span>
              <el-button @click="openPasswordDialog">修改</el-button>
            </div>
            <div class="security-item">
              <span class="security-label">绑定手机号</span>
              <el-button>绑定</el-button>
            </div>
          </div>
        </el-card>
      </el-main>
    </el-container>
    
    <!-- 修改密码对话框 -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="120px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="passwordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitPassword">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useCarbonStore } from '../store'
import { House, Van, KnifeFork, Lightning, DataLine, Star, User, ArrowDown, CollectionTag } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { userApi } from '../api'

const router = useRouter()
const carbonStore = useCarbonStore()
const activeMenu = ref('/profile')
const isEditMode = ref(false)
const passwordDialogVisible = ref(false)

// 加载用户信息
onMounted(() => {
  carbonStore.loadUserFromLocalStorage()
  initUserInfo()
})

const user = computed(() => carbonStore.user)

// 用户表单
const userForm = ref({
  username: '',
  name: '',
  email: ''
})

// 密码表单
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const rules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度应在3到20个字符之间', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
})

// 密码验证规则
const passwordRules = reactive({
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: any) => {
        if (value !== passwordForm.value.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

const userFormRef = ref()
const passwordFormRef = ref()

// 初始化用户信息
const initUserInfo = async () => {
  try {
    // 从store中获取用户信息
    if (user.value) {
      userForm.value = {
        username: user.value.username || '',
        name: user.value.name || '',
        email: user.value.email || ''
      }
    }
    // 尝试从后端获取最新用户信息
    // const userInfo = await userApi.getUserInfo()
    // userForm.value = {
    //   username: userInfo.username || '',
    //   name: userInfo.name || '',
    //   email: userInfo.email || ''
    // }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  }
}

// 切换编辑模式
const toggleEditMode = () => {
  isEditMode.value = !isEditMode.value
  if (!isEditMode.value) {
    // 取消编辑，重置表单
    initUserInfo()
  }
}

// 提交表单
const submitForm = async () => {
  if (!userFormRef.value) return
  
  await userFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        // 调用API更新用户信息
        // const updatedUser = await userApi.updateUserInfo({
        //   name: userForm.value.name,
        //   email: userForm.value.email
        // })
        
        // 更新store中的用户信息
        carbonStore.setUser({
          ...user.value,
          name: userForm.value.name,
          email: userForm.value.email
        })
        
        isEditMode.value = false
        ElMessage.success('个人信息更新成功')
      } catch (error) {
        console.error('更新用户信息失败:', error)
        ElMessage.error('更新用户信息失败')
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  initUserInfo()
}

// 打开修改密码对话框
const openPasswordDialog = () => {
  passwordDialogVisible.value = true
  passwordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
}

// 提交密码修改
const submitPassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        // 调用API修改密码
        // await userApi.updatePassword({
        //   oldPassword: passwordForm.value.oldPassword,
        //   newPassword: passwordForm.value.newPassword
        // })
        
        passwordDialogVisible.value = false
        ElMessage.success('密码修改成功')
      } catch (error) {
        console.error('修改密码失败:', error)
        ElMessage.error('修改密码失败')
      }
    }
  })
}

// 导航到个人中心
const navigateToProfile = () => {
  router.push('/profile')
}

// 处理菜单选择
const handleMenuSelect = (key: string) => {
  router.push(key)
  activeMenu.value = key
}

// 处理登出
const handleLogout = () => {
  router.push('/login')
}

// 生命周期钩子
onMounted(() => {
  initUserInfo()
})
</script>

<style scoped>
.dashboard-container {
  min-height: 100vh;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-left .logo-link {
  text-decoration: none;
  color: inherit;
  display: inline-block;
  transition: color 0.3s ease, transform 0.3s ease;
}

.header-left .logo-link:hover {
  color: #4CAF50;
  transform: translateY(-2px);
}

.header-left h1 {
  font-size: 20px;
  margin: 0;
}

.user-info {
  color: white;
  cursor: pointer;
}

.dashboard-aside {
  background-color: #fff;
}

.dashboard-menu {
  height: 100%;
  border-right: none;
}

.dashboard-main {
  padding: 20px;
}

.profile-card {
  max-width: 600px;
}

.security-card {
  max-width: 600px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-actions {
  margin-top: 20px;
}

.security-content {
  padding: 20px 0;
}

.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.security-item:last-child {
  border-bottom: none;
}

.security-label {
  font-size: 14px;
}

@media (max-width: 768px) {
  .dashboard-aside {
    display: none;
  }
  
  .dashboard-main {
    padding: 15px 10px;
  }
  
  .dashboard-header h1 {
    font-size: 16px;
  }
  
  .profile-card,
  .security-card {
    max-width: 100%;
  }
  
  .el-form {
    padding: 0 10px;
  }
  
  .el-form-item {
    margin-bottom: 15px;
  }
}

@media (max-width: 576px) {
  .dashboard-header h1 {
    font-size: 14px;
  }
  
  .el-form {
    padding: 0;
  }
  
  .el-form-item {
    margin-bottom: 10px;
  }
  
  .el-form-item__label {
    font-size: 14px;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .card-header .el-button {
    width: 100%;
  }
}
</style>