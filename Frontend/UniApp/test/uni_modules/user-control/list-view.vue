<template>
	<view>
		<view class="title"></view>
		<view class="search"></view>
	</view>
</template>

<script>
	export default {
		props: {
			showSearch: {
				type: Boolean,
				default: false
			},
			showLoadMore: {
				type: Boolean,
				default: false
			},
			dataList: {
				type: Array,
				default: []
			}
		},
		data() {
			return {
				loading: false
			}
		}
	}
</script>

<style>
</style>