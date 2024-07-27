import Vue from 'vue'
import App from './App.vue'
import ElementUi from 'element-ui'
import XLSX from 'xlsx'
 
import 'element-ui/lib/theme-chalk/index.css'
 
Vue.prototype.XLSX=XLSX
Vue.use(ElementUi)
Vue.config.productionTip = false

new Vue({
  render: h => h(App),
}).$mount('#app')
