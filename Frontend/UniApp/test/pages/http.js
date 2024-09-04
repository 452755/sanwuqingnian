/**
 * 
 * @param {post请求方式} options 
 */
export const PostRequest = (options) => {
	// uni.showModal({
	// 	title: '...',
	// 	icon: 'success'
	// })

	return new Promise((resolve, reject) => {
		uni.request({
			url: 'http://192.168.31.45:19808/TzxRestFulServer/' + options.url,
			method: 'POST',
			data: options.data || {},
			header: {
				"content-type": "application/json;charset=UTF-8",
			},
			success: (res) => {
				resolve(res)
			},
			fail: (err) => {
				uni.showModal({
					title: JSON.stringify(err),
					icon: 'error'
				})
				// alert(JSON.stringify(err))
				console.log(err)
				reject(err)
			}
		})
	})
}


/**
 * 
 * @param {get请求方式} options 
 */
export const GetRequest = (options) => {

	return new Promise((resolve, reject) => {
		uni.request({
			url: 'http://192.168.31.45:19808/TzxRestFulServer/' + options.url,
			method: 'GET',
			data: options.data || {},
			header: {
				"content-type": "application/json;charset=UTF-8"
			},
			success: (res) => {
				resolve(res)
			},
			fail: (err) => {
				console.log(err)
				reject(err)
			}
		})
	})
}

export const DownLoadFile = (options) => {
	return new Promise((resolve, reject) => {
		const requestTask = uni.request({
			url: 'http://192.168.31.45:19808/TzxRestFulServer/' + options.url,
			method: options.method,
			data: options.data || {},
			header: {
				"content-type": "application/json;charset=UTF-8"
			},
			enableChunked: true,
			responseType: 'arraybuffer', 
			success: (res) => {
				resolve(res)
			},
			fail: (err) => {
				console.log(err)
				reject(err)
			}
		})
		
		console.log(requestTask)
		
		// // 处理响应头
		// requestTask.onHeadersReceived(function(res) {
		//     if (options.onHeadersReceived) {
		//         options.onHeadersReceived(res);  // 触发自定义的响应头处理方法
		//     }
		// });

		// // 处理数据块
		// requestTask.onChunkReceived(function(res) {
		//     if (options.onChunkReceived) {
		//         options.onChunkReceived(res);  // 触发自定义的块处理方法
		//     }
		// });
	})
}