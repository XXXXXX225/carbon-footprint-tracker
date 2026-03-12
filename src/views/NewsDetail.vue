<template>
  <div class="news-detail-container">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="navbar-container">
        <div class="navbar-logo">
          <router-link to="/home" class="logo-link">
            <h1>碳足迹追踪平台</h1>
          </router-link>
        </div>
        <div class="navbar-links">
          <template v-if="user && user.id">
            <div class="user-dropdown">
              <el-dropdown @command="handleUserCommand">
                <span class="user-info">
                  <el-avatar :size="32" :icon="UserFilled" />
                  <span class="user-name">{{ user.name }}</span>
                  <el-icon class="el-icon--right">
                    <ArrowDown />
                  </el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="dashboard">
                      <el-icon><House /></el-icon>
                      仪表盘
                    </el-dropdown-item>
                    <el-dropdown-item command="profile">
                      <el-icon><User /></el-icon>
                      个人中心
                    </el-dropdown-item>
                    <el-dropdown-item command="points">
                      <el-icon><CollectionTag /></el-icon>
                      减碳积分
                    </el-dropdown-item>
                    <el-dropdown-item v-if="user.role === 'ADMIN'" command="admin">
                      <el-icon><Setting /></el-icon>
                      管理员
                    </el-dropdown-item>
                    <el-dropdown-item divided command="logout">
                      <el-icon><SwitchButton /></el-icon>
                      退出登录
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
          <template v-else>
            <router-link to="/login" class="btn btn-primary">登录/注册</router-link>
          </template>
        </div>
      </div>
    </nav>

    <!-- 返回按钮 -->
    <div class="back-section">
      <div class="back-container">
        <router-link to="/news" class="back-link">
          <el-icon><ArrowLeft /></el-icon>
          返回资讯列表
        </router-link>
      </div>
    </div>

    <!-- 新闻详情内容 -->
    <article class="news-article" v-if="currentNews">
      <div class="article-container">
        <!-- 文章头部 -->
        <header class="article-header">
          <div class="article-meta">
            <span class="article-category">{{ currentNews.category }}</span>
            <span class="article-date">{{ currentNews.date }}</span>
          </div>
          <h1 class="article-title">{{ currentNews.title }}</h1>
          <p class="article-excerpt">{{ currentNews.excerpt }}</p>
        </header>

        <!-- 文章主图 -->
        <div class="article-hero-image">
          <img :src="currentNews.image" :alt="currentNews.title" />
        </div>

        <!-- 文章内容 -->
        <div class="article-content">
          <div class="content-body" v-html="currentNews.content"></div>
        </div>

        <!-- 文章标签 -->
        <div class="article-tags">
          <span class="tag" v-for="(tag, index) in currentNews.tags" :key="index">{{ tag }}</span>
        </div>

        <!-- 分享按钮 -->
        <div class="article-share">
          <h4>分享这篇文章</h4>
          <div class="share-buttons">
            <button class="share-btn wechat" @click="shareToWechat">
              <el-icon><ChatDotRound /></el-icon>
              微信
            </button>
            <button class="share-btn weibo" @click="shareToWeibo">
              <el-icon><Share /></el-icon>
              微博
            </button>
            <button class="share-btn link" @click="copyLink">
              <el-icon><Link /></el-icon>
              复制链接
            </button>
          </div>
        </div>
      </div>
    </article>

    <!-- 相关新闻推荐 -->
    <section class="related-news-section" v-if="relatedNews.length > 0">
      <div class="related-news-container">
        <h3>相关资讯</h3>
        <div class="related-news-grid">
          <div class="related-news-card" v-for="(news, index) in relatedNews" :key="index" @click="goToNews(news.id)">
            <div class="related-news-image">
              <img :src="news.image" :alt="news.title" />
            </div>
            <div class="related-news-content">
              <span class="related-news-category">{{ news.category }}</span>
              <h4 class="related-news-title">{{ news.title }}</h4>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-container">
        <div class="footer-info">
          <h4>碳足迹追踪平台</h4>
          <p>致力于帮助个人和企业了解并减少碳排放，为环保事业贡献力量。</p>
        </div>
        <div class="footer-links">
          <router-link to="/">首页</router-link>
          <router-link to="/dashboard">仪表盘</router-link>
          <router-link to="/news">资讯中心</router-link>
          <router-link to="/recommendations">减排建议</router-link>
          <router-link to="/login">登录/注册</router-link>
        </div>
        <div class="footer-contact">
          <p>联系我们: contact@carbonfootprint.com</p>
          <p>© 2026 碳足迹追踪平台. 保留所有权利.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useCarbonStore } from '../store'
