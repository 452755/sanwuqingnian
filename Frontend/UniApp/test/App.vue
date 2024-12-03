<script>
	export default {
		onLaunch: function(option) {
			console.log('App Launch')
			try {
				// 挂载APP启动日志提交
				// uni.$dev.logReport("appOnLaunch>", option);
			} catch (error) {}
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		},
		onError(err) {
		    try {
				// 挂载devTools全局报错拦截
				uni.$dev.errorReport(err, "at App.vue onError", "oe");
		    } catch (error) {}
		}
	}
</script>

<style>
	/*每个页面公共css */
</style>
