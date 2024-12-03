<template>
  <view class="container">
    <!-- 左侧分类列表 -->
    <!-- <scroll-view class="category-list" scroll-y>
      <view v-for="(category, index) in categories" :key="category.id" @click="scrollToCategory(index)">
        {{ category.name }}
      </view>
    </scroll-view> -->

	<view class="category-list">
		<u-list 
			:scrollIntoView="selectedCategory">
			<u-list-item 
				v-for="(category, index) in categories" 
				:key="category.id" 
				:id="category.name">
				{{ category.name }}
			</u-list-item>
		</u-list>
	</view>

    <!-- 右侧商品列表 -->
    <scroll-view 
      class="product-list" 
      scroll-y 
      @scroll="onScroll" 
      :scroll-top="scrollTop">
      <!-- 占位前面的商品高度 -->
      <!-- <view :style="{ height: topBlankHeight + 'px' }"></view> -->

      <!-- 渲染可见商品 -->
      <!-- <view v-for="(item, index) in visibleProducts" :key="item.id">
        <view class="category-name">{{ item.category }}</view>
        <view class="product" v-for="product in item.products" :key="product.id">
          <image :src="product.image" mode="aspectFill" />
          <text>{{ product.name }}</text>
        </view>
      </view> -->

      <!-- 占位后面的商品高度 -->
      <!-- <view :style="{ height: bottomBlankHeight + 'px' }"></view> -->
	  
	  <button @click="buttonClick">点击+1</button>
    </scroll-view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      categories: [], // 商品分类列表
      products: [], // 所有商品数据
      // visibleProducts: [], // 当前可见的商品数据
      itemHeight: 100, // 每个商品分类的高度
      viewportHeight: 600, // 可视窗口的高度
      scrollTop: 0, // 当前滚动位置
	  selectedIndex: 1,
		selectedCategory: 'cplx1'
    };
  },
  computed: {
    // 可视范围上方的占位高度
    topBlankHeight() {
      return this.startIndex * this.itemHeight;
    },
    // 可视范围下方的占位高度
    bottomBlankHeight() {
      return (this.products.length - this.endIndex) * this.itemHeight;
    },
    // 计算当前可见的商品数据
    visibleProducts() {
      return this.products.slice(this.startIndex, this.endIndex);
    },
    // 计算起始索引
    startIndex() {
      return Math.floor(this.scrollTop / this.itemHeight);
    },
    // 计算结束索引（展示的商品数量与视窗大小有关）
    endIndex() {
      return Math.ceil((this.scrollTop + this.viewportHeight) / this.itemHeight);
    },
  },
  methods: {
    onScroll(e) {
      this.scrollTop = e.detail.scrollTop; // 更新滚动位置
    },
    scrollToCategory(index) {
      // 点击左侧分类，跳转到对应商品区域
      this.scrollTop = index * this.itemHeight;
    },
	handleChange(index) {
		this.selectedIndex = index; // 更新选中项
	},
	buttonClick() {
		this.selectedIndex = this.selectedIndex + 1
		if (this.selectedIndex >= this.categories.length) {
			this.selectedIndex = this.categories.length
		}
		
		this.selectedCategory = `cplx${this.selectedIndex}`
	},
  },
  mounted() {
    // 模拟商品数据，初始化时加载
    this.categories = [];
	
	for (let i = 1; i <= 100; i++) {
		this.categories.push({
			id: i,
			name: `cplx${i}`
		})
	}
	
    this.products = [
      { id: 1, category: 'cplx1', products: [{ id: 101, name: '商品A', image: '/images/product-a.jpg' }] },
      { id: 2, category: 'cplx2', products: [{ id: 102, name: '商品B', image: '/images/product-b.jpg' }] },
      { id: 3, category: 'cplx3', products: [{ id: 103, name: '商品C', image: '/images/product-c.jpg' }] },
    ];
  },
};
</script>

<style>
.container {
  display: flex;
  height: 100%;
}
.category-list {
  width: 20%;
  background-color: #f5f5f5;
}
.product-list {
  width: 80%;
  background-color: #fff;
}
.category-name {
  font-size: 16px;
  font-weight: bold;
  margin: 10px 0;
}
.product {
  display: flex;
  align-items: center;
  padding: 10px;
}
.product image {
  width: 50px;
  height: 50px;
  margin-right: 10px;
}
</style>