import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import LoginApp from './LoginApp.vue'

const app = createApp(LoginApp)
app.use(ElementPlus, { locale: zhCn })
app.mount('#app')