import { UserFilled, User, House, CollectionTag, SwitchButton, ArrowDown, Setting, ArrowLeft, ChatDotRound, Share, Link } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const carbonStore = useCarbonStore()

// 用户信息
const user = computed(() => carbonStore.user)

// 处理用户下拉菜单命令
const handleUserCommand = (command: string) => {
  switch (command) {
    case 'dashboard':
      router.push('/dashboard')
      break
    case 'profile':
      router.push('/profile')
      break
    case 'points':
      router.push('/points')
      break
    case 'admin':
      router.push('/admin')
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  carbonStore.user = {
    id: '',
    name: ''
  }
  ElMessage.success('已退出登录')
}

// 所有新闻数据
const allNews = ref([
  {
    id: 1,
    title: '全球碳排放创新低，环保政策成效显著',
    excerpt: '根据最新数据，全球碳排放在过去一年创新低，这主要归功于各国政府采取的积极环保政策和可再生能源的广泛应用。',
    content: `
      <p>根据国际能源署（IEA）最新发布的报告，全球碳排放在过去一年中创下新低，这是全球环保事业的重要里程碑。报告显示，2025年全球二氧化碳排放量同比下降了5.2%，这是自工业革命以来最大的年度降幅。</p>
      
      <h3>政策驱动减排成效显著</h3>
      <p>这一显著成果主要归功于各国政府采取的一系列积极环保政策。欧盟的碳边境调节机制、中国的碳达峰碳中和目标、美国的清洁能源计划等政策都在发挥重要作用。特别是可再生能源的快速发展，太阳能和风能装机容量创历史新高，为全球减排做出了巨大贡献。</p>
      
      <h3>技术创新推动绿色转型</h3>
      <p>除了政策支持，技术创新也是减排的重要推动力。新型太阳能电池效率突破26%，海上风电成本下降40%，电动汽车续航里程大幅提升，这些技术进步让清洁能源变得更加经济实用。</p>
      
      <h3>未来展望</h3>
      <p>专家预测，如果各国继续保持当前的减排势头，全球有望在2030年前实现碳达峰，2050年前实现碳中和。但这需要全球各国加强合作，共同应对气候变化挑战。</p>
    `,
    date: '2026-03-05',
    category: '全球趋势',
    image: 'https://images.unsplash.com/photo-1466611653911-95081537e5b7?w=1200&q=80',
    tags: ['碳排放', '环保政策', '可再生能源', '全球趋势']
  },
  {
    id: 2,
    title: '新能源汽车销量突破历史新高',
    excerpt: '2025年全球新能源汽车销量突破1000万辆，同比增长35%，成为减少交通领域碳排放的重要力量。',
    content: `
      <p>2025年全球新能源汽车市场迎来爆发式增长，全年销量突破1000万辆大关，同比增长35%，创下历史新高。这一数据标志着交通领域的绿色转型正在加速推进。</p>
      
      <h3>中国市场领跑全球</h3>
      <p>中国继续领跑全球新能源汽车市场，销量占全球总量的60%以上。比亚迪、特斯拉、蔚来等品牌表现亮眼，产品线不断丰富，从经济型到豪华型全覆盖。充电基础设施的完善也是销量增长的重要支撑，全国充电桩数量突破500万个。</p>
      
      <h3>技术突破带来新机遇</h3>
      <p>固态电池技术取得重大突破，能量密度提升50%，充电时间缩短至15分钟。自动驾驶技术与新能源车的结合，让出行更加智能环保。氢燃料电池技术在商用车领域也取得重要进展。</p>
      
      <h3>减排效果显著</h3>
      <p>据测算，1000万辆新能源汽车每年可减少碳排放约3000万吨，相当于种植了1.5亿棵树木。随着电力结构的持续优化，新能源汽车的全生命周期碳排放将进一步降低。</p>
    `,
    date: '2026-03-04',
    category: '交通减排',
    image: 'https://images.unsplash.com/photo-1593941707882-a5bba14938c7?w=1200&q=80',
    tags: ['新能源汽车', '电动汽车', '交通减排', '绿色出行']
  },
  {
    id: 3,
    title: '新型碳捕捉技术取得重大突破',
    excerpt: '科学家开发出一种高效碳捕捉技术，能够从空气中直接捕捉二氧化碳，转化率达到90%以上。',
    content: `
      <p>来自麻省理工学院的科研团队宣布，他们开发出一种革命性的碳捕捉技术，能够直接从空气中高效捕捉二氧化碳，转化率高达90%以上，成本仅为现有技术的三分之一。这一突破为应对气候变化提供了新的技术路径。</p>
      
      <h3>技术原理创新</h3>
      <p>新技术采用了一种特殊的金属有机框架材料（MOF），具有超大的比表面积和选择性吸附能力。这种材料能够像海绵一样吸收空气中的二氧化碳，然后通过低温加热将其释放出来，实现材料的循环使用。</p>
      
      <h3>应用场景广泛</h3>
      <p>这项技术不仅可以用于大型工业设施的碳排放控制，还可以部署在城市、机场等人口密集区域，直接清洁空气。更重要的是，捕捉到的二氧化碳可以转化为甲醇、塑料等有用产品，实现碳资源的循环利用。</p>
      
      <h3>商业化前景</h3>
      <p>多家能源巨头已经表示将与科研团队合作，推动这项技术的商业化应用。预计首批商业化设备将在2027年投入使用，每年可捕捉二氧化碳100万吨。</p>
    `,
    date: '2026-03-03',
    category: '技术创新',
    image: 'https://images.unsplash.com/photo-1532187863486-abf9dbad1b69?w=1200&q=80',
    tags: ['碳捕捉', '技术创新', '碳中和', '环保科技']
  },
  {
    id: 4,
    title: '可持续饮食成为全球新趋势',
    excerpt: '越来越多的人选择植物性饮食，减少肉类消费，这一趋势正在显著降低全球农业碳排放。',
    content: `
      <p>可持续饮食正在成为全球新的生活方式趋势。根据最新调查，全球有超过10亿人正在尝试减少肉类消费，选择更多的植物性食物。这一转变不仅有益于个人健康，更对减少农业碳排放产生了显著影响。</p>
      
      <h3>植物性饮食兴起</h3>
      <p>从硅谷到伦敦，从纽约到上海，植物性餐厅和素食选项正在快速增加。Beyond Meat、Impossible Foods等植物肉品牌的成功，让更多人意识到不吃肉也可以很美味。传统食品巨头如雀巢、联合利华也纷纷推出植物基产品线。</p>
      
      <h3>环境效益显著</h3>
      <p>畜牧业是温室气体排放的重要来源，占全球排放量的14.5%。如果每个人每周少吃一天肉，全球每年可减少碳排放约10亿吨。同时，减少肉类消费还能节约大量水资源和土地资源。</p>
      
      <h3>健康与环保双赢</h3>
      <p>研究表明，以植物为主的饮食模式可以降低心血管疾病、糖尿病等慢性病风险。可持续饮食不仅保护了地球，也让人们更加健康长寿。</p>
    `,
    date: '2026-03-02',
    category: '饮食减排',
    image: 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=1200&q=80',
    tags: ['可持续饮食', '植物性饮食', '饮食减排', '健康生活']
  },
  {
    id: 5,
    title: '企业碳足迹报告成为投资者关注焦点',
    excerpt: '越来越多的投资者将企业碳足迹报告作为投资决策的重要依据，推动企业更加重视减排。',
    content: `
      <p>在全球可持续发展浪潮的推动下，企业碳足迹报告正成为投资者评估企业价值的重要指标。ESG（环境、社会、治理）投资规模持续扩大，2025年全球ESG投资总额突破50万亿美元，占全球资产管理总额的35%。</p>
      
      <h3>投资者态度转变</h3>
      <p>越来越多的机构投资者将碳排放数据纳入投资决策流程。贝莱德、先锋集团等资产管理巨头明确表示，将优先投资那些具有清晰碳中和路径的企业。高碳排放企业面临融资成本上升、股价承压的挑战。</p>
      
      <h3>企业积极响应</h3>
      <p>面对投资者压力，全球超过3000家企业已经承诺实现碳中和目标。苹果、微软、谷歌等科技巨头不仅实现了自身运营碳中和，还要求供应链企业同步减排。传统高碳行业如钢铁、水泥也在加速绿色转型。</p>
      
      <h3>披露标准日趋严格</h3>
      <p>国际可持续发展准则理事会（ISSB）发布了新的气候信息披露标准，要求企业详细披露碳排放数据、气候风险和转型计划。这一标准将在全球范围内推广，进一步提升碳足迹报告的规范性和可比性。</p>
    `,
    date: '2026-03-01',
    category: '企业责任',
    image: 'https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=1200&q=80',
    tags: ['企业碳足迹', 'ESG投资', '企业责任', '可持续发展']
  },
  {
    id: 6,
    title: '政府推出碳税政策，促进绿色转型',
    excerpt: '多个国家开始实施碳税政策，通过经济手段鼓励企业和个人减少碳排放，推动绿色经济转型。',
    content: `
      <p>碳税作为应对气候变化的重要经济手段，正在全球范围内得到更广泛的应用。2025年，全球实施碳税的国家和地区达到50个，覆盖全球22%的碳排放量。中国、印度、巴西等新兴经济体也开始探索适合本国国情的碳定价机制。</p>
      
      <h3>碳税机制设计</h3>
      <p>碳税通过对化石燃料的碳含量征税，将环境成本内部化，激励企业和个人减少碳排放。目前全球平均碳税价格为每吨二氧化碳50美元，预计到2030年将提高到100美元以上。部分国家还实施了碳税收入再分配机制，将税收用于支持低收入群体和绿色技术研发。</p>
      
      <h3>减排效果显著</h3>
      <p>实证研究表明，实施碳税的国家碳排放强度下降速度明显快于未实施国家。瑞典自1991年实施碳税以来，碳排放量下降了35%，而同期经济增长了60%，证明了减排与经济发展可以并行不悖。</p>
      
      <h3>国际合作加强</h3>
      <p>为防止碳泄漏（高碳产业转移到无碳税国家），各国正在加强碳税政策的国际协调。欧盟的碳边境调节机制（CBAM）已经生效，对进口的高碳产品征收碳税，推动全球碳定价体系的统一。</p>
    `,
    date: '2026-02-28',
    category: '政策法规',
    image: 'https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=1200&q=80',
    tags: ['碳税', '政策法规', '碳定价', '绿色转型']
  }
])

// 当前新闻
const currentNews = computed(() => {
  const id = parseInt(route.params.id as string)
  return allNews.value.find(news => news.id === id)
})

// 相关新闻（排除当前新闻，随机选择3篇）
const relatedNews = computed(() => {
  const currentId = parseInt(route.params.id as string)
  return allNews.value
    .filter(news => news.id !== currentId)
    .slice(0, 3)
})

// 跳转到指定新闻
const goToNews = (id: number) => {
  router.push(`/news/${id}`)
}

// 分享到微信
const shareToWechat = () => {
  ElMessage.info('请使用微信扫一扫分享')
}

// 分享到微博
const shareToWeibo = () => {
  const url = encodeURIComponent(window.location.href)
  const title = encodeURIComponent(currentNews.value?.title || '')
  window.open(`https://service.weibo.com/share/share.php?url=${url}&title=${title}`, '_blank')
}

// 复制链接
const copyLink = () => {
  navigator.clipboard.writeText(window.location.href).then(() => {
    ElMessage.success('链接已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败，请手动复制')
  })
}

// 组件挂载时加载用户信息
onMounted(() => {
  carbonStore.loadUserFromLocalStorage()
  
  // 如果没有找到新闻，返回列表页
  if (!currentNews.value) {
    ElMessage.error('新闻不存在')
    router.push('/news')
  }
})
</script>

<style scoped>
/* 导航栏样式 */
.navbar {
  background-color: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.navbar-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
  padding: 1.2rem 2rem;
}

.navbar-logo .logo-link {
  text-decoration: none;
  color: #2E7D32;
  display: inline-block;
  transition: all 0.3s ease;
}

.navbar-logo .logo-link:hover {
  color: #1B5E20;
  transform: translateY(-2px);
}

.navbar-logo h1 {
  font-size: 1.8rem;
  margin: 0;
  color: inherit;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.navbar-links {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.user-dropdown {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 20px;
  background: rgba(76, 175, 80, 0.1);
  transition: all 0.3s ease;
}

.user-info:hover {
  background: rgba(76, 175, 80, 0.2);
}

.user-name {
  font-weight: 500;
  color: #1a1a1a;
  font-size: 14px;
}

/* 返回按钮区域 */
.back-section {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  padding: 1.5rem 0;
  border-bottom: 1px solid #e0e0e0;
}

.back-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

.back-link {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #2E7D32;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;
  padding: 8px 16px;
  border-radius: 8px;
  background: rgba(46, 125, 50, 0.1);
}

.back-link:hover {
  background: rgba(46, 125, 50, 0.2);
  transform: translateX(-5px);
}

/* 文章样式 */
.news-article {
  padding: 3rem 0;
  background: #fff;
}

.article-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 2rem;
}

.article-header {
  text-align: center;
  margin-bottom: 3rem;
}

.article-meta {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.article-category {
  background: linear-gradient(135deg, #2E7D32, #4CAF50);
  color: white;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
}

.article-date {
  color: #666;
  font-size: 0.95rem;
}

.article-title {
  font-size: 2.5rem;
  color: #1B5E20;
  margin-bottom: 1.5rem;
  line-height: 1.3;
  font-weight: 700;
}

.article-excerpt {
  font-size: 1.2rem;
  color: #666;
  line-height: 1.8;
  font-style: italic;
  max-width: 600px;
  margin: 0 auto;
}

/* 文章主图 */
.article-hero-image {
  margin-bottom: 3rem;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.article-hero-image img {
  width: 100%;
  height: 400px;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.article-hero-image:hover img {
  transform: scale(1.02);
}

/* 文章内容 */
.article-content {
  margin-bottom: 3rem;
}

.content-body {
  font-size: 1.1rem;
  line-height: 1.9;
  color: #333;
}

.content-body :deep(p) {
  margin-bottom: 1.5rem;
}

.content-body :deep(h3) {
  font-size: 1.5rem;
  color: #1B5E20;
  margin: 2.5rem 0 1rem;
  font-weight: 600;
}

/* 文章标签 */
.article-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 3rem;
  padding-bottom: 3rem;
  border-bottom: 1px solid #e0e0e0;
}

.tag {
  background: rgba(46, 125, 50, 0.1);
  color: #2E7D32;
  padding: 6px 14px;
  border-radius: 16px;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.3s ease;
}

.tag:hover {
  background: rgba(46, 125, 50, 0.2);
  transform: translateY(-2px);
}

/* 分享按钮 */
.article-share {
  text-align: center;
  margin-bottom: 3rem;
}

.article-share h4 {
  font-size: 1.2rem;
  color: #333;
  margin-bottom: 1.5rem;
}

.share-buttons {
  display: flex;
  justify-content: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.share-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.share-btn.wechat {
  background: #07C160;
  color: white;
}

.share-btn.wechat:hover {
  background: #06ad56;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(7, 193, 96, 0.3);
}

.share-btn.weibo {
  background: #E6162D;
  color: white;
}

.share-btn.weibo:hover {
  background: #d91429;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(230, 22, 45, 0.3);
}

.share-btn.link {
  background: #2E7D32;
  color: white;
}

.share-btn.link:hover {
  background: #1B5E20;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(46, 125, 50, 0.3);
}

/* 相关新闻 */
.related-news-section {
  padding: 4rem 0;
  background: #f8f9fa;
}

.related-news-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

.related-news-container h3 {
  font-size: 2rem;
  color: #1B5E20;
  margin-bottom: 2rem;
  text-align: center;
  font-weight: 700;
}

.related-news-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.related-news-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
}

.related-news-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
}

.related-news-image {
  height: 180px;
  overflow: hidden;
}

.related-news-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.related-news-card:hover .related-news-image img {
  transform: scale(1.1);
}

.related-news-content {
  padding: 1.5rem;
}

.related-news-category {
  background: rgba(46, 125, 50, 0.1);
  color: #2E7D32;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 500;
  display: inline-block;
  margin-bottom: 0.8rem;
}

.related-news-title {
  font-size: 1.1rem;
  color: #333;
  line-height: 1.5;
  font-weight: 600;
}

/* 页脚样式 */
.footer {
  background-color: #1B5E20;
  color: white;
  padding: 4rem 0;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 3rem;
}

.footer-info h4 {
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
  font-weight: 700;
}

.footer-info p {
  line-height: 1.7;
  opacity: 0.9;
}

.footer-links {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.footer-links a {
  color: white;
  text-decoration: none;
  transition: all 0.3s ease;
  opacity: 0.9;
}

.footer-links a:hover {
  opacity: 1;
  transform: translateX(5px);
  color: #4CAF50;
}

.footer-contact p {
  margin-bottom: 0.5rem;
  opacity: 0.9;
}

/* 按钮样式 */
.btn {
  display: inline-block;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.3s ease;
  border: none;
  cursor: pointer;
  font-size: 1rem;
}

.btn-primary {
  background: linear-gradient(135deg, #2E7D32, #4CAF50);
  color: white;
  box-shadow: 0 4px 15px rgba(46, 125, 50, 0.3);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(46, 125, 50, 0.4);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .article-title {
    font-size: 1.8rem;
  }
  
  .article-hero-image img {
    height: 250px;
  }
  
  .article-meta {
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .share-buttons {
    flex-direction: column;
    align-items: center;
  }
  
  .share-btn {
    width: 200px;
    justify-content: center;
  }
  
  .related-news-grid {
    grid-template-columns: 1fr;
  }
  
  .footer-container {
    grid-template-columns: 1fr;
    text-align: center;
  }
}
</style>