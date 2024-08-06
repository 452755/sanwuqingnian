<template>
	<view class="content">
		<button @click="connect" type="primary">connect</button>
		<view class="log">
			<view v-for="(log,index) in logs" class="" :key="index">
				{{log}}
			</view>
		</view>
	</view>
</template>

<script>
	import mqtt from 'mqtt/dist/mqtt.js'
	export default {
		data() {
			return {
				logs: []
			}
		},
		methods: {
			async connect() {
				var self = this
				self.logs.push('connect')
				// #ifdef H5
				var client = mqtt.connect('ws://test.mosquitto.org:8080')
				// #endif
				// #ifdef MP-WEIXIN||APP-PLUS
				var client = mqtt.connect('wx://test.mosquitto.org:8080')
				// #endif
				client.on('connect', function() {
					self.logs.push('on connect')
					client.subscribe('presence', function(err) {
						if (!err) {
							client.publish('presence', 'hello mqtt')
						}
					})
				}).on('reconnect', function() {
					self.logs.push('on reconnect')
				}).on('error', function() {
					self.logs.push('on error')
				}).on('end', function() {
					self.logs.push('on end')
				}).on('message', function(topic, message) {
					console.log(message)
					self.logs.push(message.toString())
				})
			}
		}
	}
</script>

<style>
	.content {
		text-align: center;
		word-break: break-all;
	}
</style>