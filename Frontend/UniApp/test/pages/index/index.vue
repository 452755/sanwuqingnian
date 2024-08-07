<template>
	<view class="content">
		<view class="container" @touchstart="handleTouchStart" @touchend="handleTouchEnd">
		  <scroll-view
		    ref="scrollView"
		    :scroll-y="true"
		    @scrolltolower="handleScrollToLower"
		    style="height: 100vh"
		  >
		    <view class="item" v-for="(item, index) in items" :key="index">
		      {{ item }}
		    </view>
		  </scroll-view>
		</view>
	</view>
</template>

<script>
	import {PostRequest,DownLoadFile} from '../http.js'
	import {zipHandler} from '../file.js'

	export default {
	  data() {
	    return {
	      items: [],
	      isTouching: false,
	    };
	  },
	  onShow() {
		DownLoadFile({
		  url: 'app/base/getAllDishesImageFiles',
		  method: 'POST',
		  data: {}
		}).then((res) => {
		  zipHandler(res.data)
		}).catch((err) => {
			console.error('Error loading zip file:', err);
		});
	  },
	  methods: {
	    handleScrollToLower() {
	      // 仅当触摸结束时才执行加载更多数据的逻辑
	      if (!this.isTouching) {
	        this.loadMoreData();
	      }
	    },
	    loadMoreData() {
	      // 模拟加载更多数据
	      setTimeout(() => {
	        const moreItems = Array.from({ length: 20 }, (_, index) => `Item ${this.items.length + index + 1}`);
	        this.items = [...this.items, ...moreItems];
	      }, 1000);
	    },
	    handleTouchStart() {
	      this.isTouching = true;
	    },
	    handleTouchEnd() {
	      this.isTouching = false;
	      // 在触摸结束时检测是否已经到达底部，如果是则加载更多数据
	      this.$nextTick(() => {
	        const scrollView = this.$refs.scrollView;
	        const { scrollTop, scrollHeight, clientHeight } = scrollView;
			console.log(scrollView, scrollTop, scrollHeight, clientHeight)
	        if (scrollHeight - scrollTop === clientHeight) {
	          this.loadMoreData();
	        }
	      });
	    },
	  },
	  mounted() {
	    // 初始化数据
	    this.items = Array.from({ length: 20 }, (_, index) => `Item ${index + 1}`);
	  },
	};
</script>

<style>
	.content {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}

	.container {
	  height: 100vh;
	  overflow: hidden;
	}
	.item {
	  height: 50px;
	  line-height: 50px;
	  text-align: center;
	  border-bottom: 1px solid #ccc;
	}
</style>
