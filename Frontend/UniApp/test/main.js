import App from './App'

import uView from '@/uni_modules/uview-ui'

// #ifndef VUE3
import Vue from 'vue'
import './uni.promisify.adaptor'

//挂载Devtools
import devTools from "./devTools/index.js";
import devToolsConfig from './devTools/config.js';

// //注册小程序端专用的拖动浮标组件
// import mpDevBubble from './devTools/core/components/mpDevBubble.vue'
// Vue.component("mpDevBubble", mpDevBubble)

Vue.config.productionTip = false

console.log(Vue.config.globalProperties)

Vue.use(uView)
Vue.use(devTools, devToolsConfig)

App.mpType = 'app'
const app = new Vue({
  ...App
})
app.$mount()
// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue'

import devTools from "./devTools/index.js";
import devToolsConfig from './devTools/config.js';
import devToolsVueMixin from "./devTools/core/proxy/vueMixin.js"

export function createApp() {
	const app = createSSRApp(App)
  
	//混入DevTools生命周期监听
    app.mixin(devToolsVueMixin)
  
    //挂载Devtools
    app.use(devTools, devToolsConfig)
  
	return {
		app
	}
}
// #endif